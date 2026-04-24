package matrix.ic;

import matrix.IC;
import matrix.Host;
import player.Player;

import java.util.ArrayList;
import java.util.Arrays;

public class Acid extends IC implements ICEffect{
  public Acid(Host host){
    super(host, "Acid");
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
