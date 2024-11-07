package music;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import reaction.Gesture;
import reaction.Reaction;

public class Stem extends Duration implements Comparable<Stem> {
  public Staff staff;
  public Head.List heads = new Head.List();
  public boolean isUp = true;
  public Beam beam = null;

  private Stem(Staff staff, Head.List heads, boolean up){
    this.staff = staff;
    isUp = up;
    for(Head h:heads){
      h.unStem();
      h.stem = this;
    }
    this.heads = heads;
    staff.sys.stems.addStem(this);
    setWrongSide();

    addReaction(new Reaction("E-E") {
      public int bid(Gesture g) {
        int y = g.vs.yM(), x1 = g.vs.xL(), x2 = g.vs.xH();
        int xS = Stem.this.x();
        if(x1 > xS || x2 < xS){return UC.noBid;}
        int y1 = Stem.this.yLow(), y2 = Stem.this.yHigh();
        if(y < y1 || y > y2){return UC.noBid;}
        return Math.abs(y-(y1+y2)/2) + 60;  //allow sys E-E to underbid this
      }

      public void act(Gesture g) {
        Stem.this.incFlag();
      }
    });

    addReaction(new Reaction("W-W") {
      public int bid(Gesture g) {
        int y = g.vs.yM(), x1 = g.vs.xL(), x2 = g.vs.xH();
        int xS = Stem.this.x();
        if(x1 > xS || x2 < xS){return UC.noBid;}
        int y1 = Stem.this.yLow(), y2 = Stem.this.yHigh();
        if(y < y1 || y > y2){return UC.noBid;}
        return Math.abs(y-(y1+y2)/2);
      }

      public void act(Gesture g) {
        Stem.this.decFlag();
      }
    });
  }

  public static Stem getStem(Staff staff, Time time, int y1, int y2, boolean up){
    Head.List heads = new Head.List();
    for(Head h : time.heads){
      int yH = h.y();
      if(yH > y1 && yH < y2){
        heads.add(h);
      }
    }
    if(heads.size() == 0){
      return null;    //no stem return if no head exists
    }
    Beam b = internalStem(staff.sys, time.x, y1, y2);   //possibly internal stem in beam group
    Stem res = new Stem(staff, heads, up);
    if(b!=null){
      b.addStem(res);
      res.nFlag = 1;
    }
    return res;
  }

  @Override
  public void show(Graphics g) {
    if(nFlag >= -1 && heads.size() > 0){
      int x = x(), h = staff.fmt.H, yH = yFirstHead(), yB = yBeamEnd();
      g.drawLine(x, yH, x, yB);
      if(nFlag >=1 && beam == null){
        if(nFlag == 1){(isUp ? Glyph.FLAG1D : Glyph.FLAG1U).showAt(g, h, x(), yBeamEnd());}
        if(nFlag == 2){(isUp ? Glyph.FLAG2D : Glyph.FLAG2U).showAt(g, h, x(), yBeamEnd());}
        if(nFlag == 3){(isUp ? Glyph.FLAG3D : Glyph.FLAG3U).showAt(g, h, x(), yBeamEnd());}
        if(nFlag == 4){(isUp ? Glyph.FLAG4D : Glyph.FLAG4U).showAt(g, h, x(), yBeamEnd());}
      }
    }

  }

  public Head firstHead(){
    return heads.get(isUp ? heads.size()-1 : 0);
  }

  public Head lastHead(){
    return heads.get(isUp ? 0 : heads.size()-1);
  }

  public int yLow(){
    return isUp ? yBeamEnd() : yFirstHead();
  }

  public int yHigh(){
    return isUp ? yFirstHead() : yBeamEnd();
  }

  public int yFirstHead(){
    Head h = firstHead();
    return h.staff.yOfLine(h.line);
  }

  public int yBeamEnd(){
    if(isInternalStem()){
      beam.setMasterBeam();
      return Beam.yOfX(x());
    }
    Head h = lastHead();
    int line = h.line;
    line += isUp ? -7 : 7;  //default one octave from head.
    int flagIncrement = nFlag > 2 ? 2*(nFlag - 2) : 0;
    line += isUp ? -flagIncrement : flagIncrement;
    if((isUp && line > 4) || (!isUp && line < 4)){line = 4;}  //hit the center line if possible
    return h.staff.yOfLine(line);
  }

  public boolean isInternalStem(){
    if(beam == null){return false;}
    if(this == beam.first() || this == beam.last()){return false;}
    return true;
  }

  public int x(){
    Head h = firstHead();
    return h.time.x + (isUp ? h.w() : 0);
  }

  public void deleteStem() {
    staff.sys.stems.remove(this);
    deleteMass();
  }

  //the last node on stem will behave differently
  public void setWrongSide() {
    Collections.sort(heads);
    int i, last, next;
    if(isUp){
      i = heads.size()-1;
      last = 0;
      next = -1;
    } else {
      i = 0;
      last = heads.size()-1;
      next = 1;
    }
    Head pH = heads.get(i); //previous head
    pH.wrongSide = false;
    while(i != last){
      i += next;
      Head nH = heads.get(i); //next head
      nH.wrongSide = (pH.staff == nH.staff && (Math.abs(nH.line - pH.line) <= 1) && !pH.wrongSide);
      pH = nH;
    }
  }

  @Override
  public int compareTo(Stem s) {
    return x() - s.x();
  }

  public static Beam internalStem(Sys sys, int x, int y1, int y2){
    for(Stem s: sys.stems){
      if(s.beam != null && s.x() < x && s.yLow() < y2 && s.yHigh() > y1){
        int bX = s.beam.first().x(), bY = s.beam.first().yBeamEnd();    //bX beginning X
        int eX = s.beam.last().x(), eY = s.beam.last().yBeamEnd();      //eX ending X
        if(Beam.verticalLineCrossSegment(x, y1, y2, bX, bY, eX, eY)){
          return s.beam;
        }
      }
    }
    return null;
  }

  //---------------------List--------------
  public static class List extends ArrayList<Stem>{
    public int yMin = 1_000_000, yMax = -1_000_000;
    public void addStem(Stem s){
      add(s);
      if(s.yLow() < yMin){yMin = s.yLow();}
      if(s.yHigh() > yMax){yMax = s.yHigh();}
    }

    public boolean fastReject(int y){
      return y > yMax || y < yMin;
    }

    public void sort(){
      Collections.sort(this);
    }

    public ArrayList<Stem> allIntersectors(int x1, int y1, int x2, int y2){
      ArrayList<Stem> res = new ArrayList<>();
      for(Stem s:this){
        if(Beam.verticalLineCrossSegment(s.x(), s.yLow(), s.yHigh(), x1, y1, x2, y2)){
          res.add(s);
        }
      }
      return res;
    }

  }
}
