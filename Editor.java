import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.LinkedList;

public class Editor implements MouseListener, MouseMotionListener {
    private static GUI gui;
    public LinkedList maps = new LinkedList<EditorMap>();
    private boolean visible = true;
    private JButton[] editorbuttons = new JButton[3];
    public EditorTileMenu tileMenu;
    private int use = 0;
    private KeyManager keyManager;
    private int zoomSteps = 0;
    private double zoom = 1 ;
    public Pointer cameraPoint;
    private LinkedList recentList = new LinkedList();
    private int selectedMap = 0 ;
    int maxRecent = 10 ;

    private JLabel idAnzeige;
    private JPanel recent = new JPanel();

    private JPanel selectMapPanel = new JPanel();
    private JComboBox mapSelectedBox;
    private JCheckBox[] mapCheck;
    private GroupLayout layout;

    // TODO: 28.03.2019 SELECTED GRAPHIC ID MUSS AN MAP ÜBERGEBEN WRDEN DAMIT DAS RICHTIGE TILE GESETZT WIRD ANSONSTEN BLEIBT DIE ID GANZE ZEIT GLEICH """""""""" 
    public Editor(GUI pGUI, int pMapSizeX, int pMapSizeY, KeyManager pKeyManager) {
        gui = pGUI;
        keyManager = pKeyManager;
        gui.addKeyListener(keyManager);
        createMenu();
        TileSet tempTS = new TileSet("res/tileSet.png", 12, 12, 3);
        createEditorMap(pMapSizeX,pMapSizeY,tempTS);
        createTileMenu();
    }

