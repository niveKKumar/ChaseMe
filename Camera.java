 import java.awt.*;

 public class Camera{
  
  int xSize, ySize;
  int xOffset = 0 ;
  int yOffset = 0;
     int targetX, targetY;
//  Pointer pointer;
  
  public Camera(){
  }
  
  public Camera(int pXSize, int pYSize){
   xSize = pXSize;
   ySize = pYSize;
  }
  public void centerOnObject(Point location){
      xOffset = (int) location.getX() - (GUI.FRAME_WIDTH/2);
      yOffset = (int) location.getY() - (GUI.FRAME_HEIGHT/2);

    if (xOffset < 0) {
      xOffset = 0;
    }else {
        targetX = xSize * Tile.TILEWIDTH - GUI.FRAME_WIDTH + 80;
        if (xOffset > targetX) {
            xOffset = targetX;
      } // end of if
     } // end of if-else

   if (yOffset < 0) {
      yOffset = 0;
    }else {
       targetY = ySize * Tile.TILEHEIGHT - GUI.FRAME_HEIGHT - 80;
       if (yOffset > targetY) {
           yOffset = targetY;
     } // end of if                      
     } // end of if-else
    }

     public void setxOffset(int xOffset) {
         this.xOffset = xOffset;
     }

     public void setyOffset(int yOffset) {
         this.yOffset = yOffset;
     }

     public int getXOffset() {
    return xOffset;
 }
     public int getYOffset() {
    return yOffset;
  }


}
 
