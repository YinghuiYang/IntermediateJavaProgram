package music;

import java.awt.Graphics;

public class Key {
  public int n = 0; //between -7 to 7, counting sharps(positive number) and flats(negative number)
  public Glyph glyph = Glyph.SHARP; //flat, sharp, or natural

  public static int[]
    sG = {0, 3, -1, 2, 5, 1, 4},  //sharp on clef G
    fG = {4, 1, 5, 2, 6, 3, 7},   //flats on clef G
    sF = {2, 5, 1, 4, 7, 3, 6},   //sharp on clef F
    fF = {6, 3, 7, 4, 8, 5, 9};   //flat on clef F

  public static void drawOnStaff(Graphics g, int n, int[] lines, int x, Glyph glyph, Staff staff) {
    int gap = gapForGlyph(glyph, staff);
    for(int i=0; i<n; i++){
      glyph.showAt(g, staff.fmt.H, x+i*gap, staff.yOfLine(lines[i]));
    }
  }

  public void drawOnSys(Graphics g, Sys sys, int x){
    for(Staff staff:sys.staffs){
      if(n == 0){return;}
      int[] array;
      boolean isG = (staff.clefAtX(x) == Glyph.CLEF_G);
      if(n > 0){
        array = isG ? sG : sF;
      } else {
        array = isG ? fG : fF;
      }
      drawOnStaff(g, Math.abs(n), array, x, glyph, staff);
    }
  }

  private static int gapForGlyph(Glyph glyph, Staff staff) {
    int H = staff.fmt.H;
    if(glyph == Glyph.SHARP){
      return 22*8/H;
    }
    if(glyph == Glyph.FLAT){
      return 18*8/H;
    }
    //Glyph Natural
    return 16*8/H;
  }

}
