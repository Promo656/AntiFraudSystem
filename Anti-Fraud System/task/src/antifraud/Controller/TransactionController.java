package antifraud.Controller;

import antifraud.Models.*;
import antifraud.Service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/antifraud")
public class TransactionController {
    @Autowired
    TransactionService transactionService;

    @GetMapping("/history")
    public ResponseEntity<List<RequestTransaction>> getAllTransactions() {
        return transactionService.getAllTransaction();
    }

    @GetMapping("/history/{number}")
    public ResponseEntity<List<RequestTransaction>> getTransaction(@PathVariable String number) {
        return transactionService.getTransaction(number);
    }

    @PostMapping("/transaction")
    public ResponseEntity<ResponseTransaction> transaction(@Valid @RequestBody RequestTransaction transaction) {
        return transactionService.transaction(transaction);
    }

    @PutMapping("/transaction")
    public ResponseEntity<RequestTransaction> addFeedback(@Valid @RequestBody Feedback feedback) {
        return transactionService.addFeedback(feedback);
    }

    @PostMapping("/suspicious-ip")
    public ResponseEntity<IP> addIP(@Valid @RequestBody IP ip) {
        return transactionService.addIP(ip);
    }

    @DeleteMapping("/suspicious-ip/{ip}")
    public ResponseEntity<ResponseOperationStatus> deleteIp(@Valid @PathVariable String ip) {
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
