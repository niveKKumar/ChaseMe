import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;

public class Editor implements ActionListener, MouseMotionListener {
    private static GUI gui;
    public LinkedList maps = new LinkedList<EditorMap>();
    public EditorTileMenu tileMenu;
    public Pointer cameraPoint;
    private JButton[] editorbuttons = new JButton[3];
    private int use = 0;
    private KeyManager keyManager;
    private int zoomSteps = 0;
    private LinkedList recentList = new LinkedList();
    public int selectedMap = 0;
    private int maxRecent = 10;
    public JLabel idAnzeige;
    private Timer autosaver;
    private JPanel recent = new JPanel();

    private JPanel selectMapPanel = new JPanel();
    private JPanel mapSelectActionsPanel = new JPanel(new GridLayout(0, 1));
    private JComboBox mapSelectedBox;
    private JButton btAutosave;
    private LinkedList mapCheck = new LinkedList<JCheckBox>();
    private GroupLayout layout;

    public Editor(GUI pGUI, int pMapSizeX, int pMapSizeY, KeyManager pKeyManager) {
        gui = pGUI;
        keyManager = pKeyManager;
        gui.addKeyListener(keyManager);
        createMenu();
        createTileMenu();
        TileSet tempTS = new TileSet("Content/Graphics/tileSets/12x12x3 - tileSet.png", 12, 12, 3);

        createEditorMap(pMapSizeX, pMapSizeY, tempTS, null);
    }

