import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditorTileMenu extends JDialog implements ActionListener{
    private JPanel contentPane;
    private JButton buttonOK;
    private JPanel west;
    private JPanel south;
    private JScrollPane tilePane;
    private JPanel tilePanel;
    private JPanel center;
    private JLabel selectedTile;
    private int selected;
    private EditorTileButton[] tileMenuButtons;
    private TileSet tileSet;

    public EditorTileMenu() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    public static void main(String[] args) {
        EditorTileMenu dialog = new EditorTileMenu();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
    public void createTile() {
        tileSet = new TileSet("res/tileSet.png", 12, 12, 3); // Standard
        tileMenuButtons = new EditorTileButton[tileSet.tileSet.length];
        // NUR BIS 10 WEGEN TESTGRÜNDEN - PERFORMANCE
        for (int i = 0; i < 10 /*tileSet.tileSet.length*/; i++) {
            tileMenuButtons[i] = new EditorTileButton();
            tileMenuButtons[i].loadImage(i);
//            tileMenuButtons[i].setText(Integer.toString(i));
            tileMenuButtons[i].addActionListener(this);
        }
    }

    public void actionPerformed(ActionEvent evt) {
        try {
            EditorTileButton btTile = (EditorTileButton) evt.getSource();
            selected = btTile.getId();
        } catch (Exception e) {
            JButton temp = (JButton) evt.getSource();
            System.out.println(temp.getText());
            checkNumberField();

            switch (temp.getText()) {
                case "Ok":
                    System.out.println("Selected:" + selected);
                    System.exit(0);
                    break;


            }
        }
        selectedTile.setIcon(new ImageIcon(tileSet.tileSet[selected].tileImage));
    }

    public void checkNumberField() {
        int test;
        try {
            test = Integer.parseInt(txtEingabe.getText());
            selected = test;
        } catch (NumberFormatException en) {
            System.out.println("Eingabe Fehlerhaft -> Keine Zahl");
        }
    }


    public int getSelected() {
        return selected;
    }
}
