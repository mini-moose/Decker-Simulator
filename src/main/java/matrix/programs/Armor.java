package matrix.programs;

import player.Player;

public class Armor extends Program {

  @Override
  public Integer getCost() { return 1500; }

  @Override
  public String getName() { return "Browse"; }

  @Override
  public String getDescription() { return "Increase your Firewall stat by +2."; }

  @Override
  public boolean isIllegal() { return false; }

  @Override
  public void applyProgramEffect(Player player) {
    player.firewall += 2;
    this.isInstalled = true;
  }

  @Override
  public void removeProgramEffect(Player player) {
    player.firewall -= 2;
    this.isInstalled = false;
  }
}