package matrix.actions;

public enum EdgeType {
  OVERCLOCK("Overclock"),
  SCRAMBLE("Scramble"),
  SHIELD("Shield");

  private final String label;

  EdgeType(String label) {
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
