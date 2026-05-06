package cli;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import data.Cyberjack;
import data.CyberjackFactory;
import data.Deck;
import data.DeckFactory;
import game.Game;
import game.GameState;
import game.Tutorial;
import game.UI;
import main.Save;
import main.SessionBuilder;
import matrix.Host;
import matrix.programs.Program;
import matrix.programs.ProgramFactory;
import mission.Mission;
import mission.Objective;
import player.Player;

public class OutMission {

  public boolean hasSaveData;

  private Random random = new Random();

  public void handleTutorial(Game game, Player player, Scanner scanner, Host defaultHost) {
    System.out.println(">> Never jacked in before? Let's fix that.");
    System.out.println(">> This'll walk you through the basics. Type 'skip' at any time.");

    // Reset game state cleanly for tutorial
    game.overWatchScore = 0;
    game.gameState = GameState.ACTIVE;

    Tutorial tutorial = new Tutorial(game, player, scanner);
    tutorial.start();

    // Clean up after tutorial
    game.resetGame(defaultHost);
    game.currentMission = null;
    game.gameState = GameState.ACTIVE;
  }

  public ArrayList<Mission> generateMissionSelect(Game game, Player player){
    ArrayList<Mission> missions = new ArrayList<>();

    missions.addAll(game.missionSelect(game, random, player.rating));

    return missions;
  }

  public boolean handleMissionSelect(Game game, Player player, UI ui, Scanner scanner, ArrayList<Mission> missions){

    SessionBuilder session = new SessionBuilder();

    System.out.print(">> Fetching available missions from Mr./Mrs. Jones ");
    ui.Loading(3);
    System.out.println("\n######## RETRIEVED MISSIONS AND TARGETS. COMPENSATION NON-NEGOTIABLE ########");
    for (int i=0; i < missions.size(); i++){
      System.out.println((i + 1) + ". " + missions.get(i));
    }
    while (true){
      System.out.println(">> Select a Mission by inputting the line number, or type 'exit' if you need some time to prepare...");
      String input = scanner.nextLine();

      boolean response;

      if (input.isEmpty()) continue;
      switch (input.toLowerCase()) {
        case "1":
          session.createSession(game, missions.get(0));
          response = acceptMission(game, missions.get(0), player, scanner);
          return response;
        case "2":
          session.createSession(game, missions.get(1));
          response = acceptMission(game, missions.get(1), player, scanner);
          return response;
        case "3":
          session.createSession(game, missions.get(2));
          response = acceptMission(game, missions.get(2), player, scanner);
          return response;
        case "exit":
          return false;
      }
    }
  }

  public void handleConfig(Game game, Player player, Scanner scanner, UI ui){
    System.out.println(">> Fetching available programs.");
    ui.Loading(3);
    System.out.println(">> Currently owned programs: ");

    for (Program program : player.ownedPrograms){
      System.out.println("Program: " + program.getName() + " | Effect: " + program.getDescription() + " | Installed: " + program.isInstalled);
    }
    
    System.out.println(">> Select program to install by typing the name of the program. Type 'exit' to return to main menu.");
    while(true){

      String input = scanner.nextLine().trim();

      if (input.isEmpty()) continue;
      if (input.equalsIgnoreCase("exit")) break;

      if (ProgramFactory.findByName(player.ownedPrograms, input) == null){
        System.out.println(">> Doesn't look like you own that program. Check the store to see what's on sale.");
      }

      Program program = ProgramFactory.findByName(player.ownedPrograms, input);

      if (player.installedPrograms.contains(program)){
        System.out.println(">> You currently have that program installed. Would you like to uninstall it? Y/n");
        String choice = scanner.nextLine().trim().toLowerCase();

        if (choice.equals("y")){
          program.isInstalled = false;
          program.removeProgramEffect(player);
          player.installedPrograms.remove(program);
          System.out.println(">> The selected program was uninstalled.");
          break;
        } else {
          System.out.println(">> Cancelling program uninstall.");
          break;
        }
      }
      if (!player.installedPrograms.contains(program) && player.installedPrograms.size() < player.playerDeck.programSlots) {
        System.out.println(">> Would you like to install this program? Y/n");
        String choice = scanner.nextLine().trim().toLowerCase();

        if (choice.isEmpty()) continue;
        if (choice.equals("y")){
          program.isInstalled = true;
          program.applyProgramEffect(player);
          player.installedPrograms.add(program);
          System.out.println(">> The selected program was installed.");
          break;
        } else {
          System.out.println(">> Cancelling program install.");
          break;
        }
      } else {
        System.out.println(">> You currently have the maximum installed programs for your Deck. Program Slots: " + player.playerDeck.programSlots);
        break;
      }
    }
  }

