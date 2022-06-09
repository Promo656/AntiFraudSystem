package antifraud.Service;

import antifraud.Enums.TransactionStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
public class TransactionService {

    public ResponseEntity<Map<String, TransactionStatus>> transaction(Long amount) {
        if (amount == null || amount <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else if (amount <= 200) {
            return new ResponseEntity<>(Map.of("result", TransactionStatus.ALLOWED), HttpStatus.OK);
        } else if (amount <= 1500) {
            return new ResponseEntity<>(Map.of("result", TransactionStatus.MANUAL_PROCESSING), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Map.of("result", TransactionStatus.PROHIBITED), HttpStatus.OK);
        }
    }
}
