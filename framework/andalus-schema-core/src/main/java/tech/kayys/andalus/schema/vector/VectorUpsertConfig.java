package tech.kayys.andalus.schema.vector;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Configuration for Vector Upsert operations.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VectorUpsertConfig {
    private String collectionName;

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }
}
