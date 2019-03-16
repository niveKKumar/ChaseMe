import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.*;

public class GUI extends JFrame implements ActionListener {
    private GamePanel gamePanel = new GamePanel();
    public JPanel south = new JPanel(new FlowLayout());
    private JPanel north = new JPanel(new FlowLayout());
    public JPanel east = new JPanel(new FlowLayout());
    private JPanel west = new JPanel(new FlowLayout());
    private JButton[] buttons = new JButton[4];
    private JTextArea taAnzeige = new JTextArea();


    private JLabel lbTitel = new JLabel();
    private Loop loop = new Loop();
    private Thread t = new Thread(loop);
    public static final int FPS = 60; //(Bilder pro Sekunde)
    public static final long maxLoopTime = 1000 / FPS;
    public static final int FRAME_WIDTH = 900;
    public static final int FRAME_HEIGHT = 900;
    public Container cp;
    private KeyManager keyManager;
    private DisplayAnalytics[] analytics;
    public Tile start;
    public Tile target;
    public Camera camera;
    private Level level;
    private Editor editor;

    public GUI() {
        super();
        this.setBackground(Color.white);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (d.width - getSize().width) / 2;
        int y = (d.height - getSize().height) / 2;
        //    int y = -100     ;
        setLocation(x, y);
        setTitle("Das tolle Spiel");
        setResizable(false);
        cp = getContentPane();
        camera = new Camera(50, 50);
        keyManager = new KeyManager();
        level = new Level(this);

        this.addKeyListener(keyManager);
        this.setFocusable(true);
        this.requestFocus();
        createGUI();
        createMenu();
        setVisible(true);
    }

