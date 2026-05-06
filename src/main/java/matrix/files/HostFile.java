package matrix.files;

import main.Debug;

import matrix.MatrixEntity;
import matrix.SecurityType;
import matrix.DataBomb;
import matrix.Host;
import matrix.files.*;

import player.Player;

import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

public class HostFile extends MatrixEntity {
  private java.util.Random random = new Random();

  public ArrayList<HostFile> filesInDirectory = new ArrayList<>();

  public SecurityType fileType;

  public String contents;
  public String owner;

  public boolean isEncrypted = false;
  public boolean isDirectory = false;

  public int encryptionRating;

  public DataBomb dataBomb;

  public HostFile(Host host, String owner, SecurityType fileType, boolean isDirectory) {
    super(host.rating);
    this.accessControl = host.accessControl;

    this.fileType = fileType;
    this.isDirectory = isDirectory;

    this.owner = owner;

    // Generate File name, and contents if not directory.
    if (isDirectory){
      generateDirectory(host, owner, fileType);
    } else {
      generateFile(owner, fileType);
      
    }

    this.devCondition = host.rating * 2;
  }

  private void generateFile(String owner, SecurityType type) {
    ArrayList<FileEntry> entries = getEntriesForType(type, owner);
    FileEntry selected = entries.get(random.nextInt(entries.size()));
    this.name = selected.name;
    this.contents = selected.description;

    this.isEncrypted = (type == SecurityType.SENSITIVE
                     || type == SecurityType.SECURITY);
  }

  private void generateDirectory(Host host, String owner, SecurityType type){
    ArrayList<DirectoryEntry> dirs = getDirectoriesForType(type, owner);
    DirectoryEntry selected = dirs.get(random.nextInt(dirs.size()));
    this.name = selected.name;
    this.contents = "Directory containing " + type.toString().toLowerCase() + " files.";

    int fileCount = random.nextInt(1, 5) + 1;
    for (int i=0; i < fileCount; i++){
      HostFile file = new HostFile(host, owner, selected.contentType, false);
      file.name = selected.name + file.name;
      addToDirectory(file);
      host.addFile(file);
    }
  }

  private void addToDirectory(HostFile file){
    for (HostFile existing : filesInDirectory){
      if (existing.name.equalsIgnoreCase(file.name)){
        Debug.log("Directory already contains: " + file.name + ", skipping.");
        return;
      }
    }
    filesInDirectory.add(file);
  }

  private ArrayList<DirectoryEntry> getDirectoriesForType(SecurityType type, String owner) {
  switch (type) {
    case PUBLIC:
      return new ArrayList<>(Arrays.asList(
        new DirectoryEntry("faq/", SecurityType.PUBLIC),
        new DirectoryEntry("customer_support/", SecurityType.PUBLIC),
        new DirectoryEntry("projects/", SecurityType.PUBLIC),
        new DirectoryEntry("contact_info/", SecurityType.PUBLIC),
        new DirectoryEntry("src/", SecurityType.PUBLIC),
        new DirectoryEntry("api/", SecurityType.PUBLIC)
      ));
    case SENSITIVE:
      return new ArrayList<>(Arrays.asList(
        new DirectoryEntry("customer_projects/", SecurityType.SENSITIVE),
        new DirectoryEntry("action_reports/", SecurityType.SENSITIVE),
        new DirectoryEntry("overwatch_contract/", SecurityType.SENSITIVE),
        new DirectoryEntry("databank/", SecurityType.SENSITIVE),
        new DirectoryEntry("contracts/", SecurityType.SENSITIVE),
        new DirectoryEntry("special_projects/", SecurityType.SENSITIVE),
        new DirectoryEntry("gov_contracts/", SecurityType.SENSITIVE)
      ));
    case ADMIN:
      return new ArrayList<>(Arrays.asList(
        new DirectoryEntry("payroll/", SecurityType.ADMIN),
        new DirectoryEntry("employee_resources/", SecurityType.ADMIN),
        new DirectoryEntry("reports/", SecurityType.ADMIN),
        new DirectoryEntry("presentations/", SecurityType.ADMIN),
        new DirectoryEntry("human_resources/", SecurityType.ADMIN),
        new DirectoryEntry("employee_mail/", SecurityType.ADMIN)
      ));
    case SECURITY:
      return new ArrayList<>(Arrays.asList(
        new DirectoryEntry("access_logs/", SecurityType.SECURITY),
        new DirectoryEntry("recordings/", SecurityType.SECURITY),
        new DirectoryEntry("spider_contracts/", SecurityType.SECURITY),
        new DirectoryEntry("drone_resources/", SecurityType.SECURITY),
        new DirectoryEntry("security_sop/", SecurityType.SECURITY),
        new DirectoryEntry("god_contracts/", SecurityType.SECURITY)
      ));
    case INTERNAL:
      return new ArrayList<>(Arrays.asList(
        new DirectoryEntry("api/", SecurityType.SECURITY),
        new DirectoryEntry("server/", SecurityType.SECURITY),
        new DirectoryEntry("logs/", SecurityType.SECURITY),
        new DirectoryEntry("data/", SecurityType.SECURITY),
        new DirectoryEntry("matrix_ops/", SecurityType.SECURITY)
      ));
    default:
      return new ArrayList<>();
    }
  }

