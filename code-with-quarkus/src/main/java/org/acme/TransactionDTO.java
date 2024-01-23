package main.java.org.acme;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class TransactionDTO {
    private UUID transactionId;
    private String transactionType;
    private String studentName;
    private String studentId;
    private String supervisorName;
    private String supervisorId;
    private String receivingSupervisorId;
    private String receivingSupervisorName;
    private String assetName;
    private String assetModel;
    private String assetId;
    private String date;
    private String userId;

    public TransactionDTO(String assetName, String assetId, String studentName, String supervisorName, String assetModel, UUID transactionId) {
        this.assetId = assetId;
        this.studentName = studentName;
        this.supervisorName = supervisorName;
        this.assetModel = assetModel;
        this.transactionId = transactionId;
    }
    public TransactionDTO(String assetName, String studentName, String supervisorName, String studentId, String supervisorId, String assetModel) {
        this.assetName = assetName;
        this.studentId = studentId;
        this.studentName = studentName;
        this.supervisorName = supervisorName;
        this.assetModel = assetModel;
        this.supervisorId = supervisorId;
    }

}
