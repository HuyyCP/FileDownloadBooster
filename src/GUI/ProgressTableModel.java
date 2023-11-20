package GUI;

import BLL.FileDownloader;
import BLL.FragmentDownloader;
import Utils.Data.FragmentWatcher;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import static Utils.Data.Constants.NUMTHREADS;

public class ProgressTableModel extends AbstractTableModel implements Observer {
    private static final Class[] columnClasses = new Class[NUMTHREADS];

    private FileDownloader fileDownloader;

    public ProgressTableModel(FileDownloader fileDownloader) {
        fileDownloader.addObserver(this);
        this.fileDownloader = fileDownloader;
        Arrays.fill(columnClasses, JProgressBar.class);
    }

    public Class getColumnClass(int col) {
        return columnClasses[col];
    }
    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount() {
        return this.fileDownloader.getFragmentWatchers().size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return this.fileDownloader.getFragmentWatchers().get(columnIndex).getProgress();
    }

    @Override
    public void update(Observable o, Object arg) {
        this.fileDownloader = (FileDownloader) o;
        if(arg != null) {
            int index = (int) arg;
            fireTableCellUpdated(0, index);
        }
    }
}
