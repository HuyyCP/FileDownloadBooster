package GUI;

import BLL.DownloadStatus;
import BLL.FileDownloadManager;
import BLL.FileDownloader;
import Utils.GUI.*;
import Utils.GUI.Button;
import Utils.GUI.TextField;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;


public class MainForm extends JFrame implements Observer
{
    private TextField urlInput;
    private DownloadsTableModel tableModel;
    private JTable table;
    private Button addButton, pauseButton, resumeButton, cancelButton, clearButton;
    private Color backgroundColor = new Color(221,230, 237);
    private Color tableHeadeColor = new Color(39, 55, 77);
    private Color textColor = new Color(255,255,255);
    private Font mainFont = new Font("Sans-Serif", Font.PLAIN, 16);
    private FileDownloader selectedDownload;
    private FileDownloadManager fileDownloadManager;
    private boolean clearing;

    public MainForm() {
        fileDownloadManager = new FileDownloadManager();
        setTitle("File Download Booster");
        setSize(640, 480);
        setMinimumSize(new Dimension(640,480));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setFont(mainFont);

//        JMenuBar menuBar = new JMenuBar();
//        JMenu fileMenu = new JMenu("File");
        // đoạn này set keyevent cho menubar -> chưa cần lắm
//        fileMenu.setMnemonic(KeyEvent.VK_F);
//        JMenuItem fileExitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
//        fileExitMenuItem.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                actionExit();
//            }
//        });
//        fileMenu.add(fileExitMenuItem);
//        menuBar.add(fileMenu);
//        setJMenuBar(menuBar);
        // mark1: từ đây trở lên là set menubar


        // Init input panel
        JPanel inputPanel = new JPanel();
        urlInput = new TextField();
        urlInput.setFont(mainFont);
        inputPanel.add(urlInput);
        addButton = new Button("Download");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    actionAdd();
                } catch (MalformedURLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        inputPanel.add(addButton);

        // init table
        tableModel = new DownloadsTableModel();
        table = new JTable(tableModel);
        table.setShowGrid(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ProgressRenderer renderer = new ProgressRenderer(0, 100);
        renderer.setStringPainted(true);
        table.setDefaultRenderer(JProgressBar.class, renderer);
        table.setRowHeight((int) renderer.getPreferredSize().getHeight());

        JTableHeader header = table.getTableHeader();
        header.setEnabled(false);
        header.setBackground(tableHeadeColor);
        header.setForeground(textColor);

        TableColumn orderColumn = table.getColumn("Order");
        orderColumn.setMaxWidth(50);

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                tableSelectionChanged();
            }
        });

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent le) {
                tableSelectionChanged();
            }
        });



        // init downloads panel
        JPanel downloadsPanel = new JPanel();
        downloadsPanel.setBorder(BorderFactory.createTitledBorder("Downloads"));
        downloadsPanel.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollbar());
        downloadsPanel.add(scrollPane, BorderLayout.CENTER);



        JPanel buttonsPanel = new JPanel();
        pauseButton = new Button("Pause");
//        pauseButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                actionPause();
//            }
//        });
        pauseButton.setEnabled(false);
        buttonsPanel.add(pauseButton);
        resumeButton = new Button("Resume");
//        resumeButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                actionResume();
//            }
//        });
        resumeButton.setEnabled(false);
        buttonsPanel.add(resumeButton);
        cancelButton = new Button("Cancel");
//        cancelButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                actionCancel();
//            }
//        });
        cancelButton.setEnabled(false);
        buttonsPanel.add(cancelButton);
        clearButton = new Button("Clear");
//        clearButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                actionClear();
//            }
//        });
        clearButton.setEnabled(false);
        buttonsPanel.add(clearButton);
        inputPanel.setBackground(backgroundColor);
        downloadsPanel.setBackground(backgroundColor);
        buttonsPanel.setBackground(backgroundColor);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(inputPanel, BorderLayout.NORTH);
        getContentPane().add(downloadsPanel, BorderLayout.CENTER);
        getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void actionExit() {
        System.exit(0);
    }

    private void actionAdd() throws MalformedURLException {
        URL verifiedUrl = verifyUrl(urlInput.getText());
        if (verifiedUrl != null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Savepath");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                tableModel.addDownload(new FileDownloader(verifiedUrl, fileChooser.getSelectedFile()));
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

    private void tableSelectionChanged() {
        if (selectedDownload != null)
            selectedDownload.deleteObserver(MainForm.this);
        if (!clearing && table.getSelectedRow() > -1) {
            selectedDownload = tableModel.getDownload(table.getSelectedRow());
            selectedDownload.addObserver(MainForm.this);
            updateButtons();
        }
    }

//    private void actionPause() {
//        selectedDownload.pause();
//        updateButtons();
//    }
//
//    private void actionResume() {
//        selectedDownload.resume();
//        updateButtons();
//    }
//
//    private void actionCancel() {
//        selectedDownload.cancel();
//        updateButtons();
//    }

    private void actionClear() {
        clearing = true;
        tableModel.clearDownload(table.getSelectedRow());
        clearing = false;
        selectedDownload = null;
        updateButtons();
    }

    private void updateButtons() {
        if (selectedDownload != null) {
            DownloadStatus status = selectedDownload.getStatus();
            switch (status) {
                case DownloadStatus.DOWNLOADING, DownloadStatus.REDIRECTING:
                    pauseButton.setEnabled(true);
                    resumeButton.setEnabled(false);
                    cancelButton.setEnabled(true);
                    clearButton.setEnabled(false);
                    break;
                case DownloadStatus.PAUSED:
                    pauseButton.setEnabled(false);
                    resumeButton.setEnabled(true);
                    cancelButton.setEnabled(true);
                    clearButton.setEnabled(false);
                    break;
                default: // COMPLETE or CANCELLED or WAITING
                    pauseButton.setEnabled(false);
                    resumeButton.setEnabled(false);
                    cancelButton.setEnabled(false);
                    clearButton.setEnabled(true);
            }
        } else {
            pauseButton.setEnabled(false);
            resumeButton.setEnabled(false);
            cancelButton.setEnabled(false);
            clearButton.setEnabled(false);
        }
    }

    public void update(Observable o, Object arg) {
        if (selectedDownload != null && selectedDownload.equals(o))
            updateButtons();
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
                        fileDownloadManager.downloadFile(tableModel.getDownload(i));
//                        break;
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

