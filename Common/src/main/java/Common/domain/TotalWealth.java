package Common.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(indexes = {
        @Index(columnList = "created")
})
public class TotalWealth {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    public double balance;
    public double buyOrdersValue;
    public double sellOrdersValue;
    public Date created;


    protected TotalWealth() {

    }

    public TotalWealth(double balance, double buyOrdersValue, double sellOrdersValue) {

        this.balance = balance;
        this.buyOrdersValue = buyOrdersValue;
        this.sellOrdersValue = sellOrdersValue;
    }

    @PrePersist
    protected void onCreate() {
        created = new Date();
    }

}
