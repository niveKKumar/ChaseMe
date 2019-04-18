public class Camera {

    private int xSize, ySize;
    private int xOffset, yOffset;
    private int clickXOffset = 0, clickYOffset = 0;

     public Camera() {
     }

    public Camera(int pXSize, int pYSize) {
         xSize = pXSize;
         ySize = pYSize;
     }

    public void centerOnObject(Pointer pointer) {
        clickXOffset = (int) pointer.getLocation().getX() - (GUI.FRAME_WIDTH / 2);
        clickYOffset = (int) pointer.getLocation().getY() - (GUI.FRAME_HEIGHT / 2);
        //Edge Snapping
//         if (clickXOffset < 0) {
//             clickXOffset = 0;
//         } else {
//             int maxLoc = xSize * Tile.TILEWIDTH - GUI.FRAME_WIDTH + 80  ;
//             if (clickXOffset > maxLoc) {
//                 clickXOffset = maxLoc;
//             } // end of if
//         } // end of if-else
//
//         if (clickYOffset < 0) {
//             clickYOffset = 0;
//         } else {
//             int maxLoc = ySize * Tile.TILEHEIGHT - GUI.FRAME_HEIGHT - 80  ;
//             if (clickYOffset > maxLoc) {
//                 clickYOffset = maxLoc;
//             } // end of if
//         } // end of if-else




         //https://gamedev.stackexchange.com/questions/138756/smooth-camera-movement-java

         //Lerp:
        int targetX = clickXOffset + Character.MOVER_WIDTH / 2;
        int targetY = clickYOffset + Character.MOVER_HEIGHT / 2;

        xOffset += (targetX - xOffset) * 0.1;
        yOffset += (targetY - yOffset) * 0.1;

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

    public int getxSize() {
        return xSize;
    }

    public int getySize() {
        return ySize;
    }

    public int getClickXOffset() {
        return xOffset;
    }

    public int getClickYOffset() {
        return yOffset;
    }
}
 
