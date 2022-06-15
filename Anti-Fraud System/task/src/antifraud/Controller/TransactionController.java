package antifraud.Controller;

import antifraud.Enums.TransactionStatus;
import antifraud.Models.*;
import antifraud.Service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/antifraud")
public class TransactionController {
    @Autowired
    TransactionService transactionService;

    @PostMapping("/transaction")
    public ResponseEntity<ResponseTransaction> transaction(@RequestBody RequestTransaction transaction) {
        return transactionService.transaction(transaction);
    }

    @PostMapping("/suspicious-ip")
    public ResponseEntity<IP> addIP(@Valid @RequestBody IP ip) {
        return transactionService.addIP(ip);
    }

    @DeleteMapping("/suspicious-ip/{ip}")
    public ResponseEntity<ResponseOperationStatus> deleteIp(@PathVariable String ip) {
        return transactionService.deleteIp(ip);
    }

    @GetMapping("/suspicious-ip")
    public ResponseEntity<List<IP>> getAllIPs() {
        return transactionService.getAllIPs();
    }

    @PostMapping("/stolencard")
    public ResponseEntity<Card> addCard(@Valid @RequestBody Card card) {
        return transactionService.addCard(card);
    }

    @DeleteMapping("/stolencard/{number}")
    public ResponseEntity<ResponseOperationStatus> deleteCard(@PathVariable String number) {
        return transactionService.deleteCard(number);
    }

    @GetMapping("/stolencard")
    public ResponseEntity<List<Card>> getAllCard() {
        return transactionService.getAllCard();
    }
}
