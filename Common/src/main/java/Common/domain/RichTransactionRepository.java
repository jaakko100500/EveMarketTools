package Common.domain;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface RichTransactionRepository extends CrudRepository<RichTransaction, Long> {
    @Query("select max(richTransaction.transactionDate) from RichTransaction richTransaction")
    public Date findLastTransactionDate();

    @Query("select richTransaction from RichTransaction richTransaction where richTransaction.unprocessedQuantity > 0 order by transactionDate asc")
    public List<RichTransaction> findUnprocessedOrders();

    @Query("select richTransaction from RichTransaction richTransaction order by transactionDate desc")
    List<RichTransaction> findAllOrderByTransactionDateDesc();

    List<RichTransaction> findByClientNameOrderByTransactionDateDesc(String clientName);
    List<RichTransaction> findByTypeNameOrderByTransactionDateDesc(String typeName);
    List<RichTransaction> findByTransactionDateBetweenOrderByTransactionDateDesc(Date date, Date localDate);
}
