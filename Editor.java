import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;

public class Editor {
    private static GUI gui;
    public LinkedList maps = new LinkedList<EditorMap>();
    public EditorTileMenu tileMenu;
    public Pointer cameraPoint;
    private JButton[] editorbuttons = new JButton[3];
    private int use = 0;
    private KeyManager keyManager;
    private int zoomSteps = 0;
    private int maxZoom = 10, minZoom = 10;
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
    private JButton btSimulate;
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

    /**
     * Erstellung von Objekten:
     */
    public void createMenu() {
        // FIXME: 14.04.2019 Anständige Initialisirung (Nicht oben)
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
                        cameraPoint.setLocation(((EditorMap) maps.get(selectedMap)).getMapSizeX() / 2, ((EditorMap) maps.get(selectedMap)).getMapSizeY() / 2);
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
                gui.requestFocus();
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
        btSimulate = new JButton("Simulate your Level");
        btSimulate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Level tempGame = new Level(gui);
                MapBase[] temp = new Map[maps.size()];
                for (int i = 0; i < maps.size(); i++) {
                    temp[i] = ((EditorMap) maps.get(i));
                }
                tempGame.createlevel0(temp, null);
                tempGame.setLevel(0);
                gui.requestFocus();
            }
        });
        mapSelectActionsPanel.add(btAutosave);
        mapSelectActionsPanel.add(btSimulate);
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
        gui.setCamera(mapSizeX, mapSizeY);
        cameraPoint = new Pointer(gui.camera);
        selectedMap = maps.size() - 1;

        createCheckbox();
    }

    public void createBlankEditorMap(int mapSizeX, int mapSizeY, TileSet pTileSet) {
        selectedMap = maps.size();
        EditorMap editorMap = new EditorMap(gui, mapSizeX, mapSizeY, pTileSet);
        editorMap.createBlankMap();
        maps.add(editorMap);
        gui.setCamera(mapSizeX, mapSizeY);
        cameraPoint = new Pointer(gui.camera);
        selectedMap = maps.size() - 1;

        createCheckbox();
    }

    public void renderEditor(Graphics2D g2d) {
        for (int i = 0; i < checkMapBoxes().size() ; i++) {
            int enabledIndex = checkMapBoxes().get(i);
            ( (EditorMap) maps.get(enabledIndex)).renderMap(g2d);
            //            System.out.println("Aktivierte Map mit dem Index:"+ enabledIndex +"\n");
        }
        if (maps.size() != 0) {
            ((EditorMap) maps.get(selectedMap)).renderMap(g2d); // Selected Map immer oberhalb
        }
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
        gui.requestFocus();
    }

    public void addRecently(int id, TileSet ts) {
        recentList.add(new EditorTileButton(id, ts));
        if (recentList.size() >= maxRecent) {
            recentList.removeFirst();
        }
            displayRecent(recent);
    }

    public void update() {
        gui.taAnzeige.setText("\n" + "X:" + cameraPoint.getLocation().getX() + "Y:" + cameraPoint.getLocation().getY()
                + "\n" + "X- Offset:" + gui.camera.getXOffset() + "\n" + "Y- Offset:" + gui.camera.getYOffset());
        if (!bordercheck(cameraPoint) && Level.keyInputToMove(gui.keyManager).getX() != 0 || !bordercheck(cameraPoint) && Level.keyInputToMove(gui.keyManager).getY() != 0) {
            cameraPoint.setMove(Level.keyInputToMove(gui.keyManager));
            gui.camera.centerOnObject(cameraPoint);

        }
    }

    public boolean bordercheck(Pointer pointer) {
        /**
         * Check ob Pointer am Rand ist (-> Etwas "Handlungsspielraum um über die Grenze zu gehen damit alle Tiles erreichbar sind -> virtualspace)
         * - true = Position des Pointers ist kleiner (block) + Rückstoß
         * - false =  Position des Pointers ist groeßer (no block)
         *
         */
        boolean leftBorder;
        boolean rightBorder;
        boolean upBorder;
        boolean downBorder;
        int virtualSpace = 64 * pointer.speed;
        int bounce = virtualSpace / 8;
        leftBorder = pointer.getLocation().getX() + virtualSpace < GUI.FRAME_WIDTH / 2;
        rightBorder = pointer.getLocation().getX() - virtualSpace > ((EditorMap) maps.get(selectedMap)).getMapSizeX() * Tile.TILEWIDTH - GUI.FRAME_WIDTH / 2;
        upBorder = pointer.getLocation().getY() + virtualSpace < 0 + GUI.FRAME_HEIGHT / 2;
        downBorder = pointer.getLocation().getY() - virtualSpace > ((EditorMap) maps.get(selectedMap)).getMapSizeY() * Tile.TILEHEIGHT - GUI.FRAME_WIDTH / 2;
        if (leftBorder || rightBorder || upBorder || downBorder) {
            if (leftBorder == false) {
                pointer.setXPos((int) pointer.getLocation().getX() - bounce);
            }
            if (rightBorder == false) {
                pointer.setXPos((int) pointer.getLocation().getX() + bounce);
            }
            if (upBorder == false) {
                pointer.setYPos((int) pointer.getLocation().getY() - bounce);
            }
            if (downBorder == false) {
                pointer.setYPos((int) pointer.getLocation().getY() + bounce);
            }
            gui.camera.centerOnObject(pointer);
            return true;
        } else {
            return false;
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
            createCheckbox();
            gui.setCamera(mapSizeX, mapSizeY);
            cameraPoint = new Pointer(gui.camera);
            selectedMap = maps.size() - 1;

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
            if (((JCheckBox) mapCheck.get(i)).isSelected() && !indexOfActivatedMaps.contains(i)) {
                indexOfActivatedMaps.add(i);
            }

        }
        return indexOfActivatedMaps;
    }

    public void zoom(boolean zoomInIsTrueZoomOutisFalse){
        // TODO: 23.03.2019 Zoom an Mapgroesse angepasst EDIT: NICHT WIRKLICH NÖTIG...
        EditorMap m = (EditorMap) maps.get(selectedMap);
        gui.camera.centerOnObject(cameraPoint);
        double zoom = 0.25;
        if (zoomInIsTrueZoomOutisFalse) {
            //Reinzoom:
            if (zoomSteps < maxZoom) {
                zoomSteps = zoomSteps + 1;
                zoom = 1 + zoom;
                Tile.setTILEWIDTH((int) Math.round(Tile.TILEWIDTH * zoom));
                Tile.setTILEHEIGHT((int) Math.round(Tile.TILEHEIGHT * zoom));
            } else {
                zoomSteps = 5;
            }
        } else {
            //Rauszoom:
            if (zoomSteps > -minZoom) {
                zoomSteps = zoomSteps - 1;
                zoom = 1 - zoom;
                Tile.setTILEWIDTH((int) Math.round(Tile.TILEWIDTH * zoom));
                Tile.setTILEHEIGHT((int) Math.round(Tile.TILEHEIGHT * zoom));
            } else {
                zoomSteps = -5;
            }
        }

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
                gui.camera.centerOnObject(cameraPoint);
                gui.requestFocus();
                break;

            case "-":
                System.out.println("Zoom raus");
                zoom(false);
                gui.camera.centerOnObject(cameraPoint);
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

    public void mouseDragged(MouseEvent e) {
        EditorMap m = (EditorMap) maps.get(selectedMap);
        m.setTile(e);
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
}
