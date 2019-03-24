import java.awt.*;

public class DisplayAnalytics{
  public boolean showGridLines = false;
  public boolean showTileIndices = false;
  public boolean showStartTarget = false;
  public boolean displayBlockedTiles = false;
  public boolean displayPath = false;
  public boolean displayPathTiles = false;
  public boolean moverHitbox = false;
  private Mover mover;
  private boolean isMover;
  private Map map;
  private GUI gui;
  private PathFinder pathFinder;  
  
  public DisplayAnalytics(GUI pGui, Map pMap, PathFinder pPathFinder){
    gui = pGui;
    map = pMap;
    pathFinder = pPathFinder;
  }
  public void setMover(Mover pMover){
  mover = pMover;
  isMover = true;
  }
  public void renderAnalytics(Graphics g){
    Graphics2D g2d = (Graphics2D) g;
    BasicStroke stroke1 = new BasicStroke(2 , BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND );
    BasicStroke stroke2 = new BasicStroke(3 , BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND );
    g2d.setStroke(stroke1);
    if (moverHitbox && isMover){
      g2d.setStroke(stroke2);
      g2d.drawLine(mover.checkPointUp.x,mover.checkPointUp.y,mover.checkPointRight.x,mover.checkPointRight.y);
      g2d.drawLine(mover.checkPointRight.x,mover.checkPointRight.y,mover.checkPointDown.x,mover.checkPointDown.y);
      g2d.drawLine(mover.checkPointDown.x,mover.checkPointDown.y,mover.checkPointLeft.x,mover.checkPointLeft.y);
      g2d.drawLine(mover.checkPointLeft.x,mover.checkPointLeft.y,mover.checkPointUp.x,mover.checkPointUp.y);
    }
    for (int spalte=0; spalte < map.mapTiles.length; spalte++ ) {
      for (int zeile=0; zeile < map.mapTiles.length; zeile++ ) {
        if (showGridLines) {
          map.mapTiles [spalte] [zeile].showImage(false);
          g2d.drawRect(map.mapTiles [spalte] [zeile].getX(), map.mapTiles [spalte] [zeile].getY(), 64, 64);
          g2d.drawString(spalte + "/" + zeile, map.mapTiles [spalte] [zeile].getX()+10, map.mapTiles [spalte] [zeile].getY()+10);
          g2d.drawString(map.mapTiles[spalte][zeile].getX() + "/" + map.mapTiles[spalte][zeile].getY(), map.mapTiles [spalte] [zeile].getX()+10, map.mapTiles [spalte] [zeile].getY()+20);
        } else {
          map.mapTiles [spalte] [zeile].showImage(true);
          }
        if (showStartTarget && map.mapTiles [spalte] [zeile] == gui.start) {
          g2d.setStroke(stroke2);
          g2d.setColor(Color.green);
          g2d.drawRect(map.mapTiles [spalte] [zeile].getX(), map.mapTiles [spalte] [zeile].getY(), 64, 64);
          g2d.setStroke(stroke1);
          g2d.setColor(Color.black);
       }
        if (showStartTarget && map.mapTiles [spalte] [zeile] == gui.target) {
          g2d.setStroke(stroke2);
          g2d.setColor(Color.RED);
          g2d.drawRect(map.mapTiles [spalte] [zeile].getX(), map.mapTiles [spalte] [zeile].getY(), 64, 64);
          g2d.setStroke(stroke1);
          g2d.setColor(Color.black);
        }
        
        if (displayBlockedTiles && map.mapTiles [spalte] [zeile].isBlocked()) {
          g2d.fillRect(map.mapTiles [spalte] [zeile].getX(), map.mapTiles[spalte] [zeile].getY(), 64, 64);
        }
      }  
      if (displayPathTiles && !pathFinder.pathTiles.isEmpty()) {
        g2d.setStroke(stroke2);
        g2d.setColor(Color.BLUE);
        for (int i =0;i <pathFinder.pathTiles.size()-1;i++) {
          Tile temp = (Tile) pathFinder.pathTiles.get(i);
          g2d.drawRect(temp.getX(), temp.getY(),64,64);
        }
        g2d.setStroke(stroke1);
        g2d.setColor(Color.black);
      }
      if (displayPath && pathFinder.getPathShape() != null) {
        g2d.setStroke(stroke2);
        g2d.draw(pathFinder.getPathShape());
        g2d.setStroke(stroke1);
      }
    }

  }
}
 
