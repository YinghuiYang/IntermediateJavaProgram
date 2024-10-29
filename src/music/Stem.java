package music;

import java.awt.Graphics;

public class Stem extends Duration{
  public Staff staff;
  public Head.List heads = new Head.List();
  public boolean isUp = true;

  public Stem(Staff staff, boolean up){
    this.staff = staff;
    isUp = up;
  }

  @Override
  public void show(Graphics g) {
    if(nFlag >= -1 && heads.size() > 0){
      int x = x(), h = staff.fmt.H, yH = yFirstHead(), yB = yBeam();
      g.drawLine(x, yH, x, yB);
    }
  }

  public Head firstHead(){
    return heads.get(isUp ? heads.size()-1 : 0);
  }

  public Head lastHead(){
    return heads.get(isUp ? 0 : heads.size()-1);
  }

  public int yFirstHead(){
    Head h = firstHead();
    return h.staff.yOfLine(h.line);
  }

  public int yBeam(){
    Head h = lastHead();
    int line = h.line;
    line += isUp ? -7 : 7;  //default one octave from head.
    int flagIncrement = nFlag > 2 ? 2*(nFlag - 2) : 0;
    line += isUp ? -flagIncrement : flagIncrement;
    if((isUp && line > 4) || (!isUp && line < 4)){line = 4;}  //hit the center line if possible
    return h.staff.yOfLine(line);
  }

  public int x(){
    Head h = firstHead();
    return h.time.x + (isUp ? h.w() : 0);
  }

  public void deleteStem() {deleteMass();}

  public void setWrongSide() {
    //stub
  }
}
