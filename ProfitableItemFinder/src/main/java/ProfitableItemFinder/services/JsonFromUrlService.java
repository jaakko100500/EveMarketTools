package ProfitableItemFinder.services;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@Component
public class JsonFromUrlService {

    private ObjectMapper objectMapper;

    @PostConstruct
    public void setup() {
        objectMapper = new ObjectMapper();
    }

    public JsonNode getJsonObjectFromUrl(String url) throws IOException {
        InputStream responseFromUrl = getResponseFromUrl(url);
       return objectMapper.readTree(responseFromUrl);
    }

    private InputStream getResponseFromUrl(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", "Java 8");

        int responseCode = con.getResponseCode();

        return con.getInputStream();
    }
}
