package main;

public class Debug {
  private static boolean enabled = false;

  public static void enable() {
    enabled = true;
  }

  public static boolean isEnabled() {
    return enabled;
  }

  // Drop-in replacement for System.out.println
  public static void log(String message) {
    if (enabled) {
      System.out.println("[DEBUG] " + message);
    }
  }

  // Useful for printing object state
  public static void log(String label, Object value) {
    if (enabled) {
      System.out.println("[DEBUG] " + label + ": " + value);
    }
  }
}