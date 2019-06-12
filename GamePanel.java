import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;

class GamePanel extends /*JInternalFrame*/ JPanel {

    /**
     * Display des Spiels
     */
    private Camera camera;
    private JPanel display;
    private LinkedList renderList;


    public GamePanel(Container cp, MouseListener mouseListener, MouseMotionListener mouseMotionListener, KeyManager keyManager) {
//        super("GUI.GamePanel", true, true, true, true);
        super(new BorderLayout());
//        display = new JPanel(new BorderLayout(), true) {
//            @Override
//            protected void paintComponent(Graphics g) {
//                super.paintComponent(g);
//                render(g);
//                revalidate();
//                repaint();
//            }
//        };

//        getContentPane().add(display);
        setName("GUI.GamePanel");
        renderList = new LinkedList();

//        display.addKeyListener(keyManager);
//        display.addMouseListener(mouseListener);
//        display.addMouseMotionListener(mouseMotionListener);
        addKeyListener(keyManager);
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseMotionListener);

        cp.add(this);
        setFocusable(false);
        setBackground(Color.green);
        setOpaque(false);
//        ((BasicInternalFrameUI) getUI()).setNorthPane(null);
        setBorder(null);
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


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        render(g);
        revalidate();
        repaint();
    }

}
