package GUI;

import Business.FileDownloader;
import Utils.GUI.CustomScrollbar;
import Utils.GUI.ProgressRenderer;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;

import static Utils.Data.Constants.TABLEHEADERCOLOR;
import static Utils.Data.Constants.TEXTCOLOR;

public class DetailForm extends JFrame {
    private JTable progressTable;
    private JTable figureTable;
    private ProgressTableModel progressTableModel;
    private FigureTableModel figureTableModel;
    private FileDownloader fileDownloader;
    private JLabel urlLabel;
    private JTextField urlField;
    private JLabel savepathLabel;
    private JTextField savepathField;
    private JLabel fileSizeLabel;
    private JTextField fileSizeField;

    public DetailForm(FileDownloader fileDownloader) {
        this.fileDownloader = fileDownloader;

        // Progress Table
        this.progressTableModel = new ProgressTableModel(this.fileDownloader);
        this.progressTable = new JTable(this.progressTableModel);

        // Figure Table
        this.figureTableModel = new FigureTableModel(this.fileDownloader);
        this.figureTable = new JTable(this.figureTableModel);

        // URL
        urlLabel = new JLabel("URL");
        urlField = new JTextField();
        urlField.setText(fileDownloader.getURL().toString());

        // Savepath
        savepathLabel = new JLabel("Savepath");
        savepathField = new JTextField();
        savepathField.setText(fileDownloader.getSavePath());

        // File size
        fileSizeLabel = new JLabel("File Size");
        fileSizeField = new JTextField();
        fileSizeField.setText(fileDownloader.getFileSize() + " Bytes");

        InitUI();
    }
    private void InitUI() {
        setTitle("Downloader details");
        setSize(550, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(0, 50));

        // Progress table
        ProgressRenderer renderer = new ProgressRenderer(0, 100);
        renderer.setStringPainted(true);
        this.progressTable.setDefaultRenderer(JProgressBar.class, renderer);
        this.progressTable.setRowHeight(30);
        this.progressTable.setTableHeader(null); // Loại bỏ header của bảng
        this.progressTable.setPreferredScrollableViewportSize(new Dimension(400, 100)); // Đặt kích thước cho bảng

        this.figureTable.setRowHeight(30);

        JTableHeader header = this.figureTable.getTableHeader();
        header.setEnabled(false);
        header.setBackground(TABLEHEADERCOLOR);
        header.setForeground(TEXTCOLOR);

        JScrollPane figureTableScrollPane = new JScrollPane(figureTable);
        figureTableScrollPane.getVerticalScrollBar().setUI(new CustomScrollbar());

        // Left panel
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        leftPanel.add(progressTable, BorderLayout.NORTH);
        leftPanel.add(figureTableScrollPane, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 5, 10);

        Font labelFont = new Font("Arial", Font.BOLD, 14);

        urlLabel.setFont(labelFont);
        urlField.setPreferredSize(new Dimension(150, 30));

        savepathLabel.setFont(labelFont);
        savepathField.setPreferredSize(new Dimension(150, 30));

        fileSizeLabel.setFont(labelFont);
        fileSizeField.setPreferredSize(new Dimension(150, 30));

        // Right panel
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        rightPanel.add(urlLabel, gbc);
        rightPanel.add(urlField, gbc);

        rightPanel.add(savepathLabel, gbc);
        rightPanel.add(savepathField, gbc);

//        rightPanel.add(fileSizeLabel, gbc);
//        rightPanel.add(fileSizeField, gbc);

        // Add components to the frame
        add(leftPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

//        setVisible(true);
    }
}