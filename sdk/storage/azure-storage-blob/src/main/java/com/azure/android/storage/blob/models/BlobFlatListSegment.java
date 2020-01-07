package com.azure.android.storage.blob.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * The BlobFlatListSegment model.
 */
@JacksonXmlRootElement(localName = "Blobs")
public final class BlobFlatListSegment {
    /*
     * The blobItems property.
     */
    @JsonProperty("Blob")
    private List<BlobItem> blobItems = new ArrayList<>();

    /**
     * Get the blobItems property: The blobItems property.
     *
     * @return the blobItems value.
     */
    public List<BlobItem> getBlobItems() {
        return this.blobItems;
    }

    /**
     * Set the blobItems property: The blobItems property.
     *
     * @param blobItems the blobItems value to set.
     * @return the BlobFlatListSegment object itself.
     */
    public BlobFlatListSegment setBlobItems(List<BlobItem> blobItems) {
        this.blobItems = blobItems;
        return this;
    }
}
