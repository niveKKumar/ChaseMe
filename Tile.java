import java.awt.Graphics;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.*;

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
  protected boolean item;
  boolean pointed;
  
  public Tile(Image pTileImage)  {
    super();
    tileImage = pTileImage;
  }
    
  public void renderTile(Graphics2D g2d, int pXPos, int pYPos){
    this.setLocation(pXPos, pYPos);
    if (showImage) {
      g2d.drawImage(tileImage, this.getX(), this.getY(), TILEWIDTH, TILEHEIGHT, null);
    }
  }
  
  public void showImage(boolean b){
    showImage = b;
  }

  public boolean isPointed() {
    return pointed;
  }

  public void setPointed(boolean pointed) {
    this.pointed = pointed;
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
  
  public LinkedList getNeighbours(){
    return neighbours;  
  }

  public void setItem(boolean b) {
    item = b;
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
  public void setPathParent(Tile pTile){
   pathParent = pTile;  
  }
  public void setID(int pID) {
    id = pID;
  }
  public void setTileImage(Image pTileImage) {
    tileImage = pTileImage;
  }

  public float getCost(){
    return costFromStart + estimatedCostToGoal;
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
  public Tile getPathParent(){
   return pathParent;  
  }
  public int getID() {
   return id; 
  }
  public int compareTo(Object other){
    float thisValue = this.getCost();
    float otherValue = ((Tile) other).getCost();
    float v = thisValue - otherValue;
    return (v>0)?1:(v<0)?-1:0; 
  }

  public static void setTILEWIDTH(int TILEWIDTH) {
    Tile.TILEWIDTH = TILEWIDTH;
  }

  public static void setTILEHEIGHT(int TILEHEIGHT) {
    Tile.TILEHEIGHT = TILEHEIGHT;
  }
}
