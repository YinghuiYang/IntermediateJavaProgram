package reaction;
import graphics.G;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import music.*;

public class Ink extends G.PL implements I.Show{

  public static Buffer BUFFER = new Buffer();

  public Ink(){
    super(BUFFER.n);
    for (int i = 0; i < BUFFER.n; i++){
      points[i] = new G.V(BUFFER.points[i]);
    }
  }

  @Override
  public void show(Graphics g) {
    g.setColor(Color.BLUE);
    //stub means a placeholder. Will come back for stub functions
    //g.drawString("InkStub", 50, 50);
    this.draw(g);
  }



  //-------------------------Buffer------------------this is a singleton
  public static class Buffer extends G.PL implements I.Show, I.Area{

    public static final int MAX = UC.inkBufferMax;

    public int n;

    public G.BBox bbox = new G.BBox();

    private Buffer(){super(MAX);}

    public void add(int x, int y){if(n<MAX){points[n++].set(x, y); bbox.add(x, y);}}

    public void clear(){n = 0;}

    //dn = mouse pressed down, user starts drawing a new line, need to clear the points buffer
    public void dn(int x, int y) {clear(); bbox.set(x, y); add(x, y);}

    public void drag(int x, int y) {add(x, y);}

    //up = mouse released. user finish drawing a line
    public void up(int x, int y) {add(x, y);}

    @Override
    public boolean hit(int x, int y) {return true;}

    @Override
    public void show(Graphics g) {bbox.draw(g); drawN(g, n);}
  }



  //--------------------------------List-------------------------list of ink
  public static class List extends ArrayList<Ink> implements I.Show{

    @Override
    public void show(Graphics g) {for(Ink ink : this) {ink.show(g);}}

  }
}
