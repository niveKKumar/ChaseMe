import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyManager implements KeyListener {
  public boolean [] keys;
  public boolean up,down,left,right,upLeft,upRight,downLeft,downRight;
  public boolean shift;
  public boolean str;
    public boolean plus, minus;

    int temp = 0;

  public KeyManager(){
      keys = new boolean[530];
  }

  public void keyPressed(KeyEvent e) {
    keys[e.getKeyCode()] = true;
//   //System.out.println(KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner());
  }
  public void keyReleased(KeyEvent e) {
   keys[e.getKeyCode()] = false;
  }

    public void keyTyped(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }

  public void update() {

    shift = keys[16];
    str = keys[17];
    up = keys[KeyEvent.VK_W];
    down = keys[KeyEvent.VK_S];
    left = keys[KeyEvent.VK_A];
    right = keys[KeyEvent.VK_D];
    
    upLeft = keys[KeyEvent.VK_Q];
    upRight = keys[KeyEvent.VK_E];
    downLeft = keys[KeyEvent.VK_Y];
    downRight = keys[KeyEvent.VK_C];
//    if (!shift){temp++;
//     //System.out.println(temp);}
      plus = keys[KeyEvent.VK_ADD];
      plus = keys[KeyEvent.VK_PLUS];
      minus = keys[KeyEvent.VK_SUBTRACT];
      minus = keys[KeyEvent.VK_MINUS];
  }
}
