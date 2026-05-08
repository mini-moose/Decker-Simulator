package matrix.actions;

import java.util.Arrays;

import game.ActionResult;
import game.Game;
import matrix.AccessState;
import matrix.MatrixEntity;

public class CheckOS extends Action {

  public CheckOS() {
    attackerStats.addAll(Arrays.asList("sleaze", "dataProcessing"));
    defenderStats.add(StatEntry.host("firewall"));
  }

  @Override
  public String getType() { return "Major"; }

  @Override
  public String getName() { return "Check Overwatch Score"; }

  @Override
  public boolean isIllegal() { return true; }

  @Override
  public boolean isContested() { return true; }

  @Override
  public AccessState accessRequired() { return AccessState.ADMIN_LEGAL; }

  @Override
  public ActionResult applyEffect(Game game, MatrixEntity attacker, MatrixEntity target, int attackerHits, int targetHits) {
    // If the attacker has more hits than the target, the Action succeeds   
    int netHits = attackerHits - targetHits;

    if (netHits > 0) {
      // If the Action succeeds, return current Overwatch Score
      if (game.overWatchScore > 0){
        return new ActionResult(true, netHits, 0, String.format(
            "\n===     0 0 0                ===\n" +
            "===     0   0      GET OUT:  ===\n" +
            "===     0 0 0       %d/40    ===\n" + 
            "===   W       W  CYCLE(S) TO ===\n" +
            "===    W  W  W      COMPLY   ===\n" +
            "===     W   W                ===\n", 40 - game.overWatchScore));
      } else {
        return new ActionResult(true, netHits, targetHits,
        "[" + attacker.name.toUpperCase() + "_TURN] OVERWATCH_SCORE: OverWatch is not tracking your movements.");
      }
    } else {
      return new ActionResult(false, netHits, 0,
          "[" + attacker.name.toUpperCase() + "_TURN] Denied access to endpoint overwatch." + target.name + ":9999 // ERRORCODE:TmljZSB0cnkgZGVja2VyCg==");
    }
  }
}