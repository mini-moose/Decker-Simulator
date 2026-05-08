package matrix.actions;

import java.util.Arrays;

import game.ActionResult;
import game.Game;
import matrix.AccessState;
import matrix.MatrixEntity;
import matrix.files.HostFile;
import player.Player;





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
    Player player = (Player) attacker;
    if (player.findOwnedProgram("Editor") != null){
      attackerHits += 1;
      System.out.println("[INFO] PROGRAM_USED: Editor gave you one free hit on your Edit.");
    }

    int netHits = attackerHits - targetHits;

    HostFile targetFile = (HostFile) target;

    if (netHits > 0) {
      return new ActionResult(true, netHits, targetHits,
          "[" + attacker.name.toUpperCase() + "_TURN] Successfully edited " + targetFile.name + "'.");
    } else {
      return new ActionResult(false, netHits, targetHits,
          "[" + attacker.name.toUpperCase() + "_TURN] Edit attempt on'" + targetFile.name + "' failed.");
    }
  }
}