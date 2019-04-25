import javax.swing.FocusManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GUI extends JFrame implements ActionListener, MouseListener, MouseMotionListener, KeyListener {
    /**
     * Model + Controller des Spiels
     */
    public static final int FRAME_WIDTH = 1300;
    public static final int FRAME_HEIGHT = 750;

    public static int GAMEPANEL_WIDTH = 0;
    public static int GAMEPANEL_HEIGHT = 0;
    public JPanel south = new JPanel(new FlowLayout());
    private JPanel north = new JPanel(new FlowLayout());
    public JPanel east = new JPanel(new FlowLayout());
    public JPanel west = new JPanel(new FlowLayout());
    public MenuUI menuUI;
    public JTextArea taAnzeige = new JTextArea();

    private Loop loop = new Loop();
    private Thread t = new Thread(loop);
    public static final int FPS = 60; //(Bilder pro Sekunde)
    public static final long maxLoopTime = 1000 / FPS;
    public Container cp;
    private JLabel focuslb = new JLabel();
    public Editor editor;
    public Level level;
    private GamePanel gamePanel;


    public GUI() {
        super();
        this.setBackground(Color.white);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (d.width - getSize().width) / 2;
        int y = (d.height - getSize().height) / 2;
        y = -30;
        setLocation(x, y);
        setTitle("Chase ME");
        setResizable(true);
        cp = getContentPane();
        cp.setLayout(new BorderLayout());
        south.add(focuslb);
//        UIManager.getLookAndFeelDefaults().put("DefaultFont",new Font("Arial",Font.PLAIN,UIManager.getFont(this).getSize()));
        createGUI();
        createGameComponents();
        setVisible(true);

//        System.out.println("Backslash Test:");
//        System.out.println("___________________");
//        String test = new String("Hallo \\ hier zwischen soll ein Blackslash stehen");
//        System.out.println("Anfangsstring: "+test);
//        String changed = test.replaceAll("\\\\","/");
//        System.out.println("Der String wurde ersetzt");
//        System.out.println(changed);
    
    }

    public Point keyInputToMove(KeyManager keyManager) {
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
        gamePanel.requestFocus();
        return new Point(xMove, yMove);
    }

    public void createGameComponents() {
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        gamePanel = new GamePanel(this);
        cp.add(gamePanel);
        menuUI = new MenuUI(gamePanel.getSize(), new String[]{"Level", "Restart", "GridLines", "Editor", "Exit", "TestButton"}, this);
//        cp.add(menuUI);
        level = new Level(gamePanel);
        level.createLobby();
        t.start();
        gamePanel.requestFocus();
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
            case "gridLines":
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
                        gamePanel.requestFocus();
                    } else {
                        level.analytics[i].showGridLines = true;
                        level.analytics[i].showTileIndices = true;
                        level.analytics[i].showStartTarget = true;
                        level.analytics[i].displayBlockedTiles = true;
                        level.analytics[i].displayPath = true;
                        level.analytics[i].displayPathTiles = true;
                        level.analytics[i].setMover(level.mover);
                        level.analytics[i].moverHitbox = true;
                        gamePanel.requestFocus();
                    } // end of if-else
                } // end of for
                break;

            case "Level":
                if (!menuUI.searchForPanel("LevelMenu")) {
                    JPanel levelMenuPane = level.createLevelMenu();
                    levelMenuPane.setName("LevelMenu");
                    menuUI.addCustomPanel(levelMenuPane);
                } else {
                    menuUI.showMenu("LevelMenu");
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
                    menuUI.showMenu("Editor");
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


    private void createGUI() {
        cp.setLayout(new BorderLayout());
        cp.add(south, BorderLayout.SOUTH);
        cp.add(east, BorderLayout.EAST);
        east.setPreferredSize(new Dimension(150, 750));
        cp.add(west, BorderLayout.WEST);
        cp.add(north, BorderLayout.NORTH);
        taAnzeige.setFocusable(false);
        taAnzeige.setBorder(BorderFactory.createLineBorder(Color.black));
        taAnzeige.setMinimumSize(new Dimension(east.getWidth(), 50));
        east.add(taAnzeige);
        JButton btMenu = new JButton("Menu");
        btMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Menu");
                if (menuUI.isVisible()) {
                    menuUI.setVisible(false);
                } else {
                    menuUI.setVisible(true);
                    gamePanel.moveToBack();
                    menuUI.moveToFront();
                }
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
        editor = new Editor(gamePanel, gamePanel.keyManager);
        level.setLevel(0);
        level.clear();
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
        gamePanel.keyManager.update();
        if (level.level != 0) {
            level.updateLevel();
        }else { //Editor:
            editor.update();
        }

        try {
            focuslb.setText("Aktueller Fokus : " + FocusManager.getCurrentManager().getFocusOwner().getName());
        } catch (Exception e) {

        }
        repaint();
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    //LISTENER:
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
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("Key");
    }

    @Override
    public void keyReleased(KeyEvent e) {

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
