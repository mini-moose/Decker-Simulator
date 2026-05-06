package matrix.ic;

import matrix.Host;
import matrix.ic.IC;
import matrix.ic.ICType;

import player.Player;

import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

public class Patrol extends IC implements ICEffect {

  public Patrol(Host host){
    super(host, ICType.PATROL);
    this.description = "Surveillance IC that is used to detect intruders in Matrix areas.\nWill search for intruders on detecting Illegal Actions, or otherwise once every minute.";

    this.lastSearch = new Random().nextInt(40);

    this.playerDefenseStats = new ArrayList<>(Arrays.asList("willpower", "firewall"));
  }
  @Override
  public void applyEffect(Player player, int netHits){
    player.isHidden = false;
    player.isDetected = true;
  }

  @Override
  public String getEffectDescription(){
    return "[WARNING] PERSONA_DISCOVERED";
  }
}
