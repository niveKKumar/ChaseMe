import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.*;

public class Editor {
  private GUI gui;
  public int tilewidth = 64, tileheight = 64;
  private String mapPlanPath;
  private TileSet tileSet;
  private int mapSizeX, mapSizeY;
  public Tile mapTiles[][];
  public int graphicID = 22;
  private boolean visible = true;
  private JButton [] editorbuttons = new JButton[3];
  public EditorTileMenu tileMenu;
  private int use = 0;
private KeyManager keyManager;
    private int click = 0,firstX,firstY,secondX,secondY;

  private JLabel idAnzeige;
  private JFileChooser fileChooser = new JFileChooser();

  public Editor(GUI pGUI,int pMapSizeX,int pMapSizeY,KeyManager pKeyManager, TileSet pTileSet){
    gui = pGUI;
    mapSizeX = pMapSizeX;
    mapSizeY = pMapSizeY;
    keyManager = pKeyManager;
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
    for (int i = 0; i < editorbuttons.length ; i++) {
      editorbuttons[i].setVisible(visible);
    }

  }
  public void createEditorMap(){
     mapTiles = new Tile[mapSizeX][mapSizeY];
     int i = 2;
     for (int zeile=0;zeile < mapSizeX;zeile++) {
       for (int spalte=0;spalte < mapSizeY;spalte++ ) {
         mapTiles[zeile][spalte] = tileSet.tileSet[graphicID].clone();
         mapTiles[zeile][spalte].setID(graphicID);
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
  public void setTile(MouseEvent e){
    int x = (int) Math.round(e.getX() +gui.getCamera().getXOffset() ) / tilewidth;
    int y = (int) Math.round(e.getY()+ gui.getCamera().getYOffset() ) / tileheight;
    mapTiles[x][y] = tileSet.tileSet[graphicID];
  }
  public void setTileRect(MouseEvent e) {
      if (keyManager.shift) {
          click++;
          if (click == 1) {
               firstX = ((int) Math.round(e.getX() +gui.getCamera().getXOffset() ) / tilewidth);
               firstY = ((int) Math.round(e.getY()+ gui.getCamera().getYOffset() ) / tileheight);}
          if (click == 2) {
               secondX = (int) Math.round(e.getX() + gui.getCamera().getXOffset()) / tilewidth;
               secondY = (int) Math.round(e.getY() + gui.getCamera().getYOffset()) / tileheight ;


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
                  click=0;
              }}
      } else {
          click = 0;
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
      JOptionPane.showMessageDialog(gui, "Daten erfolgreich gespeichert.");
    } catch (Exception e) {
      JOptionPane.showMessageDialog(gui, "Daten nicht gespeichert.","",JOptionPane.ERROR_MESSAGE);
    }
  }

  public void loadMap() {
    File path = setMapPath();                //Der Filechooeser liefert die Pfadangabe zu dem selektierten Speicherort.
    String mapString = null;
    try (BufferedReader in = new BufferedReader(new FileReader(path))) {     //Bufferedreader liest die Datei an dem von Filechooser zurückgelieferten Speicherort aus.
      String line = in.readLine();
      mapString = line;
      while ((line = in.readLine()) != null) {
        mapString = mapString + " " + line;}

        String [] temp = mapString.split("\\s+");
        mapSizeX = Integer.parseInt(temp[0]);
        mapSizeY = Integer.parseInt(temp[1]);

        int i = 2;
        for (int zeile=0;zeile < mapSizeX;zeile++) {
          for (int spalte=0;spalte < mapSizeY;spalte++ ) {
            mapTiles[spalte][zeile] = tileSet.tileSet[Integer.parseInt(temp[i])].clone();
            mapTiles[spalte][zeile].setID(Integer.parseInt(temp[i]));
            i++;
          }
        }
      JOptionPane.showMessageDialog(gui, "Map erfolgreich geladen.");
    } catch(Exception e){
      JOptionPane.showMessageDialog(gui, "Map konnte nicht geladen werden.","",JOptionPane.ERROR_MESSAGE);

    }

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
      JOptionPane.showMessageDialog(gui, "Keine Datei ausgewählt.","",JOptionPane.WARNING_MESSAGE);
      return null;
    }
  }


}
