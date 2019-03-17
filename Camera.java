 import java.awt.*;

 public class Camera{
  
  int xSize, ySize;
  int xOffset = 0 ;
  int yOffset = 0;
  int tX,tY;
//  Pointer pointer;
  
  public Camera(){
  }
  
  public Camera(int pXSize, int pYSize){
   xSize = pXSize;
   ySize = pYSize;
  }
  public void centerOnMover(Mover pMover){
      refreshOffset();
      xOffset = (int) pMover.getLocation().getX() - (GUI.FRAME_WIDTH/2);
      yOffset = (int) pMover.getLocation().getY() - (GUI.FRAME_HEIGHT/2);

    if (xOffset < 0) {
      xOffset = 0;
    }else {
        tX = xSize * Tile.TILEWIDTH - GUI.FRAME_WIDTH + 80;
      if (xOffset > tX) {
        xOffset = tX;
      } // end of if
     } // end of if-else

   if (yOffset < 0) {
      yOffset = 0;
    }else {
      tY = ySize * Tile.TILEHEIGHT - GUI.FRAME_HEIGHT - 80;
      if (yOffset > tY) {
        yOffset = tY;
     } // end of if                      
     } // end of if-else
    }
     public void centerOnEditor(Editor.Pointer pointer){
         xOffset = (int) pointer.getxPos() - (GUI.FRAME_WIDTH/2);
         yOffset = (int) pointer.getyPos() - (GUI.FRAME_HEIGHT/2);

         if (xOffset < 0) {
             xOffset = 0;
         }else {
             tX = xSize * Tile.TILEWIDTH - GUI.FRAME_WIDTH + 80;
             if (xOffset > tX) {
                 xOffset = tX;
             } // end of if
         } // end of if-else

         if (yOffset < 0) {
             yOffset = 0;
         }else {
             tY = ySize * Tile.TILEHEIGHT - GUI.FRAME_HEIGHT - 80;
             if (yOffset > tY) {
                 yOffset = tY;
             } // end of if
         } // end of if-else
     }
     public void refreshOffset(){
         tY = ySize * Tile.TILEHEIGHT - GUI.FRAME_HEIGHT - 80;
         tX = xSize * Tile.TILEWIDTH - GUI.FRAME_WIDTH + 80;
//         System.out.println(tY+"|"+tX);
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
 
