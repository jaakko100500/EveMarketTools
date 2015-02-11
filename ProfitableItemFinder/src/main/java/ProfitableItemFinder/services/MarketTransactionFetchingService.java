package ProfitableItemFinder.services;

import Common.domain.*;
import Common.services.TypeIDService;
import ProfitableItemFinder.domain.MarketTransaction;
import ProfitableItemFinder.repositories.MarketTransactionRepository;
import ProfitableItemFinder.domain.MarketVolume;
import ProfitableItemFinder.repositories.MarketVolumeRepository;
import com.google.common.base.Joiner;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class MarketTransactionFetchingService {
    @Autowired
    private JsonFromUrlService jsonFromUrlService;

    @Autowired
    private MarketTransactionRepository marketTransactionRepository;

    @Autowired
    private MarketVolumeRepository marketVolumeRepository;

   @Autowired
   private TypeIDService typeIDService;

    public void fetchMarketTransactions() {
        try {
            List<Integer> typeIDs = typeIDService.getActiveTypeIDs();
            int regionID = 10000002;

            ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
            taskScheduler.setPoolSize(6);
            taskScheduler.initialize();

            for (int i = 0; i < typeIDs.size(); i = i + 2) {
                int startIndex = i;
                int endIndex = Math.min(i + 2, typeIDs.size() - 1);
                taskScheduler.execute(() -> {
                    try {
                        createItemMarginsFromTypeIDsInRegion(typeIDs.subList(startIndex, endIndex), regionID);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createItemMarginsFromTypeIDsInRegion(List<Integer> typeIDs, int regionID) throws IOException {
        String charName = "Rita Jita";
        Joiner joiner = Joiner.on(",").skipNulls();
        String orderUrl = String.format("http://api.eve-marketdata.com/api/item_orders2.json?char_name=%s&buysell=a&type_ids=%s&stationId=60003760", charName, joiner.join(typeIDs));

        JsonNode orderJsonNodes = jsonFromUrlService.getJsonObjectFromUrl(orderUrl);

         createMarketTransactions(orderJsonNodes);

        String volumeUrl = String.format("http://api.eve-marketdata.com/api/item_history2.json?char_name=%s&region_ids=%s&type_ids=%s", charName, regionID, joiner.join(typeIDs));

        JsonNode volumeJsonNodes = jsonFromUrlService.getJsonObjectFromUrl(volumeUrl);

         createMarketVolumes(volumeJsonNodes);
    }

    private void createMarketVolumes(JsonNode jsonNode) {
        ArrayNode rows = (ArrayNode) jsonNode.get("emd").get("result");

        List<MarketVolume> marketVolumes = new ArrayList<>(30);

        for (JsonNode rowNode : rows) {
            JsonNode row = rowNode.get("row");

            double avgPrice = getDoubleFromNode(row.get("avgPrice"));
           // Date date = getIntFromNode(row.get("typeID"));
            double highPrice = getDoubleFromNode(row.get("highPrice"));
            double lowPrice = getDoubleFromNode(row.get("lowPrice"));
            long orders = getLongFromNode(row.get("orders"));
            long regionID = getLongFromNode(row.get("regionID"));
            int typeID = getIntFromNode(row.get("typeID"));
            long volume = getLongFromNode(row.get("volume"));

            marketVolumes.add(new MarketVolume(avgPrice,highPrice,lowPrice,orders,regionID,typeID,volume));
        }


    marketVolumeRepository.save(marketVolumes);

    }

    private void createMarketTransactions(JsonNode jsonNode) {
        ArrayNode rows = (ArrayNode) jsonNode.get("emd").get("result");

        List<MarketTransaction> marketTransactions = new ArrayList<>(30);
        for (JsonNode rowNode : rows) {
            JsonNode row = rowNode.get("row");

            boolean isSell = row.get("buysell").getValueAsText().equals("s") ? true : false;
            TransactionTypeEnum transactionType = isSell ? TransactionTypeEnum.SELL : TransactionTypeEnum.BUY;

            long minVolume = getLongFromNode(row.get("minVolume"));
            long orderID = getLongFromNode(row.get("orderID"));
            double price = getDoubleFromNode(row.get("price"));
            long stationID = getLongFromNode(row.get("stationID"));
            int typeId = getIntFromNode(row.get("typeID"));
            long volEntered = getLongFromNode(row.get("volEntered"));
            long volRemaining = getLongFromNode(row.get("volRemaining"));


            marketTransactions.add( new MarketTransaction(transactionType,minVolume,orderID,price,stationID,typeId,volEntered,volRemaining));
        }
        marketTransactionRepository.save(marketTransactions);
    }

    private long getLongFromNode(JsonNode jsonNode) {
        return Long.parseLong(jsonNode.getValueAsText().replace("\"", ""));
    }

    private int getIntFromNode(JsonNode jsonNode) {
        return Integer.parseInt(jsonNode.getValueAsText().replace("\"", ""));
    }

    private double getDoubleFromNode(JsonNode jsonNode) {
        return Double.parseDouble(jsonNode.getValueAsText().replace("\"", ""));
    }


}
