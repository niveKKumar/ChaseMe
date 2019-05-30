import java.awt.*;
import java.util.LinkedList;


public class Character extends Mover {
    public SpeechBubble speechBubble;
    protected int steps;
    protected boolean speaking = false;
    private int checkpointsActivated = 0;

    public Character(GamePanel pGP, int pXpos, int pYpos, MapBase[] pMap) {
        super(pGP, pXpos, pYpos, pMap);
        sprites = new SpriteSheet("Content/Graphics/player/playersheet.png", 4, 3);
        MOVER_WIDTH = sprites.getWidth();
        MOVER_HEIGHT = sprites.getHeight();
        img = sprites.getSpriteElement(0, 1);
        if (gamePanel.getCamera() != null) {
            gamePanel.getCamera().centerOnObject(this);
        }
    }

    @Override
    public void setMove(Point pMove) {
        if (!speaking) {
            super.setMove(pMove);
            if (!collisionCheck()) {
                steps++;
            }
        }
        gamePanel.getCamera().centerOnObject(this);
    }

    public boolean isOnThisTile(int xPos, int yPos, int cpAmount) {
        Point cord = new Point(xPos, yPos);
        LinkedList<Point> tileList = moverIsOnTileID();

        for (int i = 0; i < tileList.size(); i++) {
            if (tileList.get(i).getLocation().equals(cord)) {
                checkpointsActivated++;
            }
//           //System.out.println("X: " + tileList.get(i).getLocation().getX() +" must be "+ xPos + " is " + x + "\n Y:" + tileList.get(i).getLocation().getY()+" must be "+ yPos + "is " + y);
        }
        if (checkpointsActivated >= cpAmount) {
            checkpointsActivated = 0;
            return true;
        } else {
            checkpointsActivated = 0;
            return false;
        }
    }

    public boolean isInThisArea(Point firstCorner, Point secondCorner, int cpAmount) {
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
                if (isOnThisTile(j, i, cpAmount)) {
                    //System.out.println("is in area");
                    return true;
                }
            }
        }
        return false;
    }

    public void saySomething(String text, boolean clear, int speakingSpeed) {
        //temp:
        speakingSpeed = 25;

        if (speechBubble == null) {
            speechBubble = new SpeechBubble(this);
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

    @Override
    public void draw(Graphics2D g2d) {
        super.draw(g2d);
        if (speechBubble != null) {
            speechBubble.renderSpeechBubble(g2d);
        }

    }

    public void setSpeed(int pSpeed) {
        speed = pSpeed;
    }

    // Für End-Statistik: Wie viel ist "km" ist man gelaufen
    public int getSteps() {
        return steps;
    }

    public boolean isSpeaking() {
        return speaking;
    }

    public void setSpeaking(boolean speaking) {
        this.speaking = speaking;
    }

    public static class SpeechBubble {

        private Character character;
        private String text;
        private boolean visible;

        public SpeechBubble(Character character) {
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

