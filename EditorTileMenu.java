import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

public class EditorTileMenu extends JDialog implements ActionListener {
    private JPanel contentPane;
    private JPanel west;
    private JScrollPane scrollPane;
    private JPanel south;
    private JLabel selectedTile;
    private JTextField txtEingabe;
    private JPanel tilePanel;
    private JButton btOk;
    private JPanel north;
    private JTextArea textArea1;
    private int selected;
    private EditorTileButton[] tileMenuButtons;
    private TileSet tileSet;
    private Editor belongingEditor;

    public EditorTileMenu() {
        $$$setupUI$$$();
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(btOk);
        btOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
        createTile();

    }

    public EditorTileMenu(JFrame owner, boolean modal, Editor pBelongingEditor) {
        super(owner, modal);
        $$$setupUI$$$();
        setContentPane(contentPane);
        setSize(500, 300);

        btOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
        createTile();
        setVisible(true);
//        pack();
        belongingEditor = pBelongingEditor;
    }

    private void onOK() {
        System.out.println("Selected:" + selected);
        setVisible(false);
    }

    public static void main(String[] args) { // ZUM TESTEN SPÄTER ENTFERNEN!!
        EditorTileMenu dialog = new EditorTileMenu();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    public void createTile() {
        tileSet = new TileSet("res/tileSet.png", 12, 12, 3); // Standard Tile Set - spaeter aenderbar
        tileMenuButtons = new EditorTileButton[tileSet.tileSet.length];
        int spalte = 3;
        int gap = 5;
        int height = tileMenuButtons.length / spalte * 50 + (spalte + 2) * gap;
        tilePanel.setPreferredSize(new Dimension(120, height));
        for (int i = 0; i < tileSet.tileSet.length; i++) {
            tileMenuButtons[i] = new EditorTileButton();
            tileMenuButtons[i].loadImage(i);
//            tileMenuButtons[i].setText(Integer.toString(i));
            tileMenuButtons[i].addActionListener(this);
            tilePanel.add(tileMenuButtons[i]);
        }
    }


    public void actionPerformed(ActionEvent evt) {
        try {
            EditorTileButton btTile = (EditorTileButton) evt.getSource();
            selected = btTile.getId();
            txtEingabe.setText("");
            selectedinLabel(selectedTile);
        } catch (Exception e) {
            System.out.println("Action Performed Exception");
        }

    }

    public void nfcheck() {
        try {
            if (Integer.parseInt(txtEingabe.getText()) < tileSet.tileSet.length) {
                selected = Integer.parseInt(txtEingabe.getText());
                selectedinLabel(selectedTile);
            } else {
//                txtEingabe.setText(""); // FIXME: 14.03.2019 Mit Documentlistener muss anders umgegangen werden!
                JOptionPane.showMessageDialog(this, "Es können nur Tiles von  0 bis " + tileSet.tileSet.length + " verwendet werden", "", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException en) {
            if (!txtEingabe.getText().equals("")) {
                txtEingabe.setText("");
                JOptionPane.showMessageDialog(this, "Ungültige Zahl.", "", JOptionPane.WARNING_MESSAGE);
            }
        }
        System.out.println("NF check");
    }


    public int getSelected() {
        return selected;
    }

    public void selectedinLabel(JLabel anzeige) {
        try {
            anzeige.setIcon(new ImageIcon(tileSet.tileSet[selected].tileImage));
        } catch (Exception e) {
            anzeige.setText("");
            selected = 0;
        }
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("Datei");  //Einen Menü-Punkt erzeugen
        //Öffnen
        JMenuItem open;                       //Einen Menüeintrag erzeugen
        open = new JMenuItem("Öffnen");
        open.addActionListener(new ActionListener() {       //Dem Menüeintrag einen Actionlsitener hinzufügen
            public void actionPerformed(ActionEvent evt) {
                belongingEditor.loadMap();                  //Dem Actionlistener des Menüeitrags die Eregnismethode des Öffnen-Buttons hinzufügen
            }
        });

        fileMenu.add(open);                               //Den Menüeintrag open dem File-Menü hinzuügen

        fileMenu.addSeparator();                          //Trennstrich zwischen zwei Menüeinträgen hinzufügen (fakultativ)

        //Speichern
        JMenuItem save;
        save = new JMenuItem("Speichern");
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                belongingEditor.saveMap();
            }
        });
        fileMenu.add(save);

        return fileMenu;
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
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout(0, 0));
        west = new JPanel();
        west.setLayout(new BorderLayout(0, 0));
        west.setMaximumSize(new Dimension(300, 350));
        west.setMinimumSize(new Dimension(50, 50));
        west.setPreferredSize(new Dimension(200, 250));
        contentPane.add(west, BorderLayout.WEST);
        scrollPane.setAlignmentX(1.0f);
        scrollPane.setAutoscrolls(false);
        scrollPane.setHorizontalScrollBarPolicy(31);
        scrollPane.setMaximumSize(new Dimension(300, 350));
        scrollPane.setMinimumSize(new Dimension(50, 50));
        scrollPane.setOpaque(true);
        scrollPane.setPreferredSize(new Dimension(50, 300));
        scrollPane.setRequestFocusEnabled(true);
        scrollPane.setVerticalScrollBarPolicy(22);
        west.add(scrollPane, BorderLayout.CENTER);
        tilePanel = new JPanel();
        tilePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        tilePanel.setMaximumSize(new Dimension(0, 0));
        tilePanel.setMinimumSize(new Dimension(50, 50));
        tilePanel.setPreferredSize(new Dimension(50, 150));
        scrollPane.setViewportView(tilePanel);
        txtEingabe.setMaximumSize(new Dimension(50, 50));
        txtEingabe.setMinimumSize(new Dimension(50, 50));
        txtEingabe.setPreferredSize(new Dimension(50, 50));
        tilePanel.add(txtEingabe);
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
        selectedTile = new JLabel();
        selectedTile.setHorizontalAlignment(0);
        selectedTile.setHorizontalTextPosition(2);
        selectedTile.setMaximumSize(new Dimension(100, 100));
        selectedTile.setPreferredSize(new Dimension(50, 50));
        selectedTile.setText("");
        selectedTile.setVerifyInputWhenFocusTarget(false);
        south.add(selectedTile);
        north = new JPanel();
        north.setLayout(new BorderLayout(0, 0));
        contentPane.add(north, BorderLayout.NORTH);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        scrollPane = new JScrollPane();
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        txtEingabe = new JTextField();

        txtEingabe.getDocument().addDocumentListener(new DocumentListener() {
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
        JMenuBar menubar = new JMenuBar();   //Menüleiste erzeugen
        this.setJMenuBar(menubar);  //Menüleiste dem Fenster hinzufügen
        menubar.add(createFileMenu()); //Einträge zur Menüleiste hinzufügen; siehe Methode createFileMenu

    }
}
