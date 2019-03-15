import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.*;

public class GUI extends JFrame implements ActionListener{
  private GamePanel gamePanel = new GamePanel();
  public JPanel south = new JPanel(new FlowLayout());
  private JPanel north = new JPanel(new FlowLayout());
  public JPanel east = new JPanel(new FlowLayout());
  private JPanel west = new JPanel(new FlowLayout());
  private JButton [] buttons = new JButton[4];
  private JTextArea taAnzeige = new JTextArea();


  private JLabel lbTitel = new JLabel();
  private Loop loop = new Loop();
  private Thread t = new Thread(loop);
  public static final int FPS = 60; //(Bilder pro Sekunde)
  public static final long maxLoopTime = 1000/FPS;
  public static final int FRAME_WIDTH = 900;
  public static final int FRAME_HEIGHT = 900;
  public Container cp;
  private Map[] maps;
  private Mover mover;
  private Runner[] level1enemy;
  private KeyManager keyManager;
  private PathFinder pathfinder;
  private DisplayAnalytics[] analytics;
  public Tile start;
  public Tile target;
  public Camera camera ;
  private int level;; //Erhoehung = höheres Level
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
    
    keyManager = new KeyManager();
    
    this.addKeyListener(keyManager);
    this.setFocusable(true);
    this.requestFocus();
    createGUI();
    createMenu();
    setVisible(true);
  }
  
  private void createGUI(){
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
    String btNamesMenu [] = {"Exit", "gridLines", "Tutorial", "Editor"} ;

    for (int i =0;i<buttons.length;i++) {
      buttons[i] = new JButton(btNamesMenu[i]);
      buttons[i].addActionListener(this);
      south.add(buttons[i]);
    }
  }
  
  private void createMenu(){
    menuButtonsVisible(true);
    maps = new Map[1];
    analytics = new DisplayAnalytics[1];
    TileSet ts = new TileSet("res/tileSet.png", 12,12,3);
    SpriteSheet playersheet = new SpriteSheet("res/playersheet.png",4,3);
    
    maps[0] = new Map(this,"res/Menu.txt", ts, "Border",0);
    Map mapAnlaytics = maps[0];
    analytics[0] = new DisplayAnalytics(this,mapAnlaytics,pathfinder);

    mover = new Mover(this, 100,100,64,64,playersheet,maps);
    camera = new Camera(10,10);
    pathfinder = new PathFinder(maps,mover,this);
    level = 999;
    t.start();
  }
  private void createEditor(){
    EditorAbfrage temp = new EditorAbfrage(this,true);
    TileSet ts = new TileSet("res/tileSet.png", 12,12,3);
    SpriteSheet playersheet = new SpriteSheet("res/playersheet.png",4,3);
    editor = new Editor(this,temp.getMapSizeX(),temp.getMapSizeY(),ts);

    level = 0;
  }
  public void menuButtonsVisible(boolean b){
    for (int i =0;i<buttons.length;i++) {
      buttons[i].setVisible(b);
    }
  }
    ////LEVEL0 - Tutorial////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////
  public void createlevel1() {
    //Erstellung der Objekte :
    maps = new Map[2];
    analytics = new DisplayAnalytics[2];
    int anzahl = 5;
    level1enemy = new Runner[anzahl];
    TileSet ts = new TileSet("res/tileSet.png", 12,12,3);
    TileSet tsItems = new TileSet("res/tileSetB.png", 16,16,0);
    SpriteSheet playersheet = new SpriteSheet("res/playersheet.png",4,3);
//    SpriteSheet chiefsheet = new SpriteSheet("res/chiefsheet.png",4,1);
    
    maps[0] = new Map(this,"res/Chapter0.txt", ts, "All",0);
    maps[1] = new Map(this, "res/0-1Items.txt", tsItems,"Item",24);
    //TEMP------------------------------------------------------------------------
    Map mapAnalytics = maps[0];
    Map mapAnalytics1 = maps[1];
    analytics[0] = new DisplayAnalytics(this,mapAnalytics,pathfinder);
    analytics[1] = new DisplayAnalytics(this,mapAnalytics1,pathfinder);
    //-----------------------------------------------------------------------------
    
    mover = new Mover(this, (int) mover.getLocation().getX(),(int) mover.getLocation().getY(),64,64,playersheet,maps);
    pathfinder = new PathFinder(maps,mover,this);
    for (int i = 0; i < anzahl; i++) {
      level1enemy[i] = new Runner(this,(2)*64,(5)*64,64,64,playersheet,maps);
    }
    level1enemy[1] = new Runner(this,(16)*64,(5)*64,64,64,playersheet,maps);
    level1enemy[2] = new Runner(this,(13)*64,(6)*64,64,64,playersheet,maps);
    camera = new Camera(50,50);
    level = 1;
  }
  public void level1EnemyMove(){
//    level1enemy[2].movetotarget(mover);
    if ( keyInputToMove().getX() != 0||keyInputToMove().getY() != 0) {
//      level1enemy[0].enemystraightrun(64,5,1,0);
    }
  }
  //////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////
  
  
  public void actionPerformed(ActionEvent evt){
    JButton temp = (JButton) evt.getSource();
    switch (temp.getText()) {
      case "Exit" :
        System.exit(0);
        this.requestFocus();
        break;
      case "gridLines" :
        for (int i = 0;i <analytics.length ;i++ ) {
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
            analytics[i].setMover(mover);
            analytics[i].moverHitbox = true;
            this.requestFocus();
          } // end of if-else
        } // end of for
        this.requestFocus();
        break;
      case "Tutorial":
        createlevel1();
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
//      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
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

  public GamePanel getSpielpanel(){
    return gamePanel;
  }
  private Point keyInputToMove(){
    int xMove = 0;
    int yMove = 0;
    if (keyManager.up ) yMove = -1;
    if (keyManager.down) yMove = 1;
    if (keyManager.left) xMove = -1;
    if (keyManager.right) xMove = 1;
    if (keyManager.upLeft){
      yMove = -1;
      xMove = -1;}
    if (keyManager.upRight){
      yMove = -1;
      xMove = 1;}
    if (keyManager.downLeft){
      yMove = 1;
      xMove = -1;}
    if (keyManager.downRight){
      yMove = 1;
      xMove = 1;}
    return new Point(xMove,yMove);
  }
  public void update(){
    keyManager.update();

    if ( keyInputToMove().getX() != 0||keyInputToMove().getY() != 0) {
      mover.setMove(keyInputToMove());
      pathfinder.resetPath();
    } // end of if
    Point2D from = pathfinder.getNextStep();
    Point2D to = pathfinder.getNextStep();
    if ( to != null && keyInputToMove().getX() == 0 && keyInputToMove().getY() == 0) {
      mover.setPathMove(from,to);
    } // end of if

    taAnzeige.setText("X:"+mover.moverOnMapTile().x+"Y:"+mover.moverOnMapTile().y);
    switch (level){
      case 1:
//        level1EnemyMove();
        break;
    }
    repaint();
  }
  class GamePanel extends JPanel implements MouseListener,MouseMotionListener,ActionListener {
    Point firstClick;
    Point secondClick;
    boolean shift;
    int click;
    public GamePanel(){
      super();
      this.addMouseListener(this);
      this.addMouseMotionListener(this);
      Container container = getContentPane();
      container.setLayout(null);
      this.setBorder(BorderFactory.createLineBorder(Color.black, 4));
      
    }

    public void paintComponent(Graphics g){
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;
      
      switch (level){
        
        case 999: // MENU
          menuButtonsVisible(true);
//          editor.setMenuVisible(false);
          maps[0].renderMap(g2d);
          analytics[0].renderAnalytics(g);
          mover.draw(g2d);
          break;
          
        case 0:  // EDITOR
          menuButtonsVisible(true);
//          editor.setMenuVisible(true);
          editor.renderEditor(g2d);
          mover.draw(g2d);
          break;
          
        case 1:  // TUTORIAL
          menuButtonsVisible(true);
          editor.setMenuVisible(false);
          // TODO: 13.03.2019 Level Klasse --> Obriges als eigene Methode 
          maps[0].renderMap(g2d);
          mover.draw(g2d);
          maps[1].renderMap(g2d);
          analytics[1].renderAnalytics(g2d);
          for (int i = 0; i < level1enemy.length ; i++) {
            level1enemy[i].draw(g2d);
          }
          break;
          
        case 2:
          menuButtonsVisible(true);
          editor.setMenuVisible(false);
          break;
          
        case 3:
          
      break;}
    }
    public void actionPerformed(ActionEvent evt){

    }
    public void mouseClicked(MouseEvent e) {
        if (level != 0) {
        for (int i = 0; i < maps.length; i++) {
          if (maps[i].isActiveInPosition(new Point(e.getX()+camera.xOffset,e.getY()+camera.yOffset))){
            System.out.println(i);
            start = maps[i].mapTiles[(int) mover.getLocation().getX() / 64][(int) mover.getLocation().getY() / 64];
            target = maps[i].mapTiles[(e.getX() + camera.getXOffset()) / maps[i].tileWidth][(e.getY() + camera.getYOffset()) / maps[i].tileHeight];
            pathfinder.searchPath(start,target);
          }else{continue;}

//          try {
//            pathfinder.searchPath(start,target);
//          } catch (Exception e1) {
//            System.out.println("Start oder Target sind faul");
//          }
        }
      }
      // TODO: 13.03.2019 Feature von Editor nicht von GUI 
      if (level == 0) {
        shift = keyManager.shift;
        click++;
        if (shift) {
          if (click == 1) {
            firstClick = new Point(e.getX() / editor.tilewidth, e.getY() / editor.tilewidth);}
          if (click == 2) {
            secondClick = new Point(e.getX() / editor.tilewidth, e.getY() / editor.tilewidth);
            editor.setTileRect(firstClick, secondClick);
            click = 0;}
        } else {
          click = 0;
        }
      }
  }
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}

      public void mouseDragged(MouseEvent e){
        switch (level){
            case 0:
                Point clickedTile = new Point(e.getX()/64,e.getY() /64);
                editor.setTile(clickedTile);
                break;
        }
    }

  }
  class Loop implements Runnable{
    long timestamp = 0;
    long oldTimestamp = 0;
    public void run(){
      while (true) {
        oldTimestamp = System.currentTimeMillis();
        update();
        timestamp = System.currentTimeMillis();
        
        if ((timestamp - oldTimestamp) > maxLoopTime){
          System.out.println("Zu langsam");
          continue;
        }
        
        try{
          Thread.sleep(maxLoopTime - (timestamp - oldTimestamp));
        }catch(Exception e){
          System.out.println("Loopfehler: " + maxLoopTime); // - (timestamp - oldTimestamp)));
        }
      }
    }
  }
}    
