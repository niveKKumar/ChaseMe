import javax.swing.*;
import java.awt.*;

public class Editor {
  private GUI gui;
  public int tilewidth = 64, tileheight = 64;
  private String mapPlanPath;
  private TileSet tileSet;
  private int mapSizeX, mapSizeY;
  public Tile mapTiles[];
  public int graphicID = 28;
  private boolean visible = true;
  private JButton [] editorbuttons = new JButton[3];
  public EditorTileMenu tileMenu;

  private JLabel idAnzeige;
  private JFileChooser fileChooser = new JFileChooser();

  public Editor(GUI pGUI,int pMapSizeX,int pMapSizeY/*, String pMapPlanPath*/, TileSet pTileSet){
    gui = pGUI;
    mapSizeX = pMapSizeX;
    mapSizeY = pMapSizeY;
//    mapPlanPath = pMapPlanPath;
    tileSet = pTileSet;
    createMenu();
    createEditorMap();
  }
  public void createMenu(){
    idAnzeige = new JLabel();
    idAnzeige.setText("ID:  ");
    gui.east.add(idAnzeige);

    for (int i = 0 ;i<editorbuttons.length;i++) {
      String[] btNamesEditor = {"+", "-", "Tile Auswaehlen",};
      editorbuttons[i] = new JButton(btNamesEditor[i]);
      editorbuttons[i].setVisible(visible);
      editorbuttons[i].addActionListener(gui);
      gui.south.add(editorbuttons[i]);
    }

  }
  public void setMenuVisible(boolean b){
    visible = b;
    for (int i = 0; i <editorbuttons.length ; i++) {
      editorbuttons[i].setVisible(visible);
    }
  }
  public void createEditorMap(){
     mapTiles = new Tile [mapSizeX][mapSizeY];
     int i = 2;
     for (int zeile=0;zeile < mapSizeX;zeile++) {
       for (int spalte=0;spalte < mapSizeY;spalte++ ) {
         mapTiles[zeile][spalte] = tileSet.tileSet[22];
         mapTiles[zeile][spalte].setID(22);
         i++;
       }
     }
    }
  public void renderEditor(Graphics2D g2d){
    for (int zeile =0; zeile < mapSizeX;zeile++ ) {
      for (int spalte = 0; spalte < mapSizeY; spalte++ ) {
        mapTiles [zeile] [spalte].renderTile(g2d, zeile*Tile.TILEWIDTH - gui.getCamera().getXOffset() , spalte*Tile.TILEHEIGHT - gui.getCamera().getYOffset() );
      }
    }
  }
  public void setTile(Point clickedCord){
//    selectedImage = tileSet.tileSet[graphicID].tileImage;;
    int x = clickedCord.x;
    int y = clickedCord.y;
    mapTiles[x][y] = tileSet.tileSet[graphicID];
  }
  public void setTileRect(Point first, Point second) {
    int firstX = (int) Math.round(first.getX());
    int firstY = (int) Math.round(first.getY());
    int secondX = (int) Math.round(second.getX());
    int secondY = (int) Math.round(second.getY());

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
  }

  public void createTileMenu(){
    tileMenu = new EditorTileMenu();
//    tileMenu.main(null);
    graphicID = tileMenu.getSelected();
    System.out.println("Graphic ID im Editor:  "+graphicID);
  }

  public EditorTileMenu getTileMenu() {
    return tileMenu;
  }

  public void setGraphicID(int graphicID) {
    this.graphicID = graphicID;
  }

  public int getGraphicID() {
    return graphicID;
  }




  //  public File saveMap() {             //Methode des Filechosers; offnet einen Speicher-Dialog
//    fileChooser.setCurrentDirectory(new File("./res"));  //Verweis auf das aktuelle Programmverzeichnis
//    if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {  //Wenn der Ok-Button gedrueckt wird...
//      return fileChooser.getSelectedFile();
//    } else {                                                               //Enn der Ok.Button nicht gedreckt wird.
//      JOptionPane.showMessageDialog(this, "Speichern abgebrochen");
//      return null;
//    }
//  }


}
