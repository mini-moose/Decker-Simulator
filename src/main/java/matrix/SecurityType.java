package matrix;

import java.util.Random;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

public enum SecurityType {
  PUBLIC,
  SENSITIVE,
  ADMIN,
  SECURITY,
  INTERNAL;

  private static final Random RANDOM = new Random();

  private static final List<SecurityType> VALUES = Collections.unmodifiableList(
        Arrays.stream(values())
              .filter(type -> type != PUBLIC)
              .collect(Collectors.toList()));

  private static final int SIZE = VALUES.size();

  public static SecurityType returnRandomSecurityType() {
    return VALUES.get(RANDOM.nextInt(SIZE));
  }
}
