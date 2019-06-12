import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

public class MenuUI extends JFrame {
    /**
     * MenuUI Klasse womit Menus angezeigt werden können
     * -> Funktioniert durch das einfügen von JPanels (mit Namen), die auf der topPane (GridBagLayout) angezeigt werden
     * -> Integrierte MenuTab Klasse zeigt MenuButtons in der Mitte des Bildschirms an (BoxLayout)
     * -> diese können angezeigt werden mithilfe von Methoden (siehe Beschreibung unten)
     * -> besitzt vorgefertigte Buttons    : btReturn = zum Laden des Hauptmenüs
     * : btSettings = im Hauptmenü- lässt Sachen konfigurieren
     * : btInfo = zeigt Informationen/Beschreibungen zu bestimmter Seite
     */
    //Editor Konfiguration:
    public static int MENUUI_WIDTH = 500;
    public static int MENUUI_HEIGHT = 650;
    private static int optionButtonSize = 40;
    private static ActionListener actionListener;
    //Debuggin:
    public String currentPage;
    //Core Objects:
    private JPanel topPane;   //(MainPane)
    private JPanel bottomPane;
    private MenuButton btSettings;
    private MenuButton btReturn;
    private MenuButton btInfo;
    private JPanel buttonPane; //(mittige Pane für MenuTabs...)
    private LinkedList<MenuTab> addedMenus;


    /**
     * Standard Konstruktor, womit Menu JFrame erstellt wird
     */
    public MenuUI(ActionListener act) {
        super("Main Menu");
        setSize(MENUUI_WIDTH, MENUUI_HEIGHT);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (d.width - getSize().width) / 2;
        int y = (d.height - getSize().height) / 2;
        setLocation(x, y);
        actionListener = act;
        addedMenus = new LinkedList<>();
        setName("MenuUI");
        setFocusable(false);
        setBackground(null);
        setLayout(new BorderLayout());
        createMenuPane();
        repaint();
        revalidate();
    }

    /**
     * Konstruktor mit direkter Eingabe der Haupt Menu Buttons
     */
    public MenuUI(String[] btStringNames, ActionListener e) {
        this(e);
        loadMainMenu(btStringNames);
    }

    /**
     * Hilfsmethode zum Hinzufügen von Objekten auf Panel mit GridBagLayout
     */
    public static void addObject(GridBagConstraints gbc, JComponent comp, JComponent target, int gridX, int gridY, double weightX, double weightY) {
        gbc.gridx = gridX;
        gbc.gridy = gridY;
        gbc.weighty = weightX;
        gbc.weightx = weightY;
        target.add(comp, gbc);
    }

    /**
     * Erstellung der MenuPane mit den Buttons:
     */
    private void createMenuPane() {
        topPane = new JPanel(new BorderLayout());
        topPane.setBackground(new Color(0, 0, 0, 50));
        topPane.setOpaque(false);
        topPane.setFocusable(false);
        add(topPane, BorderLayout.NORTH);

        //Main Butons:
        buttonPane = new JPanel(new BorderLayout());
        add(buttonPane, BorderLayout.CENTER);

        bottomPane = new JPanel(new BorderLayout());
        bottomPane.setBackground(new Color(0, 0, 0, 50));
        bottomPane.setOpaque(false);
        bottomPane.setFocusable(false);
        add(bottomPane, BorderLayout.SOUTH);

        btReturn = new MenuButton("Content/Graphics/UI/returnIcon.png", optionButtonSize, optionButtonSize);
        btReturn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMainMenu();
            }
        });
        btReturn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btReturn.setVisible(false);
        topPane.add(btReturn, BorderLayout.WEST);

        btSettings = new MenuButton("Content/Graphics/UI/settingsIcon.png", optionButtonSize, optionButtonSize);
        btSettings.addActionListener(actionListener);
        btSettings.setName("Settings");
        btSettings.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottomPane.add(btSettings, BorderLayout.WEST);


        btInfo = new MenuButton("Content/Graphics/UI/infoIcon.png", optionButtonSize, optionButtonSize);
        btInfo.setName("Info");
        btInfo.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btInfo.addActionListener(actionListener);
        bottomPane.add(btInfo, BorderLayout.EAST);


    }

    /**
     * Methode zeigt das MenuFrame
     */
    public void showMenu() {
        if (isVisible()) {
            setVisible(false);
        } else {
            setVisible(true);
        }
    }

    /**
     * Methode erstellt HauptMenu mit ButtonNamen
     */
    public void loadMainMenu(String[] mainMenuBTNames) {
        MenuTab mainM = new MenuTab("MainMenu", mainMenuBTNames);
        addAndShowMenuTab(mainM);
        btReturn.setVisible(false);
        btSettings.setVisible(true);
        btInfo.setVisible(false);
        setVisible(true);
    }

    /**
     * Erzwingt das Laden eines Menus
     */
    public void forceAdd(MenuTab customButtonPane) {
        removeMenuPaneByName(customButtonPane.getName());
        addMenuTab(customButtonPane);
    }

    public void forceAdd(JPanel customButtonPane, boolean showReturn, boolean showSettings, boolean showInfo) {
        removeMenuPaneByName(customButtonPane.getName());
        addPanel(customButtonPane, showReturn, showSettings, showInfo);
    }

    /**
     * Erstellen einer Info Seite (InfoPaneText) = nur btReturn wird angezeigt
     */
