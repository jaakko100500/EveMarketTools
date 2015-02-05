package EveMarketTools.services;

import EveMarketTools.domain.RichTransaction;
import EveMarketTools.domain.RichTransactionRepository;
import EveMarketTools.domain.TransactionTypeEnum;
import com.beimin.eveapi.character.wallet.transactions.WalletTransactionsParser;
import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.shared.wallet.transactions.ApiWalletTransaction;
import com.beimin.eveapi.shared.wallet.transactions.WalletTransactionsResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Component
public class RichTransactionCreationService {
    private static final long EVE_API_POLL_INTERVAL = 60000L;
    private static final String MAP_SERIALIZATION_PATH = "map.json";
    private static Logger logger = LoggerFactory.getLogger(RichTransactionCreationService.class);
    private Date lastProcessedTransactionDate = null;
    private Date eveApiCachedUntil = null;
    private RepeatableStackMap<Integer, ApiWalletTransaction> repeatableStackMap = new RepeatableStackMap<Integer, ApiWalletTransaction>();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private EveApiAuthService eveApiAuthService;

    @Autowired
    private RichTransactionRepository richTransactionRepository;

    public void startCreatingTransactions() throws IOException {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(1);
        taskScheduler.initialize();
        taskScheduler.scheduleWithFixedDelay(this::getNewRichTransactions, EVE_API_POLL_INTERVAL);

        lastProcessedTransactionDate = richTransactionRepository.findLastTransactionDate();
        logger.info("Starting to update with last processed transaction date {}",lastProcessedTransactionDate);

        File file = new File(MAP_SERIALIZATION_PATH);
        if (repeatableStackMap.isEmpty() && file.exists()) {
            repeatableStackMap = objectMapper.readValue(file, RepeatableStackMap.class);
        }
        else {
            file.createNewFile();
        }
    }

    @Transactional
    public void getNewRichTransactions() {
        try {
            Set<ApiWalletTransaction> apiWalletTransactions = getApiJournalEntries();
            List<ApiWalletTransaction> sortedApiWalletTransactions = new ArrayList<>(apiWalletTransactions);
            Collections.sort(sortedApiWalletTransactions, (o1, o2) -> o1.getTransactionDateTime().compareTo(o2.getTransactionDateTime()));

            List<RichTransaction> richTransactions = new ArrayList<>(apiWalletTransactions.size());

            for (ApiWalletTransaction walletTransaction : sortedApiWalletTransactions) {
                if (lastProcessedTransactionDate == null || walletTransaction.getTransactionDateTime().compareTo(lastProcessedTransactionDate) > 0) {
                    RichTransaction richTransaction = new RichTransaction(walletTransaction);

                    if (richTransaction.transactionType == TransactionTypeEnum.BUY) {
                        repeatableStackMap.put(walletTransaction.getTypeID(), new Long(walletTransaction.getQuantity()), walletTransaction);

                    } else {
                        List<QueueEntry<ApiWalletTransaction>> buyTransactions = repeatableStackMap.take(walletTransaction.getTypeID(), new Long(walletTransaction.getQuantity()));
                        richTransaction.setFeesAndBuyPrice(buyTransactions);
                    }

                    richTransactions.add(richTransaction);
                    lastProcessedTransactionDate = walletTransaction.getTransactionDateTime();
                }
            }

            Collections.sort(richTransactions, (o1, o2) -> o2.transactionDate.compareTo(o1.transactionDate));

            richTransactionRepository.save(richTransactions);

            logger.info("{} new RichTransactions processed.",richTransactions.size());
            objectMapper.writeValue(new File(MAP_SERIALIZATION_PATH), repeatableStackMap);
        } catch (Exception e) {
            logger.error("Error: {}", e);
        }
    }

    private Set<ApiWalletTransaction> getApiJournalEntries() throws ApiException {
        if (eveApiCachedUntil == null || new Date().compareTo(eveApiCachedUntil) > 0) {
            WalletTransactionsParser walletTransactionsParser = WalletTransactionsParser.getInstance();

            WalletTransactionsResponse walletTransactionsResponse = walletTransactionsParser.getTransactionsResponse(eveApiAuthService.getApiAuthorization());

            eveApiCachedUntil = walletTransactionsResponse.getCachedUntil();
            logger.info("Fetched from API. New EVE API cache date: {}", eveApiCachedUntil);
            return walletTransactionsResponse.getAll();
        } else {
            return new HashSet<>();
        }
    }
}
