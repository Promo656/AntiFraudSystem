package antifraud.Service;

import antifraud.Models.RequestTransaction;
import antifraud.Repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class HistoryService {
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    Utils utils;

    public ResponseEntity<List<RequestTransaction>> getAllTransaction() {
        List<RequestTransaction> transactions = transactionRepository.findAll();
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    public ResponseEntity<List<RequestTransaction>> getTransaction(String number) {
        if (!utils.checkCardNumber(number)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        List<RequestTransaction> transaction = transactionRepository.findTransactionByNumber(number);
        if (transaction.size() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }
}
