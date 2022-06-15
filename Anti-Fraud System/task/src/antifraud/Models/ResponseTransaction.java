package antifraud.Models;

import antifraud.Enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseTransaction {
    private Enum<TransactionStatus> result;
    private String info;
}
