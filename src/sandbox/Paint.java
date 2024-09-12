package sandbox;

import graphics.G;
import graphics.WinApp;

//awt = abstract windows tools
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Paint extends WinApp {
    public static int repaint = 0;

    public static int clicks = 0;

    public static Path thePath = new Path();

    public static Pic thePic = new Pic();

    public Paint (){
        super("paint", 1000, 750);
    }

    //if there is no function to override, the IDE will complain.
    @Override
    public void paintComponent(Graphics g){
        //g.setColor(Color.WHITE);
        //g.fillRect(0, 0, 5000, 5000);

        repaint++;

        //the capital G is a class containing helper functions
        G.bgWhite(g);

        g.setColor(Color.black);
        //thePath.draw(g);
        thePic.draw(g);

        /*
        //g.setColor(Color.RED);
        //g.fillRect(100, 100, 100, 100);
        //g.drawRect(100, 100, 100, 100);
        //g.drawOval(100, 100, 100, 100);
        //g.drawOval(100, 100, 200, 100);
        g.setColor(G.rndColor());
        g.fillOval(100, 100, 200, 100);

        g.setColor(Color.BLUE);
        g.drawLine(100, 400, 400, 100);

        g.setColor(Color.BLACK);
        //g.drawString("I want lunch now", 400, 200);
        int x = 400, y = 200;

        //String msg = "I want lunch now. " + repaint;
        String msg = "I want lunch now. " + clicks;
        g.drawString(msg, x, y);

        g.setColor(Color.RED);
        g.drawOval(x, y, 3, 3);

        FontMetrics fm = g.getFontMetrics();
        int a = fm.getAscent(), h = fm.getHeight();
        int w = fm.stringWidth(msg);
        g.drawRect(x, y-a, w, h);
        */
    }

    @Override
    public void mousePressed(MouseEvent me){
        clicks++;
//        thePath.clear();
//        thePath.add(me.getPoint());
        thePath = new Path();
        thePath.add(me.getPoint());
        thePic.add(thePath);
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent me){
        thePath.add(me.getPoint());
        repaint();
    }

    public static void main(String[] args){
        PANEL = new Paint();
        WinApp.launch();
    }

    //-------------------path--------------------------
    public static class Path extends ArrayList<Point> {
        public void draw(Graphics g){
            for (int i=1; i<size(); i++){
                Point p = get(i-1), n = get(i);
                g.drawLine(p.
                    x, p.y, n.x, n.y);
            }
        }
    }

    public static class Pic extends ArrayList<Path> {
        public void draw(Graphics g){
            for (Path p: this){
                p.draw(g);
            }
        }
    }
}
