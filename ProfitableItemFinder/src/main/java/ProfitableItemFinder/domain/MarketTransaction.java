package ProfitableItemFinder.domain;

import Common.domain.TransactionTypeEnum;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(indexes = {
        @Index(columnList = "transactionType"),
        @Index(columnList = "typeId")
})
public class MarketTransaction {
    @Id
    private long orderID;

    private TransactionTypeEnum transactionType;
    private long minVolume;
    private double price;
    private long stationID;
    private int typeId;
    private long volEntered;
    private long volRemaining;

    protected MarketTransaction() {

    }

    public MarketTransaction(TransactionTypeEnum transactionType, long minVolume, long orderID, double price, long stationID, int typeId, long volEntered, long volRemaining) {
        this.transactionType = transactionType;
        this.minVolume = minVolume;
        this.orderID = orderID;
        this.price = price;
        this.stationID = stationID;
        this.typeId = typeId;
        this.volEntered = volEntered;
        this.volRemaining = volRemaining;
    }
}
