import java.awt.*;
import java.util.LinkedList;
import java.util.Random;

public class Runner extends Mover {
    int collisionduration = 0;
    boolean gerade, quer;
    int abstand = 5;
    boolean flag = true;
    boolean automaticWalking = false;
    Point startDistance, maxdistance;
    boolean runningPathBack;
    private double delay = 1;

    public Runner(GamePanel gp, int xPos, int yPos, MapBase[] pMap) {
        super(gp, xPos, yPos, new SpriteSheet("Content/Graphics/player/playersheet.png", 4, 3), pMap);
        Random rnd = new Random();
        // end of if-else
    }


    /**
     * Besonderheit: setMove aber Speed kann weniger als 1 sein = delay wird erhöht
     */
    public void setMoveWithSpeed(double pSpeed, int xMove, int yMove) {
        super.setMove(xMove, yMove);
        if (collisionCheck()) {
            collisionduration++;
            if (collisionduration > 5) {
                // Kann man wenn man will darauf reagieren (durch bspw. Löschen des Movers, ist aber nicht in diesem Spiel realisiert)
                System.out.println("ich laufe gegen wand und befinde mich auf Tile :" + getTileLocation());
            }
        }
    }

    public void setMoveWithSpeed(double pSpeed, Point direction) {
        setMoveWithSpeed(pSpeed, (int) direction.getX(), (int) direction.getY());
    }

    /**
     * Gegner läuft gerade aus bis bestimmten Punkt (Border)
     */
    public void enemystraightrun(Point pBorder, double pSpeed, int x, int y) {   //Gegner der nur gerade aus läuft (Delay = 1 - Kein Delay)
        if (pBorder == null) {
            pBorder = new Point(map[0].getMapSizeX(), map[0].getMapSizeY());
        }
        if (!isTileABorder(pBorder)) {
            setMoveWithSpeed(pSpeed, x, y);
        }
    }

    /**
     * Gegner läuft bestimmte Länge (maxdistance) hin und her in die angegebene Richtung
     * -> Einschränkungen = MaxDistance und StartingPoint müssen vorher konfiguriert sein und Sinn ergeben ansonsten funktioniert nicht !!
     */
    public void enemyRepeatedRun(double pSpeed, Point direction) {

        if (checkIfRepeatedRunIsPossible(direction)) {

            if (getTileLocation().equals(startDistance)) {
                runningPathBack = false;
            }
            if (getTileLocation().equals(maxdistance)) {
                runningPathBack = true;
            }
            if (runningPathBack) {
                direction.x = (int) -direction.getX();
                direction.y = (int) -direction.getY();
            }
            setMoveWithSpeed(pSpeed, direction);
        }

    }

    /**
     * Gegner läuft bestimmte Länge (maxdistance) hin und her in die angegebene Richtung
     * -> Letztes Tile wird automatisch errechnet
     * -> Wichtig! Zum Stoppen, die Methode in update nicht weiter aufrufen UND mit stopRepeatedRun beendet werden = damit keine weiteren Fehler entstehen !
     */
    public void repeatedRightRun(double speed, int distance) {
        if (automaticWalking == false) {
            setStartDistance(getTileLocation());
            setMaxdistance(new Point((int) getTileLocation().getX() + distance, (int) getTileLocation().getY()));
            automaticWalking = true;
        } else {
            enemyRepeatedRun(speed, new Point(1, 0));
        }
    }

    public void repeatedLeftRun(int speed, int distance) {
        if (automaticWalking == false) {
            setStartDistance(getTileLocation());
            setMaxdistance(new Point((int) getTileLocation().getX() - distance, (int) getTileLocation().getY()));
        } else {
            enemyRepeatedRun(speed, new Point(-1, 0));
        }
    }

    public void repeatedUpRun(int speed, int distance) {
        if (automaticWalking == false) {
            setStartDistance(getTileLocation());
            setMaxdistance(new Point((int) getTileLocation().getX(), (int) getTileLocation().getY() - distance));
        } else {
            enemyRepeatedRun(speed, new Point(0, -1));
        }
    }

