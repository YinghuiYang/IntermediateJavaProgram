package music;

import java.awt.Color;
import java.awt.Graphics;
import reaction.Gesture;
import reaction.Mass;
import reaction.Reaction;

public class Bar extends Mass {
  private static final int FAT = 0x2, RIGHT = 0x4, LEFT = 0x8;
  /*
  0 means a single line
  1: double
  2: fine (Italian word for music term, means end)
  if either right or left, it repeats
   */
  public Sys sys;
  public int x, barType=0;

  public Bar(Sys sys, int x){
    super("BACK");
    this.sys = sys;
    int right = sys.page.margins.right;
    this.x = x;
    if(Math.abs(right-x) < UC.barToMarginSnap){
      this.x = right;
    }

    addReaction(new Reaction("S-S") { //cycle this bar
      @Override
      public int bid(Gesture g) {
        int x = g.vs.xM();
        if(Math.abs(x-Bar.this.x) > UC.barToMarginSnap){
          return UC.noBid;
        }
        int y1 = g.vs.yL(), y2 = g.vs.yH();
        if(y1 < Bar.this.sys.yTop() - 20 || y2 > Bar.this.sys.yBot() + 20){
          return UC.noBid;
        }
        return Math.abs(x-Bar.this.x);
      }

      @Override
      public void act(Gesture g) {
        Bar.this.cycleType();
      }
    });
  }

  //cycleType is not always called, so barType could be other values than (0,1,2)
  public void cycleType(){
    barType++;
    if(barType>2){barType=0;}
    //barType = barType%3; this should work as well
  }

  //toggle barType to left in bit operation
  public void toggleLeft(){
    barType = barType^LEFT;
  }

  public void toggleRight(){
    barType = barType^RIGHT;
  }

  public void show(Graphics g){
    g.setColor(barType == 1 ? Color.RED:Color.BLACK);
    for(Staff staff : sys.staffs){
      g.drawLine(x, staff.yTop(), x, staff.yBot());
    }
  }

  public static void wings(Graphics g, int x, int y1, int y2, int dx, int dy){
    g.drawLine(x, y1, x+dx, y1-dy);
    g.drawLine(x, y2, x+dx, y2+dy);
  }

  public static void fatBar(Graphics g, int x, int y1, int y2, int dx){
    g.fillRect(x, y1, dx, y2-y1);
  }

  public static void fineBar(Graphics g, int x, int y1, int y2){
    g.drawLine(x, y1, x, y2);
  }

  public void drawDots(Graphics g, int x, int top){
    int H = sys.page.maxH;
    if((barType & LEFT) != 0){
      g.fillOval(x - 3*H, top + 11*H/4, H/2, H/2);
      g.fillOval(x - 3*H, top + 19*H/4, H/2, H/2);
    }
    if((barType & RIGHT) != 0){
      g.fillOval(x + 3*H/2, top + 11*H/4, H/2, H/2);
      g.fillOval(x + 3*H/2, top + 19*H/4, H/2, H/2);
    }
  }
}
