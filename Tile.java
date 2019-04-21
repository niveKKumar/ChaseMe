import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

public class Tile extends JPanel implements Cloneable, Comparable{
  protected boolean blocked = false;
  public static int TILEWIDTH = 64, TILEHEIGHT = 64;
  public Image tileImage;
  protected LinkedList neighbours;
  protected boolean showImage = true;
  protected int id;
  public Tile pathParent;
  public float costFromStart;
  protected float estimatedCostToGoal;
  boolean pointed;

  public Tile(Image pTileImage)  {
    super();
    setOpaque(true);
    pointed = false;
    tileImage = pTileImage;
  }

  public void renderTile(Graphics2D g2d, int pXPos, int pYPos){
    this.setLocation(pXPos, pYPos);
    if (showImage) {
      g2d.drawImage(tileImage, this.getX(), this.getY(), TILEWIDTH, TILEHEIGHT, null);
    }
      if (pointed) {
          showImage(false);
          setBorder(BorderFactory.createLineBorder(Color.black, 5));
      }
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

  public boolean isPointed() {
    return pointed;
  }

  public void setPointed() {
    if (pointed) {
      setBorder(BorderFactory.createLineBorder(Color.black, 5));
        System.out.println("Ich ! Das  Tile auf den Koordinaten P(" + getX() + "|" + getY() + ") ist markiert worden");
      pointed = false;
    } else {
      setBorder(null);
      pointed = true;
    }

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
  public void setTileImage(Image pTileImage) {
    tileImage = pTileImage;
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

    public void setPathParent(Tile pTile) {
        pathParent = pTile;
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

    public Image getTileImage() {
        return tileImage;
    }

  public static void setTILEWIDTH(int TILEWIDTH) {
    Tile.TILEWIDTH = TILEWIDTH;
  }

  public static void setTILEHEIGHT(int TILEHEIGHT) {
    Tile.TILEHEIGHT = TILEHEIGHT;
  }
}
