package antifraud.Service;

import antifraud.Models.Card;
import antifraud.Models.ResponseOperationStatus;
import antifraud.Repositories.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class StolenCardService {
    @Autowired
    CardRepository cardRepository;

    @Autowired
    Utils utils;

    public ResponseEntity<Card> addCard(Card card) {
        if (!utils.checkCardNumber(card.getNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Card searchedCard = cardRepository.findCardByNumber(card.getNumber());
        if (searchedCard == null) {
            cardRepository.save(new Card(card.getNumber(), true));
            return new ResponseEntity<>(card, HttpStatus.OK);
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT);
    }

    public ResponseEntity<ResponseOperationStatus> deleteCard(String number) {
        if (!utils.checkCardNumber(number)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Card searchedCard = cardRepository.findCardByNumber(number);
        if (searchedCard != null) {
            String msg = String.format("Card %s successfully removed!", number);
            cardRepository.delete(searchedCard);
            return new ResponseEntity<>(new ResponseOperationStatus(msg), HttpStatus.OK);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<List<Card>> getAllCard() {
        List<Card> cards = cardRepository.findAll();
        return new ResponseEntity<>(cards, HttpStatus.OK);
    }
}
