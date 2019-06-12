import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GUI extends JFrame implements ActionListener, MouseListener, MouseMotionListener, ComponentListener {
    /**
     * Model + Controller des Spiels
     */

    public static final int FRAME_WIDTH = 800;
    public static final int FRAME_HEIGHT = 800;
    public static final int FPS = 30; //(Bilder pro Sekunde)
    public static final long maxLoopTime = 1000 / FPS;
    public static GUI gui;
    public static int GAMEPANEL_WIDTH = 700;
    public static int GAMEPANEL_HEIGHT = 700;
    public static JPanel east = new JPanel(new GridBagLayout());
    public static JPanel south = new JPanel(new FlowLayout());
    public JPanel west = new JPanel(new FlowLayout());
    public MenuUI menuUI;
    public Container cp;
    private JPanel north = new JPanel(new FlowLayout());
    private KeyManager keyManager;
    private GamePanel gamePanel;
    private Editor editor;
    private Game game;
    private Loop loop = new Loop();
    private Thread t = new Thread(loop);


    public GUI() {
        super();
        gui = this;
        setName("GUI");
        this.setBackground(Color.white);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (d.width - getSize().width) / 2;
        int y = (d.height - getSize().height) / 2;

        setLocation(x, y);
        setTitle("Chase ME - Ein RPG Spiel zum Selbst Programmieren");
        setResizable(true);
        cp = getContentPane();
        cp.setLayout(new BorderLayout());
        createGUI();
        createGameComponents();
        setVisible(true);
        requestFocus();
    }


    public static Point keyInputToMove(KeyManager keyManager) {
        int xMove = 0;
        int yMove = 0;
        if (keyManager.up) yMove = -1;
        if (keyManager.down) yMove = 1;
        if (keyManager.left) xMove = -1;
        if (keyManager.right) xMove = 1;
        if (keyManager.upLeft) {
            yMove = -1;
            xMove = -1;
        }
        if (keyManager.upRight) {
            yMove = -1;
            xMove = 1;
        }
        if (keyManager.downLeft) {
            yMove = 1;
            xMove = -1;
        }
        if (keyManager.downRight) {
            yMove = 1;
            xMove = 1;
        }
        return new Point(xMove, yMove);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Das LookAndFeel des Betriebssystems kann nicht geladen werden!\nDas Programm wird daher jetzt im Java-LookAndFeel angezeigt.", "Allgemeine Ausnahme", JOptionPane.ERROR_MESSAGE);
        }
        new GUI();
    } // end of main

    private void createGUI() {
        keyManager = new KeyManager();
        addKeyListener(keyManager);
        addMouseListener(this);
        addMouseMotionListener(this);

        cp.setLayout(new BorderLayout());
        cp.add(south, BorderLayout.SOUTH);
        cp.add(east, BorderLayout.EAST);
        cp.add(west, BorderLayout.WEST);
        cp.add(north, BorderLayout.NORTH);

        JButton btMenu = new JButton("MenuTab");
        btMenu.setFocusable(false);
        btMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (menuUI.isVisible()) {
                    menuUI.setVisible(false);
                } else {
                    menuUI.setVisible(true);
                }
                menuUI.repaint();
                repaint();
                revalidate();
            }
        });
        btMenu.setFocusable(false);
        north.add(btMenu);

        JButton appRestart = new JButton("Spiel neustarten");
        appRestart.setFocusable(false);
        appRestart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new GUI();
            }
        });
        north.add(appRestart);

        JButton getFocus = new JButton("Regain Focus");
        getFocus.setFocusable(false);
        getFocus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gamePanel.requestFocus();
                gamePanel.revalidate();
            }
        });
        north.add(getFocus);

        gamePanel = new GamePanel(cp, this, this, keyManager);
        cp.add(gamePanel);
        gamePanel.addtoRender(this);
        menuUI = new MenuUI(this);
    }

    private void createEditor() {
        editor = new Editor(this, gamePanel, keyManager, east);
        east.add(editor.getMapSelectActionsPanel());
        game.clear();
    }

    private void createGame() {
        MenuUI.MenuTab settings = game.createSettings();
        settings.setName("Settings");
        menuUI.addMenuTab(settings);
        if (menuUI.searchForPanel("editorInfo")) {
            menuUI.removeMenuPaneByName("editorInfo");
        }
        menuUI.getButtonPaneByName("MainMenu").showInfo = false;
    }

    public void actionPerformed(ActionEvent evt) {
        if (editor != null && editor.isActive()) {
            editor.actionPerformed(evt);
        }
        JButton temp = (JButton) evt.getSource();
        if (temp.getName() != null && temp.getName().contentEquals("Info")) {
            if (editor != null && editor.isActive()) {
                menuUI.showButtonPaneByName("editorInfo");
            }
        }
        if (temp.getName() != null && temp.getName().contentEquals("Settings")) {
            menuUI.showButtonPaneByName("Settings");
        }

        switch (temp.getText()) {
            case "Exit":
                System.exit(0);
                this.requestFocus();
                break;
            case "GridLines":
                game.setGridLines();
                break;

            case "Game":
                if (!menuUI.searchForPanel("gamePage")) {
                    JPanel levelMenu = game.createLevelMenu();
                    levelMenu.setName("gamePage");
                    menuUI.addAndShowPanel(levelMenu, true, false, true);
                } else {
                    menuUI.showButtonPaneByName("gamePage");
                }

                this.requestFocus();
                break;

            case "Editor":
                if (editor == null || !editor.isActive()) {
                    createEditor();
                    MenuUI.MenuTab editorMenu = editor.createEditorMenu();
                    editorMenu.setName("EditorMenu");
                    menuUI.addAndShowMenuTab(editorMenu);

                    JPanel editorInfo = editor.createInfoPane();
                    editorInfo.setName("editorInfo");
                    menuUI.addPanel(editorInfo, true, false, false);
                    game.clear();
                } else {
                    menuUI.showButtonPaneByName("EditorMenu");
                }
                this.requestFocus();
                break;

            case "Restart":
                if (game.isActive()) {
                    game.respawn(game.whichLevel());
                }
                System.out.println("Restart");
                break;

            case "1":
                game.createlevel1();
                menuUI.showMenu();
                break;
            case "2":
                game.createlevel2();
                menuUI.showMenu();
                break;
            case "3":
                game.createlevel3();
                menuUI.showMenu();
                break;
            case "Lobby":
                game.createLobby();
                menuUI.showMenu();
                break;
            case "Schwierigkeitsgrad":
                game.changeDifficulty();
                game.createLobby();
                menuUI.showMenu();
                break;
            case "<html> Eigene<br>Schwierigkeit erstellen </html>":
                game.createOwnDifficulty();
                game.setDifficulty(999);
                game.createLobby();
                menuUI.showMenu();
                break;
        }
        this.requestFocus();
    }

    private void createGameComponents() {
        //GridLines kann beim erstellen ganz einfach eingeschaltet werden
        menuUI.loadMainMenu(new String[]{"Game", "Restart"/*, "GridLines"*/, "Editor", "Exit"});
        menuUI.getButtonPaneByName("MainMenu").setHeading("ChaseME");
        menuUI.getButtonPaneByName("MainMenu").setSubTextInHeading("Das Spiel zum Selbstprogrammieren");
        game = new Game(gamePanel, keyManager);
        createGame();
        t.start();
    }

    public void render(Graphics2D g2d) {
        try {
            if (game.isActive()) {
                game.renderLevel(g2d);
            } else {
                editor.renderEditor(g2d);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Game getGame() {
        return game;
    }

    public Editor getEditor() {
        return editor;
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public void update() {
        keyManager.update();
        if (game.isActive()) {
            game.updateLevel();
        } else { //Editor:
            editor.update();
        }
        GAMEPANEL_HEIGHT = gamePanel.getWidth();
        GAMEPANEL_WIDTH = gamePanel.getWidth();

        gamePanel.repaint();
        gamePanel.revalidate();
        //Debugging:
        if (this.hasFocus() && !menuUI.isActive()) {
            menuUI.setAlwaysOnTop(true);
        } else {
            menuUI.setAlwaysOnTop(false);
        }
    }


    //LISTENER- Methoden:
    @Override
    public void mouseClicked(MouseEvent e) {
        e.getSource();
        if (game.isActive()) {
            game.mouseClicked(e);
            //System.out.println("Level Clicked");
        } else {
            editor.mouseClicked(e);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        requestFocusInWindow();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!game.isActive() && e.getSource().equals(gamePanel)) {
            editor.mouseDragged(e);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }


    @Override
    public void componentResized(ComponentEvent e) {

    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }


    class Loop implements Runnable {
        long timestamp = 0;
        long oldTimestamp = 0;

        public void run() {
            while (true) {
                oldTimestamp = System.currentTimeMillis();
                update();
                timestamp = System.currentTimeMillis();

                if ((timestamp - oldTimestamp) > maxLoopTime) {
                    //System.out.println("Zu langsam");
                    continue;
                }

                try {
                    Thread.sleep(maxLoopTime - (timestamp - oldTimestamp));
                } catch (Exception e) {
                    System.out.println("Loopfehler: " + maxLoopTime); // - (timestamp - oldTimestamp)));
                }
            }
        }
    }

}
