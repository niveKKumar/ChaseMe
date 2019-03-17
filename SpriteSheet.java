import java.awt.image.*; 
import java.io.*;
import javax.imageio.*;
import java.awt.image.BufferedImage;


public class SpriteSheet{
  private BufferedImage spriteSheet;
  private BufferedImage [][] sprites;
  
  public SpriteSheet(String pSpriteSheetPath, int pDirections, int pMoves){
//    int pTile.TILEWIDTH, int pTile.TILEHEIGHT
    sprites = new BufferedImage[pDirections][pMoves];
    try {
      spriteSheet = ImageIO.read(new File(pSpriteSheetPath));
    } catch(IOException e) {
    e.printStackTrace();
    System.out.println("LADEFEHLER SPRITESHEET");  
    return;  
    } // end of try
  for ( int direction = 0;direction<pDirections ;direction++ ) {
    for ( int move=0;move<pMoves ;move++ ) {
        sprites[direction][move] = spriteSheet.getSubimage(move*Tile.TILEWIDTH, direction*Tile.TILEHEIGHT,Tile.TILEWIDTH,Tile.TILEHEIGHT);
    } // end of for
  } // end of for
  }
  public BufferedImage getSpriteElement(int pDirection,int pMove){
   return sprites[pDirection][pMove];
  }
}

