package main.java.org.acme.Repository;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import main.java.org.acme.Entity.User;

import java.util.List;

@ApplicationScoped
public class UserRepository implements PanacheRepositoryBase<User, String> {

/*    public String findUsernameById(String userId) {
        User user = find("id", userId).firstResult();
        if (user != null) {
            String firstName = user.getFirstName();
            String lastName = user.getLastName();
            return firstName + " " + lastName;
        }
        return null;
    }*/

    public String findRoleById(String userId) throws IllegalArgumentException{
        if (userId!=null && ! userId.isEmpty()){
            User user = find("id", userId).firstResult();
            if (user != null) {
                return user.getRole();
            }
            return null;
        }else{
            throw new IllegalArgumentException("userId can't be null.");
        }

    }

    @Transactional
    public String findUserIdsByFirstName(String fullName) throws IllegalArgumentException {
        //System.out.println(fullName);
        if (fullName != null && ! fullName.isEmpty()) {
            String firstName = extractFirstName(fullName);
            PanacheQuery<User> userQuery = find("firstName", firstName);
            User user = userQuery.firstResult();
            String id = user.getId().toString();
            return id;
        }else {
            throw new IllegalArgumentException("Username can't be null");
        }
    }

    private String extractFirstName(String userName) throws IllegalArgumentException {
        System.out.println(userName);
        if (userName != null && ! userName.isEmpty()) {
            String[] nameParts = userName.split(" ");
            if (nameParts.length > 0) {
                return nameParts[0];
            } else {
                throw new IllegalArgumentException("Invalid username format");
            }
        } else {
            throw new IllegalArgumentException("Username can't be null");
        }
    }



    public List<User> allUsers() {
        List<User> users= find("SELECT NEW User(u.firstName, u.lastName, u.Id) " +
                "FROM User u ").list();
        return users;
    }
}
