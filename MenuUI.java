import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

public class MenuUI extends JInternalFrame implements ActionListener {

    private JPanel menuPane;
    private JPanel buttonPane;
    private MenuButton btSettings;
    private MenuButton btReturn;
    private MenuButton btInfo;
    private ActionListener actionListener;
    private GridBagConstraints gbc = new GridBagConstraints();
    private String[] mainMenuBTNames;
    private LinkedList<JPanel> addedMenus;

    public MenuUI(Dimension size, String[] mainMenuBT, ActionListener e) {
        super("MenuUI", false, false, false, false);
        setSize(size);
        this.mainMenuBTNames = mainMenuBT;
        actionListener = e;
        addedMenus = new LinkedList<>();
        setOpaque(false);
        setBackground(null);
        setLayout(new BorderLayout());
        createMenuPane();
        loadMainMenu();
        ((BasicInternalFrameUI) getUI()).setNorthPane(null);
        setBorder(null);
        setBorder(BorderFactory.createLineBorder(Color.black, 5));
        setVisible(true);

    }

    public static void addObject(GridBagConstraints gbc, JComponent comp, JComponent target, int gridX, int gridY, double weightX, double weightY, boolean resetGBC) {
        //Wenn andere Variablen geändert werden muessen dann vorher konfigurieren
        if (resetGBC) {
            gbc = new GridBagConstraints();
        }
        gbc.gridx = gridX;
        gbc.gridy = gridY;
        gbc.weighty = weightX;
        gbc.weightx = weightY;
        target.add(comp, gbc);
    }

    private void createMenuPane() {
        menuPane = new JPanel(new FlowLayout(FlowLayout.CENTER)) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(getBackground());
                g.fillRect(0, 0, getSize().width, getSize().height);
            }
        };
        menuPane.setLayout(new GridBagLayout());
        menuPane.setBackground(new Color(0, 0, 0, 50));
        menuPane.setOpaque(false);
        menuPane.setFocusable(false);
        add(menuPane, BorderLayout.CENTER);

        buttonPane = new JPanel();
        buttonPane.setBackground(null);
        buttonPane.setBorder(null);
        gbc = new GridBagConstraints();
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        addObject(buttonPane, 1, 1, 1, 1, GridBagConstraints.CENTER, false);

        Image imgReturn = (new ImageIcon("Content/Graphics/UI/returnIcon.png")).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        btReturn = new MenuButton(new ImageIcon(imgReturn));
        btReturn.addActionListener(this);
        btReturn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btReturn.setVisible(false);
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        addObject(gbc, btReturn, menuPane, 0, 0, 1, 1, false);

        gbc.insets = new Insets(5, 5, 5, 5);
        Image imgSettings = (new ImageIcon("Content/Graphics/UI/settingsIcon.png")).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        btSettings = new MenuButton(new ImageIcon(imgSettings));
        btSettings.addActionListener(this);
        btSettings.setAlignmentX(RIGHT_ALIGNMENT);
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
//        addObject(gbc,btSettings,menuPane,1,2,0.5f,0.5f,false);
        add(btSettings, BorderLayout.SOUTH);
        Image imgInfo = (new ImageIcon("Content/Graphics/UI/infoIcon.png")).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        btInfo = new MenuButton(new ImageIcon(imgInfo));
        btInfo.addActionListener(this);
        btInfo.setAlignmentX(LEFT_ALIGNMENT);
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        addObject(gbc, btInfo, menuPane, 3, 2, 0.5f, 0.5f, false);
        add(btInfo, BorderLayout.SOUTH);

        btSettings.setBackground(Color.RED);
        btInfo.setBackground(Color.RED);

    }

    public void showMenu() {
        if (menuPane.isVisible()) {
            menuPane.setVisible(false);
        } else {
            menuPane.setVisible(true);
        }
    }

    private void loadMainMenu() {
        Menu mainM = new Menu("MainMenu", mainMenuBTNames);
        addCustomMenu(mainM);
        btReturn.setVisible(false);
        btSettings.setVisible(true);
        btInfo.setVisible(true);
    }

    public void addCustomMenu(Menu customButtonPane) {
        addCustomPanel(customButtonPane);
    }

    public void addCustomPanel(JPanel yourPanelWithButtons) {
        if (!searchForPanel(yourPanelWithButtons.getName())) {
            addedMenus.add(yourPanelWithButtons);
        }
        showMenu(yourPanelWithButtons.getName());
    }

    public boolean searchForPanel(String name) {
        for (int i = 0; i < addedMenus.size(); i++) {
            if (addedMenus.get(i).getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void showMenu(String name) {
        buttonPane.removeAll();
        for (int i = 0; i < addedMenus.size(); i++) {
            if (addedMenus.get(i).getName().equals(name)) {
                buttonPane.add(addedMenus.get(i));
                addedMenus.get(i).setVisible(true);
                btReturn.setVisible(true);
                btSettings.setVisible(false);
                btInfo.setVisible(false);
            } else {
                addedMenus.get(i).setVisible(false);
            }
        }

    }

    private void addObject(JComponent comp, int gridX, int gridY, double weightX, double weightY, boolean resetGBC) {
        addObject(gbc, comp, menuPane, gridX, gridY, weightX, weightY, resetGBC);
    }

    private void addObject(JComponent comp, int gridX, int gridY, double weightX, double weightY, int anchor, boolean resetGBC) {
        gbc.anchor = anchor;
        addObject(comp, gridX, gridY, weightX, weightY, resetGBC);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btReturn) {
            showMenu("MainMenu");
            btReturn.setVisible(false);
        }
    }

    class Menu extends JPanel {

        private String name;

        public Menu(String pName, String[] btNames) {
            super(new FlowLayout(FlowLayout.CENTER));
            name = pName;
            createButtons(btNames);
        }

        public void createButtons(String[] btNames) {
            setOpaque(false);
            setFocusable(false);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            MenuButton[] buttons = new MenuButton[btNames.length];
            for (int i = 0; i < buttons.length; i++) {
                buttons[i] = new MenuButton(btNames[i]);
                buttons[i].addActionListener(actionListener);
                buttons[i].setAlignmentX(Component.CENTER_ALIGNMENT);
                buttons[i].setAlignmentY(Component.CENTER_ALIGNMENT);
                buttons[i].setFocusable(false);
                add(Box.createRigidArea(new Dimension(0, 20)));
                add(buttons[i]);
            }

        }

        public String getName() {
            return name;
        }
    }
}
