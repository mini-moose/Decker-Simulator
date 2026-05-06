package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import data.Cyberjack;
import data.CyberjackFactory;
import data.Deck;
import data.DeckFactory;
import matrix.programs.Program;
import matrix.programs.ProgramFactory;
import player.Player;

public class Save {
  private static final String SAVE_FILE = "save.dat";

  public static void save(Player player) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE))) {
      writer.write("name=" + player.name);
      writer.newLine();
      writer.write("credits=" + player.credits);
      writer.newLine();
      writer.write("deck=" + player.playerDeck.id);
      writer.newLine();
      writer.write("cyberjack=" + player.playerCyberjack.id);
      writer.newLine();
      StringBuilder ownedPrograms = new StringBuilder();
      for (Program program : player.ownedPrograms){
        ownedPrograms.append(program.getName()).append(",");
      }
      writer.write("ownedPrograms=" + ownedPrograms.toString());
      writer.newLine();
      StringBuilder installedPrograms = new StringBuilder();
      for (Program program : player.installedPrograms){
        installedPrograms.append(program.getName()).append(",");
      }
      writer.write("installedPrograms=" + installedPrograms.toString());
      writer.newLine();
      System.out.println("[INFO] Game saved.");
    } catch (IOException e) {
      System.out.println("[ERROR] SAVE_FAILED: Could not write save file.");
      Debug.log("Save error: " + e.getMessage());
    }
  }

  public static boolean hasSave() {
    return new File(SAVE_FILE).exists();
  }

  public static Player load() {
    Map<String, Deck> decks = DeckFactory.loadDecks();
    Map<String, Cyberjack> cyberjacks = CyberjackFactory.loadCyberjacks();
    ArrayList<Program> allPrograms = ProgramFactory.loadPrograms();

    try (BufferedReader reader = new BufferedReader(new FileReader(SAVE_FILE))) {
      String name = "";
      int credits = 0;
      String deckId = "";
      String cyberjackId = "";

      // Store program names as strings until player is instantiated
      ArrayList<String> ownedProgramNames = new ArrayList<>();
      ArrayList<String> installedProgramNames = new ArrayList<>();

      String line;
      while ((line = reader.readLine()) != null) {
        String[] parts = line.split("=", 2);
        if (parts.length < 2) continue;

        switch (parts[0]) {
          case "name":          name = parts[1]; break;
          case "credits":       credits = Integer.parseInt(parts[1]); break;
          case "deck":          deckId = parts[1]; break;
          case "cyberjack":     cyberjackId = parts[1]; break;
          case "ownedPrograms":
            if (!parts[1].isEmpty()) {
              for (String n : parts[1].split(",")) {
                if (!n.trim().isEmpty()) ownedProgramNames.add(n.trim());
              }
            }
            break;
          case "installedPrograms":
            if (!parts[1].isEmpty()) {
              for (String n : parts[1].split(",")) {
                if (!n.trim().isEmpty()) installedProgramNames.add(n.trim());
              }
            }
            break;
        }
      }

      Deck deck = decks.get(deckId);
      Cyberjack cyberjack = cyberjacks.get(cyberjackId);

      if (deck == null) {
        System.out.println("[ERROR] LOAD_FAILED: Unknown deck id '" + deckId + "'.");
        return null;
      }
      if (cyberjack == null) {
        System.out.println("[ERROR] LOAD_FAILED: Unknown cyberjack id '" + cyberjackId + "'.");
        return null;
      }

      // Now player exists - apply programs
      Player player = new Player(deck, cyberjack);
      player.name = name;
      player.credits = credits;

      for (String pName : ownedProgramNames) {
        Program p = ProgramFactory.findByName(allPrograms, pName);
        if (p != null) {
          player.ownedPrograms.add(p);
        } else {
          Debug.log("Unknown program in save: " + pName);
        }
      }

      for (String pName : installedProgramNames) {
        Program p = player.findOwnedProgram(pName);
        if (p != null) {
          p.applyProgramEffect(player);
          player.installedPrograms.add(p);
        } else {
          Debug.log("Installed program not in owned list: " + pName);
        }
      }

      System.out.println("[INFO] Save loaded for " + name + ".");
      return player;

    } catch (IOException e) {
      System.out.println("[ERROR] LOAD_FAILED: Could not read save file.");
      Debug.log("Load error: " + e.getMessage());
      return null;
    }
  }

  public static void deleteSave() {
    File save = new File(SAVE_FILE);
    if (save.exists()) {
      save.delete();
      System.out.println("[INFO] Save deleted.");
    }
  }
}