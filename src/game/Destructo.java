package game;

import graphics.G;
import graphics.WinApp;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.swing.Timer;
import music.UC;

public class Destructo extends WinApp implements ActionListener {
  //light gray will be the new background color
  public static Color[] color = {Color.LIGHT_GRAY, Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED, Color.PINK};
  public static final int numCol = 13, numRow = 15;
  public static final int width = 60, height = 40;
  //xM = x margin, the side empty space. yM, y margin, the top empty space
  public static int xM = 100, yM = 100;
  public static int[][] grid = new int[numCol][numRow];
  public static int bricksRemaining;
  public static int newGameBoxX = 0, newGameBoxY = 0, newGameBoxWidth = 10, newGameBoxHeight = 10;

  public static Timer timer;

  public Destructo() {
    super("Destructo", UC.screenWidth, UC.screenHeight);
    startNewGame();
    timer = new Timer(20, this);
    timer.start();
  }

  public static int nColor = 3;
  public static void startNewGame(){
    if(nColor==color.length){
      nColor = 3;
    }
    rndColors(nColor);
    initRemaining();
    xM = 100;
    nColor++;
  }

  public void paintComponent(Graphics g) {
    g.setColor(color[0]);
    g.fillRect(0, 0, 5000, 5000);
    showGrid(g);
    bubble();
    boolean temp = slideCol();
    if(temp){
      xM+=width/2;
    }
    showRemaining(g);

    //"New/r/nGame will not get g.drawString to print with new line. It will ignore whitespace
    String str = "New Game";
    FontMetrics fm = g.getFontMetrics();
    int w = fm.stringWidth(str);
    int h = fm.getHeight();
    int wMargin = 5;
    int hMargin = 3;
    g.setColor(Color.PINK);
    newGameBoxWidth = w+2*wMargin;
    newGameBoxHeight = h+2*hMargin;
    g.fillRect(newGameBoxX, newGameBoxY, newGameBoxWidth,newGameBoxHeight);
    g.setColor(Color.BLACK);
    g.drawString(str, wMargin, h);
  }

  public void mouseClicked(MouseEvent me) {
    int x = me.getX(), y = me.getY();
    //click on top right corner to start a new game;
    //how to make the startNewGame() at the location of New Game box?
    if(x>newGameBoxX && x<newGameBoxX+newGameBoxWidth && y>newGameBoxY && y<newGameBoxY+newGameBoxHeight){
      startNewGame();
      return;
    }
    //clicking on meaningless coordinates
    if(x<xM || y<yM) {return;}
    int c=c(x), r=r(y);
    //clicking on illegal coordinates
    if(c<numCol && r<numRow) {crAction(c, r);}
    repaint();
  }

  public static boolean noMorePlays(){
    for(int c=0; c<numCol; c++){
      for(int r=0; r<numRow; r++){
        if(infectable(c, r)){return false;}
      }
    }
    return true;
  }

  public static void initRemaining(){bricksRemaining = numCol*numRow;}

  public static void showRemaining(Graphics g){
    String str = "Bricks Remaining: " + bricksRemaining +".";
    if(noMorePlays()){str+=" No more plays left!";}
    g.setColor(Color.BLACK);
    g.drawString(str, 100, 25);
  }

  public static void crAction(int c, int r) {
    System.out.println("("+c+","+r+")");
    if(infectable(c,r)) {
      infect(c, r, grid[c][r]);
    }
  }

  //choose a random color index based on the class variable Color[] color
  //the index 0 is the background color index, so only size-1 colors available
  //so k has to be less than size-1
  public static void rndColors(int k){
    for(int c=0; c<numCol; c++){
      for(int r=0; r<numRow; r++){
        grid[c][r] = 1+G.rnd(k);
      }
    }
  }

  public static void showGrid(Graphics g){
    for(int c=0; c<numCol; c++){
      for(int r=0; r<numRow; r++){
        g.setColor(color[grid[c][r]]);
        g.fillRect(x(c), y(r), width, height);
      }
    }
  }

  public static boolean infectable(int c, int r){
    int v = grid[c][r];
    if (v==0){return false;}
    if(c>0){if(grid[c-1][r]==v){return true;}}
    if(r>0){if(grid[c][r-1]==v){return true;}}
    if(c<numCol){if(grid[c+1][r]==v){return true;}}
    if(r<numRow-1){if(grid[c][r+1]==v){return true;}}
    return false;
  }

  public static void infect(int c, int r, int v){
    if(grid[c][r]!=v){return;}
    grid[c][r]=0;
    bricksRemaining--;
    if(c>0){infect(c-1,r,v);}
    if(r>0){infect(c,r-1,v);}
    if(c<numCol-1){infect(c+1,r,v);}
    if(r<numRow-1){infect(c,r+1,v);}
  }

  public static int x(int c){return xM+c*width;}
  public static int y(int r){return yM+r*height;}

  //unsafe, might have leftover not enough for division. fixed in mouseClicked.
  public static int c(int x){return (x-xM)/width;}
  public static int r(int y){return (y-yM)/height;}

  public static int rowCanBubble(int c){
    for(int r= numRow-1; r>0; r--){
      //the row is background color and the row above is not
      if(grid[c][r]==0 && grid[c][r-1]!=0){return r;}
    }
    //return an illegal row value
    return numRow;
  }

  public static void bubble(){
    for(int c=0; c<numCol; c++){
      int r = rowCanBubble(c);
      if(r < numRow){
        grid[c][r]=grid[c][r-1];
        grid[c][r-1]=0;
      }
    }
  }

  public boolean colIsEmpty(int c){
    for(int r=0; r<numRow; r++){
      if(grid[c][r]!=0){return false;}
    }
    return true;
  }

  public void swapCol(int c){ //c is nonempty, c-1 is empty
    for(int r=0; r<numRow; r++){
      grid[c-1][r] = grid[c][r];
      grid[c][r]=0;
    }
  }

  public boolean slideCol(){
    boolean res = false;
    for (int c = 1; c < numCol; c++) {
      if(colIsEmpty(c-1) && !colIsEmpty(c)) {
        swapCol(c);
        res = true;
      }
    }
    return res;
  }

  //this is how we do animation. Every time timer ticks, repaint.
  public void actionPerformed (ActionEvent e) {
    repaint();
  }

  public static void main(String[] args) {PANEL=new Destructo(); WinApp.launch();}
}
