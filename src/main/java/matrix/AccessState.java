package matrix;

public enum AccessState {
  ADMIN_LEGAL,
  ADMIN_ILLEGAL,
  USER,
  OUTSIDER;

  public boolean supersedes(AccessState required) {
    switch (required) {
      case OUTSIDER:
        return true; // Everyone meets outsider requirement
      case USER:
        return this == USER
            || this == ADMIN_LEGAL
            || this == ADMIN_ILLEGAL;
      case ADMIN_ILLEGAL:
        return this == ADMIN_ILLEGAL
            || this == ADMIN_LEGAL;
      case ADMIN_LEGAL:
        return this == ADMIN_LEGAL;
      default:
        throw new IllegalArgumentException("Unknown access state: " + required);
    }
  }
}
