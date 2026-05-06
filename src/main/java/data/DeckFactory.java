package data;

import java.util.HashMap;
import java.util.Map;

public class DeckFactory {
  public static Map<String, Deck> loadDecks() {
    Map<String, Deck> decks = new HashMap<>();

    decks.put("erika_m", new Deck(
      "erika_m",
      "Erika M CD-6",
      1,
      4,
      3,
      2,
      24750,
      "Basic Deck for a basic Decker. Comes loaded with all the processing power you need to break into a Quik4U Mart network."
    ));

    decks.put("spinrad", new Deck(
      "spinrad",
      "Spinrad Falcon",
      2,
      5,
      4,
      4,
      61500,
      "Solid Deck just north of beginner level. Used by Deck-kiddies and true-blue Deckers alike."
    ));

    decks.put("mct", new Deck(
      "mct",
      "MCT 360",
      3,
      6,
      5,
      6,
      95000,
      "If you can get past the boring-as-dirt name, the MCT 360 will get up to the big leagues."
    ));

    return decks;
  }
}
