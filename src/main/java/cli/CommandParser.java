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

    for (int i = 1; i < parts.length; i++) {
      String part = parts[i];

      if (part.startsWith("--") || part.startsWith("-")) {
        String key = part.startsWith("--") ? part.substring(2) : part.substring(1);

        if (i + 1 < parts.length && !parts[i + 1].startsWith("-")) {
          if (parts[i + 1].startsWith("'") || parts[i + 1].startsWith("\"")) {
            StringBuilder quoted = new StringBuilder();
            char quote = parts[++i].charAt(0);
            quoted.append(parts[i].substring(1));

            while (i + 1 < parts.length && !parts[i].endsWith(String.valueOf(quote))) {
              quoted.append(" ").append(parts[++i]);
            }

            String value = quoted.toString();
            if (value.endsWith(String.valueOf(quote))) {
              value = value.substring(0, value.length() - 1);
            }
            result.options.put(key, value);
          } else {
              result.options.put(key, parts[++i]);
          }
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
