package matrix.actions;

import game.ActionResult;
import game.Game;
import java.util.Arrays;
import matrix.AccessState;
import matrix.MatrixEntity;
import matrix.files.HostFile;
import player.Player;





public class Crack extends Action {

  public Crack() {
    attackerStats.addAll(Arrays.asList("attack", "sleaze"));
    defenderStats.add(StatEntry.host("firewall"));
    defenderStats.add(StatEntry.host("firewall"));
  }

  @Override
  public String getType() {return "Major"; }

  @Override
  public String getName() { return "Crack File"; }

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
    if (player.findOwnedProgram("Decrypt") != null){
      attackerHits += 2;
      System.out.println("[INFO] PROGRAM_USED: Decrypt gave you two free hits on your Crack attempt.");
    }

    int netHits = attackerHits - targetHits;

    HostFile targetFile = (HostFile) target;

    if (netHits > 0) {
      targetFile.isEncrypted = false;
      return new ActionResult(true, netHits, targetHits,
          "Successfully decrypted " + targetFile.name + "'.");
    } else {
      return new ActionResult(false, netHits, targetHits,
          "Decrypt attempt on'" + targetFile.name + "' failed.");
    }
  }
}