package main;

import cli.DeckerConsole;

import player.Player;

import data.DataLoader;
import data.Deck;

import matrix.Host;
import matrix.actions.Probe;
import matrix.IC;
import matrix.AccessState;

import matrix.ic.Acid;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
  public static void main(String[] args) {
    // Creating test Player
    Map<String, Deck> decks = DataLoader.loadDecks();
    System.out.println("This is Working");
    decks.forEach((id, deck) -> 
      System.out.println(deck.name + " | ATK:" + deck.attack + " FW:" + deck.firewall)
    );
    Deck startingDeck = decks.get("beginners_special");
    Player player = new Player(startingDeck);

    // Creating test Hosts
    ArrayList<Host> pubNetNCL = new ArrayList<Host>();

    Host pubNet = new Host(1, 1, "PubNet", "Quench your thirst with AfterGlow MAXX, available at a QuikMart near you!", false, pubNetNCL);
    pubNet.accessControl.put(player, AccessState.ADMIN_LEGAL);

    String loginMessage = "Welcome to the PolCorp Matrix Environment\nPlease respect the rules of our network\nOur directory can be found at /PolCorp/Directory/";
    
    ArrayList<Host> dmzNCL = new ArrayList<Host>(Arrays.asList(pubNet));
    Host dmz = new Host(2, 2, "DMZ", loginMessage, false, dmzNCL); // Easy, Defensive Host as "DMZ"
    
    ArrayList<Host> securityNCL = new ArrayList<Host>(Arrays.asList(dmz));
    Host security = new Host(4, 1, "SECURITY", loginMessage, true, securityNCL); // Tough, Aggressive Host as "Security"
    
    Game game = new Game(player);
    game.addHost(pubNet);
    game.addHost(dmz);
    game.addHost(security);

    game.currentHost = pubNet;

    Acid acidIC = new Acid(security);

    game.addIC(security, acidIC);

    ArrayList<IC> securityICs = game.getHostIC(security);

    DeckerConsole console = new DeckerConsole(game, player);
    console.start();

  }
}