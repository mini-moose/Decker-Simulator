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
}
