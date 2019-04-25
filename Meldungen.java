import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Meldungen extends JDialog implements ActionListener {
    public static JFileChooser fileChooser = new JFileChooser();
    private static JFrame owner;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel center;
    private JPanel south;
    private JLabel abfrage;
    private JPanel north;
    private JTextArea[] eingabe;
    private JButton[] button;
    private String[] userInput;
    private boolean firstStart = true;
    public TileSet selectedTS;
    private JCheckBox notificationCheckBox;

    public Meldungen(JFrame owner, boolean modal, String bestimmteAbfrage) {
        super(owner, modal);
        Meldungen.owner = owner;
        $$$setupUI$$$();
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonOK);
        setSize(500, 300);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (d.width - getSize().width) / 2;
        int y = (d.height - getSize().height) / 2;
        setLocation(x, y);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });
        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        setVisible(false);
        switch (bestimmteAbfrage) {
            case "Map":
                mapGroesseAbfrage();
                break;
            case "null":
                System.out.println("Nur Fenster erstellen :)");
                setVisible(false);
                break;
            case "TileSet":
                setMapPath("TileSet");
                break;
            case "Path":
                break;
        }
    }

    public static File setMapPath(String openOrSaveOrTileSet) {             //Methode des Filechosers; offnet einen Speicher-Dialog
        if (openOrSaveOrTileSet.equals("Open")) {
            fileChooser.setCurrentDirectory(new File("Content/maps"));
            String extension = ".txt";
            setFileFilter(extension);
            if (fileChooser.showOpenDialog(owner) == JFileChooser.APPROVE_OPTION) {  //Wenn der Ok-Button gedrueckt wird...
                return fileChooser.getSelectedFile();
            }
        }
        if (openOrSaveOrTileSet.equals("Save")) {
            fileChooser.setCurrentDirectory(new File("Content/maps"));
            String extension = ".txt";
            setFileFilter(extension);
            if (fileChooser.showSaveDialog(owner) == JFileChooser.APPROVE_OPTION) {  //Wenn der Ok-Button gedrueckt wird...
                return fileChooser.getSelectedFile();
            }
        }
        if (openOrSaveOrTileSet.equals("TileSet")) {
            fileChooser.setCurrentDirectory(new File("Content/graphics/tileSets"));
            String extension = ".png";
            setFileFilter(extension);
            if (fileChooser.showOpenDialog(owner) == JFileChooser.APPROVE_OPTION) {  //Wenn der Ok-Button gedrueckt wird...
                File f = fileChooser.getSelectedFile();
                return f;
            }
        }
        JOptionPane.showMessageDialog(owner, "Keine Datei ausgewählt.", "", JOptionPane.WARNING_MESSAGE);
        return null;

    }

    private static void setFileFilter(String extension) {
        fileChooser.resetChoosableFileFilters();
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                return f.getName().endsWith(extension);
            }

            @Override
            public String getDescription() {
                return "Text Files" + String.format(" (*%s)", extension);
            }
        });
    }

    private void onOK() {
        setVisible(false);
    }

    //Templates von Meldungen!
    public void mapGroesseAbfrage() {
        createComponents(2, "Bitte Mapgroesse angeben:");
        //On Release:
//        eingabe[0].setText("Map - Breite");
//        eingabe[1].setText("Map - Höhe");

        eingabe[0].setText("50");
        eingabe[1].setText("50");
        setVisible(true);
    }

    // Manuelles TileSet (wird von TileSet beim fehlschlagen aufgerufen):
    public void tileSetAbfrage(String pPath) {
        // Manuelles laden:
        System.out.println("Automatisch ging nicht");
        createComponents(3, "Bitte nähere Angaben zu den Tiles (Tiles pro Länge und Breite + Abstand zwischen Tiles); ");
        eingabe[0].setText("Breite");
        eingabe[1].setText("Höhe");
        eingabe[2].setText("Border");

        /** Nicht mehr nötig*/

            /*button = new JButton[2];
            button[0] = new JButton("Tile Set - Base Tiles");
            button[0].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    eingabe[0].setText("12");
                    eingabe[1].setText("12");
                    eingabe[2].setText("3");
                }
            });
            button[1] = new JButton("Tile Set - Item Tiles");
            button[1].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    eingabe[0].setText("16");
                    eingabe[1].setText("16");
                    eingabe[2].setText("0");
                }
            });

            for (int i = 0; i < button.length; i++) {
                button[i].setFocusable(false);
                center.add(button[i]);
            }*/
        setVisible(true);
    }


    public void chapterAbfrage() {
        createComponents(2, "Um wie viele Tiles soll die Map verschoben werden ?");
        eingabe[0].setText("In X - Tiles");
        eingabe[1].setText("In Y - Tiles");
        button = new JButton[1];

        button[0] = new JButton("Standard Groesse (20 nach rechts)");
        button[0].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eingabe[0].setText("20");
                eingabe[1].setText("0");
            }
        });
        for (int i = 0; i < button.length; i++) {
            center.add(button[i]);
        }

        setVisible(true);
    }

    public void unSimilarTS(EditorMap current, TileSet tsSelected) {
        createComponents(0, "TileSets sind ungleich. Es kann pro Map nur ein TileSet verwendet werden !");

        notificationCheckBox = new JCheckBox("Warnung ausblenden", false);
        notificationCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                current.setToIgnore(notificationCheckBox.isSelected());
            }
        });
        south.add(notificationCheckBox);
        button = new JButton[2];
        button[0] = new JButton("Map Tile Set");
        button[0].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedTS = current.tileSet;
                onOK();
            }
        });
        button[1] = new JButton("Gewähltes Tile Set");
        button[1].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedTS = tsSelected;
                onOK();
            }
        });
        button[0].setIcon(new ImageIcon(current.tileSet.getTileSetImage().getScaledInstance(100, 100, BufferedImage.SCALE_SMOOTH)));
        button[0].setBorder(null);
        button[1].setIcon(new ImageIcon(tsSelected.getTileSetImage().getScaledInstance(100, 100, BufferedImage.SCALE_SMOOTH)));
        button[0].setBorder(null);
        buttonOK.setVisible(false);
        for (int i = 0; i < button.length; i++) {
            button[i].setFocusable(false);
            center.add(button[i]);
        }
        setVisible(true);
    }

    public void createComponents(int eingabeAnzahl, String abfrageText) {
        eingabe = new JTextArea[eingabeAnzahl];
        userInput = new String[eingabeAnzahl];
        for (int i = 0; i < eingabe.length; i++) {
            eingabe[i] = new JTextArea("Test");
            eingabe[i].setSize(100, 100);
            eingabe[i].setVisible(true);
            eingabe[i].getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    setUserInput();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    setUserInput();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    setUserInput();
                }
            });
            int finalI = i;
            eingabe[i].addKeyListener(new KeyAdapter() {
                /**
                 * Invoked when a key has been pressed.
                 *
                 * @param e
                 */
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_TAB) {
                        if (e.getModifiers() > 0) {
                            eingabe[finalI].transferFocus();
                        } else {
                            eingabe[finalI].transferFocus();
                        }
                        e.consume();
                    }
                }
            });
            center.add(eingabe[i]);
        }
        abfrage.setText(abfrageText);
    }

    private void setUserInput() {
        for (int i = 0; i < eingabe.length; i++) {
            if (firstStart) {
                eingabe[i].setText("");
                firstStart = false;
            }
            userInput[i] = eingabe[i].getText();
        }
    }

    public String getUserInput(int welcheAWBox) {
        try {
            return userInput[welcheAWBox];
        } catch (Exception e) {
            e.printStackTrace();
            return "Falscher Input";
        }

    }

    private void onCancel() {
        System.out.println("Don´t do anything new because canceled");
        dispose();
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {

    }


    /**
     * Method generated by IntelliJ IDEA JFrame Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout(0, 0));
        contentPane.setPreferredSize(new Dimension(300, 125));
        center = new JPanel();
        center.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        contentPane.add(center, BorderLayout.CENTER);
        north = new JPanel();
        north.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        contentPane.add(north, BorderLayout.NORTH);
        abfrage = new JLabel();
        abfrage.setText("Das ist eine Demoabfrage die modifiziert werden soll !");
        north.add(abfrage);
        south = new JPanel();
        south.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        south.setBackground(new Color(-3486771));
        contentPane.add(south, BorderLayout.SOUTH);
        south.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-10790053)), null));
        buttonOK = new JButton();
        buttonOK.setText("OK");
        south.add(buttonOK);
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        south.add(buttonCancel);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    private void createUIComponents() {
    }
}
