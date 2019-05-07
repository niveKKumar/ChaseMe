import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class Tile extends JPanel implements Cloneable, Comparable{
  protected boolean blocked = false;
  public static int TILEWIDTH = 64, TILEHEIGHT = 64;
    public BufferedImage tileImage;
  protected LinkedList neighbours;
  protected boolean showImage = true;
  protected int id;
  public Tile pathParent;
  public float costFromStart;
  protected float estimatedCostToGoal;
    protected Insets borderInsets;

    public Tile(BufferedImage pTileImage) {
    super();
    setOpaque(true);
    tileImage = pTileImage;
    borderInsets = new Insets(0, 0, 0, 0);
  }

  public void renderTile(Graphics2D g2d, int pXPos, int pYPos){
    this.setLocation(pXPos, pYPos);
    if (showImage) {
      g2d.drawImage(tileImage, this.getX(), this.getY(), TILEWIDTH, TILEHEIGHT, null);
    }
    paintBorder(g2d);
  }

  private void paintBorder(Graphics2D g2d) {
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
  }

  public void setBorderInsets(Insets insets) {
    borderInsets = insets;
  }

  public void showImage(boolean b){
    showImage = b;
  }


  public void setBlocked(boolean b){
    blocked = b;
    }

  public boolean isBlocked(){return blocked;
  }
     
  public Tile clone(){
    try{
        return (Tile) super.clone();
    } catch(CloneNotSupportedException e) {
      return null;
    }
  }

    public Insets getBorderInsets() {
        return borderInsets;
    }
  public LinkedList getNeighbours(){
      return neighbours;
  }
  public void setNeighbours(LinkedList pNeighbours){
    neighbours = pNeighbours;
  }
  public void setCostFromStart(float pCost){
     costFromStart = pCost;
   }
  public void setEstimatedCostToGoal(Tile pGoal){
    int dx = pGoal.getX() - this.getX();
    int dy = pGoal.getY() - this.getY();
    this.estimatedCostToGoal = (float) Math.sqrt((dx*dx)+(dy*dy));   //sqrt leifert einen Double. Mit Typecasting leicht zu wandeln.
  }

    public float getCost() {
        return costFromStart + estimatedCostToGoal;
  }
  public void setID(int pID) {
    id = pID;
  }

    public void setPathParent(Tile pTile) {
        pathParent = pTile;
    }

    public Tile getPathParent() {
        return pathParent;
    }
  public float getCostFromStart(){
     return costFromStart;
   }
  public float getCostForNextStep(Tile pTile){
    float cost = 0;
    LinkedList neighbours = pTile.getNeighbours();
    int dx = pTile.getX() - this.getX();   //
    int dy = pTile.getY() - this.getY();
    cost = (float) Math.sqrt((dx*dx)+(dy*dy));
    return cost;
  }

    public BufferedImage getTileImage() {
        return tileImage;
    }

  public int getID() {
      return id;
  }
  public int compareTo(Object other){
    float thisValue = this.getCost();
    float otherValue = ((Tile) other).getCost();
    float v = thisValue - otherValue;
      return (v > 0) ? 1 : (v < 0) ? -1 : 0;
  }

    public void setTileImage(BufferedImage pTileImage) {
        tileImage = pTileImage;
    }

  public static void setTILEWIDTH(int TILEWIDTH) {
    Tile.TILEWIDTH = TILEWIDTH;
  }

  public static void setTILEHEIGHT(int TILEHEIGHT) {
    Tile.TILEHEIGHT = TILEHEIGHT;
  }
}
