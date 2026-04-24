package cli;

import cli.CommandParser;

import main.Game;
import main.ActionResult;
import main.TurnManager;

import matrix.Host;
import matrix.MatrixEntity;
import matrix.actions.*;

import player.Player;

import java.util.Scanner;
import java.util.ArrayList;

public class DeckerConsole {

  private Game game;
  private Player player;
  private TurnManager turnManager;

  private Scanner console = new Scanner(System.in);

  public DeckerConsole(Game game, Player player){
    this.game = game;
    this.player = player;
    this.turnManager = new TurnManager(game, player);
  }

  public void start(){
    printBanner();
    // Get decker name on first entry
    String playerName = player.name;

    while (playerName.isEmpty()) {
      System.out.println("What's your handle, decker?");
      playerName = console.nextLine().trim();
      if (playerName.isEmpty()){
        System.out.println("Trying for incognito huh? Do us both a favor and just pick a name, yea?");
      } else {
        player.name = playerName;
        break;
      }
    }

    while (true) {

      System.out.print(player.name + "@matrix:~$ ");
      String input = console.nextLine().trim();

      if (input.isEmpty()) continue;

      String[] parts = input.split(" ");
      CommandParser cmd = CommandParser.parse(parts);

      switch (cmd.command) {
        case "probe":
          handleProbe(cmd);
          break;
        case "checkos":
          handleCheckOS(cmd);
          break;
        case "backdoor":
          handleBackdoor(cmd);
          break;
        case "bruteforce":
          handleBruteForce(cmd);
          break;
        case "search":
          handleSearch(cmd);
          break;
        case "enter-host":
          handleEnterHost(cmd);
          break;
        case "exit-host":
          handleExitHost(cmd);
          break;
        case "hosts":
          handleHosts();
          break;
        case "status":
          handleStatus();
          break;
        case "help":
          handleHelp();
          break;
        case "exit":
          System.out.println("Jacking out...");
          return;
        default:
          System.out.println("Unknown command: " + cmd.command + ". Type 'help' for options.");
      }
    }
  }

  // Attempt to establish a backdoor exploit on the target system
  // Success: A backdoor exploit is established and can use the backdoor action
  // Failure: Backdoor exploit not established
  private void handleProbe(CommandParser cmd){
    // Usage: probe <hostname or host UP>
    if (cmd.positionalArgs.isEmpty()){
      System.out.println("Probe a host for vulnerabilities.");
      System.out.println("Notes: Extended Action, Illegal");
      System.out.println("Options:");
      System.out.println("  -r --repeat-attempts 'Repeat command # times or until succeed'");
      System.out.println("Usage: probe <target>");
      System.out.println("Example: probe UnirealCorp-DMZ-01");
      System.out.println("Example: probe UnirealCorp-DMZ-01 -r 3");
      return;
    }

    Host target = game.findHost(cmd.positionalArgs.get(0));

    if (target == null){
      System.out.println("[ERROR] HOST_NOT_FOUND: " + cmd.positionalArgs.get(0) + " is not a host on your network.");
      System.out.println("Use 'hosts' to list available hosts.");
      return;
    }

    int attempts = cmd.getIntOption("r", cmd.getIntOption("repeat", 1));

    for (int i=0; i < attempts; i++){
      if (attempts > 1) System.out.println("Attempt " + (i + 1) + " of " + attempts);

      Probe probe = new Probe();
      ActionResult result = probe.execute(game, player, target);
      System.out.println(result);
      turnManager.onPlayerActionTaken();

      if (result.success) { break; }
    }
  }

  // Exploit a backdoor set by Probe action
  // Success: Gives attacker Legal Admin access
  // Failure: Defender finds and removes backdoor
  private void handleBackdoor(CommandParser cmd){
    if (cmd.positionalArgs.isEmpty()){
      System.out.println("Leverage a Backdoor on the target system.");
      System.out.println("Notes: Major Action, Illegal");
      System.out.println("Options:");
      System.out.println("  -r --repeat-attempts 'Repeat command # times or until succeed'");
      System.out.println("Usage: backdoor <target>");
      System.out.println("Example: backdoor UnirealCorp-DMZ-01");
      System.out.println("Example: backdoor UnirealCorp-DMZ-01 -r 3");
      return;
    }

    Host target = game.findHost(cmd.positionalArgs.get(0));

    // If the target is not in the list of discoverable hosts, return an errror
    if (target == null){
      System.out.println("[ERROR] HOST_NOT_FOUND: " + cmd.positionalArgs.get(0) + " is not a host on your network.");
      System.out.println("Use 'hosts' to list available hosts.");
      return;
    }

    if (!target.hasBackdoor){
      System.out.println("[ERROR] HOST_NO_EXPLOIT: " + cmd.positionalArgs.get(0) + " does not have an established backdoor. Did you forget to Probe first?");
      return;
    }

    int attempts = cmd.getIntOption("r", cmd.getIntOption("repeat", 1));

    for (int i=0; i < attempts; i++){
      if (attempts > 1) System.out.println("Attempt " + (i + 1) + " of " + attempts);

      Backdoor backdoor = new Backdoor();
      ActionResult result = backdoor.execute(game, player, target);
      System.out.println(result);

      turnManager.onPlayerActionTaken();

      if (result.success) break;
    }
  }


