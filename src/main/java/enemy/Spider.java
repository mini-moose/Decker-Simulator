package enemy;

import data.Deck;
import player.Player;

import java.util.ArrayList;

public class Spider extends Player {
  public String handle;

  public ArrayList<String> spiderTaunts = new ArrayList<>();

  public String Taunt(int sel){
    return "I'm coming for you, decker!";
  }

  public Spider(String handle, Deck deck){
    super(deck);
    this.handle = handle;
  }
}
