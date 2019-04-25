import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;

public class Map extends MapBase {

  private String[] mapText;

    public Map(GamePanel gamePanel, TileSet pTileSet, String pMapPlanPath, String pStatus, Point pChapterOffset) {
        super(gamePanel, pTileSet, pStatus, pChapterOffset);
    mapText = readMap(pMapPlanPath);
    createMap();
    }

    public Map(GamePanel gp, String pMapPlanPath, String pStatus, Point pChapterOffset) {
        super(gp, null, pStatus, pChapterOffset);
    mapText = readMap(pMapPlanPath);
    System.out.println("Konnte Pfad lesen ! " + mapText[0]);
    tileSet = new TileSet(mapText[0]);
    System.out.println("tileSet auch geladen! " + tileSet.tileSet.length);
    createMap();
  }

  public void createMap() {
    Tile.setTILEHEIGHT(64);
    Tile.setTILEWIDTH(64);

    mapSizeX = Integer.parseInt(mapText[1]);
    mapSizeY = Integer.parseInt(mapText[2]);
    mapTiles = new Tile[mapSizeX][mapSizeY];
    int i = 3;
      for (int zeile = 0; zeile < mapTiles.length; zeile++) {
          for (int spalte = 0; spalte < mapTiles[zeile].length; spalte++) {
              mapTiles[zeile][spalte] = tileSet.tileSet[Integer.parseInt(mapText[i])].clone();
              mapTiles[zeile][spalte].setID(Integer.parseInt(mapText[i]));
            i++;
      }
    }
    setBorderOrItemsBlocked();
    setNeighbours();
  }

  public String[] readMap(String pMapPlanPath) {
    String mapString;
    String[] temp = null;
    try {
      //Richtige Konvertierung des PFades-> Obwohl nicht nötig
      BufferedReader br = new BufferedReader(new FileReader(pMapPlanPath));
      String line = br.readLine();
      mapString = line;
      while ((line = br.readLine()) != null) {
        mapString = mapString + ";" + line;
      }
      temp = mapString.split(";");
    } catch (Exception e) {
      System.out.println("LESEFEHLER");
    }
    return temp;
  }
}
