import java.awt.*;

class Pointer {
    protected double xPos, yPos;
    protected int speed = 10;
    /**
     * Objekt was sich bewegen kann
     */
    private Camera camera;
    private int mapSizeX, mapSizeY;
    private GamePanel gamePanel;

    public Pointer() {
    }

    /**
     * Bestimmte Position
     */
    public Pointer(GamePanel gp, int pXpos, int pYpos) {
        gamePanel = gp;
        xPos = pXpos;
        yPos = pYpos;
    }

    public Pointer(GamePanel gp, Point cords) {
        new Pointer(gp, (int) cords.getX(), (int) cords.getY());
    }


    public void setMove(Point pMove) {
        speed = Math.round(speed);
        xPos += pMove.getX() * speed;
        yPos += pMove.getY() * speed;
    }

    // FIXME: 14.04.2019 Richtige Implenmentierung nur wo ?
    //  BorderCheck wird halt nur im Editor benutzt und daher Implementierung im Pointer nicht nötig EDIT: Implementierung  ist im Editor
    public void checkBorder(int mapSizeX, int mapSizeY) {
        this.mapSizeX = mapSizeX;
        this.mapSizeY = mapSizeY;
        double oldXPos = xPos;
        double oldYPos = yPos;

        boolean leftBorder;
        boolean rightBorder;
        boolean upBorder;
        boolean downBorder;
        leftBorder = xPos < 0 + gamePanel.getWidth() / 2;
        rightBorder = xPos > mapSizeX * Tile.TILEWIDTH - gamePanel.getWidth() / 2;
        upBorder = yPos < 0 + gamePanel.getHeight() / 2;
        downBorder = yPos > mapSizeY * Tile.TILEHEIGHT - gamePanel.getHeight() / 2;
        //BEWEGENDER POINTER:
        if (leftBorder || rightBorder || upBorder || downBorder) {
            xPos = oldXPos;
            yPos = oldYPos;
        }
    }


    public Point getLocation() {
        return new Point((int) xPos, (int) yPos);
    }

    public void setLocation(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        if (camera != null) {
            camera.centerOnObject(this);
        }
    }

    public void setLocation(double xPos, double yPos) {
        setLocation(Math.round(xPos), Math.round(yPos));
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        mapSizeX = camera.getxSize();
        mapSizeY = camera.getySize();
        setLocation(mapSizeX * Tile.TILEWIDTH / 2, mapSizeY * Tile.TILEHEIGHT / 2);
        this.camera = camera;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setXPos(double xPos) {
        this.xPos = xPos;
    }

    public void setYPos(double yPos) {
        this.yPos = yPos;
    }
}
