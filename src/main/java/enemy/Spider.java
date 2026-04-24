package enemy;

import data.Deck;
import player.Player;

public class Spider extends Player {
  public String handle;

  public Spider(String handle, Deck deck){
    super(deck);
    this.handle = handle;
  }
}
