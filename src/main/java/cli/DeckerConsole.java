package cli;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import game.ActionResult;
import game.Game;
import game.GameState;
import game.TurnManager;
import game.UI;
import main.Debug;
import matrix.Host;
import matrix.MatrixEntity;
import matrix.actions.Action;
import matrix.actions.Backdoor;
import matrix.actions.BruteForce;
import matrix.actions.CheckOS;
import matrix.actions.Crack;
import matrix.actions.Dataspike;
import matrix.actions.Edge;
import matrix.actions.EdgeType;
import matrix.actions.EditFile;
import matrix.actions.EnterHost;
import matrix.actions.ExitHost;
import matrix.actions.Probe;
import matrix.actions.RunSilent;
import matrix.actions.Search;
import matrix.actions.Spoof;
import matrix.device.Commands;
import matrix.device.Device;
import matrix.files.HostFile;
import mission.ObjectiveType;
import player.Player;


public class DeckerConsole {

  private Game game;
  private Player player;
  private TurnManager turnManager;
  private ConsoleHeader header;

  private UI ui;

  private Scanner console = new Scanner(System.in);
  private Random random = new Random();

  public boolean lastActionSucceed = false;

  public DeckerConsole(Game game, Player player){
    this.game = game;
    this.player = player;
    this.header = new ConsoleHeader(game, player, turnManager);
    this.turnManager = new TurnManager(game, player);
    this.ui = new UI();
  }

  public void start(){
    ui.Clear();
    header.print();
    printBanner();
    while (game.isActive()) {

      System.out.println(game.currentHost.loginMessage.get(random.nextInt(game.currentHost.loginMessage.size())));
      System.out.print(player.name + "@matrix:~$ ");
      String input = console.nextLine().trim();

      if (input.isEmpty()) continue;

      game.clearTurnLog();

      String[] parts = input.split(" ");
      CommandParser cmd = CommandParser.parse(parts);

      turnManager.applyEdgeEffect(player);

      Action lastAction = consoleHandler(cmd);

      ui.Loading(5);

      if (game.isActive()){
        turnManager.onPlayerActionTaken(lastAction);
      }

      if (game.isActive()){
        ui.Clear();
        header.print();
      }
    }
  }

  public Action consoleHandler(CommandParser cmd){
    Action lastAction;

      switch (cmd.command) {
        case "probe":
          lastAction = handleProbe(cmd);
          return lastAction;
        case "checkos":
          handleCheckOS(cmd);
          break;
        case "backdoor":
          lastAction = handleBackdoor(cmd);
          return lastAction;
        case "bruteforce":
          handleBruteForce(cmd);
          break;
        case "dataspike":
          handleDataSpike(cmd);
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
        case "spoof":
          handleSpoof(cmd);
          break;
        case "crack":
          handleCrack(cmd);
          break;
        case "describe":
          handleDescribe(cmd);
          break;
        case "edit":
          handleEditFile(cmd);
          break;
        case "run-silent":
          handleRunSilent(cmd);
          break;
        case "edge":
          handleEdge(cmd);
          break;
        case "hosts":
          handleHosts();
          break;
        case "devs":
          handleDev();
          break;
        case "ls":
          handleListFiles();
          break;
        case "status":
          handleStatus();
          break;
        case "progress":
          handleObjectives();
          break;
        case "help":
          handleHelp();
          break;
        case "exit":
          System.out.println("Jacking out...");
          game.gameState = GameState.JACKED_OUT;
          return null;
        default:
          System.out.println("Unknown command: " + cmd.command + ". Type 'help' for options.");
      }
    return null;
  }

