package player;

import data.Deck;
import matrix.MatrixEntity;

public class Player extends MatrixEntity {
    // Player-specific stats on top of the shared matrix attributes
    public int logic = 6;
    public int willpower = 6;
    public int cracking = 5;
    public int electronics = 5;
    public int intuition = 6;

    public int conditionMonitor = 10;

    public String accessLevel = "Outsider";

    public Player(Deck startingDeck) {
        super(startingDeck.rating); // sets base matrix attributes
        // Override with deck-specific values
        this.name = "";

        this.devCondition = startingDeck.rating / 2 + 8;
        this.attack = startingDeck.attack;
        this.sleaze = startingDeck.sleaze;
        this.dataProcessing = startingDeck.dataProcessing;
        this.firewall = startingDeck.firewall;

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