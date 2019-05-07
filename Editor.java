import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;

public class Editor {
    //On release : Lokal in borderCheck !!
    boolean leftBorder;
    boolean rightBorder;
    public LinkedList maps = new LinkedList<EditorMap>();
    public EditorTileMenu tileMenu;
    double zoomFactor = 0.25;
    public Pointer cameraPoint;
    public int selectedMap;
    private int zoomSteps = 0;
    private int zoomRange = 10;
    private LinkedList recentList = new LinkedList();
    private int maxRecent = 10;
    public JLabel idAnzeige;
    private Timer autosaver;
    boolean upBorder;

    private JPanel selectMapPanel = new JPanel();
    private JPanel mapSelectActionsPanel = new JPanel(new GridLayout(0, 1));
    private JComboBox mapSelectedBox;
    private JButton btAutosave;
    private JButton btSimulate;
    private LinkedList mapCheck = new LinkedList<JCheckBox>();
    private GroupLayout layout;
    boolean downBorder;
    private GamePanel gamePanel;
    private KeyManager keyListener;
    private JPanel recent = new JPanel(new BorderLayout());
    private JTextArea editorAnzeige;
    private JTextArea customNot;
    private JLabel warning;
    private Frame owner;

    public Editor(Frame owner, GamePanel gp, KeyManager pKeyListener) {
        gamePanel = gp;
        this.owner = owner;
        keyListener = pKeyListener;
        editorAnzeige = new JTextArea();
        customNot = new JTextArea();
        GUI.addToDebugPane(editorAnzeige);
        GUI.addToDebugPane(customNot);
        warning = new JLabel("Es können beim Speichern wegen verschiedenen TileSets Fehler unterlaufen \n ", UIManager.getIcon("OptionPane.warningIcon"), SwingConstants.CENTER);
        warning.setBackground(Color.RED);
        warning.setForeground(Color.WHITE);
        warning.setFont(warning.getFont().deriveFont(Font.BOLD, 5));
        GUI.addToEast(warning);
        // TODO: 04.05.2019 Langes Laden !!
        System.out.println("Editor : Create MenuTab");
        createMenu();
        System.out.println("Editor : Create TileMenu");
        createTileMenu();
        System.out.println("Editor : Finished");

    }

    /**
     * Erstellung von Objekten:
     */
//
    public MenuUI.MenuTab createEditorMenu() {
        MenuUI.MenuTab menuTab = new MenuUI.MenuTab(new String[]{"+", "-", "Tile MenuTab"});
        return menuTab;
    }


//    public JPanel oldWaycreateEditorMenu() {
//        GridBagConstraints gbc = new GridBagConstraints();
//        JPanel editorPane = new JPanel(new GridBagLayout());
//        editorPane.setFocusable(false);
//        editorPane.setBackground(null);
//        editorPane.setBorder(BorderFactory.createLineBorder(Color.green, 5));
//
//        gbc.insets = new Insets(5, 0, 5, 0);
//        gbc.fill = GridBagConstraints.BOTH;
//        int j = 2;
//        String[] btNamesEditor = {"+", "-", "Tile Auswaehlen"};
//        MenuButton[] editorbuttons = new MenuButton[btNamesEditor.length];
//        for (int i = 0; i < editorbuttons.length; i++) {
//            editorbuttons[i] = new MenuButton(btNamesEditor[i]);
//            editorbuttons[i].setFocusable(false);
//            editorbuttons[i].addActionListener(gui);
//            MenuUI.addObject(gbc, editorbuttons[i], editorPane, 2, j++, 2, 2, true);
//        }
//        return editorPane;
//    }


