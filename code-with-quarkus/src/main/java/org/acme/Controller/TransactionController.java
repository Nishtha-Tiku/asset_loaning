package main.java.org.acme.Controller;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import main.java.org.acme.Entity.Asset;
import main.java.org.acme.Entity.Transactions;
import main.java.org.acme.Entity.User;
import main.java.org.acme.TransactionDTO;
import main.java.org.acme.Services.TransactionServices;

import java.text.ParseException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Path("/transactions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionController {

    @Inject
    TransactionServices transactionService;


    @GET
    public List<Transactions> getAllTransactions(TransactionDTO transaction) {

        if (transaction.getUserId()!= null && ! transaction.getUserId().isEmpty()){
            String id = transaction.getUserId();
            return transactionService.getTransactionsByUser(id);
        } /*else if (transaction.getTransactionId()!= null) {
            UUID id = transaction.getTransactionId();
            return transactionService.getTransactionsById(id);
        }else if(transaction.getUserId()!=null || transaction.getAssetId()!=null || transaction.getDate()!=null){
            return transactionService.getTransactionsWithFilters(transaction.getUserId(), transaction.getAssetId(), transaction.getDate());
        }*/else{
            throw new IllegalArgumentException("Invalid Parameters.");
        }
    }

    /*@GET
    @Path("/byUser")
    @Consumes(MediaType.APPLICATION_JSON)
    public List<Transactions> getTransactionsByUserId(Transactions transaction) {
        String id = transaction.getStudentId();
        return transactionService.getTransactions(id);
    }*/

    @Path("/filter")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public  List<Transactions> getTransactions(TransactionDTO filter) {

        return transactionService.getTransactionsWithFilters(filter.getUserId(), filter.getAssetId(), filter.getDate());
    }

    @GET
    @Path("/byTransaction")
    @Consumes(MediaType.APPLICATION_JSON)
    public List<Transactions> getTransactionsByTransactionId(Transactions transaction) {
           UUID id = transaction.getTransactionId();
           return transactionService.getTransactionsById(id);
    }

    @GET
    @Path("/users")
    public List<User> getAllUsers() {
        return transactionService.getAllUsers();

    }

    @GET
    @Path("/assets")
    public List<Asset> getAllAssets() {
        return transactionService.getAllAssets();

    }

    @POST
    @Path("/create")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createTransaction(TransactionDTO transaction) throws ParseException {
        try {
            if (Objects.equals(transactionService.getRole(transaction.getUserId()), "Supervisor")) {
                if (Objects.equals(transaction.getTransactionType(), "LOAN")) {
                    transactionService.createLoanTransaction(transaction.getSupervisorName(), transaction.getStudentName(), transaction.getAssetName(), transaction.getDate());
                    return Response.status(Response.Status.CREATED).build();
                } else if (Objects.equals(transaction.getTransactionType(), "RETURN")) {
                    transactionService.createReturnTransaction(transaction.getTransactionId(), transaction.getAssetId(), transaction.getReceivingSupervisorName(), transaction.getDate());
                    return Response.status(Response.Status.CREATED).build();
                } else {
                    return Response.status(Response.Status.BAD_REQUEST).entity("Invalid transaction type").build();
                }
            }else {
                return Response.status(Response.Status.FORBIDDEN).entity("Permission denied").build();
            }
        }catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PATCH
    @Path("/update")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateTransaction(TransactionDTO transaction) throws ParseException {

        if (Objects.equals(transactionService.getRole(transaction.getUserId()), "Supervisor")) {
            if (Objects.equals(transaction.getTransactionType(), "LOAN")) {
                transactionService.updateLoanTransaction(transaction.getTransactionId(), transaction.getAssetId(), transaction.getSupervisorName(), transaction.getStudentName(), transaction.getDate());
                return Response.status(Response.Status.CREATED).build();
            } else if (Objects.equals(transaction.getTransactionType(), "RETURN")) {
                transactionService.updateReturnTransaction(transaction.getTransactionId(), transaction.getAssetId(), transaction.getSupervisorName(), transaction.getStudentName(), transaction.getReceivingSupervisorName(), transaction.getDate());
                return Response.status(Response.Status.CREATED).build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity("Invalid transaction type").build();
            }
        }
        else {
            return Response.status(Response.Status.FORBIDDEN).entity("Permission denied").build();
        }
    }


}


