package game;

import java.util.Random;
import java.util.Scanner;

import cli.CommandParser;
import cli.DeckerConsole;
import main.SessionBuilder;
import mission.Mission;
import player.Player;

public class Tutorial {
  private Game game;
  private Player player;
  private Scanner scanner;
  private DeckerConsole console;
  private int step = 0;
  private boolean lastActionSucceed = false;

  // Each step maps to a phase of the tutorial
  private static final int STEP_INTRO             = 0;
  private static final int STEP_HOSTS             = 1;
  private static final int STEP_PROBE             = 2;
  private static final int STEP_BACKDOOR          = 3;
  private static final int STEP_INITIAL_ACCESS    = 4;
  private static final int STEP_SILENT            = 5;
  private static final int STEP_SEARCH            = 6;
  private static final int STEP_BRUTEFORCE        = 7;
  private static final int STEP_TARGET_ACCESS     = 8;
  private static final int STEP_INSIDE            = 9;
  private static final int STEP_OBJECTIVE         = 10;
  private static final int STEP_JACKOUT           = 11;

  public Tutorial(Game game, Player player, Scanner scanner) {
    this.game = game;
    this.player = player;
    this.scanner = scanner;
    this.console = new DeckerConsole(game, player);
  }

  public void start() {
    // Generate a simple rating 1 mission
    SessionBuilder builder = new SessionBuilder();
    Random random = new Random();
    int rating = 1;

    Mission tutorialMission = builder.createTutorialMission(game, rating, game.defaultHost);
    game.currentMission = tutorialMission;

    printHeader();
    runTutorialLoop();
  }

  private void printHeader() {
    System.out.println("\n########################################");
    System.out.println("##         TUTORIAL MODE              ##");
    System.out.println("##   Type 'skip' to exit tutorial     ##");
    System.out.println("########################################\n");
    printStep(STEP_INTRO);
  }

  private void runTutorialLoop() {
    while (game.isActive()) {
      System.out.print("n00b@matrix:~$ ");
      String input = scanner.nextLine().trim();

      if (input.isEmpty()) continue;

      if (input.equalsIgnoreCase("skip")) {
        System.out.println(">> Tutorial skipped. Good luck out there.");
        return;
      }

      String[] parts = input.split(" ");
      CommandParser cmd = CommandParser.parse(parts);

      console.consoleHandler(cmd);
      lastActionSucceed = console.lastActionSucceed;

      // Check loss conditions
      game.checkLoss();

      // Advance tutorial step based on what the player just did
      advanceStep(cmd);

      // Print next hint if game still active
      if (game.isActive() && !game.currentMission.isComplete()) {
        printCurrentHint();
      }

      // Mission complete
      if (game.currentMission.isComplete()) {
        printComplete();
        return;
      }
    }

    // Player lost
    printLossHint();
  }

  private void advanceStep(CommandParser cmd) {
    switch (step) {
      case STEP_INTRO:
        if (cmd.command.equals("hosts")) step = STEP_PROBE;
        else if (cmd.command.equals("help")) step = STEP_HOSTS;
        break;
      case STEP_HOSTS:
        if (cmd.command.equals("hosts")) step = STEP_PROBE;
        break;
      case STEP_PROBE:
        if (cmd.command.equals("probe") && lastActionSucceed){
          step = STEP_BACKDOOR;
        } else if (cmd.command.equals("probe") && !lastActionSucceed){
          printRetryHint(step);
        }
        break;
      case STEP_BACKDOOR:
        if (cmd.command.equals("backdoor") && lastActionSucceed){
          step = STEP_INITIAL_ACCESS;
        } else if (cmd.command.equals("backdoor") && !lastActionSucceed){
          printRetryHint(step);
          step = STEP_PROBE;
        }
        break;
      case STEP_INITIAL_ACCESS:
        if (cmd.command.equals("enter-host")) step = STEP_SILENT;
        break;
      case STEP_SILENT:
        if (cmd.command.equals("run-silent")) step = STEP_SEARCH;
        break;
      case STEP_SEARCH:
        if (cmd.command.equals("search") && lastActionSucceed){
          step = STEP_BRUTEFORCE;
        } else if (cmd.command.equals("search") && !lastActionSucceed){
          printRetryHint(step);
        }
        break;
      case STEP_BRUTEFORCE:
        if (cmd.command.equals("bruteforce") && lastActionSucceed){
          step = STEP_TARGET_ACCESS;
        } else if (cmd.command.equals("bruteforce") && !lastActionSucceed){
          printRetryHint(step);
        }
        break;
      case STEP_TARGET_ACCESS:
        if (cmd.command.equals("enter-host")) step = STEP_INSIDE;
      case STEP_INSIDE:
        if (cmd.command.equals("ls")) step = STEP_OBJECTIVE;
        break;
      case STEP_OBJECTIVE:
        if (game.currentMission.isComplete()) step = STEP_JACKOUT;
        break;
    }
  }

