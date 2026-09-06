package A.A_1;

import java.io.BufferedReader;
import java.io.FileReader;
//import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class LogForge {

    public static String getFeild(String str, int feildNum, char delimiter) {

        int feildCount = 0;
        String field = "";
        char chr = ' ';

        for (int i = 0; i < str.length(); i++) {

            chr = str.charAt(i);

            if (chr == delimiter) {
                feildCount++;

            } else if (feildCount == feildNum) {
                field += chr;
            }
            if (feildCount > feildNum) {
                break;
            }

        }
        if (field.equals("")) {
            return "INVALID";
        }
        return field;
    }

    public static boolean validDate(String dateStr) {

        if (dateStr.length() != 19) {
            return false;
        }

        try {
            String[] parts = {
                    getFeild(dateStr, 0, ' '),
                    getFeild(dateStr, 1, ' ')
            };
            String[] dateParts = {
                    getFeild(parts[0], 0, '-'),
                    getFeild(parts[0], 1, '-'),
                    getFeild(parts[0], 2, '-')
            };
            String[] timeParts = {
                    getFeild(parts[1], 0, ':'),
                    getFeild(parts[1], 1, ':'),
                    getFeild(parts[1], 2, ':')
            };

            for (String part : parts) {
                if (part.equals("INVALID"))
                    return false;
            }
            for (String part : dateParts) {
                if (part.equals("INVALID"))
                    return false;
            }
            for (String part : timeParts) {
                if (part.equals("INVALID"))
                    return false;
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static LogEntry validateEntry(String line) {

        boolean valid = true;

        String[] fields = {
                getFeild(line, 0, '|'), // timestamp
                getFeild(line, 1, '|'), // log level
                getFeild(line, 2, '|'), // log type
                getFeild(line, 3, '|'), // log id
                getFeild(line, 4, '|'), // log message
                getFeild(line, 5, '|') // validation check using an extra feild
        };

        for (int i = 0; i < fields.length - 1; i++) {
            if (fields[i].equals("INVALID")) {
                valid = false;
                break;
            }
        }

        if (!valid) {
            return null;
        }

        if (fields[2].equals("INVALID") || !fields[5].equals("INVALID")) {
            valid = false;
        }

        if (!valid) {
            return null;
        }

        int id = 0;
        try {
            id = Integer.parseInt(fields[3]);
        } catch (Exception e) {
            return null;
        }

        if (id <= 0 || validDate(fields[0]) == false) {
            return null;
        }

        switch (fields[2]) {
            case "ERROR", "WARN", "INFO":
                break;
            default:
                return null;
        }

        return new LogEntry(id, fields[0], fields[1], fields[2], fields[4]);
    }

    public static LogEntry[] reSizeLogEntries(LogEntry[] logEntries) {
        LogEntry[] newLogEntries = new LogEntry[logEntries.length * 2];
        for (int i = 0; i < logEntries.length; i++) {
            newLogEntries[i] = logEntries[i];
        }
        logEntries = newLogEntries;
        return logEntries;
    }

    public static ServiceStats[] reSizeServiceStats(ServiceStats[] serviceStats) {
        ServiceStats[] newServiceStats = new ServiceStats[serviceStats.length * 2];
        for (int i = 0; i < serviceStats.length; i++) {
            newServiceStats[i] = serviceStats[i];
        }
        serviceStats = newServiceStats;
        return serviceStats;
    }

    public static ServiceStats[] sortServiceStats(ServiceStats[] serviceStats, int size) {
        for (int i = 0; i < size - 1; i++) {
            int best = i;
            for (int j = i + 1; j < size; j++) {
                if (isBetter(serviceStats[j], serviceStats[best])) {
                    best = j;
                }
            }
            ServiceStats temp = serviceStats[i];
            serviceStats[i] = serviceStats[best];
            serviceStats[best] = temp;
        }
        return serviceStats;
    }

    private static boolean isBetter(ServiceStats a, ServiceStats b) {
        if (a.getErrorRate() > b.getErrorRate()) {
            return true;
        }
        if (a.getErrorRate() == b.getErrorRate()
                && a.getSource().compareTo(b.getSource()) < 0) {
            return true;
        }
        return false;
    }

    private static Incident[] reSizeIncidents(Incident[] incidents) {
        Incident[] newIncidents = new Incident[incidents.length * 2];
        for (int i = 0; i < incidents.length; i++) {
            newIncidents[i] = incidents[i];
        }
        incidents = newIncidents;
        return incidents;
    }

    private static final DateTimeFormatter TS_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static long secondsBetween(String ts1, String ts2) {
        LocalDateTime t1 = LocalDateTime.parse(ts1, TS_FORMAT);
        LocalDateTime t2 = LocalDateTime.parse(ts2, TS_FORMAT);
        return ChronoUnit.SECONDS.between(t1, t2);
    }

    private static Incident[] getIncidents(LogEntry[] logEntries, int size) {

        Incident[] incidents = new Incident[5];
        int incidentIndex = 0;
        Incident currentIncident = null;
        for (LogEntry entry : logEntries) {
            if (entry == null) {
                if (currentIncident != null && currentIncident.getCount() > 2) {
                    incidents[incidentIndex++] = currentIncident;
                }
                break;
            }

            if (entry.getLevel().equals("ERROR")) {

                if (currentIncident == null) {
                    currentIncident = new Incident(entry.getService(), entry.getTimestamp());
                } else {
                    long gap = secondsBetween(currentIncident.getStartTime(), entry.getTimestamp());
                    if (gap <= 60) {
                        currentIncident.update(entry.getTimestamp());
                    } else {
                        if (currentIncident.getCount() > 2) {
                            incidents[incidentIndex++] = currentIncident;

                            if (incidentIndex == incidents.length) {
                                incidents = reSizeIncidents(incidents);
                            }
                        }
                        currentIncident = new Incident(entry.getService(), entry.getTimestamp());
                    }
                }
            } else {
                if (currentIncident != null && currentIncident.getCount() > 2) {
                    incidents[incidentIndex++] = currentIncident;

                    if (incidentIndex == incidents.length) {
                        incidents = reSizeIncidents(incidents);
                    }
                }
                currentIncident = null;
            }
        }
        return incidents;
    }

    public static RequestStats[] resizeRequestStats(RequestStats[] requestStats) {
        RequestStats[] newRequestStats = new RequestStats[requestStats.length * 2];
        for (int i = 0; i < requestStats.length; i++) {
            newRequestStats[i] = requestStats[i];
        }
        return newRequestStats;
    }

    public static long timestampToNumber(String timestamp) {
        String datePart = getFeild(timestamp, 0, ' ');
        String timePart = getFeild(timestamp, 1, ' ');

        int year = Integer.parseInt(getFeild(datePart, 0, '-'));
        int month = Integer.parseInt(getFeild(datePart, 1, '-'));
        int day = Integer.parseInt(getFeild(datePart, 2, '-'));

        int hour = Integer.parseInt(getFeild(timePart, 0, ':'));
        int minute = Integer.parseInt(getFeild(timePart, 1, ':'));
        int second = Integer.parseInt(getFeild(timePart, 2, ':'));

        return year * 10000000000L
                + month * 100000000L
                + day * 1000000L
                + hour * 10000L
                + minute * 100L
                + second;
    }

    public static LogEntry[] sortLogEntriesByTimestamp(LogEntry[] logEntries, int size) {
        for (int i = 1; i < size; i++) {
            LogEntry current = logEntries[i];
            long currentValue = timestampToNumber(current.getTimestamp());
            int j = i - 1;

            while (j >= 0 && timestampToNumber(logEntries[j].getTimestamp()) > currentValue) {
                logEntries[j + 1] = logEntries[j];
                j--;
            }
            logEntries[j + 1] = current;
        }
        return logEntries;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please provide a log file path as an argument.");
            return;
        } else {

            LogEntry[] logEntries = new LogEntry[5];
            int logEntryIndex = 0;
            ServiceStats[] serviceStats = new ServiceStats[5];
            int serviceStatsIndex = 0;
            RequestStats[] requestStats = new RequestStats[5];
            int requestStatsIndex = 0;

            FileReader fr = null;
            BufferedReader br = null;
            try {
                fr = new FileReader(args[0]);
                br = new BufferedReader(fr);
                String line;

                int validRecords = 0, totalWarnings = 0, totalErrors = 0, totalInfo = 0, invalidRecords = 0;

                while ((line = br.readLine()) != null) {
                    LogEntry entry = validateEntry(line);

                    if (entry == null) {
                        invalidRecords++;
                        continue;
                    }

                    logEntries[logEntryIndex++] = entry;
                    if (logEntryIndex == logEntries.length) {
                        logEntries = reSizeLogEntries(logEntries);
                    }
                }

                logEntries = sortLogEntriesByTimestamp(logEntries, logEntryIndex);

                for (int i = 0; i < logEntryIndex; i++) {
                    LogEntry entry = logEntries[i];
                    boolean found = false;

                    for (int j = 0; j < requestStats.length; j++) {
                        if (requestStats[j] != null && requestStats[j].getId() == entry.getId()) {
                            requestStats[j].updateStats(entry);
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        RequestStats newRequestStat = new RequestStats(entry.getId(), entry);
                        requestStats[requestStatsIndex++] = newRequestStat;
                        if (requestStatsIndex == requestStats.length) {
                            requestStats = resizeRequestStats(requestStats);
                        }
                    }
                }

                for (int i = 0; i < logEntryIndex; i++) {
                    LogEntry entry = logEntries[i];
                    String service = entry.getService();
                    boolean found = false;

                    for (int j = 0; j < serviceStats.length; j++) {
                        if (serviceStats[j] != null && serviceStats[j].getSource().equals(service)) {
                            serviceStats[j].updateStats(entry);
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        ServiceStats newServiceStat = new ServiceStats(service);
                        newServiceStat.updateStats(entry);

                        serviceStats[serviceStatsIndex++] = newServiceStat;
                        if (serviceStatsIndex == serviceStats.length) {
                            serviceStats = reSizeServiceStats(serviceStats);
                        }
                    }
                }

                System.out.println("Service Statistics:\n");
                serviceStats = sortServiceStats(serviceStats, serviceStatsIndex);
                for (int i = 0; i < serviceStatsIndex; i++) {
                    System.out.println(serviceStats[i].toString());

                    validRecords += serviceStats[i].getTotalRecords();
                    totalWarnings += serviceStats[i].getWarningCount();
                    totalErrors += serviceStats[i].getErrorCount();
                    totalInfo += serviceStats[i].getInfoCount();
                }

                System.out.println("\nSummary:\n");
                System.out.println("Total records: " + (validRecords + invalidRecords));
                System.out.println("Valid records: " + validRecords);
                System.out.println("Invalid records: " + invalidRecords);
                System.out.println("INFO: " + totalInfo);
                System.out.println("WARN: " + totalWarnings);
                System.out.println("ERROR: " + totalErrors);

                for (int i = 0; i < serviceStatsIndex; i++) {
                    ServiceStats stats = serviceStats[i];
                    Incident[] incidents = getIncidents(stats.getLogEntries(), stats.getLogEntryIndex());

                    System.out.println("\nIncidents for service: " + stats.getSource());
                    boolean hasIncidents = false;
                    for (Incident incident : incidents) {
                        if (incident != null) {
                            System.out.println(incident.toString());
                            hasIncidents = true;
                        }
                    }
                    if (!hasIncidents) {
                        System.out.println("No incidents found for this service.");
                    }
                }

                for (RequestStats requestStat : requestStats) {
                    if (requestStat != null) {
                        System.out.println(requestStat.toString());
                    }
                }

            } catch (Exception e) {
                System.err.println("Error occurred while reading the log file: " + e.getMessage());

            } finally {
                try {
                    if (br != null)
                        br.close();
                    if (fr != null)
                        fr.close();
                } catch (Exception e) {
                    System.err.println("Error occurred while closing the file: " + e.getMessage());
                }
            }
        }
    }
}
