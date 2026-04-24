package main;

import matrix.MatrixEntity;

import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;

// Handles dice-rolling logic
public class DiceRoller{

  private static final Random random = new Random();

  // Takes Player, testStats; returns value of playerStats where the name equals a name in testStats
  public static int GrabDice(MatrixEntity entity, ArrayList<String> testStats) {
    int dicePool = 0;

    for (String stat : testStats) {
      dicePool += entity.getStat(stat);
    }

    if (entity.isHidden){
      System.out.println(">> Grabbed " + dicePool + " dice for [NAME_REDACTED].");
    } else {
      System.out.println(">> Grabbed " + dicePool + " dice for " + entity.name + ".");
    }

    return dicePool;
  }

  // Takes diceGrab; rolls [diceGrab] number of dice (#d6) and returns results
  public static ArrayList<Integer> RollDice(int dicePool) {
    ArrayList<Integer> results = new ArrayList<>();

    System.out.print("Roll Results: ");
    for (int i=0; i < dicePool; i++) {
      int roll = random.nextInt(6) + 1;
      results.add(roll);
      System.out.print(roll + " ");
      // Rolling every .25 seconds to add dramatic suspense
      try {
        Thread.sleep(400);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }

    System.out.print("\n");
    return results;
  }

  public static HashMap GetHits(ArrayList<Integer> results) {
    int hits = 0;
    int miss = 0;
    int glitch = 0;
    
    for (int roll : results) {
      if (roll >= 5) hits++;
      if (roll < 5) miss++;
      if (roll == 1) glitch++;
    }

    HashMap<String, Integer> totals = new HashMap<String, Integer>();
    totals.put("HIT", hits);
    totals.put("MISS", miss);
    totals.put("GLITCH", glitch);

    return totals;
  }
}