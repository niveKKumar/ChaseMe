import javax.swing.*;
import java.awt.*;

class EditorTileButton extends JButton {
    private int id;
    private TileSet tileSet;
    public static int size = 50;

    public EditorTileButton(int id, TileSet ts) {
        super();
        tileSet = ts;
        this.id = id;
        loadImage();
    }

    public void loadImage() {
        this.setBorder(null);
        try {
            Image img = tileSet.tileSet[id].tileImage;
            img = img.getScaledInstance(size, size, Image.SCALE_SMOOTH);
            ImageIcon icon = new ImageIcon(img);
            this.setIcon(icon);
        } catch (Exception e) {
            System.out.println("Fehler beim Laden von Bild");
        }
    }

    public TileSet getTileSet() {
        return tileSet;
    }

    public int getId() {
        return id;
    }

}