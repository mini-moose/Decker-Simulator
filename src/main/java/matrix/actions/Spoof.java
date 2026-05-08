package matrix.actions;

import java.util.Arrays;

import game.ActionResult;
import game.Game;
import matrix.AccessState;
import matrix.MatrixEntity;
import matrix.device.Device;


public class Spoof extends Action {

  public Spoof() {
    attackerStats.addAll(Arrays.asList("attack", "sleaze"));
    defenderStats.add(StatEntry.host("dataprocessing"));
    defenderStats.add(StatEntry.host("firewall"));
  }

  @Override
  public String getType() {return "Major"; }

  @Override
  public String getName() { return "Spoof Command"; }

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

    Device targetDevice = (Device) target;

    if (netHits > 0) {
      return new ActionResult(true, netHits, targetHits,
          "[" + attacker.name.toUpperCase() + "_TURN] Device: " + targetDevice.name + " recieved command.");
    } else {
      return new ActionResult(false, netHits, targetHits,
          "[" + attacker.name.toUpperCase() + "_TURN] Target device: '" + targetDevice.name + "' refused to process your command.");
    }
  } 
}