  public void handleHelp(Game game, Player player, UI ui){
    System.out.println(">> Available options: Config, Mission, Rules, Store, Tutorial, Exit");
  }

  public void handleRules(){
    System.out.println("################ GUIDE TO THE MATRIX ################");
    System.out.println(">>                    Starting                     <<");
    System.out.println(">> At the start of the game, you have bare-minimum   ");
    System.out.println(">> devices and stats. You won't be able to take on   ");
    System.out.println(">> the big jobs until you get some upgrades.         ");
    System.out.println(">> Upgrades cost money, and you get money from compl-");
    System.out.println(">> eting missions. Go to the 'mission' menu to find  ");
    System.out.println(">> your available missions.                          ");
    System.out.println(">>                                                 <<");
    System.out.println(">>                    Missions                     <<");
    System.out.println(">> When you enter a mission, you'll have a list of   ");
    System.out.println(">> Objectives to complete. You can get these with    ");
    System.out.println(">> the 'progress' command.                           ");
    System.out.println(">>                                                   ");
    System.out.println(">> Get in, complete your objectives, and get out.    ");
    System.out.println(">> The longer you stay in a system, the more danger  ");
    System.out.println(">> there is.                                         ");
    System.out.println(">>                                                 <<");
    System.out.println(">>              Winning and Losing                 <<");
    System.out.println(">> Performing illegal actions will raise your        ");
    System.out.println(">> OverWatch Score. If that gets to 40, you're done. ");
    System.out.println(">> Not just 'try again' done - your device is        ");
    System.out.println(">> bricked and OW agents come get you wherever you   ");
    System.out.println(">> are holed up. Best get to running...              ");
    System.out.println(">>                                                   ");
    System.out.println(">> Some IC (enemy programs) can also deal real-world ");
    System.out.println(">> damage. Take too many hits and you're also done.  ");
    System.out.println(">>                                                   ");
    System.out.println(">> You can also choose to quit a mission if the heat ");
    System.out.println(">> gets too bad. Better to try another one.          ");
    System.out.println(">>                                                   ");
    System.out.println(">> You win missions by completing all objectives.    ");
    System.out.println(">> You lose missions by quiting or failing an objec- ");
    System.out.println(">> tive. You won't get money, but you won't die.     ");
    System.out.println(">>                                                   ");
    System.out.println(">>                   Progression                   <<");
    System.out.println(">> Use the credits you get from completing mission   ");
    System.out.println(">> to buy new equipment.                             ");
    System.out.println(">> There's three types of equipment you can buy:     ");
    System.out.println(">>                                                   ");
    System.out.println(">> Cyberjacks - Determine your Firewall and Data     ");
    System.out.println(">> Processing. Better ones make it easier to defend  ");
    System.out.println(">> against attacks and handle files and devices.     ");
    System.out.println(">>                                                   ");
    System.out.println(">> Cyberdecks - Determine your Attack and Sleaze     ");
    System.out.println(">> Better ones make it hack into things and makes    ");
    System.out.println(">> your attacks deal more damage.                    ");
    System.out.println(">>                                                   ");
    System.out.println(">> Programs - You can install Programs onto your     ");
    System.out.println(">> Cyberdeck to give you small buffs to certain      ");
    System.out.println(">> actions and situations.                           ");
    System.out.println(">>                                                   ");
  }

