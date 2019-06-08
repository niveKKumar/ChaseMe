import java.awt.*;
import java.awt.geom.Point2D;

public class Level {
    /**
     * Dialog = 1 -> Level Dialog fängt an
     * = 0 -> Level Dialog startet nicht
     */
    public int dialog = 0;
    public Character mover;
    public Runner[] enemy;
    public MapBase[] maps;
    public PathFinder pathFinder;
    public DisplayAnalytics[] analytics;
    private int level;
    private Camera cam;
    private GamePanel gamePanel;
    private KeyManager keyListener;
    private boolean gridlines;


    public Level(GamePanel gp, int level, KeyManager keyListener, Camera camera) {
        this.level = level;
        gamePanel = gp;
        this.keyListener = keyListener;
        cam = camera;
    }

    public void createLevelObjects(int mapAmount, int enemyAmount, int checkPointsAmount) {
        maps = new Map[mapAmount];
        mover = new Character(gamePanel, 0, 0, maps);
        enemy = new Runner[enemyAmount];

        for (int i = 0; i < enemy.length; i++) {
            enemy[i] = new Runner(gamePanel, 0, 0, maps);
        }
        pathFinder = new PathFinder(maps, mover, gamePanel);
        dialog = 0;

        analytics = new DisplayAnalytics[mapAmount];
    }


    public void update() {
        if (GUI.keyInputToMove(keyListener).getX() != 0 || GUI.keyInputToMove(keyListener).getY() != 0) {
            mover.setMove(GUI.keyInputToMove(keyListener));
            gamePanel.getCamera().centerOnObject(mover);
            pathFinder.resetPath();
        } // end of if
        Point2D from = pathFinder.getNextStep();
        Point2D to = pathFinder.getNextStep();
        if (to != null && GUI.keyInputToMove(keyListener).getX() == 0 && GUI.keyInputToMove(keyListener).getY() == 0) {
            mover.setPathMove(from, to);
        } // end of if
    }

    public void renderLevel(Graphics2D g2d) {
        maps[0].renderMap(g2d);
        if (enemy.length > 0) {
            for (int enemyamount = 0; enemyamount < enemy.length; enemyamount++) {
                if (enemy[enemyamount] != null) {
                    enemy[enemyamount].draw(g2d);
                }
            }
            mover.draw(g2d);
        }
//        Item Maps :
        for (int mapsAmount = 1; mapsAmount < maps.length; mapsAmount++) {
            if (maps[mapsAmount] != null) {
                maps[mapsAmount].renderMap(g2d);
            }
        }
        for (int i = 0; i < analytics.length; i++) {
            if (analytics[i] != null) {
                analytics[i].renderAnalytics(g2d);
            }
        }
        // Zum Testen
        if (enemy.length > 0) {
            for (int enemyamount = 0; enemyamount < enemy.length; enemyamount++) {
                if (enemy[enemyamount] != null) {
                    enemy[enemyamount].draw(g2d);
                }
            }
            mover.draw(g2d);
        }
    }

    public DisplayAnalytics[] getAnalytics() {
        return analytics;
    }

    public MapBase[] getMaps() {
        return maps;
    }

    public Character getMover() {
        return mover;
    }

    public Runner[] getEnemys() {
        return enemy;
    }

    public int getLevel() {
        return level;
    }

    public int getDialog() {
        return dialog;
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

    public boolean isGridlines() {
        return gridlines;
    }
}
