import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class TileSet{
  private String tileSetImagePath;
  private int numberOfTilesX, numberOfTilesY;
    private BufferedImage tileSetImage;
  public Tile[] tileSet;
    private int width = Tile.TILEWIDTH, height = Tile.TILEHEIGHT;
  private int border;

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
            String filename = temp[temp.length - 1];
            String[] filenameSplit = null;
            filename = filename.replace(".png", "");
            int tillCharacter = filename.indexOf(" -");
            filename = filename.substring(0, tillCharacter);
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
      e.printStackTrace();
    }
    int i = 0;
      for(int y = 0; y < numberOfTilesY; y++) {
        for(int x = 0; x < numberOfTilesX; x++) {
          BufferedImage bi = tileSetImage.getSubimage(x * (width + border), y * (height + border), width, height);
          bi.getScaledInstance(Tile.TILEWIDTH, Tile.TILEHEIGHT, Image.SCALE_SMOOTH);
        tileSet[i++] = new Tile(bi);
      }
    }
  }

    public BufferedImage getTileSetImage() {
        return tileSetImage;
    }

    public String getTileSetImagePath() {
    return tileSetImagePath;
  }
}

