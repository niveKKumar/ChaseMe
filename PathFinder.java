import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.LinkedList;

public class PathFinder{
    public LinkedList pathTiles = new LinkedList();
    private MapBase[] map;
    private Path2D pathShape;
    private Mover mover;
    private LinkedList closedList = new LinkedList();
    private PriorityList openList = new PriorityList();
    private LinkedList pointsOnPath = new LinkedList();
    private GamePanel gamePanel;


    public PathFinder(MapBase[] pMap, Mover pMover, GamePanel gp) {
        map = pMap;
        mover = pMover;
        gamePanel = gp;
    }

    public Shape getPathShape() {
        return pathShape;
    }

    public void searchPath(Tile pStart, Tile pTarget) {
        if (pTarget.isBlocked() == true) {
            return;
        } // end of if
        openList.clear();
        closedList.clear();
        setBlockedTilesToClosedList();
        pStart.costFromStart = 0;
        pStart.pathParent = null;
        pStart.setEstimatedCostToGoal(pTarget);
        openList.add(pStart);
        while (!openList.isEmpty()) {
            Tile current = (Tile) openList.removeFirst();
            if (current == pTarget) {
                constructPathTilesList(pTarget);
                return;
            }
            closedList.add(current);
            LinkedList neighbours = current.getNeighbours();
            for (int i = 0; i < neighbours.size(); i++) {
                Tile neighbourTile = (Tile) neighbours.get(i);
                boolean isOnOpen = openList.contains(neighbourTile);
                boolean isOnClosed = closedList.contains(neighbourTile);
                float costFromStart = current.getCostFromStart() + current.getCostForNextStep(neighbourTile);
                if (!isOnOpen && !isOnClosed || costFromStart < neighbourTile.getCostFromStart()) {
                    neighbourTile.setEstimatedCostToGoal(pTarget);
                    neighbourTile.setCostFromStart(costFromStart);
                    neighbourTile.setPathParent(current);
                    if (isOnClosed) {
                        closedList.remove(neighbourTile);
                    }
                    if (!isOnOpen) {
                        openList.add(neighbourTile);
                    }
                }
            }
        }
    }

    private void setBlockedTilesToClosedList() {
        for (int m = 0; m < map.length; m++) {
            for (int i = 0; i < map[m].mapTiles.length; i++) {
                for (int j = 0; j < map[m].mapTiles[i].length; j++) {
                    if (map[m].mapTiles[i][j].isBlocked()) {
                        closedList.add(map[m].mapTiles[i][j]);
                    } // end of if
                } // end of for
            }

        }//end of for - Map
    }

    private void constructPathTilesList(Tile pTile) {
        pathTiles.clear();
        while (pTile.pathParent != null) {
            pathTiles.addFirst(pTile);
            pTile = pTile.pathParent;
        }
        constructMovingPath();
    }

    private void constructMovingPath() {
        pathShape = new Path2D.Double();
        pathShape.moveTo(mover.getLocation().getX() + Character.MOVER_WIDTH / 2, mover.getLocation().getY() + Character.MOVER_HEIGHT / 2);    ///
        Tile t;
        for (int i = 0; i < pathTiles.size(); i++) {
            t = (Tile) pathTiles.get(i);
            pathShape.lineTo((t.getX() + Tile.TILEHEIGHT / 2) + (gamePanel.getCamera().getXOffset()), (t.getY() + Tile.TILEHEIGHT / 2) + (gamePanel.getCamera().getYOffset()));
        }
        calculateMovingPointsOnPath(pathShape);
    }

    private void calculateMovingPointsOnPath(Shape pPathShape) {
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
                    double i = d / 1.5;   ///Das ist der Faktor, mit dem man die Anzahl der Punkte manipulieren kann!!! Je kleiner, desto mehr Punkte.
                    for (int j = 0; j < i; j++) {
                        Point2D p3 = new Point2D.Double(p1.getX() + (j / i) * (p2.getX() - p1.getX()), p1.getY() + (j / i) * (p2.getY() - p1.getY()));
                        pointsOnPath.add(p3);
                    }
                    break;
            }
            pi.next();
        }
    }

    public void resetPath() {
        pointsOnPath.clear();
    }

    public Point2D getNextStep() {
        return (Point2D) pointsOnPath.pollFirst();
    }

    class PriorityList extends LinkedList {

        public void add(Comparable object) {
            for (int i = 0; i < this.size(); i++) {
                if (object.compareTo(this.get(i)) <= 0) {
                    add(i, object);
                    return;
                }
            }
            addLast(object);
        }
    }

}
