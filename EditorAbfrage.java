import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


public class EditorAbfrage extends JDialog {
    // Anfang Attribute
    private JLabel lXGroesse = new JLabel();
    private JLabel lYGroesse = new JLabel();
    private JTextField tf50 = new JTextField();
    private JTextField yEingabe = new JTextField();
    private JButton bOK = new JButton();
    private int mapSizeX;
    private int mapSizeY;
    // Ende Attribute

    public EditorAbfrage(JFrame owner, boolean modal) {
        // Frame-Initialisierung
        super(owner, modal);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        int frameWidth = 300;
        int frameHeight = 300;
        setSize(frameWidth, frameHeight);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (d.width - getSize().width) / 2;
        int y = (d.height - getSize().height) / 2;
        setLocation(x, y);
        setTitle("EditorAbfrage");
        setResizable(false);
        Container cp = getContentPane();
        cp.setLayout(null);
        // Anfang Komponenten

        lXGroesse.setBounds(32, 104, 73, 49);
        lXGroesse.setText("X - Groesse");
        cp.add(lXGroesse);
        lYGroesse.setBounds(160, 104, 73, 49);
        lYGroesse.setText("Y- Groesse");
        lYGroesse.setEnabled(false);
        cp.add(lYGroesse);
        tf50.setBounds(16, 152, 113, 49);
        tf50.setText("50");
        cp.add(tf50);
        yEingabe.setBounds(144, 152, 113, 49);
        yEingabe.setEnabled(false);
        cp.add(yEingabe);
        bOK.setBounds(8, 216, 273, 41);
        bOK.setText("OK");
        bOK.setMargin(new Insets(2, 2, 2, 2));
        bOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                bOK_ActionPerformed(evt);
            }
        });
        cp.add(bOK);
        // Ende Komponenten

        setVisible(true);
    } // end of public EditorAbfrage

    // Anfang Methoden

    public void bOK_ActionPerformed(ActionEvent evt) {
        try {
            if (tf50.getText() != "" || yEingabe.getText() != "") {
                mapSizeX = Integer.parseInt(tf50.getText());
                mapSizeY = Integer.parseInt(tf50.getText());
                dispose();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Bitte beide Felder ausfuellen und mit Zahlen fuellen" + "\n" + "! Hinweis die Map muss Quadratisch sein, ansonsten kann die Map nicht richtig erstellt werden !");
        }

    } // end of bOK_ActionPerformed

    public int getMapSizeX() {
        return mapSizeX;
    }

    public int getMapSizeY() {
        return mapSizeY;
    }
    // Ende Methoden
} // end of class EditorAbfrage

