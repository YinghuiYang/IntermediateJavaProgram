package sandbox;

import graphics.G;
import graphics.WinApp;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import music.UC;
import reaction.Ink;

public class PaintInk extends WinApp {

  public static Ink.List inkList = new Ink.List();

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
  }

  //start drawing a line.
  public void mousePressed(MouseEvent me){Ink.BUFFER.dn(me.getX(), me.getY()); repaint();}

  public void mouseDragged(MouseEvent me){Ink.BUFFER.drag(me.getX(), me.getY()); repaint();}

  //finish drawing a line
  public void mouseReleased(MouseEvent me){
    Ink.BUFFER.up(me.getX(), me.getY());
    inkList.add(new Ink());
    repaint();
  }

  public static void main(String[] args) {
    PANEL = new PaintInk();
    WinApp.launch();
  }

}
