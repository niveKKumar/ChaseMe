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
        width = image.getWidth() / pMoves;
//       //System.out.println("Width:"+ width);
        height = image.getHeight() / pDirections;
//       //System.out.println("Width:"+ height);
    } catch(IOException e) {
        System.out.println("LADEFEHLER SPRITESHEET");
        return;
    } // end of try
  for ( int direction = 0;direction<pDirections ;direction++ ) {
    for ( int move=0;move<pMoves ;move++ ) {
        sprites[direction][move] = image.getSubimage(move * width, direction * height, width, height);
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

