import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.*;

public class Editor {
  private GUI gui;
  private TileSet tileSet;
  public int mapSizeX, mapSizeY;
  public Tile mapTiles[][];
  public int graphicID = 22;
  private boolean visible = true;
  private JButton[] editorbuttons = new JButton[3];
  public EditorTileMenu tileMenu;
  private int use = 0;
  private KeyManager keyManager;
  private int click = 0, firstX, firstY, secondX, secondY;
  private int zoomSteps= 0;
  private double zoom = 1 ;
  public Pointer camerapoint;

  private JLabel idAnzeige;
  private JFileChooser fileChooser = new JFileChooser();

  public Editor(GUI pGUI, int pMapSizeX, int pMapSizeY, KeyManager pKeyManager, TileSet pTileSet) {
    gui = pGUI;
    mapSizeX = pMapSizeX;
    mapSizeY = pMapSizeY;
    keyManager = pKeyManager;
    tileSet = pTileSet;
    createMenu();
    createEditorMap();
    createTileMenu();
   }

  public void createMenu() {
    idAnzeige = new JLabel();
    idAnzeige.setText("Kein Tile ausgewählt");
    idAnzeige.setBorder(BorderFactory.createLineBorder(Color.black));
    gui.east.add(idAnzeige);

    for (int i = 0; i < editorbuttons.length; i++) {
      String[] btNamesEditor = {"+", "-", "Tile Auswaehlen",};
      editorbuttons[i] = new JButton(btNamesEditor[i]);
      editorbuttons[i].setVisible(true);
      editorbuttons[i].addActionListener(gui);
      gui.south.add(editorbuttons[i]);
    }

  }

  public void setMenuVisible(boolean b) {
    visible = b;
    for (int i = 0; i < editorbuttons.length; i++) {
      editorbuttons[i].setVisible(visible);
    }

  }

  public void createEditorMap() {
    mapTiles = new Tile[mapSizeX][mapSizeY];
    int i = 2;
    for (int zeile = 0; zeile < mapSizeX; zeile++) {
      for (int spalte = 0; spalte < mapSizeY; spalte++) {
        mapTiles[zeile][spalte] = tileSet.tileSet[graphicID].clone();
        mapTiles[zeile][spalte].setID(graphicID);
        i++;
      }
    }
    mapTiles[mapSizeX/2] [mapSizeY/2] = tileSet.tileSet[6].clone();
    camerapoint = new Pointer(mapSizeX*Tile.TILEWIDTH/2,mapSizeY*Tile.TILEHEIGHT/2);
    gui.camera.centerOnEditor(camerapoint);
  }

  public void renderEditor(Graphics2D g2d) {
    for (int zeile = 0; zeile < mapSizeX; zeile++) {
      for (int spalte = 0; spalte < mapSizeY; spalte++) {
        mapTiles[zeile][spalte].renderTile(g2d, zeile * Tile.TILEWIDTH - gui.getCamera().getXOffset(), spalte * Tile.TILEHEIGHT - gui.getCamera().getYOffset());
      }
    }
  }

  public void setTile(MouseEvent e) {
    int x = (int) Math.round(e.getX() + gui.getCamera().getXOffset()) / Tile.TILEWIDTH;
    int y = (int) Math.round(e.getY() + gui.getCamera().getYOffset()) / Tile.TILEHEIGHT;
    mapTiles[x][y] = tileSet.tileSet[graphicID];
  }

  public void setTileRect(MouseEvent e) {
    if (keyManager.shift) {
      click++;
      if (click == 1) {
        firstX = ((int) Math.round(e.getX() + gui.getCamera().getXOffset()) / Tile.TILEWIDTH);
        firstY = ((int) Math.round(e.getY() + gui.getCamera().getYOffset()) / Tile.TILEHEIGHT);
      }
      if (click == 2) {
        secondX = (int) Math.round(e.getX() + gui.getCamera().getXOffset()) / Tile.TILEWIDTH;
        secondY = (int) Math.round(e.getY() + gui.getCamera().getYOffset()) / Tile.TILEHEIGHT;


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
          click = 0;
        }
      }
    } else {
      click = 0;
      setTile(e);
    }

  }

  public void createTileMenu() {
    if (use < 1) {
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
      JOptionPane.showMessageDialog(gui, "Daten nicht gespeichert.", "", JOptionPane.ERROR_MESSAGE);
    }
  }

  public void loadMap() {
    File path = setMapPath();                //Der Filechooeser liefert die Pfadangabe zu dem selektierten Speicherort.
    String mapString = null;
    try (BufferedReader in = new BufferedReader(new FileReader(path))) {     //Bufferedreader liest die Datei an dem von Filechooser zurückgelieferten Speicherort aus.
      String line = in.readLine();
      mapString = line;
      while ((line = in.readLine()) != null) {
        mapString = mapString + " " + line;
      }

      String[] temp = mapString.split("\\s+");
      mapSizeX = Integer.parseInt(temp[0]);
      mapSizeY = Integer.parseInt(temp[1]);

      int i = 2;
      for (int zeile = 0; zeile < mapSizeX; zeile++) {
        for (int spalte = 0; spalte < mapSizeY; spalte++) {
          mapTiles[spalte][zeile] = tileSet.tileSet[Integer.parseInt(temp[i])].clone();
          mapTiles[spalte][zeile].setID(Integer.parseInt(temp[i]));
          i++;
        }
      }
      JOptionPane.showMessageDialog(gui, "Map erfolgreich geladen.");
    } catch (Exception e) {
      JOptionPane.showMessageDialog(gui, "Map konnte nicht geladen werden.", "", JOptionPane.ERROR_MESSAGE);

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
      JOptionPane.showMessageDialog(gui, "Keine Datei ausgewählt.", "", JOptionPane.WARNING_MESSAGE);
      return null;
    }
  }
  public void zoom(boolean zoomInIsTrueZoomOutisFalse){
      zoom = 0.25;
      if (zoomInIsTrueZoomOutisFalse){
      //Reinzoom:
      if (zoomSteps < 5 ){
        zoomSteps = zoomSteps + 1;
        zoom = 1+zoom;
        Tile.setTILEWIDTH( (int) Math.round(Tile.TILEWIDTH * zoom));
        Tile.setTILEHEIGHT((int) Math.round(Tile.TILEHEIGHT *  zoom));
      }else {
        zoomSteps = 5;
      }
  }else{
      //Rauszoom:
      if (zoomSteps > -5 ){
        zoomSteps = zoomSteps - 1;
        zoom = 1-zoom;
        Tile.setTILEWIDTH((int) Math.round(Tile.TILEWIDTH *   zoom));
        Tile.setTILEHEIGHT((int) Math.round(Tile.TILEHEIGHT *  zoom));
      }else {
        zoomSteps = -5;
      }

  }
//    System.out.println(zoomSteps+": "+Tile.Tile.TILEHEIGHT);
  }

    public double getZoom() {
        return zoom;
    }
  class Pointer{

    private int xPos,yPos;
    private int speed = 5;

    public Pointer(int  xPos,int yPos){
      this.xPos = xPos;
      this.yPos = yPos;
    }
    public void setMove(Point pMove){
      gui.getCamera().centerOnEditor(this);
      xPos += pMove.getX()*speed;
      yPos += pMove.getY()*speed;
      System.out.println(xPos+"POINTER"+yPos);
    }

    public int getxPos() {
      return xPos;
    }

    public int getyPos() {
      return yPos;
    }

    public void setxPos(int xPos) {
      this.xPos = xPos;
    }

    public void setyPos(int yPos) {
      this.yPos = yPos;
    }
  }
}
