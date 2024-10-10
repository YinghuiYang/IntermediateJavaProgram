package reaction;

import graphics.G;
import music.I;
import music.I.Area;

public class Gesture {
  public static I.Area AREA = new Area() {

    @Override
    public boolean hit(int x, int y) {
      return true;
    }

    @Override
    public void dn(int x, int y) {
      Ink.BUFFER.dn(x, y);
    }

    @Override
    public void drag(int x, int y) {
      Ink.BUFFER.drag(x, y);
    }

    @Override
    public void up(int x, int y) {
      Ink.BUFFER.up(x, y);
      Ink ink = new Ink();
      Gesture gest = Gesture.getNew(ink); //can fail if unrecognized
      Ink.BUFFER.clear();
      recognized = gest == null ? "NULL":gest.shape.name;
      if(gest != null){
        Reaction r = Reaction.best(gest); //can fail, possibly no Reaction want this
        if(r != null){
          r.act(gest);
        } else {
          recognized += " no bid";
        }
      }
    }
  };

  public static String recognized = "NULL";
  public Shape shape;
  public G.VS vs;

  private Gesture(Shape shape, G.VS vs) {
    this.shape = shape;
    this.vs = vs;
  }

  public static Gesture getNew(Ink ink){
    Shape s = Shape.recognize(ink);
    return s == null ? null:new Gesture(s, ink.vs);
  }
}
