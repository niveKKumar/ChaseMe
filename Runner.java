import java.awt.*;
import java.awt.geom.Point2D;

public class Runner extends Mover {
    private PathFinder pathfinder;
    private int border;
    private int delay = 1;
    private int speed;
    int collisionduration = 0;

    public Runner(GamePanel gp, int xPos, int yPos, SpriteSheet pSpriteSheet, MapBase[] pMap) {
        super(gp, xPos, yPos, pMap);
        sprites = pSpriteSheet;
        MOVER_WIDTH = sprites.getWidth();
        MOVER_HEIGHT = sprites.getHeight();
        img = sprites.getSpriteElement(0, 1);
        pathfinder = new PathFinder(map, this, gamePanel);
    }

    public void enemystraightrun(int pBorder, int pSpeed, int x, int y) {   //Gegner der nur gerade aus läuft (Delay = 1 - Kein Delay)
        border = pBorder;
        Point richtung = new Point (x,y);
        //Delay:
        speed = pSpeed;
        if (!collisionCheck()) {
            delay++;
            if (delay < speed) {
                delay++;
            } else {
                this.setMove(richtung);
                delay = 0;
            } // end of if-else

            }else{
            collisionduration++;
            if (collisionduration > 5){
                System.out.println("ich laufe gegen wand");
            }
        }
        }


    public void movetotarget(Mover character) {                     //Dynamischer Gegner
        Tile start = map[0].mapTiles[(int)((this.getLocation().getX() + Tile.TILEWIDTH/2) /  Tile.TILEWIDTH)][(int)((this.getLocation().getY() + Tile.TILEHEIGHT) / Tile.TILEHEIGHT)];
        Tile target = map[0].mapTiles[(int)((character.getLocation().getX() + Tile.TILEWIDTH/2) /  Tile.TILEWIDTH)][(int)((character.getLocation().getY() + Tile.TILEHEIGHT) / Tile.TILEHEIGHT)];
        pathfinder.searchPath(start,target);
        Point2D from = pathfinder.getNextStep();
//        System.out.println(from);
        Point2D to = pathfinder.getNextStep();
//        System.out.println(to);
        if(to!=null ){
            from =pathfinder.getNextStep();
//            System.out.println(from);
            to = pathfinder.getNextStep();
//            System.out.println(to);
            pathfinder.searchPath(start,target);

            try {
                this.setPathMove(from, to);
            } catch (Exception e) {
                System.out.println("Game Over");
            }
        }else {
            System.out.println("Game Over");
        } // end of if-else
    }

}
