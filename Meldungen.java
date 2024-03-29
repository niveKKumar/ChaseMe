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
    private static Frame owner;
    public TileSet selectedTS;
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
    private JCheckBox notificationCheckBox;

    public Meldungen(Frame owner, boolean modal, String bestimmteAbfrage) {
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
        setAlwaysOnTop(true);
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

        switch (bestimmteAbfrage) {
            case "Map":
                mapGroesseAbfrage();
                break;
            case "null":
                setVisible(false);
                break;
            case "TileSet":
                getFilesAt("TileSet");
                break;
            case "Path":
                break;
        }
    }

    public static File getFileAt(String openOrSave) {             //Methode des Filechosers; offnet einen Speicher-Dialog
        if (openOrSave.equals("Open")) {
            fileChooser.setCurrentDirectory(new File("Content/maps"));
            String extension = ".txt";
            setFileFilter(extension);
            if (fileChooser.showOpenDialog(owner) == JFileChooser.APPROVE_OPTION) {  //Wenn der Ok-Button gedrueckt wird...
                return fileChooser.getSelectedFile();
            }
        }
        if (openOrSave.equals("Save")) {
            fileChooser.setCurrentDirectory(new File("Content/maps"));
            String extension = ".txt";
            setFileFilter(extension);
            if (fileChooser.showSaveDialog(owner) == JFileChooser.APPROVE_OPTION) {  //Wenn der Ok-Button gedrueckt wird...
                File saveFile = fileChooser.getSelectedFile();
                if (!saveFile.getName().contains(extension)) {
                    saveFile = new File(saveFile.getPath() + extension);
                }
                return saveFile;
            }
        }

        JOptionPane.showMessageDialog(owner, "Keine Datei ausgewählt.", "", JOptionPane.WARNING_MESSAGE);
        return null;
    }

    public static File[] getFilesAt(String openOrSaveOrTileSet) {             //Methode des Filechosers; offnet einen Speicher-Dialog
        if (openOrSaveOrTileSet.equals("TileSet")) {
            fileChooser.setCurrentDirectory(new File("Content/graphics/tileSets"));
            String extension = ".png";
            fileChooser.setMultiSelectionEnabled(true);
            setFileFilter(extension);
            if (fileChooser.showOpenDialog(owner) == JFileChooser.APPROVE_OPTION) {  //Wenn der Ok-Button gedrueckt wird...
                return fileChooser.getSelectedFiles();
            }
        }
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
        createComponents(1, "Geben Sie bitte die Mapgröße an!", false);
        //On Release:
        Font f = new Font(eingabe[0].getFont().getName(), Font.BOLD, 20);
        eingabe[0].setFont(f);
        eingabe[0].setText("Map - Groesse");

        setVisible(true);
    }

    // Manuelles TileSet (wird von TileSet beim fehlschlagen aufgerufen):
    public void tileSetAbfrage(String pPath) {
        // Manuelles laden:
        createComponents(3, "Bitte nähere Angaben zu den Tiles (Tiles pro Länge und Breite + Abstand zwischen Tiles)", false);
        eingabe[0].setText("Breite");
        eingabe[1].setText("Höhe");
        eingabe[2].setText("Border");

        setVisible(true);
    }


    public void chapterAbfrage() {
        createComponents(2, "Um wie viele Tiles soll die Map verschoben werden ?", false);
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
        createComponents(0, "TileSets sind ungleich. Es kann pro Map nur ein TileSet verwendet werden !", false);

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

    public void createComponents(int eingabeAnzahl, String abfrageText, boolean visible) {
        eingabe = new JTextArea[eingabeAnzahl];
        userInput = new String[eingabeAnzahl];
        for (int i = 0; i < eingabe.length; i++) {
            eingabe[i] = new JTextArea("Test");
            Font f = new Font(eingabe[i].getFont().getName(), Font.BOLD, 15);
            eingabe[i].setFont(f);
            abfrage.setFont(f);
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
        if (visible) {
            setVisible(true);
        }
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

    public JTextArea[] getEingabe() {
        return eingabe;
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
     * Method generated by IntelliJ IDEA GUI Designer
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