  // Attempt to establish a backdoor exploit on the target system
  // Success: A backdoor exploit is established and can use the backdoor action
  // Failure: Backdoor exploit not established
  private Action handleProbe(CommandParser cmd){
    // Usage: probe <hostname or host UP>
    if (cmd.hasOption("help") || cmd.hasOption("h") || cmd.positionalArgs.isEmpty()){
      System.out.println("Probe a host for vulnerabilities.");
      System.out.println("Notes: Extended Action, Illegal");
      System.out.println("Options:");
      System.out.println("  -r --repeat-attempts 'Repeat command # times or until succeed'");
      System.out.println("Usage: probe <target>");
      System.out.println("Example: probe UnirealCorp-DMZ-01");
      System.out.println("Example: probe UnirealCorp-DMZ-01 -r 3");
      return null;
    }

    Host target = game.findHost(cmd.positionalArgs.get(0));

    if (target == null){
      System.out.println("[ERROR] HOST_NOT_FOUND: " + cmd.positionalArgs.get(0) + " is not a host on your network.");
      System.out.println("Use 'hosts' to list available hosts.");
      return null;
    }

    int attempts = cmd.getIntOption("r", cmd.getIntOption("repeat", 1));
    
    Probe probe = new Probe();

    for (int i=0; i < attempts; i++){
      if (attempts > 1) System.out.println("Attempt " + (i + 1) + " of " + attempts);

      ActionResult result = probe.execute(game, player, target);
      System.out.println(result);
      
      lastActionSucceed = result.success;
      game.logTurn(result.message);

      if (result.success) { break; }
    }
    return probe;
  }

  // Exploit a backdoor set by Probe action
  // Success: Gives attacker Legal Admin access
  // Failure: Defender finds and removes backdoor
  private Action handleBackdoor(CommandParser cmd){
    if (cmd.hasOption("help") || cmd.hasOption("h") || cmd.positionalArgs.isEmpty()){
      System.out.println("Leverage a Backdoor on the target system.");
      System.out.println("Notes: Major Action, Illegal");
      System.out.println("Options:");
      System.out.println("  -r --repeat-attempts 'Repeat command # times or until succeed'");
      System.out.println("Usage: backdoor <target>");
      System.out.println("Example: backdoor UnirealCorp-DMZ-01");
      System.out.println("Example: backdoor UnirealCorp-DMZ-01 -r 3");
      return null;
    }

    Host target = game.findHost(cmd.positionalArgs.get(0));

    // If the target is not in the list of discoverable hosts, return an errror
    if (target == null){
      System.out.println("[ERROR] HOST_NOT_FOUND: " + cmd.positionalArgs.get(0) + " is not a host on your network.");
      System.out.println("Use 'hosts' to list available hosts.");
      return null;
    }

    if (!target.hasBackdoor){
      System.out.println("[ERROR] HOST_NO_EXPLOIT: " + cmd.positionalArgs.get(0) + " does not have an established backdoor. Did you forget to Probe first?");
      return null;
    }

    int attempts = cmd.getIntOption("r", cmd.getIntOption("repeat", 1));

    Backdoor backdoor = new Backdoor();

    for (int i=0; i < attempts; i++){
      if (attempts > 1) System.out.println("Attempt " + (i + 1) + " of " + attempts);

      ActionResult result = backdoor.execute(game, player, target);
      System.out.println(result);

      lastActionSucceed = result.success;
      game.logTurn(result.message);

      if (result.success) break;
    }
    return backdoor;
  }


  // Checks your current Overwatch Score
  // Success: You find your Overwatch Score
  // Failure: You are unable to find your Overwatch Score
  private Action handleCheckOS(CommandParser cmd){
    if (cmd.hasOption("help") || cmd.hasOption("h")){
      System.out.println("ERROR [HOST_NOT_FOUND]: You are not currently connected to a host");
      return null;
    }
    turnManager.applyEdgeEffect(player);

    CheckOS checkOS = new CheckOS();
    ActionResult result = checkOS.execute(game, player, game.currentHost);
    System.out.println(result);

    return checkOS;
  }

