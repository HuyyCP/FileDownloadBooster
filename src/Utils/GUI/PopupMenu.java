package Utils.GUI;

import BLL.FileDownloader;
import GUI.DetailForm;
import GUI.DownloadsTableModel;

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
    DetailForm detailForm;
    public PopupMenu(int rowIndex, JTable table, DownloadsTableModel tableModel) {
        super();
        this.rowIndex = rowIndex;
        this.tableModel = tableModel;
        this.fileDownloader = this.tableModel.getDownload(rowIndex);
        this.table = table;
        InitUI();
        AddListener();
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
                JOptionPane.showMessageDialog(null, "Pause selected");
            }
        });

        resumeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Resume selected");
            }
        });

        cancelItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Cancel selected");
            }
        });

        clearItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Clear selected");
            }
        });
    }
}
