package main;

import game.Game;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import matrix.Host;
import matrix.SecurityType;
import matrix.device.Device;
import matrix.device.DeviceFactory;
import matrix.device.DeviceType;
import matrix.files.HostFile;
import matrix.ic.ICType;
import mission.Mission;
import mission.MissionType;
import mission.Objective;
import mission.ObjectiveGenerator;


public class SessionBuilder {


  public Mission createTutorialMission(Game game, int rating, Host defaultHost){
    Random random = new Random();
    String targetName = "Horizon";


    Mission mission = new Mission(targetName, MissionType.DATA_EXFIL, rating, rating * 500);

    Host tutorialEntryHost = generateHost(mission.rating, SecurityType.PUBLIC, 1, null, defaultHost, targetName.toLowerCase(), random);
    generateHostFiles(tutorialEntryHost, targetName.toLowerCase(), SecurityType.PUBLIC);
    generateHostICs(tutorialEntryHost, mission.rating);
    game.addHost(tutorialEntryHost);

    Host tutorialTargetHost = generateHost(mission.rating, SecurityType.PUBLIC, 2, null, tutorialEntryHost, targetName.toLowerCase(), random);
    tutorialTargetHost.isHidden = true;
    generateHostFiles(tutorialTargetHost, targetName.toLowerCase(), SecurityType.PUBLIC);
    generateHostICs(tutorialTargetHost, mission.rating);
    game.addHost(tutorialTargetHost);

    ObjectiveGenerator objectiveGenerator = new ObjectiveGenerator(random);
    objectiveGenerator.generateObjectives(mission, tutorialTargetHost);

    return mission;
  }

  public ArrayList<Host> createSession(Game currentGame, Mission mission) {
    Random random = new Random();

    ObjectiveGenerator objectiveGenerator = new ObjectiveGenerator(random);

    ArrayList<Host> network = new ArrayList<>();

    String targetName = mission.targetCorp.split(" ")[0];

    int netComplexityRating = Math.max(1, random.nextInt(mission.rating + 1) / 2);
    Debug.log("Net Complexity: " + netComplexityRating + " | Net Rating: " + mission.rating);

    // Generate a Public Entry Host as the initial access point.
    Host entryHost = generateHost(mission.rating, SecurityType.PUBLIC, 1, null, currentGame.defaultHost, targetName.toLowerCase(), random);
    generateHostFiles(entryHost, targetName, SecurityType.PUBLIC);
    generateHostICs(entryHost, mission.rating);
    network.add(entryHost);
    currentGame.addHost(entryHost);

    // Generate secure hosts behind the Entry Host
    Host previousHost = entryHost;
    for (int i=0; i < netComplexityRating; i++){
      Host parent = (netComplexityRating > 3 && random.nextBoolean())
        ? network.get(random.nextInt(network.size()))
        : previousHost;
      
      SecurityType type = SecurityType.returnRandomSecurityType();

      Host newHost = generateHost(mission.rating, type, i, parent, null, targetName.toLowerCase(), random);
      generateHostFiles(newHost, targetName, type);
      generateHostICs(newHost, mission.rating);
      generateDevices(newHost, random);

      network.add(newHost);
      currentGame.addHost(newHost);
      previousHost = newHost;
    }

    // Generate Objectives for the Mission
    Host targetHost = network.get(random.nextInt(1, network.size()));
    
    objectiveGenerator.generateObjectives(mission, targetHost);

    for (Host host : network){
      Debug.log("Generated new Host | Host Name: " + host.name + " | IsHidden: " + host.isHidden);
    }
    Debug.log("Generated Mission Objectives:");
    for (Objective obj : mission.objectives){
      Debug.log("Objective: " + obj.type + " | Description: " + obj.description);
    }

    return network;
  }

  public int generateHostRating(int sessionRating, Random random) {
    int maxOffset = Math.max(1, sessionRating / 3);
    int offset = random.nextInt(maxOffset + 1);
    return Math.max(1, sessionRating - offset);
  }

