import sun.awt.SunHints;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyManager implements KeyListener {
  public boolean [] keys;
  public boolean up,down,left,right,upLeft,upRight,downLeft,downRight;
  public boolean shift;
  public boolean strZ;
  
  public KeyManager(){
    keys = new boolean[256];
  }

  public void keyPressed(KeyEvent e) {
    keys[e.getKeyCode()] = true;
  }
  public void keyReleased(KeyEvent e) {
   keys[e.getKeyCode()] = false;
  }
  public void keyTyped(KeyEvent e) {}
  
  public void update() {
    shift = keys[16];
    up = keys[KeyEvent.VK_W];
    down = keys[KeyEvent.VK_S];
    left = keys[KeyEvent.VK_A];
    right = keys[KeyEvent.VK_D];
    
    upLeft = keys[KeyEvent.VK_Q];
    upRight = keys[KeyEvent.VK_E];
    downLeft = keys[KeyEvent.VK_Y];
    downRight = keys[KeyEvent.VK_C];

    /*if (keys[KeyEvent.VK_Z] && keys[KeyEvent.CTRL_MASK]){
      strZ = true;
      System.out.println("strz");
    }*/

  }
}
