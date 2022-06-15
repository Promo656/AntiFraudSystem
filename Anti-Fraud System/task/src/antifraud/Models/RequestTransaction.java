package antifraud.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

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
}
