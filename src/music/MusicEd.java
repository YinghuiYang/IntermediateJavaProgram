package music;

import graphics.G;
import graphics.WinApp;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.GenericArrayType;
import reaction.Gesture;
import reaction.Ink;
import reaction.Layer;
import reaction.Shape;

// music editor
public class MusicEd extends WinApp {
  public static Layer BACK = new Layer("BACK"), FORE = new Layer("FORE");
  public static boolean training = false;
  public static I.Area curArea = Gesture.AREA;
  public static Page PAGE;

  public MusicEd(){
    super("Music Editor", UC.screenWidth, UC.screenHeight);
  }

  public void paintComponent(Graphics g){
    G.bgWhite(g);
    if(training){
      Shape.TRAINER.show(g);
      return;
    }
    g.setColor(Color.BLACK);
    Ink.BUFFER.show(g);
    Layer.ALL.show(g);
    g.drawString(Gesture.recognized, 900, 30);
  }

  public void trainButton(MouseEvent me){
    if(me.getX() > UC.screenWidth-40 && me.getY() < 40){
      training = !training;
      curArea = training ? Shape.TRAINER : Gesture.AREA;
    }
  }

  public void mousePressed(MouseEvent me){
    curArea.dn(me.getX(), me.getY());
    repaint();
  }

  public void mouseDragged(MouseEvent me){
    curArea.drag(me.getX(), me.getY());
    repaint();
  }

  public void mouseReleased(MouseEvent me){
    curArea.up(me.getX(), me.getY());
    trainButton(me);
    repaint();
  }

  public void keyTyped(KeyEvent ke){
    if(training){
      Shape.TRAINER.keyTyped(ke);
      repaint();
    }
  }

  public static void main(String[] args){
    PANEL = new MusicEd();
    WinApp.launch();
  }
}
