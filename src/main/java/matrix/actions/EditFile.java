package matrix.actions;

import game.ActionResult;
import game.Game;

import matrix.MatrixEntity;
import matrix.AccessState;
import matrix.files.HostFile;

import mission.ObjectiveType;

import player.Player;

import java.util.Arrays;

public class EditFile extends Action {

  public EditFile() {
    attackerStats.addAll(Arrays.asList("attack", "dataprocessing"));
    defenderStats.add(StatEntry.host("sleaze"));
    defenderStats.add(StatEntry.host("firewall"));
  }

  @Override
  public String getType() {return "Major"; }

  @Override
  public String getName() { return "Edit File"; }

  @Override
  public boolean isIllegal() { return false; }

  @Override
  public boolean isContested() { return true; }

  @Override
  public AccessState accessRequired() { return AccessState.USER; }

  @Override
  public ActionResult applyEffect(Game game, MatrixEntity attacker, MatrixEntity target, int attackerHits, int targetHits) {
    // If the attacker has more hits than the target, the Action succeeds
    int netHits = attackerHits - targetHits;

    HostFile targetFile = (HostFile) target;

    if (netHits > 0) {
      return new ActionResult(true, netHits, targetHits,
          "Successfully edited " + targetFile.name + "'.");
    } else {
      return new ActionResult(false, netHits, targetHits,
          "Edit attempt on'" + targetFile.name + "' failed.");
    }
  }
}