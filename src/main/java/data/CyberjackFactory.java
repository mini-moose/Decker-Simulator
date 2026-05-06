package data;

import java.util.HashMap;
import java.util.Map;

public class CyberjackFactory {
  public static Map<String, Cyberjack> loadCyberjacks() {
    Map<String, Cyberjack> cyberjacks = new HashMap<>();

    cyberjacks.put("level_1", new Cyberjack(
      "level_1",
      "Level 1 Cyberjack",
      1,
      4,
      3,
      1,
      24750,
      "Rudimentary, but still expensive. Ad-free subscription is 5000¥ extra."
    ));

    cyberjacks.put("level_2", new Cyberjack(
      "level_2",
      "Level 2 Cyberjack",
      2,
      5,
      4,
      1,
      65000,
      "A step above what the rookies use. Reliable, relatively cheap, and sturdy enough to take some hits."
    ));

    cyberjacks.put("level_3", new Cyberjack(
      "level_3",
      "Level 3 Cyberjack",
      3,
      6,
      5,
      1,
      80000,
      "For those that need high-speed data links to do completely legal and in no way malicious actions on the Matrix."
    ));

    return cyberjacks;
  }
}