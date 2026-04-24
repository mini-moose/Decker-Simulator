package matrix.actions;

import main.Game;
import main.ActionResult;

import matrix.Host;
import matrix.MatrixEntity;
import matrix.AccessState;

import java.util.ArrayList;

public abstract class Action {
  protected ArrayList<String> attackerStats = new ArrayList<>();
  protected ArrayList<StatEntry> defenderStats = new ArrayList<>();
  protected int attackerBonus = 0;
  protected int defenderBonus = 0;

  public abstract String getType();
  public abstract String getName();

  public abstract boolean isIllegal();
  public abstract boolean isContested();

  public abstract AccessState accessRequired();

  public abstract ActionResult applyEffect(Game game, MatrixEntity attacker, MatrixEntity target, int attackerHits, int targetHits);

  public ActionResult execute(Game game, MatrixEntity attacker, MatrixEntity target) {

    if (!game.hasRequiredAccess(attacker, target, accessRequired())) {
      AccessState current = game.getAccessState(attacker, target);
      return new ActionResult(false, 0, 0,
        "[ERROR] INSUFFICIENT_ACCESS: " + getName() + " requires " + accessRequired() + " access. Current access: " + current);
    }

    if (!isContested()) {
      return applyEffect(game, attacker, target, 0, 0);
    }

    ArrayList<String> defenderStats = resolveDefense(game, target);
    ArrayList<Integer> netHits = game.ContestedRoll(attacker, target, attackerStats, defenderStats, attackerBonus, defenderBonus);
    
    // Assign attacker hits, defender hits, and glitches to variables for easier comparison
    int attackerHits = netHits.get(0);
    int targetHits = netHits.get(1);

    // Illegal actions generate overwatch on target hits
    
    if (isIllegal() && targetHits > 0) {
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
