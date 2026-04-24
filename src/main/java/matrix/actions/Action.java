package matrix.actions;

import main.Game;
import main.ActionResult;

import matrix.Host;
import matrix.MatrixEntity;

import java.util.ArrayList;

public abstract class Action {
  protected ArrayList<String> attackerStats = new ArrayList<>();
  protected ArrayList<StatEntry> defenderStats = new ArrayList<>();

  public abstract String getName();
  public abstract boolean isIllegal();
  public abstract String accessRequired();

  public abstract ActionResult applyEffect(Game game, MatrixEntity attacker, MatrixEntity target, int attackerHits, int targetHits);

  public ActionResult execute(Game game, MatrixEntity attacker, MatrixEntity target) {

    ArrayList<String> defenderStats = resolveDefense(game, target);
    ArrayList<Integer> netHits = game.ContestedRoll(attacker, target, attackerStats, defenderStats);
    
    // Assign attacker hits, defender hits, and glitches to variables for easier comparison
    int attackerHits = netHits.get(0);
    int targetHits = netHits.get(1);

    // Illegal actions generate overwatch on target hits
    
    if (isIllegal()) {
      if (targetHits > 0){
        game.updateOverwatch(targetHits);
      }
    }

    return applyEffect(game, attacker, target, attackerHits, targetHits);
  }
  
  private ArrayList<String> resolveDefense(Game game, MatrixEntity target){
    ArrayList<String> resolvedStats = new ArrayList<>();
    MatrixEntity defender = target;

    Host host = (target instanceof Host) ? (Host) target : game.currentHost;

    for (StatEntry entry : defenderStats){
      if (entry.source == StatSource.SPIDER) {
        if (host != null && host.hasSpider()) {
          resolvedStats.add(entry.statName);
          defender = host.spider;
        } else {
          System.out.println("[INFO] HOST_NO_SPIDER: " + entry.statName + " nulled from defense pool.");
        }
      } else {
        resolvedStats.add(entry.statName);
      }
    }
    return resolvedStats;
  }
}
