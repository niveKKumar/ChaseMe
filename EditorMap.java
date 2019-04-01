import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.*;

public class EditorMap{

    private TileSet tileSet;
    public int mapSizeX, mapSizeY;
    public Tile[][] mapTiles;
    private static GUI gui;
    private int graphicID = 22;
    private int click = 0, firstX, firstY, secondX, secondY;

    public EditorMap(GUI pGUI,int pMapSizeX,int pMapSizeY,TileSet pTileSet ){
        gui = pGUI;
        mapSizeX = pMapSizeX;
        mapSizeY = pMapSizeY;
        tileSet = pTileSet;
        createMap();
    }
    public void createMap() {
        mapTiles = new Tile[mapSizeX][mapSizeY];
        int i = 2;
        for (int zeile = 0; zeile < mapSizeX; zeile++) {
            for (int spalte = 0; spalte < mapSizeY; spalte++) {
                mapTiles[spalte][zeile] = tileSet.tileSet[graphicID].clone();
                mapTiles[spalte][zeile].setID(graphicID);
                i++;
            }
        }
        mapTiles[mapSizeX/2] [mapSizeY/2] = tileSet.tileSet[6].clone();
        gui.camera = new Camera(mapSizeX,mapSizeY);

    }
    public void createBlankEditiorMap(){
        mapTiles = new Tile[mapSizeX][mapSizeY];
        int i = 2;
        for (int zeile = 0; zeile < mapSizeX; zeile++) {
            for (int spalte = 0; spalte < mapSizeY; spalte++) {
                mapTiles[spalte][zeile] = tileSet.tileSet[6].clone();
                mapTiles[spalte][zeile].setID(6);
                i++;
            }
        }
    }

    public void renderMap(Graphics2D g2d){
        for (int zeile = 0; zeile < mapSizeY; zeile++) {
            for (int spalte = 0; spalte < mapSizeX; spalte++) {
                mapTiles[spalte][zeile].renderTile(g2d, zeile * Tile.TILEWIDTH - gui.getCamera().getXOffset(), spalte * Tile.TILEHEIGHT - gui.getCamera().getYOffset());
                if (mapTiles[spalte][zeile].isPointed()){
                    g2d.setStroke(new BasicStroke(3 , BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND ));
                    g2d.setColor(Color.black);
                    g2d.drawRect(mapTiles [spalte] [zeile].getX(), mapTiles [zeile] [spalte].getY(), Tile.TILEWIDTH, Tile.TILEHEIGHT);}
            }
        }
    }
            public void setTile(MouseEvent e) {
                int x = Math.round(e.getX() + gui.getCamera().getXOffset()) / Tile.TILEWIDTH;
                int y = Math.round(e.getY() + gui.getCamera().getYOffset()) / Tile.TILEHEIGHT;
                mapTiles[y][x] = tileSet.tileSet[graphicID];
                mapTiles[y][x].setID(graphicID);
            }
            public void selectTile(MouseEvent e){
                int x = Math.round(e.getX() + gui.getCamera().getXOffset()) / Tile.TILEWIDTH;
                int y = Math.round(e.getY() + gui.getCamera().getYOffset()) / Tile.TILEHEIGHT;
            }
            public void setPointed(MouseEvent e){
                    int x = Math.round(e.getX() + gui.getCamera().getXOffset()) / Tile.TILEWIDTH;
                    int y = Math.round(e.getY() + gui.getCamera().getYOffset()) / Tile.TILEHEIGHT;
                    gui.taAnzeige.append("Markiert unter: " + x +" und " + y +"\n");
                    if (!mapTiles[x][y].isPointed()) {
                        mapTiles[x][y].setPointed(true);
                    }else {
                        mapTiles[x][y].setPointed(false);
                    }
            }
            public void setTileRect(MouseEvent e) {
                    click++;
                    if (click == 1) {
                        firstX = (Math.round(e.getX() + gui.getCamera().getXOffset()) / Tile.TILEWIDTH);
                        firstY = (Math.round(e.getY() + gui.getCamera().getYOffset()) / Tile.TILEHEIGHT);
                        gui.taAnzeige.setText("Fläche zeichnen :"+"\n"+" erster Klick");
                    }
                    if (click == 2) {
                        secondX = Math.round(e.getX() + gui.getCamera().getXOffset()) / Tile.TILEWIDTH;
                        secondY = Math.round(e.getY() + gui.getCamera().getYOffset()) / Tile.TILEHEIGHT;
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
                                mapTiles[spalte][zeile] = tileSet.tileSet[graphicID];
                            }
                            click = 0;
                        }
                    }else {
                    gui.taAnzeige.setText("");
                    click = 0;
                    setTile(e);
                }

            }


    // GET/SET METHODEN:

    public void setTileSet(TileSet tileSet) {
        this.tileSet = tileSet;
    }

    public void setGraphicID(int graphicID) {
        this.graphicID = graphicID;
    }

    public int getMapSizeY() {
        return mapSizeY;
    }

    public int getMapSizeX() {
        return mapSizeX;
    }

    public int getGraphicID() {
        return graphicID;
    }
}
