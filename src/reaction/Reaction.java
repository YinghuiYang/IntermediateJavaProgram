package reaction;

import java.util.ArrayList;
import music.I;

public abstract class Reaction implements I.React{
  public Shape shape;



  //----------------List--------------------------
  public static class List extends ArrayList<Reaction> {

  }
}
