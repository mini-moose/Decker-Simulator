package matrix.actions;

import game.ActionResult;
import game.Game;

import matrix.MatrixEntity;
import matrix.AccessState;
import matrix.Host;

import mission.ObjectiveType;

import player.Player;

import java.util.Arrays;

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

      // Can fulfil the GAIN_ACCESS objective on success
      game.checkObjectiveComplete(ObjectiveType.GAIN_ACCESS, targetHost);

      return new ActionResult(true, netHits, targetHits,
          "Backdoor exploited. You now have (Legal) Admin privileges on Host '" + targetHost.name + "'.");
    } else {
      targetHost.hasBackdoor = false;
      return new ActionResult(false, netHits, targetHits,
          "Backdoor exploitation failed. Backdoor was discovered and removed from '" + targetHost.name + "'.");
    }
  }
}