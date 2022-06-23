package antifraud.Models;

import lombok.Data;

@Data
public class DeleteEntity {
    private String username;
    private String status = "Deleted successfully!";

    public DeleteEntity(String username) {
        this.username = username;
    }
}