  public void handleStore(Game game, Player player, UI ui, Scanner scanner){
    System.out.print(">> Accessing market.grey ");
    ui.Loading(3);
    System.out.println("\n######## WELCOME, VALUED CUSTOMER. ########");
    
    while (true){
      System.out.println(">> What're ya buying?");
      System.out.println(">> Enter Deck or Cyberjack to view available inventory. Or 'exit' to leave the store.");
      String input = scanner.nextLine().trim();

      if (input.isEmpty()) continue;
      switch (input.toLowerCase()) {
        case "deck":
          handlePurchaseDeck(player, ui, scanner);
          break;
        case "cyberjack":
          Map<String, Cyberjack> cyberjacks = CyberjackFactory.loadCyberjacks();
          cyberjacks.forEach((id, cyberjack) -> {
            System.out.println("-------------------------------------------------------------------------------");
            System.out.println(cyberjack.id + ") " + cyberjack.name + " | " + cyberjack.rating + " |  Attack: " + cyberjack.dataProcessing + " // Sleaze " + cyberjack.firewall + " || Value: " + cyberjack.cost);
            System.out.println(cyberjack.description);
          });
        case "program":
          handlePurchaseProgram(game, player, ui, scanner);
        case "exit":
          return;
      }      
    }
  }

  public void handlePurchaseProgram(Game game, Player player, UI ui, Scanner scanner) {
    System.out.println("Got some good Programs on sale, Decker");

    ArrayList<Program> programs = ProgramFactory.loadPrograms();

    for (Program program : programs) {
      System.out.println("-------------------------------------------------------------------------------");
      System.out.println(program.getName() + " | " + program.getDescription() + " | Cost: " + program.getCost() + "¥"
        + (program.isIllegal() ? " [ILLEGAL]" : ""));
    }

    while (true) {
      System.out.println("-------------------------------------------------------------------------------");
      System.out.println(">> Current funds: " + player.credits + "¥");
      System.out.println(">> Enter a program name to purchase, or 'exit' to leave.");

      String input = scanner.nextLine().trim();

      if (input.isEmpty()) continue;
      if (input.equalsIgnoreCase("exit")) break;

      Program program = ProgramFactory.findByName(programs, input);

      if (program == null) {
        System.out.println("Looks like we don't have that in our selection.");
        continue;
      }

      if (player.ownedPrograms.contains(program)) {
        System.out.println("You already have " + program.getName() + " installed.");
        continue;
      }

      System.out.println("The " + program.getName() + " huh? Looking to buy? (y/n)");
      String confirm = scanner.nextLine().trim();

      switch (confirm.toLowerCase()) {
        case "y":
          if (player.credits >= program.getCost()) {
            player.credits -= program.getCost();
            player.ownedPrograms.add(program);
            System.out.println("Pleasure doing business.");
            Save.save(player);
          } else {
            System.out.println("This isn't a charity mate, come back when you have more nuyen!");
          }
          break;
        case "n":
          System.out.println("No deal. Come back when you're ready to spend.");
            break;
        default:
          System.out.println("Need a y or n, Decker.");
      }
    }
  }

