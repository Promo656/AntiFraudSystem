package antifraud.Repositories;

import antifraud.Models.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StolenCardRepository extends JpaRepository<Card, Long> {
    Card findCardByNumber(String number);
}
