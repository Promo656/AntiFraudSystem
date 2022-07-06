package antifraud.Repositories;

import antifraud.Enums.WorldRegion;
import antifraud.Models.RequestTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<RequestTransaction, Long> {
    List<RequestTransaction> findTransactionByIp(String ip);

    List<RequestTransaction> findTransactionByRegion(String region);

    @Query("SELECT COUNT(DISTINCT t.region) FROM Transaction t WHERE t.region <> ?1 AND t.number = ?2 AND t.date BETWEEN ?3 AND ?4")
    Long getTransactionsWithDistinctRegionCount(
            WorldRegion region,
            String number,
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("SELECT COUNT(DISTINCT t.ip) FROM Transaction t WHERE t.ip <> ?1 AND t.number = ?2 AND t.date BETWEEN ?3 AND ?4")
    Long getTransactionsWithDistinctIpCount(String ip,
                                            String number,
                                            LocalDateTime start,
                                            LocalDateTime end);
}
