package ProfitableItemFinder.repositories;

import ProfitableItemFinder.domain.MarketVolume;
import org.springframework.data.repository.CrudRepository;

public interface MarketVolumeRepository extends CrudRepository<MarketVolume, Long> {
}
