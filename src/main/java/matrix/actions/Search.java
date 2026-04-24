package matrix.actions;

import player.Player;

import main.Game;
import main.ActionResult;
import matrix.MatrixEntity;

import matrix.Host;

import java.util.Arrays;

public class Search extends Action {

  public Search() {
    attackerStats.addAll(Arrays.asList("electronics", "intuition"));
    defenderStats.add(StatEntry.spider("willpower"));
    defenderStats.add(StatEntry.host("sleaze"));
  }

  @Override
  public String getName() { return "Search"; }

  @Override
  public boolean isIllegal() { return false; }

  @Override
  public String accessRequired() { return "Outsider"; }

  @Override
  public ActionResult applyEffect(Game game, MatrixEntity attacker, MatrixEntity target, int attackerHits, int targetHits) {
    // If the attacker has more hits than the target, the Action succeeds
    int netHits = attackerHits - targetHits;
    
    if (netHits > 0) {
      // If the Action succeeds, set host.hasBackdoor = true
      MatrixEntity defender = (MatrixEntity) target;
      defender.isHidden = false;

      return new ActionResult(true, netHits, targetHits,
          "Discovered hidden: " + target.name);
    } else {
      return new ActionResult(false, netHits, targetHits,
          "Search found no hidden Matrix Entities.");
    }
  }
}