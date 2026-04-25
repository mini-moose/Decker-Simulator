package main;

import main.DiceRoller;
import main.MissionState;

import player.Player;

import matrix.Host;
import matrix.IC;
import matrix.ic.ICEffect;
import matrix.MatrixEntity;
import matrix.AccessState;

import java.util.Random;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

// Manages the game logic
public class Game {
  
  DiceRoller roller = new DiceRoller();

  public MissionState missionState = MissionState.GAINING_ACCESS;

  public Player player;
  public int overWatchScore = 0;
  public int gameClock = 0;

  public Host defaultHost = null;
  public Host parentHost = null;
  public Host currentHost = null;

  public ArrayList<Host> hosts = new ArrayList<Host>();

  public HashMap<Host, ArrayList<IC>> hostIC = new HashMap<>();

  private static final int OVERWATCH_LIMIT = 40;

  public Game(Player player) {
    this.player = player;
  }

  public void addHost(Host host) {
    hosts.add(host);
    hostIC.put(host, new ArrayList<>());
  }

  public void addIC(Host host, IC ic){
    if (!hostIC.containsKey(host)){
      throw new IllegalArgumentException("Host not found in session.");
    }
    hostIC.get(host).add(ic);
  }

  public ArrayList<IC> getHostIC(Host host){
    return hostIC.getOrDefault(host, new ArrayList<>());
  }

  public ArrayList<IC> getAllIC(){
    ArrayList<IC> all = new ArrayList<>();
    for (ArrayList<IC> ics : hostIC.values()){
      all.addAll(ics);
    }
    return all;
  }

  public Host findHost(String identifier) {
    for (Host host : hosts) {
      if (host.name.equalsIgnoreCase(identifier) ||
          host.uniProto.equals(identifier)) {
            return host;
          }
    }
    return null;
  }

  // Update the Overwatch Score with the defender's hits
  public void updateOverwatch(int defenderHits){
    if (defenderHits > 0) {
      overWatchScore += defenderHits;
      if (overWatchScore >= OVERWATCH_LIMIT) {
        triggerGameOver();
      }
    }
  }

  private void triggerGameOver(){
    //Thread.sleep(5000);
    System.out.println("=== FOUND YOU, DECKER ===");
    //Thread.sleep(2000);
    System.out.println("[ERROR] FAILURE_CRITICAL: OVERWATCH HAS YOUR LOCATION");
    //Thread.sleep(2000);
    System.out.println("[ERRO./..asdR] [SYSTEM_TAKEOVER_COMPLETE] SAY GOODBYE TO YOUR DECK");
    //Thread.sleep(1000);
    System.out.println("===================================================================");
    missionState = MissionState.JACKED_OUT;
  }

  // Remove these, we aren't using Mission State to track mission progress anymore
  public void setMissionState(MissionState state){
    this.missionState = state;
    System.out.println(">> INFO [MISSION STATE]: " + state);
  }

  public MissionState getMissionState(){
    return this.missionState;
  }

  // Get an ArrayList of MatrixEntities on or connected to the current Host with isHidden set to true
  public ArrayList<MatrixEntity> findHiddenEntities(Class<?> entityType) {
    ArrayList<MatrixEntity> hidden = new ArrayList<>();
    
    if (currentHost == null) return hidden;

    for (MatrixEntity entity : currentHost.entities) {
      if (entity.isHidden && entityType.isInstance(entity)) {
        hidden.add(entity);
      }
    }

    if (entityType == Host.class){
      for (Host host : hosts){
        if (host.isHidden && host.ncl.contains(currentHost)){
          hidden.add(host);
        }
      }
    }
    return hidden;
  }

  // Return all Matrix Entities of Type Spider, Player, and IC
  // Needed to establish turn order
  public ArrayList<MatrixEntity> getActiveEntities(){
    ArrayList<MatrixEntity> active = new ArrayList<>();

    if (currentHost == null) return active;

    // Player is always added
    active.add(player);

    // Add the Spider if they're in the Host
    if (currentHost.hasSpider()){
      active.add(currentHost.spider);
    }

    // Add all ICs in the current Host
    ArrayList<IC> ics = getHostIC(currentHost);
    if (ics != null) active.addAll(ics);

    // Future support for Agents deployed by the Spider
    //ArrayList<Agent> agents = getHostAgents(currentHost);
    //if (agents != null) active.addAll(agents);

    return active;
  }


  // Get the Acccess Control List for a given Matrix Entity 
  public AccessState getAccessState(MatrixEntity attacker, MatrixEntity defender){
    if (defender.accessControl == null || !defender.accessControl.containsKey(attacker)){
      return AccessState.OUTSIDER;
    }
    return defender.accessControl.get(attacker);
  }


  // Check if the attacker entity has the required access to perform an action on the defender entity
  public boolean hasRequiredAccess(MatrixEntity attacker, MatrixEntity defender, AccessState required){
    AccessState current = getAccessState(attacker, defender);
    return current.supersedes(required);
  }

  // ################################################################################
  // ##################        Dice and Rolling Methods        ######################
  // ################################################################################


