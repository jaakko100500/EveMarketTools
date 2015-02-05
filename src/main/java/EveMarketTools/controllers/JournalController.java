package EveMarketTools.controllers;

import EveMarketTools.services.EveMarketJournalService;
import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.shared.wallet.journal.ApiJournalEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

@Controller
public class JournalController {
    @Autowired
    private EveMarketJournalService eveMarketJournalService;

    @RequestMapping("journal")
    public String greeting(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) throws ApiException {
        Set<ApiJournalEntry> apiJournalEntries = eveMarketJournalService.getApiJournalEntries();

        model.addAttribute("apiJournalEntries", apiJournalEntries);
        return "greeting";
    }
}