  private void printCurrentHint() {
      printStep(step);
  }

  private void printStep(int step) {
    System.out.println("\n>> [TUTORIAL] --------------------------------");
    switch (step) {
      case STEP_INTRO:
        System.out.println(">> Welcome to the Matrix, Decker.");
        System.out.println(">> You've got a job to do. Let's walk you through it.");
        System.out.println(">> Start by typing 'help' to see your options.");
        System.out.println(">> Or type 'hosts' to see what's on the network.");
        break;
      case STEP_HOSTS:
        System.out.println(">> Your bread and butter are Hosts.");
        System.out.println(">> These are the networks of systems you can target.");
        System.out.println(">> Try 'hosts' to find all available hosts.");
        break;
      case STEP_PROBE:
        System.out.println(">> You can see the hosts on this network.");
        System.out.println(">> To get inside one, you need to gain access.");
        System.out.println(">> Try: probe <hostname>");
        System.out.println(">> PROBE is illegal but quiet - it won't alert the host.");
        System.out.println(">> If it succeeds, you'll have a backdoor in.");
        break;
      case STEP_BACKDOOR:
        System.out.println(">> What just happened was a Contested Roll.");
        System.out.println(">> You rolled a number of dice based on your Attack and Sleaze stats.");
        System.out.println(">> The Host rolled some dice in defense.");
        System.out.println(">> [TIP] Check your stats with the 'status' command.");
        System.out.println(">> Whoever rolled the most 5s or 6s (called Hits) wins!");
        System.out.println(">> Now that you've PROBE-d the host, try and exploit the BACKDOOR you made.");
        System.out.println(">> Try: backdoor <host>");
        break;
      case STEP_INITIAL_ACCESS:
        System.out.println(">> You've successfully exploited the backdoor you set!");
        System.out.println(">> You now have Admin access to the host.");
        System.out.println(">> Hosts require you to have User or Admin access to");
        System.out.println(">> enter them and interact inside.");
        System.out.println(">> Try: enter-host <host>");
        break;
      case STEP_SILENT:
        System.out.println(">> You're in, Decker!");
        System.out.println(">> Now let's make sure you stay in.");
        System.out.println(">> The more time you spend in a Host, the more likely you will get found.");
        System.out.println(">> Security programs, called ICs, will try to find and attack you.");
        System.out.println(">> They'll have a harder time finding you if you're hidden, though.");
        System.out.println(">> Try: 'run-silent'");
        break;
      case STEP_SEARCH:
        System.out.println(">> You're hidden from the host and its ICs now.");
        System.out.println(">> However, if you perform illegal actions (like you're going to)");
        System.out.println(">> the host will know something's wrong and the ICs will search for you.");
        System.out.println(">> You're not the only one that can hide, btw.");
        System.out.println(">> Some hosts and devices are hidden too, like your target.");
        System.out.println(">> Use 'search <type>' to find them.");
        System.out.println(">> Try: search host");
        break;
      case STEP_BRUTEFORCE:
        System.out.println(">> Now that you're properly hidden, you can afford to be a little loud.");
        System.out.println(">> Probe and Backdoor are great, but they take a lot of time.");
        System.out.println(">> When you need in quick, try BRUTEFORCE. It's fast, but be careful - ");
        System.out.println(">> it puts the host on high-alert.");
        System.out.println(">> Try: 'bruteforce <target>' on that host you just discovered.");
        break;
      case STEP_TARGET_ACCESS:
        System.out.println(">> Now that you've BRUTEFORCE-d an access point, you'll need");
        System.out.println(">> to enter the host, just like you did with the BACKDOOR");
        System.out.println(">> Try: enter-host <host>");
        break;
      case STEP_INSIDE:
        System.out.println(">> You're in. Nice work.");
        System.out.println(">> Notice how this host is already looking for you?");
        System.out.println(">> Remember, BRUTEFORCE is loud and fast, PROBE + BACKDOOR");
        System.out.println(">> is lengthy, but silent.");
        System.out.println(">> Now, let's get that Paydata.");
        System.out.println(">> Type 'ls' to list the files on this host.");
        break;
      case STEP_OBJECTIVE:
        System.out.println(">> Check your objectives with 'progress'.");
        System.out.println(">> Complete them to finish the mission.");
        System.out.println(">> Use 'edit <file> -c' to copy a file.");
        System.out.println(">> Use 'edit <file> -d' to delete a file.");
        System.out.println(">> Use 'edit <file> -e \"new contents\"' to tamper with a file.");
        System.out.println(">> For this job, you just need to copy the Paydata to your Deck.");
        System.out.println(">>");
        System.out.println(">> Try 'edit <file> -c'. Check 'progress' to see what the target is.");
        break;
      case STEP_JACKOUT:
        System.out.println(">> Objectives complete. Time to get out.");
        System.out.println(">> Type 'jack-out' to disconnect safely.");
        break;
    }
    System.out.println(">> ---------------------------------------------");
  }

