package music;

import graphics.G;
import graphics.G.HC;
import java.awt.Graphics;
import java.util.ArrayList;
import music.Staff.Fmt;
import music.Staff.List;
import reaction.Gesture;
import reaction.Mass;
import reaction.Reaction;

//sys is a list of staff
public class Sys extends Mass {
  public Page page;
  public int iSys;
  public Staff.List staffs;
  public Time.List times;
  public Stem.List stems = new Stem.List();

  public Sys(Page page, G.HC sysTop) {
    super("BACK");
    this.page = page;
    iSys = page.sysList.size();
    staffs = new Staff.List(sysTop);
    times = new Time.List(this);
    if(iSys == 0){
      staffs.add(new Staff(this, 0, new G.HC(sysTop, 0), new Staff.Fmt(5, 8)));
    } else {
      Sys oldSys = page.sysList.get(0);
      for(Staff oldStaff : oldSys.staffs){
        Staff ns = oldStaff.copy(this);
        this.staffs.add(ns);
      }
    }

    addReaction(new Reaction("E-E") {
      public int bid(Gesture g) {
        int x1 = g.vs.xL(), y1 = g.vs.yL(), x2 = g.vs.xH(), y2 = g.vs.yH();
        if(stems.fastReject((y1+y2)/2)){
          return UC.noBid;
        }
        ArrayList<Stem> temp = stems.allIntersectors(x1, y1, x2, y2);
        if(temp.size() < 2){
          return UC.noBid;
        }
        System.out.println("sys reaction crossed "+temp.size()+" stems");
        Beam b = temp.get(0).beam;
        for(Stem s : temp){
          if(s.beam != b){
            return UC.noBid;
          }
        }
        System.out.println("All stem share owner");
        if(b==null && temp.size()!=2){return UC.noBid;}
        if(b==null && (temp.get(0).nFlag!=0 ||temp.get(1).nFlag!=0)){return UC.noBid;}
        return 50;
      }

      public void act(Gesture g) {
        int x1 = g.vs.xL(), y1 = g.vs.yL(), x2 = g.vs.xH(), y2 = g.vs.yH();
        ArrayList<Stem> temp = stems.allIntersectors(x1, y1, x2, y2);
        Beam b = temp.get(0).beam;
        if(b == null){
          new Beam(temp.get(0), temp.get(1));
        } else{
          for (Stem s:temp){
            s.incFlag();
          }
        }
      }
    });
  }

  public Time getTime(int x){
    return times.getTime(x);
  }

  public int yTop(){return staffs.sysTop();}

  public int yBot(){return staffs.get(staffs.size()-1).yBot();}

  public int height(){return yBot()-yTop();}

  public void addNewStaff(int y){
    int off = y - staffs.sysTop();  //offset
    G.HC staffTop = new G.HC(staffs.sysTop, off);
    staffs.add(new Staff(this, staffs.size(), staffTop, new Staff.Fmt(5, 8)));
    page.updateMaxH();
  }

  public void show(Graphics g){
    int x = page.margins.left;
    g.drawLine(x, yTop(), x, yBot());
    page.show(g);
  }

  //-------------List------------------
  public static class List extends ArrayList<Sys> {

  }

}
