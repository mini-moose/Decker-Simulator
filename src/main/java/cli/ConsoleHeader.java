package cli;

import game.Game;
import game.TurnManager;
import matrix.Host;
import mission.Objective;
import player.Player;

import java.util.ArrayList;

public class ConsoleHeader {
  private Game game;
  private Player player;
  private TurnManager turnManager;

  public ConsoleHeader(Game game, Player player, TurnManager turnManager) {
    this.game = game;
    this.player = player;
    this.turnManager = turnManager;
  }

  public void print() {
    printDivider("=");
    printPlayerLine();
    printMissionLine();
    printHostLine();
    printPivotLine();
    printObjectivesLine();
    printDivider("-");
    printTurnLog();
    printDivider("=");
  }

  private void printPlayerLine() {
    System.out.printf(">> DECKER: %-15s | ATK: %d  SZ: %d  DP: %d  FW: %d",
      player.name,
      player.attack,
      player.sleaze,
      player.dataProcessing,
      player.firewall
    );
  }

  private void printMissionLine() {
    if (game.currentMission == null) return;
    if (turnManager == null) {
      System.out.println(">> ");
      return;
    }
    int elapsed = turnManager.getElapsedSeconds();
    System.out.printf(">> MISSION: %-20s | TIMER: %ds | ALERT: %s%n",
      game.currentMission.targetCorp + " - " + game.currentMission.type.getLabel(),
      elapsed,
      game.currentHost != null && game.currentHost.isAlert ? "*** ALERT ***" : "Nominal"
    );
  }

  private void printHostLine() {
    String hostName = game.currentHost != null ? game.currentHost.name : "Not connected";
    String accessLevel = game.currentHost != null
      ? game.getAccessState(player, game.currentHost).toString()
      : "N/A";
    System.out.printf(">> HOST:    %-20s | ACCESS: %s%n", hostName, accessLevel);
  }

  private void printPivotLine() {
    if (game.currentHost == null) return;

    // Show hosts reachable from current host via ACL
    ArrayList<String> reachable = new ArrayList<>();
    for (Host host : game.hosts) {
      if (host != game.currentHost && host.ncl.contains(game.currentHost)) {
        reachable.add(host.name + (host.isHidden ? " [?]" : ""));
      }
    }

    if (!reachable.isEmpty()) {
      System.out.println(">> HOSTS_ON_NETWORK:   " + String.join(" | ", reachable));
    }
  }

  private void printObjectivesLine() {
    if (game.currentMission == null) return;
    System.out.println(">> OBJECTIVES: ");
    // Since the first objective is always to gain access to a target host
    // only print the first objective until the target host is broken into
    for (Objective obj : game.currentMission.objectives) {
      if (game.currentMission.targetHost != game.currentHost){
        System.out.println((obj.isComplete ? "[X] " : "[ ] ") + obj.description + "  ");
        return;
      } else {
        System.out.println((obj.isComplete ? "[X] " : "[ ] ") + obj.description + "  ");
      }
    }
    System.out.println();
  }

  private void printTurnLog() {
    if (game.turnLog.isEmpty()) {
      System.out.println(">> No activity last turn.");
    } else {
      System.out.println(">> LAST TURN:");
      for (String entry : game.turnLog) {
        System.out.println("   " + entry);
      }
    }
  }

  private void printDivider(String char_) {
    System.out.println(char_.repeat(80));
  }
}