import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.LinkedList;
import java.util.Queue;

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
    private LinkedList recentList = new LinkedList();
    int maxRecent = 10 ;

    private JLabel idAnzeige;
    private JPanel recent = new JPanel();
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
        recent.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        recent.setPreferredSize(new Dimension(64,maxRecent*64+ (maxRecent+1)*5));
        gui.east.add(recent);

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
        gui.camera = new Camera(mapSizeX,mapSizeY);
        camerapoint = new Pointer(mapSizeX*Tile.TILEWIDTH/2,mapSizeY*Tile.TILEHEIGHT/2);

    }
    public void createBlankEditiorMap(){
        mapTiles = new Tile[mapSizeX][mapSizeY];
        int i = 2;
        for (int zeile = 0; zeile < mapSizeX; zeile++) {
            for (int spalte = 0; spalte < mapSizeY; spalte++) {
                mapTiles[zeile][spalte] = tileSet.tileSet[6].clone();
                mapTiles[zeile][spalte].setID(6);
                i++;
            }
        }
        gui.camera = new Camera(mapSizeX,mapSizeY);
        camerapoint = new Pointer(mapSizeX*Tile.TILEWIDTH/2,mapSizeY*Tile.TILEHEIGHT/2);
    }

    public void renderEditor(Graphics2D g2d) {
        for (int zeile = 0; zeile < mapSizeX; zeile++) {
            for (int spalte = 0; spalte < mapSizeY; spalte++) {
                mapTiles[zeile][spalte].renderTile(g2d, zeile * Tile.TILEWIDTH - gui.getCamera().getXOffset(), spalte * Tile.TILEHEIGHT - gui.getCamera().getYOffset());
            }
        }
//        camerapoint.renderPointer(g2d);
    }

    public void setTile(MouseEvent e) {
        int x = (int) Math.round(e.getX() + gui.getCamera().getXOffset()) / Tile.TILEWIDTH;
        int y = (int) Math.round(e.getY() + gui.getCamera().getYOffset()) / Tile.TILEHEIGHT;
        mapTiles[x][y] = tileSet.tileSet[graphicID];
        mapTiles[x][y].setID(graphicID);
        System.out.println(x+"|"+y);
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
        addRecently(graphicID);
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
                    out.write(mapTiles[i][j].getID()+" ");
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
                    mapTiles[zeile][spalte] = tileSet.tileSet[Integer.parseInt(temp[i])].clone();
                    mapTiles[zeile][spalte].setID(Integer.parseInt(temp[i]));
                    i++;
                }
            }
            JOptionPane.showMessageDialog(gui, "Map erfolgreich geladen.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(gui, "Map konnte nicht geladen werden.", "", JOptionPane.ERROR_MESSAGE);

        }

    }
    public void addRecently(int id) {
        recentList.add(new EditorTileButton(id));
        if (recentList.size() >= maxRecent) {
            recentList.removeFirst();
            System.out.println(recentList.size()+"muss 4 sein");}
            displayRecent(recent);
    }
    public void displayRecent(JPanel panel){
        panel.removeAll();
        for (int i = 0; i < recentList.size(); i++) {
            EditorTileButton temp = (EditorTileButton)recentList.get(i);
            temp.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.out.println(temp.getId()+"meine ID beim klicken  ");
                    graphicID = temp.getId();
                }
            });
            recent.add(temp);
        }
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
        // TODO: 23.03.2019 Zoom an Mapgroesse angepasst
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
    camerapoint.setxPos(mapSizeX*Tile.TILEWIDTH/2);
        camerapoint.setyPos(mapSizeY*Tile.TILEHEIGHT/2);
    }

    public double getZoom() {
        return zoom;
    }
    class Pointer{

        private int xPos,yPos;
        private int xView,yView;
        private int speed = 10;

        public Pointer(int  xPos,int yPos){
            this.xPos = xPos;
            this.yPos = yPos;
            gui.getCamera().centerOnObject(this.getLocation());
        }
        public void setMove(Point pMove) {
            gui.getCamera().centerOnObject(this.getLocation());
            speed = (int) Math.round(speed);

            int oldXPos = xPos;
            int oldYPos = yPos;

            xPos += pMove.getX() * speed;
            yPos += pMove.getY() * speed;

            boolean leftBorder;
            boolean rightBorder;
            boolean upBorder;
            boolean downBorder;
            if (xPos < 0 + GUI.FRAME_WIDTH / 2) {
                leftBorder = true;
            } else {
                leftBorder = false;
            }
            if (xPos > mapSizeX * Tile.TILEWIDTH - GUI.FRAME_WIDTH / 2) {
                rightBorder = true;
            } else {
                rightBorder = false;
            }
            if (yPos < 0 + GUI.FRAME_HEIGHT / 2) {
                upBorder = true;
            } else {
                upBorder = false;
            }
            if (yPos > mapSizeY * Tile.TILEHEIGHT - GUI.FRAME_WIDTH / 2) {
                downBorder = true;
            } else {
                downBorder = false;
            }
            //BEWEGENDER POINTER:
            if (leftBorder || rightBorder || upBorder || downBorder) {
                xPos = oldXPos;
                yPos = oldYPos;
            }
        }

            public Point getLocation(){
                return new Point(xPos,yPos);
            }
            public void setxPos(int xPos) {
                this.xPos = xPos;
            }

            public void setyPos(int yPos) {
                this.yPos = yPos;
            }

            public void renderPointer(Graphics2D g2d){
                BasicStroke stroke1 = new BasicStroke(20 , BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND );
                g2d.setColor(Color.black);
                g2d.setStroke(stroke1);
                g2d.drawLine(xPos - gui.getCamera().getXOffset(),yPos- gui.getCamera().getYOffset(),xPos - gui.getCamera().getXOffset()+10,yPos- gui.getCamera().getYOffset());
            }
        }
    }
