package EveMarketTools.controllers;

import EveMarketTools.domain.RichTransaction;
import EveMarketTools.domain.RichTransactionRepository;
import com.beimin.eveapi.exception.ApiException;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
public class TransactionController {
    @Autowired
    private RichTransactionRepository richTransactionRepository;

    @RequestMapping("transactions")
    public String greeting(@RequestParam(value = "name", required = false, defaultValue = "World") String name, Model model) throws ApiException {
        List<RichTransaction> richTransactions = Lists.newArrayList(richTransactionRepository.findAll());

        model.addAttribute("list", richTransactions);
        return "transactions";
    }
}
