package music;

import graphics.G;
import java.awt.Graphics;
import java.util.ArrayList;
import reaction.Gesture;
import reaction.Mass;
import reaction.Reaction;

//Staff is a musical term
public class Staff extends Mass {
  public Sys sys;
  public int iStaff;
  public G.HC staffTop;
  public Staff.Fmt fmt = Fmt.DEFAULT; //fmt = format

  public Staff(Sys sys, int iStaff, G.HC staffTop) {
    super("BACK");
    this.sys = sys;
    this.iStaff = iStaff;
    this.staffTop = staffTop;

    addReaction(new Reaction("S-S") {
      @Override
      public int bid(Gesture g) {
        Page PAGE = sys.page;
        int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
        if (x < PAGE.margins.left || x > PAGE.margins.right+UC.barToMarginSnap) {
          return UC.noBid;
        }
        int d = Math.abs(y1 - Staff.this.yTop()) + Math.abs(y2 - Staff.this.yBot());
        //allow S-S cycleBar gesture to out bid this reaction
        return d < 30 ? d + UC.barToMarginSnap : UC.noBid;
      }

      @Override
      public void act(Gesture g) {
        new Bar(Staff.this.sys, g.vs.xM());
      }
    });
  }

  public int yTop(){return staffTop.v();}

  public int yOfLine(int line){return yTop() + line*fmt.H;}

  public int yBot(){return yOfLine(2 * (fmt.nLines - 1));}

  public Staff copy(Sys newSys){
    G.HC hc = new G.HC(newSys.staffs.sysTop, staffTop.dv);
    return new Staff(newSys, iStaff, hc);
  }

  public void show(Graphics g){
    Page.Margins m = sys.page.margins;
    int x1 = m.left, x2 = m.right, y = yTop(), h = fmt.H*2;
    for (int i=0; i<fmt.nLines; i++){
      g.drawLine(x1, y + i*h, x2, y + i*h);
    }
  }

  //------------------------Fmt----------------------format
  public static class Fmt{
    public static Fmt DEFAULT = new Fmt(5, 8);

    public int nLines;  //5 lines on staff
    public int H; //this is half of the space between lines

    public Fmt(int nLines, int H) {
      this.nLines = nLines;
      this.H = H;
    }
  }

  //------------------------------List-----------------------------
  public static class List extends ArrayList<Staff> {
    public G.HC sysTop;

    public List(G.HC sysTop) {
      this.sysTop = sysTop;
    }

    public int sysTop(){
      return sysTop.v();
    }
  }


}
