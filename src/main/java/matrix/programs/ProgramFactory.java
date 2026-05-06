package matrix.programs;

import java.util.ArrayList;

public class ProgramFactory {
  public static ArrayList<Program> loadPrograms(){
    ArrayList<Program> programs = new ArrayList<>();

    programs.add(new Toolbox());

    return programs;
  }

  public static Program findByName(ArrayList<Program> programs, String name){
  return programs.stream()
                  .filter(p -> p.getName().equalsIgnoreCase(name))
                  .findFirst()
                  .orElse(null);
  }
}
