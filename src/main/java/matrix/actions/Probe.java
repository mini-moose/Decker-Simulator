package matrix.actions;

import java.util.Arrays;

import game.ActionResult;
import game.Game;
import matrix.AccessState;
import matrix.Host;
import matrix.MatrixEntity;

public class Probe extends Action {

  public Probe() {
    attackerStats.addAll(Arrays.asList("attack", "sleaze"));
    defenderStats.add(StatEntry.spider("willpower"));
    defenderStats.add(StatEntry.host("firewall"));
  }

  @Override
  public String getType() {return "Extended"; }

  @Override
  public String getName() { return "Probe"; }

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
    
    if (netHits > 0) {
      // If the Action succeeds, set host.hasBackdoor = true
      Host targetHost = (Host) target;
      targetHost.hasBackdoor = true;

      return new ActionResult(true, netHits, targetHits,
          "[" + attacker.name.toUpperCase() + "_TURN] Vulnerability found in " + target.name + ". Backdoor established.");
    } else {
      return new ActionResult(false, netHits, targetHits,
          "[" + attacker.name.toUpperCase() + "_TURN] Probe failed on " + target.name + ". No vulnerability found.");
    }
  }
}