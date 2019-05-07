import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyVetoException;
import java.util.LinkedList;

class GamePanel extends JInternalFrame {

    /**
     * Viewer des Spiels
     */
    private Camera camera;
    private JPanel display;
    public Tile start;
    public Tile target;
    private LinkedList renderList;


    public GamePanel(MouseListener mouseListener, MouseMotionListener mouseMotionListener, KeyManager keyManager) {
        super("GUI.GamePanel", true, true, true, true);
//        super(new BorderLayout());
        display = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                render(g);
            }
        };
        getContentPane().add(display);
        setName("GUI.GamePanel");
        renderList = new LinkedList();

        addKeyListener(keyManager);
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseMotionListener);

        setFocusable(false);
        setBackground(Color.green);
        setOpaque(false);
        ((BasicInternalFrameUI) getUI()).setNorthPane(null);
        setBorder(null);
        try {
            setMaximum(false);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
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


    private void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        for (int i = 0; i < renderList.size(); i++) {
            ((GUI) renderList.get(i)).render(g2d);
        }
    }

    public void setCamera(int pXSize, int pYSize, Point chapterOffset) {
        camera = new Camera(pXSize, pYSize, (int) chapterOffset.getX(), (int) chapterOffset.getY());
    }


}
