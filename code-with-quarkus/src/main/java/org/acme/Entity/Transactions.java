package main.java.org.acme.Entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "transactions", schema = "asset_loan")
public class Transactions  {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID transactionId;
    private String transactionType;
    private String studentId;
    private String supervisorId;
    private String receivingSupervisorId;
    private String assetId;
    private String date;

    public Transactions(String assetId, String studentId, String supervisorId, String receivingSupervisorId, UUID transactionId) {
        this.transactionId = transactionId;
        this.studentId = studentId;
        this.supervisorId = supervisorId;
        this.receivingSupervisorId = receivingSupervisorId;
        this.assetId = assetId;
    }
}