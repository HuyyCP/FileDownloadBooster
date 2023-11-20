package Utils.Data;

import BLL.FragmentDownloader;

import java.util.Observable;
import java.util.Observer;

public class FragmentWatcher {
    private int fileDownloaderID;
    private int threadID;
    private long startByte;
    private long fragmentSize;
    private long downloadedBytes;
    private DownloadStatus status;
    public FragmentWatcher(int fileDownloaderID) {
        this.fileDownloaderID = fileDownloaderID;
        this.threadID = 0;
        this.startByte = 0;
        this.fragmentSize = 0;
        this.downloadedBytes = 0;
        this.status = DownloadStatus.WAITING;
    }

    public void update(FragmentDownloader fragmentDownloader) {
        this.downloadedBytes = fragmentDownloader.getDownloaded();
        this.status = fragmentDownloader.getStatus();
    }
    public int getFileDownloaderID() {
        return this.fileDownloaderID;
    }

    public int getThreadID() {
        return threadID;
    }

    public DownloadStatus getStatus() {
        return status;
    }

    public long getStartByte() {
        return this.startByte;
    }

    public long getFragmentSize() {
        return this.fragmentSize;
    }

    public long getDownloaded() {
        return this.downloadedBytes;
    }

    public int getProgress() {
        return this.fragmentSize == 0 ? 0 : (int)(this.downloadedBytes * 100 / this.fragmentSize);
    }
    public void setFragmentView(FragmentDownloader fragmentDownloader) {
        this.fileDownloaderID = fragmentDownloader.getFileDownloaderID();
        this.threadID = fragmentDownloader.getThreadID();
        this.startByte = fragmentDownloader.getStartByte();
        this.fragmentSize = fragmentDownloader.getFragmentSize();
        this.downloadedBytes = fragmentDownloader.getDownloaded();
        this.status = fragmentDownloader.getStatus();
    }
}
