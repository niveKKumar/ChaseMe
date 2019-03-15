import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class Editor {
  private GUI gui;
  public int tilewidth = 64, tileheight = 64;
  private String mapPlanPath;
  private TileSet tileSet;
  private int mapSizeX, mapSizeY;
  public Tile mapTiles[][];
  public int graphicID = 28;
  private boolean visible = true;
  private JButton [] editorbuttons = new JButton[3];
  public EditorTileMenu tileMenu;
  private int use = 0;

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
    createTileMenu();
    tileMenu.setVisible(false);
  }
  public void createMenu(){
    idAnzeige = new JLabel();
    idAnzeige.setText("Kein Tile ausgewählt");
    idAnzeige.setBorder(BorderFactory.createLineBorder(Color.black));
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
     mapTiles = new Tile[mapSizeX][mapSizeY];
     int i = 2;
     for (int zeile=0;zeile < mapSizeX;zeile++) {
       for (int spalte=0;spalte < mapSizeY;spalte++ ) {
         mapTiles[zeile][spalte] = tileSet.tileSet[22].clone();
         mapTiles[zeile][spalte].setID(22);
         i++;
       }
     }
    gui.setCamera(mapSizeX, mapSizeY);
    }
  public void renderEditor(Graphics2D g2d){
    for (int zeile =0; zeile < mapSizeX;zeile++ ) {
      for (int spalte = 0; spalte < mapSizeY; spalte++ ) {
        mapTiles [zeile] [spalte].renderTile(g2d, zeile*Tile.TILEWIDTH - gui.getCamera().getXOffset() , spalte*Tile.TILEHEIGHT - gui.getCamera().getYOffset() );
      }
    }
  }
  public void setTile(Point clickedCord){
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
    if (use<1){
      tileMenu = new EditorTileMenu(gui, true, this);
    } else {
      tileMenu.setVisible(true);
    } // Damit kein erneutes Starten immer entsteht
    graphicID = tileMenu.getSelected();
    tileMenu.selectedinLabel(idAnzeige);
    idAnzeige.setText("ID: " + Integer.toString(graphicID));
    use++;
  }

  public void saveMap() {
    File temp = setMapPath();
    try (BufferedWriter out = new BufferedWriter(new FileWriter(temp))) {
      out.write(mapSizeX + " ");
      out.write(Integer.toString(mapSizeY));
      out.newLine();

      for (int i = 0; i < mapTiles.length; i++) {
        for (int j = 0; j < mapTiles.length; j++) {
          out.write(mapTiles[i][j].getID() + " ");
        }
        out.newLine();
      }
      JOptionPane.showMessageDialog(gui, "Daten gesichert.");
    } catch (Exception e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(gui, "Daten nicht gesichert.");
    }
  }

  public void loadMap() {
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


  public File setMapPath() {             //Methode des Filechosers; offnet einen Speicher-Dialog
    fileChooser.setCurrentDirectory(new File("./res"));  //Verweis auf das aktuelle Programmverzeichnis
    if (fileChooser.showSaveDialog(gui) == JFileChooser.APPROVE_OPTION) {  //Wenn der Ok-Button gedrueckt wird...
      return fileChooser.getSelectedFile();
    } else {                                                               //Wenn der Ok.Button nicht gedreckt wird.
      JOptionPane.showMessageDialog(gui, "Speichern abgebrochen");
      return null;
    }
  }


}
