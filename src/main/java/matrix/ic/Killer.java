package matrix.ic;

import matrix.ic.IC;
import matrix.ic.ICType;
import matrix.Host;
import player.Player;

import java.util.ArrayList;
import java.util.Arrays;

public class Killer extends IC implements ICEffect{
  public Killer(Host host){
    super(host, ICType.KILLER);
    this.description = "Attack IC that deals damage directly to your device.";

    // Assign player defense stats for attacks from this IC per player guide
    this.playerDefenseStats = new ArrayList<>(Arrays.asList("intuition", "firewall"));
  }

  // Apply damage as damage to the player's firewall stat.
  @Override 
  public void applyEffect(Player player, int netHits){
    player.devCondition -= netHits;
    System.out.println(">> [WARNING] Your Deck has taken damage: " + netHits);
  }

  @Override
  public String getEffectDescription(){
    return "Your Deck took damage";
  }

}
