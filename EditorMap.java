import com.sun.istack.internal.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

public class EditorMap extends MapBase {

    private int click = 0;
    private Point firstClick, secondClick;
    private JTextArea editorAnzeige;
    private boolean toIgnore;
    private Color color;
    private int strokeSize = 10;
    private boolean showMapBorder = true;


    public EditorMap(GamePanel gp, int pMapSizeX, int pMapSizeY, TileSet pTileSet) {
        super(gp, pTileSet, null, null);
        setMapSizeX(pMapSizeX);
        setMapSizeY(pMapSizeY);
        editorAnzeige = new JTextArea("EditorAnzeige");
        toIgnore = false;
        Random r = new Random();
        color = new Color(r.nextFloat(), r.nextFloat(), r.nextFloat());
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
        if (showMapBorder) {
            setMapBorder(5);
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
        if (showMapBorder) {
            setMapBorder(5);
        }
    }

    public void setMapBorder(int stroke) {
        //Oben:
        for (int i = 1; i < mapSizeX; i++) {
            mapTiles[i][0].setBorderInsets(new Insets(stroke, 0, 0, 0));
        }
        //Unten:
        for (int i = 1; i < mapSizeX; i++) {
            mapTiles[i][mapSizeY - 1].setBorderInsets(new Insets(0, 0, stroke, 0));
//            mapTiles[i][mapSizeY - 1].setDownBorder(true);

        }
        //Links:
        for (int i = 1; i < mapSizeY; i++) {
            mapTiles[0][i].setBorderInsets(new Insets(0, stroke, 0, 0));
//            mapTiles[0][i].setLeftBorder(true);

        }
        //Rechts:
        for (int i = 1; i < mapSizeY; i++) {
            mapTiles[mapSizeX - 1][i].setBorderInsets(new Insets(0, 0, 0, stroke));
//            mapTiles[mapSizeX - 1][i].setRightBorder(true);
        }
        mapTiles[0][0].setBorderInsets(new Insets(stroke, stroke, 0, 0));
        mapTiles[mapSizeX - 1][0].setBorderInsets(new Insets(stroke, 0, 0, stroke));
        mapTiles[0][mapSizeY - 1].setBorderInsets(new Insets(0, stroke, stroke, 0));
        mapTiles[mapSizeX - 1][mapSizeY - 1].setBorderInsets(new Insets(0, 0, stroke, stroke));
    }

    @Override
    public void renderMap(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(strokeSize));
        for (int zeile = 0; zeile < mapSizeY; zeile++) {
            for (int spalte = 0; spalte < mapSizeX; spalte++) {
                mapTiles[zeile][spalte].renderTile(g2d, chapterXOffset + zeile * Tile.TILEWIDTH - gamePanel.getCamera().getClickXOffset(), chapterYOffset + spalte * Tile.TILEHEIGHT - gamePanel.getCamera().getClickYOffset());
            }
        }
    }

    private void setMapTile(int xIndex, int yIndex, TileSet ts) {
        if (!toIgnore) {
            if (graphicID != 9999 /*Ausnahme*/ && !tileSet.getTileSetImagePath().equals(ts.getTileSetImagePath())) {
                Meldungen meld = new Meldungen(null, true, "null");
                meld.unSimilarTS(this, ts);
                tileSet = meld.selectedTS;
                reloadMap(tileSet);
            }
        } else {
            tileSet = ts;
        }

        if (xIndex <= mapSizeX && yIndex <= mapSizeY) {
            Insets tempInsets = null;
            tempInsets = mapTiles[xIndex][yIndex].getBorderInsets();
            if (graphicID == 9999) {
                mapTiles[xIndex][yIndex] = new Tile(new BufferedImage(Tile.TILEWIDTH, Tile.TILEHEIGHT, BufferedImage.TYPE_4BYTE_ABGR)).clone();
            } else {
                mapTiles[xIndex][yIndex] = tileSet.tileSet[graphicID].clone();
            }
            mapTiles[xIndex][yIndex].setBorderInsets(tempInsets);
            mapTiles[xIndex][yIndex].setID(graphicID);
        } else {
            new JOptionPane("Klick ist außerhalb der Map: " + xIndex + " || " + yIndex);
        }
        mapTiles[xIndex][yIndex].revalidate();

    }

    public Point getTileID(MouseEvent e) {
        int x = Math.round(e.getX() + gamePanel.getCamera().getClickXOffset() - chapterXOffset) / Tile.TILEWIDTH;
        int y = Math.round(e.getY() + gamePanel.getCamera().getClickYOffset() - chapterYOffset) / Tile.TILEHEIGHT;
        return new Point(x, y);
    }

    public void setTile(MouseEvent e, TileSet ts) {
        Point click = getTileID(e);
        setMapTile(click.x, click.y, ts);
    }

    public void setTileRect(MouseEvent e, TileSet ts) {
        click++;
        if (click == 1) {
            firstClick = getTileID(e);
            editorAnzeige.setText("Fläche zeichnen :" + "\n" + " erster Klick");
        }
        if (click == 2) {
            secondClick = getTileID(e);
            editorAnzeige.setText("");

            if (firstClick.getX() > secondClick.getX()) {
                int swap;
                swap = (int) firstClick.getX();
                firstClick.x = (int) secondClick.getX();
                secondClick.x = swap;
            }
            if (firstClick.getY() > secondClick.getY()) {
                int swap = (int) firstClick.getY();
                firstClick.y = (int) secondClick.getY();
                secondClick.y = swap;
            }
            for (int x = (int) firstClick.getX(); x < secondClick.getX() + 1; x++) {
                for (int y = (int) firstClick.getY(); y < secondClick.getY() + 1; y++) {
                    setMapTile(x, y, ts);
                }
            }
            click = 0;
        }

    }

    public void reloadMap(TileSet set) {
        for (int zeile = 0; zeile < mapTiles.length; zeile++) {
            for (int spalte = 0; spalte < mapTiles[zeile].length; spalte++) {
                if (mapTiles[zeile][spalte].id == 9999) {
                    mapTiles[zeile][spalte] = new Tile(new BufferedImage(Tile.TILEWIDTH, Tile.TILEHEIGHT, BufferedImage.TYPE_4BYTE_ABGR));
                } else {
                    mapTiles[zeile][spalte] = set.tileSet[mapTiles[zeile][spalte].id].clone();
                }
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
