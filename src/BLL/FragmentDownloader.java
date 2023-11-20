package BLL;

import Utils.Data.DownloadStatus;
import Utils.Data.IOStreamHelper;

import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.Observable;
import java.util.concurrent.Callable;

public class FragmentDownloader extends Observable implements Callable<Long> {
    private final static int bufferSize = 4096; // multiple of 4KB
    private final int fileDownloaderID;
    private final int threadID;
    private final URL url;
    private final String savePath;
    private final long offset;
    private final long length;
    private long downloaded;
    private DownloadStatus downloadStatus;

    public FragmentDownloader(int fileDownloaderID, int threadID, URL url, String savePath, long offset, long length) {
        this.fileDownloaderID = fileDownloaderID;
        this.threadID = threadID;
        this.url = url;
        this.savePath = savePath;
        this.offset = offset;
        this.length = length;
        this.downloaded = 0;
        this.downloadStatus = DownloadStatus.DOWNLOADING;
    }

    public int getFileDownloaderID() {
        return this.fileDownloaderID;
    }

    public int getThreadID() {
        return threadID;
    }

    public DownloadStatus getStatus() {
        return downloadStatus;
    }

    public long getStartByte() {
        return this.offset;
    }

    public long getFragmentSize() {
        return this.length;
    }

    public long getDownloaded() {
        return this.downloaded;
    }

    public int getProgress() {
        return this.length == 0 ? 0 : (int)(this.downloaded * 100 / this.length);
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
                    "Range: bytes=" + offset + "-" + (offset + length - 1) + "\r\n" +
                    "Connection: close\r\n\r\n";
            System.out.println("Thread " + threadID + " started with offset " + offset + " length " + length);

            // Send request
            IOStreamHelper.sendRequest(outputStream, getRequest);

            // Receive data
            IOStreamHelper.receiveHeader(inputStream); // skip header


            // Download fragment
            byte[] buffer = new byte[bufferSize];
            int bytesRead;
            file.seek(offset + downloaded);

            while(downloaded < length && downloadStatus == DownloadStatus.DOWNLOADING) {
                // Read data from inputStream
                bytesRead = inputStream.read(buffer);
                file.write(buffer, 0, bytesRead);
                downloaded += bytesRead;

                // Update bytesRead to FileDownloader
                setChanged();
                notifyObservers(bytesRead);
            }

            // Close connection
            inputStream.close();
            outputStream.close();
            file.close();
            downloadStatus = DownloadStatus.COMPLETED;
            setChanged();
            notifyObservers();
            System.out.println("\u001B[32m" + "Thread " + threadID + " finished: " + downloaded + " bytes" + "\u001B[0m");
            return downloaded;
        } catch (IOException exception) {
            System.out.println("Thread " + threadID + " stopped");
        }
        return 0L;
    }

}
