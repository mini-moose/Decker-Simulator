package matrix.device;

import main.Debug;
import matrix.Host;

public class DeviceFactory {
  public static Device create(Host host, DeviceType type) {
    switch(type) {
      case ALARM:    return new Alarm(host);
      case CAMERA:  return new Camera(host);
      case DOOR:  return new Door(host);
      case DRONE:  return new Drone(host);
      default:
        Debug.log("Unknown Device type: " + type);
        return null;
    }
  }
}
