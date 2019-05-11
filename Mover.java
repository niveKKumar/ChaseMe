import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.LinkedList;

public class Mover extends Pointer {
    public static int MOVER_WIDTH, MOVER_HEIGHT;
    protected Image img;
    protected SpriteSheet sprites;
    protected int moveSeq = 1;
    protected int moveSeqSleep;
    protected MapBase[] map;
    protected double angleCheck;
    protected double angle ;
    protected GamePanel gamePanel;
    public Point[] cPoints = new Point[4];
    private JTextArea moverDebugPane = new JTextArea("Mover Debugging");

    public Mover(GamePanel gp, int pXpos, int pYpos, MapBase[] pMap) { //Image Initialisierung nachher...
        super(gp, pXpos, pYpos);
        //System.out.println("Mover :" + pXpos + " || " + pYpos);
        gamePanel = gp;
        map = pMap;
        moverDebugPane.setSize(400, 150);
        GUI.addToDebugPane(moverDebugPane);

    }

    @Override
    public void setMove(Point pMove){
        double oldXPos = xPos;
        double oldYPos = yPos;

        super.setMove(pMove);

        moveSeqSleep++;
        if (moveSeqSleep == 5) {
            if (moveSeq < 2) {
                moveSeq++;
            } else {
                //       //System.out.println("else moveSeqSleep");
                moveSeq = 0;
            } // end of if-else
            moveSeqSleep = 0;
        } // end of if
        setCurrentImage((int) pMove.getX(), (int) pMove.getY(), moveSeq);
        if (collisionCheck()) {
            xPos = oldXPos;
            yPos = oldYPos;
        }
        moverDebugPane.setText("XPos: " + getLocation().getX() + "\n YPos: " + getLocation().getY());
    }

    public void setPathMove(Point2D from, Point2D to) {
        getAngle(from, to);
        moveSeqSleep++;
        if (moveSeqSleep == 5) {
            if (moveSeq < 2) {
                moveSeq++;
            } else {
                moveSeq = 0;
            } // end of if-else
            moveSeqSleep = 0;
        } // end of if
        setSprite();
        this.xPos = (int) to.getX() - Character.MOVER_WIDTH / 2;
        this.yPos = (int) to.getY() - Character.MOVER_HEIGHT / 2;
    }

    public void draw(Graphics2D g2d) {
        AffineTransform oldTransform = g2d.getTransform();

        AffineTransform at = new AffineTransform();
        at.translate((xPos - gamePanel.getCamera().getXOffset()), (yPos - gamePanel.getCamera().getYOffset()));
        at.rotate(angle, img.getWidth(null) / 2, img.getHeight(null) / 2);
        g2d.drawImage(img, at, null);

        g2d.setTransform(oldTransform);
    }

    public void setCurrentImage(int pXMove, int pYMove, int pMoveSeq) {
        moveSeq = pMoveSeq;
        if (pXMove == 0 || pYMove == 0) img = sprites.getSpriteElement(0,moveSeq);  //nix = gerade aus
        if (pXMove == -1) img = sprites.getSpriteElement(1,moveSeq);  //links
        if (pXMove == 1) img = sprites.getSpriteElement(2,moveSeq);   //rechts
        if (pYMove == -1) img = sprites.getSpriteElement(3,moveSeq);  //oben
        if (pYMove == 1) img = sprites.getSpriteElement(0,moveSeq);   //unten
    }

    // FIXME: 07.05.2019 MoverOnMapTile und collsiionCheck zusammen integrieren und vereinfachen: MoverOnMapTIle gibt TileIDs zurück
    //                                                                                            CollisionCheck guckt ob diese IDs geblockt sind

