package music;

import java.awt.Graphics;
import java.util.ArrayList;
import reaction.Mass;

public class Clef extends Mass implements Comparable<Clef> {
  public Glyph glyph;
  public int x;
  public Staff staff;

  public Clef(Staff staff, int x, Glyph glyph) {
    super("NOTE");
    this.staff = staff;
    this.x = x;
    this.glyph = glyph;
  }

  public void show(Graphics g){
    glyph.showAt(g, staff.fmt.H ,this.x, staff.yOfLine(4));
  }

  public int compareTo(Clef c) {
    return x-c.x;
  }

  //---------------------------List---------------------------
  public static class List extends ArrayList<Clef>{

  }
}
