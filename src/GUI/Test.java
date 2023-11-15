package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Test {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Table Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Tạo một mô hình dữ liệu cho bảng
        String[] columnNames = {"Thái ", "Huỳnh ", "Nè"};
        Object[][] data = {
                {"một", "vòng", "quay"},
                {"thời", "gian", "ta"},
                {"là", "bụi", "sương"}
        };
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(model);

        // Tạo menu chuột phải
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem detailItem = new JMenuItem("Detail");
        JMenuItem pauseItem = new JMenuItem("Pause");
        JMenuItem resumeItem = new JMenuItem("Resume");
        JMenuItem cancelItem = new JMenuItem("Cancel");
        JMenuItem clearItem = new JMenuItem("Clear");

        // Thêm sự kiện cho các mục menu
        detailItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Detail selected");
            }
        });

        pauseItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Pause selected");
            }
        });

        resumeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Resume selected");
            }
        });

        cancelItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Cancel selected");
            }
        });

        clearItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setRowCount(0); // Xóa tất cả các dòng trong bảng
            }
        });

        // Thêm các mục vào menu
        popupMenu.add(detailItem);
        popupMenu.add(pauseItem);
        popupMenu.add(resumeItem);
        popupMenu.add(cancelItem);
        popupMenu.add(clearItem);


        // Thêm sự kiện chuột phải cho bảng
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int r = table.rowAtPoint(e.getPoint());
                if (r >= 0 && r < table.getRowCount()) {
                    table.setRowSelectionInterval(r, r);
                } else {
                    table.clearSelection();
                }

                int rowindex = table.getSelectedRow();
                if (rowindex < 0)
                    return;
                if (e.isPopupTrigger() && e.getComponent() instanceof JTable ) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        // Thêm bảng vào khung
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane);

        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
