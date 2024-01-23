package main.java.org.acme.Repository;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.inject.Inject;
import main.java.org.acme.Entity.Transactions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.transaction.Transactional;


@ApplicationScoped
public class TransactionRepository implements PanacheRepository<Transactions> {

    @Inject
    UserRepository userRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionRepository.class);

    public Transactions findTransactionById(UUID transactionId) {
        if (transactionId != null ){
            return find("transactionId", transactionId).firstResult();
        }else{
            throw new IllegalArgumentException("transactionId can't be null.");
        }

    }

    @Transactional
    public List<Transactions> findById(String id) throws IllegalArgumentException {
        if (Objects.equals(userRepository.findRoleById(id), "Student")) {
            List<Transactions> transactions = find("SELECT t.transactionId, t.transactionType, t.date, t.assetId, a.name, a.model, concat_ws(' ', s.firstName, s.lastName) AS studentName, concat_ws(' ', su.firstName, su.lastName) AS supervisorName " +
                    "FROM Transactions t " +
                    "JOIN Asset a ON t.assetId = a.assetId " +
                    "JOIN User s ON t.studentId = s.id " +
                    "JOIN User su ON t.supervisorId = su.id " +
                    "WHERE t.studentId = ?1", id).list();
            return transactions;
        }else if (Objects.equals(userRepository.findRoleById(id), "Supervisor")) {
            return allUsers();
        }else{
            throw new IllegalArgumentException("UserId is invalid.");
        }
    }


    public List<Transactions> allUsers() {
        try{
            LOGGER.info("Entering allUsers method in TransactionRepository");

            List<Transactions> transactions = find("SELECT t.transactionId, t.transactionType, t.date, t.assetId, a.name, a.model, concat_ws(' ', s.firstName, s.lastName) AS studentName, concat_ws(' ', su.firstName, su.lastName) AS supervisorName, COALESCE(concat_ws(' ', rsu.firstName, rsu.lastName), 'N/A') AS receivingSupervisorName " +
                    "FROM Transactions t " +
                    "JOIN Asset a ON t.assetId = a.assetId " +
                    "JOIN User s ON t.studentId = s.id " +
                    "JOIN User su ON t.supervisorId = su.id " +
                    "LEFT JOIN User rsu ON t.receivingSupervisorId = rsu.id " +
                    "WHERE t.transactionType = 'RETURN' OR t.receivingSupervisorId IS NULL").list();

            LOGGER.info("Exiting allUsers method in TransactionRepository");

            return transactions;

        }catch (Exception e) {
            LOGGER.error("Error in allUsers method", e);
            throw e;
        }
    }

    public List<Transactions> findByTransactionId(UUID id) {
        List<Transactions> transactions = find("SELECT t.transactionId, t.transactionType, t.date, t.assetId, a.name, a.model, concat_ws(' ', s.firstName, s.lastName) AS studentName, concat_ws(' ', su.firstName, su.lastName) AS supervisorName " +
                "FROM Transactions t " +
                "JOIN Asset a ON t.assetId = a.assetId " +
                "JOIN User s ON t.studentId = s.id " +
                "JOIN User su ON t.supervisorId = su.id " +
                "WHERE t.transactionId = ?1", id).list();
        return transactions;
    }


}
