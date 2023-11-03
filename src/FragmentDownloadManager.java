import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class FragmentDownloadManager {
    private final int numThreads = 10;
    private final int corePoolSize = numThreads; // minimum number of threads run simultaneously
    private final int maximumPoolSize = numThreads * 3; // maximum number of threads run simultaneously
    private final long keepAliveTime = 10L; // time to live while not running (threads = maximumPoolSize - coorePoolSize)
    private final TimeUnit unit = TimeUnit.SECONDS;
    private final int queueSize = numThreads * 3;
    private final ArrayBlockingQueue<Runnable> workQueue; // queue of runnable tasks
    private final RejectedExecutionHandler handler; // handle rejected execution
    private ThreadPoolExecutor executor; // multithread manager

    public FragmentDownloadManager() throws MalformedURLException, URISyntaxException {
        this.workQueue = new ArrayBlockingQueue<>(queueSize);
        this.handler = new ThreadPoolExecutor.CallerRunsPolicy();
        this.executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }
    public URL HandleRedirectURL(String strURL) throws URISyntaxException, MalformedURLException {
        URL url = new URI(strURL).toURL();
        System.out.println("Handling redirect URL...");
        String responseCode = "";
        do {
            try (Socket socket = url.getProtocol().equals("https") ? SSLSocketFactory.getDefault().createSocket(url.getHost(), 443) : new Socket(url.getHost(), 80);
                 BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());
                 BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream())) {

                // Create a http HEAD request
                String request = "HEAD " + url.getPath() + " HTTP/1.1\r\n" +
                        "Host: " + url.getHost() + "\r\n\r\n";

                // Send request to server
                IOStreamHelper.sendRequest(outputStream, request);

                // Read response from HEAD request
                Map<String, String> headers = IOStreamHelper.receiveHeader(inputStream);
                System.out.println(headers);

                // Close connection
                inputStream.close();
                outputStream.close();
                socket.close();

                // Handle redirect
                String statusLine = headers.get("status");
                responseCode = statusLine.substring(statusLine.indexOf(" ") + 1, statusLine.indexOf(" ") + 4);
                if(responseCode.equals("200")) {
                    break;
                } else if(responseCode.startsWith("3")) {
                    String location = headers.get("location");
                    url = new URI(location).toURL();
                }
            } catch (IOException exception) {
                System.out.println(exception.getMessage());
                System.out.println("Handle redirect failed");
            }
        } while(true);
        return url;
    }

    public void downloadFile(String strURL, String savePath) throws IOException, URISyntaxException {
        // Handle redirect URL
        URL url =  HandleRedirectURL(strURL);

        System.out.println("\nFinal URL: " + url);

        // Handle string URL
        String protocol = url.getProtocol();
        String host = url.getHost();
        String path = url.getPath();
        String filename = path.substring(path.lastIndexOf('/') + 1);
        savePath += filename; // Add file name to directory

        // Create a socket connection
        try (Socket socket = protocol.equals("https") ? SSLSocketFactory.getDefault().createSocket(host, 443) : new Socket(host, 80);
             BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());
             BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream()))
        {
            // Create a http HEAD request
            String headRequest = "HEAD " + path + " HTTP/1.1\r\n" +
                    "Host: " + host + "\r\n\r\n";

            // Send request to server
            IOStreamHelper.sendRequest(outputStream, headRequest);

            // Read response from HEAD request
            Map<String, String> headers = IOStreamHelper.receiveHeader(inputStream);

            // Close the connection
            inputStream.close();
            outputStream.close();
            socket.close();

            // Calculate download range
            System.out.println("\nDownloading file...");
            long fileSize = Long.parseLong(headers.get("content-length"));
            long partSize = fileSize / numThreads;

            // Multipart download
            List<Future<Long>> futures = new ArrayList<>();
            for (int i = 0; i < numThreads; i++) {
                // Range to download
                long startByte = i * partSize;
                long endByte = (i == numThreads - 1) ? fileSize - 1 : startByte + partSize - 1;

                // Download by range
                FragmentDownloader fragmentDownloader = new FragmentDownloader(i + 1, url, savePath, startByte, endByte);
                Future<Long> future = executor.submit(fragmentDownloader);
                futures.add(future);
            }

            // Wait for all threads to finish
            long totalBytesRead = 0;
            for(Future<Long> future : futures) {
                try {
                    totalBytesRead += future.get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Total bytes read: " + totalBytesRead);
            System.out.println("File has been successfully downloaded.\n\n");
//            executor.shutdown();
        } catch (IOException exception) {
            System.out.println("Connection closed");
        }
    }
}
