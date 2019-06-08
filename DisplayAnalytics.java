import java.awt.*;

public class DisplayAnalytics{
  boolean active;
  public boolean showGridLines = false;
  public boolean showTileIndices = false;
  public boolean displayBlockedTiles = false;
  public boolean moverHitbox = false;
  private Mover mover;
  private boolean isMover;
    private MapBase map;
    private GamePanel gamePanel;

    public DisplayAnalytics(GamePanel gp, MapBase pMap) {
        gamePanel = gp;
        map = pMap;
  }
  public void setMover(Mover pMover){
  mover = pMover;
  isMover = true;
  }

    public void renderAnalytics(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;
        BasicStroke stroke1 = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
        BasicStroke stroke2 = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
    g2d.setStroke(stroke1);
    try {
      if (moverHitbox && isMover) {
        g2d.setStroke(stroke2);
        g2d.drawLine(mover.cPoints[0].x - gamePanel.getCamera().getXOffset(), mover.cPoints[0].y - gamePanel.getCamera().getYOffset(), mover.cPoints[3].x - gamePanel.getCamera().getXOffset(), mover.cPoints[3].y - gamePanel.getCamera().getYOffset());
        g2d.drawLine(mover.cPoints[3].x - gamePanel.getCamera().getXOffset(), mover.cPoints[3].y - gamePanel.getCamera().getYOffset(), mover.cPoints[1].x - gamePanel.getCamera().getXOffset(), mover.cPoints[1].y - gamePanel.getCamera().getYOffset());
        g2d.drawLine(mover.cPoints[1].x - gamePanel.getCamera().getXOffset(), mover.cPoints[1].y - gamePanel.getCamera().getYOffset(), mover.cPoints[2].x - gamePanel.getCamera().getXOffset(), mover.cPoints[2].y - gamePanel.getCamera().getYOffset());
        g2d.drawLine(mover.cPoints[2].x - gamePanel.getCamera().getXOffset(), mover.cPoints[2].y - gamePanel.getCamera().getYOffset(), mover.cPoints[0].x - gamePanel.getCamera().getXOffset(), mover.cPoints[0].y - gamePanel.getCamera().getYOffset());
      }
    } catch (Exception e) {
    }
    }

    public void setActive(boolean active) {
    if (active) {
      showGridLines = true;
      showTileIndices = true;
      displayBlockedTiles = true;
      moverHitbox = true;
      this.active = true;
    } else {
      showGridLines = false;
      showGridLines = false;
      showTileIndices = false;
      displayBlockedTiles = false;
      moverHitbox = false;
      this.active = false;
    }
  }
}
 
