package BLL;

import Utils.Data.DownloadStatus;
import Utils.Data.FragmentWatcher;

import java.io.File;
import java.util.Observable;
import java.util.Observer;
import java.net.URL;
import java.util.Vector;

import static Utils.Data.Constants.NUMTHREADS;

public class FileDownloader extends Observable implements Observer {
    private final int ID;
    public String fileName;
    private URL url;
    private long fileSize;
    private long downloadedBytes;
    public String savePath;
    private DownloadStatus status;
    private final Vector<FragmentWatcher> fragmentWatchers;

    public FileDownloader(int ID, URL url, File savePath) {
        this.ID = ID;
        this.url = url;
        this.downloadedBytes = 0;
        this.fileSize = 0;
        this.fileName = url.getPath().substring(url.getPath().lastIndexOf('/') + 1);
        this.savePath = savePath + "\\"+ fileName;
        this.status = DownloadStatus.WAITING;
        this.fragmentWatchers = new Vector<>();
        for(int i = 0; i < NUMTHREADS; i++) {
            FragmentWatcher fragmentWatcher = new FragmentWatcher(this.ID, i + 1);
            this.fragmentWatchers.add(fragmentWatcher);
        }
        System.out.println("Savepath: " + this.savePath);
    }

    @Override
    synchronized public void update(Observable o, Object arg) {
        FragmentDownloader fragmentDownloader = (FragmentDownloader) o;
        if(fragmentDownloader.getStatus() == DownloadStatus.DOWNLOADING) {
            downloadedBytes += (int) arg;
//            fragmentWatchers.elementAt(fragmentDownloader.getThreadID()).update(fragmentDownloader);
//            setChanged();
//            notifyObservers();
            System.out.print("Downloaded process: " + downloadedBytes * 100 / fileSize + " %\r");
        }
//        else if (fragmentDownloader.getStatus() == DownloadStatus.COMPLETED) {
//            fragmentWatchers.elementAt(fragmentDownloader.getThreadID()).update(fragmentDownloader);
//        }
        fragmentWatchers.elementAt(fragmentDownloader.getThreadID() - 1).update(fragmentDownloader);
        setChanged();
        notifyObservers(fragmentDownloader.getThreadID() - 1);
    }

    public int getID() {
        return this.ID;
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
        for(FragmentWatcher watcher : fragmentWatchers) {
            watcher.setStatus(this.status);
        }
        setChanged();
        notifyObservers();
    }

    public int getProgress() {
        return this.fileSize == 0 ? 0 : (int)(downloadedBytes * 100 / fileSize);
    }

    public String getFileName() {
        return this.fileName;
    }

    public void addFragmentView(FragmentDownloader fragmentDownloader, int watcherIndex) {
        this.fragmentWatchers.elementAt(watcherIndex).setFragmentView(fragmentDownloader);
    }

    public Vector<FragmentWatcher> getFragmentWatchers() {
        return this.fragmentWatchers;
    }
    public void removeFragmentView() {
        for(FragmentWatcher watcher : this.fragmentWatchers) {
            watcher.deleteObservers();
        }
    }
}
