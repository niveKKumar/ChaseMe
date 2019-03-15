import java.awt.*;
import java.io.*;
import java.io.BufferedReader;
import java.util.*;

public class Map {
  public static final int tileWidth = 64;
  public static final int tileHeight = 64;
  private String mapPlanPath;
  private TileSet tileSet;
  public Tile mapTiles [] []; 
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
    chapterOffset = pChapterOffset*tileWidth;
    createMap();
  }

  public void createMap(){
    //Einlesen des "Bauplans":
    String mapString = null;
    try{
      BufferedReader br = new BufferedReader(new FileReader(mapPlanPath));
      String line = br.readLine();
      mapString = line;
      System.out.println("Einlesen der Map");
      while ((line = br.readLine()) != null) {
        mapString = mapString + " " + line;
      }
    } catch(Exception e){
      System.out.println("LESEFEHLER");
    }
    
    //Erstellen des Arrays mit den Tiles der Map:
    System.out.println("Splitten der Mapteile");
    String [] temp = mapString.split("\\s+");
    mapSizeX = Integer.parseInt(temp[0]);
    mapSizeY = Integer.parseInt(temp[1]);
    mapTiles = new Tile[mapSizeX][mapSizeY];
    System.out.println("Erstellen der Map Tiles");
    
    int i = 2;
    for (int zeile=0;zeile < mapSizeX;zeile++) {
      for (int spalte=0;spalte < mapSizeY;spalte++ ) {
        mapTiles[spalte][zeile] = tileSet.tileSet[Integer.parseInt(temp[i])].clone();
        mapTiles[spalte][zeile].setID(Integer.parseInt(temp[i]));
        i++;
      }
    }
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
    if (position.getX() <= (getMapSizeX() * tileWidth) && position.getY() <= (getMapSizeY()*tileHeight)) {
      active = true;
    }
    return active;
  }

  public void setBorderOrItemsBlocked(){
    if (mapStatus.equals("Empty")){
      System.out.println("Empty");;}
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
          if (mapTiles [spalte] [zeile].getID() == blockedIDs.get(i)){
            mapTiles [spalte] [zeile].setBlocked(true);
          }
        }
      }
    }
  }
  public void renderMap(Graphics2D g2d){
    for (int spalte =0; spalte < mapSizeX;spalte++ ) {
        for (int zeile =0; zeile < mapSizeY; zeile++ ) {
        mapTiles [spalte] [zeile].renderTile(g2d, spalte*tileWidth - gui.getCamera().getXOffset() , zeile*tileHeight - gui.getCamera().getYOffset() );
      } 
    }
  }
  public void setNeighbours(){
    for (int spalte = 0;spalte<mapTiles.length ;spalte++ ) {
      for (int zeile = 0;zeile<mapTiles.length ;zeile++ ) {
        LinkedList temp = new LinkedList();
        try {temp.addFirst(mapTiles[spalte-1][zeile-1]);
        } catch(Exception e) {}
        try {temp.addFirst(mapTiles[spalte][zeile-1]);
        } catch(Exception e) {} 
        try {temp.addFirst(mapTiles[spalte+1][zeile-1]);
        } catch(Exception e) {} 
        try {temp.addFirst(mapTiles[spalte-1][zeile]);
        } catch(Exception e) {} 
        try {temp.addFirst(mapTiles[spalte+1][zeile]);
        } catch(Exception e) {} 
        try {temp.addFirst(mapTiles[spalte-1][zeile+1]);
        } catch(Exception e) {}
        try {temp.addFirst(mapTiles[spalte][zeile+1]);
        } catch(Exception e) {}
        try {temp.addFirst(mapTiles[spalte+1][zeile]);
        } catch(Exception e) {}    
        mapTiles[spalte][zeile].setNeighbours(temp);    
      } // end of for
    } // end of for
  }
}
