package music;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import reaction.Mass;

public class Beam extends Mass {
  private static int[] points = {0, 0, 0, 0};
  public static Polygon poly = new Polygon(points, points, 4);

  public Stem.List stems = new Stem.List();

  public Beam(Stem first, Stem last) {
    super("NOTE");
    addStem(first);
    addStem(last);
  }

  public Stem first(){
    return stems.get(0);
  }

  public Stem last(){
    return stems.get(stems.size()-1);
  }

  public void deleteStem(){
    for (Stem s : stems){
      s.beam = null;
    }
    deleteMass();
  }

  public void addStem(Stem stem){
    if(stem.beam == null){
      stems.addStem(stem);
      stem.beam = this;
      stem.nFlag = 1;
      stems.sort();
    }
  }

  public void setMasterBeam(){
    mX1 = first().x();
    mY1 = first().yBeamEnd();
    mX2 = last().x();
    mY2 = last().yBeamEnd();
  }

  public void show(Graphics g){
    g.setColor(Color.BLACK);
    drawBeamGroup(g);
  }

  private void drawBeamGroup(Graphics g){
    setMasterBeam();
    Stem firstStem = first();
    int H = firstStem.staff.fmt.H;
    int sH = firstStem.isUp ? H:-H; //sH sign of H
    int nPrev = 0;
    int nCur = first().nFlag;
    int nNext = stems.get(1).nFlag;
    int pX;   //location of previous stem
    int cX = firstStem.x();   //current stem location
    int bX = cX + 3*H;      //location of beam end
    if(nCur > nNext){
      drawBeamStack(g, nNext, nCur, cX, bX, sH);    //draw beams for first stem
    }
    for(int cur = 1; cur < stems.size(); cur++){
      Stem sCur = stems.get(cur);
      pX = cX;
      cX = sCur.x();
      nPrev = nCur;
      nCur = nNext;
      nNext = (cur < stems.size()-1) ? stems.get(cur+1).nFlag : 0;
      int nBack = Math.min(nPrev, nCur);  //find out how many beams needed
      drawBeamStack(g, 0, nBack, pX, cX, sH);
      if(nCur > nPrev && nCur > nNext){   //test if we need beam-lets
        if(nPrev < nNext){
          bX = cX + 3*H;
          drawBeamStack(g, nNext, nCur, cX, bX, sH);
        }else{
          bX = cX - 3*H;
          drawBeamStack(g, nPrev, nCur, bX, cX, sH);
        }
      }
    }
  }

  public static int yOfX(int x, int x1, int y1, int x2, int y2){
    int dy = y2 - y1;
    int dx = x2 - x1;
    return (x - x1) * dy/dx + y1;
  }

  public static int mX1, mY1, mX2, mY2; //coordinates for master beam

  public static int yOfX(int x){
    int dy = mY2 - mY1;
    int dx = mX2 - mX1;
    return (x - mX1) * dy/dx + mY1;
  }

  public static void setMasterBeam(int x1, int y1, int x2, int y2){
    mX1 = x1;
    mY1 = y1;
    mX2 = x2;
    mY2 = y2;
  }

  public static void setPoly(int x1, int y1, int x2, int y2, int H){
    int[] a = poly.xpoints;
    a[0] = x1;
    a[1] = x2;
    a[2] = x2;
    a[3] = x1;
    a = poly.ypoints;
    a[0] = y1;
    a[1] = y2;
    a[2] = y2+H;
    a[3] = y1+H;
  }

  public static void drawBeamStack(Graphics g, int n1, int n2, int x1, int x2, int H){
    System.out.println("drawBeamStack called" + x1);
    int y1 = yOfX(x1), y2 = yOfX(x2);
    for(int i = n1; i < n2; i++){
      setPoly(x1, y1+i*2*H, x2, y2+i*2*H, H);
      g.fillPolygon(poly);
    }
  }

  public static boolean verticalLineCrossSegment(int x, int y1, int y2, int bX, int bY, int eX, int eY){
    if(x<bX || x>eX){
      return false;
    }
    int y = yOfX(x, bX, bY, eX, eY);
    if(y1<y2){
      return y1<y && y<y2;
    } else{
      return y2<y && y<y1;
    }
  }
}
