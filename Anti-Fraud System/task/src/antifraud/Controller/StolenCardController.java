package antifraud.Controller;

import antifraud.Models.Card;
import antifraud.Models.ResponseOperationStatus;
import antifraud.Service.StolenCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/antifraud")
public class StolenCardController {

    @Autowired
    StolenCardService stolenCardService;

    @PostMapping("/stolencard")
    public ResponseEntity<Card> addCard(@Valid @RequestBody Card card) {
        return stolenCardService.addCard(card);
    }

    @DeleteMapping("/stolencard/{number}")
    public ResponseEntity<ResponseOperationStatus> deleteCard(@PathVariable String number) {
        return stolenCardService.deleteCard(number);
    }

    @GetMapping("/stolencard")
    public ResponseEntity<List<Card>> getAllCard() {
        return stolenCardService.getAllCard();
    }
}