    //    protected boolean collisionCheck(){
//        for (int i = 0;i < map.length ;i++ ) {
//
//            for (int j = 0; j < checkPoint.length /*(4 cPoints)*/; j++) {
//                map[i].checkActive(checkPoint[j]);
////               //System.out.println("Check checkpoint" + j);
//                if (map[i].isActive()) {
//                    Tile temp = map[i].mapTiles[(int) (checkPoint[j].getX() - map[i].chapterXOffset) / Tile.TILEWIDTH][(int) (checkPoint[j].getY() - map[i].chapterYOffset) / Tile.TILEHEIGHT];
//                    if (temp.isBlocked()) {
//                       //System.out.println("block on cPoints: " + j);
//                        return true;
//                    }
//                }
//            }
//
//        }
//        return false;
//    }
    public boolean collisionCheck() {
        LinkedList<Point> tileList = moverOnTiles();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < tileList.size(); j++) {
                Point moverCPpos = tileList.get(j);
                if (map[i].isActiveInPosition(moverCPpos) && map[i].mapTiles[(int) (moverCPpos.getX() - map[i].chapterXOffset) / Tile.TILEWIDTH][(int) (moverCPpos.getY() - map[i].chapterYOffset) / Tile.TILEHEIGHT].isBlocked()) {
                    return true;
                }
            }
        }
        return false;
    }

    public LinkedList<Point> moverOnTiles() {
        LinkedList<Point> cords = new LinkedList<>();
        int hitBoxCenter = 10;
        Point[] checkPoint = new Point[4];
        /*Up*/
        checkPoint[0] = new Point((int) xPos + MOVER_WIDTH / 2, (int) yPos + hitBoxCenter);
        /*Down*/
        checkPoint[1] = new Point((int) xPos + MOVER_WIDTH / 2, (int) yPos + MOVER_HEIGHT - hitBoxCenter);
        /*Left*/
        checkPoint[2] = new Point((int) xPos + hitBoxCenter, (int) yPos + Character.MOVER_HEIGHT / 2);
        /*Right*/
        checkPoint[3] = new Point((int) xPos + MOVER_WIDTH - hitBoxCenter, (int) yPos + MOVER_HEIGHT / 2);
        for (int j = 0; j < checkPoint.length; j++) {
            cPoints[j] = checkPoint[j];
            cords.add(checkPoint[j]);
        }
        return cords;
    }

    public LinkedList<Point> moverIsOnTileID() {
        LinkedList<Point> cords = moverOnTiles();
        LinkedList<Point> tileID = new LinkedList<>();
        moverDebugPane.setText("");
        for (int j = 0; j < map.length; j++) {
//           //System.out.println("Tile IDs for Map "+j);
            for (int i = 0; i < cords.size(); i++) {
//                moverDebugPane.append("Tile IDs for Map "+j+" : \n"+ "Cords "+i+" "+ cords.get(i));
                tileID.add(new Point(((int) cords.get(i).getX() - map[j].chapterXOffset) / Tile.TILEWIDTH, ((int) (cords.get(i).getY() - map[j].chapterYOffset)) / Tile.TILEHEIGHT));
//               //System.out.println("Active TileIDs "+i+" "+ tileID.get(i));
            }
        }
        return tileID;
    }

    protected void getAngle(Point2D from, Point2D to) {
        angleCheck = Math.atan2(from.getY() - to.getY(), from.getX() - to.getX())-Math.PI;
        angleCheck = (int) Math.abs(Math.toDegrees(angleCheck));
        //   //System.out.println(angleCheck);
    }

    public void setSprite(){
        if (angleCheck >= 0 && angleCheck < 30) { // rechts
            img = sprites.getSpriteElement(2, moveSeq);
            angle = 0;
            return;
        }
        if (angleCheck >= 30 && angleCheck < 60) { //oben rechts
            img = sprites.getSpriteElement(2, moveSeq);
            angle = Math.toRadians(angleCheck) - Math.PI/2;
            return;
        }
        if (angleCheck >= 60 && angleCheck < 120) { //oben
            img = sprites.getSpriteElement(3, moveSeq);
            angle = 0;
            return;
        }
        if (angleCheck >= 120 && angleCheck < 150) { // oben links
            img = sprites.getSpriteElement(1, moveSeq);
            angle = Math.toRadians(angleCheck) - Math.PI/2;
            return;
        }
        if (angleCheck >= 150 && angleCheck < 210) { //links
            img = sprites.getSpriteElement(1, moveSeq);
            angle = 0;
            return;
        }
        if (angleCheck >= 210 && angleCheck < 240) { // unten links
            img = sprites.getSpriteElement(1, moveSeq);
            angle = Math.toRadians(angleCheck) + Math.PI/2 ;
            return;
        }
        if (angleCheck >= 240 && angleCheck < 300) { //unten
            img = sprites.getSpriteElement(0, moveSeq);
            angle = 0;
            return;
        }
        if (angleCheck >= 300 && angleCheck < 330) { // unten rechts
            img = sprites.getSpriteElement(2, moveSeq);
            angle = Math.toRadians(angleCheck) - Math.PI/2;
            return;
        }
        if (angleCheck >= 330 && angleCheck < 360) { // rechts
            img = sprites.getSpriteElement(2, moveSeq);
            angle = 0;
            return;
        }
    }

    public void setSpritesheet(SpriteSheet sprites) {
        this.sprites = sprites;
    }

    public void setXPos(int xPos) {
        this.xPos = xPos;
    }

    public void setYPos(int yPos) {
        this.yPos = yPos;
    }

    public int getSpeed() {
        return speed;
    }
}
