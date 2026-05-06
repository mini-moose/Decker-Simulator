package matrix.ic;

import java.util.ArrayList;

import matrix.Host;
import matrix.MatrixEntity;

public class IC extends MatrixEntity {
  public String description;
  
  public int attackRating;

  public ICType icType;

  public ArrayList<String> playerDefenseStats = new ArrayList<>();

  public IC(Host host, ICType icType) {
    super(host.rating);
    this.name = icType.toString();
    this.icType = icType;

    this.attack = host.rating * 2;
    this.devCondition = host.rating * 2;

    // Storing initiative so that it can be set after initiative roll.
    this.initiative = 0;

    this.firewall = host.firewall;
    this.dataProcessing = host.dataProcessing;
    this.sleaze = host.sleaze;
  }
}