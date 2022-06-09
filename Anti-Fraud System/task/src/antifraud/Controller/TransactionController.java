package antifraud.Controller;

import antifraud.Enums.TransactionStatus;
import antifraud.Service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/antifraud")
public class TransactionController {
    @Autowired
    TransactionService transaction;

    @PostMapping("/transaction")
    public ResponseEntity<Map<String, TransactionStatus>> transaction(@RequestBody Map.Entry<String, Long> amount) {
        return transaction.transaction(amount.getValue());
    }
}
