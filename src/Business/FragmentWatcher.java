package Business;

import Utils.Data.DownloadStatus;
import java.util.Observable;

public class FragmentWatcher extends Observable{
    private int fileDownloaderID;
    private int threadID;
    private long offset;
    private long fragmentSize;
    private long downloadedBytes;
    private DownloadStatus status;
    private boolean isNewDownload;
    public FragmentWatcher(int fileDownloaderID, int threadID) {
        this.fileDownloaderID = fileDownloaderID;
        this.threadID = threadID;
        this.offset = 0;
        this.fragmentSize = 0;
        this.downloadedBytes = 0;
        this.status = DownloadStatus.WAITING;
        this.isNewDownload = true;
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

    public void setStatus (DownloadStatus status) {
        this.status = status;
        setChanged();
        notifyObservers();
    }

    public long getOffset() {
        return this.offset;
    }

    public long getFragmentSize() {
        return this.fragmentSize;
    }

    public void setDownloadRange(long startBytes, long endBytes) {
        this.offset = startBytes;
        this.fragmentSize = endBytes - startBytes + 1;
    }

    public long getDownloaded() {
        return this.downloadedBytes;
    }

    public int getProgress() {
        return this.fragmentSize == 0 ? 0 : (int)(this.downloadedBytes * 100 / this.fragmentSize);
    }

    public void setFragmentView(FragmentDownloader fragmentDownloader) {
        addObserver(fragmentDownloader);
        if(!isNewDownload) {
            fragmentDownloader.setDownloaded(this.downloadedBytes);
//            fragmentDownloader.setStatus(this.status);

            return;
        }
        this.fileDownloaderID = fragmentDownloader.getFileDownloaderID();
        this.threadID = fragmentDownloader.getThreadID();
        this.offset = fragmentDownloader.getOffset();
        this.fragmentSize = fragmentDownloader.getFragmentSize();
        this.status = fragmentDownloader.getStatus();
        this.isNewDownload = false;
    }
}
