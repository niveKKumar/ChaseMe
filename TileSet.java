import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;


public class TileSet{
    private String tileSetImagePath; /*fungiert auch als Name*/
    private String name;
  private int numberOfTilesX, numberOfTilesY;
    private BufferedImage tileSetImage;
  public Tile[] tileSet;
    private int width = Tile.TILEWIDTH, height = Tile.TILEHEIGHT;
  private int border;
    private LinkedList<Integer> blockedTiles = new LinkedList();

    public TileSet(String pTileSetImagePath) {
        /**
         * Automatisches Splitten des TileSets
         */
        tileSetImagePath = pTileSetImagePath;
        try {
            if (tileSetImagePath.contains("\\")) {
                tileSetImagePath = tileSetImagePath.replaceAll("\\\\", "/");
            }

            String[] temp;
            temp = tileSetImagePath.split("/");
            String filename = temp[temp.length - 1]; // Das letze Paket vom Path
            String[] filenameSplit = null;
            filename = filename.replace(".png", ""); //DateiZ
            temp = filename.split(" -");
            //System.out.println("Endpfad:" + filename);
            filename = temp[0];
            //System.out.println("AnfagsTeil (Daten):" + filename);
            name = temp[1];
            //System.out.println("Name:" + name);
            filenameSplit = filename.split("x");

            numberOfTilesX = Integer.parseInt(filenameSplit[0]);
            numberOfTilesY = Integer.parseInt(filenameSplit[1]);
            border = Integer.parseInt(filenameSplit[2]);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "TileSet hat einen ungültigen TileSet Pfad", "Tile Set Fehler", JOptionPane.WARNING_MESSAGE);
            Meldungen m = new Meldungen(null, true, "null");
            m.tileSetAbfrage(pTileSetImagePath);
            numberOfTilesX = Integer.parseInt(m.getUserInput(0));
            numberOfTilesY = Integer.parseInt(m.getUserInput(1));
            border = Integer.parseInt(m.getUserInput(2));
        }
        tileSet = new Tile[numberOfTilesX * numberOfTilesY];
        createTileSetImages();

    }
  public TileSet(String pTileSetImagePath, int pNumberOfTilesX, int pNumberOfTilesY, int pBorder){
    tileSetImagePath = pTileSetImagePath;
    numberOfTilesX = pNumberOfTilesX;
    numberOfTilesY = pNumberOfTilesY;
    border = pBorder;
    tileSet = new Tile[numberOfTilesX * numberOfTilesY];
    createTileSetImages();
  }

    public void createTileSetImages(){
    try {
        tileSetImage = ImageIO.read(new File(tileSetImagePath));
        width = tileSetImage.getWidth() / numberOfTilesX - border;
        height = tileSetImage.getHeight() / numberOfTilesY - border;
    } catch (IOException e) {
        System.out.println("Datei nicht vorhanden !");
    }
    int i = 0;
      for(int y = 0; y < numberOfTilesY; y++) {
        for(int x = 0; x < numberOfTilesX; x++) {
            BufferedImage bi = null;
            try {
                bi = tileSetImage.getSubimage(x * (width + border), y * (height + border), width, height);
                bi.getScaledInstance(Tile.TILEWIDTH, Tile.TILEHEIGHT, Image.SCALE_SMOOTH);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("TileSet Daten sind falsch! Probiere manuell");
                Meldungen m = new Meldungen(null, true, "null");
                m.tileSetAbfrage(tileSetImagePath);
                height = Integer.parseInt(m.getUserInput(1));
                width = Integer.parseInt(m.getUserInput(0));
                border = Integer.parseInt(m.getUserInput(2));
                bi = tileSetImage.getSubimage(x * (width + border), y * (height + border), width, height);
                bi.getScaledInstance(Tile.TILEWIDTH, Tile.TILEHEIGHT, Image.SCALE_SMOOTH);
            }/*catch (Exception e){
                System.out.println("Zweite Angabe falsch. Überprüfen Sie Ihr TileSet und versuchen Sie es später erneut");
            }*/
        tileSet[i++] = new Tile(bi);
      }
    }
  }

    public LinkedList<Integer> getBlockedTiles() {
        return blockedTiles;
    }

    public BufferedImage getTileSetImage() {
        return tileSetImage;
    }

    public String getTileSetImagePath() {
    return tileSetImagePath;
  }

    public void setBlockedTiles(Integer blockedID, boolean umkehren) {
        if (umkehren == false) {
            if (!blockedTiles.contains(blockedID)) {
                this.blockedTiles.add(blockedID);
            }
        } else {
            // TODO: 07.05.2019 !!!!!!!
            /* Pseudo-Code: Alle IDs die NICHT in der jetzigen Liste (blocked Tiles)
                vorhanden ist hinzufügen und alle geblockten Tiles entfernen*/
        }
    }

    public void setBlockedTiles(Integer[] blockedIDArray, boolean umkehren) {
        if (umkehren == false) {
            for (int i = 0; i < blockedIDArray.length; i++) {
                setBlockedTiles(blockedIDArray[i], umkehren);
            }
        } else {
            /* Pseudo-Code: Alle IDs die NICHT in der jetzigen Liste (blocked Tiles)
                vorhanden ist hinzufügen und alle geblockten Tiles entfernen*/

        }
    }

    public void automaticBlockedTiles() {
        switch (tileSetImagePath) {
            case "":
                break;

        }
    }
}

