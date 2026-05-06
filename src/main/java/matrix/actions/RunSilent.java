package matrix.actions;

import game.ActionResult;
import game.Game;

import matrix.MatrixEntity;
import matrix.AccessState;
import matrix.Host;

import player.Player;

import java.util.Arrays;

public class RunSilent extends Action {

  public RunSilent() {}

  @Override
  public String getType() {return "Major"; }

  @Override
  public String getName() { return "Run Silent"; }

  @Override
  public boolean isIllegal() { return false; }

  @Override
  public boolean isContested() { return false; }

  @Override
  public AccessState accessRequired() { return AccessState.OUTSIDER; }

  @Override
  public ActionResult applyEffect(Game game, MatrixEntity attacker, MatrixEntity target, int attackerHits, int targetHits) {
    attacker.isHidden = true;

      return new ActionResult(true, 0, 0,
          "You are now running silent.");
  }
}