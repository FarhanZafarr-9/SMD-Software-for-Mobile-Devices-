package A.A_1;

public class ServiceStats {
    private String source;
    private int totalRecords;
    private int warningCount;
    private int errorCount;
    private int infoCount;

    public ServiceStats(String source) {
        this.source = source;
        this.totalRecords = 0;
        this.warningCount = 0;
        this.errorCount = 0;
        this.infoCount = 0;
    }

    @Override
    public String toString() {
        return source +
                ", totalRecords=" + totalRecords +
                ", info=" + infoCount +
                ", warn=" + warningCount +
                ", error=" + errorCount;
    }

    public void updateStats(LogEntry entry) {
        totalRecords++;
        switch (entry.getLevel()) {
            case "ERROR":
                errorCount++;
                break;
            case "WARN":
                warningCount++;
                break;
            case "INFO":
                infoCount++;
                break;
            default:
                break;
        }
    }

    // getters

    public String getSource() {
        return source;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public int getWarningCount() {
        return warningCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public int getInfoCount() {
        return infoCount;
    }
}
