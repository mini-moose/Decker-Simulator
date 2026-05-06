package matrix.actions;

import java.util.Arrays;

import game.ActionResult;
import game.Game;
import matrix.AccessState;
import matrix.MatrixEntity;
import matrix.device.Device;
import mission.ObjectiveType;

public class Dataspike extends Action {

  public Dataspike() {
    attackerStats.addAll(Arrays.asList("attack", "dataprocessing"));
    defenderStats.add(StatEntry.host("dataprocessing"));
    defenderStats.add(StatEntry.host("firewall"));
  }

  @Override
  public String getType() {return "Major"; }

  @Override
  public String getName() { return "Data Spike"; }

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
      int damage = (attacker.attack + attacker.sleaze) / 2 + netHits;
      targetDevice.devCondition -= damage;

      if (targetDevice.devCondition <= 0){
        game.currentHost.devicesOnHost.remove(targetDevice);
        System.out.println("[INFO] Device: " + targetDevice.name + " was destroyed.");
        game.checkObjectiveComplete(ObjectiveType.DISABLE_DEVICE, targetDevice);
      }
      return new ActionResult(true, netHits, targetHits,
          "Dealt " +  damage + " damage to " + targetDevice.name + "'.");
    } else {
      return new ActionResult(false, netHits, targetHits,
          "Target device: '" + targetDevice.name + "' avoided all damage.");
    }
  }
}