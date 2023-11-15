package BLL;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Observable;
import java.util.Observer;
import java.net.URL;

public class FileDownloader extends Observable implements Observer {
    public String fileName;
    private URL url;
    private long fileSize;
    private long downloadedBytes;
    public String savePath;
    private DownloadStatus status;

    public FileDownloader(URL url, File savePath) {
        this.url = url;
        this.downloadedBytes = 0;
        this.fileSize = 1;
        this.fileName = url.getPath().substring(url.getPath().lastIndexOf('/') + 1);
        this.savePath = savePath + "\\"+ fileName;
        status = DownloadStatus.WAITING;
        System.out.println("Savepath: " + this.savePath);
    }

    @Override
    synchronized public void update(Observable o, Object arg) {
        FragmentDownloader fragmentDownloader = (FragmentDownloader) o;
        if(fragmentDownloader.getStatus() == DownloadStatus.DOWNLOADING) {
            downloadedBytes += (int) arg;
            setChanged();
            notifyObservers();
            System.out.print("Downloaded process: " + downloadedBytes * 100 / fileSize + " %\r");
        }
    }

    public URL getURL() {
        return url;
    }

    public void setURL(URL url) {
        this.url = url;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public DownloadStatus getStatus() {
        return status;
    }

    public void setStatus(DownloadStatus status) {
        this.status = status;
        setChanged();
        notifyObservers();
    }
    public int getProgress() {
        return (int)(downloadedBytes * 100 / fileSize);
    }
    public String getFileName() {
        return this.fileName;
    }
}
