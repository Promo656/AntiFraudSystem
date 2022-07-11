package antifraud.Repositories;

import antifraud.Models.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
    Card findCardByNumber(String number);
}