//    public void addInfo(JPanel yourInfoPanel) {
//        addPanel(yourInfoPanel, true, false, false);
//    }

    /**
     * Löscht Menu Seite über den Namen
     */
    public void removeMenuPaneByName(String pName) {
        for (int i = 0; i < addedMenus.size(); i++) {
            if (addedMenus.get(i).getName().contentEquals(pName)) {
                addedMenus.remove(i);
                break;
            }
        }
    }

    /**
     * Fügt ein MenuTab hinzu - Name ist notwendig zur Identifkation
     */
    public void addMenuTab(MenuTab menu) {
        menu.setVisible(false);
        if (!searchForPanel(menu.getName())) {
            addedMenus.add(menu);
        }
    }

    /**
     * Fügt eine JPanel Seite als MenuTab hinzu
     */
    public void addPanel(JPanel yourPanelWithButtons, boolean showReturn, boolean showSettings, boolean showInfo) {
        MenuTab yourPane = new MenuTab("", yourPanelWithButtons, showReturn, showSettings, showInfo);
        yourPane.setName(yourPanelWithButtons.getName());
        addMenuTab(yourPane);
    }

    /**
     * Fügt ein MenuTab als Seite hinzu und zeigt sie an
     */
    public void addAndShowMenuTab(MenuTab menu) {
        addMenuTab(menu);
        showButtonPaneByName(menu.getName());
    }

    /**
     * Fügt und zeigt eine Seite (die aus einem beliebigen JPanel besteht)
     */
    public void addAndShowPanel(JPanel yourPanelWithButtons, boolean showReturn, boolean showSettings, boolean showInfo) {
        addPanel(yourPanelWithButtons, showReturn, showSettings, showInfo);
        showButtonPaneByName(yourPanelWithButtons.getName());
    }

    public void addAndShowPanel(JPanel yourPanelWithButtons, String heading, boolean showReturn, boolean showSettings, boolean showInfo) {
        addPanel(yourPanelWithButtons, showReturn, showSettings, showInfo);
        getButtonPaneByName(yourPanelWithButtons.getName()).setHeading(heading);
        showButtonPaneByName(yourPanelWithButtons.getName());
    }

    /**
     * Lässt eine Seite per Namen suchen
     */
    public boolean searchForPanel(String name) {
        for (int i = 0; i < addedMenus.size(); i++) {
            try {
                if (addedMenus.get(i).getName().equals(name)) {
                    return true;
                }
            } catch (Exception e) {
                System.out.println("Exception is catched! The Menu " + i + "___" + addedMenus.get(i) + " has no Name!");
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Zeigt MainMenu
     */
    public void showMainMenu() {
        if (searchForPanel("MainMenu")) {
            showButtonPaneByName("MainMenu");
            btSettings.setVisible(true);
            btInfo.setVisible(true);
            btReturn.setVisible(false);
        } else {
            JOptionPane.showMessageDialog(null, "Es wurde kein Hauptmenü geladen", "", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Zeigt ButtonPane per Name
     */
    public void showButtonPaneByName(String name) {
        if (searchForPanel(name)) {
            setVisible(false);
            for (int i = 0; i < addedMenus.size(); i++) {
                addedMenus.get(i).setVisible(false);
                buttonPane.add(addedMenus.get(i));
            }
            MenuTab found = getButtonPaneByName(name);
            found.setVisible(true);
            setMinimumSize(found.getMinimumSize());
            currentPage = found.getName();
            btReturn.setVisible(found.showReturn);
            btSettings.setVisible(found.showSettings);
            btInfo.setVisible(found.showInfo);
        } else {
            JOptionPane.showMessageDialog(null, "Das gesuchte Button Pane gibt es nicht!", "", JOptionPane.WARNING_MESSAGE);
            showMainMenu();
        }
        if (!isVisible()) {
            setVisible(true);
        }
        buttonPane.revalidate();
        revalidate();
        repaint();
        requestFocus();

    }

    public MenuTab getButtonPaneByName(String name) {
        for (int i = 0; i < addedMenus.size(); i++) {
            if (addedMenus.get(i).getName().contentEquals(name)) {
                return addedMenus.get(i);
            }
        }
        return null;
    }

    public static class MenuTab extends JPanel {
        /**
         * Klasse für die Buttons im Menu (sogenannte Menu Seiten)
         */
        boolean showReturn = false, showSettings = false, showInfo = false;
        private JPanel contentPane;
        private InfoTextArea heading;
        private MenuButton[] buttons = new MenuButton[1];


        public MenuTab(String pName, String[] btNames) {
            super();
            setLayout(new BorderLayout());
            contentPane = new JPanel(new BorderLayout());
            add(contentPane);
            createButtons(btNames);
            setName(pName);
        }

        public MenuTab(String pName, String[] btNames, boolean showReturn, boolean showSettings, boolean showInfo) {
            this(pName, btNames);
            this.showReturn = showReturn;
            this.showSettings = showSettings;
            this.showInfo = showInfo;
            setName(pName);
        }

        public MenuTab(String pName, JPanel panel) {
            super();
            setLayout(new BorderLayout());
            contentPane = panel;
            add(contentPane, BorderLayout.CENTER);
            setName(pName);
        }

        public MenuTab(String name, JPanel panel, boolean showReturn, boolean showSettings, boolean showInfo) {
            this(name, panel);
            this.showReturn = showReturn;
            this.showSettings = showSettings;
            this.showInfo = showInfo;
        }

        public void createButtons(String[] buttonnames) {
            setOpaque(false);
            contentPane.setOpaque(false);
            contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

            heading = new InfoTextArea("");
            heading.setAlignmentX(CENTER_ALIGNMENT);
            heading.setAlignmentY(CENTER_ALIGNMENT);
            add(heading, BorderLayout.NORTH);

            FontMetrics fm;
            int minWidth = 0;
            int minHeight = 0;
            int gap = 10;
            buttons = new MenuButton[buttonnames.length];
            for (int i = 0; i < buttons.length; i++) {
                buttons[i] = new MenuButton(buttonnames[i]);
                fm = buttons[i].getFontMetrics(buttons[i].getFont());
                buttons[i].addActionListener(actionListener);
                buttons[i].setAlignmentX(Component.CENTER_ALIGNMENT);
                buttons[i].setAlignmentY(Component.CENTER_ALIGNMENT);
                buttons[i].setFocusable(false);
                contentPane.add(Box.createRigidArea(new Dimension(0, gap)));
                contentPane.add(buttons[i]);

                if (fm.stringWidth(buttons[i].getText()) > minWidth) {
                    minWidth = fm.stringWidth(buttons[i].getText());
                }
                if (fm.getHeight() > minHeight) {
                    minHeight = fm.getHeight();
                }
            }
            int leftSpace = 200;
            setMinimumSize(new Dimension(minWidth + leftSpace, (minHeight + gap) * (buttons.length + 1) + leftSpace));
        }

        public void setSubTextInHeading(String text) {
            heading.appendRegularText("\n" + text);
            heading.centerText();
        }

        public InfoTextArea getHeading() {
            return heading;
        }

        public void setHeading(String headingText) {
            heading.appendHeading(headingText);
            heading.centerText();
        }

        public MenuButton[] getButtons() {
            return buttons;
        }
    }
}