  private Action handleBruteForce(CommandParser cmd){
    // Usage: probe <hostname or host UP>
    if (cmd.hasOption("help") || cmd.hasOption("h") || cmd.positionalArgs.isEmpty()){
      System.out.println("Brute Force your way into a system. Will alert the target.");
      System.out.println("Notes: Major Action, Illegal");
      System.out.println("Options:");
      System.out.println("  -a --access <level>  Access level to force (user/admin). Defaults to User");
      System.out.println("  -r --repeat-attempts 'Repeat command # times or until succeed'");
      System.out.println("Usage: bruteforce <target>");
      System.out.println("Example: bruteforce UnirealCorp-DMZ-01 --access admin");
      System.out.println("Example: bruteforce UnirealCorp-DMZ-01 --access user -r 3 ");
      return null;
    }

    Host target = game.findHost(cmd.positionalArgs.get(0));

    // If the target is not in the list of discoverable hosts, return an errror
    if (target == null){
      System.out.println("[ERROR] HOST_NOT_FOUND: " + cmd.positionalArgs.get(0) + " is not a host on your network.");
      System.out.println("Use 'hosts' to list available hosts.");
      return null;
    }

    int attempts = cmd.getIntOption("r", cmd.getIntOption("repeat", 1));

    String access = cmd.getOption("access", cmd.getOption("a", "user"));

    BruteForce bruteforce = new BruteForce(access);

    for (int i=0; i < attempts; i++){
      if (attempts > 1) System.out.println("Attempt " + (i + 1) + " of " + attempts);

      ActionResult result = bruteforce.execute(game, player, target);
      System.out.println(result);

      lastActionSucceed = result.success;
      game.logTurn(result.message);

      if (result.success) break;
    }
    return bruteforce;
  }

  private Action handleDataSpike(CommandParser cmd){
    if (cmd.hasOption("help") || cmd.hasOption("h") || cmd.positionalArgs.isEmpty()){
      System.out.println("Send a large set of harmful instructions to a target Device.");
      System.out.println("Notes: Major Action, Illegal");
      System.out.println("Options:");
      System.out.println("Usage: dataspike <target>");
      System.out.println("Example: bruteforce camera_42");
      return null;
    }

    Device target = game.currentHost.findDevice(cmd.positionalArgs.get(0));

    if (target == null){
      System.out.println("[ERROR] DEVICE_NOT_FOUND: " + cmd.positionalArgs.get(0) + " is not a device on your current host.");
      System.out.println("Use 'devs' to list available devices on the host.");
      return null;
    }

    Dataspike dataspike = new Dataspike();
    ActionResult result = dataspike.execute(game, player, target);
    System.out.println(result);

    game.logTurn(result.message);

    return dataspike;
  }

  private Action handleSpoof(CommandParser cmd){
    if (cmd.hasOption("help") || cmd.hasOption("h") || cmd.positionalArgs.isEmpty()){
      System.out.println("Send a command to a target Device that it will attempt to execute.");
      System.out.println("Notes: Major Action, Illegal");
      System.out.println("Options:");
      System.out.println("Usage: dataspike <target>");
      System.out.println("Example: bruteforce camera_42");
      return null;
    }

    Device target = game.currentHost.findDevice(cmd.positionalArgs.get(0));

    if (cmd.positionalArgs.get(1).isEmpty()){
      System.out.println("[ERROR] You must specify a command for the target.");
      return null;
    }

    if (target == null){
      System.out.println("[ERROR] DEVICE_NOT_FOUND: " + cmd.positionalArgs.get(0) + " is not a device on your current host.");
      System.out.println("Use 'devs' to list available devices on the host.");
      return null;
    }

    Commands command = target.getCommand(cmd.positionalArgs.get(1));

    if (command == null){
      System.out.println("[ERROR] COMMAND_NOT_FOUND: " + cmd.positionalArgs.get(0) + " does not support the command: " + cmd.positionalArgs.get(1) + ".");
      System.out.println("Use 'describe <device>' to list available commands for the device.");
      return null;
    }

    Spoof spoof = new Spoof();
    ActionResult result = spoof.execute(game, player, target);

    System.out.println(result);
    game.logTurn(result.message);

    if(result.success) {
      game.checkCommandComplete(ObjectiveType.DISABLE_DEVICE, target, command);
    }

    return spoof;
  }

