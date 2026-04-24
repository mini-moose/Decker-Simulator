package matrix.actions;

import matrix.actions.StatSource;

public class StatEntry {
  public String statName;
  public StatSource source;

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
}