    public void createMenu() {
        idAnzeige = new JLabel();
        idAnzeige.setFocusable(false);
        idAnzeige.setText("Kein Tile ausgewählt");
        idAnzeige.setBorder(BorderFactory.createLineBorder(Color.black));

        selectMapPanel.setLayout(new GridLayout(0,2,5,5));
        selectMapPanel.add(idAnzeige);
        layout = new GroupLayout(selectMapPanel);
        layout.setAutoCreateGaps(true);


        recent.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        recent.setPreferredSize(new Dimension(64,maxRecent*64+ (maxRecent+1)*5));


        for (int i = 0; i < editorbuttons.length; i++) {
            String[] btNamesEditor = {"+", "-", "Tile Auswaehlen",};
            editorbuttons[i] = new JButton(btNamesEditor[i]);
            editorbuttons[i].setFocusable(false);
            editorbuttons[i].addActionListener(gui);
            gui.south.add(editorbuttons[i]);
        }
        mapSelectedBox = new JComboBox();
        mapSelectedBox.setFocusable(false);
        mapSelectedBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    selectedMap = mapSelectedBox.getSelectedIndex();
                    System.out.println("selected" + selectedMap);
                    for (int i = 0; i < mapCheck.size(); i++) {
                        ((JCheckBox) mapCheck.get(i)).setSelected(false);
                    }
                    ((JCheckBox) mapCheck.get(selectedMap)).setSelected(true);

                    if (cameraPoint != null) {
                        cameraPoint.setxPos(((EditorMap) maps.get(selectedMap)).getMapSizeX() / 2);
                        cameraPoint.setyPos(((EditorMap) maps.get(selectedMap)).getMapSizeY() / 2);
                        System.out.println("Setze Camera Point auf Selected");
                    }
                }
                gui.requestFocus();
            }
        });

        mapSelectActionsPanel.add(mapSelectedBox);

        JButton btChapterOffset = new JButton("Chapter Offset setzen:");
        btChapterOffset.setFocusable(false);
        btChapterOffset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Meldungen m = new Meldungen(gui, true, "null");
                m.chapterAbfrage();
                ((EditorMap) maps.get(selectedMap)).setchapterXOffset(Integer.parseInt(m.getUserInput(0)));
                ((EditorMap) maps.get(selectedMap)).setchapterYOffset(Integer.parseInt(m.getUserInput(1)));
                gui.requestFocus();
            }
        });
        mapSelectActionsPanel.add(btChapterOffset);

        autosaver = new Timer(45000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                autoSaveMap();
            }
        });
        btAutosave = new JButton("Autosave is enabled");
        btAutosave.setForeground(Color.green);
        autosaver.start();
        btAutosave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!autosaver.isRunning()) {
                    autosaver.start();
                    btAutosave.setText("Autosave enabled");
                    btAutosave.setForeground(Color.green);
                } else {
                    autosaver.stop();
                    btAutosave.setText("Autosave disabled");
                    btAutosave.setForeground(Color.red);
                }
                gui.requestFocus();
            }
        });
        mapSelectActionsPanel.add(btAutosave);

        gui.west.add(recent);
        gui.east.add(selectMapPanel);
        gui.east.add(mapSelectActionsPanel);
        gui.buttons[4].setVisible(false);
    }

    public void setMenuVisible(boolean b) {
        for (int i = 0; i < editorbuttons.length; i++) {
            editorbuttons[i].setVisible(b);
        }

    }

    public void createEditorMap(int mapSizeX, int mapSizeY, TileSet pTileSet, Integer tileID) {
        selectedMap = maps.size();
        EditorMap editorMap = new EditorMap(gui,mapSizeX,mapSizeY,pTileSet);
        editorMap.createEditorMap(tileID);
        maps.add(editorMap);
        selectedMap = maps.size() - 1;

        createCheckbox();
        gui.camera = new Camera(mapSizeX,mapSizeY);
        cameraPoint = new Pointer(gui.camera, (EditorMap) maps.get(selectedMap));
    }

    public void createBlankEditorMap(int mapSizeX, int mapSizeY, TileSet pTileSet) {
        selectedMap = maps.size();
        EditorMap editorMap = new EditorMap(gui, mapSizeX, mapSizeY, pTileSet);
        editorMap.createBlankEditorMap();
        maps.add(editorMap);

        createCheckbox();
        gui.camera = new Camera(mapSizeX, mapSizeY);
        cameraPoint = new Pointer(gui.camera, (EditorMap) maps.get(selectedMap));
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
            ((EditorMap) maps.get(selectedMap)).setGraphicID(tileMenu.getSelectedID());
            ((EditorMap) maps.get(selectedMap)).setTileSet(tileMenu.getTileSet(tileMenu.getSelectedTileSetIndex()));
        } // Damit kein erneutes Starten immer entsteht
    }

    public void addRecently(int id, TileSet ts) {
        recentList.add(new EditorTileButton(id, ts));
        if (recentList.size() >= maxRecent) {
            recentList.removeFirst();
        }
            displayRecent(recent);
    }

    public void update() {
        if (gui.keyInputToMove().getX() != 0 || gui.keyInputToMove().getY() != 0) {
            cameraPoint.setMove(gui.keyInputToMove());
        }
    }
    public void displayRecent(JPanel panel){
        panel.removeAll();
        for (int i = 0; i < recentList.size(); i++) {
            EditorTileButton temp = (EditorTileButton)recentList.get(i);
            temp.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
//                    System.out.println(temp.getId()+"meine ID beim klicken  ");
                    EditorMap m = (EditorMap) maps.get(selectedMap);
                    tileMenu.setSelectedID(temp.getId());
                    m.setGraphicID(temp.getId());
                    m.setTileSet(temp.getTileSet());
                    tileMenu.selectedinLabel(idAnzeige, null);
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
            });
            panel.add(temp);
        }
        gui.repaint();
        panel.revalidate();
    }

    public void saveMap(File path, boolean notification) {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(path))) {
            EditorMap m = (EditorMap) maps.get(selectedMap);
            out.write(m.getMapSizeX() + " ");
            out.write(Integer.toString(m.getMapSizeX()));
            out.newLine();

            for (int zeile = 0; zeile < m.mapTiles.length; zeile++) {
                for (int spalte = 0; spalte < m.mapTiles[zeile].length; spalte++) {
                    out.write(m.mapTiles[zeile][spalte].getID() + " ");
                }
                out.newLine();
            }
            if (notification) {
                JOptionPane.showMessageDialog(gui, "Daten erfolgreich gespeichert.");
            }
        } catch (Exception e) {
            if (notification) {
                JOptionPane.showMessageDialog(gui, "Daten nicht gespeichert.", "", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void autoSaveMap() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        File output = new File("Content/Maps/autosave/" + timeStamp + "_AUTOSAVE.txt");
        saveMap(output, false);

    }

    public void loadMap() {
        Meldungen m = new Meldungen(gui, true, "null");
        File path = m.setMapPath("Open");                //Der Filechooeser liefert die Pfadangabe zu dem selektierten Speicherort.
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

            m.setMapPath("TileSet");
            TileSet ts = m.getTileSet();
            EditorMap em = new EditorMap(gui, mapSizeX, mapSizeY, ts);
            em.createBaseMap(); // FIXME: 02.04.2019 Kann besser gelöst werden statt überschreiben
            int i = 2;
            for (int zeile = 0; zeile < em.mapTiles.length; zeile++) {
                for (int spalte = 0; spalte < em.mapTiles[zeile].length; spalte++) {
                    em.mapTiles[zeile][spalte] = ts.tileSet[Integer.parseInt(temp[i])].clone();
                    em.mapTiles[zeile][spalte].setID(Integer.parseInt(temp[i]));
                    i++;
                }
            }

            maps.add(em);
            selectedMap = maps.size() - 1;
            createCheckbox();
            gui.camera = new Camera(mapSizeX, mapSizeY);
            cameraPoint = new Pointer(gui.camera, (EditorMap) maps.get(selectedMap));

            JOptionPane.showMessageDialog(gui, "Map erfolgreich geladen.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(gui, "Map konnte nicht geladen werden.", "", JOptionPane.ERROR_MESSAGE);

        }

    }

    public void createCheckbox(){
        int name = 0;
        int h = 10;
        mapCheck.clear();
        mapSelectedBox.removeAllItems();
        selectMapPanel.removeAll();
        for (int i = 0; i < maps.size() ; i++) {
            name++;
            mapCheck.add(new JCheckBox("Map " + name));
            JButton temp = new JButton();
            temp = new JButton(new ImageIcon(((new ImageIcon("Content/Graphics/UI/deleteIcon.png")).getImage()).getScaledInstance(h, h, Image.SCALE_SMOOTH)));
            temp.setMaximumSize(new Dimension(h, h));
            temp.setBorder(null);
            temp.addActionListener(new RemoveBTActionListener(i) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    super.actionPerformed(e);
                    gui.requestFocus();
                }
            });
            mapSelectedBox.addItem("Map " + name);

//             https://wiki.byte-welt.net/wiki/GroupLayout_f%C3%BCr_Homosapiens
            GroupLayout.SequentialGroup verticalGroup = layout.createSequentialGroup();
            verticalGroup.addGroup(layout.createSequentialGroup()
                    .addComponent(((JCheckBox) mapCheck.get(i))).addComponent(temp));

        }
        if (!mapCheck.isEmpty()) {
            ((JCheckBox) mapCheck.get(selectedMap)).setSelected(true);
            mapSelectedBox.setSelectedItem("Map " + mapCheck.size());
            mapSelectActionsPanel.setVisible(true);
        } else {
            mapSelectActionsPanel.setVisible(false);
        }
    }
    public LinkedList<Integer> checkMapBoxes(){
        LinkedList indexOfActivatedMaps = new LinkedList<Integer>();
        for (int i = 0; i < mapCheck.size(); i++) {
//            indexOfActivatedMaps.add(selectedMap);
//            System.out.println("Map Box :"+i+" ist "+mapCheck[i].isSelected());
            if (((JCheckBox) mapCheck.get(i)).isSelected() && !indexOfActivatedMaps.contains(i)) {
                indexOfActivatedMaps.add(i);
            }

        }
//        System.out.println("Selected Index: "+ mapSelectedBox.getSelectedIndex());
        return indexOfActivatedMaps;
    }

    public void zoom(boolean zoomInIsTrueZoomOutisFalse){
        // TODO: 23.03.2019 Zoom an Mapgroesse angepasst EDIT: NICHT WIRKLICH NÖTIG...
        EditorMap m = (EditorMap) maps.get(selectedMap);
        double zoom = 0.25;
        int maxZoom = 10 ,minZoom = 10 ;
        if (zoomInIsTrueZoomOutisFalse){
            //Reinzoom:
            if (zoomSteps < maxZoom ){
                zoomSteps = zoomSteps + 1;
                zoom = 1 + zoom;
                Tile.setTILEWIDTH( (int) Math.round(Tile.TILEWIDTH * zoom));
                Tile.setTILEHEIGHT((int) Math.round(Tile.TILEHEIGHT * zoom));
            }else {
                zoomSteps = 5;
            }
        }else{
            //Rauszoom:
            if (zoomSteps > -minZoom ){
                zoomSteps = zoomSteps - 1;
                zoom = 1 - zoom;
                Tile.setTILEWIDTH((int) Math.round(Tile.TILEWIDTH * zoom));
                Tile.setTILEHEIGHT((int) Math.round(Tile.TILEHEIGHT * zoom));
            }else {
                zoomSteps = -5;
            }
        }
        cameraPoint.setxPos(m.getMapSizeX()*Tile.TILEWIDTH/2);
        cameraPoint.setyPos(m.getMapSizeY()*Tile.TILEHEIGHT/2);
    }

    public void removeMap(int index) {
        System.out.println("Index:" + index);
        maps.remove(index);
        mapCheck.remove(index);
        if (maps.isEmpty()) {
            mapCheck.clear();
            mapSelectedBox.removeAllItems();
        }
        createCheckbox();
    }

    public void actionPerformed(ActionEvent e) {
        JButton temp = (JButton) e.getSource();
        switch (temp.getText()) {
            case "+":
                System.out.println("Zoom rein");
                zoom(true);
                gui.camera.centerOnObject(cameraPoint.getLocation());
                gui.requestFocus();
                break;

            case "-":
                System.out.println("Zoom raus");
                zoom(false);
                gui.camera.centerOnObject(cameraPoint.getLocation());
                gui.requestFocus();
                break;
            case "Tile Auswaehlen":
                createTileMenu();
                gui.requestFocus();
                break;
        }

    }

    public void mouseClicked(MouseEvent e) {
        EditorMap m = (EditorMap) maps.get(selectedMap);
        if (keyManager.shift) {
            m.setTileRect(e);
        } else {
            m.setClick(0);
            m.setTile(e);
        }
        if (keyManager.str){
            m.setPointed(e);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        EditorMap m = (EditorMap) maps.get(selectedMap);
        m.setTile(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }


    private class RemoveBTActionListener implements ActionListener {
        private int mapNumber;

        public RemoveBTActionListener(int mapNumber) {
            this.mapNumber = mapNumber;
        }

        public void actionPerformed(ActionEvent e) {
            removeMap(mapNumber);
        }
    }
    class Pointer{

        private int xPos,yPos;
        private int mapSizeX,mapSizeY;
        private int speed = 10;

        public Pointer(Camera camera, EditorMap map) {
            mapSizeX = map.getMapSizeX();
            mapSizeY = map.getMapSizeY();
            this.xPos = mapSizeX / 2 * Tile.TILEWIDTH;
            this.yPos = mapSizeY / 2 * Tile.TILEHEIGHT;
            camera.centerOnObject(this.getLocation());
        }

        public Pointer(Camera camera, int pMapSizeX, int pMapSizeY) {
            mapSizeX = pMapSizeX;
            mapSizeY = pMapSizeY;
            this.xPos = mapSizeX / 2 * Tile.TILEWIDTH;
            this.yPos = mapSizeY / 2 * Tile.TILEHEIGHT;
            camera.centerOnObject(this.getLocation());
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
