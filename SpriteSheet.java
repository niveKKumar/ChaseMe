import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class SpriteSheet {
    private BufferedImage image;
    private BufferedImage[][] sprites;
    private int width, height;

    public SpriteSheet(String pSpriteSheetPath, int pDirections, int pMoves) {
//    int pTile.TILEWIDTH, int pTile.TILEHEIGHT
        sprites = new BufferedImage[pDirections][pMoves];

        //Create Sprites:
        try {
            image = ImageIO.read(new File(pSpriteSheetPath));
            width = image.getWidth() / pMoves;
            height = image.getHeight() / pDirections;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ungültiges Spritesheet auf dem Pfad : " + pSpriteSheetPath, "", JOptionPane.WARNING_MESSAGE);
        } // end of try
        for (int direction = 0; direction < pDirections; direction++) {
            for (int move = 0; move < pMoves; move++) {
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

