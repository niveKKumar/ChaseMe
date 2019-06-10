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
    //Debuggin:
    public String currentPage;
    //Core Objects:
    private JPanel topPane;   //(MainPane)
    private JPanel bottomPane;
    private MenuButton btSettings;
    private MenuButton btReturn;
    private MenuButton btInfo;
    private static ActionListener actionListener;
    private JPanel buttonPane; //(mittige Pane für MenuTabs...)
    private LinkedList<MenuTab> addedMenus;


    /**
     * Standard Konstruktor, womit Menu JFrame erstellt wird
     */
    public MenuUI(JPanel display, ActionListener act) {
//        super("MenuUI", true, true, false, true);
        super("Main Menu");
        setSize(MENUUI_WIDTH, MENUUI_HEIGHT);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (d.width - getSize().width) / 2;
        int y = (d.height - getSize().height) / 2;
        setLocation(x, y);
        actionListener = act;
        addedMenus = new LinkedList<>();
        setName("MenuUI");
//        display.add(this, BorderLayout.CENTER);
        setFocusable(false);
//        setOpaque(false);
        setBackground(null);
        setLayout(new BorderLayout());
        createMenuPane();
//        ((BasicInternalFrameUI) getUI()).setNorthPane(null);
//        setBorder(null);
//        setBorder(BorderFactory.createLineBorder(Color.black, 5));
//        try {
//            setMaximum(false);
//        } catch (PropertyVetoException e) {
//            e.printStackTrace();
//        }
        repaint();
        revalidate();
    }
    /**
     * Konstruktor mit direkter Eingabe der Haupt Menu Buttons
     */
    public MenuUI(JPanel display, String[] btStringNames, ActionListener e) {
        new MenuUI(display, e);
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

//        //Main Butons:
        buttonPane = new JPanel(new BorderLayout());
        buttonPane.setBorder(BorderFactory.createLineBorder(Color.pink,5));
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
        btInfo.setVisible(true);
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
                System.out.println("Gefunden und gelöscht");
                break;
            }
        }
        System.out.println("Couldnt Find the PaneName");
    }

    /**
     * Fügt ein MenuTab hinzu
     */
    public void addMenuTab(MenuTab menu) {
        //Debugging:
        if (menu.getName().equals(".")) {
        }

        if (!searchForPanel(menu.getName())) {
            menu.setVisible(false);
            addedMenus.add(menu);
            System.out.println("Added " + menu.getName());
        }
    }

    /**
     * Fügt eine JPanel Seite als MenuTab hinzu
     */
    public void addPanel(JPanel yourPanelWithButtons, boolean showReturn, boolean showSettings, boolean showInfo) {
        MenuTab yourPane;
        yourPane = new MenuTab(yourPanelWithButtons.getName(), yourPanelWithButtons, showReturn, showSettings, showInfo);
        System.out.println("Converted JPanel to MenuTab:" + yourPane.getName());
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
                    System.out.println("You searched for " + name + "and the Menu" + addedMenus.get(i) + "was found");
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
    public void showMainMenu(){
        if (searchForPanel("MainMenu")) {
            showButtonPaneByName("MainMenu");
            btSettings.setVisible(true);
            btInfo.setVisible(true);
            btReturn.setVisible(false);
        } else {
            System.out.println("MainMenu nicht geladen!");
        }
    }

    /**
     * Zeigt ButtonPane per Name
     */
    public void showButtonPaneByName(String name) {
        if (!isVisible()) {
            setVisible(true);
        }
        if (searchForPanel(name)) {
            for (int i = 0; i < addedMenus.size(); i++) {
                addedMenus.get(i).setVisible(false);
                buttonPane.add(addedMenus.get(i));
            }
            MenuTab found = getButtonPaneByName(name);
            found.setVisible(true);
            System.out.println("Found " + found.getName() + " and setted visible ");
            setMinimumSize(found.getMinimumSize());
            currentPage = found.getName();
            btReturn.setVisible(found.showReturn);
            btSettings.setVisible(found.showSettings);
            btInfo.setVisible(found.showInfo);
        } else {
            System.out.println("Button Pane nicht geladen!!");
        }
        buttonPane.revalidate();
        revalidate();
        repaint();
        requestFocus();

    }

    public MenuTab getButtonPaneByName(String name) {
        for (int i = 0; i < addedMenus.size(); i++) {
            if (addedMenus.get(i).getName().contentEquals(name)) {
                System.out.println("Name is in addedMenus");
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
        JPanel contentPane;
        JLabel heading;


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
            add(contentPane);
            setName(pName);
            System.out.println("setted cp and added" + pName);
        }

        public MenuTab(String name, JPanel panel, boolean showReturn, boolean showSettings, boolean showInfo) {
            this(name, panel);
            this.showReturn = showReturn;
            this.showSettings = showSettings;
            this.showInfo = showInfo;
        }

        public void createButtons(String[] btNames) {
            setOpaque(false);
            contentPane.setOpaque(false);
            contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = 0;
            gbc.gridx = 0;
            gbc.anchor = GridBagConstraints.NORTH;
            heading = new JLabel();
            heading.setAlignmentX(CENTER_ALIGNMENT);
            heading.setAlignmentY(CENTER_ALIGNMENT);
            contentPane.add(heading, gbc);

            FontMetrics fm;
            int minWidth = 0;
            int minHeight = 0;
            int gap = 10;
            MenuButton[] buttons = new MenuButton[btNames.length];
            for (int i = 0; i < buttons.length; i++) {
                buttons[i] = new MenuButton(btNames[i]);
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

        public void setHeading(String headingText) {
            heading.setText(headingText);
        }
    }
}
