package EveApiPoller.services;

import Common.domain.RichTransaction;
import Common.domain.TotalWealth;
import Common.domain.TransactionTypeEnum;
import Common.repositories.TotalWealthRepository;
import Common.services.EveApiAuthService;
import com.beimin.eveapi.character.accountbalance.AccountBalanceParser;
import com.beimin.eveapi.character.marketorders.MarketOrdersParser;
import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.shared.accountbalance.AccountBalanceHandler;
import com.beimin.eveapi.shared.accountbalance.AccountBalanceResponse;
import com.beimin.eveapi.shared.accountbalance.EveAccountBalance;
import com.beimin.eveapi.shared.marketorders.ApiMarketOrder;
import com.beimin.eveapi.shared.marketorders.MarketOrdersResponse;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class TotalWealthService {
    private static final long EVE_API_POLL_INTERVAL = 60000L;
    private static Logger logger = LoggerFactory.getLogger(TotalWealthService.class);
    private Date eveApiCachedUntil = null;

    @Autowired
    private EveApiAuthService eveApiAuthService;
    @Autowired
    private TotalWealthRepository totalWealthRepository;

    public void updateTotalWealth() {
        try {
            Set<ApiMarketOrder> marketOrders = getMarketOrders();

            if (marketOrders.isEmpty()) {
                return ;
            }

            Iterable<ApiMarketOrder> activeMarketOrders = Iterables.filter(marketOrders, s -> s.getOrderState() == 0);
            Iterable<ApiMarketOrder> sellMarketOrders = Iterables.filter(activeMarketOrders, s -> s.getBid() == 0);
            Iterable<ApiMarketOrder> buyMarketOrders = Iterables.filter(activeMarketOrders, s -> s.getBid() == 1);

            Double sellOrdersValue = getSum(sellMarketOrders);
            Double buyOrdersValue = getSum(buyMarketOrders);

            Set<EveAccountBalance> accountBalance = getAccountBalance();
            EveAccountBalance first = Iterables.getFirst(accountBalance, null);
            if (first != null) {
                totalWealthRepository.save(new TotalWealth(first.getBalance(), buyOrdersValue, sellOrdersValue));
                logger.info("Balance: {}, Buys: {}, Sells: {}", first.getBalance(), buyOrdersValue, sellOrdersValue);
            }

        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    private double getSum(Iterable<ApiMarketOrder> marketOrders) {
        double ordersValue = 0d;
        for (ApiMarketOrder buyMarketOrder : marketOrders) {
            ordersValue += buyMarketOrder.getVolRemaining() * buyMarketOrder.getPrice();
        }
        return ordersValue;
    }

    private Set<EveAccountBalance> getAccountBalance() throws ApiException {
        AccountBalanceParser accountBalanceParser = AccountBalanceParser.getInstance();
        AccountBalanceResponse accountBalanceResponse = accountBalanceParser.getResponse(eveApiAuthService.getApiAuthorization());
        logger.info("Fetched account balance from API.");

        return accountBalanceResponse.getAll();
    }

    private Set<ApiMarketOrder> getMarketOrders() throws ApiException {
        if (eveApiCachedUntil == null || new Date().compareTo(eveApiCachedUntil) > 0) {
            MarketOrdersParser marketOrdersParser = MarketOrdersParser.getInstance();

            MarketOrdersResponse marketOrdersResponse = marketOrdersParser.getResponse(eveApiAuthService.getApiAuthorization());

            eveApiCachedUntil = marketOrdersResponse.getCachedUntil();
            logger.info("Fetched Market Orders API. New EVE API cache date: {}", eveApiCachedUntil);
            return marketOrdersResponse.getAll();
        } else {
            return new HashSet<>();
        }
    }
}