  public void handlePurchaseDeck(Player player, UI ui, Scanner scanner){
    System.out.println("Got a selection of fine Decks on sale, Decker");
    Map<String, Deck> decks = DeckFactory.loadDecks();
    decks.forEach((id, deck) -> {
      System.out.println("-------------------------------------------------------------------------------");
      System.out.println("ID: " + deck.id + ") " + deck.name + " | " + deck.rating + " |  Attack: " + deck.attack + " // Sleaze " + deck.sleaze + " || Value: " + deck.cost);
      System.out.println(deck.description);
    });
    while (true){
      System.out.println("-------------------------------------------------------------------------------");
      System.out.println(">> Current funds: " + player.credits + " ¥" );
      System.out.println(">> Which one are you interested in?");
      System.out.println(">> Enter the Deck ID or 'exit' to return to the store.");
      String input = scanner.nextLine().trim();

      if (input.isEmpty()) continue;
      if (input.equalsIgnoreCase("exit")) break;

      if (!decks.containsKey(input)) {
        System.out.println("Looks like we don't have that in our selection.");
        continue;
      }

      Deck selected = decks.get(input);
      System.out.println("The " + selected.name + " huh? That's a nice piece, looking to buy? Y/n");

      String confirm = scanner.nextLine().trim();
      switch (confirm.toLowerCase()){
        case "y":
          if (player.credits >= selected.cost){
            player.credits -= selected.cost;
            player.equipDeck(selected);
            System.out.println("Haha, thank you.");
            Save.save(player);
          } else {
            System.out.println("This isn't a charity mate, come back when you have more nuyen!");
          }
          break;
        case "n":
          System.out.println("No deal huh? Come back when you're looking to spend some nuyen!");
          break;
        case "exit":
          break;
        default:
          System.out.println("Need a Y/n, Decker.");
      }
    }      
  }

  public boolean acceptMission(Game game, Mission mission, Player player, Scanner scanner){
    if (mission.rating > player.rating + 4){
      missionSummary(mission, "It's a difficult job, Decker // Respond: Y/n");
    } else if (mission.rating > player.rating + 2){
      missionSummary(mission, "It's a challenging job, Decker // Respond: Y/n");
    } else {
      missionSummary(mission, "It's an easy job, Decker // Respond: Y/n");
    }

    while(true){
      String input = scanner.nextLine();
      switch(input.toLowerCase()) {
        case "y":
          game.currentMission = mission;
          return true;
        case "n":
          return false;
        default:
          System.out.println(">> Going to need a Y/n from you, Decker.");
      }
    }
  }
  private void missionSummary(Mission mission, String prompt){
    switch (mission.type) {
      case DATA_EXFIL:
        System.out.println("#################### JOB SUMMARY ####################");
        System.out.println(">> Decker, your target is " + mission.targetCorp      );
        System.out.println(">> The client needs you to extract some PayData from ");
        System.out.println(">> " + mission.targetHost.name                        );
        System.out.println(">> Your objectives are as follows:"                   );
        for (Objective obj : mission.objectives){
          System.out.println(">> " + obj                                          );
        }
        System.out.println(">> " + prompt                                         );
        break;
      case MATRIX_ASSIST:
        System.out.println("#################### JOB SUMMARY ####################");
        System.out.println(">> Decker, your target is " + mission.targetCorp      );
        System.out.println(">> The client is a team of Runners who need you      ");
        System.out.println(">> running interference while they do their job      ");
        System.out.println(">> Your objectives are as follows:"                   );
        for (Objective obj : mission.objectives){
          System.out.println(">> " + obj                                          );
        }
        System.out.println(">> " + prompt                                         );
        break;   
      case SABOTAGE:
        System.out.println("#################### JOB SUMMARY ####################");
        System.out.println(">> Decker, your target is " + mission.targetCorp      );
        System.out.println(">> The client is a discrete one. They need to you    ");
        System.out.println(">> deal some damage inside the target's matrix.      ");
        System.out.println(">> Your objectives are as follows:"                   );
        for (Objective obj : mission.objectives){
          System.out.println(">> " + obj                                          );
        }
        System.out.println(">> " + prompt                                         );
        break;
      case DATA_TAMPER:
        System.out.println("#################### JOB SUMMARY ####################");
        System.out.println(">> Decker, your target is " + mission.targetCorp      );
        System.out.println(">> The client needs you to tamper with some data     ");
        System.out.println(">> in the target's matrix.                           ");
        System.out.println(">> Your objectives are as follows:"                   );
        for (Objective obj : mission.objectives){
          System.out.println(">> " + obj                                          );
        }
        System.out.println(">> " + prompt                                         );
        break;
    }
    

  }
}
