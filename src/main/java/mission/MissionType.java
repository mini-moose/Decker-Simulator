package mission;

public enum MissionType {
  DATA_EXFIL("Data Exfiltration"),
  MATRIX_ASSIST("Matrix Assist"),
  SABOTAGE("Sabotage"),
  //SPIDER_HUNT("Spider Hunt"),
  DATA_TAMPER("Data Tamper");

  private final String label;

  MissionType(String label) {
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
