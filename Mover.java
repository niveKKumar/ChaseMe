import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.LinkedList;

public class Mover extends Pointer {
    /**
     * Spieler Objekt mit Bild
     * kann sprechen
     */
    public Character.SpeechBubble speechBubble;
    protected int steps;
    protected boolean speaking = false;
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

    public Mover(GamePanel gp, int pXpos, int pYpos, SpriteSheet pSpriteSheet, MapBase[] pMap) { //Image Initialisierung nachher...
        super(gp, pXpos, pYpos);
        sprites = pSpriteSheet;
        MOVER_WIDTH = sprites.getWidth();
        MOVER_HEIGHT = sprites.getHeight();
        img = sprites.getSpriteElement(0, 1);
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
        moverDebugPane.setText("XPos: " + getLocation().getX() + "\n = Tile: " + getLocation().getX() / Tile.TILEWIDTH + "\n YPos: " + getLocation().getY() + " = \nTile: " + getLocation().getY() / Tile.TILEHEIGHT);
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
        if (pXMove == 0 || pYMove == 0) img = sprites.getSpriteElement(0,moveSeq);  //nix = gerade aus
        if (pXMove == -1) img = sprites.getSpriteElement(1,moveSeq);  //links
        if (pXMove == 1) img = sprites.getSpriteElement(2,moveSeq);   //rechts
        if (pYMove == -1) img = sprites.getSpriteElement(3,moveSeq);  //oben
        if (pYMove == 1) img = sprites.getSpriteElement(0,moveSeq);   //unten
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
    // FIXME: 07.05.2019 MoverOnMapTile und collsiionCheck zusammen integrieren und vereinfachen: MoverOnMapTIle gibt TileIDs zurück
    //                                                                                            CollisionCheck guckt ob diese IDs geblockt sind

    public boolean collisionCheck() {
        LinkedList<Point> tileList = moverOnTiles();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < tileList.size(); j++) {
                Point moverCPpos = tileList.get(j);
                if (map[i] != null && map[i].isActiveInPosition(moverCPpos) && map[i].mapTiles[(int) (moverCPpos.getX() - map[i].chapterXOffset) / Tile.TILEWIDTH][(int) (moverCPpos.getY() - map[i].chapterYOffset) / Tile.TILEHEIGHT].isBlocked()) {
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
        for (int j = 0; j < map.length; j++) {
//           //System.out.println("Tile IDs for Map "+j);
            if (map[j] != null) {
                for (int i = 0; i < cords.size(); i++) {
//                moverDebugPane.append("Tile IDs for Map "+j+" : \n"+ "Cords "+i+" "+ cords.get(i));
                    tileID.add(new Point(((int) cords.get(i).getX() - map[j].chapterXOffset) / Tile.TILEWIDTH, ((int) (cords.get(i).getY() - map[j].chapterYOffset)) / Tile.TILEHEIGHT));
//               //System.out.println("Active TileIDs "+i+" "+ tileID.get(i));
                }
            }
        }
        return tileID;
    }

    public Point getTileLocation() {
        return new Point((int) getLocation().getX() / Tile.TILEWIDTH, (int) getLocation().getY() / Tile.TILEHEIGHT);
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

    public void setDirection(int direction) {
        //1: Links
        //2: Rechts
        //3: Oben
        //4: Unten
        img = sprites.getSpriteElement(direction, 1);
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
                int width = getMaxWidth(showText, metric); // Gibt GesamtBreite der Textfolge an
                int height = showText.length * metric.getHeight(); //Gibt den Standard-Zeilenabstand (Abstand zwischen Grundlinie und Grundlinie) in Pixeln zurück
                int rounded = height / 8;
                int gap = metric.getMaxAdvance() / 4; //Liefert die Breite des breitesten Zeichens, –1, wenn unbekannt.

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
