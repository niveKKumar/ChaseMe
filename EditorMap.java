import com.sun.istack.internal.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Random;

public class EditorMap extends MapBase {

    private int click = 0, firstX, firstY, secondX, secondY;
    private JTextArea editorAnzeige;
    private boolean toIgnore;
    private Color color;
    private int strokeSize = 10;

    private JLabel test = new JLabel();

    public EditorMap(GamePanel gp, int pMapSizeX, int pMapSizeY, TileSet pTileSet) {
        super(gp, pTileSet, null, null);
        setMapSizeX(pMapSizeX);
        setMapSizeY(pMapSizeY);
        GUI.addToEast(test);
        editorAnzeige = new JTextArea("EditorAnzeige");
        toIgnore = false;
        Random rand = new Random();
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        color = new Color(r, g, b);
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
        setMapBorder();
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

    public void setMapBorder() {
        int stroke = 10;
        //Oben:
        for (int i = 0; i < mapSizeX - 1; i++) {
            mapTiles[i][0].setBorderInsets(new Insets(stroke, 0, 0, 0));

        }
        //Unten:
        for (int i = 0; i < mapSizeX - 1; i++) {
            mapTiles[i][mapSizeY - 1].setBorderInsets(new Insets(0, 0, stroke, 0));
//            mapTiles[i][mapSizeY - 1].setDownBorder(true);

        }
        //Links:
        for (int i = 0; i < mapSizeY - 1; i++) {
            mapTiles[0][i].setBorderInsets(new Insets(0, stroke, 0, 0));
//            mapTiles[0][i].setLeftBorder(true);

        }
        //Rechts:
        for (int i = 0; i < mapSizeY - 1; i++) {
            mapTiles[mapSizeY - 1][i].setBorderInsets(new Insets(0, 0, 0, stroke));
//            mapTiles[mapSizeX - 1][i].setRightBorder(true);
        }
    }

    @Override
    public void renderMap(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(strokeSize));
        super.renderMap(g2d);

    }

    private void setMapTile(int xIndex, int yIndex, TileSet ts) {
        if (!toIgnore) {
            if (!tileSet.getTileSetImagePath().equals(ts.getTileSetImagePath())) {
                Meldungen meld = new Meldungen(null, true, "null");
                meld.unSimilarTS(this, ts);
                tileSet = meld.selectedTS;
                reloadMap(tileSet);
            }
        } else {
            tileSet = ts;
        }

        if (xIndex <= mapSizeX && yIndex <= mapSizeY) {
            mapTiles[xIndex][yIndex] = tileSet.tileSet[graphicID];
            mapTiles[xIndex][yIndex].setID(graphicID);
        } else {
            new JOptionPane("Klick ist außerhalb der Map: " + xIndex + " || " + yIndex);
        }
        test.setText("Set Map Tile on " + xIndex + " || " + yIndex);
    }

    public void setTile(MouseEvent e, TileSet ts) {
        int x = (e.getX() + gamePanel.getCamera().getXOffset() - chapterXOffset) / Tile.TILEWIDTH;
        int y = (e.getY() + gamePanel.getCamera().getYOffset() - chapterYOffset) / Tile.TILEHEIGHT;
        setMapTile(x, y, ts);
    }

    public void setTileRect(MouseEvent e, TileSet ts) {
        System.out.println("Klick ist beim Aufruf" + click);
        click++;
        if (click == 1) {
            firstX = (Math.round(e.getX() + gamePanel.getCamera().getXOffset() - chapterXOffset) / Tile.TILEWIDTH);
            firstY = (Math.round(e.getY() + gamePanel.getCamera().getYOffset() - chapterYOffset) / Tile.TILEHEIGHT);
            editorAnzeige.setText("Fläche zeichnen :" + "\n" + " erster Klick");
        }
        if (click == 2) {
            secondX = Math.round(e.getX() + gamePanel.getCamera().getXOffset() - chapterXOffset) / Tile.TILEWIDTH;
            secondY = Math.round(e.getY() + gamePanel.getCamera().getYOffset() - chapterYOffset) / Tile.TILEHEIGHT;
            editorAnzeige.setText("");

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

}
