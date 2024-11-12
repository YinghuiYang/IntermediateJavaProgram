package music;

import java.awt.Graphics;
import reaction.Gesture;
import reaction.Mass;
import reaction.Reaction;

public class Accid extends Mass { //accidental, some music term
  public static Glyph[] GLYPHS = {Glyph.DFLAT, Glyph.FLAT, Glyph.NATURAL, Glyph.SHARP, Glyph.DSHARP};
  public static final int FLAT = 1, NATURAL = 2, SHARP = 3;

  public int iGlyph;
  public Head head;
  public int left = 0;

  public Accid(Head head, int iGlyph) {
    super("NOTE");
    this.iGlyph = iGlyph;
    this.head = head;

    addReaction(new Reaction("DOT") {
      @Override
      public int bid(Gesture g) {
        int x = g.vs.xM(), y = g.vs.yM();
        int accidx = Accid.this.x(), accidy = head.y();
        int dx = Math.abs(x-accidx), dy = Math.abs(y-accidy), dist = dx+dy;
        return dist > 50 ? UC.noBid : dist;
      }

      @Override
      public void act(Gesture g) {
        left += 10;
        if(left > 50){
          left = 0;
        }
      }
    });

    addReaction(new Reaction("S-N") { //for accid deletion
      public int bid(Gesture g) {
       int x = g.vs.xM(), y = g.vs.yL();
       int ax = Accid.this.x() + head.w()/2, ay = Accid.this.head.y();
       int dx = Math.abs(x-ax), dy = Math.abs(y-ay), dist = dx+dy;
       return dist > 50 ? UC.noBid : dist;
      }
      public void act(Gesture g) {
        Accid.this.deleteAccid();
      }
    });
  }

  public void deleteAccid() {
    head.accid = null;
    deleteMass();
  }

  public void show(Graphics g) {
    GLYPHS[iGlyph].showAt(g, head.staff.fmt.H, x(), head.y());
  }

  public int x(){
    return head.x() - left - UC.accidHeadOffset;
  }

}
