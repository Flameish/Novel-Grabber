package grabber;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

public class xhrRequest {

    private final String USER_AGENT = "Mozilla/5.0";

    public static Map<String, String> tapReadGetChapterList(String url, String paramenter) {
        xhrRequest http = new xhrRequest();
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(http.sendPost(url, paramenter));
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject results = (JSONObject) jsonObject.get("result");
            JSONArray chapterObjects = (JSONArray) results.get("chapterList");
            Map<String, String> chapterMap = new LinkedHashMap<>();
            for (Object o : chapterObjects) {
                JSONObject slide = (JSONObject) o;
                String chapterId = String.valueOf(slide.get("chapterId"));
                String chapterName = String.valueOf(slide.get("chapterName"));
                chapterMap.put(chapterId, chapterName);
            }
            return chapterMap;
        } catch (ParseException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String tapReadGetChapterContent(String url, String paramenter) {
        xhrRequest http = new xhrRequest();
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(http.sendPost(url, paramenter));
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject results = (JSONObject) jsonObject.get("result");
            String content = (String) results.get("content");
            return content;
        } catch (ParseException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // HTTP POST request
    public String sendPost(String url, String paramenter) throws IOException {
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = paramenter;

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        //System.out.println(response.toString());
        return response.toString();
    }

}

