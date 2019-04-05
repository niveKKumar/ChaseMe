import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class EditorTileMenu extends JDialog implements ActionListener {
    //Frame Components:
    private JPanel contentPane;
    private JPanel west;
    private JScrollPane[] scrollPane;
    private JPanel south;
    private JLabel selected;
    private JTextField[] txtEingabe;
    private JPanel[] tilePanel;
    private JButton btOk;
    private JPanel north;
    private JPanel center;
    private JTabbedPane tsTabPane;
    private JMenuBar menubar = new JMenuBar();   //Menüleiste erzeugen

    private int selectedID;
    private int selectedTileSet;
    private EditorTileButton[] tileMenuButtons;
    private TileSet[] tileSet = new TileSet[1];
    private Editor belongingEditor;

    private JFrame owner;

    private boolean firstStart = true;

    public EditorTileMenu(JFrame owner, boolean modal, Editor pBelongingEditor) {
        super(owner, modal);
        this.owner = owner;
        belongingEditor = pBelongingEditor;
        selectedID = 22; // Wiese
        $$$setupUI$$$();
        setContentPane(contentPane);
        setSize(500, 300);

        btOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
        createTileSet();
        setVisible(false);
//        pack();
    }

    private void onOK() {

        setVisible(false);
    }

    public void createTileSet() {
        int newTileSetAmount = tileSet.length - 1; // INDEX FÜR DAS LETZTE TILE
        createTileSetComponents();
        //Standard TileSet:
        if (firstStart) {
            tileSet[newTileSetAmount] = new TileSet("res/tileSet.png", 12, 12, 3); // Standard Tile Set
            firstStart = false;
        } else {
            Meldungen meldung = new Meldungen(owner, true, "null");
            File path = Meldungen.setMapPath("TileSet");
            meldung.tileSetAbfrage();
            tileSet[newTileSetAmount] = new TileSet(path.getPath()
                    , Integer.parseInt(meldung.getUserInput(0))
                    , Integer.parseInt(meldung.getUserInput(1))
                    , Integer.parseInt(meldung.getUserInput(2)));
        }
        tileMenuButtons = new EditorTileButton[tileSet[newTileSetAmount].tileSet.length - 1];
        for (int i = 0; i < tileMenuButtons.length; i++) {
            tileMenuButtons[i] = new EditorTileButton(i, tileSet[newTileSetAmount]);
            tileMenuButtons[i].loadImage(i);
            tileMenuButtons[i].addActionListener(this);
            tilePanel[newTileSetAmount].add(tileMenuButtons[i]);
        }
        int gap = 5;
        int spalte = 5;
        int height = tileMenuButtons.length * (EditorTileButton.size) / spalte;
        tilePanel[newTileSetAmount].setPreferredSize(new Dimension(spalte, height));
    }


    public void actionPerformed(ActionEvent evt) {
        try {
            EditorTileButton btTile = (EditorTileButton) evt.getSource();
            selectedID = btTile.getId();
            for (int i = 0; i < tileSet.length; i++) {
                if (btTile.getTileSet().equals(tileSet[i])) {
                    selectedTileSet = i;
                }
            }
            for (int i = 0; i < txtEingabe.length; i++) {
                txtEingabe[i].setText("");
            }
            selectedinLabel(selected);

        } catch (Exception e) {
            System.out.println("Action Performed Exception");
        }

    }

    public void nfcheck() {
        int temp = 0;
        try {
            for (int i = 0; i < tileSet.length; i++) {
                if (Integer.parseInt(txtEingabe[i].getText()) <= tileSet[i].tileSet.length) {
                    temp++;
                    selectedID = Integer.parseInt(txtEingabe[i].getText());
                    selectedinLabel(selected);
                } else {
                    txtEingabe[temp].setText(""); // FIXME: 14.03.2019 Mit Documentlistener muss anders umgegangen werden!
                    JOptionPane.showMessageDialog(this, "Es können nur Tiles von  0 bis " + tileSet[i].tileSet.length + " verwendet werden", "", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (NumberFormatException en) {
            if (!txtEingabe[temp].getText().equals("")) {
                txtEingabe[temp].setText("");
                JOptionPane.showMessageDialog(this, "Ungültige Zahl.", "", JOptionPane.WARNING_MESSAGE);
            }
        }
//        System.out.println("NF check");
    }


    public void selectedinLabel(JLabel anzeige) {
        try {
            for (int i = 0; i < tileSet.length; i++) {
                anzeige.setIcon(new ImageIcon(tileSet[selectedTileSet].tileSet[selectedID].tileImage));
            }
        } catch (Exception e) {
            anzeige.setText("");
            selectedID = 0;
        }
    }


    public int getSelectedID() {
        return selectedID;
    }

    public int getSelectedTileSetIndex() {
        return selectedTileSet;
    }

    public TileSet getTileSet(int index) {
        return tileSet[index];
    }

    public void createTileSetComponents() {
        scrollPane = new JScrollPane[tileSet.length];
        tilePanel = new JPanel[tileSet.length];
        txtEingabe = new JTextField[tileSet.length];
        for (int i = 0; i < tileSet.length; i++) {
            scrollPane[i] = new JScrollPane();
            scrollPane[i].getVerticalScrollBar().setUnitIncrement(16);
            scrollPane[i].setAlignmentX(1.0f);
            scrollPane[i].setAutoscrolls(false);
            scrollPane[i].setHorizontalScrollBarPolicy(31);
            scrollPane[i].setMaximumSize(new Dimension(300, 350));
            scrollPane[i].setMinimumSize(new Dimension(50, 50));
            scrollPane[i].setOpaque(true);
            scrollPane[i].setPreferredSize(new Dimension(50, 300));
            scrollPane[i].setRequestFocusEnabled(true);
            scrollPane[i].setVerticalScrollBarPolicy(22);
            tilePanel[i] = new JPanel();
            tilePanel[i].setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            tilePanel[i].setMaximumSize(new Dimension(0, 0));
            tilePanel[i].setMinimumSize(new Dimension(50, 50));
            tilePanel[i].setPreferredSize(new Dimension(50, 150));
            scrollPane[i].setViewportView(tilePanel[i]);
            txtEingabe[i] = new JTextField();
            txtEingabe[i].setMaximumSize(new Dimension(50, 50));
            txtEingabe[i].setMinimumSize(new Dimension(50, 50));
            txtEingabe[i].setPreferredSize(new Dimension(50, 50));
            txtEingabe[i].getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    nfcheck();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    nfcheck();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    nfcheck();
                }
            });
            tilePanel[i].add(txtEingabe[i]);
            tsTabPane.addTab("Tile Set " + tileSet.length, scrollPane[i]);
        }
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
        west.setMaximumSize(new Dimension(300, 350));
        west.setMinimumSize(new Dimension(50, 50));
        west.setPreferredSize(new Dimension(200, 250));
        contentPane.add(west, BorderLayout.WEST);
        tsTabPane = new JTabbedPane();
        west.add(tsTabPane, BorderLayout.CENTER);
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
        selected = new JLabel();
        selected.setHorizontalAlignment(0);
        selected.setHorizontalTextPosition(2);
        selected.setMaximumSize(new Dimension(100, 100));
        selected.setPreferredSize(new Dimension(50, 50));
        selected.setText("");
        selected.setVerifyInputWhenFocusTarget(false);
        south.add(selected);
        north = new JPanel();
        north.setLayout(new BorderLayout(0, 0));
        contentPane.add(north, BorderLayout.NORTH);
        center = new JPanel();
        center.setLayout(new BorderLayout(0, 0));
        contentPane.add(center, BorderLayout.CENTER);
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
//        contentPane.add(menubar, BorderLayout.NORTH);

        JMenuItem open = new JMenuItem("Öffnen");
        open.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                belongingEditor.loadMap();
            }
        });

        JMenuItem save = new JMenuItem("Speichern");
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                belongingEditor.saveMap();
            }
        });


        JMenu newMap = new JMenu("Neue Map anlegen");
        JMenuItem newMapWithSelection = new JMenuItem("Neue Map mit dem ausgewählten Tile erstellen");
        newMapWithSelection.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                System.out.println();
                TileSet tempTS = new TileSet("res/tileSet.png", 12, 12, 3);
                Meldungen meldung = new Meldungen(owner, true, "Map");
                belongingEditor.createEditorMap(Integer.parseInt(meldung.getUserInput(0)), Integer.parseInt(meldung.getUserInput(1)), tempTS);
            }
        });
        JMenuItem newBlankMap = new JMenuItem("Neue leere Map");
        newBlankMap.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                TileSet tempTS = new TileSet("res/tileSet.png", 12, 12, 3);
                Meldungen meldung = new Meldungen(owner, true, "Map");
                belongingEditor.createBlankEditorMap(Integer.parseInt(meldung.getUserInput(0)), Integer.parseInt(meldung.getUserInput(1)), tempTS);
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
        menubar.add(open);
        menubar.add(save);
        menubar.add(load);
    }
}
