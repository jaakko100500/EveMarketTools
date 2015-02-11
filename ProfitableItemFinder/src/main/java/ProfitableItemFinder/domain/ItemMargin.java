package ProfitableItemFinder.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class ItemMargin {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    private Date created;
    private double averageVolume;
    private double buy;
    private Integer typeID;
    private double sell;

    protected ItemMargin() {

    }

    public ItemMargin(Integer typeID, Double sell, Double buy, Double averageVolume) {
        this.typeID = typeID;
        this.sell = sell;
        this.buy = buy;
        this.averageVolume = averageVolume;
        this.created = new Date();
    }
}
