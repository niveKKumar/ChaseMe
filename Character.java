import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;


public class Character extends Mover {

    public JTextArea moverDebugPane = new JTextArea("");
    /**
     * MainCharacter Klasse (abgeleitet von Mover Klasse)
     * -> kann Schritte zählen
     * -> Position auf einem Tile oder auf einer Tile Fläche kann abgefragt werden
     */
    protected int steps;
    protected int checkpointsActivated = 0;

    /**
     * Zum Erstellen des Character benötigt man
     * - einen Bildschirm (Panel),Position (xPos,yPos) und die zugehörige(n) Map(s)
     */
    public Character(GamePanel pGP, int pXpos, int pYpos, MapBase[] pMap) {
        super(pGP, pXpos, pYpos, new SpriteSheet("Content/Graphics/player/playersheet.png", 4, 3), pMap);
        if (gamePanel.getCamera() != null) {
            gamePanel.getCamera().centerOnObject(this);
        }

        moverDebugPane.setSize(400, 150);
        GUI.addToDebugPane(moverDebugPane);

    }

    /**
     * Schritte werden hochgezählt
     */
    @Override
    public void setMove(Point pMove) {
        if (!speaking) {
            super.setMove(pMove);
            if (!collisionCheck()) {
                steps++;
            }
        }

        moverDebugPane.setText("XPos: " + getLocation().getX() + "\n = Tile: " + getLocation().getX() / Tile.TILEWIDTH + "\n YPos: " + getLocation().getY() + " = \nTile: " + getLocation().getY() / Tile.TILEHEIGHT);

        gamePanel.getCamera().centerOnObject(this);
    }

    /**
     * Liste mit allen "aktiven" TileIDs wird aufgerufen (moverIsOnTileID)
     * und mit Parameter IDs abgeglichen
     * CP Amount gibt an, wie weit der Mover das Tile berühren soll (1 = leicht berühren,4 = voller Umfang)
     */
    public boolean isOnThisTile(int xTileID, int yTileID, int cpAmount) {
        Point cord = new Point(xTileID, yTileID);
        LinkedList<Point> tileList = moverIsOnTileID();

        for (int i = 0; i < tileList.size(); i++) {
            if (tileList.get(i).getLocation().equals(cord)) {
                checkpointsActivated++;
            }
        }
        if (checkpointsActivated >= cpAmount) {
            checkpointsActivated = 0;
            return true;
        } else {
            checkpointsActivated = 0;
            return false;
        }
    }

    /**
     * Abfrage ob Character in einer bestimmten FLäche ist
     */
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

        for (int i = (int) firstCorner.getY(); i < (int) secondCorner.getY() + 1; i++) {
            for (int j = (int) firstCorner.getX(); j < (int) secondCorner.getX() + 1; j++) {
                if (isOnThisTile(j, i, cpAmount)) {
                    System.out.println("is in area");
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Funktionsmethoden (Schrittanzahl,)
     */
    public int getSteps() {
        return steps;
    }

}

