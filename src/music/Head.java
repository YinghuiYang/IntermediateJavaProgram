package music;

import java.awt.Graphics;
import java.util.ArrayList;
import reaction.Gesture;
import reaction.Mass;
import reaction.Reaction;

public class Head extends Mass {
  public Staff staff;
  public int line;
  public Time time;
  public Glyph forcedGlyph; //in case we need to use a special music head note
  public Stem stem = null;
  public boolean wrongSide = false;

  public Head(Staff staff, int x, int y) {
    super("NOTE");
    this.staff = staff;
    time = staff.sys.getTime(x);
    time.heads.add(this);
    line = staff.lineOfY(y);

    addReaction(new Reaction("S-S") { //stem or unstem heads
      public int bid(Gesture g) {
        int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
        int W = Head.this.w(), y = Head.this.y();
        if(y1 > y || y2 < y){return UC.noBid;}
        int hL = Head.this.time.x, hR = hL+W;
        if(x < hL-W || x > hR+W){return UC.noBid;}
        if(x < hL+W/2){return hL-x;}
        if(x > hR-W/2){return x-hR;}
        return UC.noBid;
      }

      public void act(Gesture g) {
        int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
        Staff staff = Head.this.staff;
        Time t = Head.this.time;
        int w = Head.this.w();
        boolean up = x>(t.x + w/2);
        if(Head.this.stem == null){
          t.stemHeads(staff, up, y1, y2);
        } else{
          t.unStemHeads(y1,y2);
        }
      }
    });
  }

  //width
  public int w(){
    return 24*staff.fmt.H/10;
  }

  public void show(Graphics g) {
    int H = staff.fmt.H;
    (forcedGlyph!=null ? forcedGlyph : normalGlyph()).showAt(g,H, time.x, staff.yTop() + line*H);
  }

  public Glyph normalGlyph() {return Glyph.HEAD_Q;}

  public int y(){return staff.yOfLine(line);}

  public int x(){return time.x;}

  //stub: since we need to delete all reference, not just here
  public void delete(){time.heads.remove(this);}

  public void unStem() {
    if(stem != null) {
      stem.heads.remove(this);
      if(stem.heads.size() == 0) {stem.deleteStem();}
      stem = null;
      wrongSide = false;
    }
  }

  public void joinStem(Stem s){
    if(stem != null) {unStem();}
    s.heads.add(this);
    stem = s;
  }

  //---------------------------------List-------------------------
  public static class List extends ArrayList<Head> {

  }

}
