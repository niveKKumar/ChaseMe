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
    public LinkedList maps = new LinkedList<EditorMap>();
    public EditorTileMenu tileMenu;
    double zoomFactor = 0.25;
    public Pointer cameraPoint;
    public int selectedMap;
    private GUI gui;
    private int zoomSteps = 0;
    private int zoomRange = 10;
    private LinkedList recentList = new LinkedList();
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

    public Editor(GUI gui) {
        this.gui = gui;
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
            editorbuttons[i].addActionListener(gui);
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
                Level tempGame = new Level(gui.getGamePanel());
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
        // FIXME: 25.04.2019 Unschön...
        recent.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 3));
        selectMapPanel.setBorder(BorderFactory.createLineBorder(Color.PINK, 3));
        mapSelectActionsPanel.setBorder(BorderFactory.createLineBorder(Color.CYAN, 3));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = 0;
        gui.west.add(recent);
        gui.east.add(selectMapPanel, gbc);
        gbc.gridy = 1;
        gui.east.add(mapSelectActionsPanel, gbc);
    }


    public void createEditorMap(int mapSizeX, int mapSizeY, TileSet pTileSet, Integer tileID) {
        EditorMap editorMap = new EditorMap(gui.getGamePanel(), mapSizeX, mapSizeY, pTileSet);
        editorMap.createEditorMap(tileID);
        maps.add(editorMap);
        gui.getGamePanel().setCamera(mapSizeX, mapSizeY);
        cameraPoint.setCamera(gui.getGamePanel().getCamera());
        createCheckbox();
    }

    public void createBlankEditorMap(int mapSizeX, int mapSizeY, TileSet pTileSet) {
        EditorMap editorMap = new EditorMap(gui.getGamePanel(), mapSizeX, mapSizeY, pTileSet);
        editorMap.createBlankMap();
        maps.add(editorMap);
        gui.getGamePanel().setCamera(mapSizeX, mapSizeY);
        cameraPoint.setCamera(gui.getGamePanel().getCamera());
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
            tileMenu = new EditorTileMenu(gui, true, this);
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
        gui.debugAnzeige.setText("\n" + "X:" + cameraPoint.getLocation().getX() + "Y:" + cameraPoint.getLocation().getY()
                + "\n" + "X- Offset:" + gui.getGamePanel().getCamera().getClickXOffset() + "\n" + "Y- Offset:" + gui.getGamePanel().getCamera().getClickYOffset());
        if (!bordercheck(cameraPoint) && gui.keyInputToMove().getX() != 0 || !bordercheck(cameraPoint) && gui.keyInputToMove().getY() != 0) {
            cameraPoint.setMove(gui.keyInputToMove());
            gui.getGamePanel().getCamera().centerOnObject(cameraPoint);

        }
        if (!((EditorMap) maps.get(selectedMap)).isToIgnore()) {
            JLabel warning = new JLabel("Es können beim Speichern Fehler unterlaufen weil möglicherweise zwei verschiedene Tile Sets benutzt werden ", UIManager.getIcon("OptionPane.warningIcon"), SwingConstants.CENTER);
            warning.setForeground(Color.RED);
        }

        if (gui.keyManager.plus) {
            double before = zoomFactor;
            zoomFactor = 0.02;
            double comparison = before / zoomFactor;
            zoomRange = (int) Math.round(zoomRange * comparison);
            zoom(true);
            zoomFactor = before;
        }
        if (gui.keyManager.minus) {
            double before = zoomFactor;
            zoomFactor = 0.02;
            double comparison = before / zoomFactor;
            zoomRange = (int) Math.round(zoomRange * comparison);
            zoom(false);
            zoomFactor = before;
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
        int bounce = 100;
        leftBorder = pointer.getLocation().getX() < GamePanel.GAMEPANEL_WIDTH / 2;
        rightBorder = pointer.getLocation().getX() > (((EditorMap) maps.get(selectedMap)).getMapSizeX()) * Tile.TILEWIDTH - GamePanel.GAMEPANEL_WIDTH / 2;
        upBorder = pointer.getLocation().getY() < 0 + GamePanel.GAMEPANEL_HEIGHT / 2;
        downBorder = pointer.getLocation().getY() > (((EditorMap) maps.get(selectedMap)).getMapSizeY()) * Tile.TILEHEIGHT - GamePanel.GAMEPANEL_HEIGHT / 2;
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
        gui.getGamePanel().repaint();
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
        File f = Meldungen.setMapPath("Open");//Der Filechooeser liefert die Pfadangabe zu dem selektierten Speicherort.
        String path = f.getPath();
        Map mapTemp = new Map(gui.getGamePanel(), path, "null", new Point(0, 0));
        //Übertragung der Map = lesen muss nicht extra für EditorMap implementier werden
        EditorMap em = new EditorMap(gui.getGamePanel(), mapTemp.getMapSizeX(), mapTemp.getMapSizeY(), mapTemp.getTileSet());
        em.mapTiles = mapTemp.mapTiles;

        if (em != null) {
            maps.add(em);
            createCheckbox();
            gui.getGamePanel().setCamera(em.getMapSizeX(), em.getMapSizeY());
            cameraPoint.setCamera(gui.getGamePanel().getCamera());
            selectedMap = maps.size() - 1;
            JOptionPane.showMessageDialog(gui, "Map erfolgreich geladen.");
        } else {
            JOptionPane.showMessageDialog(gui, "Map konnte nicht geladen werden.", "", JOptionPane.ERROR_MESSAGE);
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
        gui.getGamePanel().getCamera().centerOnObject(cameraPoint);
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
                gui.getGamePanel().getCamera().centerOnObject(cameraPoint);
                cameraPoint.setCamera(gui.getGamePanel().getCamera());
                gui.requestFocus();
                break;

            case "-":
                System.out.println("Zoom raus");
                zoom(false);
                gui.getGamePanel().getCamera().centerOnObject(cameraPoint);
                cameraPoint.setCamera(gui.getGamePanel().getCamera());
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
        TileSet ts = tileMenu.getTileSet(tileMenu.getSelectedTileSetIndex());
        if (gui.keyManager.shift) {
            m.setTileRect(e, ts);
        } else {
            m.setClick(0);
            m.setTile(e, ts);
        }

        if (gui.keyManager.str) {
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
