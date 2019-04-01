import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class Meldungen extends JDialog implements ActionListener {
    private static JFrame owner;

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel center;
    private JPanel south;
    private JLabel abfrage;
    private JTextArea[] eingabe;
    private String[] userInput;
    private boolean firstStart = true;
    public static JFileChooser fileChooser = new JFileChooser();

    public Meldungen(JFrame owner, boolean modal, String bestimmteAbfrage) {
        super(owner, modal);
        Meldungen.owner = owner;
        $$$setupUI$$$();
        int x = (this.getWidth() - getSize().width) / 2;
        int y = (this.getWidth() - getSize().height) / 2;
        setLocation(x, y);
        setSize(500, 300);
        setContentPane(contentPane);
//        getRootPane().setDefaultButton(buttonOK);

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
        requestFocus();
        switch (bestimmteAbfrage) {
            case "Map":
                mapGroesseAbfrage();
                break;
            case "null":
                System.out.println("Nur Fenster erstellen :)");
                setVisible(false);
                break;
            case "TileSet":
                tileSetAbfrage();
                break;
            case "Path":
                break;
        }
    }

    private void onOK() {
        setVisible(false);
    }

    //Templates von Meldungen!
    public void mapGroesseAbfrage() {
        createComponents(2, "Bitte Mapgroesse angeben:");
        eingabe[0].setText("Map - Breite");
        eingabe[1].setText("Map - Höhe");
        eingabe[0].setText("50");
        eingabe[1].setText("50");
        setVisible(true);
    }

    public void tileSetAbfrage() {
        createComponents(3, "Bitte nähere Angaben zu den Tiles (Tiles pro Länge und Breite + Abstand zwischen Tiles); ");
        eingabe[0].setText("Breite");
        eingabe[1].setText("Höhe");
        eingabe[2].setText("Border");
        eingabe[0].setText("16");
        eingabe[1].setText("16");
        eingabe[2].setText("0");
        setVisible(true);
    }

    public void createComponents(int anzahl, String abfrageText) {
        eingabe = new JTextArea[anzahl];
        userInput = new String[anzahl];
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

    public static File setMapPath(String openOrSaveOrTileSet) {             //Methode des Filechosers; offnet einen Speicher-Dialog
        fileChooser.setCurrentDirectory(new File("./res"));  //Verweis auf das aktuelle Programmverzeichnis
        if (openOrSaveOrTileSet.equals("Open")) {
            String extension = ".txt";
            setFileFilter(extension);
            if (fileChooser.showOpenDialog(owner) == JFileChooser.APPROVE_OPTION) {  //Wenn der Ok-Button gedrueckt wird...
                return fileChooser.getSelectedFile();
            } else {                                                               //Wenn der Ok.Button nicht gedreckt wird.
                JOptionPane.showMessageDialog(owner, "Keine Datei ausgewählt.", "", JOptionPane.WARNING_MESSAGE);
                return null;
            }
        }
        if (openOrSaveOrTileSet.equals("Save")) {
            String extension = ".txt";
            setFileFilter(extension);
            if (fileChooser.showSaveDialog(owner) == JFileChooser.APPROVE_OPTION) {  //Wenn der Ok-Button gedrueckt wird...
                return fileChooser.getSelectedFile();
            } else {                                                               //Wenn der Ok.Button nicht gedreckt wird.
                JOptionPane.showMessageDialog(owner, "Keine Datei ausgewählt.", "", JOptionPane.WARNING_MESSAGE);
                return null;
            }
        }
        if (openOrSaveOrTileSet.equals("TileSet")) {
            String extension = ".png";
            setFileFilter(extension);
            if (fileChooser.showSaveDialog(owner) == JFileChooser.APPROVE_OPTION) {  //Wenn der Ok-Button gedrueckt wird...
                return fileChooser.getSelectedFile();
            } else {                                                               //Wenn der Ok.Button nicht gedreckt wird.
                JOptionPane.showMessageDialog(owner, "Keine Datei ausgewählt.", "", JOptionPane.WARNING_MESSAGE);
                return null;
            }
        }
        return null;

    }

    private static void setFileFilter(String extension) {
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
        abfrage = new JLabel();
        abfrage.setText("Das ist eine Demoabfrage die modifiziert werden soll !");
        center.add(abfrage);
        south = new JPanel();
        south.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(south, BorderLayout.SOUTH);
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        south.add(buttonCancel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("OK");
        south.add(buttonOK, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        south.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
