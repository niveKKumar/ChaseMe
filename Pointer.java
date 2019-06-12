import java.awt.*;

class Pointer {
    protected double xPos, yPos;
    protected double speed = 20;
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


    public void setMove(int xMove, int yMove) {
        xPos += xMove * speed;
        yPos += yMove * speed;
    }

    public Point getLocation() {
        return new Point((int) xPos, (int) yPos);
    }

    public int getxPos() {
        return (int) xPos;
    }

    public int getyPos() {
        return (int) yPos;
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

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setXPos(double xPos) {
        this.xPos = xPos;
    }

    public void setYPos(double yPos) {
        this.yPos = yPos;
    }
}
