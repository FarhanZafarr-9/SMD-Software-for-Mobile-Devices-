package A.A_1;

import java.io.BufferedReader;
import java.io.FileReader;

public class LogForge {

    public static String checkType(String str) {

        int feildCount = 0;
        String type = "";
        char chr = ' ';

        for (int i = 0; i < str.length(); i++) {

            chr = str.charAt(i);
            if (feildCount == 2 && chr != '|') {
                type += chr;
            }
            if (chr == '|') {
                feildCount++;
            }
            if (feildCount > 2) {
                break;
            }

        }
        return type;
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
                int errorCount = 0;
                int warnCount = 0;
                int infoCount = 0;

                while ((line = br.readLine()) != null) {

                    total++;
                    switch (checkType(line)) {
                        case "ERROR":
                            errorCount++;
                            break;
                        case "WARN":
                            warnCount++;
                            break;
                        case "INFO":
                            infoCount++;
                            break;
                    }
                }

                System.out.println("Total records: " + total);
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
