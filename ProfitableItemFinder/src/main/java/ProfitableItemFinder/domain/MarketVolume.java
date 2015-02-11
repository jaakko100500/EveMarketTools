package ProfitableItemFinder.domain;

import javax.persistence.*;

@Entity
@Table(indexes = {
        @Index(columnList = "typeId")
})
public class MarketVolume {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private double avgPrice;
    private double highPrice;
    private double lowPrice;
    private long orders;
    private long regionID;
    private int typeID;
    private long volume;

    protected MarketVolume() {

    }

    public MarketVolume(double avgPrice, double highPrice, double lowPrice, long orders, long regionID, int typeID, long volume) {
        this.avgPrice = avgPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.orders = orders;
        this.regionID = regionID;
        this.typeID = typeID;
        this.volume = volume;
    }
}
