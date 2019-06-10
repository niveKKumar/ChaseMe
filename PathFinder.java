import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.LinkedList;


public class PathFinder {                                                                                                                                                                                                                       //mm
    public LinkedList<Tile> pathTiles = new LinkedList<Tile>();
    private Path2D pathShape;
    private MapBase map;
    private Mover mover;
    private LinkedList<Tile> closedList = new LinkedList<Tile>();
    private PriorityList openList = new PriorityList();
    private LinkedList<Point2D> pointsOnPath = new LinkedList<Point2D>();
    private GamePanel gp;


    public PathFinder(MapBase pMap, Mover pMover, GamePanel gamePanel) {
        map = pMap;
        mover = pMover;
        gp = gamePanel;
    }

    public Shape getPathShape() {
        return pathShape;
    }

    private void setBlockedTilesToClosedList() {
        for (int i = 0; i < map.mapTiles.length; i++) {
            for (int j = 0; j < map.mapTiles.length; j++) {
                if (map.mapTiles[i][j].isBlocked()) {
                    closedList.add(map.mapTiles[i][j]);
                } // end of if
            } // end of for
        }
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
                float costFromStart = current.costFromStart + current.getCostForNextStep(neighbourTile);

                if (!isOnOpen && !isOnClosed || costFromStart < neighbourTile.costFromStart) {
                    neighbourTile.setEstimatedCostToGoal(pTarget);
                    neighbourTile.costFromStart = costFromStart;
                    neighbourTile.pathParent = current;

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
        pathShape.moveTo(mover.getLocation().getX() + 64 / 2, mover.getLocation().getY() + 64 / 2);
        Tile temp;
        for (int i = 0; i < pathTiles.size(); i++) {
            temp = pathTiles.get(i);
            pathShape.lineTo(temp.getX() + Tile.TILEWIDTH / 2 + gp.getCamera().getXOffset(), temp.getY() + Tile.TILEHEIGHT / 2 + gp.getCamera().getYOffset());
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
                    Point2D p1 = pointsOnPath.get(pointsOnPath.size() - 1);
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

    public void resetPath() {
        pointsOnPath.clear();
    }

    public Point2D getNextStep() {
        return pointsOnPath.pollFirst();
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
} // end of for
