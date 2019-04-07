import com.sun.istack.internal.Nullable;

import java.awt.*;
import java.awt.event.MouseEvent;

public class EditorMap extends MapBase {

    private int click = 0, firstX, firstY, secondX, secondY;

    public EditorMap(GUI pGUI, int pMapSizeX, int pMapSizeY, TileSet pTileSet) {
        super(pGUI, pTileSet, null, null);
        setMapSizeX(pMapSizeX);
        setMapSizeY(pMapSizeY);
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

    public void createBlankEditorMap() {
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

    public void setTile(MouseEvent e) {
        int x = Math.round(e.getX() + gui.getCamera().getXOffset() + chapterXOffset) / Tile.TILEWIDTH;
        int y = Math.round(e.getY() + gui.getCamera().getYOffset() + chapterYOffset) / Tile.TILEHEIGHT;
        mapTiles[x][y] = tileSet.tileSet[graphicID];
        mapTiles[x][y].setID(graphicID);
        gui.taAnzeige.setText("");
    }

    public void setPointed(MouseEvent e){
        int x = Math.round(e.getX() + gui.getCamera().getXOffset() + chapterXOffset) / Tile.TILEWIDTH;
        int y = Math.round(e.getY() + gui.getCamera().getYOffset() + chapterYOffset) / Tile.TILEHEIGHT;
        mapTiles[x][y].setPointed();
// FIXME: 05.04.2019 Buggy !! Aber anzeige klappt halbwegs (wenn man markierung removed dann bleibt es im Textfeld

        gui.taAnzeige.append("Markiert unter: " + mapTiles[x][y].getX() / Tile.TILEWIDTH + " und " + mapTiles[x][y].getY() / Tile.TILEHEIGHT + "\n");

    }

    public void setTileRect(MouseEvent e) {
        System.out.println("Klick ist beim Aufruf" + click);
        click++;
        if (click == 1) {
            firstX = (Math.round(e.getX() + gui.getCamera().getXOffset() + chapterXOffset) / Tile.TILEWIDTH);
            firstY = (Math.round(e.getY() + gui.getCamera().getYOffset() + chapterYOffset) / Tile.TILEHEIGHT);
            gui.taAnzeige.setText("Fläche zeichnen :"+"\n"+" erster Klick");
        }
        if (click == 2) {
            secondX = Math.round(e.getX() + gui.getCamera().getXOffset() + chapterXOffset) / Tile.TILEWIDTH;
            secondY = Math.round(e.getY() + gui.getCamera().getYOffset() + chapterYOffset) / Tile.TILEHEIGHT;
            gui.taAnzeige.setText("");

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
            for (int zeile = firstX; zeile < secondX + 1; zeile++) {
                for (int spalte = firstY; spalte < secondY + 1; spalte++) {
                    mapTiles[zeile][spalte] = tileSet.tileSet[graphicID];
                }
            }
            click = 0;
        }

    }


    public void setTileSet(TileSet tileSet) {
        this.tileSet = tileSet;
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