    public void repeatedDownRun(int speed, int distance) {
        if (automaticWalking == false) {
            setStartDistance(getTileLocation());
            setMaxdistance(new Point((int) getTileLocation().getX(), (int) getTileLocation().getY() + distance));
        } else {
            enemyRepeatedRun(speed, new Point(0, 1));
        }
    }

    public void stopRepeatedRun() {
        automaticWalking = false;
    }

    public boolean checkIfRepeatedRunIsPossible(Point direction) {
        boolean xTile = true, yTile = true;
        if (direction.getX() != 0 && direction.getY() != 0 && flag) {
            System.err.println("Es können bis zum jetztigen Stand keine Diagonalen gelöst werden ! Daher sollte der Weg selber gecheckt werden !");
        }
        //Check ob Vertikal oder Horizontal sind!
        if (direction.getX() == 1 || direction.getX() == -1) {
            if (startDistance.getY() != maxdistance.getY()) {
                xTile = false;
            }
        }
        if (direction.getY() == 1 || direction.getY() == -1) {
            if (startDistance.getX() != maxdistance.getX()) {
                yTile = false;
            }
        }
        flag = false;
        return xTile && yTile;
    }

    public void setStartDistance(Point startDistance) {
        this.startDistance = startDistance;
    }

    public void setMaxdistance(Point maxdistance) {
        this.maxdistance = maxdistance;
    }

    public void movetotarget(Mover character) {
        movetotarget(speed, character);
    }

    public void movetotarget(double pSpeed, Mover character) {                     //Dynamischer Gegner
        //Von Eric K.
        int playerX = (int) character.getLocation().getX() - gamePanel.getCamera().getXOffset();
        int playerY = (int) character.getLocation().getY() - gamePanel.getCamera().getYOffset();
        int runnerPosX = (int) xPos - gamePanel.getCamera().getXOffset();
        int runnerPosY = (int) yPos - gamePanel.getCamera().getYOffset();
        int xMove = 0, yMove = 0;

        if (!quer) {
            if (gerade) {
                if (runnerPosX < playerX - abstand)
                    xMove = 1;
                else if (runnerPosX > playerX + abstand)
                    xMove = -1;
                if (xMove == 0)
                    gerade = false;
            } else {
                if (runnerPosY < playerY - abstand)
                    yMove = 1;
                else if (runnerPosY > playerY + abstand)
                    yMove = -1;
                if (yMove == 0)
                    gerade = true;
            } // end of if-else
        }
        if (quer) {
            if (runnerPosX < playerX - abstand)
                xMove = 1;
            else if (runnerPosX > playerX + abstand)
                xMove = -1;
            if (runnerPosY < playerY - abstand)
                yMove = 1;
            else if (runnerPosY > playerY + abstand)
                yMove = -1;
        } // end of if-else

        setMove(xMove, yMove);
    }

    public boolean movercheck(Mover mover) {
        LinkedList<Point> runnerCP = checkPointCords();
        LinkedList<Point> moverCP = mover.checkPointCords();
        int toleranz = 5;
        for (int i = 0; i < runnerCP.size(); i++) {
            for (int j = 0; j < moverCP.size(); j++) {
                if (runnerCP.get(i).getX() + toleranz > moverCP.get(j).getX() && runnerCP.get(i).getX() - toleranz < moverCP.get(j).getX()
                        && runnerCP.get(i).getY() + toleranz > moverCP.get(j).getY() && runnerCP.get(i).getY() - toleranz < moverCP.get(j).getY()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void draw(Graphics2D g2d) {
        super.draw(g2d);
    }

    public boolean isTileABorder(Point pBorder) {
        LinkedList temp = moverIsOnTileID();
        for (int i = 0; i < temp.size(); i++) {
            if (temp.get(i).equals(pBorder)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setSpritesheet(SpriteSheet sprites) {
        super.setSpritesheet(sprites);
    }
}