    private void createGUI() {
        cp.setLayout(new BorderLayout());
        cp.add(gamePanel, BorderLayout.CENTER);
        cp.add(south, BorderLayout.SOUTH);
        cp.add(east, BorderLayout.EAST);
        cp.add(west, BorderLayout.WEST);
        cp.add(north, BorderLayout.NORTH);
        east.add(taAnzeige);
        taAnzeige.setBorder(BorderFactory.createLineBorder(Color.black));
        taAnzeige.setPreferredSize(new Dimension(90, 750));
        taAnzeige.append("Score: ");
        lbTitel.setFont(new Font("Dialog", 1, 10));
        lbTitel.setText("Das tolle Spiel");
        north.add(lbTitel);
        String btNamesMenu[] = {"Exit", "gridLines", "Tutorial", "Editor"};

        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new JButton(btNamesMenu[i]);
            buttons[i].addActionListener(this);
            south.add(buttons[i]);
        }

    }

    private void createMenu() {
//    level.createlevel1();
        TileSet ts = new TileSet("res/tileSet.png", 12, 12, 3);
        t.start();
    }

    private void createEditor() {
        EditorAbfrage temp = new EditorAbfrage(this, true);
        TileSet ts = new TileSet("res/tileSet.png", 12, 12, 3);
        SpriteSheet playersheet = new SpriteSheet("res/playersheet.png", 4, 3);
        editor = new Editor(this, temp.getMapSizeX(), temp.getMapSizeY(), keyManager, ts);
//    level.setLevel(0);
    }


    public void actionPerformed(ActionEvent evt) {
        JButton temp = (JButton) evt.getSource();
        switch (temp.getText()) {
            case "Exit":
                System.exit(0);
                this.requestFocus();
                break;
            case "gridLines":
                for (int i = 0; i < analytics.length; i++) {
                    if (analytics[i].showGridLines == true) {
                        analytics[i].showGridLines = false;
                        analytics[i].showGridLines = false;
                        analytics[i].showTileIndices = false;
                        analytics[i].showStartTarget = false;
                        analytics[i].displayBlockedTiles = false;
                        analytics[i].displayPath = false;
                        analytics[i].displayPathTiles = false;
                        analytics[i].moverHitbox = false;
                        this.requestFocus();
                    } else {
                        analytics[i].showGridLines = true;
                        analytics[i].showTileIndices = true;
                        analytics[i].showStartTarget = true;
                        analytics[i].displayBlockedTiles = true;
                        analytics[i].displayPath = true;
                        analytics[i].displayPathTiles = true;
                        analytics[i].setMover(level.mover);
                        analytics[i].moverHitbox = true;
                        this.requestFocus();
                    } // end of if-else
                } // end of for
                this.requestFocus();
                break;
            case "Tutorial":
                level.createlevel1();
//        level1EnemyMove();
                this.requestFocus();
                break;

            case "Editor":
                createEditor();
                this.requestFocus();
                break;

            ///EDITOR Buttons:
            case "+":
                System.out.println("Zoom rein");
                this.requestFocus();
                break;

            case "-":
                System.out.println("Zoom raus");
                this.requestFocus();
                break;
            case "Tile Auswaehlen":
                editor.createTileMenu();
                this.requestFocus();
                break;

        }
    }

    public static void main(String[] args) {
//    try {
//      UIManager.setLookAndFeel(UIManager.());
//    }catch(Exception e) {
//      JOptionPane.showMessageDialog(null,"Das LookAndFeel des Betriebssystems kann nicht geladen werden!\nDas Programm wird daher jetzt im Java-LookAndFeel angezeigt.","Allgemeine Ausnahme",JOptionPane.ERROR_MESSAGE);
//    }
        new GUI();
    } // end of main

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(int pXSize, int pYSize) {
        camera = new Camera(pXSize, pYSize);
    }

    public GamePanel getSpielpanel() {
        return gamePanel;
    }

    public Point keyInputToMove() {
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

    public void update() {
        keyManager.update();
        if (level.level != 0) {
            level.update();
            taAnzeige.setText("X:" + level.mover.moverOnMapTile().x + "Y:" + level.mover.moverOnMapTile().y);
            //        level1EnemyMove();
        }
        repaint();
    }


    class GamePanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener {
        public GamePanel() {
            super();
            this.addMouseListener(this);
            this.addMouseMotionListener(this);
            Container container = getContentPane();
            container.setLayout(null);
            this.setBorder(BorderFactory.createLineBorder(Color.black, 4));

        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            switch (level.level) {

                case 999: // MENU
                    level.renderLevel(g2d);
                    break;

                case 0:  // EDITOR
//                    editor.setMenuVisible(true);
//                    editor.renderEditor(g2d);
                    break;

                case 1:  // TUTORIAL
                    level.renderLevel(g2d);
                    break;

                case 2:
                    break;

                case 3:

                    break;
            }
        }

        public void actionPerformed(ActionEvent evt) {

        }

        public void mouseClicked(MouseEvent e) {
            if (level.level != 0){
                level.mouseClicked(e);
            }

            if (level.level == 0) {
                editor.setTileRect(e);
            }
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mouseMoved(MouseEvent e) {
        }

        public void mouseDragged(MouseEvent e) {
            switch (level.level) {
                case 0:
                    editor.setTile(e);
                    break;
            }
        }

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

    class Level {

        private GUI gui;
        public Camera camera;
        public int level;
        public Map[] maps;
        public Mover mover;
        public Runner[] enemy;
        public PathFinder pathFinder;

        public Level(GUI gui) {
            this.gui = gui;
        }

        public void createLevelObject(int mapAmount, int enemyAmount) {
            menuButtons(true, false);
            maps = new Map[mapAmount];
            SpriteSheet playersheet = new SpriteSheet("res/playersheet.png", 4, 3);
            mover = new Mover(gui, 100, 100, 64, 64, playersheet, maps);

            if (enemyAmount > 0) {
                enemy = new Runner[enemyAmount];
            } else {
                System.out.println("Keine Gegner");
            }
        }

        public void createLobby() {
            createLevelObject(1, 0);
            System.out.println("Map Amount . " + maps.length);
            TileSet ts = new TileSet("res/tileSet.png", 12, 12, 3);
            SpriteSheet playersheet = new SpriteSheet("res/playersheet.png", 4, 3);
            //Einlesung der Maps:
            maps[0] = new Map(gui, "res/Menu.txt", ts, "Border", 0);
            camera = new Camera(maps[0].getMapSizeX(), maps[0].getMapSizeY());
            // Intialisierung der Spieler:
            mover = new Mover(gui, 100, 100, 64, 64, playersheet, maps);
            camera.centerOnMover(mover);
            level = 999;
        }

        ////LEVEL0 - Tutorial////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////
        public void createlevel1() {
            createLevelObject(2, 5);

            TileSet ts = new TileSet("res/tileSet.png", 12, 12, 3);
            TileSet tsItems = new TileSet("res/tileSetB.png", 16, 16, 0);
            SpriteSheet playersheet = new SpriteSheet("res/playersheet.png", 4, 3);
            //Einlesung der Maps:
            maps[0] = new Map(gui, "res/Chapter0.txt", ts, "All", 0);
            maps[1] = new Map(gui, "res/0-1Items.txt", tsItems, "Item", 24);
            camera = gui.getCamera();
            camera = new Camera(maps[0].getMapSizeX(), maps[0].getMapSizeY());
            // Intialisierung der Spieler:
            mover = new Mover(gui, (int) mover.getLocation().getX(), (int) mover.getLocation().getY(), 64, 64, playersheet, maps);
            camera.centerOnMover(mover);
            for (int i = 0; i < enemy.length; i++) {
                enemy[i] = new Runner(gui, (2) * 64, (5) * 64, 64, 64, playersheet, maps);
            }
            enemy[1] = new Runner(gui, (16) * 64, (5) * 64, 64, 64, playersheet, maps);
            enemy[2] = new Runner(gui, (13) * 64, (6) * 64, 64, 64, playersheet, maps);

            pathFinder = new PathFinder(gui, maps, mover);
            level = 1;
        }

        public void level1EnemyMove() {
            enemy[2].movetotarget(mover);
            enemy[0].enemystraightrun(64, 5, 1, 0);
        }
        //////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////

        public void renderLevel(Graphics2D g2d) {
            maps[0].renderMap(g2d);
            mover.draw(g2d);

            for (int enemyamount = 0; enemyamount < enemy.length; enemyamount++) {
                enemy[enemyamount].draw(g2d);
            }

            for (int mapsAmount = 1; mapsAmount < maps.length; mapsAmount++) {
                //IF Mover is on Map
                maps[mapsAmount].renderMap(g2d);
            }

        }

        public void update() {
            if ( keyInputToMove().getX() != 0||keyInputToMove().getY() != 0) {
                mover.setMove(keyInputToMove());
                pathFinder.resetPath();
            } // end of if
            Point2D from = pathFinder.getNextStep();
            Point2D to = pathFinder.getNextStep();
            if ( to != null && keyInputToMove().getX() == 0 && keyInputToMove().getY() == 0) {
                mover.setPathMove(from,to);
            } // end of if
        }

        public void menuButtons(boolean menu, boolean editormenu) {
            for (int i = 0; i < gui.buttons.length; i++) {
                buttons[i].setVisible(menu);
            }
            if (editor != null) {
                gui.editor.setMenuVisible(editormenu);
            }
        }

        public void mouseClicked(MouseEvent e) {
            if (level != 0) {
                for (int i = 0; i < maps.length; i++) {
                    if (maps[i].isActiveInPosition(new Point(e.getX() + camera.xOffset, e.getY() + camera.yOffset))) {
                        System.out.println(i);
                        start = maps[i].mapTiles[(int) mover.getLocation().getX() / 64][(int) mover.getLocation().getY() / 64];
                        target = maps[i].mapTiles[(e.getX() + camera.getXOffset()) / maps[i].tileWidth][(e.getY() + camera.getYOffset()) / maps[i].tileHeight];
                        pathFinder.searchPath(start, target);
                    } else {continue;}}
            }
        }
    }
}