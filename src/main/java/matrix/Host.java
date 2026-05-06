package matrix;

import java.util.ArrayList;

import enemy.Spider;
import main.Debug;
import matrix.device.Device;
import matrix.files.HostFile;
import matrix.ic.IC;
import matrix.ic.ICFactory;
import matrix.ic.ICType;

public class Host extends MatrixEntity {

  public int type;

  public boolean isAlert = false;

  public ArrayList<String> loginMessage;

  public SecurityType hostType;

  public ArrayList<HostFile> filesOnHost = new ArrayList<>();

  public ArrayList<Device> devicesOnHost = new ArrayList<>();

  public ArrayList<Host> ncl = new ArrayList<>();
  public ArrayList<MatrixEntity> entities = new ArrayList<>();
  public ArrayList<ICType> icTypesAllowed = new ArrayList<>();
  public ArrayList<IC> deployedIC = new ArrayList<>();

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

  public void addToNCL(Host host){
    ncl.add(host);
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

  // IC deployment and operating logic

  public IC deployIC(ICType type){
    if (!icTypesAllowed.contains(type)) {
      Debug.log("Host is not authorized to deploy " + type.getLabel());
      return null;
    }

    IC ic = ICFactory.create(type, this);
    if (ic != null){
      deployedIC.add(ic);
      System.out.println("[SYSTEM] Host has deployed " + type.getLabel());
    }

    return ic;
  }

  public void addAllowedICType(ICType ic){
    icTypesAllowed.add(ic);
  }

  public boolean hasDeployedICOfType(ICType type){
    for (IC ic : deployedIC){
      if (ic.icType.equals(type)) return true;
    }
    return false;
  }

  // File management and operating logic

  public void addFile(HostFile file){
    for (HostFile existing : filesOnHost){
      if (existing.name.equalsIgnoreCase(file.name)){
        Debug.log("filesOnHost already contains: " + file.name + ", skipping.");
        return;
      }
    }
    filesOnHost.add(file);
  }

  public HostFile findFile(String path) {
    for (HostFile file : filesOnHost) {
      if (file.name.equalsIgnoreCase(path)) {
        return file;
      }

      if (file.isDirectory) {
        for (HostFile nested : file.filesInDirectory) {
          if (nested.name.equalsIgnoreCase(path)) {
            return nested;
          }
        }
      }
    }

    System.out.println("[ERROR] FILE_NOT_FOUND: '" + path + "' not found on this host.");
    System.out.println("Use 'ls' to list files on the host.");
    return null;
  }

  // Device deployment and management logic

  public void addDevice(Device device){
    for (Device existing : devicesOnHost){
      if (existing.name.equalsIgnoreCase(device.name)){
        Debug.log("devicesOnHost already contains: " + device.name + ", skipping.");
        return;
      }  
    }
    devicesOnHost.add(device);
  }

  public Device findDevice(String deviceName) {
    for (Device device : devicesOnHost) {
      if (device.name.equalsIgnoreCase(deviceName)) {
        return device;
      }
    }
    System.out.println("[ERROR] DEVICE_NOT_FOUND: '" + deviceName + "' not found on this host.");
    System.out.println("Use 'devs' to list devices on the host.");
    return null;
  }

  public Host(int hostRating, int type, SecurityType hostType, String hostName, ArrayList<String> loginMessage, boolean isHidden, ArrayList<Host> ncl) {
    super(hostRating);
    this.type = type;
    this.hostType = hostType;
    this.name = hostName;
    this.loginMessage = loginMessage;

    this.hasBackdoor = false;
    this.isHidden = isHidden;
    this.ncl = ncl;

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
