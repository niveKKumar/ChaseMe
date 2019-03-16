import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform ;



public class Mover{
    protected Image img;
    protected int xPos,yPos;
    protected int width,height;
    protected SpriteSheet sprites;
    protected int moveSeq = 1;
    protected int moveSeqSleep;
    protected int speed= 3;
    protected Map[] map;
    protected double angleCheck;
    protected double angle ;
//    protected boolean blocked;

    protected Point checkPointUp = new Point();
    protected Point checkPointDown = new Point();
    protected Point checkPointLeft = new Point();
    protected Point checkPointRight = new Point();
    protected GUI gui;

    public Mover(GUI pGUI,int pXpos,int pYpos,int pWidth,int pHeight,SpriteSheet pSpriteSheet, Map[] pMap){
        gui = pGUI;
        sprites = pSpriteSheet;
        xPos = pXpos;
        yPos = pYpos;
        map = pMap;
        img = sprites.getSpriteElement(0,1);
        width = pWidth;
        height = pHeight;
    }

    public void setMove(Point pMove){
        gui.getCamera().centerOnMover(this);
        int oldXPos = xPos;
        int oldYPos = yPos;

        xPos += pMove.getX()*speed;
        yPos += pMove.getY()*speed;
        moveSeqSleep++;
        if (moveSeqSleep == 5 ) {
            if (moveSeq < 2) {
                moveSeq++;
            }else {
                //        System.out.println("else moveSeqSleep");
                moveSeq =0;
            } // end of if-else
            moveSeqSleep =0;
        } // end of if
        setCurrentImage((int) pMove.getX(), (int) pMove.getY(),moveSeq);
        moverOnMapTile();

            if (collisionCheck()) {
                xPos = oldXPos ;
                yPos = oldYPos ;}


    }
    public void setPathMove(Point2D from ,Point2D to ) {
        gui.getCamera().centerOnMover(this);
        getAngle(from, to);
        moveSeqSleep++;
        if (moveSeqSleep == 5 ) {
            if (moveSeq < 2) {
                moveSeq++;
            }else {
                moveSeq =0;
            } // end of if-else
            moveSeqSleep =0;
        } // end of if
        setSprite();
        this.xPos = (int) to.getX() - this.width/2;
        this.yPos = (int) to.getY() -this.height/2;
    }
    public void draw(Graphics2D g2d){
        AffineTransform at = new AffineTransform();
        at.translate((xPos - gui.getCamera().getXOffset()), (yPos - gui.getCamera().getYOffset()));
        at.rotate(angle, img.getWidth(null)/2, img.getHeight(null)/2);
        g2d.drawImage(img,at,null);

    }
    public Point getLocation(){
        return new Point(xPos,yPos);
    }
    public void setCurrentImage(int pXMove,int pYMove, int pMoveSeq) {
        moveSeq = pMoveSeq;
        if (pXMove == 0 || pYMove == 0) img = sprites.getSpriteElement(0,moveSeq);  //nix = gerade aus
        if (pXMove == -1) img = sprites.getSpriteElement(1,moveSeq);  //links
        if (pXMove == 1) img = sprites.getSpriteElement(2,moveSeq);   //rechts
        if (pYMove == -1) img = sprites.getSpriteElement(3,moveSeq);  //oben
        if (pYMove == 1) img = sprites.getSpriteElement(0,moveSeq);   //unten
    }
    protected boolean collisionCheck(){
        boolean blocked = false;
        int hitBoxCenter = 3;
        checkPointUp.setLocation(xPos+width/2, yPos + hitBoxCenter );
        checkPointDown.setLocation(xPos+width/2, yPos+height - hitBoxCenter);
        checkPointLeft.setLocation(xPos , yPos + height/2 + hitBoxCenter);
        checkPointRight.setLocation(xPos+width - hitBoxCenter , yPos + height/2 );
        for (int i = 0;i < map.length ;i++ ) {
            Tile[] temp1 = new Tile[map.length];
            if (map[i].isActive()){temp1[i] = map[i].mapTiles [(int) checkPointUp.getX()/64] [ (int) checkPointUp.getY()/64];}else{continue;}
            Tile[] temp2 = new Tile[map.length];
            if (map[i].isActive()){temp2[i] = map[i].mapTiles [(int) checkPointDown.getX()/64] [ (int) checkPointDown.getY()/64];}else{continue;}
            Tile[] temp3 = new Tile[map.length];
            if (map[i].isActive()){temp3[i] = map[i].mapTiles [(int) checkPointLeft.getX()/64] [ (int) checkPointLeft.getY()/64];}else{continue;}
            Tile[] temp4 = new Tile[map.length];
            if (map[i].isActive()){temp4[i] = map[i].mapTiles [(int) checkPointRight.getX()/64] [ (int) checkPointRight.getY()/64];}else{continue;}
                if (temp1[i].isBlocked() || temp2[i].isBlocked() || temp3[i].isBlocked() || temp4[i].isBlocked()){
                    blocked = true;}

            }
        return blocked;
    }
    public Point moverOnMapTile(){
        Point temp = new Point();
        for (int i = 0; i < map.length ; i++) {
            for (int y = 0; y < map[i].getMapSizeY() ; y++) {
                if (this.getLocation().y + this.height / 2 > Map.tileHeight * y && this.getLocation().y + this.height / 2 < Map.tileHeight * (y + 1)) {
                    temp.y = y;
                }
            }
            for (int x = 1; x < map[i].getMapSizeX() ; x++) {
//                int right = this.getLocation().x + width;
//                System.out.println(right);
                if ( this.getLocation().x + this.width / 2 >= Map.tileWidth * x && this.getLocation().x + this.width/2 <= Map.tileWidth * (x+1) ) {
                    temp.x = x + 2 ;// weil bei 0
                }
            }
//            System.out.println("Map:"+i+"Y:"+temp.y+"X:"+temp.x);
            if (temp.x   > map[i].getMapSizeX() || temp.y  > map[i].getMapSizeY() ){
                map[i].setActive(false);
//                System.out.println("false");
            }else{
                map[i].setActive(true);
              }
        }

        return temp;
    }
    public void setSpeed(int pSpeed){
        speed = pSpeed;
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
}  