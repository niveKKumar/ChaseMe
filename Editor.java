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
    private static GamePanel gamePanel;
    public LinkedList maps = new LinkedList<EditorMap>();
    public EditorTileMenu tileMenu;
    public Pointer cameraPoint;
    public int selectedMap;
    private KeyManager keyManager;
    private int zoomSteps = 0;
    private int maxZoom = 10, minZoom = 10;
    private LinkedList recentList = new LinkedList();
    //    private boolean firstStart = true;
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

    public Editor(GamePanel gp, KeyManager pKeyManager) {
        gamePanel = gp;
        keyManager = pKeyManager;
        gp.getGUI().addKeyListener(keyManager);
        createMenu();
        createTileMenu();

    }

    /**
     * Erstellung von Objekten:
     */
//
    public JPanel createEditorMenu() {
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel editorPane = new JPanel(new GridBagLayout());
        editorPane.setFocusable(false);
        editorPane.setBackground(null);
        editorPane.setBorder(BorderFactory.createLineBorder(Color.green, 5));

        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.fill = GridBagConstraints.BOTH;
        int j = 2;
        String[] btNamesEditor = {"+", "-", "Tile Auswaehlen",};
        MenuButton[] editorbuttons = new MenuButton[btNamesEditor.length];
        for (int i = 0; i < editorbuttons.length; i++) {
            editorbuttons[i] = new MenuButton(btNamesEditor[i]);
            editorbuttons[i].setFocusable(false);
            editorbuttons[i].addActionListener(gamePanel.getGUI());
            MenuUI.addObject(gbc, editorbuttons[i], editorPane, 2, j++, 2, 2, true);
        }
        return editorPane;
    }


    public void createMenu() {
        // FIXME: 14.04.2019 Anständige Initialisierung (Nicht oben)
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


        mapSelectedBox = new JComboBox();
        mapSelectedBox.setFocusable(false);
        mapSelectedBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    selectedMap = mapSelectedBox.getSelectedIndex();
//                    System.out.println("selected" + selectedMap);
                    for (int i = 0; i < mapCheck.size(); i++) {
                        ((JCheckBox) mapCheck.get(i)).setSelected(false);
                    }
                    ((JCheckBox) mapCheck.get(selectedMap)).setSelected(true);

                    if (cameraPoint != null) {
                        cameraPoint.setLocation(((EditorMap) maps.get(selectedMap)).getMapSizeX() / 2, ((EditorMap) maps.get(selectedMap)).getMapSizeY() / 2);
                    }
                }
                gamePanel.requestFocus();
            }
        });

        mapSelectActionsPanel.add(mapSelectedBox);

        JButton btChapterOffset = new JButton("Chapter Offset setzen:");
        btChapterOffset.setFocusable(false);
        btChapterOffset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Meldungen m = new Meldungen(gamePanel.getGUI(), true, "null");
                m.chapterAbfrage();
                ((EditorMap) maps.get(selectedMap)).setchapterXOffset(Integer.parseInt(m.getUserInput(0)));
                ((EditorMap) maps.get(selectedMap)).setchapterYOffset(Integer.parseInt(m.getUserInput(1)));
                gamePanel.getGUI().requestFocus();
            }
        });
        mapSelectActionsPanel.add(btChapterOffset);

        autosaver = new Timer(45000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                autoSaveMap();
                gamePanel.getGUI().requestFocus();
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
                gamePanel.getGUI().requestFocus();
            }
        });
        btSimulate = new JButton("Simulate your Level");
        btSimulate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Level tempGame = new Level(gamePanel);
                MapBase[] temp = new Map[maps.size()];
                for (int i = 0; i < maps.size(); i++) {
                    temp[i] = ((EditorMap) maps.get(i));
                }
                tempGame.createlevel0(temp, null);
                tempGame.setLevel(0);
                gamePanel.getGUI().requestFocus();
            }
        });
        mapSelectActionsPanel.add(btAutosave);
        mapSelectActionsPanel.add(btSimulate);
        gamePanel.getGUI().west.add(recent);
        gamePanel.getGUI().east.add(selectMapPanel);
        gamePanel.getGUI().east.add(mapSelectActionsPanel);
    }


    public void createEditorMap(int mapSizeX, int mapSizeY, TileSet pTileSet, Integer tileID) {
        EditorMap editorMap = new EditorMap(gamePanel, mapSizeX, mapSizeY, pTileSet);
        editorMap.createEditorMap(tileID);
        maps.add(editorMap);
        gamePanel.setCamera(mapSizeX, mapSizeY);
        cameraPoint.setCamera(gamePanel.camera);
        createCheckbox();
    }

    public void createBlankEditorMap(int mapSizeX, int mapSizeY, TileSet pTileSet) {
        EditorMap editorMap = new EditorMap(gamePanel, mapSizeX, mapSizeY, pTileSet);
        editorMap.createBlankMap();
        maps.add(editorMap);
        gamePanel.setCamera(mapSizeX, mapSizeY);
        cameraPoint.setCamera(gamePanel.camera);
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
        if (tileMenu == null) {
            tileMenu = new EditorTileMenu(gamePanel.getGUI(), true, this);
            TileSet tempTS = new TileSet("Content/Graphics/tileSets/12x12x3 - tileSet.png", 12, 12, 3);
            //On release:
//            Meldungen m = new Meldungen(JFrame,true,"Map");
//            createEditorMap(Integer.parseInt(m.getUserInput(0)),Integer.parseInt(m.getUserInput(1)), tempTS, null);
            cameraPoint = new Pointer();
            createEditorMap(50, 50, tempTS, null);
            cameraPoint.setSpeed(15);
        } else {
            tileMenu.setVisible(true);
            ((EditorMap) maps.get(selectedMap)).setGraphicID(tileMenu.getSelectedID());
        } // Damit kein erneutes Starten immer entsteht
        gamePanel.getGUI().requestFocus();
    }

    public void addRecently(int id, TileSet ts) {
        recentList.add(new EditorTileButton(id, ts));
        if (recentList.size() >= maxRecent) {
            recentList.removeFirst();
        }
            displayRecent(recent);
    }

    public void update() {
        gamePanel.getGUI().taAnzeige.setText("\n" + "X:" + cameraPoint.getLocation().getX() + "Y:" + cameraPoint.getLocation().getY()
                + "\n" + "X- Offset:" + gamePanel.camera.getClickXOffset() + "\n" + "Y- Offset:" + gamePanel.camera.getClickYOffset());
        if (!bordercheck(cameraPoint) && gamePanel.getGUI().keyInputToMove(gamePanel.keyManager).getX() != 0 || !bordercheck(cameraPoint) && gamePanel.getGUI().keyInputToMove(gamePanel.keyManager).getY() != 0) {
            cameraPoint.setMove(gamePanel.getGUI().keyInputToMove(gamePanel.keyManager));
            gamePanel.camera.centerOnObject(cameraPoint);

        }
        if (!((EditorMap) maps.get(selectedMap)).isToIgnore()) {
            JLabel warning = new JLabel("Es können beim Speichern Fehler unterlaufen weil möglicherweise zwei verschiedene Tile Sets benutzt werden ", UIManager.getIcon("OptionPane.warningIcon"), SwingConstants.CENTER);
            warning.setForeground(Color.RED);

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
        int virtualSpace = 500;
        int bounce = virtualSpace / 4;
        leftBorder = pointer.getLocation().getX() < GUI.GAMEPANEL_WIDTH / 2 - virtualSpace;
        rightBorder = pointer.getLocation().getX() > (((EditorMap) maps.get(selectedMap)).getMapSizeX() + 1) * Tile.TILEWIDTH - GUI.GAMEPANEL_WIDTH / 2 + virtualSpace;
        upBorder = pointer.getLocation().getY() + virtualSpace < 0 + GUI.GAMEPANEL_HEIGHT / 2;
        downBorder = pointer.getLocation().getY() > (((EditorMap) maps.get(selectedMap)).getMapSizeY() + 1) * Tile.TILEHEIGHT - GUI.GAMEPANEL_HEIGHT / 2 + virtualSpace;
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
        gamePanel.repaint();
        panel.revalidate();
    }

    public void saveMap(File path, boolean notification) {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(path))) {
            EditorMap m = (EditorMap) maps.get(selectedMap);
            out.write(m.tileSet.getTileSetImagePath());
            out.newLine();
            out.write(m.getMapSizeX() + ";" + m.getMapSizeX());
            out.newLine();

            for (int zeile = 0; zeile < m.mapTiles.length; zeile++) {
                for (int spalte = 0; spalte < m.mapTiles[zeile].length; spalte++) {

                    out.write(m.mapTiles[zeile][spalte].getID() + ";");
                }
                out.newLine();
            }
            if (notification) {
                JOptionPane.showMessageDialog(gamePanel.getGUI(), "Daten erfolgreich gespeichert.");
            }
        } catch (Exception e) {
            if (notification) {
                JOptionPane.showMessageDialog(gamePanel.getGUI(), "Daten nicht gespeichert.", "", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void autoSaveMap() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        File output = new File("Content/Maps/autosave/" + timeStamp + "_AUTOSAVE.txt");
        saveMap(output, false);

    }

    public void loadMap() {
        Meldungen m = new Meldungen(gamePanel.getGUI(), true, "null");
        File f = Meldungen.setMapPath("Open");//Der Filechooeser liefert die Pfadangabe zu dem selektierten Speicherort.
        String path = f.getPath();
        Map mapTemp = new Map(gamePanel, path, "null", new Point(0, 0));
        //Übertragung der Map = lesen muss nicht extra für EditorMap implementier werden
        EditorMap em = new EditorMap(gamePanel, mapTemp.getMapSizeX(), mapTemp.getMapSizeY(), mapTemp.getTileSet());
        em.mapTiles = mapTemp.mapTiles;

        if (em != null) {
            maps.add(em);
            createCheckbox();
            gamePanel.setCamera(em.getMapSizeX(), em.getMapSizeY());
            cameraPoint.setCamera(gamePanel.camera);
            selectedMap = maps.size() - 1;
            JOptionPane.showMessageDialog(gamePanel.getGUI(), "Map erfolgreich geladen.");
        } else {
            JOptionPane.showMessageDialog(gamePanel.getGUI(), "Map konnte nicht geladen werden.", "", JOptionPane.ERROR_MESSAGE);
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
                    gamePanel.getGUI().requestFocus();
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
        gamePanel.camera.centerOnObject(cameraPoint);
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
                gamePanel.camera.centerOnObject(cameraPoint);
                cameraPoint.setCamera(gamePanel.camera);
                gamePanel.getGUI().requestFocus();
                break;

            case "-":
                System.out.println("Zoom raus");
                zoom(false);
                gamePanel.camera.centerOnObject(cameraPoint);
                cameraPoint.setCamera(gamePanel.camera);
                gamePanel.getGUI().requestFocus();
                break;
            case "Tile Auswaehlen":
                createTileMenu();
                gamePanel.getGUI().requestFocus();
                break;
        }

    }

    public void mouseClicked(MouseEvent e) {
        EditorMap m = (EditorMap) maps.get(selectedMap);
        TileSet ts = tileMenu.getTileSet(tileMenu.getSelectedTileSetIndex());
        if (keyManager.shift) {
            m.setTileRect(e, ts);
        } else {
            m.setClick(0);
            m.setTile(e, ts);
        }

        if (keyManager.str){
            m.setPointed(e);
        }
    }

    public void mouseDragged(MouseEvent e) {
        EditorMap m = (EditorMap) maps.get(selectedMap);
        TileSet ts = tileMenu.getTileSet(tileMenu.getSelectedTileSetIndex());
        m.setTile(e, ts);
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
