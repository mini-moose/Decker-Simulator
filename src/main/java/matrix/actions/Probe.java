package matrix.actions;

import player.Player;

import main.Game;
import main.ActionResult;
import matrix.MatrixEntity;
import main.MissionState;

import matrix.Host;

import java.util.Arrays;

public class Probe extends Action {

  public Probe() {
    attackerStats.addAll(Arrays.asList("cracking", "logic"));
    defenderStats.add(StatEntry.spider("willpower"));
    defenderStats.add(StatEntry.host("firewall"));
  }

  @Override
  public String getName() { return "Probe"; }

  @Override
  public boolean isIllegal() { return true; }

  @Override
  public String accessRequired() { return "Outsider"; }

  @Override
  public ActionResult applyEffect(Game game, MatrixEntity attacker, MatrixEntity target, int attackerHits, int targetHits) {
    // If the attacker has more hits than the target, the Action succeeds
    int netHits = attackerHits - targetHits;
    
    if (netHits > 0) {
      // If the Action succeeds, set host.hasBackdoor = true
      Host targetHost = (Host) target;
      targetHost.hasBackdoor = true;

      return new ActionResult(true, netHits, targetHits,
          "Vulnerability found. Backdoor established.");
    } else {
      return new ActionResult(false, netHits, targetHits,
          "Probe failed. No vulnerability found.");
    }
  }
}