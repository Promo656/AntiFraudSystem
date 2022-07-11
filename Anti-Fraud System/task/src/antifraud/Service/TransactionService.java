package antifraud.Service;

import antifraud.Enums.TransactionStatus;
import antifraud.Models.*;
import antifraud.Repositories.IPsRepository;
import antifraud.Repositories.CardRepository;
import antifraud.Repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class TransactionService {
    @Autowired
    IPsRepository iPsRepository;
    @Autowired
    CardRepository cardRepository;
    @Autowired
    TransactionRepository transactionRepository;

    TransactionStatus result;
    List<String> info = new ArrayList<>();

    private void changeLimit(RequestTransaction transaction, String feedback) {
        String trResult = transaction.getResult().toString();
        Card card = cardRepository.findCardByNumber(transaction.getNumber());

        int increasedAllowed = (int) Math.ceil(0.8 * card.getMinLimit() + 0.2 * transaction.getAmount());
        int decreasedAllowed = (int) Math.ceil(0.8 * card.getMinLimit() - 0.2 * transaction.getAmount());
        int increasedManual = (int) Math.ceil(0.8 * card.getMaxLimit() + 0.2 * transaction.getAmount());
        int decreasedManual = (int) Math.ceil(0.8 * card.getMaxLimit() - 0.2 * transaction.getAmount());

        if (feedback.equals("MANUAL_PROCESSING") && trResult.equals("ALLOWED")) {
            card.setMinLimit(decreasedAllowed);
        } else if (feedback.equals("PROHIBITED") && trResult.equals("ALLOWED")) {
            card.setMinLimit(decreasedAllowed);
            card.setMaxLimit(decreasedManual);
        } else if (feedback.equals("ALLOWED") && trResult.equals("MANUAL_PROCESSING")) {
            card.setMinLimit(increasedAllowed);
        } else if (feedback.equals("PROHIBITED") && trResult.equals("MANUAL_PROCESSING")) {
            card.setMaxLimit(decreasedManual);
        } else if (feedback.equals("ALLOWED") && trResult.equals("PROHIBITED")) {
            card.setMinLimit(increasedAllowed);
            card.setMaxLimit(increasedManual);
        } else if (feedback.equals("MANUAL_PROCESSING") && trResult.equals("PROHIBITED")) {
            card.setMaxLimit(increasedManual);
        }

        cardRepository.save(card);
    }

    public ResponseEntity<RequestTransaction> addFeedback(Feedback feedback) {
        RequestTransaction transaction = transactionRepository.findTransactionByTransactionId(feedback.getTransactionId());
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
        changeLimit(transaction, feedback.getFeedback());
        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

    private void checkAmount(RequestTransaction transaction) {
        Card card = cardRepository.findCardByNumber(transaction.getNumber());
        if (transaction.getAmount() == null || transaction.getAmount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else if (transaction.getAmount() <= card.getMinLimit()) {
            result = TransactionStatus.ALLOWED;
            info.add("none");
        } else if (transaction.getAmount() <= card.getMaxLimit()) {
            result = TransactionStatus.MANUAL_PROCESSING;
            info.add("amount");
        } else {
            result = TransactionStatus.PROHIBITED;
            info.add("amount");
        }
    }

    private void checkTransactionRegion(RequestTransaction transaction) {
        Long transactionsWithDistinctRegionCount = transactionRepository
                .getTransactionsWithDistinctRegionCount(
                        transaction.getRegion(),
                        transaction.getNumber(),
                        transaction.getDate().minusHours(1),
                        transaction.getDate());

        if (transactionsWithDistinctRegionCount > 1) {
            info.add("region-correlation");
            info.remove("none");
            if (transactionsWithDistinctRegionCount == 2) {
                result = TransactionStatus.MANUAL_PROCESSING;
            } else {
                result = TransactionStatus.PROHIBITED;
            }
        }
    }

    private void checkTransactionIpCount(RequestTransaction transaction) {
        Long transactionsWithDistinctIpCount = transactionRepository
                .getTransactionsWithDistinctIpCount(
                        transaction.getIp(),
                        transaction.getNumber(),
                        transaction.getDate().minusHours(1),
                        transaction.getDate());

        if (transactionsWithDistinctIpCount > 1) {
            info.add("ip-correlation");
            info.remove("none");
            if (transactionsWithDistinctIpCount == 2) {
                result = TransactionStatus.MANUAL_PROCESSING;
            } else {
                result = TransactionStatus.PROHIBITED;
            }
        }
    }

    private void checkIpBlacklist(RequestTransaction transaction) {
        IP ip = iPsRepository.findByIp(transaction.getIp());
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
    }

    private void checkCardBlackList(RequestTransaction transaction) {
        Card card = cardRepository.findCardByNumberAndIsStolenTrue(transaction.getNumber());
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
    }

    public ResponseEntity<ResponseTransaction> transaction(RequestTransaction transaction) {
        info.clear();
        if (cardRepository.findCardByNumber(transaction.getNumber()) == null) {
            cardRepository.save(new Card(transaction.getNumber(), false));
        }
        checkAmount(transaction);
        checkTransactionRegion(transaction);
        checkTransactionIpCount(transaction);
        checkIpBlacklist(transaction);
        checkCardBlackList(transaction);

        transaction.setResult(result);
        transaction.setFeedback("");
        transactionRepository.save(transaction);
        Collections.sort(info);
        return new ResponseEntity<>(new ResponseTransaction(result, String.join(", ", info)), HttpStatus.OK);

    }

}
