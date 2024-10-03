package reaction;

import graphics.G;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;
import music.I;
import music.UC;

public class Shape {
  public static Shape.Database DB = Shape.Database.load();
  public static Shape DOT = DB.get("DOT");
  // LIST will always be up to date with DB, supported by language feature
  public static Collection<Shape> LIST = DB.values();

  public Prototype.List prototypes = new Prototype.List();
  //name would be compass directions: SW, NE, NW ...
  public String name;

  public Shape(String name) {
    this.name = name;
  }

  public static Shape recognize(Ink ink){ //note: can return null
    if(ink.vs.size.x < UC.dotThreshHold && ink.vs.size.y < UC.dotThreshHold){return DOT;}
    Shape bestMatch = null;
    int bestSoFar = UC.noMatchDist;
    for(Shape s : LIST){
      int d = s.prototypes.bestDist(ink.norm);
      if(d < bestSoFar){
        bestMatch = s;
        bestSoFar = d;}
    }
    return bestMatch;
  }

  //------------------------------Database---------------------------------------
  // TreeMap sort the keys. HashMap does not sort keys.
  public static class Database extends TreeMap<String, Shape> {

    public static Database load(){
      Database res = new Database();
      //stub
      res.put("DOT", new Shape("DOT"));
      return res;
    }

    public static void save(){
      //stub
    }

    public boolean isKnown(String name){return containsKey(name);}

    public boolean isUnknown(String name){return !containsKey(name);}

    public boolean isLegal(String name){return !name.equals("") && !name.equals("DOT");}
  }

  //--------------------------Prototype--------------------represent one way of drawing a shape, like a circle can be drawn clockwise or counterclockwise
  public static class Prototype extends Ink.Norm {
    public int nBlend;

    public void blend(Ink.Norm norm){
      blend(norm, nBlend);
      nBlend++;
    }

    //------------------------------List---------------------------
    public static class List extends ArrayList<Prototype> implements I.Show{
      //set as side effect of bestDist()
      public static Prototype bestMatch;

      public int bestDist(Ink.Norm norm){
        bestMatch = null;
        int bestSoFar = UC.noMatchDist;
        for (Prototype p : this){
          int d = p.dist(norm);
          if (d < bestSoFar){
            bestMatch = p;
            bestSoFar = d;
          }
        }
        return bestSoFar;
      }

      //debug purpose
      private static int m = 10, w = 60; //m for margin, w for width
      private static G.VS showBox = new G.VS(m, m, w, w);
      @Override
      public void show(Graphics g) {
        g.setColor(Color.ORANGE);
        for (int i = 0; i < size(); i++){
          Prototype p = get(i);
          int x = m + i * (m+w);
          showBox.loc.set(x, m);
          p.drawAt(g, showBox);
          g.drawString(""+p.nBlend, x, 20);
        }
      }
    }
  }

}