  public Host generateHost(int sessionRating, SecurityType hostSecurityType, int depth, Host parentHost, Host defaultHost, String targetCorp, Random random){
    
    // Generate Network Control List for the host
    ArrayList<Host> hostNCL = new ArrayList<>();

    // If no parent host defined, default to defaultHost
    if (defaultHost != null) {
      hostNCL.add(defaultHost);
    } else {
      hostNCL.add(parentHost);
    }

    for (Host host : hostNCL){
      Debug.log(host.name);
    }

    int hostRating = generateHostRating(sessionRating, random);

    int hostType = (depth > 1) ? 2 : random.nextInt(2) + 1;

    // Weighted check to see if host is hidden
    boolean isHidden = false;
    double weight = 0.8;

    // Depth 1 hosts will never be hidden
    if (depth != 1){
      if (Math.random() < weight){
        isHidden = false;
      } else {
        isHidden = true;
      }
    }

    String hostName = generateHostName(targetCorp, depth, random);
    ArrayList<String> hostBanner = generateBanner(targetCorp, hostSecurityType);

    Host host = new Host(hostRating, hostType, hostSecurityType, hostName, hostBanner, isHidden, hostNCL);

    Debug.log("Generated Host: " + hostName + 
              "\nHost Type:"       + hostType +
              "\nHost Security lvl: " + hostSecurityType +
              "\nRating: "         + hostRating +
              "\nHidden: "       + isHidden
    );


    return host;
  }

  public void generateHostICs(Host host, int hostRating) {
    // Every host has at least a Patrol IC available to deploy.
    host.addAllowedICType(ICType.PATROL);

    // Lower rated hosts have less-than-lethal ICs.
    if (hostRating >= 2) host.addAllowedICType(ICType.JAMMER);
    if (hostRating >= 3) host.addAllowedICType(ICType.ACID);
    if (hostRating >= 4) host.addAllowedICType(ICType.KILLER);

    // Higher rated hosts have lethal protections.
    if (hostRating >= 6) host.addAllowedICType(ICType.SPARKY); // Sparky damages the player's health directly.
    if (hostRating >= 8) host.addAllowedICType(ICType.BLACK_IC); // Black IC does the same, but also Link-Locks the player meaning they can't leave the Matrix

    // Aggressive hosts have extra offensive capabilities
    if (host.type == 1 && hostRating >= 5) {
      host.addAllowedICType(ICType.BLASTER);
      host.addAllowedICType(ICType.CRASH);
      host.addAllowedICType(ICType.BINDER);
    }

    if (host.type == 2 && hostRating >= 5) {
      host.addAllowedICType(ICType.TRACK);
      host.addAllowedICType(ICType.TARPIT);
      host.addAllowedICType(ICType.SCRAMBLE);   
    }

    for (ICType type : host.icTypesAllowed){
      Debug.log(type.toString());
    }
  }

  public void generateHostFiles(Host host, String targetCorp, SecurityType hostSecurityType) {
    int targetFiles = Math.max(1, host.rating);
    int targetDirs = Math.max(1, host.rating / 2); // Math.max(1,...) prevents 0
    int maxAttempts = (targetFiles + targetDirs) * 3;
    int attempts = 0;

    // Generate files
    while (host.filesOnHost.size() < targetFiles && attempts < maxAttempts) {
      HostFile file = new HostFile(host, targetCorp, hostSecurityType, false);
      host.addFile(file);
      attempts++;
    }

    // Generate directories separately with their own attempt counter
    int dirAttempts = 0;
    long currentDirs = host.filesOnHost.stream().filter(f -> f.isDirectory).count();
    while (currentDirs < targetDirs && dirAttempts < maxAttempts) {
      HostFile directory = new HostFile(host, targetCorp, hostSecurityType, true);
      dirAttempts++;
      currentDirs = host.filesOnHost.stream().filter(f -> f.isDirectory).count();
    }

    Debug.log("Generated " + host.filesOnHost.size() + " items for " + host.name 
        + " (target: " + targetFiles + " files, " + targetDirs + " dirs)");

    for (HostFile file : host.filesOnHost) {
      if (file.isDirectory) {
        Debug.log("DIR:  " + file.name + " (" + file.filesInDirectory.size() + " files)");
      } else {
        Debug.log("FILE: " + file.name);
      }
    }
  }

  public void generateDevices(Host host, Random random) {
    int targetDevices = Math.max(1, host.rating);
    int maxAttempts = (targetDevices) * 2;
    int attempts = 0;

    ArrayList<DeviceType> generalDevices = new ArrayList<>(Arrays.asList(DeviceType.ALARM, DeviceType.DOOR));
    ArrayList<DeviceType> securityDevices = new ArrayList<>(Arrays.asList(DeviceType.CAMERA, DeviceType.DRONE));

    // Generate devices
    DeviceFactory deviceFactory = new DeviceFactory();

    while (host.devicesOnHost.size() < targetDevices && attempts < maxAttempts) {
      if (!host.hostType.equals(SecurityType.SECURITY)){
        Device device = deviceFactory.create(host, generalDevices.get(random.nextInt(generalDevices.size())));
        host.addDevice(device);
        attempts++;
      } else {
        Device device = deviceFactory.create(host, securityDevices.get(random.nextInt(securityDevices.size())));
        host.addDevice(device);
        attempts++;
      }
    }
    for (Device device : host.devicesOnHost) {
        Debug.log("Device: " + device.name);
    }
  }

