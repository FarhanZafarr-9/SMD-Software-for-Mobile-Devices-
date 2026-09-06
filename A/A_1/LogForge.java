package A.A_1;

import java.io.BufferedReader;
import java.io.FileReader;
//import java.util.Scanner;

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

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please provide a log file path as an argument.");
            return;
        } else {
            for (String arg : args) {
                System.out.println(arg);
            }
            FileReader fr = null;
            BufferedReader br = null;
            try {
                fr = new FileReader(args[0]);
                br = new BufferedReader(fr);
                String line;

                int total = 0;
                // int validCount = 0;
                int invalidCount = 0;
                int errorCount = 0;
                int warnCount = 0;
                int infoCount = 0;

                while ((line = br.readLine()) != null) {

                    boolean valid = true;

                    String[] fields = {
                            getFeild(line, 0, '|'), // timestamp
                            getFeild(line, 1, '|'), // log level
                            getFeild(line, 2, '|'), // log type
                            getFeild(line, 3, '|'), // log id
                            getFeild(line, 4, '|'), // log message
                            getFeild(line, 5, '|') // validation check using an extra feild
                    };

                    total++;

                    for (int i = 0; i < fields.length - 1; i++) {
                        if (fields[i].equals("INVALID")) {
                            valid = false;
                            break;
                        }
                    }

                    if (!valid) {
                        invalidCount++;
                        continue;
                    }

                    if (fields[2].equals("INVALID") || !fields[5].equals("INVALID")) {
                        valid = false;
                    }

                    if (!valid) {
                        invalidCount++;
                        continue;
                    }

                    int id = 0;
                    try {
                        id = Integer.parseInt(fields[3]);
                    } catch (Exception e) {
                        invalidCount++;
                        continue;
                    }

                    if (id <= 0 || validDate(fields[0]) == false) {
                        invalidCount++;
                        continue;
                    }

                    switch (fields[2]) {
                        case "ERROR":
                            errorCount++;
                            break;
                        case "WARN":
                            warnCount++;
                            break;
                        case "INFO":
                            infoCount++;
                            break;
                        default:
                            valid = false;
                            break;
                    }

                    if (!valid) {
                        invalidCount++;
                        continue;
                    }
                }

                System.out.println("Total lines: " + total);
                System.out.println("Valid records: " + (total - invalidCount));
                System.out.println("Invalid records: " + invalidCount);
                System.out.println("INFO: " + infoCount);
                System.out.println("WARN: " + warnCount);
                System.out.println("ERROR: " + errorCount);

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
