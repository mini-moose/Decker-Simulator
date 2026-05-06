package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import main.Debug;
import main.DiceRoller;
import matrix.AccessState;
import matrix.Host;
import matrix.MatrixEntity;
import matrix.actions.EdgeType;
import matrix.device.Device;
import matrix.files.HostFile;
import matrix.ic.IC;
import matrix.ic.ICEffect;
import matrix.ic.ICType;
import mission.Mission;
import mission.MissionType;
import mission.Objective;
import mission.ObjectiveType;
import player.Player;

// Manages the game logic
public class Game {
  
  DiceRoller roller = new DiceRoller();

  public GameState gameState = GameState.ACTIVE;

  public Player player;
  public int overWatchScore = 0;
  public int gameClock = 0;

  public Host defaultHost = null;
  public Host parentHost = null;
  public Host currentHost = null;

  public Mission currentMission = null;

  public ArrayList<Host> hosts = new ArrayList<>();

  public HashMap<Host, ArrayList<IC>> hostIC = new HashMap<>();

  private static final int OVERWATCH_LIMIT = 40;

  public Game(Player player) {
    this.player = player;
  }

  public void addHost(Host host) {
    hosts.add(host);
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

  public ArrayList<Host> getAllHosts(){
    return hosts;
  }

  // Update the Overwatch Score with the defender's hits
  public void updateOverwatch(int defenderHits){
    if (defenderHits > 0) {
      overWatchScore += defenderHits;
    }
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

  public ArrayList<HostFile> getAllFiles(){
    ArrayList<HostFile> all = new ArrayList<>();
    
    for (HostFile file : currentHost.filesOnHost){
      all.add(file);
    }

    return all;
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

  // Handles Contested Roll Logic
  public ArrayList<Integer> ContestedRoll(MatrixEntity attacker, MatrixEntity defender, int attackerPool, int defenderPool) {
    ArrayList<Integer> hitList = new ArrayList<>();

    // Rolling Attacker's dice
    ArrayList<Integer> attackerResults = roller.RollDice(attackerPool);
    HashMap<String, Integer> attackerTotals = roller.GetHits(attackerResults);

    hitList.add(attackerTotals.get("HIT"));

    // Rolling Defender's dice
    
    ArrayList<Integer> defenderResults = roller.RollDice(defenderPool);
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
      System.out.println("[INFO] IC_" + ic.icType + ": IC Action avoided");
      return;
    }
    
    System.out.println("[WARNING] IC_" + ic.icType + ": IC Action succeeded with " + netHits + " Net Hits");

    // If IC has an additional effect tied to hits, apply it here
    if (ic instanceof ICEffect) {
      ((ICEffect) ic).applyEffect(player, netHits);
    }
  }

  // Some Actions and rolls take additional modifiers to the dice pool.
  // This method handles those rolls.
  public ArrayList<Integer> ModifiedContestedRoll(MatrixEntity attacker, MatrixEntity defender, ArrayList<String> attackerStats, ArrayList <String> defenderStats){
    ArrayList<Integer> hitList = new ArrayList<>();

    // Rolling Attacker's dice
    int attackerDicePool = roller.GrabDice(attacker, attackerStats);
    ArrayList<Integer> attackerResults = roller.RollDice(attackerDicePool);
    HashMap<String, Integer> attackerTotals = roller.GetHits(attackerResults);

    hitList.add(attackerTotals.get("HIT"));

    // Rolling Defender's dice    
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

  // Establish a turn cycle and roll initiative ## UNUSED
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

  public void getObjectives(){
    for (Objective obj : currentMission.objectives){
      System.out.println("Objective: " + obj);
    }
  }

  public void checkObjectiveComplete(ObjectiveType type, MatrixEntity objectiveEntity){
    for (Objective obj : currentMission.objectives){
      if (!obj.isComplete && obj.type == type) {
        if (objectiveEntity == null || objectiveMatchesEntity(obj, objectiveEntity)){
          obj.complete();
          break;
        }
      }
    }
    if (currentMission.isComplete()){
      triggerMissionComplete();
    }
  }

  private boolean objectiveMatchesEntity(Objective obj, MatrixEntity entity){
    return obj.description.contains(entity.name);
  }

  private void triggerMissionComplete() {
    gameState = GameState.MISSION_COMPLETE;
    player.credits += currentMission.reward;
    System.out.println("Mission Complete!");
  }

  public void resetGame(Host staticHost){
    gameClock = 0;
    overWatchScore = 0;

    hosts.clear();
    addHost(staticHost);
    currentHost = staticHost;
    defaultHost = staticHost;
    currentMission = null;

    gameState = GameState.ACTIVE;
  }

  public void printMissionSummary(Game game, Player player) {
      System.out.println("\n======== MISSION DEBRIEF ========");
      switch (game.gameState) {
        case MISSION_COMPLETE:
          System.out.println(">> Status:  SUCCESS");
          System.out.println(">> Payout:  " + game.currentMission.reward + "¥ transferred.");
          System.out.println(">> Credits: " + player.credits + "¥ total.");
          break;
        case CONVERGENCE:
          System.out.println(">> Status:  DUMPED");
          System.out.println(">> OW agents are looking for you. Time to get out of town...");
          break;
        case PLAYER_DEAD:
          System.out.println(">> Status:  FLATLINED");
          System.out.println(">> Someone's going to find you in that alley.");
          break;
        case JACKED_OUT:
          System.out.println(">> Status:  ABORTED");
          System.out.println(">> No payout. Better luck next time.");
          break;
        default:
          break;
      }
      System.out.println("=================================\n");
  }

  // #######################
  //   Win/Lose Condition
  // #######################

  public boolean isActive() {
    return gameState == GameState.ACTIVE;
  }

  // Game loss occurs when one of the following happens:
  //    - Player's condition monitor (the real one) reaches 0.
  //    - Overwatch Score reaches 40. Character's location is discovered and they're on the run.
  public void checkLoss(){
    if (player.conditionMonitor <= 0){
      triggerDeath();
    } else if (overWatchScore >= OVERWATCH_LIMIT){
      triggerConvergence();
    }
  }

  public void triggerDeath(){
    gameState = gameState.PLAYER_DEAD;
  }

  public void triggerConvergence(){
    gameState = gameState.CONVERGENCE;
  }


  // #######################
  //   Mission Generation
  // #######################
  public void generateMission(Game game, Player player, String target, int rating){

  }

  public String getCorps(int rating, Random random) {
    // ArrayLists of Mission Targets
    ArrayList<String> aaaNames = new ArrayList<>(Arrays.asList(
      "Ares Macrotech",
      "Aztechnology",
      "Cross Applied Technologies",
      "Fuchi Industrial Electronics",
      "Mitsuhama Computer Technologies",
      "Novatech",
      "Renraku Computer Systems",
      "Evo",
      "Horizon",
      "NeoNET",
      "Saeder-Krupp Heavy Industries",
      "Shiawase Corporation",
      "Wuxing",
      "Yamatetsu"
    ));

    ArrayList<String> aaNames = new ArrayList<>(Arrays.asList(
      "Aesa",
      "AG-Chemi Europa",
      "Atlantean Foundation",
      "BrackHaven Investments",
      "Chrysler-Nissan",
      "DocWagon",
      "Erika",
      "Espirit Industries",
      "ESUS",
      "FBV",
      "Federated-Boeing",
      "FoMoCo",
      "Frankfurter Bankverein",
      "Gunderson Corporation",
      "HKB",
      "Kolkota Integrated Talent and Technologies",
      "Lockmartston",
      "Lone-Star Security Services",
      "Lusiada",
      "Marks"
    ));

    ArrayList<String> aNames = new ArrayList<>(Arrays.asList(
      "Eurotronics",
      "EMC",
      "High-Plains Coding",
      "Hondarich",
      "IFMU",
      "Index-Axa",
      "Microdeck"
    ));

    if (rating <= 4){
      return aNames.get(random.nextInt(aNames.size()));
    } else if (rating <= 7){
      return aaNames.get(random.nextInt(aaNames.size()));
    } else if (rating <= 12){
      return aaaNames.get(random.nextInt(aaaNames.size()));
    } else {
      return aaaNames.get(random.nextInt(aaaNames.size()));
    }
  }

  public int getReward(int rating){
    return 2500 * rating;
  }

  public int getMissionRating(int rating, Random random){
    double scale = Math.pow(rating / 5.0, 1.5) * 8 + 1;
    int baseRating = (int) Math.round(scale);

    int maxVariance = Math.max(1, 4 - (rating - 1));
    int variance = random.nextInt(maxVariance + 1);

    int variableRating = Math.max(1, Math.min(12, baseRating + variance));

    return variableRating;
  }

  public ArrayList<Mission> missionSelect(Game game, Random random, int rating){
    ArrayList<Mission> missions = new ArrayList<>();

    MissionType[] missionType = MissionType.values();

    for (int i=0; i < 3; i++){
      String corp = getCorps(rating, random);
      int missionRating = getMissionRating(rating, random);
      int reward = getReward(missionRating);
      MissionType type = missionType[random.nextInt(missionType.length)];
      missions.add(new Mission(corp, type, missionRating, reward));
    }
    return missions;
  }

  // ################################################################################
  // #################        IC and SPIDER Logic and AI        #####################
  // ################################################################################

  int consecutiveDetections = 0;

  public void respondToIllegalAction(Host host, Player player, Random random){
    if (!host.hasDeployedICOfType(ICType.PATROL)){
      host.deployIC(ICType.PATROL);
      return;
    }

    IC patrolIC = getDeployedICOfType(host, ICType.PATROL);
    if (patrolIC == null) return;

    if (player.isDetected) {
      if (player.edgeType != null && player.edgeType.equals(EdgeType.SCRAMBLE)){
        System.out.println("[INFO] SCRAMBLE: Detection avoided.");
        player.edgeType = null;
      }
      handleDetection(host, player, random);
    } else {
      handlePatrolAction(patrolIC);
    }
  }

  public void handlePatrolAction(IC patrol){
    System.out.println("[WARNING] Patrol IC scanning for intrusion.");
    // If the player isn't hidden, they get spotted right away. Otherwise the Patrol IC has to look for them.
    if (!player.isHidden){
      player.isDetected = true;
      System.out.println("[WARNING] Host has flagged you as a Persona of Interest.");
    } else {
      ResolveICAttack(patrol, player);
    }
  }

  private void handleDetection(Host host, Player player, Random random){
    consecutiveDetections++;

    if (player.isHidden) {
      int alertChance = Math.min(90, consecutiveDetections * 20);
      int roll = random.nextInt(100);

      if (roll < alertChance) {
        triggerAlert(host, player);
      } else {
        System.out.println("[SYSTEM] IC Patrol accounting for system error, rerouting.");

      }
    } else {
      triggerAlert(host, player);
    }
  }

  private void triggerAlert(Host host, Player player){
    host.isAlert = true;
    consecutiveDetections = 0;
    player.isDetected = true;
    System.out.println("[SYSTEM] ALERT: HOST HAS DETECTED INTRUSION, DEPLOYING IC");
    deployFirstAvailableCombatIC(host);
  }

  private void deployFirstAvailableCombatIC(Host host) {
    ArrayList<ICType> combatTypes = new ArrayList<>(Arrays.asList(
      ICType.KILLER, ICType.BLACK_IC, ICType.BLASTER,
      ICType.SPARKY, ICType.ACID, ICType.JAMMER
    ));

    for (ICType type : combatTypes){
      Debug.log(type.getLabel());
      if (host.icTypesAllowed.contains(type) && !host.hasDeployedICOfType(type)){
        host.deployIC(type);
        return;
      }
    }
  }

  public IC getDeployedICOfType(Host host, ICType type){
    for (IC ic : host.deployedIC){
      if (ic.icType.equals(type)) return ic;
    }
    return null;
  }

  public Class<?> resolveEntityType(String type){
    switch (type) {
      case "host":    return Host.class;
      case "file":    return HostFile.class;
      case "device":  return Device.class;
      default:        return null;
    }
  }
}