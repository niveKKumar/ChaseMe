import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class GamePanel extends JInternalFrame implements ComponentListener {
    public Camera camera;
    public Tile start;
    public Tile target;
    public KeyManager keyManager;
    /**
     * Viewer des Spiels
     */
    private GUI gui;

    public GamePanel(JFrame frame) {
        super("GamePanel", false, false, false, false);
        setBorder(null);
        setVisible(true);
        frame.add(this);

    }

    public GamePanel(GUI pGUI) {
        super("GamePanel", false, false, false, false);
        this.gui = gui;
        System.out.println("GamePanels Groesse 1: " + getSize());
        pGUI.add(this, BorderLayout.CENTER);
        System.out.println("GamePanels Groesse 2: " + getSize());

        setBackground(Color.green);
        keyManager = new KeyManager();
        this.addKeyListener(keyManager);
        this.addMouseListener(pGUI);
        this.addMouseMotionListener(pGUI);
        camera = new Camera();
        GUI.GAMEPANEL_WIDTH = getWidth();
        GUI.GAMEPANEL_HEIGHT = getHeight();
        gui = pGUI;
        requestFocus();
        ((BasicInternalFrameUI) getUI()).setNorthPane(null);
//        setBorder(null);
        setVisible(true);
        System.out.println("GamePanels Groesse 3: " + getSize());
    }

    public Camera getCamera() {
        return camera;
    }

    public GUI getGUI() {
        return gui;
    }

    public void setCamera(int pXSize, int pYSize) {
        camera = new Camera(pXSize, pYSize);
    }

    @Override
    public void componentResized(ComponentEvent e) {
//        gui.menuUI.setSize(getSize());
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

    @Override
    protected void paintChildren(Graphics g) {
        super.paintChildren(g);
        Graphics2D g2d = (Graphics2D) g;
        if (gui.level.isActive()) {
            gui.level.renderLevel(g2d);
        } else {
            gui.editor.renderEditor(g2d);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        if (gui.level.isActive()) {
            gui.level.renderLevel(g2d);
        } else {
            gui.editor.renderEditor(g2d);
        }
    }
}
