package sandbox;

import graphics.G;
import graphics.WinApp;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import music.UC;
import reaction.Ink;
import reaction.Shape;
import reaction.Shape.Prototype;

public class PaintInk extends WinApp {
  public static Ink.List inkList = new Ink.List();
  public static Shape.Prototype.List pList = new Shape.Prototype.List();

  public PaintInk(){
    super("Paint Ink", UC.screenWidth, UC.screenHeight);
//    inkList.add(new Ink());
  }

  public void paintComponent(Graphics g){
    G.bgWhite(g);
//    g.setColor(Color.RED);
//    g.fillRect(100, 100, 100, 100);
    g.setColor(Color.BLACK);
    inkList.show(g);
    Ink.BUFFER.show(g);
    g.drawString("Points: " + Ink.BUFFER.n, 600, 300);
    if(inkList.size()>1){
      int last = inkList.size()-1;
      int dist = inkList.get(last).norm.dist(inkList.get(last-1).norm);
      g.setColor(dist>UC.noMatchDist ? Color.RED:Color.BLACK);
      g.drawString("Dist: " + dist, 600, 60);
    }
    pList.show(g);
  }

  //start drawing a line.
  public void mousePressed(MouseEvent me){Ink.BUFFER.dn(me.getX(), me.getY()); repaint();}

  public void mouseDragged(MouseEvent me){Ink.BUFFER.drag(me.getX(), me.getY()); repaint();}

  //finish drawing a line
  public void mouseReleased(MouseEvent me){
    Ink.BUFFER.up(me.getX(), me.getY());
    Ink ink = new Ink();
    inkList.add(ink);
    Shape.Prototype proto;
    if(pList.bestDist(ink.norm) < UC.noMatchDist){
      proto = Shape.Prototype.List.bestMatch;
      proto.blend(ink.norm);
    } else {
      proto = new Shape.Prototype();
      pList.add(proto);
    }
    ink.norm = proto;
    repaint();
  }

  public static void main(String[] args) {
    PANEL = new PaintInk();
    WinApp.launch();
  }

}
