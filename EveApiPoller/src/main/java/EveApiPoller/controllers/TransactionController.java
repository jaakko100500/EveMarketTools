package EveApiPoller.controllers;

import Common.domain.RichTransaction;
import Common.domain.RichTransactionRepository;
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
    public String greeting(Model model) throws ApiException {
        List<RichTransaction> richTransactions = Lists.newArrayList(richTransactionRepository.findAll());

        model.addAttribute("list", richTransactions);
        return "transactions";
    }
}
