import com.sun.istack.internal.Nullable;

import java.awt.*;
import java.awt.event.MouseEvent;

public class EditorMap extends MapBase {

    private int click = 0, firstX, firstY, secondX, secondY;
    private boolean toIgnore;

    public EditorMap(GamePanel gp, int pMapSizeX, int pMapSizeY, TileSet pTileSet) {
        super(gp, pTileSet, null, null);
        setMapSizeX(pMapSizeX);
        setMapSizeY(pMapSizeY);
        toIgnore = false;
    }


    public void createEditorMap(@Nullable Integer iD) {
        createBaseMap();
        if (iD == null) {
            iD = 22;
        }
        int i = 2;
        for (int zeile = 0; zeile < mapTiles.length; zeile++) {
            for (int spalte = 0; spalte < mapTiles[zeile].length; spalte++) {
                mapTiles[zeile][spalte] = tileSet.tileSet[iD].clone();
                mapTiles[zeile][spalte].setID(iD);
                mapTiles[zeile][spalte].setOpaque(false);
                i++;
            }
        }
    }

    public void createBlankMap() {
        createBaseMap();
        int i = 2;
        mapTiles = new Tile[mapSizeX][mapSizeY];
        for (int zeile = 0; zeile < mapTiles.length; zeile++) {
            for (int spalte = 0; spalte < mapTiles[zeile].length; spalte++) {
                mapTiles[zeile][spalte] = tileSet.tileSet[6].clone();
                mapTiles[zeile][spalte].setID(6);
                mapTiles[zeile][spalte].setOpaque(false);
                i++;
            }
        }
    }

    @Override
    public void renderMap(Graphics2D g2d) {
        super.renderMap(g2d);
    }

    private void setMapTile(int xIndex, int yIndex, TileSet ts) {
        System.out.println("Notfi Map " + toIgnore);
        if (!tileSet.getTileSetImagePath().equals(ts.getTileSetImagePath()) && !toIgnore) {
            Meldungen meld = new Meldungen(gamePanel.getGUI(), true, "null");
            meld.unSimilarTS(this, ts);
            if (tileSet.getTileSetImagePath().equals(meld.selectedTS)) {
                ts = tileSet;
            } else {
                reloadMap(ts);
            }
        }
        tileSet = ts;
        mapTiles[xIndex][yIndex] = tileSet.tileSet[graphicID];
        mapTiles[xIndex][yIndex].setID(graphicID);
    }

    public void setTile(MouseEvent e, TileSet ts) {
        int x = (e.getX() + gamePanel.getCamera().getClickXOffset() + chapterXOffset) / Tile.TILEWIDTH;
        int y = (e.getY() + gamePanel.getCamera().getClickYOffset() + chapterYOffset) / Tile.TILEHEIGHT;
        setMapTile(x, y, ts);
    }

    public void setPointed(MouseEvent e){
        int x = Math.round(e.getX() + gamePanel.getCamera().getClickXOffset() + chapterXOffset) / Tile.TILEWIDTH;
        int y = Math.round(e.getY() + gamePanel.getCamera().getClickYOffset() + chapterYOffset) / Tile.TILEHEIGHT;
        mapTiles[x][y].setPointed();
// FIXME: 05.04.2019 Buggy !! Aber anzeige klappt halbwegs (wenn man markierung removed dann bleibt es im Textfeld

        gamePanel.getGUI().debugAnzeige.append("Markiert unter: " + mapTiles[x][y].getX() / Tile.TILEWIDTH + " und " + mapTiles[x][y].getY() / Tile.TILEHEIGHT + "\n");

    }

    public void setTileRect(MouseEvent e, TileSet ts) {
        System.out.println("Klick ist beim Aufruf" + click);
        click++;
        if (click == 1) {
            firstX = (Math.round(e.getX() + gamePanel.getCamera().getClickXOffset() + chapterXOffset) / Tile.TILEWIDTH);
            firstY = (Math.round(e.getY() + gamePanel.getCamera().getClickYOffset() + chapterYOffset) / Tile.TILEHEIGHT);
            gamePanel.getGUI().debugAnzeige.setText("Fläche zeichnen :" + "\n" + " erster Klick");
        }
        if (click == 2) {
            secondX = Math.round(e.getX() + gamePanel.getCamera().getClickXOffset() + chapterXOffset) / Tile.TILEWIDTH;
            secondY = Math.round(e.getY() + gamePanel.getCamera().getClickYOffset() + chapterYOffset) / Tile.TILEHEIGHT;
            gamePanel.getGUI().debugAnzeige.setText("");

            if (firstX > secondX) {
                int swap;
                swap = firstX;
                firstX = secondX;
                secondX = swap;
            }
            if (firstY > secondY) {
                int swap = firstY;
                firstY = secondY;
                secondY = swap;
            }
            for (int x = firstX; x < secondX + 1; x++) {
                for (int y = firstY; y < secondY + 1; y++) {
                    setMapTile(x, y, ts);
                }
            }
            click = 0;
        }

    }

    public void reloadMap(TileSet set) {
        for (int zeile = 0; zeile < mapTiles.length; zeile++) {
            for (int spalte = 0; spalte < mapTiles[zeile].length; spalte++) {
                mapTiles[zeile][spalte] = set.tileSet[mapTiles[zeile][spalte].id].clone();
            }
        }
    }

    public void setTileSet(TileSet tileSet) {
        this.tileSet = tileSet;
    }

    public boolean isToIgnore() {
        return toIgnore;
    }

    public void setToIgnore(boolean toIgnore) {
        this.toIgnore = toIgnore;
    }


    public void setGraphicID(int graphicID) {
        this.graphicID = graphicID;
    }

    public void setClick(int click) {
        this.click = click;
    }

    public Point getChapterOffset() {
        return new Point(chapterXOffset, chapterYOffset);
    }
}
