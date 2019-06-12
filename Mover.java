import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.LinkedList;

public class Mover extends Pointer {
    public static int MOVER_WIDTH, MOVER_HEIGHT;
    /**
     * Spieler Objekt mit Bild
     * kann sprechen
     */
    public Character.SpeechBubble speechBubble;
    public Point[] cPoints = new Point[4];
    protected boolean speaking = false;
    protected Image img;
    protected SpriteSheet sprites;
    protected int moveSeq = 1;
    protected int moveSeqSleep;
    protected MapBase[] map;
    protected double angleCheck;
    protected double angle;
    protected GamePanel gamePanel;

    public Mover(GamePanel gp, int pXpos, int pYpos, SpriteSheet pSpriteSheet, MapBase[] pMap) { //Image Initialisierung nachher...
        super(gp, pXpos, pYpos);
        sprites = pSpriteSheet;
        MOVER_WIDTH = sprites.getWidth();
        MOVER_HEIGHT = sprites.getHeight();
        img = sprites.getSpriteElement(0, 1);
        gamePanel = gp;
        map = pMap;
    }

    @Override
    public void setMove(int xMove, int yMove) {
        int oldXPos = (int) xPos;
        int oldYPos = (int) yPos;
        super.setMove(xMove, yMove);
        moveSeqSleep++;
        if (moveSeqSleep == 5) {
            if (moveSeq < 2) {
                moveSeq++;
            } else {
                moveSeq = 0;
            } // end of if-else
            moveSeqSleep = 0;
        } // end of if
        setCurrentImage(xMove, yMove, moveSeq);
        if (collisionCheck()) {
            setLocation(oldXPos, oldYPos);
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
        AffineTransform oldTransform = g2d.getTransform();

        AffineTransform at = new AffineTransform();
        at.translate((xPos - gamePanel.getCamera().getXOffset()), (yPos - gamePanel.getCamera().getYOffset()));
        at.rotate(angle, img.getWidth(null) / 2, img.getHeight(null) / 2);
        g2d.drawImage(img, at, null);

        g2d.setTransform(oldTransform);
        if (speechBubble != null) {
            speechBubble.renderSpeechBubble(g2d);
        }
    }

    public void setCurrentImage(int pXMove, int pYMove, int pMoveSeq) {
        moveSeq = pMoveSeq;
        if (pXMove == 0 || pYMove == 0) img = sprites.getSpriteElement(0, moveSeq);  //nix = gerade aus
        if (pXMove == -1) img = sprites.getSpriteElement(1, moveSeq);  //links
        if (pXMove == 1) img = sprites.getSpriteElement(2, moveSeq);   //rechts
        if (pYMove == -1) img = sprites.getSpriteElement(3, moveSeq);  //oben
        if (pYMove == 1) img = sprites.getSpriteElement(0, moveSeq);   //unten
    }

    public void saySomething(String text, boolean clear, int speakingSpeed) {
        //temp:
        speakingSpeed = 25;

        if (speechBubble == null) {
            speechBubble = new Character.SpeechBubble(this);
        }

        if (clear) {
            speechBubble.setText("");
        }
        speechBubble.setVisible(true);
        speaking = true;
        for (int i = 0; i < text.length(); i++) {
            speechBubble.append(String.valueOf(text.charAt(i)));
            try {
                Thread.sleep(speakingSpeed);//Delay des Textes
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        speaking = false;
    }
    public boolean collisionCheck() {
        LinkedList<Point> tileList = moverIsOnTileID();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < tileList.size(); j++) {
                Point moverCPpos = tileList.get(j);
                if (map[i] != null && map[i].isActiveInTileID(moverCPpos) &&
                        map[i].mapTiles[(int) moverCPpos.getX()][(int) moverCPpos.getY()].isBlocked()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean dangerCheck() {
        LinkedList<Point> tileList = moverIsOnTileID();
        int activeCP = 0;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < tileList.size(); j++) {
                Point moverCPpos = tileList.get(j);
                if (map[i] != null && map[i].isActiveInTileID(moverCPpos) &&
                        map[i].mapTiles[(int) moverCPpos.getX()][(int) moverCPpos.getY()].isDanger()) {
                    activeCP++;
                }
            }
        }
        return activeCP >= 2;
    }

    public LinkedList<Point> checkPointCords() {
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
            cords.add(checkPoint[j]);
        }
        return cords;
    }

    public LinkedList<Point> moverIsOnTileID() {
        LinkedList<Point> cords = checkPointCords();
        LinkedList<Point> tileID = new LinkedList<>();
        for (int j = 0; j < map.length; j++) {
            if (map[j] != null) {
                for (int i = 0; i < cords.size(); i++) {
                    tileID.add(new Point(((int) cords.get(i).getX() - map[j].chapterXOffset) / Tile.TILEWIDTH, ((int) (cords.get(i).getY() - map[j].chapterYOffset)) / Tile.TILEHEIGHT));
                }
            }
        }
        return tileID;
    }

    public Point getTileLocation() {
        return new Point((int) (getLocation().getX()) / Tile.TILEWIDTH, (int) (getLocation().getY()) / Tile.TILEHEIGHT);
    }

    protected void getAngle(Point2D from, Point2D to) {
        angleCheck = Math.atan2(from.getY() - to.getY(), from.getX() - to.getX()) - Math.PI;
        angleCheck = (int) Math.abs(Math.toDegrees(angleCheck));
    }

    public void setSprite() {
        if (angleCheck >= 0 && angleCheck < 30) { // rechts
            img = sprites.getSpriteElement(2, moveSeq);
            angle = 0;
            return;
        }
        if (angleCheck >= 30 && angleCheck < 60) { //oben rechts
            img = sprites.getSpriteElement(2, moveSeq);
            angle = Math.toRadians(angleCheck) - Math.PI / 2;
            return;
        }
        if (angleCheck >= 60 && angleCheck < 120) { //oben
            img = sprites.getSpriteElement(3, moveSeq);
            angle = 0;
            return;
        }
        if (angleCheck >= 120 && angleCheck < 150) { // oben links
            img = sprites.getSpriteElement(1, moveSeq);
            angle = Math.toRadians(angleCheck) - Math.PI / 2;
            return;
        }
        if (angleCheck >= 150 && angleCheck < 210) { //links
            img = sprites.getSpriteElement(1, moveSeq);
            angle = 0;
            return;
        }
        if (angleCheck >= 210 && angleCheck < 240) { // unten links
            img = sprites.getSpriteElement(1, moveSeq);
            angle = Math.toRadians(angleCheck) + Math.PI / 2;
            return;
        }
        if (angleCheck >= 240 && angleCheck < 300) { //unten
            img = sprites.getSpriteElement(0, moveSeq);
            angle = 0;
            return;
        }
        if (angleCheck >= 300 && angleCheck < 330) { // unten rechts
            img = sprites.getSpriteElement(2, moveSeq);
            angle = Math.toRadians(angleCheck) - Math.PI / 2;
            return;
        }
        if (angleCheck >= 330 && angleCheck < 360) { // rechts
            img = sprites.getSpriteElement(2, moveSeq);
            angle = 0;
            return;
        }
    }

    public void setDirection(int direction) {
        //1: Links
        //2: Rechts
        //3: Oben
        //4: Unten
        img = sprites.getSpriteElement(direction, 1);
    }

    public void setImg(Image img) {
        this.img = img;
    }

    public double getSpeed() {
        return speed;
    }

    /**
     * set Methoden (ob Mover spricht) = sich nicht bewegen kann / Spritesheet = andere Textur
     */
    public void setSpritesheet(SpriteSheet sprites) {
        this.sprites = sprites;
    }

    public void setSpeaking(boolean speaking) {
        this.speaking = speaking;
    }

    public void setLocationAtTile(int xTileID, int yTileID) {
        setLocation((xTileID) * Tile.TILEWIDTH, (yTileID) * Tile.TILEHEIGHT);
    }

    public static class SpeechBubble {

        private Mover character;
        private String text;
        private boolean visible;

        public SpeechBubble(Mover character) {
            super();
            this.character = character;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
        }

        public void setText(String pText) {
            text = pText;
        }

        public void append(String pText) {
            setText(text + pText);
        }

        public void renderSpeechBubble(Graphics2D g2d) {
            if (visible) {
                g2d.setStroke(new BasicStroke(1.25f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
                int xPos = (int) (character.xPos - character.gamePanel.getCamera().getXOffset());
                int yPos = (int) (character.yPos - character.gamePanel.getCamera().getYOffset());

                FontMetrics metric = g2d.getFontMetrics();

                String[] showText;
                if (text.contains("\n")) {
                    showText = text.split("\n");
                } else {
                    showText = new String[1];
                    showText[0] = text;
                }

                int width = getMaxWidth(showText, metric);
                int height = showText.length * metric.getHeight();
                int rounded = height / 8;
                int gap = metric.getMaxAdvance() / 4;

                g2d.setColor(Color.white);
                g2d.fillRoundRect(xPos - width / 2 - gap / 2, yPos - height - gap / 2, width + gap, height + gap, rounded, rounded);
                g2d.setColor(Color.black);
                g2d.drawRoundRect(xPos - width / 2 - gap / 2, yPos - height - gap / 2, width + gap, height + gap, rounded, rounded);
                height -= metric.getHeight();
                for (int i = 0; i < showText.length; i++) {
                    g2d.drawString(showText[i], xPos - width / 2, yPos + i * g2d.getFontMetrics().getHeight() - height);
                }
            }
        }

        public Integer getMaxWidth(String[] text, FontMetrics metrics) {
            int maxWidth = 0;
            for (int i = 0; i < text.length; i++) {
                if (metrics.stringWidth(text[i]) > maxWidth) {
                    maxWidth = metrics.stringWidth(text[i]);
                }
            }
            return maxWidth;
        }
    }
}
