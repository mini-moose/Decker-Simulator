package matrix.actions;

import player.Player;

import main.Game;
import main.ActionResult;
import matrix.MatrixEntity;
import matrix.AccessState;

import matrix.Host;

import java.util.Arrays;

public class Backdoor extends Action {

  public Backdoor() {
    attackerStats.addAll(Arrays.asList("cracking", "logic"));
    defenderStats.add(StatEntry.spider("willpower"));
    defenderStats.add(StatEntry.host("firewall"));
  }

  @Override
  public String getName() { return "Back Door"; }

  @Override
  public boolean isIllegal() { return true; }

  @Override
  public String accessRequired() { return "Outsider"; }

  @Override
  public ActionResult applyEffect(Game game, MatrixEntity attacker, MatrixEntity target, int attackerHits, int targetHits) {
    // If the attacker has more hits than the target, the Action succeeds
    int netHits = attackerHits - targetHits;

    Host targetHost = (Host) target;

    if (!targetHost.hasBackdoor){
      return new ActionResult(false, 0, 0,
          "No backdoor found on '" + targetHost.name + "'. Did you forget to Probe first?");
    }
    
    if (netHits > 0) {
      game.currentHost = targetHost;
      
      targetHost.accessControl.put(attacker, AccessState.ADMIN_LEGAL);
      return new ActionResult(true, netHits, targetHits,
          "Backdoor exploited. You now have (Legal) Admin privileges on Host '" + targetHost.name + "'.");
    } else {
      targetHost.hasBackdoor = false;
      return new ActionResult(false, netHits, targetHits,
          "Backdoor exploitation failed. Backdoor was discovered and removed from '" + targetHost.name + "'.");
    }
  }
}