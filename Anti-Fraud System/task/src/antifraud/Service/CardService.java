package antifraud.Service;

import antifraud.Models.Card;
import antifraud.Models.ResponseOperationStatus;
import antifraud.Repositories.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Service
public class CardService {
    @Autowired
    CardRepository cardRepository;

    @Autowired
    Utils utils;

    public ResponseEntity<Card> addCard(Card card) {
        if (!utils.checkCardNumber(card.getNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Card searchedCard;

        if (cardRepository.findCardByNumber(card.getNumber()) != null) {
            if (cardRepository.findCardByNumberAndIsStolenTrue(card.getNumber()) != null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT);
            } else {
                searchedCard = cardRepository.findCardByNumber(card.getNumber());
                searchedCard.setStolen(true);
            }
        } else {
            searchedCard = new Card(card.getNumber(), true);
        }

        cardRepository.save(searchedCard);
        return new ResponseEntity<>(searchedCard, HttpStatus.OK);
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
        List<Card> cards = cardRepository.findAllByIsStolenTrue();
        return new ResponseEntity<>(cards, HttpStatus.OK);
    }
}
