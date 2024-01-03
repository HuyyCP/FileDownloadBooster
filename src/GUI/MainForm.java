package GUI;

import Business.FileDownloadManager;
import Business.FileDownloader;
import Utils.Data.DownloadStatus;
import Utils.GUI.Button;
import Utils.GUI.CustomScrollbar;
import Utils.GUI.ProgressRenderer;
import Utils.GUI.TextField;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;

import static Utils.Data.Constants.*;


public class MainForm extends JFrame
{
    private TextField urlInput;
    private DownloadsTableModel tableModel;
    private JTable table;
    private Button addButton;
    private FileDownloadManager fileDownloadManager;
    private PopupMenu popupMenu;

    public MainForm() {
        InitUI();
        AddListeners();
    }
    private void InitUI() {
        fileDownloadManager = new FileDownloadManager();
        setTitle("File Download Booster");
        setSize(640, 480);
        setMinimumSize(new Dimension(640,480));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setFont(MAINFONT);

        // Init input panel
        JPanel inputPanel = new JPanel();
        urlInput = new TextField();
        urlInput.setFont(MAINFONT);
        inputPanel.add(urlInput);
        addButton = new Button("Download");
        inputPanel.add(addButton);

        // Init download table
        tableModel = new DownloadsTableModel();
        table = new JTable(tableModel);
        table.setShowGrid(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ProgressRenderer renderer = new ProgressRenderer(0, 100);
        renderer.setStringPainted(true);
        table.setDefaultRenderer(JProgressBar.class, renderer);
        table.setRowHeight(30);

        JTableHeader header = table.getTableHeader();
        header.setEnabled(false);
        header.setBackground(TABLEHEADERCOLOR);
        header.setForeground(TEXTCOLOR);

        TableColumn orderColumn = table.getColumn("Order");
        orderColumn.setMaxWidth(50);

        // Init downloads panel
        JPanel downloadsPanel = new JPanel();
        downloadsPanel.setBorder(BorderFactory.createTitledBorder("Downloads"));
        downloadsPanel.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollbar());
        downloadsPanel.add(scrollPane, BorderLayout.CENTER);

        inputPanel.setBackground(BACKGROUNDCOLOR);
        downloadsPanel.setBackground(BACKGROUNDCOLOR);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(inputPanel, BorderLayout.NORTH);
        getContentPane().add(downloadsPanel, BorderLayout.CENTER);

        setVisible(true);
    }
    private void AddListeners() {
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    actionAdd();
                } catch (MalformedURLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int r = table.rowAtPoint(e.getPoint());
                if (r >= 0 && r < table.getRowCount()) {
                    table.setRowSelectionInterval(r, r);
                } else {
                    table.clearSelection();
                }

                int rowIndex = table.getSelectedRow();
                if (rowIndex < 0)
                    return;
                if (e.isPopupTrigger() && e.getComponent() instanceof JTable ) {
                    popupMenu = new PopupMenu(rowIndex, table, tableModel, fileDownloadManager);
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    private void actionAdd() throws MalformedURLException {
        URL verifiedUrl = verifyUrl(urlInput.getText());
        if (verifiedUrl != null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Savepath");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                tableModel.addDownload(new FileDownloader(tableModel.getRowCount() - 1, verifiedUrl, fileChooser.getSelectedFile()));
                urlInput.setText(""); // reset add text field
            } else {
                System.out.println("No selection");
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid Download URL", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private URL verifyUrl(String url) {
        if (!url.toLowerCase().startsWith("http://") && !url.toLowerCase().startsWith("https://"))
            return null;
        URL verifiedUrl;
        try {
            verifiedUrl = new URL(url);
        } catch (Exception e) {
            return null;
        }
        if (verifiedUrl.getFile().length() < 2)
            return null;
        return verifiedUrl;
    }

    public void run() {
        Thread thread = new Thread(() -> {
            while(true) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                for(int i = 0; i < tableModel.getRowCount(); i++) {
                    if(tableModel.getDownload(i).getStatus() == DownloadStatus.WAITING) {
                        FileDownloader selectedDownloader = tableModel.getDownload(i);
//                        Thread threadDownload = new Thread(() -> {
                            fileDownloadManager.handleRedirectURL(selectedDownloader);
                            fileDownloadManager.HandleFragmentation(selectedDownloader);
                            fileDownloadManager.downloadFile(selectedDownloader);
//                        });
//                        threadDownload.start();
//                        fileDownloadManager.handleRedirectURL(selectedDownloader);
//                        fileDownloadManager.HandleFragmentation(selectedDownloader);
//                        fileDownloadManager.downloadFile(selectedDownloader);
                    }
                }
            }
        });
        thread.start();
    }
    public static void main(String[] args) {
        FlatLightLaf.setup();
        MainForm form = new MainForm();
        form.run();
    }
}

