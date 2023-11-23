package Utils.GUI;

import BLL.FileDownloadManager;
import BLL.FileDownloader;
import GUI.DetailForm;
import GUI.DownloadsTableModel;
import Utils.Data.DownloadStatus;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PopupMenu extends JPopupMenu {
    JMenuItem detailItem;
    JMenuItem pauseItem;
    JMenuItem resumeItem;
    JMenuItem cancelItem;
    JMenuItem clearItem;
    FileDownloader fileDownloader;
    JTable table;
    int rowIndex;
    DownloadsTableModel tableModel;
    FileDownloadManager fileDownloadManager;
    DetailForm detailForm;
    public PopupMenu(int rowIndex, JTable table, DownloadsTableModel tableModel, FileDownloadManager fileDownloadManager) {
        super();
        this.rowIndex = rowIndex;
        this.tableModel = tableModel;
        this.fileDownloadManager = fileDownloadManager;
        this.fileDownloader = this.tableModel.getDownload(rowIndex);
        this.table = table;
        InitUI();
        AddListener();
        UpdateButtons();
    }
    private void InitUI() {
        detailItem = new JMenuItem("Detail");
        pauseItem = new JMenuItem("Pause");
        resumeItem = new JMenuItem("Resume");
        cancelItem = new JMenuItem("Cancel");
        clearItem = new JMenuItem("Clear");

        this.add(detailItem);
        this.add(pauseItem);
        this.add(resumeItem);
        this.add(cancelItem);
        this.add(clearItem);
    }
    private void AddListener() {
        // Thêm sự kiện cho các mục menu
        detailItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(detailForm == null) {
                    detailForm = new DetailForm(tableModel.getDownload(rowIndex));
                }
                if(!detailForm.isVisible())
                    detailForm.setVisible(true);
                detailForm.requestFocus();
            }
        });

        pauseItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileDownloader.setStatus(DownloadStatus.PAUSED);
            }
        });

        resumeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileDownloader.setStatus(DownloadStatus.DOWNLOADING);
                fileDownloadManager.downloadFile(fileDownloader);
            }
        });

        cancelItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileDownloader.setStatus(DownloadStatus.CANCELLED);
            }
        });

        clearItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableModel.removeDownload(rowIndex);
            }
        });
    }

    private void UpdateButtons() {
        if (fileDownloader != null) {
            DownloadStatus status = fileDownloader.getStatus();
            switch (status) {
                case DOWNLOADING, REDIRECTING:
                    pauseItem.setEnabled(true);
                    resumeItem.setEnabled(false);
                    cancelItem.setEnabled(true);
                    clearItem.setEnabled(false);
                    break;
                case PAUSED:
                    pauseItem.setEnabled(false);
                    resumeItem.setEnabled(true);
                    cancelItem.setEnabled(true);
                    clearItem.setEnabled(false);
                    break;
                default: // COMPLETE or CANCELLED or ERROR or WAITING
                    pauseItem.setEnabled(false);
                    resumeItem.setEnabled(false);
                    cancelItem.setEnabled(false);
                    clearItem.setEnabled(true);
            }
        }
    }
}
