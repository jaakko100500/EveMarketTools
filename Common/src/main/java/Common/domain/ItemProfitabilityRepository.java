package Common.domain;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface ItemProfitabilityRepository extends CrudRepository<RichTransaction, Long> {
    @Query("select " +
            "  rt.typeName," +
            "  sum(" +
            "     (rt.price * rt.quantity) - " +
            "      (rt.buyPrice + " +
            "      (rt.buyPrice*rt.brokerFee) + " +
            "      (rt.price*rt.quantity*rt.brokerFee) + " +
            "      (rt.price*rt.quantity*transactionTax))) as profitSum," +
            "  sum(rt.price * rt.quantity) as volumeSum " +
            " from RichTransaction rt " +
            " where " +
            "  rt.transactionType = 1 and " +
            "  rt.transactionDate between ?1 and ?2" +
            " group by rt.typeName" +
            " order by profitSum desc")
    public List<Object> findProfitsByTypeName(Date from, Date to);
}
