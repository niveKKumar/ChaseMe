import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class MenuButton extends JButton {

    public MenuButton(String text) {
        super(text);
        setForeground(Color.DARK_GRAY);
        setBackground(Color.WHITE);
        Border line = new LineBorder(Color.BLACK);
        Border margin = new EmptyBorder(5, 15, 5, 15);
        Border compound = new CompoundBorder(line, margin);
        setBorder(compound);
        setFont(getFont().deriveFont(Font.BOLD, 50));
    }

    public MenuButton(int level) {
        super(Integer.toString(level));
        setForeground(Color.DARK_GRAY);
        setBackground(Color.WHITE);
        Border line = new LineBorder(Color.BLACK);
        Border margin = new EmptyBorder(5, 15, 5, 15);
        Border compound = new CompoundBorder(line, margin);
        setBorder(compound);
        setFont(getFont().deriveFont(Font.BOLD, 25));
    }

    public MenuButton(ImageIcon icon) {
        setIcon(icon);
        setOpaque(false);
        setBackground(null);
        setVisible(false);
        setBorder(null);
        setSize(icon.getIconWidth(), icon.getIconHeight());
        setContentAreaFilled(false);
    }
}
