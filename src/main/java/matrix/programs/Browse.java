package matrix.programs;

import player.Player;

public class Browse extends Program {

  @Override
  public Integer getCost() { return 1500; }

  @Override
  public String getName() { return "Browse"; }

  @Override
  public String getDescription() { return "Increase your dice pool for Search actions."; }

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
