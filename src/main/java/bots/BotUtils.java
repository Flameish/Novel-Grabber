//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package bots;

import grabber.GrabberUtils;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class BotUtils {
    public BotUtils() {
    }

    public static String getStringFromFile(String fileName) {
        StringBuilder resultStringBuilder = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            Throwable var3 = null;

            try {
                String line;
                try {
                    while((line = br.readLine()) != null) {
                        resultStringBuilder.append(line).append("\n");
                    }
                } catch (Throwable var13) {
                    var3 = var13;
                    throw var13;
                }
            } finally {
                if (br != null) {
                    if (var3 != null) {
                        try {
                            br.close();
                        } catch (Throwable var12) {
                            var3.addSuppressed(var12);
                        }
                    } else {
                        br.close();
                    }
                }

            }
        } catch (IOException var15) {
            GrabberUtils.err("File found: " + fileName);
        }

        return resultStringBuilder.toString();
    }
}
