package cli;

import game.ActionResult;
import game.Game;
import game.GameState;
import game.TurnManager;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import main.Debug;
import matrix.Host;
import matrix.MatrixEntity;
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

  private Scanner console = new Scanner(System.in);
  private Random random = new Random();

  public boolean lastActionSucceed = false;

  public DeckerConsole(Game game, Player player){
    this.game = game;
    this.player = player;
    this.turnManager = new TurnManager(game, player);
  }

  public void start(){
    printBanner();
    while (game.isActive()) {

      System.out.println(game.currentHost.loginMessage.get(random.nextInt(game.currentHost.loginMessage.size())));
      System.out.print(player.name + "@matrix:~$ ");
      String input = console.nextLine().trim();

      if (input.isEmpty()) continue;

      String[] parts = input.split(" ");
      CommandParser cmd = CommandParser.parse(parts);

      consoleHandler(cmd);
    }
  }

  public void consoleHandler(CommandParser cmd){
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
          return;
        default:
          System.out.println("Unknown command: " + cmd.command + ". Type 'help' for options.");
      }
  }

  // Attempt to establish a backdoor exploit on the target system
  // Success: A backdoor exploit is established and can use the backdoor action
  // Failure: Backdoor exploit not established
  private void handleProbe(CommandParser cmd){
    // Usage: probe <hostname or host UP>
    if (cmd.hasOption("help") || cmd.hasOption("h") || cmd.positionalArgs.isEmpty()){
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
    turnManager.applyEdgeEffect(player);

    int attempts = cmd.getIntOption("r", cmd.getIntOption("repeat", 1));

    for (int i=0; i < attempts; i++){
      if (attempts > 1) System.out.println("Attempt " + (i + 1) + " of " + attempts);

      Probe probe = new Probe();
      ActionResult result = probe.execute(game, player, target);
      System.out.println(result);
      turnManager.onPlayerActionTaken(probe);

      lastActionSucceed = result.success;

      if (result.success) { break; }
    }
  }

  // Exploit a backdoor set by Probe action
  // Success: Gives attacker Legal Admin access
  // Failure: Defender finds and removes backdoor
  private void handleBackdoor(CommandParser cmd){
    if (cmd.hasOption("help") || cmd.hasOption("h") || cmd.positionalArgs.isEmpty()){
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
    turnManager.applyEdgeEffect(player);

    int attempts = cmd.getIntOption("r", cmd.getIntOption("repeat", 1));

    for (int i=0; i < attempts; i++){
      if (attempts > 1) System.out.println("Attempt " + (i + 1) + " of " + attempts);

      Backdoor backdoor = new Backdoor();
      ActionResult result = backdoor.execute(game, player, target);
      System.out.println(result);

      turnManager.onPlayerActionTaken(backdoor);

      lastActionSucceed = result.success;

      if (result.success) break;
    }
  }


  // Checks your current Overwatch Score
  // Success: You find your Overwatch Score
  // Failure: You are unable to find your Overwatch Score
  private void handleCheckOS(CommandParser cmd){
    if (cmd.hasOption("help") || cmd.hasOption("h")){
      System.out.println("ERROR [HOST_NOT_FOUND]: You are not currently connected to a host");
      return;
    }
    turnManager.applyEdgeEffect(player);

    CheckOS checkOS = new CheckOS();
    ActionResult result = checkOS.execute(game, player, game.currentHost);
    System.out.println(result);

    turnManager.onPlayerActionTaken(checkOS);
  }

  private void handleBruteForce(CommandParser cmd){
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
    turnManager.applyEdgeEffect(player);

    for (int i=0; i < attempts; i++){
      if (attempts > 1) System.out.println("Attempt " + (i + 1) + " of " + attempts);

      BruteForce bruteforce = new BruteForce(access);
      ActionResult result = bruteforce.execute(game, player, target);
      System.out.println(result);

      turnManager.onPlayerActionTaken(bruteforce);

      lastActionSucceed = result.success;

      if (result.success) break;
    }
  }

  private void handleDataSpike(CommandParser cmd){
    if (cmd.hasOption("help") || cmd.hasOption("h") || cmd.positionalArgs.isEmpty()){
      System.out.println("Send a large set of harmful instructions to a target Device.");
      System.out.println("Notes: Major Action, Illegal");
      System.out.println("Options:");
      System.out.println("Usage: dataspike <target>");
      System.out.println("Example: bruteforce camera_42");
      return;
    }

    Device target = game.currentHost.findDevice(cmd.positionalArgs.get(0));

    if (target == null){
      System.out.println("[ERROR] DEVICE_NOT_FOUND: " + cmd.positionalArgs.get(0) + " is not a device on your current host.");
      System.out.println("Use 'devs' to list available devices on the host.");
      return;
    }

    Dataspike dataspike = new Dataspike();
    ActionResult result = dataspike.execute(game, player, target);
    System.out.println(result);

    turnManager.onPlayerActionTaken(dataspike);

    lastActionSucceed = result.success;
  }

  private void handleSpoof(CommandParser cmd){
    if (cmd.hasOption("help") || cmd.hasOption("h") || cmd.positionalArgs.isEmpty()){
      System.out.println("Send a command to a target Device that it will attempt to execute.");
      System.out.println("Notes: Major Action, Illegal");
      System.out.println("Options:");
      System.out.println("Usage: dataspike <target>");
      System.out.println("Example: bruteforce camera_42");
      return;
    }

    Device target = game.currentHost.findDevice(cmd.positionalArgs.get(0));

    if (cmd.positionalArgs.get(1).isEmpty()){
      System.out.println("[ERROR] You must specify a command for the target.");
      return;
    }

    if (target == null){
      System.out.println("[ERROR] DEVICE_NOT_FOUND: " + cmd.positionalArgs.get(0) + " is not a device on your current host.");
      System.out.println("Use 'devs' to list available devices on the host.");
      return;
    }

    Commands command = target.getCommand(cmd.positionalArgs.get(1));

    if (command == null){
      System.out.println("[ERROR] COMMAND_NOT_FOUND: " + cmd.positionalArgs.get(0) + " does not support the command: " + cmd.positionalArgs.get(1) + ".");
      System.out.println("Use 'describe <device>' to list available commands for the device.");
      return;
    }

    turnManager.applyEdgeEffect(player);


    Spoof spoof = new Spoof();
    ActionResult result = spoof.execute(game, player, target);

    System.out.println(result);
  
    if(result.success) {
      game.checkCommandComplete(ObjectiveType.DISABLE_DEVICE, target, command);
    }

    turnManager.onPlayerActionTaken(spoof);
  }

  // Search Action
  // Requires: MatrixEntity type (host, file, device, icon)
  // Search for a hidden system on or connected to your current Host
  // Success: You discover the system you were looking for
  // Failure: You do not discover the system you were looking for
  private void handleSearch(CommandParser cmd){
    if (cmd.hasOption("help") || cmd.hasOption("h") || cmd.positionalArgs.isEmpty()) {
      System.out.println("Search your current Host for Programs that are in Run Silent mode.");
      System.out.println("Notes: Major Action, Legal");
      System.out.println("Usage: search <entity_type>");
      System.out.println("Example: search Host");
      System.out.println("Example: search File");
      return;
    }

    String entityType = cmd.positionalArgs.get(0);

    Class<?> targetClass = game.resolveEntityType(entityType);
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

    turnManager.applyEdgeEffect(player);

    Search search = new Search();
    for (MatrixEntity target : targets) {
      ActionResult result = search.execute(game, player, target);
      lastActionSucceed = result.success;
      System.out.println(result);
    }

    turnManager.onPlayerActionTaken(search);
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

  private void handleCrack(CommandParser cmd){
    if (cmd.hasOption("help") || cmd.hasOption("h") || cmd.positionalArgs.isEmpty()){
      System.out.println("Decrypt a File on a host. You must do this before editing an encrypted File.");
      System.out.println("Notes: Major Action, Illegal");
      System.out.println("Options:");
      System.out.println("Usage: crack <target>");
      System.out.println("Example: crack PayData");
      return;
    }
    String targetName = cmd.positionalArgs.get(0);

    HostFile target = game.currentHost.findFile(targetName);

    // If the target is not in the list of discoverable hosts, return an errror
    if (target == null){
      System.out.println("[ERROR] FILE_NOT_FOUND: " + targetName + " is not on your current Host.");
      System.out.println("Use 'ls' to list available files.");
      return;
    }

    if (!target.isEncrypted){
      System.out.println("[ERROR] FILE_NOT_ENCRYPTED: " + targetName + " is not Encrypted.");
      return;
    }

    turnManager.applyEdgeEffect(player);

    Crack decrypt = new Crack();
    ActionResult result = decrypt.execute(game, player, target);

    lastActionSucceed = result.success;
    System.out.println(result);

    turnManager.onPlayerActionTaken(decrypt);
  }

  private void handleEditFile(CommandParser cmd){
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
      return;
    }
    String targetName = cmd.positionalArgs.get(0);

    HostFile target = game.currentHost.findFile(targetName);

    // If the target is not in the list of discoverable hosts, return an errror
    if (target == null){
      System.out.println("[ERROR] FILE_NOT_FOUND: " + targetName + " is not on your current Host.");
      System.out.println("Use 'ls' to list available files.");
      return;
    }

    if (target.isEncrypted){
      System.out.println("[ERROR] FILE_ENCRYPTED: " + targetName + " is currently Encrypted.");
      return;
    }

    turnManager.applyEdgeEffect(player);

    EditFile editFile = new EditFile();
    ActionResult result = editFile.execute(game, player, target);
    System.out.println(result);

    if (result.success){
      if (cmd.hasOption("e") || cmd.hasOption("edit")){ // If editing, change description to match edit.
        String newContents = cmd.getOption("e", cmd.getOption("edit", null));
        if (newContents == null) {
          System.out.println("[ERROR] No new contents was defined");
          return;
        }
        target.contents = newContents;
        game.checkObjectiveComplete(ObjectiveType.TAMPER_FILE, target); // Completes objective Tamper File
        System.out.println("[INFO] FILE_EDITED: Contents of '" + target.name + "' now reads: '" + target.contents + "'.");
        return;
      } else if (cmd.hasOption("d") || cmd.hasOption("delete")){
        game.currentHost.filesOnHost.remove(target);
        game.checkObjectiveComplete(ObjectiveType.DELETE_FILE, target); // Completes objective Delete File
        System.out.println("[INFO] FILE_DELETED: '" + target.name + "' has been deleted.");
        return;
      } else if (cmd.hasOption("c") || cmd.hasOption("copy")){
        game.checkObjectiveComplete(ObjectiveType.EXFIL_FILE, target); // Completes objective Delete File
        System.out.println("[INFO] FILE_COPIED: '" + target.name + "' has been copied to your Deck.");
        return;
      } 
    }

    lastActionSucceed = result.success;

    turnManager.onPlayerActionTaken(editFile);
  }

  private void handleEnterHost(CommandParser cmd){
    if (cmd.hasOption("help") || cmd.hasOption("h") || cmd.positionalArgs.isEmpty()){
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
    turnManager.onPlayerActionTaken(enterHost);
  }

  private void handleExitHost(CommandParser cmd){
    if (cmd.hasOption("help") || cmd.hasOption("h")){
      System.out.println("Exit from your current host to your previous host");
      System.out.println("Notes: Simple Action, Legal");
      System.out.println("Usage: exit-host");
      System.out.println("Options");
      System.out.println("    -d --default  Exit to your default Home host (Usually PubNet)");
      System.out.println("Example: exit-host");
      System.out.println("Example: exit-host --default");
      return;
    }

    ExitHost exitHost = new ExitHost();
    ActionResult result;

    if (cmd.hasOption("default") || cmd.hasOption("d")) {
      if (game.defaultHost == null) {
        System.out.println("[ERROR] NO_DEFAULT_HOST: No default host is set.");
        return;
      }
      result = exitHost.execute(game, player, game.defaultHost);
    } else {
      if (game.parentHost == null) {
        System.out.println("[ERROR] NO_PREVIOUS_HOST: No parent host to return to.");
        return;
      }
      result = exitHost.execute(game, player, game.parentHost);
    }

    System.out.println(result);

    turnManager.onPlayerActionTaken(exitHost);
  }

  private void handleRunSilent(CommandParser cmd){
    if (cmd.hasOption("help") || cmd.hasOption("h")){
      System.out.println("Configure to run silent. Hides you from passive detection, but ICs might still search for you.");
      System.out.println("Notes: Major Action, Legal");
      System.out.println("Usage: run-silent");
      return;
    }

    RunSilent runSilent = new RunSilent();
    ActionResult result;

    result = runSilent.execute(game, player, game.currentHost);

    System.out.println(result);

    turnManager.onPlayerActionTaken(runSilent);
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

  private void handleEdge(CommandParser cmd){
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
      return;
    }

    String edgeTypeInput = cmd.positionalArgs.get(0);
    Edge edge;
    ActionResult result;

    switch(edgeTypeInput){
      case("overclock"):
        edge = new Edge(EdgeType.OVERCLOCK);
        result = edge.execute(game, player, game.currentHost);

        System.out.println(result);
        break;
      case("shield"):
        edge = new Edge(EdgeType.SHIELD);
        result = edge.execute(game, player, game.currentHost);

        System.out.println(result);
        break;
      case("scramble"):
        edge = new Edge(EdgeType.SCRAMBLE);
        result = edge.execute(game, player, game.currentHost);

        System.out.println(result);
        break;
      default:
        System.out.println("[ERROR] INVALID_OPTION: " + edgeTypeInput + " is not a valid option.");
        break;
    }
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
