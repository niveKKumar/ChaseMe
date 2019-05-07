import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

class EditorTileButton extends JButton {
    private int id;
    private BufferedImage img;
    private String tileSetPath;
    public static int size = 50;


    public EditorTileButton(int id, BufferedImage img) {
        super();
        this.id = id;
        this.img = img;
    }

    public EditorTileButton(int id, TileSet ts) {
        super();
        this.id = id;
        this.img = ts.tileSet[id].getTileImage();
        tileSetPath = ts.getTileSetImagePath();
        loadImage(img);
    }

    public void loadImage(BufferedImage img) {
        this.setBorder(null);
        try {
            ImageIcon icon = new ImageIcon(img.getScaledInstance(size, size, Image.SCALE_SMOOTH));
            this.setIcon(icon);
        } catch (Exception e) {
            System.out.println("Fehler beim Laden von Bild");
        }


    }

    public BufferedImage getImg() {
        return img;
    }

    public String getTileSetPath() {
        return tileSetPath;
    }

    public int getId() {
        return id;
    }
}