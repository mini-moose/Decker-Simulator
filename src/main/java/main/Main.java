package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

import cli.DeckerConsole;
import cli.OutMission;
import data.Cyberjack;
import data.CyberjackFactory;
import data.Deck;
import data.DeckFactory;
import game.Game;
import game.GameState;
import game.UI;
import matrix.AccessState;
import matrix.Host;
import matrix.SecurityType;
import matrix.programs.Toolbox;
import mission.Mission;
import player.Player;

public class Main {
  public static void main(String[] args) {

    // Debug mode
    for (String arg : args) {
      if (arg.equalsIgnoreCase("--debug")) {
        Debug.enable();
        Debug.log("Debug mode enabled");
        break;
      }
    }

    UI ui = new UI();
    Scanner scanner = new Scanner(System.in);
    Player player;

    // Check if there's a SAVE file, if yes load data, if no create new player.
    if (Save.hasSave()){
      System.out.println(">> Save data found, load? Y/n");
      String input = scanner.nextLine().trim();
      if (input.equalsIgnoreCase("y")){
        player = Save.load();
        if (player == null) {
          System.out.println(">> Save corrupted. Sorry, loading a fresh save.");
          player = createNewPlayer(scanner, ui);
        }
        } else {
          player = createNewPlayer(scanner, ui);
        }
      } else {
        player = createNewPlayer(scanner, ui);
      }

    Game game = new Game(player);

    // Generating default host PubNet
    ArrayList<Host> pubNetNCL = new ArrayList<>();
    ArrayList<String> pubNetBanner = new ArrayList<>(Arrays.asList(
      "###########  Quench your thirst with AfterGlow MAXX, available at a QuikMart near you!  ##########\n",
      "########  See something suspicious on the Matrix? Ping OverWatch! Rewards of up to 50 ¥!  ########\n",
      "######  Rent due? No cash? RentDaddy's got your back! Pay today's rent, tomorrow! Or else.  ######\n"
    ));

    // TESTING PROGRAMS
    Toolbox toolbox = new Toolbox();
    player.ownedPrograms.add(toolbox);

    // END TEST

    Host pubNet = new Host(1, 1, SecurityType.PUBLIC, "PubNet", pubNetBanner, false, pubNetNCL);
    pubNet.accessControl.put(player, AccessState.ADMIN_LEGAL);

    // Game Loop Logic

    // Set base game state with PubNet as default and only Host
    game.resetGame(pubNet);

    OutMission outmission = new OutMission();
    DeckerConsole console = new DeckerConsole(game, player);

    System.out.println("\n======== WELCOME TO THE MATRIX, DECKER ========");
    System.out.println(">> Type 'help' at any time to get your options.");

    // Pre-generate Missions
    ArrayList<Mission> missions = outmission.generateMissionSelect(game, player);
    boolean missionStale = false;

    while (true) {
      System.out.print(">> ");
      String input = scanner.nextLine();

      boolean response;

      if (missionStale){
        System.out.println(">> NEW_MESSAGE: from:Mr/Mrs. Jones // message: Hey Decker, I've got some more work lined up for you.");
        missions = outmission.generateMissionSelect(game, player);
        missionStale = false;
      }

      if (input.isEmpty()) continue;

      switch (input.toLowerCase()) {
        case "help":
          outmission.handleHelp(game, player, ui);
          break;
        case "tutorial":
          outmission.handleTutorial(game, player, scanner, pubNet);
          break;
        case "config":
          outmission.handleConfig(game, player, scanner, ui);
          break;
        case "store":
          outmission.handleStore(game, player, ui, scanner);
          break;
        case "mission":
          response = outmission.handleMissionSelect(game, player, ui, scanner, missions);
          if (response){
            console.start();

            missionStale = true;

            game.printMissionSummary(game, player);
            if (game.gameState == GameState.MISSION_COMPLETE) {
              Save.save(player);
            }
            game.resetGame(pubNet);
          }
          game.resetGame(pubNet);
          break;
        case "rules":
          outmission.handleRules();
          break;
        case "exit":
          return;
      }
    }
  }

  private static Player createNewPlayer(Scanner scanner, UI ui){
    System.out.print("[INFO] Booting up Decker.os");
    ui.Loading(5);
    System.out.println(" Boot Process Complete!");
    
    // Creating Player

    // Loading Available CyberDecks
    Map<String, Deck> decks = DeckFactory.loadDecks();

    // Loading Available Cyberjacks
    Map<String, Cyberjack> cyberjacks = CyberjackFactory.loadCyberjacks();

    Deck startingDeck = decks.get("erika_m");
    Cyberjack startingCyberjack = cyberjacks.get("level_1");

    Player player = new Player(startingDeck, startingCyberjack);

    String playerName = player.name;

    while (playerName.isEmpty()) {
      System.out.println("What's your handle, decker?");
      playerName = scanner.nextLine().trim();
      if (playerName.isEmpty()){
        System.out.println("Trying for incognito huh? Do us both a favor and just pick a name, yea?");
      } else {
        player.name = playerName;
        break;
      }
    }

    // Playing intro...
    System.out.print(">> Welcome: " + playerName);
    ui.Loading(5);
    System.out.println("\n>> You are a Decker, a Hacker-for-Hire.");
    ui.Loading(10);
    System.out.println("\n>> Your missions are your own to choose.");
    ui.Loading(10);
    System.out.println("\n>> Complete missions, earn cash, and upgrade yourself to complete harder missions.");
    ui.Loading(10);
    System.out.println("\n>> We'll be watching your progress, Decker.");
    ui.Loading(10);

    return player;
  }
}