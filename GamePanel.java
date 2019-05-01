import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;

class GamePanel extends JPanel {

    /**
     * Viewer des Spiels
     */
    private Camera camera;
    public Tile start;
    public Tile target;
    private LinkedList renderList;


    public GamePanel(JPanel display, MouseListener mouseListener, MouseMotionListener mouseMotionListener, KeyManager keyManager) {
//        super("GUI.GamePanel", true, true, true, true);
        super(new BorderLayout());
        setName("GUI.GamePanel");
        renderList = new LinkedList();
        display.add(this, BorderLayout.CENTER);

        addKeyListener(keyManager);
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseMotionListener);

        setFocusable(false);
        setBackground(Color.green);
        setOpaque(false);
//        ((BasicInternalFrameUI) getUI()).setNorthPane(null);
        setBorder(null);
//        try {
//            setMaximum(false);
//        } catch (PropertyVetoException e) {
//            e.printStackTrace();
//        }
        repaint();
        revalidate();
        setVisible(true);
    }

    public Camera getCamera() {
        return camera;
    }

    public void addtoRender(GUI gui) {
        if (!renderList.contains(gui)) {
            renderList.add(gui);
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        for (int i = 0; i < renderList.size(); i++) {
            ((GUI) renderList.get(i)).render(g2d);
        }
    }

    public void setCamera(int pXSize, int pYSize, Point chapterOffset) {
        camera = new Camera(pXSize, pYSize, (int) chapterOffset.getX(), (int) chapterOffset.getY());
    }


}
