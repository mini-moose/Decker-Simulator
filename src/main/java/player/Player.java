package player;

import java.util.ArrayList;

import data.Cyberjack;
import data.Deck;
import matrix.MatrixEntity;
import matrix.actions.EdgeType;
import matrix.programs.Program;

public class Player extends MatrixEntity {
  // Player-specific stats on top of the shared matrix attributes
  public Deck playerDeck;
  public Cyberjack playerCyberjack;

  public int conditionMonitor = 10;
  public EdgeType edgeType = null;

  public int logic = 6;
  public int willpower = 6;
  public int cracking = 5;
  public int electronics = 5;
  public int intuition = 6;

  public int credits = 0;

  public boolean linkLocked = false;
  public boolean isDetected = false;

  public ArrayList<Program> ownedPrograms = new ArrayList<>();
  public ArrayList<Program> installedPrograms = new ArrayList<>();

  // TODO: Add Program support:
  //    - Add Program.java
  //    - Add at least 4 basic and Hacking programs

  public void equipDeck(Deck deck){
    this.rating = deck.rating;
    this.attack = deck.attack;
    this.sleaze = deck.sleaze;
  }

  public void equipCyberjack(Cyberjack cyberjack){
    this.rating = cyberjack.rating;
    this.dataProcessing = cyberjack.dataProcessing;
    this.firewall = cyberjack.firewall;
  }

  public void equipProgram(Program program){
    program.applyProgramEffect(this);
  }

  public void removeProgram(Program program){
    program.removeProgramEffect(this);
  }

  public Program findOwnedProgram(String name) {
    for (Program p : ownedPrograms) {
      if (p.getName().equalsIgnoreCase(name)) return p;
    }
    return null;
  }

  public Player(Deck startingDeck, Cyberjack startingCyberjack) {
      super(startingDeck.rating); // sets base matrix attributes
      // Override with deck-specific values
      this.playerDeck = startingDeck;
      this.playerCyberjack = startingCyberjack;
      
      this.name = "";

      this.rating = (playerDeck.rating + playerCyberjack.rating) / 2;

      this.devCondition = playerDeck.rating / 2 + 8;
      this.attack = playerDeck.attack;
      this.sleaze = playerDeck.sleaze;
      this.dataProcessing = playerCyberjack.dataProcessing;
      this.firewall = playerCyberjack.firewall;

      this.isAlive = 1;
  }

  @Override
  public int getStat(String statName) {
    switch (statName.toLowerCase()) {
      case "logic":             return logic;
      case "willpower":         return willpower;
      case "cracking":          return cracking;
      case "electronics":       return electronics;
      case "intuition":         return intuition;
      case "conditionmonitor":  return conditionMonitor;
      default: return super.getStat(statName);
    }
  }
}