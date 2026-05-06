package matrix.actions;

import matrix.actions.StatSource;

public class StatEntry {

  public String statName;
  public String substituteStat;

  public int flatValue;
  
  public StatSource source;
  public StatSource substituteSource;

  public StatEntry(String statName, StatSource source) {
    this.statName = statName;
    this.source = source;
  }

  public static StatEntry host(String stat) {
    return new StatEntry(stat, StatSource.HOST);
  }

  public static StatEntry spider(String stat) {
    return new StatEntry(stat, StatSource.SPIDER);
  }

  public static StatEntry flat(int value) {
    StatEntry entry = new StatEntry(null, StatSource.FLAT);
    entry.flatValue = value;
    return entry;
  }

  public static StatEntry flatWithBonus(int baseValue) {
    StatEntry entry = new StatEntry(null, StatSource.FLAT);
    entry.flatValue = baseValue;
    return entry;
  }

  public static StatEntry withSubstitute(String primaryStat, StatSource primarySource, String subStat, StatSource subSource, int subMultiplier){
    StatEntry entry = new StatEntry(primaryStat, primarySource);
    entry.source = primarySource;
    entry.substituteStat = subStat;
    entry.substituteSource = subSource;
    entry.flatValue = subMultiplier;
    return entry;
  }
}
