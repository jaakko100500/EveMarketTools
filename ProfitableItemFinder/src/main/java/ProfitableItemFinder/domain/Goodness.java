package ProfitableItemFinder.domain;

import javax.persistence.*;

@Entity
@Table(indexes = {
        @Index(columnList = "volume"),
        @Index(columnList = "goodnessValue"),
        @Index(columnList = "marketValue"),
        @Index(columnList = "typeId")
})
public class Goodness {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    public final double sell;
    public final double buy;
    public final double volume;
    public final int typeID;
    public final String typeName;

    public double margin;
    public double marginPercentage;

    public double marketValue;
    public double feePercentage = 0.0058 * 2 + 0.0075;

    public double goodnessValue;

    public Goodness(double sell, double buy, double volume, Integer typeID, String nameByTypeID) {
        this.sell = sell;
        this.buy = buy;
        this.volume = volume;
        this.typeID = typeID;
        this.typeName = nameByTypeID;

        this.margin = sell - buy;
        this.marginPercentage = margin / sell;

        this.marketValue = margin * volume;
        this.feePercentage = 0.0058 * 2 + 0.0075;

        this.goodnessValue = marketValue * (marginPercentage - feePercentage);
    }
}
