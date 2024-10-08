package reaction;
import graphics.G;
import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.ArrayList;
import music.*;

public class Ink implements I.Show{
  public static Buffer BUFFER = new Buffer();
  //public static final int K = UC.normSampleSize;
  public Norm norm;
  public G.VS vs;
  //public static G.VS temp = new G.VS(100, 100, 100, 100);

  public Ink(){
//    super(BUFFER.n);
//    for (int i = 0; i < BUFFER.n; i++){
//      points[i] = new G.V(BUFFER.points[i]);
//    }
    //super(K);
    //BUFFER.subSample(this);
    //G.V.T.set(BUFFER.bbox, temp);
    //transform();
    //G.V.T.set(temp, BUFFER.bbox.getNewVS());
    //transform();
    norm = new Norm();
    vs = BUFFER.bbox.getNewVS();
  }

  public void show(Graphics g) {
    g.setColor(Color.BLUE);
    //stub means a placeholder. Will come back for stub functions
    //g.drawString("InkStub", 50, 50);
    //this.draw(g);
    norm.drawAt(g, vs);
  }

  //----------------------------Norm----------------------normalized coordinate system
  public static class Norm extends G.PL implements Serializable {
    public static final int N = UC.normSampleSize, MAX = UC.normCoordMax;
    //normalized coordinate system
    public static final G.VS NCS = new G.VS(0,0, MAX, MAX);

    public Norm(){
      super(N);
      BUFFER.subSample(this);
      G.V.T.set(BUFFER.bbox, NCS);
      transform();
    }

    public void drawAt(Graphics g, G.VS vs){
      G.V.T.set(NCS, vs);
      for(int i=1; i<N; i++){
        g.drawLine(points[i-1].tx(), points[i-1].ty(), points[i].tx(), points[i].ty());
      }
    }

    //this dist is o for comparison, no need to square root
    public int dist(Norm n){
      int res = 0;
      for (int i=0; i<N; i++){
        int dx = points[i].x - n.points[i].x, dy = points[i].y - n.points[i].y;
        res += (dx*dx + dy*dy);
      }
      return res;
    }

    public void blend(Norm norm, int nBlend){
      for(int i=0; i<N; i++){
        points[i].blend(norm.points[i], nBlend);
      }
    }
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

    public boolean hit(int x, int y) {return true;}

    public void show(Graphics g) {
      // bbox.draw(g);
      drawN(g, n);
    }

    //since handwriting tends to slow down around corner, there would be a cluster of points around corner
    //sampling every nth point can still preserve the corner shape
    public void subSample(G.PL pl){
      int k = pl.size();
      for(int i = 0; i < k; i++){
        pl.points[i].set(this.points[i*(n-1)/(k-1)]);
      }
    }
  }



  //--------------------------------List-------------------------list of ink
  public static class List extends ArrayList<Ink> implements I.Show{

    @Override
    public void show(Graphics g) {for(Ink ink : this) {ink.show(g);}}

  }
}
