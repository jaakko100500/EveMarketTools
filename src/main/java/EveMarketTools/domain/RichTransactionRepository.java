package EveMarketTools.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

import java.util.Date;
import java.util.List;

public interface RichTransactionRepository extends CrudRepository<RichTransaction, Long> {
    @Query("select max(richTransaction.transactionDate) from RichTransaction richTransaction")
    public Date findLastTransactionDate();

    @Query("select richTransaction from RichTransaction richTransaction where richTransaction.unprocessedQuantity > 0 order by transactionDate asc")
    public List<RichTransaction> findUnprocessedOrders();
}
