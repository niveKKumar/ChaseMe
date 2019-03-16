import java.util.*;
import java.awt.geom.*;
import java.awt.*;
import javax.swing.*;

public class PathFinder{
  private Path2D pathShape;
  private Map[] map;
  private Mover mover;
  private LinkedList <Tile> closedList = new LinkedList<Tile>();
  private PriorityList openList = new PriorityList();
  public LinkedList <Tile> pathTiles = new LinkedList<Tile>();;
  private LinkedList <Point2D> pointsOnPath = new LinkedList<Point2D>();
  private GUI gui;
  public PathFinder(GUI pGUI,Map [] pMap, Mover pMover){
    map = pMap;
    mover = pMover;
    gui = pGUI;
  }

  public Shape getPathShape(){
    return pathShape;
  }

  private void setBlockedTilesToClosedList(){
    for (int m = 0; m < map.length; m++) {
//      System.out.println("Map Laenge Pathfinder"+m);
      for (int i = 0; i < map[m].mapTiles.length; i++) {
        for (int j = 0; j < map[m].mapTiles.length; j++) {
          if (map[m].mapTiles[i][j].isBlocked()) {
//            System.out.printf("Map 2 Tiles blocked");
            closedList.add(map[m].mapTiles[i][j]);} // end of if
        } // end of for
      }

    }//end of for - Map
  }

  public void searchPath(Tile pStart, Tile pTarget){
    if (pTarget.isBlocked()== true ) {
      return;
    } // end of if
    openList.clear();
    closedList.clear();
    setBlockedTilesToClosedList();
    pStart.setCostFromStart(0);
    pStart.setPathParent(null);
    pStart.setEstimatedCostToGoal(pTarget);
    openList.add((Tile) pStart);
    while (!openList.isEmpty()) {
      Tile current = (Tile) openList.removeFirst();
      if (current == pTarget){
        constructPathTilesList(pTarget);
        return;
      }

      //System.out.println("start"+pStart.getNeighbours());
      closedList.add((Tile) current);
      LinkedList neighbours = current.getNeighbours();
      for (int i=0;i<neighbours.size();i++ ) {
        Tile neighbourTile = (Tile) neighbours.get(i);
        boolean isOnOpen = openList.contains(neighbourTile);
        boolean isOnClosed = closedList.contains(neighbourTile);
        float costFromStart = current.getCostFromStart()+current.getCostForNextStep(neighbourTile);
        if (!isOnOpen && !isOnClosed|| costFromStart < neighbourTile.getCostFromStart()) {
          neighbourTile.setEstimatedCostToGoal(pTarget);
          neighbourTile.setCostFromStart(costFromStart);
          neighbourTile.setPathParent(current);
          if(isOnClosed){
            closedList.remove(neighbourTile);}
          if(!isOnOpen){
            openList.add((Tile)neighbourTile);
          }}
      }
    }
  }


  private void constructPathTilesList(Tile pTile){
    pathTiles.clear();
    while (pTile.getPathParent() != null) {
      pathTiles.addFirst((Tile) pTile);
      pTile = pTile.getPathParent();
    }
    constructMovingPath();
  }
  private void constructMovingPath(){
    pathShape = new Path2D.Double();
    pathShape.moveTo(mover.getLocation().getX()+64/2,mover.getLocation().getY()+64/2);
    Tile t;
    for (int i =0;i< pathTiles.size();i++) {
      t = (Tile) pathTiles.get(i);
      pathShape.lineTo((t.getX()+64/2)+gui.getCamera().getXOffset(), (t.getY()+64/2)+gui.getCamera().getYOffset());
    }
    calculateMovingPointsOnPath(pathShape);
  }

  private void calculateMovingPointsOnPath(Shape pPathShape){
    pointsOnPath.clear();
    PathIterator pi = pPathShape.getPathIterator(null, 0.1);
    while (!pi.isDone()) {
      double[] koordinaten = new double[6];
      switch (pi.currentSegment(koordinaten)) {
        case PathIterator.SEG_MOVETO:
          pointsOnPath.add(new Point2D.Double(koordinaten[0], koordinaten[1]));
          break;
        case PathIterator.SEG_LINETO:
          Point2D p1 = (Point2D) pointsOnPath.get(pointsOnPath.size() - 1);
          Point2D p2 = new Point2D.Double(koordinaten[0], koordinaten[1]);
          double d = p1.distance(p2);
          double i = d / 1.5;
          for (int j = 0; j < i; j++) {
            Point2D p3 = new Point2D.Double(p1.getX() + (j / i) * (p2.getX() - p1.getX()), p1.getY() + (j / i) * (p2.getY() - p1.getY()));
            pointsOnPath.add(p3);
          }
          break;
      }
      pi.next();
    }
  }

  public void resetPath(){
    pointsOnPath.clear();
  }

  public Point2D getNextStep(){
    return (Point2D) pointsOnPath.pollFirst();
  }

  class PriorityList extends LinkedList{

    public void add(Comparable object){
      for (int i=0;i < this.size();i++) {
        if(object.compareTo(this.get(i)) <=0) {
          add(i,object);
          return;}
      }
      addLast(object);
    }}

} // end of for

