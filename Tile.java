import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class Tile extends JPanel implements Cloneable, Comparable {
    public static int TILEWIDTH = 64, TILEHEIGHT = 64;
    public BufferedImage tileImage;
    public Tile pathParent;
    public float costFromStart;
    /**
     * Tile Klasse - Basis der Maps
     * -> hat verschiedene Attribute (blocked,danger) auf die vom Mover reagiert werden können
     * -> besitzt eine ID zur Identifikation (standardmäßig die Tilenummer im TileSet)
     * -> besitzt ein Image (Grafik)
     */
    protected boolean blocked = false;
    protected boolean danger = false;
    protected LinkedList neighbours = new LinkedList();
    protected boolean showImage = true;
    protected int id;
    protected float estimatedCostToGoal;
    protected Insets borderInsets;
    protected boolean fill;
    protected Color color;

    public Tile(BufferedImage pTileImage) {
        super();
        setOpaque(true);
        tileImage = pTileImage;
        borderInsets = new Insets(0, 0, 0, 0);
    }

    public static void setTILEWIDTH(int TILEWIDTH) {
        Tile.TILEWIDTH = TILEWIDTH;
    }

    public static void setTILEHEIGHT(int TILEHEIGHT) {
        Tile.TILEHEIGHT = TILEHEIGHT;
    }

    /**
     * rendert das Tile Bild und den Border
     */
    public void renderTile(Graphics2D g2d, int pXPos, int pYPos) {
        this.setLocation(pXPos, pYPos);
        if (showImage) {
            g2d.drawImage(tileImage, this.getX(), this.getY(), TILEWIDTH, TILEHEIGHT, null);
        }
        paintBorder(g2d);
    }

    private void paintBorder(Graphics2D g2d) {
        g2d.setColor(color);
        if (borderInsets.top > 0) {
            int stroke = borderInsets.top;
            g2d.setStroke(new BasicStroke(stroke));
            g2d.drawLine(getX() + stroke / 2, getY() + stroke / 2, getX() + TILEWIDTH - stroke / 2, getY() + stroke / 2);
        }

        if (borderInsets.right > 0) {
            int stroke = borderInsets.right;
            g2d.setStroke(new BasicStroke(stroke));
            g2d.drawLine(getX() + TILEWIDTH - stroke / 2, getY() + stroke / 2, getX() + TILEWIDTH - stroke / 2, getY() + TILEHEIGHT - stroke / 2);
        }

        if (borderInsets.bottom > 0) {
            int stroke = borderInsets.bottom;
            g2d.setStroke(new BasicStroke(stroke));
            g2d.drawLine(getX() + stroke / 2, getY() + TILEHEIGHT - stroke / 2, getX() + TILEWIDTH - stroke / 2, getY() + TILEHEIGHT - stroke / 2);
        }

        if (borderInsets.left > 0) {
            int stroke = borderInsets.left;
            g2d.setStroke(new BasicStroke(stroke));
            g2d.drawLine(getX() + stroke / 2, getY() + stroke / 2, getX() + stroke / 2, getY() + TILEHEIGHT - stroke / 2);
        }
        if (fill) {
            g2d.fillRect(getX(), getY(), Tile.TILEWIDTH, Tile.TILEHEIGHT);
        }

    }

    public Point getTileLocation() {
        return new Point((getX() / TILEWIDTH) - 1, (getY() / TILEHEIGHT) - 1);
    }

    public void setFill(boolean fill) {
        this.fill = fill;
    }

    public void showImage(boolean b) {
        showImage = b;
    }

    public boolean isDanger() {
        return danger;
    }

    public void setDanger(boolean danger) {
        this.danger = danger;
        if (danger) {
            setColor(Color.red);
        }
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean b) {
        blocked = b;
    }

    public Tile clone() {
        try {
            return (Tile) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public Insets getBorderInsets() {
        return borderInsets;
    }

    public void setBorderInsets(Insets insets) {
        borderInsets = insets;
    }

    public LinkedList getNeighbours() {
        return neighbours;
    }

    public void setNeighbours(LinkedList pNeighbours) {
        neighbours = pNeighbours;
    }

    public void setEstimatedCostToGoal(Tile pGoal) {
        int dx = pGoal.getX() - this.getX();
        int dy = pGoal.getY() - this.getY();
        this.estimatedCostToGoal = (float) Math.sqrt((dx * dx) + (dy * dy));   //sqrt leifert einen Double. Mit Typecasting leicht zu wandeln.
    }

    public float getCost() {
        return costFromStart + estimatedCostToGoal;
    }

    public Tile getPathParent() {
        return pathParent;
    }

    public void setPathParent(Tile pTile) {
        pathParent = pTile;
    }

    public float getCostFromStart() {
        return costFromStart;
    }

    public void setCostFromStart(float pCost) {
        costFromStart = pCost;
    }

    public float getCostForNextStep(Tile pTile) {
        float cost = 0;
        LinkedList neighbours = pTile.getNeighbours();
        int dx = pTile.getX() - this.getX();   //
        int dy = pTile.getY() - this.getY();
        cost = (float) Math.sqrt((dx * dx) + (dy * dy));
        return cost;
    }

    public BufferedImage getTileImage() {
        return tileImage;
    }

    public void setTileImage(BufferedImage pTileImage) {
        tileImage = pTileImage;
    }

    public int getID() {
        return id;
    }

    public void setID(int pID) {
        id = pID;
    }

    public int compareTo(Object other) {
        float thisValue = this.getCost();
        float otherValue = ((Tile) other).getCost();
        float v = thisValue - otherValue;
        return (v > 0) ? 1 : (v < 0) ? -1 : 0;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void drawCenteredCircle(Graphics2D g, int x, int y, int r) {
        x = x - (r / 2);
        y = y - (r / 2);
        g.fillOval(x, y, r, r);
    }
}
