package data;

import data.Deck;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.Reader;
import java.io.FileReader;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class DataLoader {
  private static final String DATA_PATH = "src/data/";
  private static final Gson gson = new Gson();

  public static Map<String, Deck> loadDecks() {
    try (Reader reader = new FileReader(DATA_PATH + "decks.json")) {
      List<Deck> deckList = gson.fromJson(reader, new TypeToken<List<Deck>>(){}.getType());

      // Convert list to a map keyed by id for easy lookup
      Map<String, Deck> decks = new HashMap<>();
      for (Deck deck : deckList) {
          decks.put(deck.id, deck);
      }
      return decks;

    } catch (Exception e) {
        throw new RuntimeException("Failed to load decks: " + e.getMessage());
    }
  }
}
