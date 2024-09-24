package game;

import graphics.G;
import graphics.WinApp;
import java.awt.Color;
import java.awt.Graphics;

public class Maze extends WinApp {
  public static final int xSize = 1000, ySize = 700;
  // C for cell size
  public static final int xM = 50, yM = 50, C = 30;
  // number of cells in a row/column
  public static final int W = (xSize-2*xM)/C, H = (ySize-2*yM)/C;
  public static int y;
  public static Graphics gg;

  //double linked list(?), circular list(?)
  static int[] next = new int[W+1], prev = new int[W+1];

  public Maze(){
    super("Maze", xSize, ySize);
  }

  public void paintComponent(Graphics g){
    G.bgWhite(g);
    g.setColor(Color.BLACK);
    gg = g;
    //drawing the first line/row
    hRowZero();
    mid();
    vLast();
    hLast();
  }

  public int x(int i){return xM+i*C;}

  //hLine = horizontal line edge
  public void hLine(int i){gg.drawLine(x(i), y, x(i+1), y); merge(i, i+1);}

  public void vLine(int i){gg.drawLine(x(i), y, x(i), y+C);}

  public void merge(int i, int j){
    int pi = prev[i], pj = prev[j];
    next[pj] = i; next[pi] = j;
    prev[i] = pj; prev[j] = pi;
  }

  public void split(int i){
    int pi = prev[i], ni = next[i];
    next[pi] = ni; prev[ni] = pi;
    next[i] = i; prev[i] = i;
  }

  public void singletonCycle(int i){next[i] = i; prev[i] = i;}

  // this is the order(n) operation. Not the most efficient maze algorithm
  public boolean sameCycle(int i, int j){
    int n = next[i];
    while(n != i){
      if(n == j) {return true;}
      n = next[n];
    }
    return false;
  }

  //pV = probability of vertical connection
  public static boolean pV(){return G.rnd(100) < 33;}

  public static boolean pH(){return G.rnd(100) < 47;}

  //horizontal rule
  public void hRule(int i){if(!sameCycle(i, i+1) && pH()){hLine(i);}}

  public void vRule(int i){
    if(next[i] == i || pV()){
      vLine(i);
    } else{
      noVLine(i);
    }
  }

  public void noVLine(int i){
    split(i);
  }

  //horizontal connections on each row
  public void hRow(){for(int i = 0; i < W; i++){hRule(i);}}

  //vertical connections on each row
  public void vRow(){vLine(0); for(int i = 1; i < W; i++){vRule(i);} vLine(W);}

  public void hRowZero(){
    y = yM;
    singletonCycle(0);
    for(int i = 0; i < W; i++){
      singletonCycle(i+1);
      hLine(i);
    }
  }

  public void mid(){
    for (int i = 0; i < H-1; i++){
      vRow();
      y += C;
      hRow();
    }
  }

  public void vLast(){
    vLine(0);
    vLine(W);
    for(int i = 1; i<W; i++){
      if(!sameCycle(i, 0)){
        merge(i, 0);
        vLine(i);
      }
    }
  }

  public void hLast(){
    y += C;
    for(int i = 0; i < W; i++){
      hLine(i);
    }
  }

  public static void main(String[] args) {
    PANEL = new Maze();
    WinApp.launch();
  }

}
