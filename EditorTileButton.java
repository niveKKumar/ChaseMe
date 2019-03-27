import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

class EditorTileButton extends JButton  {
    private int id;
    private TileSet tileSet = new TileSet("res/tileSet.png", 12,12,3);
    public static int size = 50;

    public EditorTileButton(){
        super();
        loadImage(id);
    }
    public EditorTileButton(int id){
        super();
        loadImage(id);
    }

    public void loadImage(int imageno){
        this.setBorder(null);
        try{
        Image img = tileSet.tileSet[imageno].tileImage;
        img = img.getScaledInstance(size,size, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(img);
        this.setIcon(icon);
    } catch(Exception e) {
        System.out.println("Fehler beim Laden von Bild");}
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

}