  // Search Action
  // Requires: MatrixEntity type (host, file, device, icon)
  // Search for a hidden system on or connected to your current Host
  // Success: You discover the system you were looking for
  // Failure: You do not discover the system you were looking for
  private Action handleSearch(CommandParser cmd){
    if (cmd.hasOption("help") || cmd.hasOption("h") || cmd.positionalArgs.isEmpty()) {
      System.out.println("Search your current Host for Programs that are in Run Silent mode.");
      System.out.println("Notes: Major Action, Legal");
      System.out.println("Usage: search <entity_type>");
      System.out.println("Example: search Host");
      System.out.println("Example: search File");
      return null;
    }

    String entityType = cmd.positionalArgs.get(0);

    Class<?> targetClass = game.resolveEntityType(entityType);
    if (targetClass == null){
      System.out.println("[ERROR] ENTITY_UNKNOWN_TYPE: The Matrix Entity type '" + entityType + "' is unknown.");
      System.out.println("Valid types: host, device, file, ic, agent");
      return null;
    }

    ArrayList<MatrixEntity> targets = game.findHiddenEntities(targetClass);

    if (targets.isEmpty()) {
      System.out.println("[INFO] ENTITY_NO_SIGNATURES: There are no Matrix signatures on this host for hidden " + entityType + " entities.");
      return null;
    }

    System.out.println("[INFO] ENTITY_DETECT_SIGNATURES: Matrix Scan detected " + targets.size() + " hidden " + entityType + " on this host.");


    Search search = new Search();
    for (MatrixEntity target : targets) {
      ActionResult result = search.execute(game, player, target);
      game.logTurn(result.message);
      System.out.println(result);
    }
    
    return search;
  }

  private void handleDescribe(CommandParser cmd){
    if (cmd.hasOption("help") || cmd.hasOption("h") || cmd.positionalArgs.isEmpty()){
      System.out.println("Describe a file, device, or IC on the host.");
      System.out.println("Notes: Minor Action, Legal");
      System.out.println("Usage: describe <type> <target>");
      System.out.println("Example: describe file PayData");
      System.out.println("Example: describe ic Patrol");
      System.out.println("Example: describe device Camera_1");
      return;
    }

    String entityType = cmd.positionalArgs.get(0);

    Class<?> targetClass = game.resolveEntityType(entityType);
    if (targetClass == null){
      System.out.println("[ERROR] ENTITY_UNKNOWN_TYPE: The Matrix Entity type '" + entityType + "' is unknown.");
      System.out.println("Valid types: device, file, ic");
      return;
    }

    String targetName = cmd.positionalArgs.get(1);
    if (targetClass.equals(HostFile.class)){
      HostFile target = null;

      for (HostFile file : game.currentHost.filesOnHost) {
        if (file.name.equalsIgnoreCase(targetName)){
          target = file;
          break;
        }
      }
      // If the file is not in the list of discoverable files, return an errror
      if (target == null){
        System.out.println("[ERROR] FILE_NOT_FOUND: " + cmd.positionalArgs.get(1) + " is not on your current Host.");
        System.out.println("Use 'ls' to list available files.");
        return;
      }

      if (target.isEncrypted) {
        System.out.println("[ERROR] FILE_ENCRYPTED: " + cmd.positionalArgs.get(0) + " is currently Encrypted.");
      } else {
        System.out.println(target.name + " Contents: " + target.contents);
      }
    } else if (targetClass.equals(Device.class)){
      Device target = null;

      for (Device device : game.currentHost.devicesOnHost) {
        if (device.name.equalsIgnoreCase(targetName)){
          Debug.log(device.name);
          target = device;
          break;
        }
      }
      // If the file is not in the list of discoverable files, return an errror
      if (target == null){
        System.out.println("[ERROR] DEVICE_NOT_FOUND: " + cmd.positionalArgs.get(1) + " is not on your current Host.");
        System.out.println("Use 'devs' to list available files.");
        return;
      }
      System.out.println(target.name + " Available Commands: ");
      for (Commands command : target.devCommands){
        System.out.println(command.toString());
      }
    }
  }

