package matrix.actions;

import player.Player;

import main.Game;
import main.ActionResult;
import matrix.MatrixEntity;
import main.MissionState;

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
    
    if (netHits > 0) {
      Host targetHost = (Host) target;
      game.currentHost = targetHost;
      
      Player player = (Player) attacker;
      player.accessLevel = "Admin";

      return new ActionResult(true, netHits, targetHits,
          "Backdoor entered. You now have Admin privileges on Host " + targetHost.name + ".");
    } else {
      Host targetHost = (Host) target;
      targetHost.hasBackdoor = false;

      return new ActionResult(false, netHits, targetHits,
          "Backdoor entry failed. Backdoor was discovered and removed.");
    }
  }
}