  public String generateHostName(String targetCorp, int depth, Random random){
    ArrayList<String> outerHostName = new ArrayList<>(Arrays.asList(
      targetCorp + "-dmz",
      targetCorp + "-public",
      targetCorp + "-guest",
      targetCorp + "-net",
      targetCorp + "-public-matrix",
      targetCorp + "-gateway"
    ));

    ArrayList<String> innerHostName = new ArrayList<>(Arrays.asList(
      targetCorp + "-file-server",
      targetCorp + "-security",
      targetCorp + "-database",
      targetCorp + "-matrix-server",
      targetCorp + "-private",
      targetCorp + "-workspace",
      targetCorp + "-private-net"
    ));

    // If depth is 1, generate an outer host name, otherwise generate an inner host name
    if (depth == 1){
      return outerHostName.get(random.nextInt(outerHostName.size()));
    } else {
      return innerHostName.get(random.nextInt(outerHostName.size()));
    }
  }

  public ArrayList<String> generateBanner(String targetName, SecurityType securityType) {
    ArrayList<String> publicBanners = new ArrayList<>(Arrays.asList(
      "#######  Welcome to the " + targetName + " public host! Please follow our matrix rules.  #########\n",
      "### " + targetName + " is Hiring! Take our 147-page aptitude test to see if you're a good fit! ####\n",
      "###########  Please report any suspicious activity to the nearest security icon.  #############\n",
      "#########  REMEMBER, DECKER. GRID OVERWATCH IS __ALWAYS__ WATCHING. GET OUT ###########\n"
    ));

    ArrayList<String> adminBanners = new ArrayList<>(Arrays.asList(
      "#######  Welcome, user. Your access to this system is considered the start of your workday.  #########\n",
      "#######  " + targetName + " is an equal opportunity employer. You are all equaly fireable.  ########\n",
      "#######  Remember to report time-theft to your Supervisor. Reward is 1/2 hour pay per finding.  ########\n",
      "#######  If you are experiencing distress in the workplace, submit form 13a-c to your shredder.  #########\n"
    ));

    ArrayList<String> securityBanners = new ArrayList<>(Arrays.asList(
      "#######  You are accessing a " + targetName + " secure host. Access is consent to monitoring.  #########\n",
      "#######  Accessing secure hosts without permission bears the penalty of life in prison.  ########\n",
      "#######  Unauthorized access to this host will be subject to IC response. Deadly force authorized.  ########\n",
      "#######  " + targetName + " is not liable for any real-world damage caused by IC actions.  #########\n"
    ));

    ArrayList<String> sensitiveBanners = new ArrayList<>(Arrays.asList(
      "#######  Welcome, investor, stakeholder, or management person. Please access only your allowed documents.  #########\n",
      "#######  " + targetName + " files are proprietary. Unauthorized disclosure is punishable by law.  ########\n",
      "#######  Please refrain from renaming project files. IC response will trigger if files are mishandled.  ########\n",
      "#######  Remember - Deckers could be anywhere. Report suspicious actions to Grid Overwatch immediately!  #########\n"
    ));

    ArrayList<String> internalBanners = new ArrayList<>(Arrays.asList(
      "#######  Members of the " + targetName + " development team are subject to deck inspection at the end of the workday.  #########\n",
      "#######  " + targetName + " matrix employees are eligible for retainment pay. See your supervisor for details.  ########\n",
      "#######  The MatrixOps team would like to remind you: DON'T TOUCH THE HOST CONFIGURATIONS!!  ########\n",
      "#######  Developers that do not submit their code at the end of the workday will be tazed by Security.  #########\n"
    ));

    switch(securityType){
      case PUBLIC:
        return publicBanners;
      case SECURITY:
        return securityBanners;
      case ADMIN:
        return adminBanners;
      case SENSITIVE:
        return sensitiveBanners;
      case INTERNAL:
        return internalBanners;
      default:
        return publicBanners;
    }
  }
}
