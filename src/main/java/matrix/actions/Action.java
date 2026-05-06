package matrix.actions;

import game.Game;
import game.ActionResult;

import main.Debug;
import main.DiceRoller;

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

  public DiceRoller roller = new DiceRoller();

  public ActionResult execute(Game game, MatrixEntity attacker, MatrixEntity target) {

    if (!game.hasRequiredAccess(attacker, target, accessRequired())) {
      AccessState current = game.getAccessState(attacker, target);
      return new ActionResult(false, 0, 0,
        "[ERROR] INSUFFICIENT_ACCESS: " + getName() + " requires " + accessRequired() + " access. Current access: " + current);
    }

    if (!isContested()) {
      return applyEffect(game, attacker, target, 0, 0);
    }

    int attackerPool = roller.GrabDice(attacker, attackerStats) + attackerBonus;

    int defenderPool = resolveDefense(game, target);
    ArrayList<Integer> netHits = game.ContestedRoll(attacker, target, attackerPool, defenderPool);
    
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
  
  private int resolveDefense(Game game, MatrixEntity target){
    int totalDice = defenderBonus;

    Host host = (target instanceof Host) ? (Host) target : game.currentHost;

    for (StatEntry entry : defenderStats){
      switch (entry.source){
        case HOST:
          totalDice += target.getStat(entry.statName);
          break;
        case SPIDER:
          if (host != null && host.hasSpider()) {
            totalDice += host.spider.getStat(entry.statName);
          } else {
            Debug.log("HOST_NO_SPIDER: " + entry.statName + " nulled from defense pool.");
          }
          break;
        case FLAT:
          totalDice += entry.flatValue;
          break;
        case SUBSTITUTE:
          boolean primaryAvailable = (entry.source == StatSource.SPIDER)
            ? (host != null && host.hasSpider())
            : true;
          
          if (primaryAvailable && host != null && host.hasSpider()){
            totalDice += host.spider.getStat(entry.statName);
          } else {
            int subValue = target.getStat(entry.substituteStat) * entry.flatValue;
            totalDice += subValue;
          }
          break;
      }
    }
    return totalDice;
  }
}
