import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class FragmentDownloadManager {
    URL url; // Unhandled URL
    int numThreads = 10;

    public FragmentDownloadManager(String urlStr) throws MalformedURLException {
        this.url = new URL(urlStr);
    }
    public void HandleRedirectURL() {
        System.out.println("Handling redirect URL...");
        String responseCode = ""; // Response code
        do {
            try {
                // Create socket and stream
                Socket socket;
                if(url.getProtocol().equals("https")) {
                    socket = SSLSocketFactory.getDefault().createSocket(url.getHost(), 443);
                } else {
                    socket = new Socket(url.getHost(), 80);
                }
                BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream());
                BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());

                // Create a http HEAD request
                String request = "HEAD " + url.getPath() + " HTTP/1.1\r\n" +
                                 "Host: " + url.getHost() + "\r\n\r\n";

                // Send request to server
                IOStreamHelper.sendRequest(outputStream, request);

                // Read response from HEAD request
                Map<String, String> headers = IOStreamHelper.receiveHeader(inputStream);

                // Close connection
                inputStream.close();
                outputStream.close();
                socket.close();

                // Handle redirect
                String statusLine = headers.get("Status");
                responseCode = statusLine.substring(9, statusLine.indexOf(" ", 9));
                if(responseCode.equals("200")) { break;}
                else if(responseCode.startsWith("3")) {
                    String location = headers.get("Location").substring(10);
                    url = new URL(location);
                }
            } catch (IOException exception) {
                System.out.println("Handle redirect failed");
            }
        } while(true);
    }

    public void downloadFile(String savePath) throws IOException {
        // Handle redirect URL
        HandleRedirectURL();

        // Handle string URL
        String protocol = url.getProtocol(); // Get prototcol
        String host = url.getHost(); // Get host
        String path = url.getPath(); // Get path
        String filename = path.substring(path.lastIndexOf('/') + 1); // Get file name
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
            System.out.println("Status: " + headers.get("Status") + "\nContent-length: " + headers.get("Content-Length") + "\r\n");

            System.out.println("Downloading file...");
            CountDownLatch latch = new CountDownLatch(numThreads);
            long fileSize = Long.parseLong(headers.get("Content-Length"));
            long partSize = fileSize / numThreads;
            for (int i = 0; i < numThreads; i++) {
                // Range to download
                long startByte = i * partSize;
                long endByte = (i == numThreads - 1) ? fileSize - 1 : startByte + partSize - 1;

                // Download by range
                try {
                    FragmentDownloader fragmentDownloader = new FragmentDownloader(i + 1, url, savePath, startByte, endByte, latch);
                    fragmentDownloader.startDownload();
                } catch (InterruptedException e) {
                    System.out.println("Thread " + (i + 1) + " is interrupted");
                }
            }

            try {
                // Wait for all threads to finish
                latch.await();
                System.out.println("File has been successfully downloaded.");
            } catch (InterruptedException e) {
                System.out.println("Threads interrupted");
            }
        } catch (IOException exception) {
            System.out.println("Connection closed");
        }
    }
}
