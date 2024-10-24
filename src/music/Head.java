package music;

import java.awt.Graphics;
import reaction.Mass;

public class Head extends Mass {
  public Staff staff;
  public int line;
  public Time time;

  public Head(Staff staff, int x, int y) {
    super("NOTE");
    this.staff = staff;
    time = staff.sys.getTime(x);
    int H = staff.fmt.H;
    int top = staff.yTop() - H;
    line = (y - top + H/2)/H - 1;
  }

  public void show(Graphics g) {
    int H = staff.fmt.H;
    Glyph.HEAD_Q.showAt(g,H, time.x, staff.yTop() + line*H);
  }

}
