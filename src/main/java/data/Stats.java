package data;

import java.util.HashMap;

public class Stats {
  public HashMap<String, Integer> basic_stats(){
    HashMap<String, Integer> basic_stats = new HashMap<String, Integer>();

    basic_stats.put("Logic", 0);
    basic_stats.put("Willpower", 0);
    basic_stats.put("Cracking", 0);
    basic_stats.put("Electronics", 0);

    return basic_stats;
  }

  public HashMap<String, Integer> matrix_attributes(){
    HashMap<String, Integer> basic_stats = new HashMap<String, Integer>();

    basic_stats.put("Attack", 0);
    basic_stats.put("Sleaze", 0);
    basic_stats.put("Data Processing", 0);
    basic_stats.put("Firewall", 0);

    return basic_stats;
  }
}
