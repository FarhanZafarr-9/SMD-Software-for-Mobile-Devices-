package A.A_1;

public class ServiceStats {
    private String source;
    private int totalRecords;
    private int warningCount;
    private int errorCount;
    private int infoCount;
    private float errorRate;
    private LogEntry[] logEntries;
    private int logEntryIndex;

    public ServiceStats(String source) {
        this.source = source;
        this.totalRecords = 0;
        this.warningCount = 0;
        this.errorCount = 0;
        this.infoCount = 0;
        this.errorRate = 0.00f;
        this.logEntries = new LogEntry[10];
        this.logEntryIndex = 0;
    }

    @Override
    public String toString() {
        return "Service: " + source + "\n" +
                "Total: " + totalRecords + "\n" +
                "INFO: " + infoCount + "\n" +
                "WARN: " + warningCount + "\n" +
                "ERROR: " + errorCount + "\n" +
                "Error Rate: " + String.format("%.2f", errorRate) + "%\n";
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

        errorRate = (float) errorCount / totalRecords * 100;

        logEntries[logEntryIndex++] = entry;

        if (logEntryIndex == logEntries.length) {
            LogEntry[] newLogEntries = new LogEntry[logEntries.length * 2];
            for (int i = 0; i < logEntries.length; i++) {
                newLogEntries[i] = logEntries[i];
            }
            logEntries = newLogEntries;
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

    public float getErrorRate() {
        return errorRate;
    }

    public LogEntry[] getLogEntries() {
        return logEntries;
    }

    public int getLogEntryIndex() {
        return logEntryIndex;
    }
}
