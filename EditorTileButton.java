import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

class EditorTileButton extends JButton  {
    private int id;
    private TileSet tileSet= new TileSet("res/tileSet.png", 12, 12, 3);
    public static int size = 50;

    public EditorTileButton(int id,TileSet ts){
        super();
        tileSet = ts;
        this.id = id;
        loadImage(id);
    }
    public EditorTileButton(int id){
        super();
        this.id = id;
        loadImage(id);
    }

    public void loadImage(int imageno) {
        this.setBorder(null);
        try {
            Image img = tileSet.tileSet[imageno].tileImage;
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