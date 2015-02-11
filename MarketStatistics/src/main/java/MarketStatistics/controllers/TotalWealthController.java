package MarketStatistics.controllers;

import Common.domain.TotalWealth;
import Common.repositories.ItemProfitabilityRepository;
import Common.repositories.TotalWealthRepository;
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
public class TotalWealthController {
    @Autowired
    private TotalWealthRepository totalWealthRepository;

    @RequestMapping(value = "totalWealth", params = {})
    public String getTotalWealth(Model model) throws ApiException {
        List<TotalWealth> list = totalWealthRepository.findAllOrderByCreatedDate();

        model.addAttribute("list", list);
        return "totalWealth";

    }
}
