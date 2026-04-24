package cli;

import java.util.HashMap;
import java.util.ArrayList;

// Provides parsing functionality to support multi-argument and options for commands
public class CommandParser {
  public String command;
  public ArrayList<String> positionalArgs = new ArrayList<>();
  public HashMap<String, String> options = new HashMap<>();

  public static CommandParser parse(String[] parts) {
    CommandParser result = new CommandParser();
    result.command = parts[0].toLowerCase();

    for (int i=0; i < parts.length; i++) {
      String part = parts[i];

      if (part.startsWith("--")){
        String key = part.substring(2);
        if (i + 1 < parts.length && !parts[i + 1].startsWith("-")){
          result.options.put(key, parts[++i]);
        } else {
          result.options.put(key, "true");
        }
      } else if (part.startsWith("-")){
        String key = part.substring(1);
        if (i + 1 < parts.length && !parts[i + 1].startsWith("-")){
          result.options.put(key, parts[++i]);
        } else {
          result.options.put(key, "true");
        }
      } else {
        result.positionalArgs.add(part);
      }
    }
    return result;
  }

  public boolean hasOption(String key){
    return options.containsKey(key);
  }

  public String getOption(String key, String defaultValue){
    return options.getOrDefault(key, defaultValue);
  }

  public int getIntOption(String key, int defaultValue){
    try {
      return Integer.parseInt(options.getOrDefault(key, String.valueOf(defaultValue)));
    } catch (NumberFormatException e){
      return defaultValue;
    }
  }
}
