package main.java.org.acme.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.UUID;
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "asset", schema = "asset_loan")
public class Asset {
    @Id
    private String assetId;
    private String name;
    private String serialNumber;
    private String model;
    private String status;

    public Asset(String name, String assetId) {
        this.name = name;
        this.assetId = assetId;

    }


}
