import javax.swing.FocusManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GUI extends JFrame implements ActionListener, MouseListener, MouseMotionListener, ComponentListener {
    /**
     * Model + Controller des Spiels
     */
    public static final int FRAME_WIDTH = 1080;
    public static final int FRAME_HEIGHT = 720;

    public static int GAMEPANEL_WIDTH = 500;
    public static int GAMEPANEL_HEIGHT = 500;
    public static JPanel east = new JPanel(new GridBagLayout());
    private JPanel north = new JPanel(new FlowLayout());
    public static JPanel tempDebugPane;
    private static GridBagConstraints gbc = new GridBagConstraints();
    public JPanel west = new JPanel(new FlowLayout());
    private JPanel south = new JPanel(new FlowLayout());
    private KeyManager keyManager;
    public JTextArea debugAnzeige = new JTextArea();
    private GamePanel gamePanel;
    public MenuUI menuUI;
    private Editor editor;
    private Level level;

    private Loop loop = new Loop();
    private Thread t = new Thread(loop);
    public static final int FPS = 60; //(Bilder pro Sekunde)
    public static final long maxLoopTime = 1000 / FPS;

    public Container cp;
    private JLabel focuslb = new JLabel();


    public GUI() {
        super();
        setName("GUI");
        this.setBackground(Color.white);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (d.width - getSize().width) / 2;
        int y = (d.height - getSize().height) / 2;

        setLocation(x, y);
        setTitle("ChaseME");
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

//        tempDebugPane = new JPanel(new GridLayout(0,1,5,5));
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

        JButton btMenu = new JButton("Menu");
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
                getOwner().requestFocusInWindow();
                getOwner().requestFocus();
            }
        });
        south.add(getFocus);
    }

    private void createEditor() {
        editor = new Editor(gamePanel,keyManager);
        level.setLevel(0);
        level.clear();
    }

    public void actionPerformed(ActionEvent evt) {
        JButton temp = (JButton) evt.getSource();

        if (level.level == 0) {
            editor.actionPerformed(evt);
        }

        switch (temp.getText()) {
            case "Exit":
                System.exit(0);
                this.requestFocus();
                break;
            case "GridLines":
                for (int i = 0; i < level.analytics.length; i++) {
                    if (level.analytics[i].showGridLines == true) {
                        level.analytics[i].showGridLines = false;
                        level.analytics[i].showGridLines = false;
                        level.analytics[i].showTileIndices = false;
                        level.analytics[i].showStartTarget = false;
                        level.analytics[i].displayBlockedTiles = false;
                        level.analytics[i].displayPath = false;
                        level.analytics[i].displayPathTiles = false;
                        level.analytics[i].moverHitbox = false;
                        this.requestFocus();
                    } else {
                        level.analytics[i].showGridLines = true;
                        level.analytics[i].showTileIndices = true;
                        level.analytics[i].showStartTarget = true;
                        level.analytics[i].displayBlockedTiles = true;
                        level.analytics[i].displayPath = true;
                        level.analytics[i].displayPathTiles = true;
                        level.analytics[i].setMover(level.mover);
                        level.analytics[i].moverHitbox = true;
                        this.requestFocus();
                    } // end of if-else
                } // end of for
                break;

            case "Level":
                if (!menuUI.searchForPanel("LevelMenu")) {
                    JPanel levelMenuPane = level.createLevelMenu();
                    levelMenuPane.setName("LevelMenu");
                    menuUI.addCustomPanel(levelMenuPane);
                } else {
                    menuUI.showButtonPane("LevelMenu");
                }

                this.requestFocus();
                break;

            case "Editor":
                if (editor == null) {
                    createEditor();
                    JPanel editorMenuPane = editor.createEditorMenu();
                    editorMenuPane.setName("Editor");
                    menuUI.addCustomPanel(editorMenuPane);
                } else {
                    menuUI.showButtonPane("Editor");
                }

                this.requestFocus();
                break;

            case "Neustart":
                System.out.println("Spawn to CP");
                break;
            case "TestButton":

                break;
        }
        this.requestFocus();
    }

    private void createGameComponents() {
        JPanel mainPane = new JPanel(new BorderLayout(), true);
        JPanel gamePane = new JPanel(new BorderLayout(), true);
        cp.add(gamePane);
        gamePanel = new GamePanel(gamePane, this, this, keyManager);
        gamePanel.addtoRender(this);
        menuUI = new MenuUI(mainPane, this);
        menuUI.setAlwaysOnTop(true);
        menuUI.loadMainMenu(new String[]{"Level", "Restart", "GridLines", "Editor", "Exit", "TestButton"});
//        gamePanel.toFront();
        menuUI.toFront();

        level = new Level(gamePanel, keyManager);

        t.start();
    }

    public void render(Graphics2D g2d ) {
        try {
            if (level.isActive()) {
                level.renderLevel(g2d);
            } else {
                editor.renderEditor(g2d);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Render Fehler ! \n Level/Editor ist möglicherweise noch gar nicht existent");
        }
    }
    
    public Level getLevel() {
        return level;
    }

    public Editor getEditor() {
        return editor;
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public static void main(String[] args) {
        //https://www.java-forum.org/thema/swing-komponenten-standart-windows-design.34019/
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e) {
            JOptionPane.showMessageDialog(null,"Das LookAndFeel des Betriebssystems kann nicht geladen werden!\nDas Programm wird daher jetzt im Java-LookAndFeel angezeigt.","Allgemeine Ausnahme",JOptionPane.ERROR_MESSAGE);
        }
        new GUI();
    } // end of main

    public void update() {
        keyManager.update();
        if (keyInputToMove(keyManager).getX() != 0 || keyInputToMove(keyManager).getY() != 0) {
            this.requestFocus();
        }
        if (level.level != 0) {
            level.updateLevel();
        }else { //Editor:
            editor.update();
        }

        GAMEPANEL_HEIGHT = gamePanel.getWidth();
        GAMEPANEL_WIDTH = gamePanel.getWidth();
        debugAnzeige.setText("GP Groesse:" + GAMEPANEL_WIDTH + " | " + GAMEPANEL_HEIGHT + "\n" + gamePanel.getCamera());

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
        try {
            if (FocusManager.getCurrentManager().getFocusOwner().getName() == null) {
                System.out.println(FocusManager.getCurrentManager().getFocusOwner());
                focuslb.setText("See in OutPrint");
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
        if (level.level != 0) {
            level.mouseClicked(e);
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
        if (level.level == 0) {
            editor.mouseDragged(e);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }


    @Override
    public void componentResized(ComponentEvent e) {
        menuUI.setSize(getSize());
        System.out.println("Groesse vom GUI.GamePanel hat sich geändert " + getSize());
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
                    System.out.println("Zu langsam");
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
