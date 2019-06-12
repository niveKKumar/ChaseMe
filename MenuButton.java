import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class MenuButton extends JButton {

    public MenuButton() {
        super();
        setForeground(Color.DARK_GRAY);
        setBackground(Color.WHITE);
        Border line = new LineBorder(Color.BLACK);
        Border margin = new EmptyBorder(5, 15, 5, 15);
        Border compound = new CompoundBorder(line, margin);
        setBorder(compound);
        setFont(getFont().deriveFont(Font.BOLD, 50));
    }

    public MenuButton(String text) {
        this();
        setText(text);
    }

    public MenuButton(String iconPath, int height, int width) {
        this();
        setImage(iconPath, height, width);
    }

    public MenuButton(int level) {
        this(Integer.toString(level));
    }

    public MenuButton(ImageIcon icon) {

    }

    public void setImage(String pImagePath, int width, int height) {
        Image imgSettings = (new ImageIcon(pImagePath)).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(imgSettings);
        setIcon(icon);
        setOpaque(false);
        setBackground(null);
        setBorder(null);
        setSize(icon.getIconWidth(), icon.getIconHeight());
        setContentAreaFilled(false);
    }

    public void setLocked() {
        setEnabled(false);
        setImage("Content/Graphics/UI/lockIcon.png", 50, 50);
    }

    public void setUnlocked() {
        setEnabled(true);
        setIcon(null);
    }
}
