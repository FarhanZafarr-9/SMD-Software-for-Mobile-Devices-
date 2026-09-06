package A.A_1;

public class Incident {
    String service;
    String startTime;
    String endTime;
    int count;

    public Incident(String service, String startTime) {
        this.service = service;
        this.startTime = startTime;
        this.endTime = startTime;
        this.count = 1;
    }

    public void update(String end) {
        this.endTime = end;
        count++;
    }

    public String getService() {
        return service;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "Service: " + service + "\n" +
                "First Error: " + startTime + "\n" +
                "Last Error: " + endTime + "\n";
    }


}
