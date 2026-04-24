package main;

import player.Player;
import matrix.MatrixEntity;
import matrix.IC;
import enemy.Spider;

import java.util.Random;
import java.util.ArrayList;

public class TurnManager {
  private Game game;
  private Player player;
  private int currentTurn = 0;
  private static final int SECONDS_PER_TURN = 6;

  public TurnManager(Game game, Player player){
    this.game = game;
    this.player = player;
  }

  public void onPlayerActionTaken(){
    currentTurn++;
    printTurnInfo();

    if (game.currentHost == null) return;

    if (game.currentHost.isAlert) {
      processAlertTurns();
    } else {
      processSoloTurns();
    }
  }

  private void processSoloTurns(){
    for (MatrixEntity entity : getPatrolEntities()) {
      entity.lastSearch++;

      if (entity.lastSearch >= entity.searchInterval){
        System.out.println("\n[SYSTEM] SYSTEM_ALERT: We are conducting a routine scan of our system.");
        performPatrolSearch(entity);
        entity.lastSearch = 0;
      }
    }
  }

  private void processAlertTurns(){
    ArrayList<MatrixEntity> turnOrder = game.getTurnOrder();

    for (MatrixEntity entity : turnOrder){
      if (entity == player) continue;

      System.out.println("\n[SYSTEM] SYSTEM_ALERT_TURN: " + entity.name + " is conducting anti-intrusion countermeasures");
      processEntityTurn(entity);
    }
  }

  private void processEntityTurn(MatrixEntity entity) {
    if (entity instanceof IC){
      processICTurn((IC) entity);
    } else if (entity instanceof Spider){
      processSpiderTurn((Spider) entity);
    }
  }

  private void processICTurn(IC ic){
    if (game.currentHost.isAlert){
      System.out.println("[SYSTEM] SYSTEM_IC_" + ic.name.toUpperCase() + ": Activating countermeasures.");
      game.ResolveICAttack(ic, player);
    } else {
      if (ic.icType.equals("Patrol")) {
        ic.lastSearch ++;
        if (ic.lastSearch >= ic.searchInterval){
          System.out.println("[SYSTEM] SYSTEM_IC_" + ic.name.toUpperCase() + ": Performing Matrix Search.");
          performPatrolSearch(ic);
          ic.lastSearch = 0;
        }
      }
    }
  }

  private void processSpiderTurn(Spider spider){
    if (game.currentHost.isAlert) {
      Random random = new Random();
      System.out.println("[SYSTEM] SYSTEM_SECURITY: " + spider.Taunt(random.nextInt(2)));
    } else {
      spider.lastSearch++;
      if (spider.lastSearch >= spider.searchInterval){
        System.out.println("[SYSTEM] SYSTEM_SECURITY: Security operator '" + spider.name + "' is running routine security scan.");
        performPatrolSearch(spider);
        spider.lastSearch = 0;
      }
    }
  }

  private void performPatrolSearch(MatrixEntity searcher){
    if(!player.isHidden){
      System.out.println("[WARNING] SYSTEM_IC_" + searcher.name + ": IC Patrol has spotted your persona!");
      game.currentHost.isAlert = true;
      System.out.println("[WARNING] SYSTEM_ALERT: " + game.currentHost.name + " INTRUSION ACTIVITY DETECTED. ACTIVATING COUNTERMEASURES.");
    } else {
      System.out.println("[SYSTEM] ALL_CLEAR: Security scanning has completed, resume your normal activities.");
    }
  }

  private ArrayList<MatrixEntity> getPatrolEntities() {
    ArrayList<MatrixEntity> patrollers = new ArrayList<>();
    ArrayList<IC> ics = game.getHostIC(game.currentHost);
    if (ics != null) patrollers.addAll(ics);
    if (game.currentHost.hasSpider()) patrollers.add(game.currentHost.spider);
    return patrollers;
  }

  private void printTurnInfo(){
    int elapsed = currentTurn * SECONDS_PER_TURN;
    System.out.println(String.format("\n[TIME] Turn %d | %ds elapsed", currentTurn, elapsed));
  }

  public int getCurrentTurn() { return currentTurn; }
  public int getElapsedSeconds() { return currentTurn + SECONDS_PER_TURN; }
}
