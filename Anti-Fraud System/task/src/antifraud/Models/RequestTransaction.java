package antifraud.Models;

import antifraud.Enums.TransactionStatus;
import antifraud.Enums.WorldRegion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity(name = "Transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestTransaction {
    @Id
    @GeneratedValue
    private int transactionId;
    @NotNull
    private Long amount;
    @NotEmpty
    private String ip;
    @NotEmpty
    private String number;
    @NotNull
    private WorldRegion region;
    @NotNull
    private LocalDateTime date;
    private TransactionStatus result;
    private String feedback;
}
