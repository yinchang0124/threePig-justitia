package com.zrsy.threepig.domain.BDQL;

import com.google.gson.annotations.SerializedName;

import java.util.Map;
import java.util.TreeMap;

public class MetaData {

    /**
     * The id.
     */
    @SerializedName("id")
    private String id;

    /**
     * The metadata.
     */
    @SerializedName("metadata")
    private Map<String, Object> metadata = new TreeMap<String, Object>();

    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the metadata.
     *
     * @return the metadata
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetaData(String key, Object value) {
        this.metadata.put(key, value);
    }
}
