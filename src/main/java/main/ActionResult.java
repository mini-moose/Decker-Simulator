package main;

public class ActionResult {
    public boolean success;
    public Integer netHits;
    public int overwatchGenerated;
    public String message;

    public ActionResult(boolean success, int netHits, int overwatchGenerated, String message) {
        this.success = success;
        this.netHits = netHits;
        this.overwatchGenerated = overwatchGenerated;
        this.message = message;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Net Hits: %d | Overwatch: +%d",
            success ? "SUCCESS" : "FAILED", message, netHits, overwatchGenerated);
    }
}