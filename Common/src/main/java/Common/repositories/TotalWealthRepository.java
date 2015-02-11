package Common.repositories;

import Common.domain.RichTransaction;
import Common.domain.TotalWealth;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TotalWealthRepository extends CrudRepository<TotalWealth, Long> {
    @Query("select totalWealth from TotalWealth totalWealth order by totalWealth.created desc")
    List<TotalWealth> findAllOrderByCreatedDate();
}
