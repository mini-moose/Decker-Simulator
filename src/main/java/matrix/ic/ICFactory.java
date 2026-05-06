package matrix.ic;

import main.Debug;

import matrix.Host;
import matrix.ic.*;

public class ICFactory {
  public static IC create(ICType type, Host host) {
    switch(type) {
      case ACID:    return new Acid(host);
      case KILLER:  return new Killer(host);
      case JAMMER:  return new Jammer(host);
      case PATROL:  return new Patrol(host);
      default:
        Debug.log("Unknown IC type: " + type);
        return null;
    }
  }
}
