package matrix.ic;

import matrix.IC;

import matrix.Host;
import player.Player;

import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

public class Patrol extends IC implements ICEffect {

  public Patrol(Host host){
    super(host, "Patrol");

    this.lastSearch = new Random().nextInt(40);

    this.playerDefenseStats = new ArrayList<>(Arrays.asList("willpower", "firewall"));
  }
  @Override
  public void applyEffect(Player player, int netHits){
    player.isHidden = false;
  }

  @Override
  public String getEffectDescription(){
    return "[WARNING] PERSONA_DISCOVERED";
  }


}
