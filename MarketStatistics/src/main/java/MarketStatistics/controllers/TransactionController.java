package MarketStatistics.controllers;

import Common.domain.RichTransaction;
import Common.domain.RichTransactionRepository;
import com.beimin.eveapi.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.*;
import java.util.Date;
import java.util.List;

@Controller
public class TransactionController {
    @Autowired
    private RichTransactionRepository richTransactionRepository;

    @RequestMapping(value = "transactions", params = {})
    public String getTransactions(
            Model model) throws ApiException {
        ZonedDateTime from = ZonedDateTime.now().withHour(0).withMinute(0).withSecond(0);
        ZonedDateTime to = ZonedDateTime.now().plusDays(1L);

        return getTransactionsByDate(from, to, model);
    }

    @RequestMapping(value = "transactions", params = {"from", "to"})
    public String getTransactionsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime to, Model model) throws ApiException {
        List<RichTransaction> richTransactions = richTransactionRepository.findByTransactionDateBetweenOrderByTransactionDateDesc(Date.from(from.toInstant()), Date.from(to.toInstant()));

        return getView(model, richTransactions);
    }

    @RequestMapping(value = "transactions", params = "typeName")
    public String getTransactionsByTypeName(@RequestParam String typeName, Model model) throws ApiException {
        List<RichTransaction> richTransactions = richTransactionRepository.findByTypeNameOrderByTransactionDateDesc(typeName);

        return getView(model, richTransactions);
    }

    @RequestMapping(value = "transactions", params = "clientName")
    public String getTransactionsByClientName(@RequestParam String clientName, Model model) throws ApiException {
        List<RichTransaction> richTransactions = richTransactionRepository.findByClientNameOrderByTransactionDateDesc(clientName);

        return getView(model, richTransactions);
    }

    private String getView(Model model, List<RichTransaction> richTransactions) {
        model.addAttribute("list", richTransactions);
        return "transactions";
    }
}
