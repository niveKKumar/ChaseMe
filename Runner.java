import java.awt.*;
import java.util.LinkedList;
import java.util.Random;

public class Runner extends Mover {
    private double speed;
    int collisionduration = 0;
    private double delay = 1;
    boolean gerade, quer;
    int abstand;

    public Runner(GamePanel gp, int xPos, int yPos, MapBase[] pMap) {
        super(gp, xPos, yPos, new SpriteSheet("Content/Graphics/player/playersheet.png", 4, 3), pMap);
        Random rnd = new Random();
        // end of if-else
        gerade = rnd.nextInt(2) == 0;
        // end of if-else
        quer = rnd.nextInt(2) == 0;
        abstand = rnd.nextInt(20) + 20;
    }


    /**
     * Besonderheit: setMove aber Speed kann weniger als 1 sein = delay wird erhöht
     */
    public void setMoveWithSpeed(double pSpeed, int xMove, int yMove) {
        speed = pSpeed;
        Point richtung = new Point(xMove, yMove);
        double delaysteps = 1;
        //Delay:
        if (speed < 1) {
            delaysteps = speed;
            speed = 1;
        }

        if (!collisionCheck()) {
            if (delay < speed) {
                delay += delaysteps;
            } else {
                this.setMove(richtung);
                delay = 0;
            } // end of if-else
        } else {
            collisionduration++;
            if (collisionduration > 5) {
                System.out.println("ich laufe gegen wand und befinde mich auf Tile :" + getTileLocation());
            }
        }
    }

    /**
     * Gegner läuft gerade aus bis bestimmten Punkt (Border)
     */
    public void enemystraightrun(Point pBorder, double pSpeed, int x, int y) {   //Gegner der nur gerade aus läuft (Delay = 1 - Kein Delay)

        if (pBorder == null) {
            pBorder = new Point(map[0].getMapSizeX(), map[0].getMapSizeY());
        }
        if (!isTileABorder(pBorder)) {
            setMoveWithSpeed(pSpeed, x, y);
        }
    }

    public void movetotarget(double pSpeed, Mover character) {                     //Dynamischer Gegner
        // TODO: 07.06.2019 Code verbessern 
        double playerX = character.getLocation().getX() - gamePanel.getCamera().getXOffset();
        double playerY = character.getLocation().getY() - gamePanel.getCamera().getYOffset();
        int ePosX = (int) Math.round(xPos - gamePanel.getCamera().getXOffset());
        int ePosY = (int) Math.round(yPos - gamePanel.getCamera().getYOffset());
        int xMove = 0, yMove = 0;

        if (!quer) {
            if (gerade) {
                if (ePosX < playerX - abstand)
                    xMove = 1;
                else if (ePosX > playerX + abstand)
                    xMove = -1;
                if (xMove == 0)
                    gerade = false;
            } else {
                if (ePosY < playerY - abstand)
                    yMove = 1;
                else if (ePosY > playerY + abstand)
                    yMove = -1;
                if (yMove == 0)
                    gerade = true;
            } // end of if-else
        }
        if (quer) {
            if (ePosX < playerX - abstand)
                xMove = 1;
            else if (ePosX > playerX + abstand)
                xMove = -1;
            if (ePosY < playerY - abstand)
                yMove = 1;
            else if (ePosY > playerY + abstand)
                yMove = -1;
        } // end of if-else
        setMoveWithSpeed(pSpeed, xMove, yMove);
    }

    public boolean isTileABorder(Point pBorder) {
        LinkedList temp = moverIsOnTileID();
        for (int i = 0; i < temp.size(); i++) {
            if (temp.get(i).equals(pBorder)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setSpritesheet(SpriteSheet sprites) {
        super.setSpritesheet(sprites);
    }
}
