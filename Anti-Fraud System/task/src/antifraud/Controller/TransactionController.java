package antifraud.Controller;

import antifraud.Models.Feedback;
import antifraud.Models.RequestTransaction;
import antifraud.Models.ResponseTransaction;
import antifraud.Service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/antifraud")
public class TransactionController {
    @Autowired
    TransactionService transactionService;

    @PostMapping("/transaction")
    public ResponseEntity<ResponseTransaction> transaction(@Valid @RequestBody RequestTransaction transaction) {
        return transactionService.transaction(transaction);
    }

    @PutMapping("/transaction")
    public ResponseEntity<RequestTransaction> addFeedback(@Valid @RequestBody Feedback feedback) {
        return transactionService.addFeedback(feedback);
    }

}
