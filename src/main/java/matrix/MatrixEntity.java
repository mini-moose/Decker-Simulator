package matrix;

import java.util.UUID;

public abstract class MatrixEntity {
  public int attack;
  public int sleaze;
  public int dataProcessing;
  public int firewall;
  public int rating;

  public int initiative;
  public int devCondition;

  public int isAlive; // 0 for no 1 for yes

  public String uniProto = UUID.randomUUID().toString();

  public String name;

  public boolean hasBackdoor;
  public boolean isHidden;

  public MatrixEntity(int rating) {
    this.rating = rating;
    this.attack = rating;
    this.sleaze = rating;
    this.dataProcessing = rating;
    this.firewall = rating;
  }

  public int getStat(String statName) {
    switch (statName.toLowerCase()) {
      case "initiative":       return initiative;
      case "devCondition":     return devCondition;
      case "attack":           return attack;
      case "sleaze":           return sleaze;
      case "dataprocessing":   return dataProcessing;
      case "firewall":         return firewall;
      case "isalive":          return isAlive;
      default: throw new IllegalArgumentException("Unknown stat: " + statName);
    }
  }

}
