package GUI;

import BLL.FileDownloader;
import Utils.Data.FragmentWatcher;

import javax.swing.table.AbstractTableModel;
import java.util.Observable;
import java.util.Observer;

public class FigureTableModel extends AbstractTableModel implements Observer {
    private static final String[] columnNames = {"Thread ID", "Start byte", "Fragment size", "Downloaded bytes", "Status"};
    private static final Class[] columnClasses = {String.class, String.class, String.class, String.class, String.class};
    private static final String[] STATUSES = {"REDIRECTING","WAITING", "DOWNLOADING", "PAUSED", "CANCELLED", "COMPLETED", "ERROR"};
    private FileDownloader fileDownloader;

    public FigureTableModel(FileDownloader fileDownloader) {
        fileDownloader.addObserver(this);
        this.fileDownloader = fileDownloader;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Class getColumnClass(int col) {
        return columnClasses[col];
    }

    @Override
    public int getRowCount() {
        return this.fileDownloader.getFragmentWatchers().size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        FragmentWatcher fragmentWatcher = this.fileDownloader.getFragmentWatchers().get(rowIndex);
        switch(columnIndex) {
            case 0: // Thread ID
                return fragmentWatcher.getThreadID();
            case 1: // Start byte
                return fragmentWatcher.getOffset();
            case 2: // Fragment Size
                return fragmentWatcher.getFragmentSize();
            case 3: // Downloaded bytes
                return fragmentWatcher.getDownloaded();
            case 4: // Status
                return FigureTableModel.STATUSES[fragmentWatcher.getStatus().ordinal()];
        }
        return null;
    }

    @Override
    public void update(Observable o, Object arg) {
        this.fileDownloader = (FileDownloader) o;
        if(arg != null) {
            int index = (int) arg;
            fireTableRowsUpdated(index, index);
        }
    }
}
