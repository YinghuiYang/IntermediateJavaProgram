package reaction;

import java.awt.Graphics;
import music.I;

public abstract class Mass extends Reaction.List implements I.Show{
  public Layer layer;

  public Mass(String layerName){
    this.layer = Layer.byName.get(layerName);
    if(layer != null){
      layer.add(this);
    } else {
      System.out.println("Bad layer name: " + layerName);
    }
  }

  public void deleteMass(){
    clearAll();
    layer.remove(this);
  }

  public void show(Graphics g){
    //stub
  }

  //fix bug that shows up when removing Mass as I.show from Layers
  private static int M = 1;
  private int hash = M++;

  @Override
  public int hashCode(){return hash;}

  @Override
  public boolean equals(Object obj){return this == obj;}

}
