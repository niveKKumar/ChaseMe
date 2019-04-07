import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class TileSet{
  private String tileSetImagePath;
  private int numberOfTilesX, numberOfTilesY;
  public Tile[] tileSet;
  private int border;
  
  
  public TileSet(String pTileSetImagePath, int pNumberOfTilesX, int pNumberOfTilesY, int pBorder){
    tileSetImagePath = pTileSetImagePath;
    numberOfTilesX = pNumberOfTilesX;
    numberOfTilesY = pNumberOfTilesY;
    border = pBorder;
    tileSet = new Tile[numberOfTilesX * numberOfTilesY];
    createTileSetImages();
  }

  public void createTileSetImages(){
    BufferedImage tileSetImage;
    int width = Tile.TILEWIDTH, height = Tile.TILEHEIGHT;
    try {
      // TODO: 06.04.2019 TileSet erkennt breite automatisch DONE!
      tileSetImage = ImageIO.read(new File(tileSetImagePath));
      width = tileSetImage.getWidth() / numberOfTilesX - border;
      height = tileSetImage.getHeight() / numberOfTilesY - border;
//      System.out.println("TS: height_"+height+" width_"+width);

    } catch (IOException e) {
      e.printStackTrace();
      return;
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

