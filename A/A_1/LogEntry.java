package A.A_1;

public class LogEntry {
    private int id;
    private String timestamp;
    private String service;
    private String level;
    private String message;

    public LogEntry(int id, String timestamp, String service, String level, String message) {
        this.id = id;
        this.timestamp = timestamp;
        this.service = service;
        this.level = level;
        this.message = message;
    }

    @Override
    public String toString() {
        return "LogEntry{" +
                "id=" + id +
                ", timestamp='" + timestamp + '\'' +
                ", service='" + service + '\'' +
                ", level='" + level + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getService() {
        return service;
    }

    public String getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

}
