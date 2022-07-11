package antifraud.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Card {
    @Id
    @GeneratedValue
    private int id;
    @NotEmpty
    @Pattern(regexp = "\\d{16}")
    private String number;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean isStolen;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int minLimit = 200;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int maxLimit = 1500;

    public Card(String number, boolean isStolen) {
        this.isStolen = isStolen;
        this.number = number;
    }
}
