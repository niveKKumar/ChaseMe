import java.awt.*;
import java.awt.geom.Point2D;

public class Runner extends Mover {
    private PathFinder pathfinder;
    private int border;
    private int delay = 1;
    private int speed;
    int collisionduration = 0;

    public Runner(GUI pGUI, int xPos, int yPos, int pWidth, int pHeight, SpriteSheet pSpriteSheet, Map[] pMap) {
        super(pGUI, xPos, yPos, pWidth, pHeight, pSpriteSheet, pMap);
        pathfinder = new PathFinder(map,this,gui);
    }

    public void enemystraightrun(int pBorder, int pSpeed, int x, int y) {   //Gegner der nur gerade aus läuft (Delay = 1 - Kein Delay)
        border = pBorder;
        Point richtung = new Point (x,y);
        //Delay:
        speed = pSpeed;
        this.setSpeed(speed);
        if (!collisionCheck()) {
            delay++;
            if (delay < speed) {
                delay++;
            } else {
                this.setMove(richtung);
                delay = 0;
            } // end of if-else

            }else{
            collisionduration++;
            if (collisionduration > 5){
                System.out.println("ich laufe gegen wand");
            }
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
