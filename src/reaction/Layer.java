package reaction;

import java.awt.Graphics;
import java.util.ArrayList;
import music.I;

public class Layer extends ArrayList<I.Show> implements I.Show {
  public String name;

  public Layer(String name) {
    this.name = name;
  }

  @Override
  public void show(Graphics g) {for(I.Show item : this) {item.show(g);}}}
