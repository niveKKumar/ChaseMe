import javax.swing.FocusManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GUI extends JFrame implements ActionListener, MouseListener, MouseMotionListener, ComponentListener {
    /**
     * Model + Controller des Spiels
     */
    public static GUI gui;
    public static final int FRAME_WIDTH = 1080;
    public static final int FRAME_HEIGHT = 720;

    public static int GAMEPANEL_WIDTH = 500;
    public static int GAMEPANEL_HEIGHT = 500;
    public static JPanel east = new JPanel(new GridBagLayout());
    private JPanel north = new JPanel(new FlowLayout());
    public static JPanel tempDebugPane;
    private static GridBagConstraints gbc = new GridBagConstraints();
    public JPanel west = new JPanel(new FlowLayout());
    public static JPanel south = new JPanel(new FlowLayout());
    private KeyManager keyManager;
    public JTextArea debugAnzeige = new JTextArea();

    public MenuUI menuUI;
    private GamePanel gamePanel;
    private Editor editor;
    private Game game;

    private Loop loop = new Loop();
    private Thread t = new Thread(loop);
    public static final int FPS = 60; //(Bilder pro Sekunde)
    public static final long maxLoopTime = 1000 / FPS;

    public Container cp;
    private JLabel focuslb = new JLabel();


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

    /**
     * Temp:
     */
    public static void addToDebugPane(JComponent comp) {
        gbc.gridx = 0;
        gbc.gridy = gbc.gridy + 1;
        tempDebugPane.add(comp, gbc);
    }

    public static void addToEast(JComponent comp) {
        gbc.gridx = 0;
        gbc.gridy = gbc.gridy + 1;
        east.add(comp, gbc);
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

    private void createGUI() {
        //Listener:
        keyManager = new KeyManager();
        addKeyListener(keyManager);
        addMouseListener(this);
        addMouseMotionListener(this);

        cp.setLayout(new BorderLayout());
        cp.add(south, BorderLayout.SOUTH);
        cp.add(east, BorderLayout.EAST);
        cp.add(west, BorderLayout.WEST);
        cp.add(north, BorderLayout.NORTH);

        tempDebugPane = new JPanel(new GridBagLayout());
        debugAnzeige.setFocusable(false);
        debugAnzeige.setBorder(BorderFactory.createLineBorder(Color.black));
        debugAnzeige.setSize(100, 100);
        debugAnzeige.setText("Debugging TextArea");
        focuslb.setText("Shows current Focus");
        focuslb.setBorder(BorderFactory.createLineBorder(Color.red, 3));
        addToDebugPane(debugAnzeige);
        addToDebugPane(focuslb);
        west.add(tempDebugPane);

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
        south.add(btMenu);

        JButton appRestart = new JButton("Spiel neustarten");
        appRestart.setFocusable(false);
        appRestart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new GUI();
            }
        });
        south.add(appRestart);

        JButton getFocus = new JButton("Regain Focus");
        getFocus.setFocusable(false);
        getFocus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gamePanel.requestFocus();
                gamePanel.revalidate();
            }
        });
        south.add(getFocus);
    }

    private void createEditor() {
        editor = new Editor(this, gamePanel, keyManager);
        game.clear();
    }

    public void actionPerformed(ActionEvent evt) {
//        if (game != null && game.isActive()) {
//            game.actionPerformed(evt);
//        }
        if (editor != null && editor.isActive()) {
            editor.actionPerformed(evt);
        }
        JButton temp = (JButton) evt.getSource();
        if (temp.getName() != null && temp.getName().contentEquals("Info")) {
            System.out.println("info clicked");
            if (game != null && game.isActive()) {
                menuUI.showButtonPaneByName("guiInfo");
            }
            if (editor != null && editor.isActive()) {
                menuUI.showButtonPaneByName("editorInfo");
            }
        }
        if (temp.getName() != null && temp.getName().contentEquals("Settings")) {
            menuUI.showMainMenu();
            System.out.println("Settings pressed and MainMenu loaded");
        }
        switch (temp.getText()) {
            case "Exit":
                System.exit(0);
                this.requestFocus();
                break;
            case "GridLines":
                for (int i = 0; i < game.analytics.length; i++) {
                    if (game.analytics[i].active == true) {
                        game.analytics[i].setActive(false);
                        this.requestFocus();
                    } else {
                        game.analytics[i].setMover(game.mover);
                        game.analytics[i].setActive(true);
                        this.requestFocus();
                    } // end of if-else
                } // end of for
                break;

            case "Game":
                if (!menuUI.searchForPanel("Level")) {
                    MenuUI.MenuTab levelMenu = game.createLevelMenu();
                    levelMenu.setName("Level");
                    menuUI.addAndShowMenuTab(levelMenu);
                } else {
                    menuUI.showButtonPaneByName("Level");
                }
                this.requestFocus();
                break;

            case "Editor":
                if (editor == null) {
                    createEditor();
                    MenuUI.MenuTab editorMenu = editor.createEditorMenu();
                    editorMenu.setName("Editor");
                    menuUI.addInfo(editor.createInfoPane());
                    menuUI.addAndShowMenuTab(editorMenu);
                } else {
                    menuUI.showButtonPaneByName("Editor");
                }
                this.requestFocus();
                break;

            case "Restart":
                if (game.isActive()) {
                    game.respawn(game.whichLevel());
                }
                System.out.println("Restart");
                break;
            case "TestButton":

                break;

            /**Level*/
            case "1":
                menuUI.showMenu();
                game.createlevel1();
                menuUI.showButtonPaneByName("MainMenu");
                break;
            case "2":
                menuUI.showMenu();
                game.createlevel2();
                menuUI.showButtonPaneByName("MainMenu");
                break;
            case "3":
                menuUI.showMenu();
                game.createlevel3();
                menuUI.showButtonPaneByName("MainMenu");
                break;
            case "4":
                menuUI.showMenu();
                game.createlevel4();
                menuUI.showButtonPaneByName("MainMenu");
                break;
            case "5":
                menuUI.showMenu();
                game.createlevel5();
                menuUI.showButtonPaneByName("MainMenu");
                break;
            /** Editor Buttons:*/ // FIXME: 03.05.2019 Action Peformed in Editor ? (siehe V. 1.05.2019)
        }
        this.requestFocus();
    }

    private void createGameComponents() {
        gamePanel = new GamePanel(cp, this, this, keyManager);
        cp.add(gamePanel);
        gamePanel.addtoRender(this);
        menuUI = new MenuUI(null, this);
        JPanel guiInfoPane = new JPanel(new BorderLayout());
        JTextArea info = new JTextArea
                ("Willkommen zu Chase ME! Du willst wissen was das Spiel kann? " +
                        "\n Zur Entstehung... Steuerung: " +
                        "\n W: Oben Laufen  S: Unten Laufen" +
                        "\n A: Links Laufen D: Rechts Laufen" +
                        "\n LG Kevin");
        guiInfoPane.add(info);
        guiInfoPane.setName("guiInfo");
        menuUI.addInfo(guiInfoPane);
        menuUI.loadMainMenu(new String[]{"Game", "Restart", "GridLines", "Editor", "Exit", "TestButton"});
        game = new Game(gamePanel, keyManager);

        t.start();
    }

    public void render(Graphics2D g2d ) {
        try {
            if (game.isActive()) {
                game.renderLevel(g2d);
            } else {
                editor.renderEditor(g2d);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Render Fehler ! \n Game/Editor ist möglicherweise noch gar nicht existent");
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

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e) {
            JOptionPane.showMessageDialog(null,"Das LookAndFeel des Betriebssystems kann nicht geladen werden!\nDas Programm wird daher jetzt im Java-LookAndFeel angezeigt.","Allgemeine Ausnahme",JOptionPane.ERROR_MESSAGE);
        }
        new GUI();
    } // end of main

    public void update() {
        keyManager.update();
        if (game.isActive()) {
            game.updateLevel();
        }else { //Editor:
            editor.update();
        }
        GAMEPANEL_HEIGHT = gamePanel.getWidth();
        GAMEPANEL_WIDTH = gamePanel.getWidth();
        debugAnzeige.setText("GP Groesse:" + GAMEPANEL_WIDTH + " | " + GAMEPANEL_HEIGHT + "\n" + menuUI.currentPage);

        gamePanel.repaint();
        gamePanel.revalidate();
        // Nicht nötig wenn maximized !!
//        if (gamePanel.getSize() != menuUI.getSize()){
//            if (gamePanel.getWidth() > menuUI.getWidth()){
//            menuUI.setSize(gamePanel.getWidth(),menuUI.getHeight());
//            }else{
//                gamePanel.setSize(menuUI.getWidth(),gamePanel.getHeight());}
//
//            if (gamePanel.getHeight() > menuUI.getHeight()){
//              menuUI.setSize(menuUI.getWidth(),gamePanel.getHeight());
//            }else{
//                gamePanel.setSize(gamePanel.getWidth(),menuUI.getHeight());}
//
//        }
        //Debugging:
        if (this.hasFocus() && !menuUI.isActive()) {
            menuUI.setAlwaysOnTop(true);
        } else {
            menuUI.setAlwaysOnTop(false);
        }
        try {
            if (FocusManager.getCurrentManager().getFocusOwner().getName() == null) {
//               //System.out.println(FocusManager.getCurrentManager().getFocusOwner());
                focuslb.setText("See in OutPrint /*(Im Moment ausgeschaltet)*/");
            } else {
                if (FocusManager.getCurrentManager().getFocusOwner().getName().equals("null.contentPane")) {
                    this.requestFocus();
                }
                focuslb.setText(FocusManager.getCurrentManager().getFocusOwner().getName());
            }
        } catch (Exception e) {
            focuslb.setText("Focus outside");
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
        System.out.println("Dragged");
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }


    @Override
    public void componentResized(ComponentEvent e) {
        menuUI.setSize(getSize());
        //System.out.println("Groesse vom GUI.GamePanel hat sich geändert " + getSize());
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