  private Action handleCrack(CommandParser cmd){
    if (cmd.hasOption("help") || cmd.hasOption("h") || cmd.positionalArgs.isEmpty()){
      System.out.println("Decrypt a File on a host. You must do this before editing an encrypted File.");
      System.out.println("Notes: Major Action, Illegal");
      System.out.println("Options:");
      System.out.println("Usage: crack <target>");
      System.out.println("Example: crack PayData");
      return null;
    }
    String targetName = cmd.positionalArgs.get(0);

    HostFile target = game.currentHost.findFile(targetName);

    // If the target is not in the list of discoverable hosts, return an errror
    if (target == null){
      System.out.println("[ERROR] FILE_NOT_FOUND: " + targetName + " is not on your current Host.");
      System.out.println("Use 'ls' to list available files.");
      return null;
    }

    if (!target.isEncrypted){
      System.out.println("[ERROR] FILE_NOT_ENCRYPTED: " + targetName + " is not Encrypted.");
      return null;
    }

    turnManager.applyEdgeEffect(player);

    Crack decrypt = new Crack();
    ActionResult result = decrypt.execute(game, player, target);

    game.logTurn(result.message);
    System.out.println(result);

    return decrypt;
  }

  private Action handleEditFile(CommandParser cmd){
    if (cmd.hasOption("help") || cmd.hasOption("h") || cmd.positionalArgs.isEmpty()){
      System.out.println("Edit a File on a host. You can change, copy, or delete the target File.");
      System.out.println("Notes: Major Action, Legal");
      System.out.println("Options:");
      System.out.println("  -e --edit <'New file contents'>  'Edit the contents of the File'");
      System.out.println("  -c --copy  'Copy the target File to your Deck'.");
      System.out.println("  -d --delete  'Delete the target File'.");
      System.out.println("Usage: edit <target>");
      System.out.println("Example: edit PayData -c");
      System.out.println("Example: edit PayData -e 'Decker_name was here.'");
      return null;
    }
    String targetName = cmd.positionalArgs.get(0);

    HostFile target = game.currentHost.findFile(targetName);

    // If the target is not in the list of discoverable hosts, return an errror
    if (target == null){
      System.out.println("[ERROR] FILE_NOT_FOUND: " + targetName + " is not on your current Host.");
      System.out.println("Use 'ls' to list available files.");
      return null;
    }

    if (target.isEncrypted){
      System.out.println("[ERROR] FILE_ENCRYPTED: " + targetName + " is currently Encrypted.");
      return null;
    }

    EditFile editFile = new EditFile();
    ActionResult result = editFile.execute(game, player, target);

    game.logTurn(result.message);
    System.out.println(result);

    if (result.success){
      if (cmd.hasOption("e") || cmd.hasOption("edit")){ // If editing, change description to match edit.
        String newContents = cmd.getOption("e", cmd.getOption("edit", null));
        if (newContents == null) {
          System.out.println("[ERROR] No new contents was defined");
          return editFile;
        }
        target.contents = newContents;
        game.checkObjectiveComplete(ObjectiveType.TAMPER_FILE, target); // Completes objective Tamper File
        System.out.println("[INFO] FILE_EDITED: Contents of '" + target.name + "' now reads: '" + target.contents + "'.");
        return editFile;
      } else if (cmd.hasOption("d") || cmd.hasOption("delete")){
        game.currentHost.filesOnHost.remove(target);
        game.checkObjectiveComplete(ObjectiveType.DELETE_FILE, target); // Completes objective Delete File
        System.out.println("[INFO] FILE_DELETED: '" + target.name + "' has been deleted.");
        return editFile;
      } else if (cmd.hasOption("c") || cmd.hasOption("copy")){
        game.checkObjectiveComplete(ObjectiveType.EXFIL_FILE, target); // Completes objective Delete File
        System.out.println("[INFO] FILE_COPIED: '" + target.name + "' has been copied to your Deck.");
        return editFile;
      } 
    }
    return null;
  }

  private Action handleEnterHost(CommandParser cmd){
    if (cmd.hasOption("help") || cmd.hasOption("h") || cmd.positionalArgs.isEmpty()){
      System.out.println("Enter a Host you have access to");
      System.out.println("Notes: Simple Action, Legal");
      System.out.println("Usage: enter-host <target>");
      System.out.println("Example: enter-host UnirealCorp-DMZ-01");
      System.out.println("Example: enter-host UnirealCorp-DMZ-01 -r 3");
      return null;
    }

    Host target = game.findHost(cmd.positionalArgs.get(0));

    if (target == null){
      System.out.println("[ERROR] HOST_NOT_FOUND: " + cmd.positionalArgs.get(0) + " is not a host on your network.");
      System.out.println("Use 'hosts' to list available hosts.");
      return null;
    }

    EnterHost enterHost = new EnterHost();
    ActionResult result = enterHost.execute(game, player, target);

    game.logTurn(result.message);
    System.out.println(result);

    return enterHost;
  }

