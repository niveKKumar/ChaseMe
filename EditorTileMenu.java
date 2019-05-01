import com.sun.istack.internal.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class EditorTileMenu extends JDialog {
    //Frame Components:
    private JPanel contentPane;
    private JPanel west;
    private JPanel south;
    private JLabel selectedLabel;
    private JButton btOk;
    private JPanel north;
    private JPanel center;
    private JTabbedPane tsTabPane;
    private JMenuBar menubar = new JMenuBar();   //Menüleiste erzeugen

    private int selectedID;
    private int selectedTileSet;

    private ArrayList tileSet = new ArrayList<TileSet>();
    private Editor belongingEditor;

    private JFrame owner;
    private int columns = 5;

    private boolean firstStart = true;

    public EditorTileMenu(JFrame owner, boolean modal, Editor pBelongingEditor) {
        super(owner, modal);
        this.owner = owner;
        belongingEditor = pBelongingEditor;
        selectedID = 22; // Wiese
        $$$setupUI$$$();
        setContentPane(contentPane);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (d.width - getSize().width) / 2;
        int y = (d.height - getSize().height) / 2;
        setLocation(x, y);

        btOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
        pack();
        createTileSet();
        setVisible(false);
        setSize(500, 300);
    }

    private void onOK() {
        belongingEditor.addRecently(selectedID, ((TileSet) tileSet.get(selectedTileSet)));
        setVisible(false);
    }

    public void createTileSet() {
        //Standard TileSet:
        if (firstStart) {
            tileSet.add(new TileSet("Content/Graphics/tileSets/12x12x3 - tileSet.png", 12, 12, 3)); // Standard Tile Set)
            createTileSetComponents();
            firstStart = false;
        } else {
            Meldungen meldung = new Meldungen(owner, true, "null");
            tileSet.add(new TileSet(Meldungen.setMapPath("TileSet").getPath()));
            createTileSetComponents();
        }
    }


    public void selectedinLabel(JLabel anzeige, @Nullable Icon icon) {
        try {
            anzeige.setIcon(icon);
        } catch (Exception e) {
            for (int i = 0; i < tileSet.size(); i++) {
                anzeige.setIcon(new ImageIcon(((TileSet) tileSet.get(selectedTileSet)).tileSet[selectedID].tileImage));
            }
        }
    }


    public int getSelectedID() {
        return selectedID;
    }

    public int getSelectedTileSetIndex() {
        return selectedTileSet;
    }

    public void setTileSet(TileSet pTileSet) {
        tileSet.add(pTileSet);
        selectedTileSet = tileSet.size() - 1;
    }

    public TileSet getTileSet(int index) {
        return ((TileSet) tileSet.get(index));
    }

    public void createTileSetComponents() {
        // FIXME: 06.04.2019 Kann besser gelöst werden -> nur Scrollpane als Array, der Rest Local Done
        int gap = 2;
        JPanel tilePanel = new JPanel();
        columns = (int) Math.floor(center.getWidth() / EditorTileButton.size);
        System.out.println(columns);
        tilePanel.setLayout(new GridLayout(0, columns, gap, gap));
        //Hinzufuegen der TileEingabe auf das JPanel
        JTextField txtEingabe = new JTextField();
        txtEingabe.setSize(EditorTileButton.size, EditorTileButton.size);
        txtEingabe.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                try {
                    for (int i = 0; i < tileSet.size(); i++) {
                        if (Integer.parseInt(txtEingabe.getText()) <= ((TileSet) tileSet.get(i)).tileSet.length) {
                            selectedID = Integer.parseInt(txtEingabe.getText());
                            selectedinLabel(selectedLabel, null);
                        } else {
                            txtEingabe.setText(""); // FIXME: 14.03.2019 Mit Documentlistener muss anders umgegangen werden!
                            JOptionPane.showMessageDialog(owner, "Es können nur Tiles von  0 bis " + ((TileSet) tileSet.get(i)).tileSet.length + " verwendet werden", "", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                } catch (NumberFormatException en) {
                    if (!txtEingabe.getText().equals("")) {
                        txtEingabe.setText("");
                        JOptionPane.showMessageDialog(owner, "Ungültige Zahl.", "", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        tilePanel.add(txtEingabe);
        //Hinzufuegen der Tiles auf das JPanel
        selectedTileSet = tileSet.size() - 1;
        TileSet current = ((TileSet) tileSet.get(selectedTileSet));
        EditorTileButton[] tileMenuButtons = new EditorTileButton[current.tileSet.length - 1];
        for (int i = 0; i < tileMenuButtons.length; i++) {
            tileMenuButtons[i] = new EditorTileButton(i, current);
            // FIXME: 19.04.2019 Action Listener
            tileMenuButtons[i].addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {

                    if (e.getClickCount() == 2) {
                        EditorTileButton source = ((EditorTileButton) e.getSource());
                        selectedID = source.getId();
                        ((EditorMap) belongingEditor.maps.get(belongingEditor.selectedMap)).setGraphicID(getSelectedID());
                    }

                }

                @Override
                public void mousePressed(MouseEvent e) {
                    EditorTileButton source = ((EditorTileButton) e.getSource());
                    selectedID = source.getId();
                    for (int i = 0; i < tileSet.size(); i++) {
                        if (source.getTileSet().getTileSetImagePath().equals(((TileSet) tileSet.get(i)).getTileSetImagePath())) {
                            selectedTileSet = i;
                        }
                    }
                    selectedinLabel(selectedLabel, source.getIcon());
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

            tilePanel.add(tileMenuButtons[i]);
        }
        // Tile Panel Groeße nach Tile Button
        // Erstellen des Scrollpanes und zuweisen des JPanels

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getVerticalScrollBar().setUnitIncrement(8);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setOpaque(true);
        scrollPane.setViewportView(tilePanel);
        tsTabPane.addTab("Tile Set " + tileSet.size(), scrollPane);
//        tsTabPane.setMinimumSize(new Dimension(columns * (EditorTileButton.size + gap), current.tileSet.length / columns * (EditorTileButton.size + gap)));
    }

    public void setSelectedID(int selectedID) {
        this.selectedID = selectedID;
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        contentPane.setLayout(new BorderLayout(0, 0));
        west = new JPanel();
        west.setLayout(new BorderLayout(0, 0));
        west.setMinimumSize(new Dimension(50, 50));
        contentPane.add(west, BorderLayout.WEST);
        south = new JPanel();
        south.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        contentPane.add(south, BorderLayout.SOUTH);
        btOk = new JButton();
        btOk.setHorizontalTextPosition(2);
        btOk.setMaximumSize(new Dimension(300, 50));
        btOk.setMinimumSize(new Dimension(100, 50));
        btOk.setPreferredSize(new Dimension(100, 50));
        btOk.setText("Bestaetigen");
        south.add(btOk);
        selectedLabel = new JLabel();
        selectedLabel.setHorizontalAlignment(0);
        selectedLabel.setHorizontalTextPosition(2);
        selectedLabel.setMaximumSize(new Dimension(100, 100));
        selectedLabel.setPreferredSize(new Dimension(50, 50));
        selectedLabel.setText("");
        selectedLabel.setVerifyInputWhenFocusTarget(false);
        south.add(selectedLabel);
        north = new JPanel();
        north.setLayout(new BorderLayout(0, 0));
        contentPane.add(north, BorderLayout.NORTH);
        center = new JPanel();
        center.setLayout(new BorderLayout(0, 0));
        contentPane.add(center, BorderLayout.CENTER);
        tsTabPane = new JTabbedPane();
        center.add(tsTabPane, BorderLayout.CENTER);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    private void createUIComponents() {
        contentPane = new JPanel();
        menubar = new JMenuBar();   //Menüleiste erzeugen
        setJMenuBar(menubar);  //Menüleiste dem Fenster hinzufügen

        JMenuItem open = new JMenuItem("Öffnen");
        open.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                belongingEditor.loadMap();
            }
        });

        JMenuItem save = new JMenuItem("Speichern");
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                belongingEditor.saveMap(Meldungen.setMapPath("Save"), true);
            }
        });


        JMenu newMap = new JMenu("Neue Map anlegen");
        JMenuItem newMapWithSelection = new JMenuItem("Neue Map mit dem ausgewählten Tile erstellen");
        newMapWithSelection.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Meldungen meldung = new Meldungen(owner, true, "Map");
                belongingEditor.createEditorMap(Integer.parseInt(meldung.getUserInput(0)), Integer.parseInt(meldung.getUserInput(1)), ((TileSet) tileSet.get(selectedTileSet)), selectedID);
            }
        });
        JMenuItem newBlankMap = new JMenuItem("Neue leere Map");
        newBlankMap.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                TileSet tempTS = new TileSet("Content/Graphics/tileSets/12x12x3 - tileSet.png", 12, 12, 3);
                Meldungen meldung = new Meldungen(owner, true, "Map");
                belongingEditor.createBlankEditorMap(Integer.parseInt(meldung.getUserInput(0)), Integer.parseInt(meldung.getUserInput(1)), tempTS);
            }
        });
        JMenuItem newItemMap = new JMenuItem("Neue Item Map");
        newItemMap.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                TileSet tempTS = new TileSet("Content/Graphics/tileSets/16x16x0 - tileSetItems.png", 16, 16, 0);
                Meldungen meldung = new Meldungen(owner, true, "Map");
                belongingEditor.createEditorMap(Integer.parseInt(meldung.getUserInput(0)), Integer.parseInt(meldung.getUserInput(1)), tempTS, 0);
            }
        });

        JMenuItem load = new JMenuItem("Neues Tile Set laden");
        load.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                createTileSet();
            }
        });
        menubar.add(newMap);
        newMap.add(newMapWithSelection);
        newMap.add(newBlankMap);
        newMap.add(newItemMap);
        menubar.add(open);
        menubar.add(save);
        menubar.add(load);
    }
}
