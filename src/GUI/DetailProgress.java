package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;

public class DetailProgress extends JFrame {
    private JLabel label_Thread;
    private JPanel progressBar;
    private JScrollPane progressView;
    private JTable progressDetail;

    public DetailProgress() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Detail Progress");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        progressDetail = new JTable();
        progressDetail.setModel(new DefaultTableModel(
                new Object[][]{
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null}
                },
                new String[]{
                        "STT", "Đã tải xuống", "Thông tin", "  "
                }
        ));
        progressDetail.setName("progressDetail");

        progressView = new JScrollPane();
        progressView.setViewportView(progressDetail);

        int number = 8,width = 400/number;
        progressBar = new JPanel();
        progressBar.setPreferredSize(new Dimension(400, 30));
        JPanel panel = new JPanel(new GridLayout(1, number));
        ArrayList<JProgressBar> progressBarArray = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            JProgressBar progressBar = new JProgressBar(0, 100);
            progressBar.setPreferredSize(new Dimension(width, 30));
            progressBar.setValue(100);
            progressBar.setStringPainted(true);
            progressBarArray.add(progressBar);
            panel.add(progressBar);
        }
        progressBar.add(panel);

        label_Thread = new JLabel();
        label_Thread.setText("Threads");


        GroupLayout progressBarLayout = new GroupLayout(progressBar);


        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(progressView, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label_Thread, GroupLayout.PREFERRED_SIZE, 58, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(label_Thread)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(progressView, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new DetailProgress().setVisible(true);
            }
        });
    }
}