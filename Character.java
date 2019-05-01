import javax.swing.*;
import java.awt.*;


public class Character extends Mover {
    public JTextArea speechBubble;
    protected int steps;
    protected boolean speaking = false;

    public Character(GamePanel pGP, int pXpos, int pYpos, MapBase[] pMap) {
        super(pGP, pXpos, pYpos, pMap);
        sprites = new SpriteSheet("Content/Graphics/player/playersheet.png", 4, 3);
        MOVER_WIDTH = sprites.getWidth();
        MOVER_HEIGHT = sprites.getHeight();
        img = sprites.getSpriteElement(0, 1);
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

    public boolean isOnThisTile(int xPos, int yPos) {
        boolean x = false;
        boolean y = false;
        if (moverOnMapTile().getLocation().getX() == xPos) {
            x = true;
        }

        if (moverOnMapTile().getLocation().getY() == yPos) {
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

    public void saySomething(String text, boolean clear, int speakingSpeed) {
        if (speechBubble == null) {
            speechBubble = new JTextArea("Mover:");
        } else {
            speechBubble.setVisible(true);
        }
        JOptionPane optionPane = new JOptionPane("Mover:");
        optionPane.add(speechBubble);

        if (clear) {
            speechBubble.setText("");
        }
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
        speechBubble.setVisible(false);
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

