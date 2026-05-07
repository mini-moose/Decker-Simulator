package matrix.programs;

import player.Player;

public class Decrypt extends Program {

  @Override
  public Integer getCost() { return 2500; }

  @Override
  public String getName() { return "Editor"; }

  @Override
  public String getDescription() { return "Increase your dice pool for Edit actions."; }

  @Override
  public boolean isIllegal() { return false; }

  @Override
  public void applyProgramEffect(Player player) {
    this.isInstalled = true;
  }

  @Override
  public void removeProgramEffect(Player player) {
    this.isInstalled = false;
  }
}