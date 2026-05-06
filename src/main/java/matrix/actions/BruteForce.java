package matrix.actions;

import game.ActionResult;
import game.Game;

import matrix.Host;
import matrix.AccessState;
import matrix.MatrixEntity;

import mission.ObjectiveType;

import player.Player;

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
    attackerStats.addAll(Arrays.asList("attack", "attack"));
    defenderStats.add(StatEntry.spider("willpower"));
    defenderStats.add(StatEntry.host("firewall"));
  
    if (requestedAccess.equalsIgnoreCase("admin")){
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
      // If the action succeeds, give requested access to attacker on the Host
      Host targetHost = (Host) target;
      Player attackerEntity = (Player) attacker;

      // Bruteforce always alerts the target Host
      targetHost.isAlert = true;

      if (requestedAccess.equalsIgnoreCase("User")){
        targetHost.accessControl.put(attacker, AccessState.USER);
      } else if (requestedAccess.equalsIgnoreCase("Admin")){
        targetHost.accessControl.put(attacker, AccessState.ADMIN_ILLEGAL);
      }

      // Can fulfil the GAIN_ACCESS objective on success

      return new ActionResult(true, netHits, targetHits,
          "Brute Force successful, (Illegal) " + requestedAccess + " access gained on '" + targetHost.name + "'.\n[WARNING] HOST ALERTED TO INTRUSION, DEPLOYING COUNTERMEASURES.");
    } else {
      return new ActionResult(false, netHits, targetHits,
          "Brute Force unsuccessful.");
    }
  }
}