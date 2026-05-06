package matrix.programs;

import player.Player;

public abstract class Program {
  public boolean isInstalled = false;

  public abstract Integer getCost();

  public abstract String getName();
  public abstract String getDescription();

  public abstract boolean isIllegal();

  public void applyProgramEffect(Player player){
    System.out.println(">> Program: " + this.getName() + " installed. " + this.getDescription() + ".");
  }

  public void removeProgramEffect(Player player){
    System.out.println(">> Program: " + this.getName() + " removed. No longer applying: " + this.getDescription() + ".");
  }
}
