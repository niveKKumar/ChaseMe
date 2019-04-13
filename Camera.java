import java.util.Date;

 public class Camera{

  int xSize, ySize;
     long time = (new Date()).getTime();
  int xOffset = 0 ;
  int yOffset = 0;
     Mover mover;

     public Camera() {
     }

     public Camera(Mover pMover, int pXSize, int pYSize) {
         mover = pMover;
         xSize = pXSize;
         ySize = pYSize;
     }

     public void centerOnObject(Mover pMover) {

         if (pMover.getLocation().getX() < 0) {
             pMover.setxPos(0);
         } else {
             int maxLoc = xSize * Tile.TILEWIDTH - GUI.FRAME_WIDTH;
             System.out.println(maxLoc);
             if (pMover.getLocation().getX() > maxLoc) {
                 pMover.setxPos(maxLoc);
             } // end of if
         } // end of if-else

         if (pMover.getLocation().getY() < 0) {
             pMover.setyPos(0);
         } else {
             int maxLoc = ySize * Tile.TILEHEIGHT - GUI.FRAME_HEIGHT;
             if (pMover.getLocation().getY() > maxLoc) {
                 pMover.setyPos(maxLoc);
             } // end of if
         } // end of if-else

         int targetX = pMover.getLocation().x - GUI.FRAME_WIDTH / 2 + Mover.MOVER_WIDTH / 2;
         int targetY = pMover.getLocation().y - GUI.FRAME_WIDTH / 2 + Mover.MOVER_HEIGHT / 2;


         //https://gamedev.stackexchange.com/questions/138756/smooth-camera-movement-java
         //Lerp:
         xOffset += (targetX - xOffset) * 0.2;
         yOffset += (targetY - yOffset) * 0.2;

         xOffset = Math.min(xSize * Tile.TILEWIDTH - GUI.FRAME_WIDTH, Math.max(0, xOffset));
         yOffset = Math.min(ySize * Tile.TILEWIDTH - GUI.FRAME_WIDTH, Math.max(0, yOffset));


     }


     public void setxOffset(int xOffset) {
         this.xOffset = xOffset;
     }

     public void setyOffset(int yOffset) {
         this.yOffset = yOffset;
     }

     public int getXOffset() {
         return Math.round(xOffset);
 }
     public int getYOffset() {
         return Math.round(yOffset);
  }


}
 
