package antifraud.Repositories;

import antifraud.Models.RequestTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<RequestTransaction, Long> {
}
