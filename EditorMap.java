import java.awt.*;
import java.awt.event.MouseEvent;

public class EditorMap extends MapBase {

    private int click = 0, firstX, firstY, secondX, secondY;

    public EditorMap(GUI pGUI, int pMapSizeX, int pMapSizeY, TileSet pTileSet) {
        super(pGUI, pTileSet, null, null);
        setMapSizeX(pMapSizeX);
        setMapSizeY(pMapSizeY);
    }

    public void createEditorMap() {
        createBaseMap();
    }

    public void createBlankEditorMap() {
        int i = 2;
        mapTiles = new Tile[mapSizeX][mapSizeY];
        for (int zeile = 0; zeile < mapSizeX; zeile++) {
            for (int spalte = 0; spalte < mapSizeY; spalte++) {
                mapTiles[spalte][zeile] = tileSet.tileSet[6].clone();
                mapTiles[spalte][zeile].setID(6);
                mapTiles[spalte][zeile].setOpaque(false);
                i++;
            }
        }
    }

    public void setTile(MouseEvent e) {
        int x = Math.round(e.getX() + gui.getCamera().getXOffset() + chapterXOffset) / Tile.TILEWIDTH;
        int y = Math.round(e.getY() + gui.getCamera().getYOffset() + chapterYOffset) / Tile.TILEHEIGHT;
        mapTiles[x][y] = tileSet.tileSet[graphicID];
        mapTiles[x][y].setID(graphicID);
            }
            public void setPointed(MouseEvent e){
                System.out.println("pointed ausgeführt");
                int x = Math.round(e.getX() + gui.getCamera().getXOffset() + chapterXOffset) / Tile.TILEWIDTH;
                int y = Math.round(e.getY() + gui.getCamera().getYOffset() + chapterYOffset) / Tile.TILEHEIGHT;
                    if (!mapTiles[x][y].isPointed()) {
                        mapTiles[x][y].setPointed(true);
                    }else {
                        mapTiles[x][y].setPointed(false);
                    }

                for (int zeile = 0; zeile < mapSizeX; zeile++) {
                    for (int spalte = 0; spalte < mapSizeY; spalte++) {
                        if (mapTiles[zeile][spalte].isPointed()) {
                            gui.taAnzeige.append("Markiert unter: " + mapTiles[zeile][spalte].getX() / Tile.TILEWIDTH + " und " + mapTiles[zeile][spalte].getY() / Tile.TILEHEIGHT + "\n");
                        }
                    }
                }
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
                        gui.taAnzeige.setText("Fläche zeichnen :"+ "\n"+"zweiter Klick");

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
                    }else {
                    setTile(e);
                }

            }



    public void setTileSet(TileSet tileSet) {
        this.tileSet = tileSet;
    }

    public void setGraphicID(int graphicID) {
        this.graphicID = graphicID;
    }

    public Point getChapterOffset() {
        return new Point(chapterXOffset, chapterYOffset);
    }
}
