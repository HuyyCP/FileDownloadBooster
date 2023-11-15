package Utils.GUI;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class CustomScrollbar extends BasicScrollBarUI {
    private final Dimension dimension = new Dimension();

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

    private JButton createZeroButton() {
        JButton button = new JButton();
        button.setPreferredSize(dimension);
        button.setMinimumSize(dimension);
        button.setMaximumSize(dimension);
        return button;
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        g.setColor(Color.WHITE);
        g.fillRect(trackBounds.x + 5, trackBounds.y, trackBounds.width - 5, trackBounds.height);
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        JScrollBar sb = (JScrollBar) c;
        Color color;
        if (isDragging) {
            color = new Color(134, 134, 134);
        } else if (isThumbRollover()) {
            color = new Color(175, 175, 175);
        } else {
            color = new Color(200, 200, 200);
        }

        g2d.setPaint(color);
        g2d.fillRoundRect(thumbBounds.x + 2, thumbBounds.y, thumbBounds.width - 5, thumbBounds.height, 10, 10);

        g2d.dispose();
    }
}