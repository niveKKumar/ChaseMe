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

    public JPanel south = new JPanel(new FlowLayout());
    private JPanel north = new JPanel(new FlowLayout());
    public JPanel east = new JPanel(new GridBagLayout());
    public JPanel west = new JPanel(new BorderLayout());
    public JTextArea debugAnzeige = new JTextArea();

    public MenuUI menuUI;
    public KeyManager keyManager;
    private JPanel tempDebugPane;
    private GamePanel gamePanel;
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
        setVisible(true);
        createGameComponents();
    }

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

        tempDebugPane = new JPanel();
        west.add(tempDebugPane, BorderLayout.NORTH);

        debugAnzeige.setFocusable(false);
        debugAnzeige.setBorder(BorderFactory.createLineBorder(Color.black));
        debugAnzeige.setMinimumSize(new Dimension(east.getWidth(), 50));
        debugAnzeige.setSize(100, 100);
        tempDebugPane.add(debugAnzeige);
        debugAnzeige.setText("Debugging TextArea");
        focuslb.setText("Shows current Focus");
        focuslb.setBorder(BorderFactory.createLineBorder(Color.red, 3));
        tempDebugPane.add(focuslb);


        JButton btMenu = new JButton("Menu");
        btMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuUI.toFront();
                menuUI.showMenu();
                menuUI.repaint();
                repaint();
                revalidate();
            }
        });
        south.add(btMenu);

        JButton appRestart = new JButton("Komplettes Spiel neustarten");
        appRestart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new GUI();
            }
        });
        south.add(appRestart);
    }

    private void createEditor() {
        editor = new Editor(this);
        level.setLevel(0);
        level.clear();
    }

    private void createGameComponents() {
        JPanel mainPane = new JPanel(new BorderLayout(), true);
        cp.add(mainPane);

        gamePanel = new GamePanel(mainPane, this);
        menuUI = new MenuUI(mainPane, this);
        menuUI.loadMainMenu(new String[]{"Level", "Restart", "GridLines", "Editor", "Exit", "TestButton"});
        gamePanel.toBack();
        menuUI.toFront();

        level = new Level(gamePanel);

        t.start();
    }

    public Point keyInputToMove(/*KeyManager keyManager*/) {
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
        this.requestFocus();
        return new Point(xMove, yMove);
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

    public void render(Graphics2D g2d) {

        try {
            if (level.isActive()) {
                level.renderLevel(g2d);
            } else {
                editor.renderEditor(g2d);
            }
        } catch (Exception e) {
            System.out.println("Level ist noch gar nicht existent");
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
        if (level.level != 0) {
            level.updateLevel();
        }else { //Editor:
            editor.update();
        }
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
        repaint();
        //Debugging:
        try {
            if (FocusManager.getCurrentManager().getFocusOwner().getName() == null) {
                System.out.println(FocusManager.getCurrentManager().getFocusOwner());
                focuslb.setText("See in OutPrint");
            } else {
                focuslb.setText(FocusManager.getCurrentManager().getFocusOwner().getName());
            }
        } catch (Exception e) {
            System.out.println("Focus outside");
        }
    }


    //LISTENER- Methoden:
    @Override
    public void mouseClicked(MouseEvent e) {
        if (level.level != 0) {
            level.mouseClicked(e);
        } else {
            editor.mouseClicked(e);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

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
        System.out.println("Groesse vom GamePanel hat sich geändert " + getSize());
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
