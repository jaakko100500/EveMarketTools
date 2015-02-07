package MarketStatistics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackageClasses = {MarketStatisticsApplication.class, Common.domain.RichTransaction.class })
@EnableJpaRepositories(basePackageClasses = {Common.domain.RichTransactionRepository.class})
@EntityScan(basePackageClasses = {Common.domain.RichTransaction.class})
public class MarketStatisticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketStatisticsApplication.class, args);
    }
}
