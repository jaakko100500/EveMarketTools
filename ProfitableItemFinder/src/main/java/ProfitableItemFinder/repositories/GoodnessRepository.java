package ProfitableItemFinder.repositories;

import ProfitableItemFinder.domain.Goodness;
import org.springframework.data.repository.CrudRepository;

public interface GoodnessRepository extends CrudRepository<Goodness, Long> {
}