  // Handles checking for if a attacker or defender have an Edge against their opponent
  // If the Attacker's Attack Rating is 4 or higher than the Defender's Defense Rating, they get an Edge, and vice-versa
  // Since I'm not keen on implementing dice reroll logic or Edge accumulation and all the tracking that comes with that
  // to the system (yet), I'm keeping this as Edge = +1 bonus dice to roll on the check
  // Returns int, 0 or 1. 1 = Attacker has Edge; 2 = Defender has Edge. Neither returns 0.
  public int CalculateEdge(MatrixEntity attacker, MatrixEntity defender){
    int hasEdge = 0;

    if (attacker.attackRating > defender.defenseRating + 4){
      hasEdge = 1;
      return hasEdge;
    } else if (attacker.attackRating + 4 < defender.defenseRating){
      hasEdge = 2;
      return hasEdge;
    } else {
      return hasEdge;
    }
  }

  // Handles Contested Roll Logic
  // attackerDice and defenderDice can be assigned before grabbing dice to provide static bonuses from
  // situational or Action-specific sources
  public ArrayList<Integer> ContestedRoll(MatrixEntity attacker, MatrixEntity defender, ArrayList<String> attackerStats, ArrayList <String> defenderStats, int attackerBonus, int defenderBonus) {
    ArrayList<Integer> hitList = new ArrayList<Integer>();

    // Rolling Attacker's dice
    int attackerDice = attackerBonus; // Immediately set to the bonus value

    int attackerDicePool = roller.GrabDice(attacker, attackerStats);
    ArrayList<Integer> attackerResults = roller.RollDice(attackerDicePool);
    HashMap<String, Integer> attackerTotals = roller.GetHits(attackerResults);

    hitList.add(attackerTotals.get("HIT"));

    // Rolling Defender's dice
    int defenderDice = defenderBonus;
    
    int defenderDicePool = roller.GrabDice(defender, defenderStats);
    ArrayList<Integer> defenderResults = roller.RollDice(defenderDicePool);
    HashMap<String, Integer> defenderTotals = roller.GetHits(defenderResults);

    hitList.add(defenderTotals.get("HIT"));

    // Calculating net hits: Attacker's Hits - Defender's Hits
    Integer netHits = attackerTotals.get("HIT") - defenderTotals.get("HIT");

    hitList.add(netHits);

    return hitList;
  }

  // IC Attacks trigger certain effects on hit depending on the type of IC
  // This means it needs it's own Roll method rather than piggybacking off of the generic Contested Roll
  public void ResolveICAttack(IC ic, Player player){
    // IC attacks with its attack pool
    int icDice = roller.GrabDice(ic, new ArrayList<>(Arrays.asList("attack")));
    HashMap<String, Integer> icRoll = roller.GetHits(roller.RollDice(icDice));

    // Player defends with stats specified in the IC block
    int playerDice = roller.GrabDice(player, ic.playerDefenseStats);
    HashMap<String, Integer> playerRoll = roller.GetHits(roller.RollDice(playerDice));

    int netHits = icRoll.get("HIT") - playerRoll.get("HIT");

    // Check to see if IC hits, if not then no further effects
    if (netHits <= 0) {
      System.out.println("[INFO] IC_" + ic.icType + ": IC Attack avoided");
      return;
    }
    
    System.out.println("[WARNING] IC_" + ic.icType + ": IC Attack succeeded with " + netHits + " Net Hits");

    // If IC has an additional effect tied to hits, apply it here
    if (ic instanceof ICEffect) {
      ((ICEffect) ic).applyEffect(player, netHits);
    }
  }

  // Some Actions and rolls take additional modifiers to the dice pool.
  // This method handles those rolls.
  public ArrayList<Integer> ModifiedContestedRoll(MatrixEntity attacker, MatrixEntity defender, ArrayList<String> attackerStats, ArrayList <String> defenderStats, int attackerBonus, int defenderBonus){
    ArrayList<Integer> hitList = new ArrayList<Integer>();

    // Rolling Attacker's dice
    int attackerDice = attackerBonus;

    int attackerDicePool = roller.GrabDice(attacker, attackerStats);
    ArrayList<Integer> attackerResults = roller.RollDice(attackerDicePool);
    HashMap<String, Integer> attackerTotals = roller.GetHits(attackerResults);

    hitList.add(attackerTotals.get("HIT"));

    // Rolling Defender's dice
    int defenderDice = defenderBonus;
    
    int defenderDicePool = roller.GrabDice(defender, defenderStats);
    ArrayList<Integer> defenderResults = roller.RollDice(defenderDicePool);
    HashMap<String, Integer> defenderTotals = roller.GetHits(defenderResults);

    hitList.add(defenderTotals.get("HIT"));

    // Calculating net hits: Attacker's Hits - Defender's Hits
    Integer netHits = attackerTotals.get("HIT") - defenderTotals.get("HIT");

    hitList.add(netHits);

    return hitList;
  }

  // ################################################################################
  // #################        Game State and Flow Methods       #####################
  // ################################################################################
  
  // Start Game Clock
  // Each Action = 6 seconds (technically 3 seconds in the sourcebook but that's really restrictive)

  // Establish a turn cycle and roll initiative
  public ArrayList<MatrixEntity> getTurnOrder(){
    ArrayList<MatrixEntity> activeEntities = getActiveEntities();
    Random random = new Random();
    
    for (MatrixEntity entity : activeEntities){
      if (entity.initiative == 0){
        entity.rollInitiative(random);
        System.out.println(entity.name + " initiative: ");
      } else {
        System.out.println(entity.name + " initiative: ");
      }

    }
    activeEntities.sort((a, b) -> b.initiative - a.initiative);

    return activeEntities;
  }


}