package game;

import graphics.G;
import graphics.WinApp;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.Timer;

public class Tetris extends WinApp implements ActionListener {
  public static Timer timer;
  //  Constants can use capital letters
  //  C is cell size, W is how many cells across
  public static final int H = 20, W = 10, C = 25;
  public static int xM = 50, yM = 50;
  public static int time = 1, iShape = 0;
  public static Shape shape;

  public static Color[] color = {Color.RED, Color.GREEN, Color.BLUE, Color.ORANGE, Color.CYAN,
      Color.YELLOW, Color.MAGENTA};
  public static Shape[] shapes = {Shape.Z, Shape.S, Shape.J, Shape.L, Shape.I, Shape.O, Shape.T};

  public Tetris() {
    super("Tetris", 1000, 700);
    shape = shapes[G.rnd(shapes.length)];
    timer = new Timer(30, this);
    timer.start();
  }

  public void paintComponent(Graphics g) {
//    g.setColor(Color.WHITE);
//    g.fillRect(0, 0, 5000, 5000);
    G.bgWhite(g);
//    time++;
//    if(time==60){time = 0; iShape = (iShape+1)%7;}
//    shapes[iShape].show(g);
//    if(time == 30){shapes[iShape].rot();}
    shape.show(g);
  }

  @Override
  public void actionPerformed(ActionEvent ae) {
    repaint();
  }

  public void keyPressed(KeyEvent ke) {
    int vk = ke.getKeyCode();
    //VK =virtual key. Arrow keys does not print anyhting in UNIX code
    if(vk == KeyEvent.VK_LEFT){shape.slide(G.LEFT);}
    if(vk == KeyEvent.VK_RIGHT){shape.slide(G.RIGHT);}
    if(vk == KeyEvent.VK_UP){shape.rot();}
    if(vk == KeyEvent.VK_DOWN){shape.drop();}
    repaint();
  }

  public static void main(String[] args) {
    PANEL = new Tetris();
    WinApp.launch();
  }

  //--------------------------------Shape class------------------------------------
  public static class Shape{
    public static Shape Z, S, J, L, I, O, T;
    public static G.V temp = new G.V(0, 0);
    public G.V[] a = new G.V[4];
    public int iColor;
//    static block
    static {
      Z = new Shape(new int[]{0,0,1,0,1,1,2,1},0);
      S = new Shape(new int[]{0,1,1,0,1,1,2,0},1);
      J = new Shape(new int[]{0,0,0,1,1,1,2,1},2);
      L = new Shape(new int[]{0,1,1,1,2,1,2,0},3);
      I = new Shape(new int[]{0,0,1,0,2,0,3,0},4);
      O = new Shape(new int[]{0,0,1,0,0,1,1,1},5);
      T = new Shape(new int[]{0,1,1,0,1,1,2,1},6);
    }

    public Shape(int[] xy, int iColor) {
      this.iColor = iColor;
      for (int i = 0; i < 4; i++) {
        a[i] = new G.V(xy[2*i], xy[2*i+1]);
      }
    }

    public void show(Graphics g) {
      g.setColor(color[iColor]);
      for (int i = 0; i < 4; i++) {g.fillRect(x(i), y(i), C, C);}
      g.setColor(Color.BLACK);
      for (int i = 0; i < 4; i++) {g.drawRect(x(i), y(i), C, C);}
    }

    public int x(int i) {return xM+C*a[i].x;}
    public int y(int i) {return yM+C*a[i].y;}

    public void rot(){
      temp.set(0,0);
      for (int i = 0; i < 4; i++) {
        a[i].set(-a[i].y, a[i].x);
        if(temp.x>a[i].x){temp.x=a[i].x;}
        if(temp.y>a[i].y){temp.y=a[i].y;}
      }
      temp.set(-temp.x, -temp.y);
      for (int i = 0; i < 4; i++) {a[i].add(temp);}
    }

    //collision detection shape
    public static Shape cds = new Shape(new int[]{0,0,0,0,0,0,0,0}, 0);

    public boolean collisionDetected(){
      for (int i = 0; i < 4; i++) {
        G.V v = cds.a[i];
        if(v.x<0 || v.x>=W || v.y<0 || v.y>=H){return true;}
      }
      return false;
    }

    public void cdsSet(){for (int i = 0; i < 4; i++) {cds.a[i].set(a[i]);}}
    public void cdsGet(){for (int i = 0; i < 4; i++) {a[i].set(cds.a[i]);}}
    public void cdsAdd(G.V v){for(int i = 0; i < 4; i++) {cds.a[i].add(v);}}

    public void slide(G.V dx){
      cdsSet();
      cdsAdd(dx);
      if(collisionDetected()){return;}
      cdsGet();
    }

    public void drop(){
      cdsSet();
      cdsAdd(G.DOWN);
      if(collisionDetected()){return;}
      cdsGet();
    }
  }
}
