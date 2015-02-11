package ProfitableItemFinder;

import Common.domain.RichTransaction;
import Common.repositories.RichTransactionRepository;
import ProfitableItemFinder.domain.Goodness;
import ProfitableItemFinder.domain.MarketTransaction;
import ProfitableItemFinder.repositories.GoodnessRepository;
import ProfitableItemFinder.services.MarketTransactionFetchingService;
import ProfitableItemFinder.services.ProfitableItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.annotation.PostConstruct;

@SpringBootApplication
@ComponentScan(basePackageClasses = {ProfitableItemFinderApplication.class, Common.ScanRoot.class})
@EnableJpaRepositories(basePackageClasses = {GoodnessRepository.class, RichTransactionRepository.class})
@EntityScan(basePackageClasses = {RichTransaction.class, Goodness.class})
public class ProfitableItemFinderApplication {
    @Autowired
    private ProfitableItemService profitableItemService;

    @Autowired
    private MarketTransactionFetchingService marketTransactionFetchingService;

    public static void main(String[] args) {
        SpringApplication.run(ProfitableItemFinderApplication.class, args);
    }

    @PostConstruct
    public void startItemFinding() {
//        marketTransactionFetchingService.fetchMarketTransactions();
//        profitableItemService.findProfitableItems();
    }
}