  private Action handleExitHost(CommandParser cmd){
    if (cmd.hasOption("help") || cmd.hasOption("h")){
      System.out.println("Exit from your current host to your previous host");
      System.out.println("Notes: Simple Action, Legal");
      System.out.println("Usage: exit-host");
      System.out.println("Options");
      System.out.println("    -d --default  Exit to your default Home host (Usually PubNet)");
      System.out.println("Example: exit-host");
      System.out.println("Example: exit-host --default");
      return null;
    }

    ExitHost exitHost = new ExitHost();
    ActionResult result;

    if (cmd.hasOption("default") || cmd.hasOption("d")) {
      if (game.defaultHost == null) {
        System.out.println("[ERROR] NO_DEFAULT_HOST: No default host is set.");
        return null;
      }
      result = exitHost.execute(game, player, game.defaultHost);
    } else {
      if (game.parentHost == null) {
        System.out.println("[ERROR] NO_PREVIOUS_HOST: No parent host to return to.");
        return null;
      }
      result = exitHost.execute(game, player, game.parentHost);
    }

    game.logTurn(result.message);
    System.out.println(result);
    
    return exitHost;
  }

  private Action handleRunSilent(CommandParser cmd){
    if (cmd.hasOption("help") || cmd.hasOption("h")){
      System.out.println("Configure to run silent. Hides you from passive detection, but ICs might still search for you.");
      System.out.println("Notes: Major Action, Legal");
      System.out.println("Usage: run-silent");
      return null;
    }

    RunSilent runSilent = new RunSilent();
    ActionResult result;

    result = runSilent.execute(game, player, game.currentHost);

    game.logTurn(result.message);
    System.out.println(result);
    
    return runSilent;
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

  private void handleDev() {
    // Usage: Shows available hosts on your network.
    // DOES NOT SHOW HOSTS THAT ARE HIDING, PERCEPTION MAY BE REQUIRED TO REVEAL HIDDEN HOSTS
    Host targetHost = game.currentHost;
    if (targetHost.devicesOnHost.isEmpty()){
      System.out.println("No devices detected on host.");
      return;
    }
    System.out.println("=== DETECTED DEVICES ON HOST ===");
    for (Device device : targetHost.devicesOnHost) {
        System.out.println(device.name);
    }
    System.out.println("=================================");
  }

  private void handleListFiles() {
    ArrayList<HostFile> allFiles = game.getAllFiles();
    System.out.println("=== HOST FILESYSTEM PARSED ===");
    for (HostFile file : allFiles){
      if (file.isDirectory){
        for (HostFile subFile : file.filesInDirectory){
          System.out.println(subFile.name);
        }
      } else {
        System.out.println(file.name);
      }
    }
    System.out.println("==============================");
  }

  private Action handleEdge(CommandParser cmd){
    if (cmd.positionalArgs.get(0).equals("help") || cmd.positionalArgs.get(0).equals("h") || cmd.positionalArgs.isEmpty()){
      System.out.println("Give yourself an Edge to help you perform difficult actions.");
      System.out.println("Notes: Simple Action, Legal");
      System.out.println("Usage: edge <type>");
      System.out.println("Options");
      System.out.println("    overclock  Increase your Attack and Sleaze // Decrease your DataProcessing and Firewall.");
      System.out.println("    shield     Increase your DataProcessing and Firewall // Decrease your Attack and Sleaze.");
      System.out.println("    scramble   Increase your Overwatch score. The next time a Patrol would spot you, stay hidden.");
      System.out.println("Example: edge overclock");
      System.out.println("Example: edge scramble");
      return null;
    }

    String edgeTypeInput = cmd.positionalArgs.get(0);
    Edge edge;
    ActionResult result;

    switch(edgeTypeInput){
      case("overclock") -> {
          edge = new Edge(EdgeType.OVERCLOCK);
          result = edge.execute(game, player, game.currentHost);
          game.logTurn(result.message);
          System.out.println(result);
          return edge;
          }
      case("shield") -> {
          edge = new Edge(EdgeType.SHIELD);
          result = edge.execute(game, player, game.currentHost);
          game.logTurn(result.message);
          System.out.println(result);
          return edge;
          }
      case("scramble") -> {
          edge = new Edge(EdgeType.SCRAMBLE);
          result = edge.execute(game, player, game.currentHost);
          game.logTurn(result.message);
          System.out.println(result);
          return edge;
          }
      default -> System.out.println("[ERROR] INVALID_OPTION: " + edgeTypeInput + " is not a valid option.");
    }
    return null;
  }

  private void handleObjectives(){
    System.out.println("=== Current Mission Objectives ===");
    game.getObjectives();
    System.out.println("==================================");
  }

  private void handleStatus(){
    System.out.println("=== DECKER STATUS ===");
    System.out.println("ATTACK:            " + player.attack);
    System.out.println("FIREWALL:          " + player.firewall);
    System.out.println("DATA_PROCESSING:   " + player.dataProcessing);
    System.out.println("SLEAZE:            " + player.sleaze);
    System.out.println("DECK_CONDITION     " + player.devCondition);
    System.out.println("CONDITION:         " + player.conditionMonitor);
    System.out.println("CURRENT_HOST:      " + game.currentHost.name);
    System.out.println("=====================");
  }

  private void handleHelp(){
    System.out.println("=== AVAILABLE COMMANDS ===");
    System.out.println("===       ACCESS       ===");
    System.out.println("{ Attack, Sleaze }            probe <target>             - Probe a host for vulnerabilities.");
    System.out.println("{ Attack, Sleaze }            backdoor <target>          - Exploit a Backdoor you set with Probe. Grants Admin access.");
    System.out.println("{ Attack, Attack }            bruteforce <target>        - Break in to a host directly. Can grant Admin or User access.");
    System.out.println("{ No Check }                  enter-host <target>        - Enter the target host. Must have at least User access first.");
    System.out.println("{ No Check }                  exit-host                  - Exit your current host to the previous host.");
    System.out.println("===        RECON       ===");
    System.out.println("{ Sleaze, Data-Processing }   search <host/device/file>  - Search for a hidden entity of a specific type.");
    System.out.println("{ No Check }                  ls                         - List all files in your current host");
    System.out.println("{ No Check }                  hosts                      - List all unhidden hosts on the network");
    System.out.println("{ No Check }                  describe                   - Describe a File, Device, or IC on your current host");
    System.out.println("===   FILE OPERATIONS  ===");
    System.out.println("{ Attack, Sleaze }            crack <file>               - Attempt to break a File's encryption.");
    System.out.println("{ Attack, Data-Processing }   edit <file>                - Edit, copy, or delete a File. Can only be performed on unencrypted Files.");
    System.out.println("===  DEVICE OPERATIONS ===");
    System.out.println("{ Attack, Data-Processing }   dataspike <device>         - Attempt to damage a Device by sending a large amount of harmful instructions.");
    System.out.println("{ No Check }                  devs                       - List all devices in your current host");
    System.out.println("===       UTILITY      ===");
    System.out.println("{ Sleaze, Data-Processing }   checkos                    - Check your OverWatch score.");
    System.out.println("{ Sleaze, Sleaze }            run-silent:                - Hide your digital signature from the Host. ICs can still spot you with a successful check.");
    System.out.println("{ Firewall, Data-Processing } exit                       - Jack out of the matrix. Needs a check if you're link-locked.");
    System.out.println("{ No Check }                  edge <type>                - Give yourself an Edge on the next action.");
    System.out.println("{ No Check }                  status                     - Display the status of your deck");
    System.out.println("==========================");
  }

  private void printBanner() {
    System.out.println("=========================================");
    System.out.println("   DECKER CONSOLE v0.1 // SHADOWRUN 6E  ");
    System.out.println("=========================================");
    System.out.println("Type 'help' for available commands.");
    System.out.println();
  }

}
