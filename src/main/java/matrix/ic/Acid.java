package matrix.ic;

import java.util.ArrayList;
import java.util.Arrays;

import matrix.Host;
import player.Player;

public class Acid extends IC implements ICEffect{
  public Acid(Host host){
    super(host, ICType.ACID);
    this.description = "Specialized Attack IC that lowers target's Firewall on hit.";

    // Assign player defense stats for attacks from this IC per player guide
    this.playerDefenseStats = new ArrayList<>(Arrays.asList("willpower", "firewall"));
  }

  // Apply damage as damage to the player's firewall stat.
  @Override 
  public void applyEffect(Player player, int netHits){
    player.firewall -= netHits;
    System.out.println(">> WARNING [FIREWALL_WARNING]: Firewall degraded by: -" + netHits);
  }

  @Override
  public String getEffectDescription(){
    return "Your Firewall took damage";
  }
}
