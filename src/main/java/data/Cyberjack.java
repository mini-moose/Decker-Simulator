package data;

public class Cyberjack {
  public String id;
  public String name;
  public int rating;
  public int dataProcessing;
  public int firewall;
  public int initBonus;
  public int cost;
  public String description;

  public Cyberjack(String id, String name, int rating, int dataProcessing, int firewall, int initBonus,
              int cost, String description){
    this.id = id;
    this.name = name;
    this.rating = rating;
    this.dataProcessing = dataProcessing;
    this.firewall = firewall;
    this.initBonus = initBonus;
    this.description = description;

  }
}
