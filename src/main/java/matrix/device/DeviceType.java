package matrix.device;

public enum DeviceType {
  ALARM("Alarm"),
  CAMERA("Camera"),
  DOOR("Door"),
  DRONE("Drone");


  private final String label;

  DeviceType(String label) {
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
