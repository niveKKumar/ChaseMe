import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform ;

public class Runner extends Mover {
    private PathFinder pathfinder;
    private int border;
    private int delay;
    private int speed;

    public Runner(GUI pGUI, int tileXPos, int tileYPos, int pWidth, int pHeight, SpriteSheet pSpriteSheet, Map[] pMap) {
        super(pGUI,tileXPos, tileYPos, pWidth, pHeight, pSpriteSheet, pMap);
//        int pXpos = tileXPos*pMap.tileWidth;
//        int pYpos = tileYPos*pMap.tileHeight;
        pathfinder = new PathFinder(map,this,gui);
    }

    public void enemystraightrun(int pBorder,/*int pDelay, */int pSpeed, int x, int y) {   //Gegner der nur gerade aus läuft (Delay = 1 - Kein Delay)
        border = pBorder;
        Point richtung = new Point (x,y);

        //Delay:
        /*delay = pDelay;*/
        speed = pSpeed;
        this.setSpeed(speed);
        if (!this.collisionCheck()) {
            delay++;
            if (delay < speed) {
                delay++;
            } else {
                richtung = new Point(richtung); // Richtung (x,y)
                this.setMove(richtung);
                delay = 0;
            } // end of if-else
            if (this.getLocation().getX() <= border) {
                System.out.println("Gegner an Grenze dann remove");
            } // end of if
        }
    }


    public void movetotarget(Mover character) {                     //Dynamischer Gegner
        Tile start = map[0].mapTiles[(int)((this.getLocation().getX() + Tile.TILEWIDTH/2) /  Tile.TILEWIDTH)][(int)((this.getLocation().getY() + Tile.TILEHEIGHT) / Tile.TILEHEIGHT)];
        Tile target = map[0].mapTiles[(int)((character.getLocation().getX() + Tile.TILEWIDTH/2) /  Tile.TILEWIDTH)][(int)((character.getLocation().getY() + Tile.TILEHEIGHT) / Tile.TILEHEIGHT)];
        pathfinder.searchPath(start,target);
        Point2D from = pathfinder.getNextStep();
//        System.out.println(from);
        Point2D to = pathfinder.getNextStep();
//        System.out.println(to);
        if(to!=null ){
            from =pathfinder.getNextStep();
//            System.out.println(from);
            to = pathfinder.getNextStep();
//            System.out.println(to);
            pathfinder.searchPath(start,target);

            try {
                this.setPathMove(from, to);
            } catch (Exception e) {
                System.out.println("Game Over");
            }
        }else {
            System.out.println("Game Over");
        } // end of if-else
    }


//    @Override
//    public void setMove(Point pMove) {
//        int oldXPos = xPos;
//        int oldYPos = yPos;
//
//        xPos += pMove.getX() * speed;
//        yPos += pMove.getY() * speed;
//        moveSeqSleep++;
//        if (moveSeqSleep == 5) {
//            if (moveSeq < 2) {
//                moveSeq++;
//            } else {
////        System.out.println("else moveSeqSleep");
//                moveSeq = 0;
//            } // end of if-else
//            moveSeqSleep = 0;
//        } // end of if
//        setCurrentImage((int) pMove.getX(), (int) pMove.getY(), moveSeq);
////    System.out.println(moveSeqSleep);
////    System.out.println(moveSeq);
//        if (collisionCheck()) {
//            xPos = oldXPos;
//            yPos = oldYPos;
//        }
//    }

//    @Override
//    public void setPathMove(Point2D from, Point2D to) {
//        getAngle(from, to);
//        moveSeqSleep++;
//        if (moveSeqSleep == 5) {
//            if (moveSeq < 2) {
//                moveSeq++;
//            } else {
//                moveSeq = 0;
//            } // end of if-else
//            moveSeqSleep = 0;
//        } // end of if
//        setSprite();
//        this.xPos = (int) to.getX() - this.width / 2;
//        this.yPos = (int) to.getY() - this.height / 2;
//    }

//    @Override
//    public void draw(Graphics2D g2d) {
//            AffineTransform at = new AffineTransform();
//            at.translate(this.xPos, this.yPos);
//            at.rotate(angle, img.getWidth(null)/2, img.getHeight(null)/2);
//            g2d.drawImage(img,at,null);
//    }
}
