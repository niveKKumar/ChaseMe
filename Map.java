import java.awt.*;
import java.io.*;
import java.io.BufferedReader;
import java.util.*;

public class Map {
  private String mapPlanPath;
  private TileSet tileSet;
  public Tile[][] mapTiles;
  public int mapSizeX;  
  public int mapSizeY;
  private int chapterOffset;
  private GUI gui;
  private String mapStatus; //Map nach ID geblockt
  private boolean active;
  
  
  public Map(GUI pGUI, String pMapPlanPath, TileSet pTileSet, String pStatus, int pChapterOffset ){
    gui = pGUI;
    mapPlanPath = pMapPlanPath;
    tileSet = pTileSet;
    mapStatus = pStatus;
    chapterOffset = pChapterOffset* Tile.TILEWIDTH;
    createMap();
  }

  public void createMap(){
    //Einlesen des "Bauplans":
    String mapString = null;
    try{
      BufferedReader br = new BufferedReader(new FileReader(mapPlanPath));
      String line = br.readLine();
      mapString = line;
      while ((line = br.readLine()) != null) {
        mapString = mapString + " " + line;
      }
    } catch(Exception e){
      System.out.println("LESEFEHLER");
    }
    
    //Erstellen des Arrays mit den Tiles der Map:
//    System.out.println("Splitten der Mapteile");
    String [] temp = mapString.split("\\s+");
    mapSizeX = Integer.parseInt(temp[0]);
    mapSizeY = Integer.parseInt(temp[1]);
    mapTiles = new Tile[mapSizeX][mapSizeY];
//    System.out.println("Erstellen der Map Tiles");
    
    int i = 2;
    for (int zeile=0;zeile < mapSizeX;zeile++) {
      for (int spalte=0;spalte < mapSizeY;spalte++ ) {
        mapTiles[zeile][spalte] = tileSet.tileSet[Integer.parseInt(temp[i])].clone();
        mapTiles[zeile][spalte].setID(Integer.parseInt(temp[i]));
        i++;
      }
    }
//    System.out.println("Map Tiles fertig erstellt");
    setBorderOrItemsBlocked();
    setNeighbours();
  }
  
  public int getMapSizeX(){
    return  mapSizeX;
  }
  public int getMapSizeY(){
    return  mapSizeY;
  }
  public String getMapStatus(){return mapStatus;}
  public boolean isActive() {
    return active;
  }
  public void setActive(boolean i){
    active = i;
  }

  public boolean isActiveInPosition(Point position) {
    boolean active = false;
    if (position.getX() <= (getMapSizeX() *  Tile.TILEWIDTH) && position.getY() <= (getMapSizeY()* Tile.TILEHEIGHT)) {
      active = true;
    }
    return active;
  }

  public void setBorderOrItemsBlocked(){
    if (mapStatus.equals("Empty")){
      System.out.println("Empty");
    }
    if (mapStatus.equals("Item")){
      itemBlock();}
    if (mapStatus.equals("Border")){
      borderBlock();}
    if (mapStatus.equals("All")){
      itemBlock();
      borderBlock();}
  }

  public void borderBlock(){
    //Oben:
    for (int i =0;i< mapSizeX-1;i++) {
    mapTiles[0] [i].setBlocked(true);}
    //Unten:
    for (int i = 0;i < mapSizeX-1;i++) {
    mapTiles[mapSizeY-1] [i].setBlocked(true);}
    //Links:
    for (int i = 0;i< mapSizeY-1;i++) {
    mapTiles[i] [0].setBlocked(true); }
    //Rechts:
    for (int i = 0;i< mapSizeY-1;i++) {
    mapTiles[i][mapSizeY-1].setBlocked(true);}
  }
  public void itemBlock(){
    LinkedList<Integer> blockedIDs = new LinkedList<Integer>();
    blockedIDs.add(01);
    blockedIDs.add(06);
    blockedIDs.add(12);
    blockedIDs.add(14);
    blockedIDs.add(24);
    blockedIDs.add(25);
    blockedIDs.add(42);
    blockedIDs.add(58);
    blockedIDs.add(94);
    blockedIDs.add(115);
    blockedIDs.add(116);
    blockedIDs.add(117);
    blockedIDs.add(127);
    blockedIDs.add(129);
    blockedIDs.add(139);
    blockedIDs.add(140);
    blockedIDs.add(141);
    blockedIDs.add(178);
    blockedIDs.add(179);
    blockedIDs.add(195);
    
    for (int zeile =0;zeile < mapSizeX;zeile++) {
      for (int spalte = 0; spalte < mapSizeY; spalte++) {
        for (int i = 0; i < blockedIDs.size();i++){
          if (mapTiles [zeile] [spalte].getID() == blockedIDs.get(i)){
            mapTiles [zeile] [spalte].setBlocked(true);
          }
        }
      }
    }
  }
  //Erst kommt die Zeile, dann kommt die Spalte!!!!!! Die Zeile definiert die YPos; Die Spalte definiert die XPos; erst kommt X, dann kommt Y;
    public void renderMap(Graphics2D g2d){
      for (int zeile = 0; zeile < mapSizeX; zeile++) {
        for (int spalte = 0; spalte < mapSizeY; spalte++) {
          mapTiles[zeile][spalte].renderTile(g2d, zeile * Tile.TILEWIDTH - gui.getCamera().getXOffset(), spalte * Tile.TILEHEIGHT - gui.getCamera().getYOffset());
        }
        }
    }
  public void setNeighbours(){
    for (int zeile = 0;zeile<mapTiles.length ;zeile++ ) {
      for (int spalte = 0;spalte<mapTiles.length ;spalte++ ) {
        LinkedList temp = new LinkedList();
        try {temp.addFirst(mapTiles[zeile-1][spalte-1]);
        } catch(Exception e) {}
        try {temp.addFirst(mapTiles[zeile][spalte-1]);
        } catch(Exception e) {} 
        try {temp.addFirst(mapTiles[zeile+1][spalte-1]);
        } catch(Exception e) {} 
        try {temp.addFirst(mapTiles[zeile-1][spalte]);
        } catch(Exception e) {} 
        try {temp.addFirst(mapTiles[zeile+1][spalte]);
        } catch(Exception e) {}
        try {temp.addFirst(mapTiles[zeile-1][spalte+1]);
        } catch(Exception e) {}
        try {temp.addFirst(mapTiles[zeile][spalte+1]);
        } catch(Exception e) {}
        try {temp.addFirst(mapTiles[zeile+1][spalte]);
        } catch(Exception e) {}    
        mapTiles[zeile][spalte].setNeighbours(temp);
      } // end of for
    } // end of for
  }
}
