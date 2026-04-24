package matrix;

import matrix.AccessState;

import java.util.UUID;
import java.util.HashMap;

public abstract class MatrixEntity {

  public HashMap<MatrixEntity, AccessState> accessControl;

  public String name;
  public String uniProto = UUID.randomUUID().toString();

  public int attack;
  public int sleaze;
  public int dataProcessing;
  public int firewall;
  public int rating;

  public int attackRating;
  public int defenseRating;

  public int initiative;
  public int devCondition;

  public int isAlive; // 0 for no 1 for yes

  public boolean hasBackdoor;
  public boolean isHidden;

  public MatrixEntity(int rating) {
    this.rating = rating;
    this.attack = rating;
    this.sleaze = rating;
    this.dataProcessing = rating;
    this.firewall = rating;

    this.attackRating = attack + sleaze;
    this.defenseRating = dataProcessing + firewall;

    this.accessControl = new HashMap<>();
  }

  public int getStat(String statName) {
    switch (statName.toLowerCase()) {
      case "initiative":       return initiative;
      case "devcondition":     return devCondition;
      case "attack":           return attack;
      case "sleaze":           return sleaze;
      case "dataprocessing":   return dataProcessing;
      case "firewall":         return firewall;
      case "isalive":          return isAlive;
      case "attackrating":     return attackRating;
      case "defenserating":    return defenseRating;
      default: throw new IllegalArgumentException("Unknown stat: " + statName);
    }
  }

}
