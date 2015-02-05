package EveMarketTools.domain;

import EveMarketTools.services.QueueEntry;
import com.beimin.eveapi.shared.wallet.transactions.ApiWalletTransaction;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;
import java.util.List;

@Entity
public class RichTransaction {
    @Id
    private Long id;

    public static final double TRANSACTION_TAX_PERCENT = 0.0075d;
    public static final double BROKER_FEE_PERCENT = 0.0075d;
    public String typeName;
    public Date transactionDate;
    public int quantity;
    public double price;
    public String clientName;
    public TransactionTypeEnum transactionType;

    public double brokerFee;
    public double transactionTax;
    public double buyPrice;

    protected RichTransaction() {

    }

    public RichTransaction(ApiWalletTransaction walletTransaction) {
        this.id = walletTransaction.getTransactionID();

        this.transactionDate = walletTransaction.getTransactionDateTime();
        this.quantity = walletTransaction.getQuantity();
        this.typeName = walletTransaction.getTypeName();
        this.price = walletTransaction.getPrice();
        this.clientName = walletTransaction.getClientName();
        this.transactionType = walletTransaction.getTransactionType().equals("buy") ? TransactionTypeEnum.BUY : TransactionTypeEnum.SELL;

        this.brokerFee =0d;
        this.transactionTax=0d;
        this.buyPrice=0d;
    }

    public void setFeesAndBuyPrice(List<QueueEntry<ApiWalletTransaction>> buyTransactions) {
        this.brokerFee=0d;

        for (QueueEntry<ApiWalletTransaction> buyTransaction : buyTransactions) {
            double buyPrice = buyTransaction.amount * buyTransaction.object.getPrice();
            this.buyPrice+= buyPrice;
            this.brokerFee+= buyPrice* BROKER_FEE_PERCENT;
            this.transactionTax+=this.quantity*this.price* TRANSACTION_TAX_PERCENT;
        }
    }
}
