package antifraud.Repositories;

import antifraud.Models.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    Card findCardByNumber(String number);

    List<Card> findAllByIsStolenTrue();

    Card findCardByNumberAndIsStolenTrue(String number);
}
