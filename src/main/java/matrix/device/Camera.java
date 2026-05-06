package matrix.device;

import java.util.ArrayList;
import java.util.Arrays;

import matrix.Host;

public class Camera extends Device{
  public Camera(Host host){
    super(host, DeviceType.CAMERA);

    ArrayList<Commands> cameraCommands = new ArrayList<>(Arrays.asList(Commands.RECORD, Commands.STOP_RECORD, Commands.PLAYBACK, Commands.SHUTDOWN));
    this.devCommands = cameraCommands;

    this.dataProcessing = host.rating * 2;
  }
}
