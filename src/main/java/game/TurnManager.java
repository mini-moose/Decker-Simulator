package game;

import java.util.Random;

import matrix.Host;
import matrix.actions.Action;
import matrix.ic.IC;
import matrix.ic.ICType;
import player.Player;

public class TurnManager {
  private Game game;
  private Player player;
  private int previousTurn = 0;
  private int currentTurn = 0;
  private static final int SECONDS_PER_TURN = 6;

  public Random random = new Random();

  public TurnManager(Game game, Player player){
    this.game = game;
    this.player = player;
  }

  public void applyEdgeEffect(Player player){
    if (player.edgeType == null) return;
    
    switch(player.edgeType){
      case OVERCLOCK:
        player.attack += player.rating;
        player.sleaze += player.rating;
        player.firewall -= player.rating;
        player.dataProcessing -= player.rating;
        break;
      case SHIELD:
        player.attack += player.rating;
        player.sleaze += player.rating;
        player.firewall -= player.rating;
        player.dataProcessing -= player.rating;
        break;
      case SCRAMBLE:
        game.updateOverwatch(player.rating);
        break;
    }
  }

  private void resetEdgeEffect(Player player){
    switch(player.edgeType){
      case OVERCLOCK:
        player.attack -= player.rating;
        player.sleaze -= player.rating;
        player.firewall += player.rating;
        player.dataProcessing += player.rating;
        break;
      case SHIELD:
        player.attack -= player.rating;
        player.sleaze -= player.rating;
        player.firewall += player.rating;
        player.dataProcessing += player.rating;
        break;
    }
    player.edgeType = null;
  }

  public void onPlayerActionTaken(Action action){
    // If the mission is complete, return
    if (game.currentMission.isComplete()) return;

    if (action == null){
      return;
    }

    // Move turn forward differently for each action
    if (action.getType().equals("Extended")) {
      previousTurn = currentTurn;
      currentTurn += 10;
    } else if (action.getType().equals("Major")) {
      previousTurn = currentTurn;
      currentTurn++;
    }
    printTurnInfo();
    if (player.edgeType != null){
      resetEdgeEffect(player);
    }

    if (game.currentHost == game.defaultHost) return;

    if (action.isIllegal() || game.currentHost.isAlert) {
      game.respondToIllegalAction(game.currentHost, player, random);
    }

    handleHostAction(game.currentHost);
    game.checkLoss();

  }

  public void handleHostAction(Host host){
    // If the mission is complete, return
    if (game.currentMission.isComplete()) return;

    System.out.println("[INFO] Host taking turn.");
    IC patrol = game.getDeployedICOfType(host, ICType.PATROL);

    if (patrol == null) return;
    if (player.isHidden && (host.isAlert || player.isDetected || patrol.lastSearch > 60)){
      game.handlePatrolAction(patrol);
    } else {
      patrol.lastSearch += 6;
      System.out.println("[DEBUG] Patrol Last Search: " + patrol.lastSearch);
    }
    if (!player.isHidden && player.isDetected){
      for (IC ic : host.deployedIC){
        if (ic.icType != ICType.PATROL){
          handleICAction(ic);
        }
      }
    }
  }

  private void printTurnInfo(){
    int elapsed = currentTurn * SECONDS_PER_TURN;
    System.out.println(String.format("\n[TIME] Turn %d | %ds elapsed", currentTurn, elapsed));
  }

  public void handleICAction(IC ic){
    System.out.println("[WARNING] Host IC's converging on matrix area.");
    game.ResolveICAttack(ic, player);
  }

  public int getCurrentTurn() { return currentTurn; }
  public int getElapsedSeconds() { return currentTurn + SECONDS_PER_TURN; }
}
