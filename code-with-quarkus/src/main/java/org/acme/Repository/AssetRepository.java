package main.java.org.acme.Repository;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import main.java.org.acme.Entity.Asset;

import java.util.List;

@ApplicationScoped
public class AssetRepository implements PanacheRepositoryBase<Asset, String> {

    @Transactional
    public String findAssetIdByName(String assetName) throws IllegalArgumentException {
        if (assetName != null && ! assetName.isEmpty()){
            PanacheQuery<Asset> assetQuery = find("name", assetName);
            Asset asset = assetQuery.firstResult();
            String id = asset.getAssetId().toString();
            return id;
        }else{
            throw new IllegalArgumentException("assetName can't be null.");
        }
    }

    @Transactional
    public String findAssetModelByName(String assetName) throws IllegalArgumentException{
        if (assetName != null && ! assetName.isEmpty()){
            PanacheQuery<Asset> assetQuery = find("name", assetName);
            Asset asset = assetQuery.firstResult();
            String model = asset.getModel().toString();
            return model;
        }else{
            throw new IllegalArgumentException("assetName can't be null.");
        }
    }

    @Transactional
    public Asset updateLoanStatus(String assetName) throws IllegalArgumentException{
        if (assetName != null && ! assetName.isEmpty()){
            PanacheQuery<Asset> assetQuery = find("name", assetName);
            Asset asset = assetQuery.firstResult();
            if ("AVAILABLE".equals(asset.getStatus())) {
                asset.setStatus("LOANED");
            }else {

                throw new IllegalStateException("Illegal action: Cannot update asset with status " + asset.getStatus());
            }
            return asset;
        }else{
            throw new IllegalArgumentException("assetName can't be null.");
        }
    }

    public String getAssetNameById(String assetId) throws IllegalArgumentException{
        if (assetId!=null && ! assetId.isEmpty()){
            PanacheQuery<Asset> assetQuery = find("assetId", assetId);
            Asset asset = assetQuery.firstResult();
            String name = asset.getName();
            return name;
        }else{
            throw new IllegalStateException("assetId can't be null.");
        }
    }

    @Transactional
    public Asset updateReturnStatus(String assetId) throws IllegalArgumentException {
        if (assetId!=null && ! assetId.isEmpty()){
            PanacheQuery<Asset> assetQuery = find("assetId", assetId);
            Asset asset = assetQuery.firstResult();
            if ("LOANED".equals(asset.getStatus())) {
                asset.setStatus("AVAILABLE");
            }else {
                throw new IllegalStateException("Illegal action: Cannot update asset with status " + asset.getStatus());
            }
            return asset;
        }else{
            throw new IllegalStateException("assetId can't be null.");
        }
    }


    public List<Asset> allAssets() {
        List<Asset> assets = find("SELECT NEW Asset(a.name, a.assetId) " +
                "FROM Asset a WHERE status = 'AVAILABLE'").list();
        return assets;
    }
}
