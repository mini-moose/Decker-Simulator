package matrix.device;

import java.util.ArrayList;
import java.util.Arrays;

import matrix.Host;

public class Drone extends Device{
  public Drone(Host host){
    super(host, DeviceType.DRONE);

    ArrayList<Commands> droneCommands = new ArrayList<>(Arrays.asList(Commands.TARGET, Commands.FIRE, Commands.STAND_DOWN, Commands.SHUTDOWN));
    this.devCommands = droneCommands;

    this.attack = host.rating * 2;
    this.dataProcessing = host.rating * 2;
  }
}
