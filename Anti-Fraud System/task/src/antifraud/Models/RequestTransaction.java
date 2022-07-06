package antifraud.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity(name = "Transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestTransaction {
    @Id
    @GeneratedValue
    private int id;
    @NotNull
    private Long amount;
    @NotEmpty
    private String ip;
    @NotEmpty
    private String number;
    @NotEmpty
    private String region;
    @NotEmpty
    private String date;
}
