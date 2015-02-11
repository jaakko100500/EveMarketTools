package MarketStatistics;

import Common.repositories.RichTransactionRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackageClasses = {MarketStatisticsApplication.class, Common.ScanRoot.class})
@EnableJpaRepositories(basePackageClasses = {RichTransactionRepository.class})
@EntityScan(basePackageClasses = {Common.domain.RichTransaction.class})
public class MarketStatisticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketStatisticsApplication.class, args);
    }
}
