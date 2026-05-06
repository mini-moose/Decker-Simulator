package matrix.ic;

public enum ICType {
  ACID("Acid"),
  BINDER("Binder"),
  BLACK_IC("Black IC"),
  BLASTER("Blaster"),
  CRASH("Crash"),
  JAMMER("Jammer"),
  KILLER("Killer"),
  MARKER("Marker"),
  PATROL("Patrol"),
  SCRAMBLE("Scramble"),
  SPARKY("Sparky"),
  TARPIT("Tarpit"),
  TRACK("Track");

  private final String label;

  ICType(String label) {
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
