package matrix.programs;

import player.Player;

public class Toolbox extends Program {

  @Override
  public Integer getCost() { return 1000; }

  @Override
  public String getName() { return "Toolbox"; }

  @Override
  public String getDescription() { return "Increase Data Processing +1"; }

  @Override
  public boolean isIllegal() { return false; }

  @Override
  public void applyProgramEffect(Player player) {
    player.dataProcessing += 1;
    this.isInstalled = true;
  }

  @Override
  public void removeProgramEffect(Player player) {
    player.dataProcessing -= 1;
    this.isInstalled = false;
  }
}
