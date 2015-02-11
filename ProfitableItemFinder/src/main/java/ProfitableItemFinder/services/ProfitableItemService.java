package ProfitableItemFinder.services;

import Common.repositories.ItemProfitabilityRepository;
import Common.services.TypeIDService;
import ProfitableItemFinder.domain.Goodness;
import ProfitableItemFinder.repositories.GoodnessRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

@Component
public class ProfitableItemService {
    private static Logger logger = LoggerFactory.getLogger(ProfitableItemService.class);

    @Autowired
    private JsonFromUrlService jsonFromUrlService;

    @Autowired
    private ItemProfitabilityRepository itemProfitabilityRepository;

    @Autowired
    private TypeIDService typeIDService;

    @Autowired
    private GoodnessRepository goodnessRepository;

    public void findProfitableItems() {
        try {
            List<Integer> typeIDs = typeIDService.getActiveTypeIDs();

            for (Integer typeID : typeIDs) {
                Object[] profitValueArray = itemProfitabilityRepository.findProfitValues(typeID);
                if (profitValueArray.length != 1) {
                    continue;
                }

                Object[] profitValues = (Object[]) profitValueArray[0];


                if (profitValues[1] == null || profitValues[2] == null || profitValues[3] == null) {
                    continue;
                }

                double sell = (double) profitValues[1];
                double buy = (double) profitValues[2];
                double volume = ((BigDecimal) profitValues[3]).doubleValue();

                double margin = sell - buy;
                double marginPercentage = margin / sell;

                double marketValue = margin * volume;
                double feePercentage = 0.0058 * 2 + 0.0075;

                double goodnessValue = marketValue * (marginPercentage - feePercentage);

                Goodness goodness = new Goodness(sell, buy, volume, typeID, typeIDService.getNameByTypeID(typeID));
                goodnessRepository.save(goodness);

                System.out.println(String.format("TypeID: %s, MarketValue: %s, MarginPercentage: %s, Goodness: %s", typeID, marketValue, marginPercentage * 100, goodnessValue));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