  // Checks your current Overwatch Score
  // Success: You find your Overwatch Score
  // Failure: You are unable to find your Overwatch Score
  private void handleCheckOS(CommandParser cmd){
    if (game.currentHost == null){
      System.out.println("ERROR [HOST_NOT_FOUND]: You are not currently connected to a host");
      return;
    }

    CheckOS checkOS = new CheckOS();
    ActionResult result = checkOS.execute(game, player, game.currentHost);
    System.out.println(result);

    turnManager.onPlayerActionTaken();
  }

  private void handleBruteForce(CommandParser cmd){
    // Usage: probe <hostname or host UP>
    if (cmd.positionalArgs.size() < 1){
      System.out.println("Brute Force your way into a system.");
      System.out.println("Notes: Major Action, Illegal");
      System.out.println("Options:");
      System.out.println("  -a --access <level>  Access level to force (user/admin). Defaults to User");
      System.out.println("  -r --repeat-attempts 'Repeat command # times or until succeed'");
      System.out.println("Usage: bruteforce <target>");
      System.out.println("Example: bruteforce UnirealCorp-DMZ-01 --access admin");
      System.out.println("Example: bruteforce UnirealCorp-DMZ-01 --access user -r 3 ");
      return;
    }

    Host target = game.findHost(cmd.positionalArgs.get(0));

    // If the target is not in the list of discoverable hosts, return an errror
    if (target == null){
      System.out.println("[ERROR] HOST_NOT_FOUND: " + cmd.positionalArgs.get(0) + " is not a host on your network.");
      System.out.println("Use 'hosts' to list available hosts.");
      return;
    }

    int attempts = cmd.getIntOption("r", cmd.getIntOption("repeat", 1));

    String access = cmd.getOption("access", cmd.getOption("a", "user"));

    for (int i=0; i < attempts; i++){
      if (attempts > 1) System.out.println("Attempt " + (i + 1) + " of " + attempts);

      BruteForce bruteforce = new BruteForce(access);
      ActionResult result = bruteforce.execute(game, player, target);
      System.out.println(result);

      turnManager.onPlayerActionTaken();

      if (result.success) break;
    }
  }

  // Search Action
  // Requires: MatrixEntity type (host, file, device, icon)
  // Search for a hidden system on or connected to your current Host
  // Success: You discover the system you were looking for
  // Failure: You do not discover the system you were looking for
  private void handleSearch(CommandParser cmd){
    if (cmd.positionalArgs.isEmpty()) {
      System.out.println("Search your current Host for Programs that are in Run Silent mode.");
      System.out.println("Notes: Major Action, Legal");
      System.out.println("Usage: search <entity_type>");
      System.out.println("Example: search Host");
      System.out.println("Example: search File");
      return;
    }

    String entityType = cmd.positionalArgs.get(0);

    Class<?> targetClass = resolveEntityType(entityType);
    if (targetClass == null){
      System.out.println("[ERROR] ENTITY_UNKNOWN_TYPE: The Matrix Entity type '" + entityType + "' is unknown.");
      System.out.println("Valid types: host, device, file, ic, agent");
      return;
    }

    ArrayList<MatrixEntity> targets = game.findHiddenEntities(targetClass);

    if (targets.isEmpty()) {
      System.out.println("[INFO] ENTITY_NO_SIGNATURES: There are no Matrix signatures on this host for hidden " + entityType + " entities.");
      return;
    }

    System.out.println("[INFO] ENTITY_DETECT_SIGNATURES: Matrix Scan detected " + targets.size() + " hidden " + entityType + " on this host.");

    Search search = new Search();
    for (MatrixEntity target : targets) {
      ActionResult result = search.execute(game, player, target);
      System.out.println(result);
    }

    turnManager.onPlayerActionTaken();
  }

