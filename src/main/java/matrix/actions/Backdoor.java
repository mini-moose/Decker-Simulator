package matrix.actions;

import java.util.Arrays;

import game.ActionResult;
import game.Game;
import matrix.AccessState;
import matrix.Host;
import matrix.MatrixEntity;

public class Backdoor extends Action {

  public Backdoor() {
    attackerStats.addAll(Arrays.asList("attack", "sleaze"));
    defenderStats.add(StatEntry.spider("willpower"));
    defenderStats.add(StatEntry.host("firewall"));
  }

  @Override
  public String getType() {return "Major"; }

  @Override
  public String getName() { return "Back Door"; }

  @Override
  public boolean isIllegal() { return true; }

  @Override
  public boolean isContested() { return true; }

  @Override
  public AccessState accessRequired() { return AccessState.OUTSIDER; }

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
      targetHost.accessControl.put(attacker, AccessState.ADMIN_LEGAL);

      return new ActionResult(true, netHits, targetHits,
          "[" + attacker.name.toUpperCase() + "_TURN] Backdoor exploited. You now have (Legal) Admin privileges on Host '" + targetHost.name + "'.");
    } else {
      targetHost.hasBackdoor = false;
      return new ActionResult(false, netHits, targetHits,
          "[" + attacker.name.toUpperCase() + "_TURN] Backdoor exploitation failed. Backdoor was discovered and removed from '" + targetHost.name + "'.");
    }
  }
}