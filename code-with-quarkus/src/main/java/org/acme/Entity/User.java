package main.java.org.acme.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "user", schema = "asset_loan")
public class User {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String gender;
    private String role;

    public User() {
    }

    public User(String firstName, String lastName, String userId) {
        this.firstName = firstName;
        this.id = userId;
        this.lastName = lastName;

    }

}

