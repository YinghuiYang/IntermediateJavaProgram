package music;

import java.awt.Graphics;

public class Key {
  public static int[]
    sG = {0, 3, -1, 2, 5, 1, 4},  //sharp on clef G
    fG = {4, 1, 5, 2, 6, 3, 7},   //flats on clef G
    sF = {2, 5, 1, 4, 7, 3, 6},   //sharp on clef F
    fF = {6, 3, 7, 4, 8, 5, 9};   //flt on clef F

  public static void drawOnStaff(Graphics g, int n, int[] lines, int x, Glyph glyph, Staff staff) {
    int gap = gapForGlyph(glyph, staff);
    for(int i=0; i<n; i++){
      glyph.showAt(g, staff.fmt.H, x+i*gap, staff.yOfLine(lines[i]));
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
