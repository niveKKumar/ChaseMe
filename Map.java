import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;

public class Map extends MapBase {

    /**
     * Map Klasse die für Spielmaps geeignet ist
     * -> kann eingelesen werden
     * -> Spiel Objekte können konfiguriert werden
     */

    private String[] mapText;

    /**
     * Map Klasse die für Spielmaps geeignet ist
     * Konstruktor für Maps ohne Verknüpfung  eines TileSets
     * -> manuelle Eingabe des TileSets,Mappfads,Status und Offset
     */
    public Map(GamePanel gp, TileSet pTileSet, String pMapPlanPath, String pStatus, Point pChapterOffset) {
        super(gp, pTileSet, pStatus, pChapterOffset);
        mapText = readMap(pMapPlanPath);
        createMap();
    }

    public Map(GamePanel gp, String pMapPlanPath, String pStatus, Point pChapterOffset) {
        super(gp, null, pStatus, pChapterOffset);
        mapText = readMap(pMapPlanPath);
        tileSet = new TileSet(mapText[0]);
        createMap();
    }

    /**
     * Erstellen der Map mit 64x64 Tiles (können beliebig verändert werden)
     * und der Groesse der Map
     * Tile ID - 9999 ist das universelle durchsichtige Tile
     */
    public void createMap() {
        Tile.setTILEHEIGHT(64);
        Tile.setTILEWIDTH(64);

        mapSizeX = Integer.parseInt(mapText[1]);
        mapSizeY = Integer.parseInt(mapText[2]);
        mapTiles = new Tile[mapSizeX][mapSizeY];
        int i = 3;
        for (int zeile = 0; zeile < mapTiles.length; zeile++) {
            for (int spalte = 0; spalte < mapTiles[zeile].length; spalte++) {
                if (Integer.parseInt(mapText[i]) != 9999) {
                    mapTiles[zeile][spalte] = tileSet.tileSet[Integer.parseInt(mapText[i])].clone();
                } else {
                    Tile emptyTile = new Tile(new BufferedImage(Tile.TILEWIDTH, Tile.TILEHEIGHT, BufferedImage.TYPE_INT_ARGB_PRE));
                    mapTiles[zeile][spalte] = emptyTile.clone();
                }
                mapTiles[zeile][spalte].setID(Integer.parseInt(mapText[i]));
                i++;
            }
        }
        setBorderOrItemsBlocked();
    }

    /**
     * Lesen des Mappfads
     */
    public String[] readMap(String pMapPlanPath) {
        String mapString;
        String[] temp = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(pMapPlanPath));
            String line = br.readLine();
            mapString = line;
            while ((line = br.readLine()) != null) {
                mapString = mapString + ";" + line;
            }
            temp = mapString.split(";");
        } catch (Exception e) {
            System.out.println("LESEFEHLER im Dateipfad " + pMapPlanPath);
        }
        return temp;
    }

    /**
     * Tile als Alarmanlage einstellen
     * -> Hier können weitere SpielTiles programmiert werden
     */
    public void setTileAsAlert(int xTileID, int yTileID, int radius) {
        mapTiles[xTileID][yTileID].setDanger(true);
        int stroke = 5;
        if (xTileID - radius >= 0 && xTileID - radius <= mapSizeX - 1
                && xTileID + radius >= 0 && xTileID + radius <= mapSizeX - 1) {
            for (int i = xTileID - radius; i < xTileID + radius + 1; i++) {
                mapTiles[i][yTileID].setDanger(true);
            }
            mapTiles[xTileID - radius][yTileID].setBorderInsets(new Insets(stroke, stroke, stroke, 0));
            mapTiles[xTileID + radius][yTileID].setBorderInsets(new Insets(stroke, 0, stroke, stroke));
        }
        if (yTileID - radius >= 0 && yTileID - radius <= mapSizeY - 1
                && yTileID + radius >= 0 && yTileID + radius <= mapSizeY - 1) {
            for (int i = yTileID - radius; i < yTileID + radius + 1; i++) {
                mapTiles[xTileID][i].setDanger(true);
            }
            mapTiles[xTileID][yTileID - radius].setBorderInsets(new Insets(stroke, stroke, 0, stroke));
            mapTiles[xTileID][yTileID + radius].setBorderInsets(new Insets(0, stroke, stroke, stroke));
        }
        if (radius > 1) {
            for (int i = 1; i < radius; i++) {
                mapTiles[xTileID - radius + i][yTileID - i].setDanger(true);
                mapTiles[xTileID - radius + i][yTileID - i].setBorderInsets(new Insets(stroke, stroke, 0, 0));
                mapTiles[xTileID - radius + i][yTileID + i].setDanger(true);
                mapTiles[xTileID - radius + i][yTileID + i].setBorderInsets(new Insets(0, stroke, stroke, 0));

                mapTiles[xTileID + radius - i][yTileID + i].setDanger(true);
                mapTiles[xTileID + radius - i][yTileID + i].setBorderInsets(new Insets(0, 0, stroke, stroke));
                mapTiles[xTileID + radius - i][yTileID - i].setDanger(true);
                mapTiles[xTileID + radius - i][yTileID - i].setBorderInsets(new Insets(stroke, 0, 0, stroke));
            }
        }
    }
}

