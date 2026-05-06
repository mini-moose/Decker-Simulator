package matrix.device;

import java.util.ArrayList;
import java.util.Random;

import matrix.Host;
import matrix.MatrixEntity;

public class Device extends MatrixEntity {

  public ArrayList<Commands> devCommands = new ArrayList<>();

  public Device(Host host, DeviceType devType) {
    super(host.rating);
    this.name = devType.toString() + "_" + new Random().nextInt(99);
    this.accessControl = host.accessControl;

    this.devCondition = host.rating * 2;

    this.attack = host.rating;
    this.firewall = host.rating;
    this.dataProcessing = host.rating;
    this.sleaze = host.rating;
  }
}