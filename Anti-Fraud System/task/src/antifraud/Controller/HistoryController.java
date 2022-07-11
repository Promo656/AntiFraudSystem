package antifraud.Controller;

import antifraud.Models.RequestTransaction;
import antifraud.Service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/antifraud")
public class HistoryController {

    @Autowired
    HistoryService historyService;

    @GetMapping("/history")
    public ResponseEntity<List<RequestTransaction>> getAllTransactions() {
        return historyService.getAllTransaction();
    }

    @GetMapping("/history/{number}")
    public ResponseEntity<List<RequestTransaction>> getTransaction(@PathVariable String number) {
        return historyService.getTransaction(number);
    }
}
