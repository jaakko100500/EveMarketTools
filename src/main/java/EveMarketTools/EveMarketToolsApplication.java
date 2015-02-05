package EveMarketTools;

import EveMarketTools.services.RichTransactionCreationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.io.IOException;

@SpringBootApplication
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
