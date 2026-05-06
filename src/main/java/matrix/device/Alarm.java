package matrix.device;

import java.util.ArrayList;
import java.util.Arrays;

import matrix.Host;

public class Alarm extends Device{
  public Alarm(Host host){
    super(host, DeviceType.ALARM);

    ArrayList<Commands> alarmCommands = new ArrayList<>(Arrays.asList(Commands.ALARM_ON, Commands.ALARM_OFF, Commands.SNOOZE));
    this.devCommands = alarmCommands;

    this.firewall = host.rating * 2;
  }
}
