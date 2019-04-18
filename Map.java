import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;

public class Map extends MapBase {
  private String[] mapText;

  public Map(GUI pGUI, TileSet pTileSet, String pMapPlanPath, String pStatus, Point pChapterOffset) {
    super(pGUI, pTileSet, pStatus, pChapterOffset);
    mapText = readMap(pMapPlanPath);
    createMap();
    }

  public void createMap() {
    Tile.setTILEHEIGHT(64);
    Tile.setTILEWIDTH(64);


    mapSizeX = Integer.parseInt(mapText[0]);
    mapSizeY = Integer.parseInt(mapText[1]);

    mapTiles = new Tile[mapSizeX][mapSizeY];
    int i = 2;
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
      BufferedReader br = new BufferedReader(new FileReader(pMapPlanPath));
      String line = br.readLine();
      mapString = line;
      while ((line = br.readLine()) != null) {
        mapString = mapString + " " + line;
      }
      temp = mapString.split("\\s+");
    } catch (Exception e) {
      System.out.println("LESEFEHLER");
    }
    return temp;
  }

    public MapBase MapToMapBase() {
        MapBase convert = new MapBase(gui, tileSet, mapStatus, new Point(chapterXOffset, chapterYOffset));
        convert.mapTiles = mapTiles;
        return convert;
    }
}
