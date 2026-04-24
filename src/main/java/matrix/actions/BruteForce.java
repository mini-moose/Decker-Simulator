package matrix.actions;

import player.Player;

import main.Game;
import main.ActionResult;
import main.MissionState;

import matrix.Host;
import matrix.AccessState;
import matrix.MatrixEntity;

import java.util.Arrays;
import java.util.HashMap;


// Brute Force
// Gaining Access to a Matrix Entity the loud way.
// Faster than Probe + Back Door, but sets the system on alert immediately
// Probably best not to try this on a Host, but Entities within the Host are fair game
// Unlike Probe + Back Door, this Action will give you a choice of Admin or User access
// Admin access gained by this skill is Illegal access and will build Overwatch Level passively
public class BruteForce extends Action {

  private String requestedAccess;

  public BruteForce(String requestedAccess) {
    this.requestedAccess = requestedAccess;
    attackerStats.addAll(Arrays.asList("cracking", "logic"));
    defenderStats.add(StatEntry.spider("willpower"));
    defenderStats.add(StatEntry.host("firewall"));
  
    if (requestedAccess.equalsIgnoreCase("Admin")){
      defenderBonus = 2;
    }
  }

  @Override
  public String getType() {return "Major"; }

  @Override
  public String getName() { return "Brute Force"; }

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
      Player attackerEntity = (Player) attacker;

      if (requestedAccess.equalsIgnoreCase("User")){
        targetHost.accessControl.put(attacker, AccessState.USER);
      } else if (requestedAccess.equalsIgnoreCase("Admin")){
        targetHost.accessControl.put(attacker, AccessState.ADMIN_ILLEGAL);
      }

      return new ActionResult(true, netHits, targetHits,
          "Brute Force successful, (Illegal) " + requestedAccess + " access gained on '" + targetHost.name + "'.");
    } else {
      return new ActionResult(false, netHits, targetHits,
          "Search found no hidden Matrix Entities.");
    }
  }
}