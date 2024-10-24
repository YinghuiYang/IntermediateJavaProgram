package music;

import graphics.G;
import graphics.WinApp;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import reaction.Gesture;
import reaction.Ink;
import reaction.Layer;
import reaction.Reaction;
import reaction.Shape;

// music editor
public class MusicEd extends WinApp {

//  public static Layer BACK = new Layer("BACK"), FORE = new Layer("FORE");

  //static block, for initialization purpose
  static{
    new Layer("BACK");
    new Layer("NOTE");
    new Layer("FORE");
  }
  public static boolean training = false;
  public static I.Area curArea = Gesture.AREA;
  public static Page PAGE;

  public MusicEd() {
    super("Music Editor", UC.screenWidth, UC.screenHeight);

    Reaction.initialReactions.addReaction(new Reaction("W-W") {
      public int bid(Gesture g) {
        return 0;
      }

      public void act(Gesture g) {
        int y = g.vs.yM();
        PAGE = new Page(y);
        this.disable();
      }
    });
  }

  public void paintComponent(Graphics g) {
    G.bgWhite(g);
    if (training) {
      Shape.TRAINER.show(g);
      return;
    }
    g.setColor(Color.BLACK);
    Ink.BUFFER.show(g);
    Layer.ALL.show(g);
    g.drawString(Gesture.recognized, 900, 30);
    if (PAGE != null) {
      Glyph.CLEF_G.showAt(g, 8, 100, PAGE.margins.top+4*8);
      Glyph.HEAD_W.showAt(g, 8, 200, PAGE.margins.top+4*8);
    }
  }

  public void trainButton(MouseEvent me) {
    if (me.getX() > UC.screenWidth - 40 && me.getY() < 40) {
      training = !training;
      curArea = training ? Shape.TRAINER : Gesture.AREA;
    }
  }

  public void mousePressed(MouseEvent me) {
    curArea.dn(me.getX(), me.getY());
    repaint();
  }

  public void mouseDragged(MouseEvent me) {
    curArea.drag(me.getX(), me.getY());
    repaint();
  }

  public void mouseReleased(MouseEvent me) {
    curArea.up(me.getX(), me.getY());
    trainButton(me);
    repaint();
  }

  public void keyTyped(KeyEvent ke) {
    if (training) {
      Shape.TRAINER.keyTyped(ke);
      repaint();
    }
  }

  public static void main(String[] args) {
    PANEL = new MusicEd();
    WinApp.launch();
  }
}
