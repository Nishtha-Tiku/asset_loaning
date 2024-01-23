package main.java.org.acme.Services;

import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import main.java.org.acme.Entity.Asset;
import main.java.org.acme.Entity.Transactions;
import main.java.org.acme.Entity.User;
import main.java.org.acme.Repository.AssetRepository;
import main.java.org.acme.Repository.TransactionRepository;
import main.java.org.acme.Repository.UserRepository;

import java.text.ParseException;
import java.util.*;


@ApplicationScoped
public class TransactionServices {
    @Inject
    TransactionRepository transactionRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    AssetRepository assetRepository;

    public List<User> getAllUsers() {
        return userRepository.allUsers();
    }

    public List<Asset> getAllAssets() {
        return assetRepository.allAssets();
    }

    public List<Transactions> getTransactionsByUser(String userId) throws IllegalArgumentException{
        return transactionRepository.findById(userId);
    }

    public List<Transactions> getTransactionsById(UUID id) {
        return transactionRepository.findByTransactionId(id);
    }


    public String getRole(String id){
        return userRepository.findRoleById(id);
    }
    /*public List<Transactions> getAllTransactions() {
        return transactionRepository.allUsers();
    }*/

    public void createLoanTransaction(String supervisorName, String studentName, String assetName, String issueDate) throws IllegalArgumentException {

        try{
            String supervisorId = userRepository.findUserIdsByFirstName(supervisorName);
            String studentId = userRepository.findUserIdsByFirstName(studentName);
            String assetId = assetRepository.findAssetIdByName(assetName);

            Transactions transaction = new Transactions();
            transaction.setStudentId(studentId);
            transaction.setSupervisorId(supervisorId);
            transaction.setAssetId(assetId);
            transaction.setDate(issueDate);
            transaction.setTransactionType("LOAN");
            transactionRepository.persist(transaction);


            Asset status = assetRepository.updateLoanStatus(assetName);
            assetRepository.persist(status);
        }catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error creating loan transaction" );
        }
    }

    public void createReturnTransaction(UUID transactionID, String assetId, String receivingSupervisorName, String returnDate) throws IllegalArgumentException {

        //System.out.println(returnDate);

        try{
            Transactions loanTransaction = transactionRepository.findTransactionById(transactionID);
            String supervisorId = loanTransaction.getSupervisorId();
            String studentId = loanTransaction.getStudentId();

            String receivingSupervisorId = userRepository.findUserIdsByFirstName(receivingSupervisorName);
            String assetName = assetRepository.getAssetNameById(assetId);


            Transactions transaction = new Transactions();
            transaction.setReceivingSupervisorId(receivingSupervisorId);
            transaction.setStudentId(studentId);
            transaction.setSupervisorId(supervisorId);
            transaction.setAssetId(assetId);
            transaction.setDate(returnDate);
            transaction.setTransactionType("RETURN");
            transactionRepository.persist(transaction);
            transactionRepository.persist(transaction);

            System.out.println(assetName);
            Asset status = assetRepository.updateReturnStatus(assetId);
            assetRepository.persist(status);
        }catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void updateLoanTransaction(UUID transactionId, String assetId, String supervisorName, String studentName, String date) throws ParseException {

        Transactions existingTransaction = transactionRepository.findTransactionById(transactionId);

        if (existingTransaction != null) {
            if (Objects.equals(existingTransaction.getTransactionType(), "LOAN")) {

                existingTransaction.setAssetId(assetId);
                existingTransaction.setStudentId(userRepository.findUserIdsByFirstName(studentName));
                existingTransaction.setSupervisorId(userRepository.findUserIdsByFirstName(supervisorName));
                existingTransaction.setDate(date);
                transactionRepository.persist(existingTransaction);
            } else {
                throw new RuntimeException("Transaction is not a loan type");
            }
        } else {
            throw new RuntimeException("Transaction not found");
        }
    }

    public void updateReturnTransaction(UUID transactionId, String assetId, String supervisorName, String studentName, String receivingSupervisorName, String date) throws ParseException {

        Transactions existingTransaction = transactionRepository.findTransactionById(transactionId);

        if (existingTransaction != null) {
            System.out.println(receivingSupervisorName);
            if (Objects.equals(existingTransaction.getTransactionType(), "RETURN")) {

                existingTransaction.setAssetId(assetId);
                existingTransaction.setStudentId(userRepository.findUserIdsByFirstName(studentName));
                existingTransaction.setSupervisorId(userRepository.findUserIdsByFirstName(supervisorName));
                existingTransaction.setReceivingSupervisorId(userRepository.findUserIdsByFirstName(receivingSupervisorName));
                existingTransaction.setDate(date);
                transactionRepository.persist(existingTransaction);
            } else {
                throw new RuntimeException("Transaction is not a return type");
            }
        } else {
            throw new RuntimeException("Transaction not found");
        }
    }

    /*public List<Transactions> getTransactionsWithFilters(String userId, String assetId, String date) {
        if (userId != null && !userId.isEmpty()) {
            return transactionRepository.findByUserId(userId);
        }
        if (assetId != null && !assetId.isEmpty()) {
            return transactionRepository.findByAssetId(assetId);
        }
        if (date != null && !date.isEmpty()) {
            return transactionRepository.findByDate(date);
        }else{
            return transactionRepository.allUsers();
        }
    }*/

    @Transactional
    public List<Transactions> getTransactionsWithFilters(String userId, String assetId, String date) {
        StringBuilder queryBuilder = new StringBuilder("SELECT t.transactionId, t.transactionType, t.date, t.assetId, a.name, a.model, concat_ws(' ', s.firstName, s.lastName) AS studentName, concat_ws(' ', su.firstName, su.lastName) AS supervisorName " +
                "FROM Transactions t " +
                "JOIN Asset a ON t.assetId = a.assetId " +
                "JOIN User s ON t.studentId = s.id " +
                "JOIN User su ON t.supervisorId = su.id WHERE 1=1");
        Map<String, Object> params = new HashMap<>();

        if (Objects.equals(getRole(userId), "Supervisor")) {
            if (userId != null) {
                queryBuilder.append(" AND t.supervisorId = :userId");
                params.put("userId", userId);
            }
        } else if (Objects.equals(getRole(userId), "Student")) {
            if (userId != null) {
                queryBuilder.append(" AND t.studentId = :userId");
                params.put("userId", userId);
            }
        }

        if (assetId != null) {
            queryBuilder.append(" AND t.assetId = :assetId");
            params.put("assetId", assetId);
        }

        if (date != null) {
            queryBuilder.append(" AND t.date = :date");
            params.put("date", date);
        }
        return transactionRepository.find(queryBuilder.toString(), Sort.by("date"), params).list();
    }

    }





