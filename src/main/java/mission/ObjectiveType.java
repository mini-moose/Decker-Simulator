package mission;

import java.util.Random;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

public enum ObjectiveType {
  GAIN_ACCESS,
  EXFIL_FILE,
  DELETE_FILE,
  TAMPER_FILE,
  DESTROY_IC,
  PLACE_DATABOMB,
  DISABLE_DEVICE,
  JACK_OUT_CLEAN;

  private static final Random RANDOM = new Random();

  private static final List<ObjectiveType> VALUES = Collections.unmodifiableList(
        Arrays.stream(values())
              .filter(type -> type != GAIN_ACCESS)
              .collect(Collectors.toList()));

  private static final int SIZE = VALUES.size();

  public static ObjectiveType returnRandomObjectiveType(){
    return VALUES.get(RANDOM.nextInt(SIZE));
  }
}