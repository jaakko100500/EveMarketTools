package EveApiPoller;

import EveApiPoller.services.RichTransactionCreationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.annotation.PostConstruct;
import java.io.IOException;

@SpringBootApplication
@ComponentScan(basePackageClasses = {EveMarketToolsApplication.class, Common.domain.RichTransaction.class })
@EnableJpaRepositories(basePackageClasses = {Common.domain.RichTransactionRepository.class})
@EntityScan(basePackageClasses = {Common.domain.RichTransaction.class})
public class EveMarketToolsApplication {
    @Autowired
    private RichTransactionCreationService richTransactionCreationService;

    public static void main(String[] args) {
        SpringApplication.run(EveMarketToolsApplication.class, args);
    }

    @PostConstruct
    public void start() throws IOException {
        richTransactionCreationService.startCreatingTransactions();
    }
}
