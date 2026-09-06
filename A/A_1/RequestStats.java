package A.A_1;

public class RequestStats {
    private int id;
    private String status;
    private String[] services;
    private int records;
    private int errors;
    private LogEntry[] logEntries;
    private int logEntryIndex;

    public RequestStats(int id, LogEntry entry) {
        this.id = id;
        this.services = new String[10];
        this.services[0] = entry.getService();
        this.records = 1;
        if (entry.getLevel().equals("ERROR")) {
            this.errors = 1;
            this.status = "FAILED";
        } else {
            this.errors = 0;
            this.status = "SUCCESS";
        }
        this.logEntries = new LogEntry[10];
        this.logEntries[0] = entry;
        this.logEntryIndex = 1;
    }

    public void updateStats(LogEntry entry) {
        records++;
        if (entry.getLevel().equals("ERROR")) {
            errors++;
            status = "FAILED";
        }
        boolean serviceExists = false;
        for (int i = 0; i < services.length; i++) {
            if (services[i] != null && services[i].equals(entry.getService())) {
                serviceExists = true;
                break;
            }
        }
        if (!serviceExists) {
            for (int i = 0; i < services.length; i++) {
                if (services[i] == null) {
                    services[i] = entry.getService();
                    break;
                }
            }
        }

        logEntries[logEntryIndex++] = entry;

        if (logEntryIndex == logEntries.length) {
            LogEntry[] newLogEntries = new LogEntry[logEntries.length * 2];
            for (int i = 0; i < logEntries.length; i++) {
                newLogEntries[i] = logEntries[i];
            }
            logEntries = newLogEntries;
        }
    }

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String[] getServices() {
        return services;
    }

    public int getRecords() {
        return records;
    }

    public int getErrors() {
        return errors;
    }

    public LogEntry[] getLogEntries() {
        return logEntries;
    }

    public int getLogEntryIndex() {
        return logEntryIndex;
    }

    @Override
    public String toString() {
        String servicesList = "";
        for (int i = 0; i < services.length; i++) {
            if (services[i] != null) {
                servicesList += services[i] + " ";
            }
        }
        return "Request: " + id + "\n" +
                "Status: " + status + "\n" +
                "Records: " + records + "\n" +
                "Errors: " + errors + "\n" +
                "Services: " + servicesList.trim() + "\n";
    }

    

}
