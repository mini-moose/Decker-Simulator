package matrix.ic;

import matrix.ic.IC;
import matrix.ic.ICType;
import matrix.Host;
import player.Player;

import java.util.ArrayList;
import java.util.Arrays;

public class Jammer extends IC implements ICEffect{
  public Jammer(Host host){
    super(host, ICType.JAMMER);
    this.description = "Specialized Attack IC that lowers target's Attack on hit.";

    // Assign player defense stats for attacks from this IC per player guide
    this.playerDefenseStats = new ArrayList<>(Arrays.asList("willpower", "attack"));
  }

  // Apply damage as damage to the player's firewall stat.
  @Override 
  public void applyEffect(Player player, int netHits){
    player.attack -= netHits;
    System.out.println(">> [WARNING] ATTACK_WARNING: Attack degraded by: -" + netHits);
  }

  @Override
  public String getEffectDescription(){
    return "Your Attack took damage";
  }

}
