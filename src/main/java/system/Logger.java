package system;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Logger {

    public static void info(String msg) {
        System.out.println(msg);
    }

    public static void error(String msg) {
        System.err.println(msg);
    }

    public static void verbose(String msg) {
        System.out.println(msg);
    }


    public static void logToFile(String msg) {
        String time = ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("./log.txt", true))) {
            writer.write("[" + time + "] " + msg);
            writer.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
