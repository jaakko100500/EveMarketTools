package Common.domain;

import com.beimin.eveapi.shared.wallet.transactions.ApiWalletTransaction;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(indexes = {
        @Index(columnList = "transactionDate"),
        @Index(columnList = "unprocessedQuantity"),
        @Index(columnList = "typeName"),
        @Index(columnList = "transactionType"),
        @Index(columnList = "clientName")
})
public class RichTransaction {
    @Id
    private Long id;
    public String typeName;
    public Date transactionDate;
    public int quantity;
    public double price;
    public String clientName;
    public TransactionTypeEnum transactionType;

    public double brokerFee;
    public double transactionTax;
    public double buyPrice;

    public int unprocessedQuantity;
    public String debug;

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

        this.unprocessedQuantity = quantity;
        this.debug = "";

        this.brokerFee = 0.0058d;
        this.transactionTax = 0.0075d;
        this.buyPrice = 0d;
    }
}
