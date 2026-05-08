package game;

public class UI {
  public void Loading(int loadingTime){
    for (int i=0; i < loadingTime; i++){
      System.out.print(". ");
      try {
        Thread.sleep(400);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

  public void Clear(){
    System.out.println("\033[H\033[2J");
    System.out.flush();
  }
}
