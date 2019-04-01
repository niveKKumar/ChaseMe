import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.*;
import java.io.File;

public class GUI extends JFrame implements ActionListener {
  private GamePanel gamePanel = new GamePanel();
  public JPanel south = new JPanel(new FlowLayout());
  private JPanel north = new JPanel(new FlowLayout());
  public JPanel east = new JPanel(new FlowLayout());
  public JPanel west = new JPanel(new FlowLayout());
  public JButton[] buttons = new JButton[6];
  public JTextArea taAnzeige = new JTextArea();
  

  private Loop loop = new Loop();
  private Thread t = new Thread(loop);
  public static final int FPS = 60; //(Bilder pro Sekunde)
  public static final long maxLoopTime = 1000 / FPS;
  public static final int FRAME_WIDTH = 900;
  public static final int FRAME_HEIGHT = 850;
  public Container cp;
  private KeyManager keyManager;
  private DisplayAnalytics[] analytics;
  public Tile start;
  public Tile target;
  public Camera camera;
  private Level level;
  private Editor editor;
  private Meldungen temp;


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
    keyManager = new KeyManager();
    level = new Level(this);
    camera = new Camera();
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
    east.setPreferredSize(new Dimension(150,750));
    cp.add(west, BorderLayout.WEST);
    cp.add(north, BorderLayout.NORTH);
    east.add(taAnzeige);
    taAnzeige.setBorder(BorderFactory.createLineBorder(Color.black));
    taAnzeige.setMaximumSize(new Dimension(50,750));
    String[] btNamesMenu = {"Exit","Neustart", "gridLines", "Tutorial", "Editor","TestButton"};
    for (int i = 0; i < buttons.length; i++) {
      buttons[i] = new JButton(btNamesMenu[i]);
      buttons[i].addActionListener(this);
      south.add(buttons[i]);
    }
    
  }
  
  private void createMenu() {
    level.createLobby();
    t.start();
  }
  
  private void createEditor() {
    temp = new Meldungen(this,true,"Map");
    TileSet ts = new TileSet("res/tileSet.png", 12, 12, 3);
    SpriteSheet playersheet = new SpriteSheet("res/playersheet.png", 4, 3);
    System.out.println(temp.getUserInput(1));
    int sizeX = Integer.parseInt(temp.getUserInput(0)),sizeY = Integer.parseInt(temp.getUserInput(1));
    editor = new Editor(this, sizeX,sizeY , keyManager);
    level.setLevel(0);
  }
  
  
  public void actionPerformed(ActionEvent evt) {
    JButton temp = (JButton) evt.getSource();
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
        this.requestFocus();
        break;
      case "Tutorial":
        level.createlevel1();
        this.requestFocus();
        break;
        
      case "Editor":
        createEditor();
        this.requestFocus();
        break;

      case "Neustart":
        dispose();
        GUI gui = new GUI();
        break;
      case "TestButton":

        break;
        ///EDITOR Buttons:
      case "+":
        System.out.println("Zoom rein");
        editor.zoom(true);
        camera.centerOnObject(editor.cameraPoint.getLocation());
        this.requestFocus();
        break;
        
      case "-":
        System.out.println("Zoom raus");
        editor.zoom(false);
        camera.centerOnObject(editor.cameraPoint.getLocation());
        this.requestFocus();
        break;
      case "Tile Auswaehlen":
        editor.createTileMenu();
        this.requestFocus();
        break;
        
    }
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
    //        System.out.println("Level Status: " + level.level);
    if (level.level != 0) {
      level.updateLevel();
    }else { //Editor:
      if ( keyInputToMove().getX() != 0||keyInputToMove().getY() != 0) {
        editor.cameraPoint.setMove(keyInputToMove());
          System.out.println(keyInputToMove());}
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
      
    }

    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;
      switch (level.level) {
        
        case 999: // MENU
          level.renderLevel(g2d);
          break;
          
        case 0:  // EDITOR
          editor.setMenuVisible(true);
//          System.out.println("editor");
          editor.renderEditor(g2d);
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
      }else{
        editor.mouseClicked(e);
      }

      JComponent comp = (JComponent) e.getSource();
      comp.requestFocusInWindow();

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
      if (level.level == 0){editor.mouseDragged(e);}
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
    public int level;
    public Map[] maps;
    public Mover mover;
    public Runner[] enemy;
    public PathFinder pathFinder;
    public DisplayAnalytics[] analytics;
    public int click = 0;
    boolean[] dialog;

    public Level(GUI gui) {
      this.gui = gui;
    }

    public void createLevelObject(int mapAmount, int enemyAmount) {
      menuButtons(true, false);
      maps = new Map[mapAmount];
      analytics = new DisplayAnalytics[mapAmount];
      SpriteSheet playersheet = new SpriteSheet("res/playersheet.png", 4, 3);
      mover = new Mover(gui, 100, 100, 64, 64, playersheet, maps);
      enemy = new Runner[enemyAmount];
    }

    public void createLobby() {
      createLevelObject(1, 0);
//      System.out.println("Map Amount . " + maps.length);
      TileSet ts = new TileSet("res/tileSet.png", 12, 12, 3);
      SpriteSheet playersheet = new SpriteSheet("res/playersheet.png", 4, 3);
      //Einlesung der Maps:
      maps[0] = new Map(gui, "res/Menu.txt", ts, "Border", 0);
      gui.setCamera(maps[0].getMapSizeX(), maps[0].getMapSizeY());
      // Intialisierung der Spieler:
      mover = new Mover(gui, 100, 100, 64, 64, playersheet, maps);
      camera.centerOnObject(mover.getLocation());
      pathFinder = new PathFinder(maps, mover,gui);

      for (int i = 0; i < maps.length ; i++) {
        analytics[i] = new DisplayAnalytics(gui,maps[i],pathFinder);
      }
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
      gui.setCamera(maps[0].getMapSizeX(), maps[0].getMapSizeY());
      // Intialisierung der Spieler:
      mover = new Mover(gui, (int) mover.getLocation().getX(), (int) mover.getLocation().getY(), 64, 64, playersheet, maps);
      camera.centerOnObject(mover.getLocation());
      for (int i = 0; i < enemy.length; i++) {
        enemy[i] = new Runner(gui, (2) * 64, (5) * 64, 64, 64, playersheet, maps);
      }
      enemy[1] = new Runner(gui, (16) * 64, (5) * 64, 64, 64, playersheet, maps);
      enemy[2] = new Runner(gui, (13) * 64, (6) * 64, 64, 64, playersheet, maps);
      
      pathFinder = new PathFinder(maps, mover,gui);
      dialog = new boolean[2];
      for (int i = 0; i < dialog.length ; i++) {
        dialog[i] = false;
      }
      dialog[0] = true; // erste Sprechblase

      for (int i = 0; i < maps.length ; i++) {
        analytics[i] = new DisplayAnalytics(gui,maps[i],pathFinder);
      }
      level = 1;
    }

    public void level1GameMechanic() {
//      enemy[2].movetotarget(mover);
      System.out.println(click);

      enemy[0].enemystraightrun(64, mover.speed/2, 0,1);
      // TODO: 26.03.2019 Eigene Game Dialog Klasse/ Textdatei für Level
      if (dialog[0] && mover.isOnThisMap(5,5) ){
        mover.saySomething("Wo bin ich?"+"\n"+ "(Klicken für weiteren Dialog)",true);
        dialog[0] = false;
        dialog[1] = true;
        click = 0;
      }
      if(dialog[1] && click == 1){
        mover.saySomething("W-A-S-D drücken um sich zu bewegen",true);}
      dialog[1] = false;
    }


    public void renderLevel(Graphics2D g2d) {
      maps[0].renderMap(g2d);
      mover.draw(g2d);
      
      if (enemy.length >0) {
        for (int enemyamount = 0; enemyamount < enemy.length; enemyamount++) {
          enemy[enemyamount].draw(g2d);
        }}

      
      for (int mapsAmount = 1; mapsAmount < maps.length; mapsAmount++) {
        maps[mapsAmount].renderMap(g2d);
      }
      for (int analyticsamount = 0; analyticsamount < analytics.length; analyticsamount++) {
        analytics[analyticsamount].renderAnalytics(g2d);
      }
      
    }

    public void updateLevel() {
      if (keyInputToMove().getX() != 0||keyInputToMove().getY() != 0) {
        mover.setMove(keyInputToMove());
        pathFinder.resetPath();
      } // end of if
      Point2D from = pathFinder.getNextStep();
      Point2D to = pathFinder.getNextStep();
      if (mover.mayIMove && to != null && keyInputToMove().getX() == 0 && keyInputToMove().getY() == 0) {
        mover.setPathMove(from,to);
      } // end of if

      //////////////////////////////////////////////////////////////////////////////////
      //////////////////////////////////////////////////////////////////////////////////
      //////////////////////////////////////////////////////////////////////////////////

      switch (level){
        case 1 :
          level1GameMechanic();
          taAnzeige.setText("X:" + mover.moverOnMapTile().x + "Y:" + mover.moverOnMapTile().y);
          break;
      }
      camera.centerOnObject(mover.getLocation());
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
            start = maps[i].mapTiles[(int) (mover.getLocation().getX() / Tile.TILEWIDTH)][(int) mover.getLocation().getY() / Tile.TILEHEIGHT];
            target = maps[i].mapTiles[(e.getX() + camera.getXOffset()) /  Tile.TILEWIDTH ][(e.getY() + camera.getYOffset()) / Tile.TILEHEIGHT];
            pathFinder.searchPath(start, target);
            click++;
          } else {continue;}}
      }
    }

    public void setLevel(int level) {
      this.level = level;
    }
  }
}
