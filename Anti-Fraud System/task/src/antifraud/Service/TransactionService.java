package antifraud.Service;

import antifraud.Enums.TransactionStatus;
import antifraud.Models.*;
import antifraud.Repositories.IPsRepository;
import antifraud.Repositories.StolenCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TransactionService {
    @Autowired
    IPsRepository iPsRepository;
    @Autowired
    StolenCardRepository stolenCardRepository;

    private IP findIp(String ip) {
        return iPsRepository.findByIp(ip);
    }

    private List<IP> getAllIP() {
        return iPsRepository.findAll();
    }

    private void saveIp(IP ip) {
        iPsRepository.save(ip);
    }

    private void removeIp(IP ip) {
        iPsRepository.delete(ip);
    }

    private Card findCard(String number) {
        return stolenCardRepository.findCardByNumber(number);
    }

    private List<Card> getAllCards() {
        return stolenCardRepository.findAll();
    }

    private void saveCard(Card card) {
        stolenCardRepository.save(card);
    }

    private void removeCard(Card card) {
        stolenCardRepository.delete(card);
    }

    public ResponseEntity<ResponseTransaction> transaction(RequestTransaction transaction) {
        Enum<TransactionStatus> result;
        List<String> info = new ArrayList<>();

        if ( transaction.getAmount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else if (transaction.getAmount() <= 200) {
            result = TransactionStatus.ALLOWED;
            info.add("none");
        } else if (transaction.getAmount() <= 1500) {
            result = TransactionStatus.MANUAL_PROCESSING;
            info.add("amount");
        } else {
            result = TransactionStatus.PROHIBITED;
            info.add("amount");
        }

        IP ip = findIp(transaction.getIp());
        if (ip != null) {
            info.add("ip");
            info.remove("none");
            result = TransactionStatus.PROHIBITED;
        }

        Card card = findCard(transaction.getNumber());
        if (card != null) {
            info.add("card-number");
            info.remove("none");
            result = TransactionStatus.PROHIBITED;
        }
        return new ResponseEntity<>(new ResponseTransaction(result, info.toString()), HttpStatus.OK);

    }

    public ResponseEntity<IP> addIP(IP ip) {
        IP searchedIp = findIp(ip.getIp());
        if (searchedIp == null) {
            saveIp(ip);
            return new ResponseEntity<>(ip, HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    public ResponseEntity<ResponseOperationStatus> deleteIp(String ip) {
        IP searchedIp = findIp(ip);
        if (searchedIp != null) {
            String msg = String.format("IP %s successfully removed!", ip);
            removeIp(searchedIp);
            return new ResponseEntity<>(new ResponseOperationStatus(msg), HttpStatus.OK);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<List<IP>> getAllIPs() {
        List<IP> ips = getAllIP();
        return new ResponseEntity<>(ips, HttpStatus.OK);
    }

    public ResponseEntity<Card> addCard(Card card) {
        Card searchedCard = findCard(card.getNumber());
        if (searchedCard == null) {
            saveCard(card);
            return new ResponseEntity<>(card, HttpStatus.OK);
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT);
    }

    public ResponseEntity<ResponseOperationStatus> deleteCard(String number) {
        Card searchedCard = findCard(number);
        if (searchedCard != null) {
            String msg = String.format("Card %s successfully removed!", number);
            removeCard(searchedCard);
            return new ResponseEntity<>(new ResponseOperationStatus(msg), HttpStatus.OK);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<List<Card>> getAllCard() {
        List<Card> cards = getAllCards();
        return new ResponseEntity<>(cards, HttpStatus.OK);
    }
}
