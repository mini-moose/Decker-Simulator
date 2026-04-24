package matrix;

import enemy.Spider;

import java.util.ArrayList;

public class Host extends MatrixEntity {

  public int type;
  public String loginMessage;

  public ArrayList<Host> acl = new ArrayList<>();
  public ArrayList<MatrixEntity> entities = new ArrayList<>();

  public void addEntity(MatrixEntity entity) {
    entities.add(entity);
  }

  public ArrayList<MatrixEntity> getEntitiesByType(Class<?> type){
    ArrayList<MatrixEntity> result = new ArrayList<>();
    for (MatrixEntity e : entities){
      if (type.isInstance(e)) result.add(e);
    }
    return result;
  }

  public void addToACL(Host host){
    acl.add(host);
  }

  // Supports assigning a Spider (enemy corpo Decker) to the Host
  // The Host will gain the Spider's mental Attributes on tests
  public Spider spider = null;

  public boolean hasSpider(){
    return spider != null;
  }

  public void assignSpider(Spider spider) {
    this.spider = spider;
  }

  public Host(int hostRating, int type, String hostName, String loginMessage, boolean isHidden, ArrayList<Host> acl) {
    super(hostRating);
    this.type = type;
    this.name = hostName;
    this.loginMessage = loginMessage;

    this.hasBackdoor = false;
    this.isHidden = isHidden;
    this.acl = acl;

    // Host Type Reference:
    //   - 1: Aggressive) Higher attack attributes
    //   - 2: Defensive)  Higher defense attributes
    if (type == 1) {
      this.attack = hostRating + 3;
      this.sleaze = hostRating + 2;
      this.dataProcessing = hostRating + 1;
      this.firewall = hostRating;
    } else if (type == 2) {
      this.attack = hostRating;
      this.sleaze = hostRating + 1;
      this.dataProcessing = hostRating + 2;
      this.firewall = hostRating + 3;
    }
  }
}
