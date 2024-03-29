import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class Editor implements ActionListener {

    /**
     * Editor Klasse womit Maps erstellt und gespeichert werden können
     * Am Quellcode nichts verändern !
     */

    // eigene Objekte:
    public EditorTileMenu tileMenu;
    public LinkedList<EditorMap> maps = new LinkedList<>();
    //Core Objects:
    public Pointer cameraPoint;
    public int selectedMap;
    public JLabel idAnzeige;
    //Editor Konfiguration:
    //Kann editiert werden!
    double bounce = 1;
    boolean upBorder;
    double zoomFactor = 0.25;
    boolean leftBorder;
    boolean rightBorder;
    boolean downBorder;
    //Benutzerführung:
    JPanel selectMapPanel = new JPanel();
    private boolean autosave = true;
    private int maxRecentTiles = 10;
    private int zoomSteps = 0;
    private int zoomRange = 10;
    private Timer autosaver;
    private LinkedList recentListTiles = new LinkedList();
    private LinkedList<JCheckBox> mapCheck = new LinkedList<>();
    private boolean active;
    private Frame owner;
    private GamePanel gamePanel;
    private JPanel mapSelectActionsPanel = new JPanel(new GridLayout(0, 1));
    private KeyManager keyListener;
    private JComboBox mapSelectedBox;
    private JButton btAutosave;
    private GroupLayout layout;
    private JPanel recent = new JPanel(new BorderLayout());

    /**
     * Konstruktor:
     */
    public Editor(Frame pOwner, GamePanel gp, KeyManager pKeyListener, JPanel recentPane) {
        gamePanel = gp;
        owner = pOwner;
        keyListener = pKeyListener;
        createMenu();
        createTileMenu();
        active = true;
    }

    public void clear() {
        selectMapPanel.removeAll();
        recent.removeAll();

        active = false;
    }

    /**
     * Erstellung Editor Menü (in Form eines MenüTabs):
     */
    public MenuUI.MenuTab createEditorMenu() {
        MenuUI.MenuTab menuTab = new MenuUI.MenuTab("shouldBeSelectedByTheManager", new String[]{"+", "-", "Tile MenuTab"}, true, false, true);
        return menuTab;
    }

    public JPanel createInfoPane() {
        InfoTextArea info = new InfoTextArea("");
        info.appendHeading("Info");
        info.appendRegularText
                ("Willkommen zum Editor ! Du willst wissen was der Editor kann? " +
                        " Es können ganz einfach Maps erstellt werden" +
                        " Es gibt ein eigenes Menü für den Editor, dazu einfach" +
                        " auf das Menü (oben in der Leiste) -> Editor -> Tile Menü" +
                        " Du kannst deine gewünschten Tiles mit Doppelklick direkt anwählen oder dein Tile  durch einen Klick und und per Bestätigen auswählen." +
                        " Um die Tile zu verändern einfach auf die gewünschte Fläche in deiner Map klicken/ziehen ." +
                        " Mit der Shift Taste und zwei Klicks können ganze Flächen ausgewählt und geändert werden" +
                        " Mit der STR Taste können Tiles aus der Map ganz einfach kopiert und verwendet werden." +
                        " Kamera - Steuerung:" +
                        "\n W: Kamera nach oben bewegen" +
                        "\n  A: Kamera nach links bewegen" +
                        "\n  S: Kamera nach unten bewegen" +
                        "\n  D: Kamera nach rechts bewegen" +
                        " - : Zoom verringern | + : Zoom erhöhen" +
                        " Im Tile Menü kann außerdem mit einem dreifach-Schnellklick die TileID abgefragt werden" +
                        " Um Maps zu Speichern einfach auf Speichern klicken und im gewünschten Pfad den Namen eintippen" +
                        " Wenn du Autosave eingeschaltet hast werden die Maps automatisch im autosave Ordner gespeichert." +
                        " Das Laden von TileSets und Maps kann ganz einfach in der Leiste im Menü gemacht werden." +
                        " Du kannst auch direkt mehrere TileSets aufeinmal laden, was aber durchaus zu Verzögerungen führen kann" +
                        " Viel Spaß beim Maps bauen  -Kevin");
        return info;
    }

    /**
     * Erstellung der benötigten Anzeige-Objekten:
     * -> Anzeige der Map Ebenen
     * -> Erstellen der Checkboxen um Maps sichtbar zu machen (createCheckBox())
     * -> Button um Map zu verschieben (Chapter Offset)
     * -> Button für automatisches Speichern der Maps ! - vorher nur die aktive Map
     * -> Leiste für vorherige Tiles (recentTiles)
     */
    public void createMenu() {
        idAnzeige = new JLabel();
        idAnzeige.setFocusable(false);
        idAnzeige.setText("Kein Tile ausgewählt");

        selectMapPanel.setLayout(new GridLayout(0, 2, 5, 5));
        selectMapPanel.add(idAnzeige);
        layout = new GroupLayout(selectMapPanel);
        layout.setAutoCreateGaps(true);

        mapSelectActionsPanel.add(selectMapPanel);

        recent.setLayout(new GridLayout(maxRecentTiles, 1, 5, 5));

        mapSelectedBox = new JComboBox();
        mapSelectedBox.setFocusable(false);
        mapSelectedBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    selectedMap = mapSelectedBox.getSelectedIndex();
                    for (int i = 0; i < mapCheck.size(); i++) {
                        mapCheck.get(i).setSelected(false);
                    }
                    mapCheck.get(selectedMap).setSelected(true);

                    if (cameraPoint != null) {
                        EditorMap currentMap = maps.get(selectedMap);
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
                    maps.get(selectedMap).setchapterXOffset(Integer.parseInt(m.getUserInput(0)));
                } catch (NumberFormatException ex) {
                    maps.get(selectedMap).setchapterXOffset(0);
                }

                try {
                    maps.get(selectedMap).setchapterYOffset(Integer.parseInt(m.getUserInput(1)));
                } catch (NumberFormatException ex) {
                    maps.get(selectedMap).setchapterYOffset(0);
                }
            }
        });
        mapSelectActionsPanel.add(btChapterOffset);

        autosaver = new Timer(30000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (autosave) {
                    autoSaveMap();
                }

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
        mapSelectActionsPanel.add(btAutosave);

    }

    /**
     * Erstellt aus allen Maps Checkboxen womit die sichbar gemanaged wird
     */
    public void createCheckbox() {
        int name = 0;
        int h = 15;
        mapCheck.clear();
        mapSelectedBox.removeAllItems();
        selectMapPanel.removeAll();

        for (int i = 0; i < maps.size(); i++) {
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
                    .addComponent(mapCheck.get(i)).addComponent(temp));

        }
        if (!mapCheck.isEmpty()) {
            mapCheck.get(selectedMap).setSelected(true);
            mapSelectedBox.setSelectedItem("Map " + mapCheck.size());
            mapSelectActionsPanel.setVisible(true);
        } else {
            mapSelectActionsPanel.setVisible(false);
        }
        selectedMap = maps.size() - 1;
    }

    /**
     * Methoden zur Erstellung von Maps:
     */
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

    /**
     * Erstellung von TileMenu:
     * -> Eine zugehörige Klasse um das Editieren (anschaulich) zu ermöglichen
     * -> typische create-Methode, wie in der Dokumentation erklärt
     */
    public void createTileMenu() {
        if (tileMenu == null) {
            tileMenu = new EditorTileMenu(owner, false, this);
            cameraPoint = new Pointer();
            TileSet tempTS = new TileSet("Content/Graphics/tileSets/12x12x3 - tileSet.png", 12, 12, 3);
            //On release:
            Meldungen m = new Meldungen(null, true, "Map");
            createEditorMap(Integer.parseInt(m.getUserInput(0)), Integer.parseInt(m.getUserInput(0)), tempTS, null);
            cameraPoint.setSpeed(15);
        } else {
            // Damit kein erneutes Starten immer entsteht
            tileMenu.setVisible(true);
            maps.get(selectedMap).setGraphicID(tileMenu.getSelectedID());
        }
    }

    public void addRecently(int id, TileSet ts) {
        if (id != 9999) {
            recentListTiles.add(new EditorTileButton(id, ts));
        } else {
            recentListTiles.add(new EditorTileButton(9999, new BufferedImage(Tile.TILEWIDTH, Tile.TILEHEIGHT, BufferedImage.TYPE_INT_ARGB_PRE)));
        }
        if (recentListTiles.size() >= maxRecentTiles) {
            recentListTiles.removeFirst();
        }
        displayRecent(recent);
    }

    /**
     * Editor Mechanik:
     * -> Steuern der Tastatureingabe
     * - Steuern des Pointers bzw. der Kamera
     * - Zoomen mit Tasten  // FIXME: 29.05.2019 (> Vllt auch mit Mausrad später möglich)
     * -> Steuert die Anzeige der Warnung > muss überarbeitet werden
     */
    public void update() {
        try {
            if (!bordercheck(cameraPoint, maps.get(selectedMap)) && GUI.keyInputToMove(keyListener).getX() != 0 || !bordercheck(cameraPoint, maps.get(selectedMap)) && GUI.keyInputToMove(keyListener).getY() != 0) {
                cameraPoint.setMove((int) GUI.keyInputToMove(keyListener).getX(), (int) GUI.keyInputToMove(keyListener).getY());
                gamePanel.getCamera().centerOnObject(cameraPoint);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    public boolean bordercheck(Pointer pointer, EditorMap em) {
        /**
         * Check ob Pointer am Rand ist (-> Etwas "Handlungsspielraum um über die Grenze zu gehen damit alle Tiles erreichbar sind)
         * - true = Position des Pointers ist kleiner (block) + Rückstoß
         * - false =  Position des Pointers ist groeßer (no block)
         */
        int maxxBorder = em.getMapSizeX() * Tile.TILEWIDTH + em.chapterXOffset,
                maxyBorder = em.getMapSizeY() * Tile.TILEHEIGHT + em.chapterYOffset;

        int minxBorder = em.chapterXOffset,
                minyBorder = em.chapterYOffset;

        leftBorder = pointer.getLocation().getX() < minxBorder + GUI.GAMEPANEL_WIDTH / 2;

        rightBorder = pointer.getLocation().getX() > maxxBorder - GUI.GAMEPANEL_WIDTH / 2;

        upBorder = pointer.getLocation().getY() < minyBorder + GUI.GAMEPANEL_WIDTH / 2;

        downBorder = pointer.getLocation().getY() > maxyBorder - GUI.GAMEPANEL_WIDTH / 2;

        if (leftBorder || rightBorder || upBorder || downBorder) {
            Camera cam = gamePanel.getCamera();
            if (leftBorder) {
                pointer.setXPos((int) pointer.getLocation().getX() + bounce);
                gamePanel.getCamera().centerOnObject(pointer);
            }
            if (rightBorder) {
                pointer.setXPos((int) pointer.getLocation().getX() - bounce);
                gamePanel.getCamera().centerOnObject(pointer);
            }
            if (upBorder) {
                pointer.setYPos((int) pointer.getLocation().getY() + bounce);
                gamePanel.getCamera().centerOnObject(pointer);
            }
            if (downBorder) {
                pointer.setYPos((int) pointer.getLocation().getY() - bounce);
                gamePanel.getCamera().centerOnObject(pointer);
            }
            return true;
        }
        return false;
    }

    /**
     * Fügt die Recent Leiste zu beliebigen Panel hinzu
     */
    public void displayRecent(JPanel panel) {
        panel.removeAll();
        for (int i = 0; i < recentListTiles.size(); i++) {
            EditorTileButton temp = (EditorTileButton) recentListTiles.get(i);
            temp.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    EditorMap m = maps.get(selectedMap);
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

    /**
     * Speicher Methoden:
     * -> Beziehen sich aufeinander (saveMap als Kernmethode zum Speichern einer X beliebigen Map)
     */
    public void saveMap(EditorMap toBeSavedMap, File path, boolean notification) {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(path))) {
            out.write(toBeSavedMap.tileSet.getTileSetImagePath());
            out.newLine();
            out.write(toBeSavedMap.getMapSizeX() + ";" + toBeSavedMap.getMapSizeX());
            out.newLine();

            for (int zeile = 0; zeile < toBeSavedMap.mapTiles.length; zeile++) {
                String line = "";
                for (int spalte = 0; spalte < toBeSavedMap.mapTiles[zeile].length - 1; spalte++) {
                    line = line + toBeSavedMap.mapTiles[zeile][spalte].getID() + ";";
                }
                line = line + toBeSavedMap.mapTiles[zeile][toBeSavedMap.mapTiles[zeile].length - 1].getID();
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

    public void saveCurrentMap(File path) {
        saveMap(maps.get(selectedMap), path, true);
    }

    public void autoSaveMap() {
        for (int i = 0; i < maps.size(); i++) {
            String timeStamp = new SimpleDateFormat("MM-DD HHmmss").format(new Date());
            File output = new File("Content/Maps/autosave/" + timeStamp + "Map_" + i + "_AUTOSAVE.txt");
            saveMap(maps.get(i), output, false);
        }
    }

    /**
     * Hinzufügen von EditorMap zum Editor
     * -> Macht alle nötigen Komponenten (Kamera)  auf die neue Map "bereit"
     */
    public void addMapToEditor(EditorMap em) {
        maps.add(em);
        if (!tileMenu.checkTileSet(em.getTileSet())) {
            tileMenu.addCustomTileSet(em.getTileSet());
        }
        createCheckbox();
        gamePanel.setCamera(em.getMapSizeX(), em.getMapSizeY(), em.getChapterOffset());
        cameraPoint.setCamera(gamePanel.getCamera());
        selectedMap = maps.size() - 1;
    }

    /**
     * Löschen von EditorMap zum Editor
     * -> Macht alle nötigen Komponenten (Kamera)  auf die neue Map "bereit"
     */
    public void removeMap(int index) {
        maps.remove(index);
        mapCheck.remove(index);
        if (maps.isEmpty()) {
            mapCheck.clear();
            mapSelectedBox.removeAllItems();
        }
        createCheckbox();
        gamePanel.setCamera(maps.get(selectedMap).getMapSizeX(), maps.get(selectedMap).getMapSizeY(), maps.get(selectedMap).getChapterOffset());
        cameraPoint.setCamera(gamePanel.getCamera());
        selectedMap = maps.size() - 1;
    }

    /**
     * Laden von Maps per Abfrage
     * -> Lädt den Pfad (Nutzung der Map Klasse) und das Konvertieren der Map zur EditorMap (Editor kann nur damit Arbeiten)
     * -> Macht alle nötigen Komponenten (Kamera)  auf die neue Map "bereit"
     */
    public void loadMap(String path) {
        Map mapTemp = new Map(gamePanel, path, "null", new Point(0, 0));
        //Übertragung der Map = lesen muss nicht extra für EditorMap implementier werden
        EditorMap em = new EditorMap(gamePanel, mapTemp.getMapSizeX(), mapTemp.getMapSizeY(), mapTemp.getTileSet());
        em.mapTiles = mapTemp.mapTiles;
        if (em != null) {
            addMapToEditor(em);
            JOptionPane.showMessageDialog(null, "Map erfolgreich geladen.");
        } else {
            JOptionPane.showMessageDialog(null, "Map konnte nicht geladen werden.", "", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Laden von Maps per Abfrage
     * -> Öffnet Meldungsfenster um Map Datei zu öffnen
     */
    public void loadCustomMap() {
        Meldungen m = new Meldungen(null, true, "null");
        File f = Meldungen.getFileAt("Open");
        String path = f.getPath();
        loadMap(path);
    }

    /**
     * Liste aller ausgewählten Checkboxen
     */
    public LinkedList<Integer> checkMapBoxes() {
        LinkedList indexOfActivatedMaps = new LinkedList<Integer>();
        for (int i = 0; i < mapCheck.size(); i++) {
            if (mapCheck.get(i).isSelected() && !indexOfActivatedMaps.contains(i)) {
                indexOfActivatedMaps.add(i);
            }
        }
        return indexOfActivatedMaps;
    }

    /**
     * Zoom Methode
     * -> Macht globale Tile-Groeße kleiner
     * -> Grenzen des Zooms können in der Konfiguration geändert werden
     */
    public void zoom(boolean zoomInIsTrueZoomOutisFalse) {
        // TODO: 23.03.2019 Zoom an Mapgroesse angepasst EDIT: NICHT WIRKLICH NÖTIG...
        EditorMap m = maps.get(selectedMap);
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

    /**
     * Render Methode der aktiven Maps!:
     * -> Liste die alle aktivierten Map IDs hat
     * -> render führt nur Methode bei IDs aus der oben gennanten Liste
     * -> WICHTIG: Reihenfolge nur nach öffnen der Maps (erstmal nicht änderbar)
     * d.h wenn Map 1 und Map 2 ausgewählt sind, wird erst 1 und dannach 2 gerendert (Maps überlappen)
     */
    public void renderEditor(Graphics2D g2d) {
        for (int i = 0; i < checkMapBoxes().size(); i++) {
            int enabledIndex = checkMapBoxes().get(i);
            maps.get(enabledIndex).renderMap(g2d);
        }
    }

    /**
     * Action Methoden
     */
    public void actionPerformed(ActionEvent evt) {
        JButton temp = (JButton) evt.getSource();
        switch (temp.getText()) {
            case "+":
                zoom(true);
                break;
            case "-":
                zoom(false);
                break;
            case "Tile MenuTab":
                createTileMenu();
                break;
        }


    }

    public void mouseClicked(MouseEvent e) {
        EditorMap m = maps.get(selectedMap);
        TileSet ts = tileMenu.getTileSet(tileMenu.getSelectedTileSetIndex());
        if (keyListener.shift) {
            m.setTileRect(e, ts);
        } else {
            m.setClick(0);
        }
        if (keyListener.str) {
            Tile clicked = m.mapTiles[(int) m.getTileID(e).getX()][(int) m.getTileID(e).getY()];
            m.setGraphicID(clicked.getID());
            addRecently(m.getGraphicID(), m.getTileSet());
        }
        if (keyListener.alt) {
        }
        m.setTile(e, ts);
        keyListener.update();
        gamePanel.repaint();
    }

    public void mouseDragged(MouseEvent e) {
        EditorMap m = maps.get(selectedMap);
        TileSet ts = tileMenu.getTileSet(tileMenu.getSelectedTileSetIndex());
        m.setTile(e, ts);
        gamePanel.repaint();
    }

    public boolean isActive() {
        return active;
    }

    public JPanel getMapSelectActionsPanel() {
        return mapSelectActionsPanel;
    }

    /**
     * // TODO: 29.05.2019 Wöfur habe ich das ?...
     */
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
