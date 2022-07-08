package antifraud.Service;

import antifraud.Enums.TransactionStatus;
import antifraud.Models.*;
import antifraud.Repositories.IPsRepository;
import antifraud.Repositories.StolenCardRepository;
import antifraud.Repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TransactionService {
    @Autowired
    IPsRepository iPsRepository;
    @Autowired
    StolenCardRepository stolenCardRepository;

    @Autowired
    TransactionRepository transactionRepository;

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

    private List<RequestTransaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    private List<RequestTransaction> getTransactionByNumber(String number) {
        return transactionRepository.findTransactionByNumber(number);
    }

    private RequestTransaction getTransactionByTransactionId(int id) {
        return transactionRepository.findTransactionByTransactionId(id);
    }

    private boolean checkCardNumber(String value) {
        int sum = Character.getNumericValue(value.charAt(value.length() - 1));
        int parity = value.length() % 2;
        for (int i = value.length() - 2; i >= 0; i--) {
            int summand = Character.getNumericValue(value.charAt(i));
            if (i % 2 == parity) {
                int product = summand * 2;
                summand = (product > 9) ? (product - 9) : product;
            }
            sum += summand;
        }
        return (sum % 10) == 0;
    }

    public ResponseEntity<List<RequestTransaction>> getAllTransaction() {
        List<RequestTransaction> transactions = getAllTransactions();
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    public ResponseEntity<List<RequestTransaction>> getTransaction(String number) {
        if (!checkCardNumber(number)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        List<RequestTransaction> transaction = getTransactionByNumber(number);
        if (transaction.size() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

    public ResponseEntity<RequestTransaction> addFeedback(Feedback feedback) {
        RequestTransaction transaction = getTransactionByTransactionId(feedback.getTransactionId());
        if (transaction == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        var isWrongFormat = Arrays.stream(TransactionStatus.values()).anyMatch(el -> el.toString().equals(feedback.getFeedback()));

        if (!isWrongFormat) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (transaction.getResult().toString().equals(feedback.getFeedback())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (!transaction.getFeedback().isBlank()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }


        transaction.setFeedback(feedback.getFeedback());
        transactionRepository.save(transaction);
        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

    public ResponseEntity<ResponseTransaction> transaction(RequestTransaction transaction) {
        TransactionStatus result;
        List<String> info = new ArrayList<>();

        if (transaction.getAmount() == null || transaction.getAmount() <= 0) {
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

        Long transactionsWithDistinctRegionCount = transactionRepository.getTransactionsWithDistinctRegionCount(transaction.getRegion(), transaction.getNumber(), transaction.getDate().minusHours(1), transaction.getDate());

        if (transactionsWithDistinctRegionCount > 1) {
            info.add("region-correlation");
            info.remove("none");
            if (transactionsWithDistinctRegionCount == 2) {
                result = TransactionStatus.MANUAL_PROCESSING;
            } else {
                result = TransactionStatus.PROHIBITED;
            }
        }

        Long transactionsWithDistinctIpCount = transactionRepository.getTransactionsWithDistinctIpCount(transaction.getIp(), transaction.getNumber(), transaction.getDate().minusHours(1), transaction.getDate());

        if (transactionsWithDistinctIpCount > 1) {
            info.add("ip-correlation");
            info.remove("none");
            if (transactionsWithDistinctIpCount == 2) {
                result = TransactionStatus.MANUAL_PROCESSING;
            } else {
                result = TransactionStatus.PROHIBITED;
            }
        }

        IP ip = findIp(transaction.getIp());
        if (ip != null) {
            info.add("ip");
            info.remove("none");
            info.remove("amount");
            if (transaction.getAmount() >= 1500) {
                result = TransactionStatus.MANUAL_PROCESSING;
                info.add("amount");
            }
            result = TransactionStatus.PROHIBITED;
        }

        Card card = findCard(transaction.getNumber());
        if (card != null) {
            info.add("card-number");
            info.remove("none");
            info.remove("amount");
            if (transaction.getAmount() >= 1500) {
                result = TransactionStatus.MANUAL_PROCESSING;
                info.add("amount");
            }
            result = TransactionStatus.PROHIBITED;
        }

        transaction.setResult(result);
        transaction.setFeedback("");
        transactionRepository.save(transaction);
        Collections.sort(info);
        return new ResponseEntity<>(new ResponseTransaction(result, String.join(", ", info)), HttpStatus.OK);

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
        if (!ip.matches(Regex.IP_REGEX)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
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
        if (!checkCardNumber(card.getNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Card searchedCard = findCard(card.getNumber());
        if (searchedCard == null) {
            saveCard(card);
            return new ResponseEntity<>(card, HttpStatus.OK);
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT);
    }

    public ResponseEntity<ResponseOperationStatus> deleteCard(String number) {
        if (!checkCardNumber(number)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
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
