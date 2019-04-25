import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class Mover extends Pointer {
    public static int MOVER_WIDTH, MOVER_HEIGHT;
    protected Image img;
    protected SpriteSheet sprites;
    protected int moveSeq = 1;
    protected int moveSeqSleep;
    protected MapBase[] map;
    protected double angleCheck;
    protected double angle ;
    protected Point[] checkPoint = new Point[4];
    protected GamePanel gamePanel;

    public Mover(GamePanel gp, int pXpos, int pYpos, MapBase[] pMap) { //Image Initialisierung nachher...
        super(gp, pXpos, pYpos);
        System.out.println("Mover :" + pXpos + " || " + pYpos);
        gamePanel = gp;
        map = pMap;
        for (int i = 0; i < checkPoint.length; i++) {
            checkPoint[i] = new Point();
        }
    }
    @Override
    public void setMove(Point pMove){
        int oldXPos = xPos;
        int oldYPos = yPos;

        super.setMove(pMove);

        moveSeqSleep++;
        if (moveSeqSleep == 5) {
            if (moveSeq < 2) {
                moveSeq++;
            } else {
                //        System.out.println("else moveSeqSleep");
                moveSeq = 0;
            } // end of if-else
            moveSeqSleep = 0;
        } // end of if
        setCurrentImage((int) pMove.getX(), (int) pMove.getY(), moveSeq);
        if (collisionCheck()) {
            xPos = oldXPos;
            yPos = oldYPos;
        }
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
        AffineTransform at = new AffineTransform();
        at.translate((xPos - gamePanel.getCamera().getXOffset()), (yPos - gamePanel.getCamera().getYOffset()));
        at.rotate(angle, img.getWidth(null) / 2, img.getHeight(null) / 2);
        g2d.drawImage(img, at, null);

    }

    public Point getLocation(){
        return new Point(xPos,yPos);
    }

    public void setCurrentImage(int pXMove, int pYMove, int pMoveSeq) {
        moveSeq = pMoveSeq;
        if (pXMove == 0 || pYMove == 0) img = sprites.getSpriteElement(0,moveSeq);  //nix = gerade aus
        if (pXMove == -1) img = sprites.getSpriteElement(1,moveSeq);  //links
        if (pXMove == 1) img = sprites.getSpriteElement(2,moveSeq);   //rechts
        if (pYMove == -1) img = sprites.getSpriteElement(3,moveSeq);  //oben
        if (pYMove == 1) img = sprites.getSpriteElement(0,moveSeq);   //unten
    }

    protected boolean collisionCheck(){
        moverOnMapTile();
        int hitBoxCenter = 3;
        /*Up*/
        checkPoint[0].setLocation(xPos + Character.MOVER_WIDTH / 2, yPos + hitBoxCenter);
        /*Down*/
        checkPoint[1].setLocation(xPos + Character.MOVER_WIDTH / 2, yPos + Character.MOVER_HEIGHT - hitBoxCenter);
        /*Left*/
        checkPoint[2].setLocation(xPos, yPos + Character.MOVER_HEIGHT / 2 + hitBoxCenter);
        /*Right*/
        checkPoint[3].setLocation(xPos + Character.MOVER_WIDTH - hitBoxCenter, yPos + Character.MOVER_HEIGHT / 2);
        for (int i = 0;i < map.length ;i++ ) {
            Tile[][] temp = new Tile[map.length][checkPoint.length];
            for (int j = 0; j < checkPoint.length /*(4 CP)*/; j++) {
                map[i].checkActive(checkPoint[j]);
                System.out.println("Check checkpoint" + j);
                if (map[i].isActive()) {
                    temp[i][j] = map[i].mapTiles[(int) (checkPoint[j].getX() - map[i].chapterXOffset) / Tile.TILEWIDTH][(int) (checkPoint[j].getY() - map[i].chapterYOffset) / Tile.TILEHEIGHT];
                    if (temp[i][j].isBlocked()) {
                        System.out.println("block on CP: " + j);
                        return true;
                    }
                }
            }
        }
        System.out.println("no block");
        return false;
    }

    public Point moverOnMapTile(){
        Point temp = new Point();
        for (int i = 0; i < map.length ; i++) {
                for (int y = 0; y < map[i].getMapSizeY() ; y++) {
                    if (this.getLocation().y + Character.MOVER_HEIGHT / 2 >= Tile.TILEHEIGHT * y && this.getLocation().y + Character.MOVER_HEIGHT / 2 <= Tile.TILEHEIGHT * (y + 1)) {
                    temp.y = y;
                }
            }
            for (int x = 0; x < map[i].getMapSizeX(); x++) {
//                int right = this.getLocation().x + width;
//                System.out.println(right);
                if (this.getLocation().x + Character.MOVER_WIDTH / 2 >= Tile.TILEWIDTH * x && this.getLocation().x + Character.MOVER_WIDTH / 2 <= Tile.TILEWIDTH * (x + 1)) {
                    temp.x = x + 1;// weil bei 0
                }
            }
//            System.out.println("Map:"+i+"Y:"+temp.y+"X:"+temp.x);
        }

        return temp;
    }

    protected void getAngle(Point2D from, Point2D to) {
        angleCheck = Math.atan2(from.getY() - to.getY(), from.getX() - to.getX())-Math.PI;
        angleCheck = (int) Math.abs(Math.toDegrees(angleCheck));
        //    System.out.println(angleCheck);
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