  private void handleEnterHost(CommandParser cmd){
    if (cmd.positionalArgs.isEmpty()){
      System.out.println("Enter a Host you have access to");
      System.out.println("Notes: Simple Action, Legal");
      System.out.println("Usage: enter-host <target>");
      System.out.println("Example: enter-host UnirealCorp-DMZ-01");
      System.out.println("Example: enter-host UnirealCorp-DMZ-01 -r 3");
      return;
    }

    Host target = game.findHost(cmd.positionalArgs.get(0));

    if (target == null){
      System.out.println("[ERROR] HOST_NOT_FOUND: " + cmd.positionalArgs.get(0) + " is not a host on your network.");
      System.out.println("Use 'hosts' to list available hosts.");
      return;
    }

    EnterHost enterHost = new EnterHost();
    ActionResult result = enterHost.execute(game, player, target);
    System.out.println(result);
    turnManager.onPlayerActionTaken();
  }

  private void handleExitHost(CommandParser cmd){
    if (cmd.positionalArgs.isEmpty()){
      System.out.println("Exit from your current host to your previous host");
      System.out.println("Notes: Simple Action, Legal");
      System.out.println("Usage: exit-host <target>");
      System.out.println("Example: exit-host UnirealCorp-DMZ-01");
      System.out.println("Example: exit-host UnirealCorp-DMZ-01 -r 3");
      return;
    }

    Host target = game.findHost(cmd.positionalArgs.get(0));

    if (target == null){
      System.out.println("[ERROR] HOST_NOT_FOUND: " + cmd.positionalArgs.get(0) + " is not a host on your network.");
      System.out.println("Use 'hosts' to list available hosts.");
      return;
    }

    ExitHost exitHost = new ExitHost();
    ActionResult result = exitHost.execute(game, player, target);
    System.out.println(result);
    turnManager.onPlayerActionTaken();
  }

  private void handleHosts() {
    // Usage: Shows available hosts on your network.
    // DOES NOT SHOW HOSTS THAT ARE HIDING, PERCEPTION MAY BE REQUIRED TO REVEAL HIDDEN HOSTS
    if (game.hosts.isEmpty()){
      System.out.println("No hosts detected.");
      return;
    }
    System.out.println("=== DETECTED HOSTS ON NETWORK ===");
    for (Host host : game.hosts) {
      if (host.isHidden == false && host.ncl.contains(game.currentHost)){
        System.out.printf("%-20s %s%n", host.name, host.uniProto);
      }
    }
    System.out.println("=================================");
  }

  private void handleStatus(){
    System.out.println("=== DECKER STATUS ===");
    System.out.println("FIREWALL:          " + player.firewall);
    System.out.println("ATTACK:            " + player.attack);
    System.out.println("INITIATIVE:        " + player.initiative);
    System.out.println("CONDITION:         " + player.conditionMonitor);
    System.out.println("MISSION_STATUS:    " + game.getMissionState());
    System.out.println("=====================");
  }

  private void handleHelp(){
    System.out.println("=== AVAILABLE COMMANDS ===");
    System.out.println("probe <target>             - Probe a host for vulnerabilities ['ILLEGAL' ;) ]");
    System.out.println("backdoor <target>          - Probe a host for vulnerabilities ['ILLEGAL' ;) ]");
    System.out.println("bruteforce <target>        - Break in to a host directly. Can grant Admin or User access ['Illegal' ;) ]");
    System.out.println("checkos                    - Check your OverWatch score ['ILLEGAL' ;) ]");
    System.out.println("search <host/device/file>  - Search for a hidden entity of a specific type.");
    System.out.println("enter-host <target>        - Enter the target host. Must have the right access first.");
    System.out.println("exit-host                  - Exit your current host to the previous host.");
    System.out.println("hosts                      - List all unhidden hosts on the network");
    System.out.println("status                     - Display the status of your deck");
    System.out.println("exit                       - Jack out of the matrix");
    System.out.println("==========================");
  }

  private void printBanner() {
    System.out.println("=========================================");
    System.out.println("   DECKER CONSOLE v0.1 // SHADOWRUN 6E  ");
    System.out.println("=========================================");
    System.out.println("Type 'help' for available commands.");
    System.out.println();
  }

  private Class<?> resolveEntityType(String type){
    switch (type) {
      case "host":    return Host.class;
      default:        return null;
    }
  }

}
