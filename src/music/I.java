package music;

import java.awt.Graphics;

//C# all interface names start with capital I, I stands for interface. The first capital letter indicates the variable type.
public interface I {
  public interface Draw{public void draw(Graphics g);}
  public interface Hit{public boolean hit(int x, int y);}
  public interface Area extends Hit{
    //dn for down
    public void dn(int x, int y);
    public void drag(int x, int y);
    public void up(int x, int y);
  }
}
