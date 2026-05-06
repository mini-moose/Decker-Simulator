package matrix.device;

import java.util.ArrayList;
import java.util.Arrays;

import matrix.Host;

public class Door extends Device{
  public Door(Host host){
    super(host, DeviceType.DOOR);

    ArrayList<Commands> doorCommands = new ArrayList<>(Arrays.asList(Commands.OPEN, Commands.CLOSE, Commands.LOCK, Commands.UNLOCK));
    this.devCommands = doorCommands;

    this.firewall = host.rating * 2;
  }
}
