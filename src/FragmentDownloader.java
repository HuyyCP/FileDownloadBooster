import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

public class FragmentDownloader implements Runnable {
    int threadNumber;
    URL url;
    String savePath;
    long startByte;
    long endByte;
    Thread thread;
    CountDownLatch latch;
    public FragmentDownloader(int threadNumber, URL url, String savePath, long startByte, long endByte, CountDownLatch latch) {
        this.threadNumber = threadNumber;
        this.url = url;
        this.savePath = savePath;
        this.startByte = startByte;
        this.endByte = endByte;
        this.latch = latch;
    }
    @Override
    public void run() {
        String protocol = url.getProtocol(); // Get prototcol
        String host = url.getHost(); // Get host
        String path = url.getPath(); // Get path

        // Create socket connection
        try (Socket socket = protocol.equals("https") ? SSLSocketFactory.getDefault().createSocket(host, 443) : new Socket(host, 80);
             BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());
             BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream());
             RandomAccessFile file = new RandomAccessFile(savePath, "rwd")) {

            // Create request
            String getRequest = "GET " + path + " HTTP/1.1\r\n" +
                    "Host: " + host + "\r\n" +
                    "Range: bytes=" + startByte + "-" + endByte + "\r\n" +
                    "Connection: close\r\n\r\n";
            System.out.println("Thread " + threadNumber + " started");

            // Send request
            IOStreamHelper.sendRequest(outputStream, getRequest);

            // Receive data
            long totalBytesRead = IOStreamHelper.receiveBody(inputStream, file, startByte);

            // Close connection
            inputStream.close();
            outputStream.close();
            file.close();
            System.out.println("Thread " + threadNumber + " done: " + totalBytesRead + " bytes");
            latch.countDown();
        } catch (IOException exception) {
            System.out.println("Thread " + threadNumber + " stopped");
        }
    }
    public void startDownload() throws InterruptedException {
        if(this.thread == null) {
            this.thread = new Thread(this);
            this.thread.start();
        }
    }
}
