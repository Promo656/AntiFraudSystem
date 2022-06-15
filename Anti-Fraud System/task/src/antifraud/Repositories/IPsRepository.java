package antifraud.Repositories;

import antifraud.Models.IP;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPsRepository extends JpaRepository<IP, Long> {
    IP findByIp(String ip);
}
