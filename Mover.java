import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;


public class Mover{
    protected Image img;
    protected int xPos,yPos;
    protected static int MOVER_WIDTH, MOVER_HEIGHT;
    protected SpriteSheet sprites;
    protected int moveSeq = 1;
    protected int moveSeqSleep;
    protected int speed= 3;
    protected int steps;
    protected Map[] map;
    protected double angleCheck;
    protected double angle ;
    protected boolean speaking = false;
    public JTextArea speechBubble;
//    protected boolean blocked;

    protected Point[] checkPoint = new Point[4];

    protected GUI gui;

    public Mover(GUI pGUI,int pXpos,int pYpos,int pWidth,int pHeight,SpriteSheet pSpriteSheet, Map[] pMap){
        gui = pGUI;
        sprites = pSpriteSheet;
        xPos = pXpos;
        yPos = pYpos;
        map = pMap;
        img = sprites.getSpriteElement(0,1);
        MOVER_WIDTH = pWidth;
        MOVER_HEIGHT = pHeight;
        gui.south.add(speechBubble = new JTextArea("Mover:"));
        speechBubble.setVisible(false);
        for (int i = 0; i < checkPoint.length; i++) {
            checkPoint[i] = new Point();
        }
    }

    public void setMove(Point pMove){
        if (!speaking) {
            int oldXPos = xPos;
            int oldYPos = yPos;

            xPos += pMove.getX() * speed;
            yPos += pMove.getY() * speed;
            steps++;

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
                steps--;
                System.out.println("BLock");
            }
        }
    }
    public void setPathMove(Point2D from ,Point2D to ) {
        if (!speaking) {
            gui.getCamera().centerOnObject(this);
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
            this.xPos = (int) to.getX() - MOVER_WIDTH / 2;
            this.yPos = (int) to.getY() - MOVER_HEIGHT / 2;
        }
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
        moverOnMapTile();

        boolean blocked = false;
        int hitBoxCenter = 3;
        /*Up*/
        checkPoint[0].setLocation(xPos + MOVER_WIDTH / 2, yPos + hitBoxCenter);
        /*Down*/
        checkPoint[1].setLocation(xPos + MOVER_WIDTH / 2, yPos + MOVER_HEIGHT - hitBoxCenter);
        /*Left*/
        checkPoint[2].setLocation(xPos, yPos + MOVER_HEIGHT / 2 + hitBoxCenter);
        /*Right*/
        checkPoint[3].setLocation(xPos + MOVER_WIDTH - hitBoxCenter, yPos + MOVER_HEIGHT / 2);
        for (int i = 0;i < map.length ;i++ ) {
            Tile[][] temp = new Tile[map.length][checkPoint.length];
            for (int j = 0; j < checkPoint.length; j++) {
                if (map[i].isActive() && checkPoint[j].getX() - map[i].chapterXOffset >= 0 && checkPoint[j].getY() - map[i].chapterYOffset >= 0) {
                    temp[i][j] = map[i].mapTiles[(int) (checkPoint[j].getX() - map[i].chapterXOffset) / Tile.TILEWIDTH][((int) checkPoint[j].getY() - map[i].chapterYOffset) / Tile.TILEWIDTH];
                } else {
                    System.out.println(i + " ist nicht aktiv");
                    map[i].setActive(false);
                }
                if (map[i].isActive() && temp[i][j].isBlocked()) {
                    blocked = true;
                }
            }
        }
        return blocked;
    }
    public Point moverOnMapTile(){
        Point temp = new Point();
        for (int i = 0; i < map.length ; i++) {
                for (int y = 0; y < map[i].getMapSizeY() ; y++) {
                    if (this.getLocation().y + MOVER_HEIGHT / 2 >= Tile.TILEHEIGHT * y && this.getLocation().y + MOVER_HEIGHT / 2 <= Tile.TILEHEIGHT * (y + 1)) {
                    temp.y = y;
                }
            }
            for (int x = 0; x < map[i].getMapSizeX(); x++) {
//                int right = this.getLocation().x + width;
//                System.out.println(right);
                if (this.getLocation().x + MOVER_WIDTH / 2 >= Tile.TILEWIDTH * x && this.getLocation().x + MOVER_WIDTH / 2 <= Tile.TILEWIDTH * (x + 1)) {
                    temp.x = x + 1;// weil bei 0
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

    public boolean isOnThisTile(int xPos, int yPos) {
        boolean x = false;
        boolean y = false;
        if(moverOnMapTile().getLocation().getX() == xPos){
            x = true;
            }

        if(moverOnMapTile().getLocation().getY() == yPos){
            y = true;
        }
        return x && y;
    }

    public boolean isInThisArea(Point firstCorner, Point secondCorner) {
        if (firstCorner.getX() > secondCorner.getX()) {
            int swap = (int) firstCorner.getX();
            firstCorner.setLocation(secondCorner.getX(), firstCorner.getY());
            secondCorner.setLocation(swap, secondCorner.getY());
        }
        if (firstCorner.getY() > secondCorner.getY()) {
            int swap = (int) firstCorner.getY();
            firstCorner.setLocation(firstCorner.getX(), secondCorner.getY());
            secondCorner.setLocation(secondCorner.getX(), swap);
        }

        for (int i = (int) firstCorner.getY(); i < (int) secondCorner.getY(); i++) {
            for (int j = (int) firstCorner.getX(); j < (int) secondCorner.getX(); j++) {
                if (isOnThisTile(j, i)) {
                    System.out.println("is in area");
                    return true;
                }
            }
        }
        return false;
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

    public void saySomething(String text, boolean clear, int speakingSpeed) {
        if (clear){
            speechBubble.setText("");
        }
        speaking = true;
        speechBubble.setVisible(true);
        for(int i = 0; i < text.length(); i++){
            speechBubble.append(String.valueOf(text.charAt(i)));
            try{
                Thread.sleep(speakingSpeed);//Delay des Textes
            }catch(InterruptedException ex){
                Thread.currentThread().interrupt();
            }
        }
        speaking = false;
//        speechBubble.setVisible(false);

    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public void setyPos(int yPos) {
        this.yPos = yPos;
    }

    public int getSpeed() {
        return speed;
    }

    public int getSteps() {
        return steps / 64;
    }

    public boolean isSpeaking() {
        return speaking;
    }

    public void setSpeaking(boolean speaking) {
        setMove(new Point(0, 0));
        this.speaking = speaking;
    }
//    // TODO: 24.03.2019 Komplett neu bedenken!!!
//    public static class SpeechBubble extends JTextArea {
//        public SpeechBubble(String text){
//            super();
//            setPreferredSize(new Dimension(100,100));
//            System.out.println("Sprechblaase wurde erstellt");
//        }
//
//    }
}

