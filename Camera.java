 import java.awt.*;

 public class Camera{
  
  int xSize, ySize;
  int xOffset = 0 ;
  int yOffset = 0;
  
  public Camera(int pXSize, int pYSize){
   xSize = pXSize;
   ySize = pYSize;
  }
  public void centerOnMover(Mover pMover){
    xOffset = (int) pMover.getLocation().getX() - (GUI.FRAME_WIDTH/2);
    
//    System.out.println("XKord:"+pMover.getLocation().getX() +"XOFF:"+xOffset);
    if (xOffset < 0) {
      xOffset = 0;
    }else {
      int t = xSize * Tile.TILEWIDTH - GUI.FRAME_WIDTH + 80;
      if (xOffset > t) {
        xOffset = t; 
      } // end of if
     } // end of if-else
    
    yOffset = (int) pMover.getLocation().getY() - (GUI.FRAME_HEIGHT/2);
//    System.out.println("YKord:"+pMover.getLocation().getY()+"YOFF:"+ yOffset);
    if (yOffset < 0) {
      yOffset = 0;
    }else {
      int t = ySize * Tile.TILEHEIGHT - GUI.FRAME_HEIGHT - 80;
      if (yOffset > t) {
        yOffset = t; 
     } // end of if                      
     } // end of if-else
    }  
  public int getXOffset() {
    return xOffset;
 }
  public int getYOffset() {
    return yOffset;
  }
  
}
 