    public void createMenu() {
        // FIXME: 14.04.2019 Anständige Initialisierung (Nicht oben)
        JPanel sideMenu = new JPanel();
        
        idAnzeige = new JLabel();
        idAnzeige.setFocusable(false);
        idAnzeige.setText("Kein Tile ausgewählt");
        idAnzeige.setBorder(BorderFactory.createLineBorder(Color.black));

        selectMapPanel.setLayout(new GridLayout(0,2,5,5));
        selectMapPanel.add(idAnzeige);
        layout = new GroupLayout(selectMapPanel);
        layout.setAutoCreateGaps(true);


        recent.setLayout(new GridLayout(maxRecent, 1, 5, 5));

        mapSelectedBox = new JComboBox();
        mapSelectedBox.setFocusable(false);
        mapSelectedBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    selectedMap = mapSelectedBox.getSelectedIndex();
                    for (int i = 0; i < mapCheck.size(); i++) {
                        ((JCheckBox) mapCheck.get(i)).setSelected(false);
                    }
                    ((JCheckBox) mapCheck.get(selectedMap)).setSelected(true);

                    if (cameraPoint != null) {
                        System.out.println("CameraPoint is moved to middle of Map No: " + selectedMap);
                        EditorMap currentMap = ((EditorMap) maps.get(selectedMap));
                        cameraPoint.setLocation((int) (currentMap.getMapSizeX() + currentMap.getChapterOffset().getX()) / 2, (int) (currentMap.getMapSizeY() + currentMap.getChapterOffset().getY()) / 2);
                    }
                }
            }
        });

        mapSelectActionsPanel.add(mapSelectedBox);

        JButton btChapterOffset = new JButton("Chapter Offset setzen:");
        btChapterOffset.setFocusable(false);
        btChapterOffset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Meldungen m = new Meldungen(null, true, "null");
                m.chapterAbfrage();

                try {
                    ((EditorMap) maps.get(selectedMap)).setchapterXOffset(Integer.parseInt(m.getUserInput(0)));
                } catch (NumberFormatException ex) {
                    ((EditorMap) maps.get(selectedMap)).setchapterXOffset(0);
                }

                try {
                    ((EditorMap) maps.get(selectedMap)).setchapterYOffset(Integer.parseInt(m.getUserInput(1)));
                } catch (NumberFormatException ex) {
                    ((EditorMap) maps.get(selectedMap)).setchapterYOffset(0);
                }
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

            }
        });
        btSimulate = new JButton("Simulate your Game");
        btSimulate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Game tempGame = new Game(gamePanel, keyListener);
                MapBase[] temp = new Map[maps.size()];
                for (int i = 0; i < maps.size(); i++) {
                    temp[i] = ((EditorMap) maps.get(i));
                }
                tempGame.createlevel0(temp, null);
                tempGame.setLevel(0);

            }
        });
        mapSelectActionsPanel.add(btAutosave);
        mapSelectActionsPanel.add(btSimulate);

        recent.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 3));
        selectMapPanel.setBorder(BorderFactory.createLineBorder(Color.PINK, 3));
        mapSelectActionsPanel.setBorder(BorderFactory.createLineBorder(Color.CYAN, 3));

        // FIXME: 29.04.2019 Andere Lösung = unabhängig von GUI
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        GUI.east.add(recent, gbc);
        GUI.addToEast(selectMapPanel);
        GUI.addToEast(mapSelectActionsPanel);
    }


    public void createEditorMap(int mapSizeX, int mapSizeY, TileSet pTileSet, Integer tileID) {
        EditorMap editorMap = new EditorMap(gamePanel, mapSizeX, mapSizeY, pTileSet);
        editorMap.createEditorMap(tileID);
        maps.add(editorMap);
        gamePanel.setCamera(mapSizeX, mapSizeY, editorMap.getChapterOffset());
        cameraPoint.setCamera(gamePanel.getCamera());
        createCheckbox();
    }

    public void createBlankEditorMap(int mapSizeX, int mapSizeY, TileSet pTileSet) {
        EditorMap editorMap = new EditorMap(gamePanel, mapSizeX, mapSizeY, pTileSet);
        editorMap.createBlankMap();
        maps.add(editorMap);
        gamePanel.setCamera(mapSizeX, mapSizeY, editorMap.getChapterOffset());
        cameraPoint.setCamera(gamePanel.getCamera());
        createCheckbox();
    }

    public void renderEditor(Graphics2D g2d) {
        for (int i = 0; i < checkMapBoxes().size() ; i++) {
            int enabledIndex = checkMapBoxes().get(i);
            ( (EditorMap) maps.get(enabledIndex)).renderMap(g2d);
            //            System.out.println("Aktivierte Map mit dem Index:"+ enabledIndex +"\n");
        }
    }


    public void createTileMenu() {
        if (tileMenu == null) {
            System.out.println("Editor : Create TileMenu");
            tileMenu = new EditorTileMenu(owner, false, this);
            System.out.println("Editor : Create Standard TS");
            TileSet tempTS = new TileSet("Content/Graphics/tileSets/12x12x3 - tileSet.png", 12, 12, 3);
            //On release:
//            Meldungen m = new Meldungen(JFrame,true,"Map");
//            createEditorMap(Integer.parseInt(m.getUserInput(0)),Integer.parseInt(m.getUserInput(1)), tempTS, null);
            cameraPoint = new Pointer();
            createEditorMap(50, 50, tempTS, null);
            cameraPoint.setSpeed(15);
            System.out.println("Editor : Finished");
        } else {
            tileMenu.setVisible(true);
            ((EditorMap) maps.get(selectedMap)).setGraphicID(tileMenu.getSelectedID());
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
        try {
            if (!bordercheck(cameraPoint, ((EditorMap) maps.get(selectedMap))) && GUI.keyInputToMove(keyListener).getX() != 0 || !bordercheck(cameraPoint, ((EditorMap) maps.get(selectedMap))) && GUI.keyInputToMove(keyListener).getY() != 0) {
                cameraPoint.setMove(GUI.keyInputToMove(keyListener));
                gamePanel.getCamera().centerOnObject(cameraPoint);
            }
            if (((EditorMap) maps.get(selectedMap)).isToIgnore()) {
                warning.setVisible(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Selected Map is not in the Maps list ! " +
                    "\n The Map has a size of " + maps.size() + "and the selected Map is " + selectedMap);
        }

        if (keyListener.plus) {
            double before = zoomFactor;
            zoomFactor = 0.02;
            double comparison = before / zoomFactor;
            zoomRange = (int) Math.round(zoomRange * comparison);
            zoom(true);
            zoomFactor = before;
        }
        if (keyListener.minus) {
            double before = zoomFactor;
            zoomFactor = 0.02;
            double comparison = before / zoomFactor;
            zoomRange = (int) Math.round(zoomRange * comparison);
            zoom(false);
            zoomFactor = before;
        }
        if (tileMenu.isVisible()) {

        }
    }

    private void debugPane(int maxBorderX, int maxBorderY) {
        editorAnzeige.setText("Editor: "
                + "\n selected Map :" + selectedMap + "from total: " + maps.size()
                + "\n Current Map Stats -"
                + "\n X-Size: " + ((EditorMap) maps.get(selectedMap)).getMapSizeX()
                + "\n Y-Size: " + ((EditorMap) maps.get(selectedMap)).getMapSizeY()
                + "\n Chapter X-Offset: " + ((EditorMap) maps.get(selectedMap)).getChapterOffset().getX()
                + "\n Chapter Y-Offset: " + ((EditorMap) maps.get(selectedMap)).getChapterOffset().getY()
                + "\n Current CameraPointer -"
                + "\n X-Pos: " + cameraPoint.getLocation().getX() + " maxXPos: " + (maxBorderX - GUI.GAMEPANEL_WIDTH / 2)
                + "\n Y-Pos: " + cameraPoint.getLocation().getY() + " maxYPos: " + (maxBorderY - GUI.GAMEPANEL_HEIGHT / 2)
                + "\n X-Offset:" + gamePanel.getCamera().getXOffset() + " max : " + (maxBorderX - GUI.GAMEPANEL_WIDTH)
                + "\n Y- Offset:" + gamePanel.getCamera().getYOffset() + " max : " + (maxBorderY - GUI.GAMEPANEL_HEIGHT)
        );

        String[] length = customNot.getText().split("\n");
        if (length.length > 10) {
            customNot.setText("");
        }

    }

    public boolean bordercheck(Pointer pointer, EditorMap em) {
        /**
         * Check ob Pointer am Rand ist (-> Etwas "Handlungsspielraum um über die Grenze zu gehen damit alle Tiles erreichbar sind -> virtualspace)
         * - true = Position des Pointers ist kleiner (block) + Rückstoß
         * - false =  Position des Pointers ist groeßer (no block)
         *
         */
        double bounce = 1;
//        leftBorder = pointer.getLocation().getX() < GUI.GAMEPANEL_WIDTH / 2;
//        rightBorder = pointer.getLocation().getX() > (((EditorMap) maps.get(selectedMap)).getMapSizeX()) * Tile.TILEWIDTH - GUI.GAMEPANEL_WIDTH / 2;
//        upBorder = pointer.getLocation().getY() < 0 + GUI.GAMEPANEL_HEIGHT / 2;
//        downBorder = pointer.getLocation().getY() > (((EditorMap) maps.get(selectedMap)).getMapSizeY()) * Tile.TILEHEIGHT - GUI.GAMEPANEL_HEIGHT / 2;
        int maxxBorder = em.getMapSizeX() * Tile.TILEWIDTH + em.chapterXOffset, maxyBorder = em.getMapSizeY() * Tile.TILEHEIGHT + em.chapterYOffset;
        int minxBorder = em.chapterXOffset, minyBorder = em.chapterYOffset;
        leftBorder = pointer.getLocation().getX() < minxBorder + GUI.GAMEPANEL_WIDTH / 2;

        rightBorder = pointer.getLocation().getX() > maxxBorder - GUI.GAMEPANEL_WIDTH / 2;

        upBorder = pointer.getLocation().getY() < minyBorder + GUI.GAMEPANEL_WIDTH / 2;

        downBorder = pointer.getLocation().getY() > maxyBorder - GUI.GAMEPANEL_WIDTH / 2;


        debugPane(maxxBorder, maxyBorder);
        if (leftBorder || rightBorder || upBorder || downBorder) {
            Camera cam = gamePanel.getCamera();
            if (leftBorder) {
                customNot.append("Left Bounce \n");
                pointer.setXPos((int) pointer.getLocation().getX() + bounce);
                gamePanel.getCamera().centerOnObject(pointer);
            }
            if (rightBorder) {
                customNot.append("Right Bounce \n");
                pointer.setXPos((int) pointer.getLocation().getX() - bounce);
                gamePanel.getCamera().centerOnObject(pointer);
            }
            if (upBorder) {
                customNot.append(" Up Bounce \n");
                pointer.setYPos((int) pointer.getLocation().getY() + bounce);
                gamePanel.getCamera().centerOnObject(pointer);
            }
            if (downBorder) {
                customNot.append(" Down Bounce \n");
                pointer.setYPos((int) pointer.getLocation().getY() - bounce);
                gamePanel.getCamera().centerOnObject(pointer);
            }
            return true;
        } else {
            String[] length = customNot.getText().split("\n");
            if (length.length > 0 && !length[length.length - 1].contentEquals("\n")) {
                customNot.append("\n");
            }
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
                    m.setTileSet(new TileSet(temp.getTileSetPath()));
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
        gamePanel.repaint();
        panel.revalidate();
        panel.setBorder(BorderFactory.createLineBorder(Color.yellow, 3));
    }

    public void saveMap(File path, boolean notification) {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(path))) {
            EditorMap m = (EditorMap) maps.get(selectedMap);
            out.write(m.tileSet.getTileSetImagePath());
            out.newLine();
            out.write(m.getMapSizeX() + ";" + m.getMapSizeX());
            out.newLine();

            System.out.println("Saving Map");
            for (int zeile = 0; zeile < m.mapTiles.length; zeile++) {
                String line = "";
                for (int spalte = 0; spalte < m.mapTiles[zeile].length - 1; spalte++) {
                    line = line + m.mapTiles[zeile][spalte].getID() + ";";
                }
                line = line + m.mapTiles[zeile][m.mapTiles[zeile].length - 1].getID();
                out.write(line);
                out.newLine();
            }
            if (notification) {
                JOptionPane.showMessageDialog(null, "Daten erfolgreich gespeichert.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (notification) {
                JOptionPane.showMessageDialog(null, "Daten nicht gespeichert.", "", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void autoSaveMap() {
        String timeStamp = new SimpleDateFormat("MM:dd_HH_mm").format(Calendar.getInstance().getTime());
        File output = new File("Content/Maps/autosave/" + timeStamp + "_AUTOSAVE.txt");
        saveMap(output, false);

    }

    public void loadMap() {
        Meldungen m = new Meldungen(null, true, "null");
        File f = Meldungen.getFileAt("Open");//Der Filechooeser liefert die Pfadangabe zu dem selektierten Speicherort.
        String path = f.getPath();
        Map mapTemp = new Map(gamePanel, path, "null", new Point(0, 0));
        //Übertragung der Map = lesen muss nicht extra für EditorMap implementier werden
        EditorMap em = new EditorMap(gamePanel, mapTemp.getMapSizeX(), mapTemp.getMapSizeY(), mapTemp.getTileSet());
        em.mapTiles = mapTemp.mapTiles;

        if (em != null) {
            maps.add(em);
            createCheckbox();
            gamePanel.setCamera(em.getMapSizeX(), em.getMapSizeY(), em.getChapterOffset());
            cameraPoint.setCamera(gamePanel.getCamera());
            selectedMap = maps.size() - 1;
            JOptionPane.showMessageDialog(null, "Map erfolgreich geladen.");
        } else {
            JOptionPane.showMessageDialog(null, "Map konnte nicht geladen werden.", "", JOptionPane.ERROR_MESSAGE);
        }

    }

    public void createCheckbox(){
        int name = 0;
        int h = 15;
        mapCheck.clear();
        mapSelectedBox.removeAllItems();
        selectMapPanel.removeAll();

        for (int i = 0; i < maps.size() ; i++) {
            name++;
            mapCheck.add(new JCheckBox("Map " + name));
            JButton temp = new JButton(new ImageIcon(((new ImageIcon("Content/Graphics/UI/deleteIcon.png")).getImage()).getScaledInstance(h, h, Image.SCALE_SMOOTH)));
            temp.setOpaque(false);
            temp.setMaximumSize(new Dimension(h, h));
            temp.setBorder(null);
            temp.addActionListener(new RemoveBTActionListener(i) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    super.actionPerformed(e);
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
        selectedMap = maps.size() - 1;
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
        gamePanel.getCamera().centerOnObject(cameraPoint);
        if (zoomInIsTrueZoomOutisFalse) {
            //Reinzoom:
            if (zoomSteps < +(zoomRange)) {
                zoomSteps = zoomSteps + 1;
                zoomFactor = 1 + zoomFactor;
                Tile.setTILEWIDTH((int) Math.round(Tile.TILEWIDTH * zoomFactor));
                Tile.setTILEHEIGHT((int) Math.round(Tile.TILEHEIGHT * zoomFactor));
            } else {
                zoomSteps = zoomRange;
            }
        } else {
            //Rauszoom:
            if (zoomSteps > -(zoomRange)) {
                zoomSteps = zoomSteps - 1;
                zoomFactor = 1 - zoomFactor;
                Tile.setTILEWIDTH((int) Math.round(Tile.TILEWIDTH * zoomFactor));
                Tile.setTILEHEIGHT((int) Math.round(Tile.TILEHEIGHT * zoomFactor));
            } else {
                zoomSteps = -zoomRange;
            }
        }

        gamePanel.getCamera().centerOnObject(cameraPoint);
        cameraPoint.setCamera(gamePanel.getCamera());
        gamePanel.repaint();

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

    public void mouseClicked(MouseEvent e) {
        EditorMap m = (EditorMap) maps.get(selectedMap);
        TileSet ts = tileMenu.getTileSet(tileMenu.getSelectedTileSetIndex());
        if (keyListener.shift) {
            m.setTileRect(e, ts);
        } else {
            m.setClick(0);
            m.setTile(e, ts);
        }

        keyListener.update();
        gamePanel.repaint();
    }

    public void mouseDragged(MouseEvent e) {
        EditorMap m = (EditorMap) maps.get(selectedMap);
        TileSet ts = tileMenu.getTileSet(tileMenu.getSelectedTileSetIndex());
        m.setTile(e, ts);
        gamePanel.repaint();
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
