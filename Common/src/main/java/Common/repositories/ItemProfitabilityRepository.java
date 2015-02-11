package Common.repositories;

import Common.domain.RichTransaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ItemProfitabilityRepository extends CrudRepository<RichTransaction, Long> {
    @Query(value = "SELECT \n" +
            "    type_id,\n" +
            "    (SELECT \n" +
            "            MIN(market_transaction_sell.price)\n" +
            "        FROM\n" +
            "            market_transaction AS market_transaction_sell\n" +
            "        WHERE\n" +
            "            market_transaction_sell.transaction_type = 1\n" +
            "                AND market_transaction_sell.stationid = 60003760\n" +
            "                AND market_transaction_sell.type_id = market_transaction.type_id) AS sell,\n" +
            "    (SELECT \n" +
            "            MAX(market_transaction_buy.price)\n" +
            "        FROM\n" +
            "            market_transaction AS market_transaction_buy\n" +
            "        WHERE\n" +
            "            market_transaction_buy.transaction_type = 0\n" +
            "                AND market_transaction_buy.stationid = 60003760\n" +
            "                AND market_transaction_buy.type_id = market_transaction.type_id) AS buy,\n" +
            "    (SELECT \n" +
            "            AVG(volume)\n" +
            "        FROM\n" +
            "            market_volume\n" +
            "        WHERE\n" +
            "            typeid = market_transaction.type_id\n" +
            "        GROUP BY typeid\n" +
            "        ORDER BY typeid) as volume\n" +
            "FROM\n" +
            "    market_transaction\n" +
            " where market_transaction.type_id = ?1 " +
            "GROUP BY type_id", nativeQuery = true)
    public Object[] findProfitValues(Integer typeID);

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
