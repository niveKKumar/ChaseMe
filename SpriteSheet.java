import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class SpriteSheet{
    private BufferedImage image;
  private BufferedImage [][] sprites;
    private int width, height;
  
  public SpriteSheet(String pSpriteSheetPath, int pDirections, int pMoves){
//    int pTile.TILEWIDTH, int pTile.TILEHEIGHT
    sprites = new BufferedImage[pDirections][pMoves];

      //Create Sprites:
    try {
        image = ImageIO.read(new File(pSpriteSheetPath));
        width = image.getWidth() / pDirections;
        height = image.getHeight() / pMoves;
    } catch(IOException e) {
    e.printStackTrace();
        System.out.println("LADEFEHLER SPRITESHEET");
        return;
    } // end of try
  for ( int direction = 0;direction<pDirections ;direction++ ) {
    for ( int move=0;move<pMoves ;move++ ) {
        sprites[direction][move] = image.getSubimage(move * Tile.TILEWIDTH, direction * Tile.TILEHEIGHT, Tile.TILEWIDTH, Tile.TILEHEIGHT);
    } // end of for
  } // end of for
  }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public BufferedImage getSpriteElement(int pDirection, int pMove) {
   return sprites[pDirection][pMove];
  }
}

