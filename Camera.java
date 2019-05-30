public class Camera {

    private int xSize, ySize;
    private int chapterOffsetX, chapterOffsetY;
    private int xOffset, yOffset;
    private int clickXOffset = 0, clickYOffset = 0;

     public Camera() {
     }

    public Camera(int pXSize, int pYSize, int chapterOffsetX, int chapterOffsetY) {
         xSize = pXSize;
         ySize = pYSize;
        this.chapterOffsetX = chapterOffsetX;
        this.chapterOffsetY = chapterOffsetY;
     }

    public void centerOnObject(Pointer pointer) {
//        int virtualSpace = this.virtualSpace;
//
//         if (clickXOffset <= GUI.GAMEPANEL_WIDTH/2) {
//            virtualSpace = virtualSpace;
//        } else {
//            int maxLoc = xSize * Tile.TILEWIDTH - GUI.GAMEPANEL_WIDTH  ;
//            if (clickXOffset >= maxLoc) {
//                virtualSpace = - virtualSpace;
//            } // end of if
//        } // end of if-else
//
//        if (clickYOffset <= GUI.GAMEPANEL_WIDTH ) {
//            virtualSpace = virtualSpace;
//        } else {
//            int maxLoc = ySize * Tile.TILEHEIGHT - GUI.GAMEPANEL_HEIGHT ;
//            if (clickYOffset >= maxLoc) {
//                virtualSpace = -virtualSpace;
//            } // end of if
//        } // end of if-else


        clickXOffset = (int) pointer.getLocation().getX() - (GUI.GAMEPANEL_WIDTH / 2);
        clickYOffset = (int) pointer.getLocation().getY() - (GUI.GAMEPANEL_HEIGHT / 2);
//        Edge Snapping

         //https://gamedev.stackexchange.com/questions/138756/smooth-camera-movement-java

         //Lerp:
        int targetX = clickXOffset + Character.MOVER_WIDTH / 2;
        int targetY = clickYOffset + Character.MOVER_HEIGHT / 2;

        xOffset += (targetX - xOffset) * 0.1f;
        yOffset += (targetY - yOffset) * 0.1f;

        xOffset = Math.min(chapterOffsetX + xSize * Tile.TILEWIDTH - GUI.GAMEPANEL_WIDTH, Math.max(0, xOffset));
        yOffset = Math.min(chapterOffsetY + ySize * Tile.TILEHEIGHT - GUI.GAMEPANEL_HEIGHT, Math.max(0, yOffset));

//        if (xSize * Tile.TILEWIDTH < GUI.GAMEPANEL_WIDTH) {
//            clickXOffset = xSize * Tile.TILEWIDTH / 2;
//            xOffset = xSize * Tile.TILEWIDTH / 2;
//        }
//        if (ySize * Tile.TILEHEIGHT < GUI.GAMEPANEL_WIDTH) {
//            clickYOffset = ySize * Tile.TILEHEIGHT / 2;
//            yOffset = ySize * Tile.TILEHEIGHT / 2;
//        }

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

    public void setClickXOffset(int clickXOffset) {
        this.clickXOffset = clickXOffset;
    }

    public int getClickYOffset() {
        return clickYOffset;
    }

    public int getClickXOffset() {
        return clickXOffset;
    }

    public void setClickYOffset(int clickYOffset) {
        this.clickYOffset = clickYOffset;
    }
}
 
