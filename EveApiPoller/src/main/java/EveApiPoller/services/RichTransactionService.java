package EveApiPoller.services;

import Common.domain.RichTransaction;
import Common.repositories.RichTransactionRepository;
import Common.domain.TransactionTypeEnum;
import Common.services.EveApiAuthService;
import com.beimin.eveapi.character.wallet.transactions.WalletTransactionsParser;
import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.shared.wallet.transactions.ApiWalletTransaction;
import com.beimin.eveapi.shared.wallet.transactions.WalletTransactionsResponse;
import com.google.common.collect.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class RichTransactionService {
    private static Logger logger = LoggerFactory.getLogger(RichTransactionService.class);
    private Date eveApiCachedUntil = null;

    @Autowired
    private EveApiAuthService eveApiAuthService;

    @Autowired
    private RichTransactionRepository richTransactionRepository;

    @Transactional
    public void createNewTransactions() {

        try {
            Set<ApiWalletTransaction> apiWalletTransactions = getApiJournalEntries();
            if (!apiWalletTransactions.isEmpty()) {
                createNewRichTransactions(apiWalletTransactions);
                fillBuyPrices();
            }
        } catch (ApiException e) {
            logger.error("Error: {}", e);
        }
    }

    private void fillBuyPrices() {
        int filledBuyPrices = 0;
        List<RichTransaction> unprocessedRichTransactions = richTransactionRepository.findUnprocessedOrders();

        ListMultimap<TransactionKey, RichTransaction> richTransactionsByTypeName = ArrayListMultimap.create();
        for (RichTransaction unprocessedRichTransaction : unprocessedRichTransactions) {
            richTransactionsByTypeName.put(new TransactionKey(unprocessedRichTransaction), unprocessedRichTransaction);
        }


        for (RichTransaction unprocessedRichTransaction : unprocessedRichTransactions) {
            if (unprocessedRichTransaction.transactionType == TransactionTypeEnum.SELL) {
                RichTransaction sellRichTransaction = unprocessedRichTransaction;
                TransactionKey transactionKey = new TransactionKey(sellRichTransaction.typeName, TransactionTypeEnum.BUY);
                List<RichTransaction> buyRichTransactions = richTransactionsByTypeName.get(transactionKey);

                int sellQuantityLeft = sellRichTransaction.quantity;
                for (RichTransaction buyRichTransaction : buyRichTransactions) {
                    if (buyRichTransaction.transactionDate.compareTo(sellRichTransaction.transactionDate) > 0) {
                        continue;
                    }
                    if (sellQuantityLeft <= buyRichTransaction.unprocessedQuantity) {
                        sellRichTransaction.buyPrice += sellQuantityLeft * buyRichTransaction.price;
                        buyRichTransaction.unprocessedQuantity -= sellQuantityLeft;
                        sellQuantityLeft = 0;

                        sellRichTransaction.debug += "B" + buyRichTransaction.price;
                    } else if (buyRichTransaction.unprocessedQuantity > 0) {
                        sellRichTransaction.buyPrice += buyRichTransaction.unprocessedQuantity * buyRichTransaction.price;
                        sellQuantityLeft -= buyRichTransaction.unprocessedQuantity;
                        buyRichTransaction.unprocessedQuantity = 0;

                        sellRichTransaction.debug += "B" + buyRichTransaction.price;
                    }


                    if (sellQuantityLeft == 0) {
                        break;
                    }
                }

                if (sellQuantityLeft > 0) {
                    sellRichTransaction.debug += "Could not find enough buy transactions!";
                    sellRichTransaction.buyPrice+=sellQuantityLeft * sellRichTransaction.price;
                }

                sellRichTransaction.unprocessedQuantity = 0;
                richTransactionRepository.save(buyRichTransactions);
                richTransactionRepository.save(sellRichTransaction);

                filledBuyPrices++;
            }
        }
        logger.info("{} buyPrices filled.",filledBuyPrices);
    }


    private void createNewRichTransactions(Set<ApiWalletTransaction> apiWalletTransactions) {
        Date lastProcessedTransactionDate = richTransactionRepository.findLastTransactionDate();

        List<ApiWalletTransaction> sortedApiWalletTransactions = new ArrayList<>(apiWalletTransactions);
        Collections.sort(sortedApiWalletTransactions, (o1, o2) -> o1.getTransactionDateTime().compareTo(o2.getTransactionDateTime()));

        List<RichTransaction> richTransactions = new ArrayList<>(apiWalletTransactions.size());

        for (ApiWalletTransaction walletTransaction : sortedApiWalletTransactions) {
            if (lastProcessedTransactionDate == null || walletTransaction.getTransactionDateTime().compareTo(lastProcessedTransactionDate) > 0) {
                RichTransaction richTransaction = new RichTransaction(walletTransaction);
                richTransactions.add(richTransaction);
            }
        }

        Collections.sort(richTransactions, (o1, o2) -> o2.transactionDate.compareTo(o1.transactionDate));

        richTransactionRepository.save(richTransactions);

        logger.info("{} new RichTransactions processed.", richTransactions.size());
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

    private static class TransactionKey {
        public String typeName;
        public TransactionTypeEnum transactionType;

        public TransactionKey(RichTransaction richTransaction) {
            this(richTransaction.typeName, richTransaction.transactionType);
        }

        public TransactionKey(String typeName, TransactionTypeEnum transactionType) {
            this.typeName = typeName;
            this.transactionType = transactionType;
        }

        @Override
        public boolean equals(Object obj) {
            if (super.equals(obj)) {
                return true;
            }

            if (obj instanceof TransactionKey) {
                TransactionKey other = (TransactionKey) obj;
                return this.transactionType.equals(other.transactionType) && this.typeName.equals(other.typeName);
            }

            return false;
        }

        @Override
        public int hashCode() {
            return com.google.common.base.Objects.hashCode(this.transactionType, this.typeName);
        }

        @Override
        public String toString() {
            return String.format("[%s, %s]", transactionType, typeName);
        }
    }
}
