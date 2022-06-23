package antifraud.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestTransaction {
    @NotEmpty
    private Long amount;
    @NotEmpty
    private String ip;
    @NotEmpty
    private String number;
    @NotEmpty
    private String region;
    @NotEmpty
    private LocalDate date;
}
