import java.awt.image.*; 
import java.io.*;
import javax.imageio.*;
import java.awt.image.BufferedImage;


public class TileSet{
  private String tileSetImagePath;
  private int numberOfTilesX, numberOfTilesY;
  private BufferedImage tileSetImage;
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
    try {
      tileSetImage = ImageIO.read(new File(tileSetImagePath));
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    
    int i = 0;
      for(int y = 0; y < numberOfTilesY; y++) {
        for(int x = 0; x < numberOfTilesX; x++) {
        BufferedImage bi = tileSetImage.getSubimage(x * (Tile.TILEWIDTH+border), y * (Tile.TILEHEIGHT+border), Tile.TILEWIDTH, Tile.TILEHEIGHT);
        tileSet[i++] = new Tile(bi);
      }
    }
  }
  
  
  
}

