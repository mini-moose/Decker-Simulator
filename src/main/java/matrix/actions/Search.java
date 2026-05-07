package matrix.actions;

import game.ActionResult;
import game.Game;
import java.util.Arrays;
import matrix.AccessState;
import matrix.MatrixEntity;
import player.Player;




public class Search extends Action {

  public Search() {
    attackerStats.addAll(Arrays.asList("sleaze", "dataprocessing"));
    defenderStats.add(StatEntry.spider("willpower"));
    defenderStats.add(StatEntry.host("sleaze"));
  }

  @Override
  public String getType() {return "Minor"; }

  @Override
  public String getName() { return "Search"; }

  @Override
  public boolean isIllegal() { return false; }

  @Override
  public boolean isContested() { return true; }

  @Override
  public AccessState accessRequired() { return AccessState.OUTSIDER; }

  @Override
  public ActionResult applyEffect(Game game, MatrixEntity attacker, MatrixEntity target, int attackerHits, int targetHits) {
    // If the attacker has more hits than the target, the Action succeeds
    Player player = (Player) attacker;
    if (player.findOwnedProgram("Browse") != null){
      attackerHits += 1;
      System.out.println("[INFO] PROGRAM_USED: Browse gave you one free hit on your Search attempt.");
    }

    int netHits = attackerHits - targetHits;
    
    if (netHits > 0) {
      // If the Action succeeds, set host.hasBackdoor = true
      MatrixEntity defender = target;
      defender.isHidden = false;

      return new ActionResult(true, netHits, targetHits,
          "Discovered hidden: " + target.name);
    } else {
      return new ActionResult(false, netHits, targetHits,
          "Search found no hidden Matrix Entities.");
    }
  }
}