    public void createMenu() {
        idAnzeige = new JLabel();
        idAnzeige.setText("Kein Tile ausgewählt");
        idAnzeige.setBorder(BorderFactory.createLineBorder(Color.black));
        gui.east.add(idAnzeige);
        selectMapPanel.setLayout(new GridLayout(0,2,5,5));
        layout = new GroupLayout(selectMapPanel);
        layout.setAutoCreateGaps(true);
        recent.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        recent.setPreferredSize(new Dimension(64,maxRecent*64+ (maxRecent+1)*5));
        gui.west.add(recent);
        mapSelectedBox = new JComboBox();
        mapSelectedBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
//                if (mapSelectedBox.getSelectedIndex() != selectedMap){
                selectedMap = mapSelectedBox.getSelectedIndex();
                System.out.println("selected"+selectedMap);
                for (int i = 0;i<mapCheck.length;i++ ) {
                    mapCheck[i].setSelected(false);
                }
                mapCheck[selectedMap].setSelected(true);

                if (cameraPoint != null){
                    cameraPoint.setxPos(((EditorMap) maps.get(selectedMap)).getMapSizeX()/2);
                    cameraPoint.setyPos(((EditorMap) maps.get(selectedMap)).getMapSizeY()/2);
                    System.out.println("Setze Camera Point auf Selected");
                }
            }

        });
        selectMapPanel.add(mapSelectedBox);
        gui.east.add(selectMapPanel);
        gui.buttons[4].setVisible(false);

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

    public void createEditorMap(int mapSizeX,int mapSizeY,TileSet pTileSet) {
        selectedMap = maps.size();
        EditorMap editorMap = new EditorMap(gui,mapSizeX,mapSizeY,pTileSet);
        maps.add(editorMap);
        selectedMap = maps.size() - 1;

        createCheckbox();
        gui.camera = new Camera(mapSizeX,mapSizeY);
        cameraPoint = new Pointer(mapSizeX*Tile.TILEWIDTH/2,mapSizeY*Tile.TILEHEIGHT/2,mapSizeX,mapSizeY);
    }
    public void createBlankEditiorMap(int mapSizeX,int mapSizeY,TileSet pTileSet) {
        selectedMap = maps.size();
        EditorMap editorMap = new EditorMap(gui, mapSizeX, mapSizeY, pTileSet);
        editorMap.createBlankEditiorMap();
        maps.add(editorMap);

        createCheckbox();
        gui.camera = new Camera(mapSizeX, mapSizeY);
        cameraPoint = new Pointer(mapSizeX * Tile.TILEWIDTH / 2, mapSizeY * Tile.TILEHEIGHT / 2, mapSizeX, mapSizeY);
    }

    public void renderEditor(Graphics2D g2d) {
        for (int i = 0; i < checkMapBoxes().size() ; i++) {
            int enabledIndex = checkMapBoxes().get(i);
            ( (EditorMap) maps.get(enabledIndex)).renderMap(g2d);
//            System.out.println("Aktivierte Map mit dem Index:"+ enabledIndex +"\n");
        }
//        ((EditorMap) maps.get(selectedMap)).renderMap(g2d); // Selected Map immer oberhalb
//        System.out.println("_____________________________");

    }


    public void createTileMenu() {
        if (use < 1) {
            tileMenu = new EditorTileMenu(gui, true, this);
            use++;
        } else {
            tileMenu.setVisible(true);
        } // Damit kein erneutes Starten immer entsteht
            EditorMap m = (EditorMap) maps.get(selectedMap);
            m.setGraphicID(tileMenu.getSelected());
            m.setTileSet(tileMenu.getTileSet(tileMenu.getSelectedTileSetIndex()));
            tileMenu.selectedinLabel(idAnzeige);

            addRecently(m.getGraphicID());
            idAnzeige.setText("ID von "+ selectedMap +" : " + m.getGraphicID());
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
                    EditorMap m = (EditorMap) maps.get(selectedMap);
                    tileMenu.setSelectedID(temp.getId());
                    m.setGraphicID(temp.getId());
                    m.setTileSet(temp.getTileSet());
                    tileMenu.selectedinLabel(idAnzeige);
                }
            });
            recent.add(temp);
        }
    }
    public void saveMap() {
        Meldungen pathRequest = new Meldungen(gui,true,"null");
        try (BufferedWriter out = new BufferedWriter(new FileWriter(pathRequest.setMapPath("Save")))) {
            EditorMap m = (EditorMap) maps.get(selectedMap);
            out.write(m.getMapSizeX() + " ");
            out.write(Integer.toString(m.getMapSizeX()));
            out.newLine();

            for (int i = 0; i < m.getMapSizeX(); i++) {
                for (int j = 0; j < m.getMapSizeY(); j++) {
                    out.write(m.mapTiles[i][j].getID()+" ");
                }
                out.newLine();
            }
            JOptionPane.showMessageDialog(gui, "Daten erfolgreich gespeichert.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(gui, "Daten nicht gespeichert.", "", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadMap() {
        Meldungen pathRequest = new Meldungen(gui,true,"null");
        File path = pathRequest.setMapPath("Open");                //Der Filechooeser liefert die Pfadangabe zu dem selektierten Speicherort.
        String mapString = null;
        try (BufferedReader in = new BufferedReader(new FileReader(path))) {     //Bufferedreader liest die Datei an dem von Filechooser zurückgelieferten Speicherort aus.
            String line = in.readLine();
            mapString = line;
            while ((line = in.readLine()) != null) {
                mapString = mapString + " " + line;
            }

            String[] temp = mapString.split("\\s+");
            int mapSizeX = Integer.parseInt(temp[0]);
            int mapSizeY = Integer.parseInt(temp[1]);
            Meldungen m = new Meldungen(gui,true,"null");
            File f = m.setMapPath("TileSet");
            m.tileSetAbfrage();
            TileSet ts =new TileSet( f.getPath()
                    , Integer.parseInt(m.getUserInput(0))
                    , Integer.parseInt(m.getUserInput(1))
                    , Integer.parseInt(m.getUserInput(2)));
            EditorMap map = new EditorMap(gui,mapSizeX,mapSizeY,ts);
            int i = 2;
            for (int zeile=0;zeile < map.getMapSizeX();zeile++) {
                for (int spalte=0;spalte < map.getMapSizeY();spalte++ ) {
                    map.mapTiles[spalte][zeile] = ts.tileSet[Integer.parseInt(temp[i])].clone();
                    map.mapTiles[spalte][zeile].setID(Integer.parseInt(temp[i]));
                    i++;
                }
            }
            maps.add(map);

            JOptionPane.showMessageDialog(gui, "Map erfolgreich geladen.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(gui, "Map konnte nicht geladen werden.", "", JOptionPane.ERROR_MESSAGE);

        }

    }

    public void createCheckbox(){
        int name = 0;
        int h = 10;
        selectMapPanel.removeAll();
        mapCheck = new JCheckBox[maps.size()];
        for (int i = 0; i < maps.size() ; i++) {
            name++;
            mapCheck[i] = new JCheckBox("Map "+ name);
            JButton temp = new JButton();
            temp = new JButton(new ImageIcon(((new ImageIcon("res/deleteIcon.png")).getImage()).getScaledInstance(h, h, Image.SCALE_SMOOTH)));
            temp.setMaximumSize(new Dimension(h, h));
            temp.setBorder(null);
            temp.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Remove");
                }
            });

//            selectMapPanel.add(mapCheck[i]);
            // https://wiki.byte-welt.net/wiki/GroupLayout_f%C3%BCr_Homosapiens
            GroupLayout.SequentialGroup verticalGroup = layout.createSequentialGroup();
            verticalGroup.addGroup(layout.createSequentialGroup()
                    .addComponent(mapCheck[i]).addComponent(temp));

//            GroupLayout.ParallelGroup horizontalGroup = layout.createParallelGroup();
//            horizontalGroup.addGroup(layout.createSequentialGroup()
//                    .addComponent(mapCheck[i]).addComponent(temp));
        }
        mapCheck[selectedMap].setSelected(true);
        mapSelectedBox.addItem("Map "+ mapCheck.length );
        mapSelectedBox.setSelectedItem("Map "+ mapCheck.length);
//        System.out.println(mapCheck.length+"x Chseleeckboxes");
    }
    public LinkedList<Integer> checkMapBoxes(){
        LinkedList indexOfActivatedMaps = new LinkedList<Integer>();
        for (int i = 0; i < mapCheck.length; i++) {
//            indexOfActivatedMaps.add(selectedMap);
//            System.out.println("Map Box :"+i+" ist "+mapCheck[i].isSelected());
        if (mapCheck[i].isSelected() && !indexOfActivatedMaps.contains(i)){
                indexOfActivatedMaps.add(i);
            }

        }
//        System.out.println("Selected Index: "+ mapSelectedBox.getSelectedIndex());
        return indexOfActivatedMaps;
    }

    public void zoom(boolean zoomInIsTrueZoomOutisFalse){
        // TODO: 23.03.2019 Zoom an Mapgroesse angepasst
        EditorMap m = (EditorMap) maps.get(selectedMap);
        zoom = 0.25;
        int maxZoom = 10 ,minZoom = 10 ;
        if (zoomInIsTrueZoomOutisFalse){
            //Reinzoom:
            if (zoomSteps < maxZoom ){
                zoomSteps = zoomSteps + 1;
                zoom = 1+zoom;
                Tile.setTILEWIDTH( (int) Math.round(Tile.TILEWIDTH * zoom));
                Tile.setTILEHEIGHT((int) Math.round(Tile.TILEHEIGHT *  zoom));
            }else {
                zoomSteps = 5;
            }
        }else{
            //Rauszoom:
            if (zoomSteps > -minZoom ){
                zoomSteps = zoomSteps - 1;
                zoom = 1-zoom;
                Tile.setTILEWIDTH((int) Math.round(Tile.TILEWIDTH *   zoom));
                Tile.setTILEHEIGHT((int) Math.round(Tile.TILEHEIGHT *  zoom));
            }else {
                zoomSteps = -5;
            }
        }
        cameraPoint.setxPos(m.getMapSizeX()*Tile.TILEWIDTH/2);
        cameraPoint.setyPos(m.getMapSizeY()*Tile.TILEHEIGHT/2);
    }

    public double getZoom() {
        return zoom;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        EditorMap m = (EditorMap) maps.get(selectedMap);
        m.setTile(e);
        if (keyManager.str){
            m.setTileRect(e);
        }
        System.out.println("Editor Klick");
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        EditorMap m = (EditorMap) maps.get(selectedMap);
        m.setTile(e);
        System.out.println("Drag");
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    class Pointer{

        private int xPos,yPos;
        private int mapSizeX,mapSizeY;
        private int speed = 10;

        public Pointer(int  xPos,int yPos,EditorMap map){
            this.xPos = xPos;
            this.yPos = yPos;
            mapSizeX = map.getMapSizeX();
            mapSizeY =map.getMapSizeY();

            gui.getCamera().centerOnObject(this.getLocation());
        }

        public Pointer(int  xPos,int yPos,int pMapSizeX,int pMapSizeY){
            this.xPos = xPos;
            this.yPos = yPos;
            mapSizeX = pMapSizeX;
            mapSizeY = pMapSizeY;

            gui.getCamera().centerOnObject(this.getLocation());
        }


        public void setMove(Point pMove) {
            gui.getCamera().centerOnObject(this.getLocation());
            speed = Math.round(speed);

            int oldXPos = xPos;
            int oldYPos = yPos;

            xPos += pMove.getX() * speed;
            yPos += pMove.getY() * speed;

            boolean leftBorder;
            boolean rightBorder;
            boolean upBorder;
            boolean downBorder;
            leftBorder = xPos < 0 + GUI.FRAME_WIDTH / 2;
            rightBorder = xPos > mapSizeX * Tile.TILEWIDTH - GUI.FRAME_WIDTH / 2;
            upBorder = yPos < 0 + GUI.FRAME_HEIGHT / 2;
            downBorder = yPos > mapSizeY * Tile.TILEHEIGHT - GUI.FRAME_WIDTH / 2;
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

        }
    }
