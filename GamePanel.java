import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import java.awt.*;
import java.beans.PropertyVetoException;

public class GamePanel extends JInternalFrame {

    /**
     * Viewer des Spiels
     */

    public static int GAMEPANEL_WIDTH = 500;
    public static int GAMEPANEL_HEIGHT = 500;

    private Camera camera;
    public Tile start;
    public Tile target;

    private GUI gui;

    private Level level;
    private Editor editor;


    public GamePanel(JPanel display, GUI gui) {
        super("GamePanel", true, true, true, true);
        this.gui = gui;
        camera = new Camera();
        setName("GamePanel");

//        System.out.println("GamePanels Groesse 1: " + getSize());
//        System.out.println("GamePanels Bounds 1: " + getBounds());
        display.add(this, BorderLayout.CENTER);
        GAMEPANEL_HEIGHT = display.getHeight();
        GAMEPANEL_WIDTH = display.getWidth();
        setSize(GAMEPANEL_WIDTH, GAMEPANEL_HEIGHT);
//        System.out.println("GamePanels Groesse 2: " + getSize());
//        System.out.println("GamePanels Bounds 2: " + getBounds());

//        setFocusable(false);
        setBackground(Color.green);
        addMouseListener(gui);
        addMouseMotionListener(gui);
        ((BasicInternalFrameUI) getUI()).setNorthPane(null);
        setBorder(null);
        try {
            setMaximum(true);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
//        System.out.println("GamePanels Groesse 3: " + getSize());
//        System.out.println("GamePanels Bounds 3: " + getBounds());
        repaint();
        revalidate();
        setVisible(true);
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
    protected void paintChildren(Graphics g) {
        super.paintChildren(g);
        Graphics2D g2d = (Graphics2D) g;
        gui.render(g2d);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        gui.render(g2d);
    }
}
