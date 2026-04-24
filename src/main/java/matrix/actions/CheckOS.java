package matrix.actions;

import main.Game;
import main.ActionResult;
import matrix.MatrixEntity;
import main.MissionState;

import matrix.Host;

import java.util.Arrays;

public class CheckOS extends Action {

  public CheckOS() {
    attackerStats.addAll(Arrays.asList("cracking", "logic"));
    defenderStats.add(StatEntry.host("firewall"));
  }

  @Override
  public String getName() { return "Check Overwatch Score"; }

  @Override
  public boolean isIllegal() { return true; }

  @Override
  public String accessRequired() { return "Admin"; }

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
        "[INFO] OVERWATCH_SCORE: OverWatch is not tracking your movements.");
      }
    } else {
      return new ActionResult(false, netHits, 0,
          "Denied access to endpoint overwatch." + target.name + ":9999 // ERRORCODE:TmljZSB0cnkgZGVja2VyCg==");
    }
  }
}