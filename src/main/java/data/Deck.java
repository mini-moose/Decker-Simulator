package data;

public class Deck {
  public String id;
  public String name;
  public int rating;
  public int attack;
  public int sleaze;
  public int programSlots;
  public int cost;
  public String description;

  public Deck(String id, String name, int rating, int attack, int sleaze, int programSlots,
              int cost, String description){
    this.id = id;
    this.name = name;
    this.rating = rating;
    this.attack = attack;
    this.sleaze = sleaze;
    this.cost = cost;
    this.programSlots = programSlots;
    this.description = description;
  }
}
