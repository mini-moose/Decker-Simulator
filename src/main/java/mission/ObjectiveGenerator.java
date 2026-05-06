package mission;

import java.util.ArrayList;
import java.util.Random;

import main.Debug;
import matrix.Host;
import matrix.MatrixEntity;
import matrix.device.Device;
import matrix.files.HostFile;

public class ObjectiveGenerator {
  private Random random;

  public ObjectiveGenerator(Random random){
    this.random = random;
  }

  public void generateObjectives(Mission mission, Host targetHost){
    mission.targetHost = targetHost;

    ArrayList<MatrixEntity> targets = new ArrayList<>();

    switch (mission.type) {
      case DATA_EXFIL:
        for (int i=0; i < (mission.rating / 2) + 1; i++){
          targets.add(targetHost.filesOnHost.get(random.nextInt(targetHost.filesOnHost.size())));
        }
        generateDataExfilObjectives(mission, targetHost, targets);
        break;
      case MATRIX_ASSIST:
        for (int i=0; i < (mission.rating / 2) + 1; i++){
          targets.add(targetHost.devicesOnHost.get(random.nextInt(targetHost.devicesOnHost.size())));
        }
        generateMatrixAssistObjectives(mission, targetHost, targets);
        break;
      case SABOTAGE:
        for (int i=0; i < (mission.rating / 2); i++){
          targets.add(targetHost.filesOnHost.get(random.nextInt(targetHost.filesOnHost.size())));
        }
        for (int i=0; i < (mission.rating / 2); i++){
          targets.add(targetHost.devicesOnHost.get(random.nextInt(targetHost.devicesOnHost.size())));
        }
        generateSabotageObjectives(mission, targetHost, targets);
        break;
      case DATA_TAMPER:
        for (int i=0; i < (mission.rating / 2) + 2; i++){
          targets.add(targetHost.filesOnHost.get(random.nextInt(targetHost.filesOnHost.size())));
        }
        generateDataTamperObjectives(mission, targetHost, targets);
        break;
    }
  }

  private void generateDataExfilObjectives(Mission mission, Host targetHost, ArrayList<MatrixEntity> targets){
    mission.addObjective(new Objective(
      ObjectiveType.GAIN_ACCESS,
      "Gain access to Host " + targetHost.name
    ));
    for (MatrixEntity target : targets){
      mission.addObjective(new Objective(
        ObjectiveType.EXFIL_FILE,
        "Exfiltrate " + target.name + " from " + targetHost.name
    ));
    }
  }

  // TODO: ADD LOGIC
  private void generateMatrixAssistObjectives(Mission mission, Host targetHost, ArrayList<MatrixEntity> targets){
    mission.addObjective(new Objective(
      ObjectiveType.GAIN_ACCESS,
      "Gain access to Host " + targetHost.name
    ));
    for (MatrixEntity target : targets){
      mission.addObjective(new Objective(
        ObjectiveType.DISABLE_DEVICE,
        "Disable " + target.name + " on " + targetHost.name
    ));
    }
  }

  // TODO: ADD LOGIC
  private void generateSabotageObjectives(Mission mission, Host targetHost, ArrayList<MatrixEntity> targets){  
    mission.addObjective(new Objective(
      ObjectiveType.GAIN_ACCESS,
      "Gain access to Host " + targetHost.name
    ));
    for (MatrixEntity target : targets){
      if (target instanceof Device){
        mission.addObjective(new Objective(
          ObjectiveType.DISABLE_DEVICE,
          "Destroy or disable " + target.name + " on " + targetHost.name
        ));
      } else if (target instanceof HostFile){
        mission.addObjective(new Objective(
          ObjectiveType.DELETE_FILE,
          "Remove " + target.name + " from " + targetHost.name
        ));
      } else {
        Debug.log("Unknown target class type for sabatoge objective.");
      }
    }
  }

  // TODO: ADD LOGIC
  private void generateDataTamperObjectives(Mission mission, Host targetHost, ArrayList<MatrixEntity> targets){
    mission.addObjective(new Objective(
      ObjectiveType.GAIN_ACCESS,
      "Gain access to Host " + targetHost.name
    ));
    for (MatrixEntity target : targets){
      mission.addObjective(new Objective(
        ObjectiveType.TAMPER_FILE,
        "Edit and change " + target.name + " on " + targetHost.name
    ));
    }
  }
}