  private void printRetryHint(int step) {
    System.out.println("\n>> [TUTORIAL] --------------------------------");
    switch (step) {
      case STEP_PROBE:
        System.out.println(">> What just happened was a Contested Roll.");
        System.out.println(">> You rolled a number of dice based on your Attack and Sleaze stats.");
        System.out.println(">> The Host rolled some dice in defense.");
        System.out.println(">> [TIP] Check your stats with the 'status' command.");
        System.out.println(">> Whoever rolled the most 5s or 6s (called Hits) wins!");
        System.out.println(">> This time it was the target host. All good, try it again.");
        break;
      case STEP_BACKDOOR:
        System.out.println(">> Looks like the host discovered and removed your backdoor.");
        System.out.println(">> It happens to the best of us. Probe it again and then make");
        System.out.println(">> another attempt at 'backdoor'.");
        break;
      case STEP_SEARCH:
        System.out.println(">> When you run a Search, you'll see the number of hidden");
        System.out.println(">> targets on the network.");
        System.out.println(">> Looks like the search failed but there's still a hidden host");
        System.out.println(">> somewhere here. Search it again!");
        break;
      case STEP_BRUTEFORCE:
        System.out.println(">> Looks like your Bruteforce attempt failed. No worries,");
        System.out.println(">> sometimes the dice aren't in your favor.");
        System.out.println(">> Give it another try!");
        break;
    }
    System.out.println(">> ---------------------------------------------");
  }

  private void printComplete() {
    System.out.println("\n########################################");
    System.out.println("##       TUTORIAL COMPLETE            ##");
    System.out.println("##  You know the basics. Stay sharp.  ##");
    System.out.println("########################################");
    System.out.println(">> Reward: " + game.currentMission.reward + "¥ transferred.");
    System.out.println(">> You're ready for the real thing.\n");
  }

  private void printLossHint() {
    System.out.println("\n>> [TUTORIAL] Don't worry. Better to try again when you're ready than die because you're not.");
    switch (game.gameState) {
      case CONVERGENCE:
        System.out.println(">> Your Overwatch Score hit 40.");
        System.out.println(">> Tip: Legal actions don't raise Overwatch.");
        System.out.println(">> Tip: Use probe and backdoor instead of brute force.");
        break;
      case PLAYER_DEAD:
        System.out.println(">> Biofeedback damage took you out.");
        System.out.println(">> Tip: Avoid Black IC and Blaster IC until you have better gear.");
        break;
      default:
          break;
    }
    System.out.println(">> Try the tutorial again or jump into a real mission.\n");
  }
}