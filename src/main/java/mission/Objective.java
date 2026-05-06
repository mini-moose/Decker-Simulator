package mission;

public class Objective {
  public String description;
  public boolean isComplete = false;
  public ObjectiveType type;

  public Objective(ObjectiveType type, String description){
    this.type = type;
    this.description = description;
  }

  public void complete(){
    isComplete = true;
    System.out.println("[INFO] OBJECTIVE_COMPLETE: " + description);
  }

  @Override
  public String toString(){
    return (isComplete ? "[X]" : "[ ]") + description;
  }
}
