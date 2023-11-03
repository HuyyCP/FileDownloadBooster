import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.concurrent.Callable;

public class FragmentDownloader implements Callable<Long> {
    int threadNumber;
    URL url;
    String savePath;
    long startByte;
    long endByte;
    public FragmentDownloader(int threadNumber, URL url, String savePath, long startByte, long endByte) {
        this.threadNumber = threadNumber;
        this.url = url;
        this.savePath = savePath;
        this.startByte = startByte;
        this.endByte = endByte;
    }
    @Override
    public Long call() {
        String protocol = url.getProtocol(); // Get prototcol
        String host = url.getHost(); // Get host
        String path = url.getPath(); // Get path

        // Create socket connection
        try (Socket socket = protocol.equals("https") ? SSLSocketFactory.getDefault().createSocket(host, 443) : new Socket(host, 80);
             BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());
             BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream());
             RandomAccessFile file = new RandomAccessFile(savePath, "rw"))
        {
            // Create request
            String getRequest = "GET " + path + " HTTP/1.1\r\n" +
                    "Host: " + host + "\r\n" +
                    "Range: bytes=" + startByte + "-" + endByte + "\r\n" +
                    "Connection: close\r\n\r\n";
            System.out.println("Thread " + threadNumber + " started with offset " + startByte + " length " + (endByte - startByte + 1) );

            // Send request
            IOStreamHelper.sendRequest(outputStream, getRequest);

            // Receive data
            long totalBytesRead = IOStreamHelper.receiveBody(inputStream, file, startByte, endByte - startByte + 1);

            // Close connection
            inputStream.close();
            outputStream.close();
            file.close();
            System.out.println("\u001B[32m" + "Thread " + threadNumber + " finished: " + totalBytesRead + " bytes" + "\u001B[0m");
            return totalBytesRead;
        } catch (IOException exception) {
            System.out.println("Thread " + threadNumber + " stopped");
        }
        return 0L;
    }
}
