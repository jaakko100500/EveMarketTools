package EveMarketTools.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

import java.util.Date;

public interface RichTransactionRepository extends CrudRepository<RichTransaction, Long> {
    @Query("select max(richTransaction.transactionDate) from RichTransaction richTransaction")
        public Date findLastTransactionDate();
}
