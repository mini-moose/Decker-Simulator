package matrix.actions;

import player.Player;

import matrix.MatrixEntity;
import matrix.AccessState;
import matrix.Host;

import main.Game;
import main.ActionResult;

// Enter Host
// Broken out actions between Exit Host and Enter Host
// Enter Host lets the user enter a Host which has them in their ACL
// This means the player has to get that access another way (see BruteForce and Backdoor)
public class ExitHost extends Action {

  public ExitHost() {}

  @Override
  public String getType() { return "Minor"; };

  @Override
  public String getName() { return "Enter Host"; }
  
  @Override
  public boolean isIllegal() { return false; }

  @Override
  public boolean isContested() { return false; }

  @Override
  public AccessState accessRequired() { return AccessState.USER; }
  
  @Override
  public ActionResult applyEffect(Game game, MatrixEntity attacker, MatrixEntity defender, int attackerHits, int targetHits){
    Host targetHost = (Host) defender;

    game.currentHost = targetHost;
    game.parentHost = null;

    return new ActionResult(true, 0, 0,
      "[INFO] HOST_STATE: Exited to Host " + defender.name + ".");
  }

}