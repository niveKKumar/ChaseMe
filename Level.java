import java.awt.*;
import java.util.LinkedList;

public class Level {
    /**
     * Dialog = 1 -> Level Dialog fängt an
     * = 0 -> Level Dialog startet nicht
     */
    public int dialog = 0;
    public Character mover;
    public LinkedList<Runner> enemy = new LinkedList<>();
    public Map[] maps;
    private int level;
    private GamePanel gamePanel;
    private KeyManager keyListener;
    private boolean gridlines;


    public Level(GamePanel gp, int level, KeyManager keyListener) {
        this.level = level;
        gamePanel = gp;
        this.keyListener = keyListener;

    }

    public void createLevelObjects(int mapAmount, int enemyAmount) {
        maps = new Map[mapAmount];
        mover = new Character(gamePanel, 0, 0, maps);

        if (!enemy.isEmpty()) {
            enemy.clear();
        }
        for (int i = 0; i < enemyAmount; i++) {
            enemy.add(new Runner(gamePanel, 0, 0, maps));
        }
        dialog = 0;
    }


    public void update() {
        if (GUI.keyInputToMove(keyListener).getX() != 0 || GUI.keyInputToMove(keyListener).getY() != 0) {
            mover.setMove((int) GUI.keyInputToMove(keyListener).getX(), (int) GUI.keyInputToMove(keyListener).getY());
            gamePanel.getCamera().centerOnObject(mover);
        } // end of if
    }

    public void renderLevel(Graphics2D g2d) {
        if (maps[0] != null) {
            maps[0].renderMap(g2d);
        }
        if (enemy.size() > 0) {
            for (int enemyamount = 0; enemyamount < enemy.size(); enemyamount++) {
                if (enemy.get(enemyamount) != null) {
                    enemy.get(enemyamount).draw(g2d);
                }
            }
        }
        mover.draw(g2d);
//        Item Maps :
        for (int mapsAmount = 1; mapsAmount < maps.length; mapsAmount++) {
            if (maps[mapsAmount] != null) {
                maps[mapsAmount].renderMap(g2d);
            }
        }
    }

    public void setMap(int mapNumber, String tsPath, String pStatus, Point chapterOffset) {
        maps[mapNumber] = new Map(gamePanel, tsPath, pStatus, chapterOffset);
    }

    public void setBaseMap(String tsPath, String pStatus) {
        setMap(0, tsPath, pStatus, new Point(0, 0));
        gamePanel.setCamera(maps[0].mapSizeX, maps[0].getMapSizeY(), maps[0].getChapterOffset());

    }

    public void setGridLines(boolean b) {
        gridlines = b;
        for (int i = 0; i < maps.length; i++) {
            if (maps[i] != null) {
                maps[i].setGridLines(b);
            }
        }
    }

    public void addNewEnemyOnTile(int xTile, int yTile, double pSpeed) {
        addNewEnemy(xTile * Tile.TILEWIDTH, yTile * Tile.TILEHEIGHT, pSpeed);
    }

    public void addNewEnemy(int xPos, int yPos, double pSpeed) {
        Runner r = new Runner(gamePanel, xPos, yPos, maps);
        r.setSpeed(pSpeed);
        addEnemy(r);
    }

    public void addEnemy(Runner r) {
        enemy.add(r);
    }

    public void stop() {
        mover.setSpeaking(true);
        for (int i = 0; i < enemy.size(); i++) {
            enemy.get(i).setSpeaking(true);
        }
    }

    public void clear() {
    }

    public Map[] getMaps() {
        return maps;
    }

    public Map getBaseMap() {
        return getMaps()[0];
    }

    public Character getMover() {
        return mover;
    }

    public LinkedList<Runner> getEnemyList() {
        return enemy;
    }

    public Runner getEnemy(int enemyNumber) {
        return enemy.get(enemyNumber);
    }

    public int getLevel() {
        return level;
    }

    public boolean isGridlines() {
        return gridlines;
    }

}
