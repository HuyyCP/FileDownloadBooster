package BLL;

import Utils.Data.DownloadStatus;
import Utils.Data.FragmentWatcher;
import Utils.Data.IOStreamHelper;
import static Utils.Data.Constants.NUMTHREADS;

import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.*;


public class FileDownloadManager {
    private final int corePoolSize = NUMTHREADS; // minimum number of threads run simultaneously
    private final int maximumPoolSize = NUMTHREADS * 3; // maximum number of threads run simultaneously
    private final long keepAliveTime = 10L; // time to live while not running (threads = maximumPoolSize - coorePoolSize)
    private final TimeUnit unit = TimeUnit.SECONDS;
    private final int queueSize = NUMTHREADS * 3;
    private final ArrayBlockingQueue<Runnable> workQueue; // queue of runnable tasks
    private final RejectedExecutionHandler handler; // handle rejected execution
    private ThreadPoolExecutor executor; // multithread manager

    public FileDownloadManager() {
        this.workQueue = new ArrayBlockingQueue<>(queueSize);
        this.handler = new ThreadPoolExecutor.CallerRunsPolicy();
        this.executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }
    public void handleRedirectURL(FileDownloader fileDownloader) {
        System.out.println("Handling redirect URL...");
        URL url = fileDownloader.getURL();
        fileDownloader.setStatus(DownloadStatus.REDIRECTING);
        String responseCode;
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
                if (responseCode.equals("200")) {
                    break;
                } else if (responseCode.startsWith("3")) {
                    String location = headers.get("location");
                    url = new URI(location).toURL();
                }
            } catch (IOException exception) {
                fileDownloader.setStatus(DownloadStatus.ERROR);
                System.out.println(exception.getMessage());
                System.out.println("Handle redirect failed");
            } catch (URISyntaxException e) {

            }
        } while (true);
        fileDownloader.setURL(url);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        fileDownloader.setStatus(DownloadStatus.DOWNLOADING);
    }

    public void HandleFragmentation(FileDownloader fileDownloader) {
        URL url = fileDownloader.getURL();
        System.out.println("\nFinal URL: " + url);

        // Handle string URL
        String protocol = url.getProtocol();
        String host = url.getHost();
        String path = url.getPath();

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
            System.out.println("\nFragmenting...");
            long fileSize = Long.parseLong(headers.get("content-length"));
            long partSize = fileSize / NUMTHREADS;
            fileDownloader.setFileSize(fileSize);

            Vector<FragmentWatcher> watchers = fileDownloader.getFragmentWatchers();
            // Fragmenting file
            for (int i = 0; i < NUMTHREADS; i++) {
                // Range to download
                long startByte = i * partSize;
                long endByte = (i == NUMTHREADS - 1) ? fileSize - 1 : startByte + partSize - 1;
                watchers.elementAt(i).setDownloadRange(startByte, endByte);
            }

        } catch (IOException exception) {
            System.out.println("Connection closed");
        }
    }

    public void downloadFile(FileDownloader fileDownloader) {
        try {
            // Multi fragment download
            Vector<FragmentWatcher> watchers = fileDownloader.getFragmentWatchers();
            List<Future<Long>> futures = new ArrayList<>();
            for (int i = 0; i < NUMTHREADS; i++) {
                FragmentDownloader fragmentDownloader = new FragmentDownloader(fileDownloader.getID(),
                        i + 1,
                        fileDownloader.getURL(),
                        fileDownloader.getSavePath(),
                        watchers.elementAt(i).getOffset(),
                        watchers.elementAt(i).getFragmentSize(),
                        watchers.elementAt(i).getDownloaded());
                fragmentDownloader.addObserver(fileDownloader);
                watchers.elementAt(i).addObserver(fragmentDownloader);
                Future<Long> future = executor.submit(fragmentDownloader);
                futures.add(future);
            }

            // Wait for all threads to finish
            long totalBytesRead = 0;
            for (Future<Long> future : futures) {
                try {
                    totalBytesRead += future.get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Total bytes read: " + totalBytesRead);
            if (totalBytesRead == fileDownloader.getFileSize()) {
                System.out.println("File has been successfully downloaded.\n\n");
                fileDownloader.setStatus(DownloadStatus.COMPLETED);
            }
            fileDownloader.removeFragmentView();
        } catch (Exception e) {
            System.out.println("Exception in downloading");
        }
    }
}
