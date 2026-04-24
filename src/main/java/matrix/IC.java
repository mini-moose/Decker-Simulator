package matrix;

import matrix.MatrixEntity;
import matrix.Host;
import player.Player;

import java.util.ArrayList;
import java.util.Arrays;

public class IC extends MatrixEntity {
  public int attackRating;
  public String icType;

  public ArrayList<String> playerDefenseStats = new ArrayList<String>();

  public IC(Host host, String icType) {
    super(host.rating);
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