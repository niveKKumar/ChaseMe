import javax.imageio.ImageIO;
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
        tileSetImagePath = pTileSetImagePath;
        tileSet = new Tile[numberOfTilesX * numberOfTilesY];
//    getAutomaticName();
    }
  public TileSet(String pTileSetImagePath, int pNumberOfTilesX, int pNumberOfTilesY, int pBorder){
    tileSetImagePath = pTileSetImagePath;
    numberOfTilesX = pNumberOfTilesX;
    numberOfTilesY = pNumberOfTilesY;
    border = pBorder;
    tileSet = new Tile[numberOfTilesX * numberOfTilesY];
    createTileSetImages();
  }

    //  public void getAutomaticName(){
//      try {
//          File temp = new File(tileSetImagePath);
//          String filename = temp.getName();
//          String[] filenameSplit = null;
//          filename = filename.replace(".png", "");
//          int tillCharacter = filename.indexOf(" -");
//          filename = filename.substring(0, tillCharacter);
//          filenameSplit = filename.split("x");
//
//          for (int i = 0; i < filenameSplit.length; i++) {
//              System.out.println(filenameSplit[i]);
//          }
//
//
//      } catch (Exception e) {
//
//      }
//  }
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

  public String getTileSetImagePath() {
    return tileSetImagePath;
  }
}

