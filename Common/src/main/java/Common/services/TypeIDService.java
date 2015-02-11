package Common.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TypeIDService {
    @Value("classpath:typeIDs.csv")
    private Resource typeIDResource;

    @Value("classpath:typeIDs.txt")
    private Resource activeTypeIDResource;

    private Map<Integer, String> typeIDMap = new HashMap<>();
    private List<Integer> activeTypeIDs = new ArrayList<>();

    @PostConstruct
    private void setup() throws IOException {
        readTypeIDNames(typeIDResource.getInputStream());
        readTypeIDsFromFile(activeTypeIDResource.getInputStream());
    }

    public List<Integer> getActiveTypeIDs() {
        return activeTypeIDs;
    }

    public String getNameByTypeID(Integer typeID) {
        return typeIDMap.get(typeID);
    }

    private void readTypeIDNames(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        String line = null;
        while ((line = br.readLine()) != null) {
            String[] split = line.split("\\|");
            typeIDMap.put(Integer.parseInt(split[0]), split[1]);
        }

        br.close();
    }

    private void readTypeIDsFromFile(InputStream file) throws IOException {
        // Construct BufferedReader from FileReader
        BufferedReader br = new BufferedReader(new InputStreamReader(file));

        String line = null;
        while ((line = br.readLine()) != null) {
            activeTypeIDs.add(Integer.parseInt(line));
        }

        br.close();
    }

}
