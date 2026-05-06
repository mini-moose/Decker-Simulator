package main;

import matrix.MatrixEntity;

import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;
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
    String chars = "!@#$%^&*-~=+";

    for (int i = 0; i < dicePool; i++) {
      int roll = random.nextInt(6) + 1; // roll the die first
      results.add(roll);                // add to results immediately

      // Scramble frames before settling on result
      for (int frame = 0; frame < 6; frame++) {
        StringBuilder display = new StringBuilder("Roll Results: ");

        // Already settled dice
        for (int settled = 0; settled < i; settled++) {
          display.append(results.get(settled)).append(" ");
        }

        // Currently scrambling
        display.append(chars.charAt(random.nextInt(chars.length()))).append(" ");

        // Pending dice
        for (int pending = i + 1; pending < dicePool; pending++) {
          display.append("_ ");
        }

        System.out.print("\r" + display);
        System.out.flush();

        try {
          Thread.sleep(60); // fast scramble frames
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    }

    // Print final settled line
    StringBuilder finalDisplay = new StringBuilder("Roll Results: ");
    for (int result : results) {
      finalDisplay.append(result).append(" ");
    }
    System.out.println("\r" + finalDisplay);

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