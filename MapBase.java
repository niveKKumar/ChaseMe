import com.sun.istack.internal.Nullable;

import java.awt.*;
import java.util.LinkedList;

public class MapBase {
    protected static GUI gui;
    public Tile[][] mapTiles;
    public int mapSizeX;
    public int mapSizeY;
    protected TileSet tileSet;
    protected int graphicID = 22;
    protected int chapterXOffset, chapterYOffset;
    protected String mapStatus;
    private int[] blockedID;
    private boolean active;

    public MapBase(GUI pGUI, TileSet pTileSet, @Nullable String pStatus, @Nullable Point pChapterOffset) {
        gui = pGUI;
        tileSet = pTileSet;
        mapStatus = pStatus;
        if (pStatus == null) {
            mapStatus = "null";
        } else {
            mapStatus = pStatus;
        }
        if (pChapterOffset == null) {
            chapterYOffset = 0;
            chapterXOffset = 0;
        } else {
            chapterXOffset = (int) pChapterOffset.getX() * Tile.TILEWIDTH;
            chapterYOffset = (int) pChapterOffset.getY() * Tile.TILEHEIGHT;
        }
    }


    public void createBaseMap() {
        Tile.setTILEHEIGHT(64);
        Tile.setTILEWIDTH(64);

        mapTiles = new Tile[mapSizeX][mapSizeY];
        int i = 2;
        for (int zeile = 0; zeile < mapSizeX; zeile++) {
            for (int spalte = 0; spalte < mapSizeY; spalte++) {
                mapTiles[zeile][spalte] = tileSet.tileSet[22].clone();
                mapTiles[zeile][spalte].setID(22);
                mapTiles[zeile][spalte].setFocusable(false);
                i++;
            }
        }
        setBorderOrItemsBlocked();
        setNeighbours();
    }


    public void setBorderOrItemsBlocked() {
        if (mapStatus.equals("null")) {
            System.out.println("No Blocking");
        }
        if (mapStatus.equals("Item")) {
            itemBlock();
        }
        if (mapStatus.equals("Border")) {
            borderBlock();
        }
        if (mapStatus.equals("All")) {
            itemBlock();
            borderBlock();
        }
    }

    public void borderBlock() {
        //Oben:
        for (int i = 0; i < mapSizeX - 1; i++) {
            mapTiles[0][i].setBlocked(true);
        }
        //Unten:
        for (int i = 0; i < mapSizeX - 1; i++) {
            mapTiles[mapSizeY - 1][i].setBlocked(true);
        }
        //Links:
        for (int i = 0; i < mapSizeY - 1; i++) {
            mapTiles[i][0].setBlocked(true);
        }
        //Rechts:
        for (int i = 0; i < mapSizeY - 1; i++) {
            mapTiles[i][mapSizeY - 1].setBlocked(true);
        }
    }

    public void itemBlock() {
        LinkedList<Integer> blockedIDs = new LinkedList<Integer>();
        blockedID = new int[]{01, 06, 12, 14, 24, 25, 42, 58, 94, 115, 116, 117, 127, 129, 139, 140, 141, 178, 179, 195};

        for (int i = 0; i < blockedID.length; i++) {
            blockedIDs.add(blockedID[i]);
        }

        for (int zeile = 0; zeile < mapSizeX; zeile++) {
            for (int spalte = 0; spalte < mapSizeY; spalte++) {
                for (int i = 0; i < blockedIDs.size(); i++) {
                    if (mapTiles[spalte][zeile].getID() == blockedIDs.get(i)) {
                        mapTiles[spalte][zeile].setBlocked(true);
                    }
                }
            }
        }
    }

    public boolean isActiveInPosition(Point position) {
        active = position.getX() <= (getMapSizeX() * Tile.TILEWIDTH) && position.getY() <= (getMapSizeY() * Tile.TILEHEIGHT);
        return active;
    }

    public void renderMap(Graphics2D g2d) {
        for (int zeile = 0; zeile < mapSizeY; zeile++) {
            for (int spalte = 0; spalte < mapSizeX; spalte++) {
                mapTiles[zeile][spalte].renderTile(g2d, zeile * Tile.TILEWIDTH - EditorMap.gui.getCamera().getXOffset() - chapterXOffset, spalte * Tile.TILEHEIGHT - EditorMap.gui.getCamera().getYOffset() - chapterYOffset);
            }

        }
    }

    public void setNeighbours() {
        for (int zeile = 0; zeile < mapTiles.length; zeile++) {
            for (int spalte = 0; spalte < mapTiles.length; spalte++) {
                LinkedList temp = new LinkedList();
                try {
                    temp.addFirst(mapTiles[zeile - 1][spalte - 1]);
                } catch (Exception e) {
                }
                try {
                    temp.addFirst(mapTiles[zeile][spalte - 1]);
                } catch (Exception e) {
                }
                try {
                    temp.addFirst(mapTiles[zeile + 1][spalte - 1]);
                } catch (Exception e) {
                }
                try {
                    temp.addFirst(mapTiles[zeile - 1][spalte]);
                } catch (Exception e) {
                }
                try {
                    temp.addFirst(mapTiles[zeile + 1][spalte]);
                } catch (Exception e) {
                }
                try {
                    temp.addFirst(mapTiles[zeile - 1][spalte + 1]);
                } catch (Exception e) {
                }
                try {
                    temp.addFirst(mapTiles[zeile][spalte + 1]);
                } catch (Exception e) {
                }
                try {
                    temp.addFirst(mapTiles[zeile + 1][spalte]);
                } catch (Exception e) {
                }
                mapTiles[spalte][zeile].setNeighbours(temp);
            } // end of for
        } // end of for
    }

    public int getMapSizeY() {
        return mapSizeY;
    }

    public void setMapSizeY(int mapSizeY) {
        this.mapSizeY = mapSizeY;
    }

    public int getMapSizeX() {
        return mapSizeX;
    }

    public void setMapSizeX(int mapSizeX) {
        this.mapSizeX = mapSizeX;
    }

    public int getGraphicID() {
        return graphicID;
    }

    public TileSet getTileSet() {
        return tileSet;
    }

    public void setchapterXOffset(int pChapterOffset) {
        chapterXOffset = pChapterOffset * Tile.TILEWIDTH;
    }

    public void setchapterYOffset(int pChapterOffset) {
        chapterYOffset = pChapterOffset * Tile.TILEHEIGHT;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
