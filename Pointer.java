import java.awt.*;

class Pointer {
    protected int xPos, yPos;
    protected int speed = 10;
    /**
     * Objekt was sich bewegen kann
     */
    private Camera camera;
    private int mapSizeX, mapSizeY;

    public Pointer() {
    }

    /**
     * Bestimmte Position
     */
    public Pointer(int pXpos, int pYpos) {
        xPos = pXpos;
        yPos = pYpos;
    }

    public Pointer(Point cords) {
        new Pointer((int) cords.getX(), (int) cords.getY());
    }


    public void setMove(Point pMove) {
        speed = Math.round(speed);
        xPos += pMove.getX() * speed;
        yPos += pMove.getY() * speed;
        if (camera != null) {
            camera.centerOnObject(this);
        }
    }

    // FIXME: 14.04.2019 Richtige Implenmentierung nur wo ?
    //  BorderCheck wird halt nur im Editor benutzt und daher Implementierung im Pointer nicht nötig EDIT: Implementierung  ist im Editor
    public void checkBorder(int mapSizeX, int mapSizeY) {
        this.mapSizeX = mapSizeX;
        this.mapSizeY = mapSizeY;
        int oldXPos = xPos;
        int oldYPos = yPos;

        boolean leftBorder;
        boolean rightBorder;
        boolean upBorder;
        boolean downBorder;
        leftBorder = xPos < 0 + GUI.FRAME_WIDTH / 2;
        rightBorder = xPos > mapSizeX * Tile.TILEWIDTH - GUI.FRAME_WIDTH / 2;
        upBorder = yPos < 0 + GUI.FRAME_HEIGHT / 2;
        downBorder = yPos > mapSizeY * Tile.TILEHEIGHT - GUI.FRAME_WIDTH / 2;
        //BEWEGENDER POINTER:
        if (leftBorder || rightBorder || upBorder || downBorder) {
            xPos = oldXPos;
            yPos = oldYPos;
        }
    }


    public Point getLocation() {
        return new Point(xPos, yPos);
    }

    public void setLocation(Point pKords) {
        xPos = (int) pKords.getX();
        yPos = (int) pKords.getY();
    }

    public void setLocation(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
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

    public void setXPos(int xPos) {
        this.xPos = xPos;
    }

    public void setYPos(int yPos) {
        this.yPos = yPos;
    }
}
