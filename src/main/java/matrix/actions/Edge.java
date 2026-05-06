package matrix.actions;

import game.ActionResult;
import game.Game;
import matrix.AccessState;
import matrix.MatrixEntity;
import player.Player;

public class Edge extends Action {

  private EdgeType edgeType;

  public Edge(EdgeType type) {
    this.edgeType = type;
  }

  @Override
  public String getType() { return "Minor"; };

  @Override
  public String getName() { return "Edge"; }
  
  @Override
  public boolean isIllegal() { return false; }

  @Override
  public boolean isContested() { return false; }

  @Override
  public AccessState accessRequired() { return AccessState.OUTSIDER; }
  
  @Override
  public ActionResult applyEffect(Game game, MatrixEntity attacker, MatrixEntity defender, int attackerHits, int targetHits){

    Player player = (Player) attacker;
    player.edgeType = edgeType;

    return new ActionResult(true, 0, 0,
      "[INFO] EDGE_GAINED: Deck reconfigured to mode:  " + edgeType.toString() + ".");
  }

}