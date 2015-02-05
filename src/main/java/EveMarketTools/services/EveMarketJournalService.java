package EveMarketTools.services;

import com.beimin.eveapi.character.wallet.journal.WalletJournalParser;
import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.shared.wallet.journal.ApiJournalEntry;
import com.beimin.eveapi.shared.wallet.journal.WalletJournalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class EveMarketJournalService {
    @Autowired
    private EveApiAuthService eveApiAuthService;

    public Set<ApiJournalEntry> getApiJournalEntries() throws ApiException {
        WalletJournalParser walletJournalParser = WalletJournalParser.getInstance();
        WalletJournalResponse walletJournalResponse = walletJournalParser.getWalletJournalResponse(  eveApiAuthService.getApiAuthorization());
        return walletJournalResponse.getAll();
    }
}