  private ArrayList<FileEntry> getEntriesForType(SecurityType type, String owner){
    switch (type) {
      case PUBLIC:
        return new ArrayList<>(Arrays.asList(
          new FileEntry(
            "contact_us.page",
            "Public contact page for matrix users submitting inquiries to " + owner + "."),
          new FileEntry(
            "customer_support_ticket-" + random.nextInt(100),
            "A current customer support ticket. Has been closed by " + owner + " and reopened by the user several times."),
          new FileEntry(
            "faq-" + random.nextInt(10),
            "A list of frequenty asked questions about " + owner + "."),
          new FileEntry(
            "projects_overview",
            "An overview of " + owner + "'s current publicly-available projects and products."),
          new FileEntry(
            "main.page",
            "The main public page for " + owner + ". Lots of buzzwords and slogans."),
          new FileEntry(
            "customer_support.page",
            "Public page where customers can submit requests and support tickets. Response time is 5 - 30 business days."),
          new FileEntry(
            "project_inquiry.page",
            "Public page where customers can submit inquiries about specific projects."),
          new FileEntry(
            "matrix_freedom_of_use_act.page",
            "Public declaration of " + owner + "'s compliance with the Matrix Freedom of Use Act. Check the fine print.")
        ));
      case SENSITIVE:
        return new ArrayList<>(Arrays.asList(
          new FileEntry(
            "customer_project_report" + random.nextInt(100),
            "Contains reports and metrics for the development of a customer-tailored project."),
          new FileEntry(
            "special_project_budget-" + random.nextInt(2080, 2097),
            "Annual report for special projects budgets. Sums in the billions of dollars."),
          new FileEntry(
            "action_report-" + random.nextInt(1, 13) + "-" + random.nextInt(2080, 2097),
            "An internal action report for a field test for a special project. Results were quite deadly."),
          new FileEntry(
            "data-" + random.nextInt(100),
            "A file containing lists of unstructured data. No way to tell what it's about"),
          new FileEntry(
            "overwatch_contract-" + random.nextInt(2080, 2097),
            "A file containing the details about " + owner + "'s annual GOD contract for Matrix Surveillance."),
          new FileEntry(
            "ytd_contracts-" + random.nextInt(2080, 2097),
            "A file containing contract summaries for the listed year. Looks like profits are up."),
          new FileEntry(
            "gov_contacts-" + random.nextInt(2080, 2097),
            "A file containing summaries of government contracts for the listed year.")
        ));
      case ADMIN:
        return new ArrayList<>(Arrays.asList(
          new FileEntry(
            "employee_report" + random.nextInt(100),
            "A report on an employee's performance. A single line reads 'PERFORMANCE: SUB_OPTIMAL'."),
          new FileEntry(
            "hr_report-" + random.nextInt(100),
            "Looks like someone got in trouble. There's an unresolved Conflict Resolution checklist."),
          new FileEntry(
            "payroll-" + random.nextInt(2080, 2097),
            "A file containing the annual payroll for " + owner + ". That's a LOT of zeros."),
          new FileEntry(
            "company_timekeeping_policy",
            "Lists " + owner + "'s timekeeping policy. Time-theft gets a whole 12 sections."),
          new FileEntry(
            "presentation-" + random.nextInt(1, 13),
            "Contains slides for an internal presentation. Lots of line graphs that don't make sense out of context."),
          new FileEntry(
            "mandated_meeting_notification",
            "A file with a description of the next mandated all-hands meeting. Snacks are deducted from employee paychecks."),
          new FileEntry(
            "office_printer_report-" + random.nextInt(100),
            "Subject reads: 'What does it MEAN?!'. Description reads: 'PC_LOAD_LETTER'."),
          new FileEntry(
            "layoff_notice-" + random.nextInt(2080, 2097),
            "A file containing a list of employee names followed by 'Security is on their way to escort you from the building'.")
        ));
      case SECURITY:
        return new ArrayList<>(Arrays.asList(
          new FileEntry(
            "access_log-" + random.nextInt(100),
            "A physical and matrix access log for " + owner + "'s secure Matrix. Oh look, there's your Alias!"),
          new FileEntry(
            "drone_action_report-" + random.nextInt(1, 13),
            "An action report for a company's drone that used deadly force."),
          new FileEntry(
            "cam_recording-" + random.nextInt(1, 13) + random.nextInt(2080, 2097),
            "A file containing video records of all camera recording from this date."),
          new FileEntry(
            "spider_contract-" + random.nextInt(100),
            "A digitally-signed contract between a Spider and " + owner + " for Matrix security. The Spider was a tough negotiator."),
          new FileEntry(
            "drone-manifest",
            "Contains a living manifest of all automated drones in the " + owner + " building."),
          new FileEntry(
            "camera-manifest",
            "Contains a living manifest of all cameras and recording equipment in the " + owner + " building."),
          new FileEntry(
            "security_shift_report-" + random.nextInt(30) + "-" + random.nextInt(1, 13),
            "A file containing details of the daily security actions at " + owner + ".")
        ));
      case INTERNAL:
        return new ArrayList<>(Arrays.asList(
          new FileEntry(
            "server_log-" + random.nextInt(100),
            "An internal server event log with terrabytes of data. Would take lifetimes to search by hand."),
          new FileEntry(
            "api_root.cfg",
            "A configuration file for the Matrix API controller."),
          new FileEntry(
            "ai_policy_v" + random.nextInt(1, 10),
            "The company's AI usage policy. Looks like it has root access to all " + owner + " servers. It's probably fine.")
        ));
        default:
          return new ArrayList<>();
    }
  }

  @Override
  public boolean equals(Object obj){
    if (this == obj) return true;
    if (!(obj instanceof HostFile)) return false;
    HostFile other = (HostFile) obj;
    return this.name.equalsIgnoreCase(other.name);
  }

  @Override
  public int hashCode() {
    return name.toLowerCase().hashCode();
  }
}

class DirectoryEntry {
  public String name;
  public SecurityType contentType;

  public DirectoryEntry(String name, SecurityType contentType) {
    this.name = name;
    this.contentType = contentType;
  }
}

class FileEntry {
  public String name;
  public String description;

  public FileEntry(String name, String description){
    this.name = name;
    this.description = description;
  }
}