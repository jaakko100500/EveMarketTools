package EveApiPoller;

import Common.repositories.RichTransactionRepository;
import EveApiPoller.services.RichTransactionService;
import EveApiPoller.services.TotalWealthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.annotation.PostConstruct;
import java.io.IOException;

@SpringBootApplication
@ComponentScan(basePackageClasses = {EveApiPollerApplication.class, Common.ScanRoot.class})
@EnableJpaRepositories(basePackageClasses = {RichTransactionRepository.class, })
@EntityScan(basePackageClasses = {Common.domain.RichTransaction.class})
public class EveApiPollerApplication {
    private static final long EVE_API_POLL_INTERVAL = 60000L;
    private static Logger logger = LoggerFactory.getLogger(EveApiPollerApplication.class);

    @Autowired
    private RichTransactionService richTransactionService;

    @Autowired
    private TotalWealthService totalWealthService;


    public static void main(String[] args) {
        SpringApplication.run(EveApiPollerApplication.class, args);
    }

    @PostConstruct
    public void start() throws IOException {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(1);
        taskScheduler.initialize();

        logger.info("Starting to update");
        taskScheduler.scheduleWithFixedDelay(() -> {
            richTransactionService.createNewTransactions();
            totalWealthService.updateTotalWealth();
        }, EVE_API_POLL_INTERVAL);
    }
}
