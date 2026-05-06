package mission;

import matrix.Host;

import mission.MissionType;
import mission.Objective;

import java.util.ArrayList;

public class Mission {
  public String targetCorp;

  public int rating;
  public int reward;

  public MissionType type;

  public ArrayList<Objective> objectives = new ArrayList<>();
  public Host targetHost;

  public Mission(String targetCorp, MissionType type, int rating, int reward) {
    this.targetCorp = targetCorp;
    this.type = type;
    this.rating = rating;
    this.reward = reward;
  }

  public void setTargetHost(Host targetHost){
    this.targetHost = targetHost;
  }

  public void addObjective(Objective objective){
    objectives.add(objective);
  }

  public boolean isComplete(){
    for (Objective obj : objectives){
      if (!obj.isComplete) return false;
    }
    return true;
  }

  public void printObjectives(){
    System.out.println("======== Mission Objectives ========");
    for (Objective obj : objectives){
      System.out.println("  " + obj);
    }
    System.out.println("====================================");
  }

  @Override
  public String toString() {
    return String.format("[%d] Target: %s | Job: %s | Payout: %d¥", rating, targetCorp, type.toString(), reward);
  }
}
