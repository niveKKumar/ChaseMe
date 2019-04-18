import javax.swing.FocusManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GUI extends JFrame implements ActionListener {
    public static final int FRAME_WIDTH = 800;
    public static final int FRAME_HEIGHT = 800;
    public JPanel south = new JPanel(new FlowLayout());
    private JPanel north = new JPanel(new FlowLayout());
    public JPanel east = new JPanel(new FlowLayout());
    public JPanel west = new JPanel(new FlowLayout());
    public KeyManager keyManager;
    public JButton[] buttons = new JButton[6];
    public JTextArea taAnzeige = new JTextArea();
    public Tile target;

    private Loop loop = new Loop();
    private Thread t = new Thread(loop);
    public static final int FPS = 60; //(Bilder pro Sekunde)
    public static final long maxLoopTime = 1000 / FPS;
    public Tile start;
    public Container cp;
    private DisplayAnalytics[] analytics;
    private JLabel focuslb = new JLabel();
    private GamePanel gamePanel = new GamePanel();
    public Camera camera;
    public Level level;
    private Editor editor;


    public GUI() {
        super();

        keyManager = new KeyManager();
        this.addKeyListener(keyManager);
        this.addMouseListener(gamePanel);
        this.addMouseMotionListener(gamePanel);
        this.setBackground(Color.white);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (d.width - getSize().width) / 2;
        int y = (d.height - getSize().height) / 2;
        y = -60;
        setLocation(x, y);
        setTitle("Das tolle Spiel");
        setResizable(false);
        cp = getContentPane();
        level = new Level(this);
        camera = new Camera();
        south.add(focuslb);
        createGUI();
        createMenu();
        setVisible(true);
    }

    public void actionPerformed(ActionEvent evt) {
        JButton temp = (JButton) evt.getSource();
        if (level.level == 0) {
            System.out.println("Editor ActionPerf");
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

        }
        System.out.println(level.level);
    }


    private void createGUI() {
        cp.setLayout(new BorderLayout());
        cp.add(gamePanel, BorderLayout.CENTER);
        cp.add(south, BorderLayout.SOUTH);
        cp.add(east, BorderLayout.EAST);
        east.setPreferredSize(new Dimension(150, 750));
        cp.add(west, BorderLayout.WEST);
        cp.add(north, BorderLayout.NORTH);
        taAnzeige.setFocusable(false);
        taAnzeige.setBorder(BorderFactory.createLineBorder(Color.black));
        taAnzeige.setMinimumSize(new Dimension(east.getWidth(), 50));
        east.add(taAnzeige);
        String[] btNamesMenu = {"Exit", "Neustart", "gridLines", "Tutorial", "Editor", "TestButton"};
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new JButton(btNamesMenu[i]);
            buttons[i].setFocusable(false);
            buttons[i].addActionListener(this);
            south.add(buttons[i]);
        }
        gamePanel.requestFocus();

    }

    private void createMenu() {
        level.createLobby();
        t.start();
    }

    private void createEditor() {
        Meldungen temp = new Meldungen(this, true, "Map");
        System.out.println(temp.getUserInput(1));
        int sizeX = Integer.parseInt(temp.getUserInput(0)), sizeY = Integer.parseInt(temp.getUserInput(1));
        editor = new Editor(this, sizeX, sizeY, keyManager);
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

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(int pXSize, int pYSize) {
        camera = new Camera(pXSize, pYSize);
    }

    public void update() {
        keyManager.update();
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

    class GamePanel extends JPanel implements MouseListener, MouseMotionListener {

        public GamePanel() {
            super();
            this.addMouseListener(this);
            this.addMouseMotionListener(this);
            Container container = getContentPane();
            container.setLayout(null);

        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (level.level != 0){
                level.mouseClicked(e);
            }else{
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

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            if (level.isActive()) {
                level.renderLevel(g2d);
            } else {
                editor.setMenuVisible(true);
                editor.renderEditor(g2d);
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

}
