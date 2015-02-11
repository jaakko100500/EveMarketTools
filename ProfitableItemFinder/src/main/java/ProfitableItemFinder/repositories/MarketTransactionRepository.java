package ProfitableItemFinder.repositories;

import ProfitableItemFinder.domain.MarketTransaction;
import org.springframework.data.repository.CrudRepository;

public interface MarketTransactionRepository extends CrudRepository<MarketTransaction, Long> {
}
