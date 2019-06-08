import java.awt.*;
import java.util.LinkedList;


public class Character extends Mover {
    private int checkpointsActivated = 0;

    public Character(GamePanel pGP, int pXpos, int pYpos, MapBase[] pMap) {
        super(pGP, pXpos, pYpos, new SpriteSheet("Content/Graphics/player/playersheet.png", 4, 3), pMap);
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
    
}

