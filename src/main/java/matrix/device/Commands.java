package matrix.device;

public enum Commands {

  SHUTDOWN("shutdown"),

  // ALARM COMMANDS
  ALARM_ON("alarm_on"),
  ALARM_OFF("alarm_off"),
  SNOOZE("alarm_snooze"),
  // CAMERA COMMANDS
  RECORD("camera_record"),
  STOP_RECORD("camera_stop_record"),
  PLAYBACK("camera_playback"),
  // DOOR COMMANDS
  OPEN("door_open"),
  CLOSE("door_close"),
  LOCK("door_lock"),
  UNLOCK("door_unlock"),
  // DRONE COMMANDS
  TARGET("drone_target"),
  FIRE("drone_fire"),
  STAND_DOWN("drone_stand_down");

  private final String label;

  Commands(String label) {
    this.label = label;
  }

  public String getLabel(){
    return label;
  }

  @Override
  public String toString(){
    return this.label;
  }
}