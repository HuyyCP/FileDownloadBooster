package GUI;

import BLL.FileDownloader;

import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

public class DownloadsTableModel extends AbstractTableModel implements Observer
{
    private static final String[] columnNames = {"Order", "Filename", "Progress", "Status"};
    private static final Class[] columnClasses = {String.class, String.class, JProgressBar.class, String.class};
    private static final String[] STATUSES = {"REDIRECTING","WAITING", "DOWNLOADING", "PAUSED", "CANCELLED", "COMPLETED", "ERROR"};
    private ArrayList<FileDownloader> downloadList = new ArrayList<>();

    public void addDownload(FileDownloader download) {
        download.addObserver(this);
        downloadList.add(download);
        fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
    }

    public FileDownloader getDownload(int row) {
        return downloadList.get(row);
    }

    public void clearDownload(int row) {
        downloadList.remove(row);
        fireTableRowsDeleted(row, row);
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Class getColumnClass(int col) {
        return columnClasses[col];
    }

    public int getRowCount() {
        return downloadList.size();
    }

    public Object getValueAt(int row, int col) {
        FileDownloader download = downloadList.get(row);
        switch (col) {
            case 0: // Order
                return row + 1;
            case 1: // FileName
                return download.getFileName();
            case 2: // Progress
                return download.getProgress();
            case 3: // Status
                return DownloadsTableModel.STATUSES[download.getStatus().ordinal()];
        }
        return null;
    }


    public void update(Observable o, Object arg) {
        int index = downloadList.indexOf(o);
        fireTableRowsUpdated(index, index);
    }
}

