package music;

import graphics.G;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import music.Clef.List;
import reaction.Gesture;
import reaction.Mass;
import reaction.Reaction;

//Staff is a musical term
public class Staff extends Mass {
  public Sys sys;
  public int iStaff;
  public G.HC staffTop;
  public Staff.Fmt fmt; //fmt = format
  public Clef.List clefs = null;

  public Staff(Sys sys, int iStaff, G.HC staffTop, Staff.Fmt fmt) {
    super("BACK");
    this.sys = sys;
    this.iStaff = iStaff;
    this.staffTop = staffTop;
    this.fmt = fmt;

    addReaction(new Reaction("S-S") {   //create new bar
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

    addReaction(new Reaction("S-S") { //toggle bar continue
      @Override
      public void act(Gesture g) {
        fmt.toggleBarContinues();
      }

      @Override
      public int bid(Gesture g) {
        if(Staff.this.sys.iSys != 0){
          return UC.noBid;
        }
        int y1 = g.vs.yL(), y2 = g.vs.yH();
        if(iStaff == sys.staffs.size()-1){
          return UC.noBid;
        }
        if(Math.abs(y1-yBot()) > 20){
          return UC.noBid;
        }
        Staff nextStaff = sys.staffs.get(iStaff+1);
        if(Math.abs(y2-nextStaff.yTop()) > 20){
          return UC.noBid;
        }
        return 10;
      }
    });

    addReaction(new Reaction("SW-SW") {
      @Override
      public void act(Gesture g) {
        new Head(Staff.this, g.vs.xM(), g.vs.yM());
      }

      @Override
      public int bid(Gesture g) {
        Page PAGE = sys.page;
        int x = g.vs.xM(), y = g.vs.yM();
        if(x < PAGE.margins.left || x > PAGE.margins.right) {return UC.noBid;}
        int H = Staff.this.fmt.H, top = Staff.this.yTop() - H, bot = Staff.this.yBot() + H;
        if(y < top || y > bot){return UC.noBid;}
        return 10;
      }
    });

    //W-S add quarter rest
    addReaction(new Reaction("W-S") {
      public int bid(Gesture g) {
        int x = g.vs.xM(), y = g.vs.yM();
        if(x < sys.page.margins.left || x > sys.page.margins.right) {return UC.noBid;}
        int H = fmt.H, top = yTop() - H, bot = yBot() + H;
        if(y < top || y > bot){return UC.noBid;}
        return 10;
      }

      public void act(Gesture g) {
        Time t = Staff.this.sys.getTime(g.vs.xM());
        //need to check whether a REST already exists
        new Rest(Staff.this, t);
      }
    });

    addReaction(new Reaction("E-S") {
      public int bid(Gesture g) {
        int x = g.vs.xM(), y = g.vs.yM();
        if(x < sys.page.margins.left || x > sys.page.margins.right) {return UC.noBid;}
        int H = fmt.H, top = yTop() - H, bot = yBot() + H;
        if(y < top || y > bot){return UC.noBid;}
        return 10;
      }

      public void act(Gesture g) {
        Time t = Staff.this.sys.getTime(g.vs.xM());
        //need to check whether a REST already exists
        (new Rest(Staff.this, t)).nFlag = 1;
      }
    });

    addReaction(new Reaction("SW-SE") { //G clef
      public int bid(Gesture g) {
        int dTop = Math.abs(g.vs.yL() - yTop()), dBot = Math.abs(g.vs.yH() - yBot());
        if(dTop+dBot > 60){return UC.noBid;}
        return dTop+dBot;
      }

      public void act(Gesture g) {
        if(Staff.this.initialClef() == null){
          setInitialClef(Glyph.CLEF_G);
        } else {
          addNewClef(Glyph.CLEF_G, g.vs.xM());
        }
      }
    });

    addReaction(new Reaction("SE-SW") { //F clef
      public int bid(Gesture g) {
        int dTop = Math.abs(g.vs.yL() - yTop()), dBot = Math.abs(g.vs.yH() - yBot());
        if(dTop+dBot > 60){return UC.noBid;}
        return dTop+dBot;
      }

      public void act(Gesture g) {
        if(Staff.this.initialClef() == null){
          setInitialClef(Glyph.CLEF_F);
        } else {
          addNewClef(Glyph.CLEF_F, g.vs.xM());
        }
      }
    });
  }

  public void setInitialClef(Glyph glyph){
    Staff s=this, ps=prevStaff();
    while(ps!=null){
      s=ps;
      ps=s.prevStaff();
    }
    s.clefs = new Clef.List();
    s.clefs.add(new Clef(s, -900, glyph)); //put the initial clef off the screen display range
  }

  public void addNewClef(Glyph glyph, int x){
    if(clefs == null){
      clefs = new Clef.List();
    }
    clefs.add(new Clef(this, x, glyph));
    Collections.sort(clefs);
  }

  public Staff prevStaff(){
    return sys.iSys == 0 ? null : sys.page.sysList.get(sys.iSys-1).staffs.get(this.iStaff);
  }

  public Clef lastClef(){return clefs==null ? null:clefs.get(clefs.size()-1);}

  public Clef firstClef(){return clefs==null ? null:clefs.get(0);}

  public Clef initialClef(){
    Staff s = this, ps = prevStaff();
    while(ps != null && ps.clefs == null){
      s = ps;
      ps = s.prevStaff();
    }
    return ps == null ? s.firstClef():ps.lastClef();
  }

  public int yTop(){return staffTop.v();}

  public int yOfLine(int line){return yTop() + line*fmt.H;}

  //addition of H/2 will turn truncation to rounding. the bias will make the negative number rounded correctly
  public int lineOfY(int y){
    int H = fmt.H, bias = 100;
    int top = yTop() - H*bias;
    return (y - top + H/2)/H - bias;
  }

  public int yBot(){return yOfLine(2 * (fmt.nLines - 1));}

  public Staff copy(Sys newSys){
    G.HC hc = new G.HC(newSys.staffs.sysTop, staffTop.dv);
    return new Staff(newSys, iStaff, hc, fmt);
  }

  public void show(Graphics g){
    Page.Margins m = sys.page.margins;
    int x1 = m.left, x2 = m.right, y = yTop(), h = fmt.H*2;
    for (int i=0; i<fmt.nLines; i++){
      g.drawLine(x1, y + i*h, x2, y + i*h);
    }
    Clef clef = initialClef();
    int x = sys.page.margins.left + UC.initialClefOffset;
    if(clef != null){
      clef.glyph.showAt(g, fmt.H, x, yOfLine(4));
    }
  }

  //------------------------Fmt----------------------format
  public static class Fmt{
    public boolean barContinues = false;

    public static Fmt DEFAULT = new Fmt(5, 8);

    public int nLines;  //5 lines on staff
    public int H; //this is half of the space between lines

    public Fmt(int nLines, int H) {
      this.nLines = nLines;
      this.H = H;
    }

    public void toggleBarContinues(){barContinues = !barContinues;}
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
