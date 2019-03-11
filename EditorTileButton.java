import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;

class EditorTileButton extends JButton {
    private int id;
    private TileSet tileSet;

    public EditorTileButton(){
        super();
        tileSet = new TileSet("res/tileSet.png", 12,12,3);
        loadImage(id);
    }
    public void setTile(){

    }
    public void loadImage(int pID){
        this.setBorder(null);
        id = pID;
        try{
        Image img = tileSet.tileSet[id].tileImage;
        img = img.getScaledInstance(50,50, Image.SCALE_SMOOTH);
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