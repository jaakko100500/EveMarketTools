package MarketStatistics.controllers;

import Common.repositories.ItemProfitabilityRepository;
import com.beimin.eveapi.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@Controller
public class ProfitabilityController {
    @Autowired
    private ItemProfitabilityRepository itemProfitabilityRepository;

    @RequestMapping(value = "profitability", params = {})
    public String getTransactions(@RequestParam Integer days,
                                  Model model) throws ApiException {
        ZonedDateTime to = ZonedDateTime.now();
        ZonedDateTime from = to.minusDays(days);

        List<Object> profitsByTypeName = itemProfitabilityRepository.findProfitsByTypeName(Date.from(from.toInstant()), Date.from(to.toInstant()));

        model.addAttribute("list", profitsByTypeName);
        return "profitability";

    }
}
