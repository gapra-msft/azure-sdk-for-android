// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.storage.blob;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.azure.android.core.http.Response;
import com.azure.android.core.http.ServiceClient;
import com.azure.android.core.http.interceptor.AddDateInterceptor;
import com.azure.android.core.http.interceptor.RequestIdInterceptor;
import com.azure.android.core.util.CancellationToken;
import com.azure.android.storage.blob.implementation.util.ModelHelper;
import com.azure.android.storage.blob.interceptor.MetadataInterceptor;
import com.azure.android.storage.blob.interceptor.NormalizeEtagInterceptor;
import com.azure.android.storage.blob.models.AccessTier;
import com.azure.android.storage.blob.models.BlobDeleteResponse;
import com.azure.android.storage.blob.models.BlobDownloadResponse;
import com.azure.android.storage.blob.models.BlobGetPropertiesHeaders;
import com.azure.android.storage.blob.models.BlobGetPropertiesResponse;
import com.azure.android.storage.blob.models.BlobGetTagsResponse;
import com.azure.android.storage.blob.models.BlobHttpHeaders;
import com.azure.android.storage.blob.models.BlobItem;
import com.azure.android.storage.blob.models.BlobRange;
import com.azure.android.storage.blob.models.BlobRequestConditions;
import com.azure.android.storage.blob.models.BlobSetHttpHeadersResponse;
import com.azure.android.storage.blob.models.BlobSetMetadataResponse;
import com.azure.android.storage.blob.models.BlobSetTagsResponse;
import com.azure.android.storage.blob.models.BlobSetTierResponse;
import com.azure.android.storage.blob.models.BlobsPage;
import com.azure.android.storage.blob.models.BlockBlobItem;
import com.azure.android.storage.blob.models.BlockBlobsCommitBlockListResponse;
import com.azure.android.storage.blob.models.BlockBlobsStageBlockResponse;
import com.azure.android.storage.blob.models.ContainerCreateResponse;
import com.azure.android.storage.blob.models.ContainerDeleteResponse;
import com.azure.android.storage.blob.models.ContainerGetPropertiesHeaders;
import com.azure.android.storage.blob.models.ContainerGetPropertiesResponse;
import com.azure.android.storage.blob.models.ContainersListBlobFlatSegmentResponse;
import com.azure.android.storage.blob.models.ListBlobsFlatSegmentResponse;
import com.azure.android.storage.blob.models.ListBlobsIncludeItem;
import com.azure.android.storage.blob.models.ListBlobsOptions;
import com.azure.android.storage.blob.options.BlobDeleteOptions;
import com.azure.android.storage.blob.options.BlobGetPropertiesOptions;
import com.azure.android.storage.blob.options.BlobGetTagsOptions;
import com.azure.android.storage.blob.options.BlobRawDownloadOptions;
import com.azure.android.storage.blob.options.BlobSetAccessTierOptions;
import com.azure.android.storage.blob.options.BlobSetHttpHeadersOptions;
import com.azure.android.storage.blob.options.BlobSetMetadataOptions;
import com.azure.android.storage.blob.options.BlobSetTagsOptions;
import com.azure.android.storage.blob.options.BlockBlobCommitBlockListOptions;
import com.azure.android.storage.blob.options.BlockBlobStageBlockOptions;
import com.azure.android.storage.blob.options.ContainerCreateOptions;
import com.azure.android.storage.blob.options.ContainerDeleteOptions;
import com.azure.android.storage.blob.options.ContainerGetPropertiesOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Interceptor;
import okhttp3.ResponseBody;

/**
 * Client for Storage Blob service.
 *
 * <p>
 * This client is instantiated through {@link StorageBlobClient.Builder}.
 */
public class StorageBlobClient {
    private final ServiceClient serviceClient;
    private final StorageBlobServiceImpl storageBlobServiceClient;

    private StorageBlobClient(ServiceClient serviceClient, String serviceVersion) {
        this.serviceClient = serviceClient;
        this.storageBlobServiceClient = new StorageBlobServiceImpl(this.serviceClient, serviceVersion);
    }

    /**
     * Creates a new {@link Builder} with initial configuration copied from this {@link StorageBlobClient}
     *
     * @return A new {@link Builder}.
     */
    public StorageBlobClient.Builder newBuilder() {
        return new Builder(this);
    }

    /**
     * Gets the blob service base URL.
     *
     * @return The blob service base URL.
     */
    public String getBlobServiceUrl() {
        return this.serviceClient.getBaseUrl();
    }

    /**
     * Creates a new container within a storage account. If a container with the same name already exists, the operation
     * fails.
     * For more information, see the <a href="https://docs.microsoft.com/rest/api/storageservices/create-container">Azure Docs</a>.
     *
     * @param containerName The container name.
     */
    @Nullable
    public Void createContainer(@NonNull String containerName) {
        return this.createContainerWithResponse(new ContainerCreateOptions(containerName)).getValue();
    }

    /**
     * Creates a new container within a storage account. If a container with the same name already exists, the operation
     * fails.
     * For more information, see the <a href="https://docs.microsoft.com/rest/api/storageservices/create-container">Azure Docs</a>.
     *
     * @param options {@link ContainerCreateOptions}
     * @return The response information returned from the server when creating a container.
     */
    @NonNull
    public ContainerCreateResponse createContainerWithResponse(@NonNull ContainerCreateOptions options) {
        Objects.requireNonNull(options);
        return storageBlobServiceClient.createContainerWithRestResponse(options.getContainerName(),
            options.getTimeout(), options.getMetadata(), options.getPublicAccessType(), options.getCancellationToken());
    }

    /**
     * Marks the specified container for deletion. The container and any blobs contained within it are later deleted
     * during garbage collection. For more information, see the
     * <a href="https://docs.microsoft.com/rest/api/storageservices/delete-container">Azure Docs</a>.
     *
     * @param containerName The container name.
     */
    @Nullable
    public Void deleteContainer(@NonNull String containerName) {
        return this.deleteContainerWithResponse(new ContainerDeleteOptions(containerName)).getValue();
    }

    /**
     * Marks the specified container for deletion. The container and any blobs contained within it are later deleted
     * during garbage collection. For more information, see the
     * <a href="https://docs.microsoft.com/rest/api/storageservices/delete-container">Azure Docs</a>.
     *
     * @param options {@link ContainerDeleteOptions}
     * @return The response information returned from the server when deleting a container.
     */
    @NonNull
    public ContainerDeleteResponse deleteContainerWithResponse(@NonNull ContainerDeleteOptions options) {
        Objects.requireNonNull(options);
        ModelHelper.validateRequestConditions(options.getRequestConditions(), false, true, true, false);
        return storageBlobServiceClient.deleteContainerWithRestResponse(options.getContainerName(),
            options.getTimeout(), options.getRequestConditions(), options.getCancellationToken());
    }

    /**
     * Returns the container's metadata and system properties. For more information, see the
     * <a href="https://docs.microsoft.com/rest/api/storageservices/get-container-metadata">Azure Docs</a>.
     *
     * @param containerName The container name.
     * @return The container's properties.
     */
    /* TODO: (gapra) This should probably return a handwrapped type? */
    @NonNull
    public ContainerGetPropertiesHeaders getContainerProperties(@NonNull String containerName) {
        return this.getContainerPropertiesWithResponse(new ContainerGetPropertiesOptions(containerName)).getDeserializedHeaders();
    }

    /**
     * Returns the container's metadata and system properties. For more information, see the
     * <a href="https://docs.microsoft.com/rest/api/storageservices/get-container-metadata">Azure Docs</a>.
     *
     * @param options {@link ContainerGetPropertiesOptions}
     * @return The response information returned from the server when getting a container's properties.
     */
    @NonNull
    public ContainerGetPropertiesResponse getContainerPropertiesWithResponse(@NonNull ContainerGetPropertiesOptions options) {
        Objects.requireNonNull(options);
        ModelHelper.validateRequestConditions(options.getRequestConditions(), false, false, true, false);
        BlobRequestConditions requestConditions = options.getRequestConditions() == null ? new BlobRequestConditions()
            : options.getRequestConditions();

        return storageBlobServiceClient.getContainerPropertiesWithResponse(options.getContainerName(),
            options.getTimeout(), requestConditions.getLeaseId(), options.getCancellationToken());
    }

    /**
     * Gets a list of blobs identified by a page id in a given container.
     *
     * @param pageId        Identifies the portion of the list to be returned.
     * @param containerName The container name.
     * @param options       The page options.
     * @return A list of blobs.
     */
    public BlobsPage getBlobsInPage(String pageId,
                                    String containerName,
                                    ListBlobsOptions options) {
        ListBlobsFlatSegmentResponse result = this.storageBlobServiceClient.listBlobFlatSegment(pageId,
            containerName, options);

        final List<BlobItem> list;
        if (result.getSegment() != null
            && result.getSegment().getBlobItems() != null) {
            list = result.getSegment().getBlobItems();
        } else {
            list = new ArrayList<>(0);
        }
        return new BlobsPage(list, pageId, result.getNextMarker());
    }

    /**
     * Gets a list of blobs identified by a page id in a given container.
     *
     * @param pageId            Identifies the portion of the list to be returned.
     * @param containerName     The container name.
     * @param prefix            Filters the results to return only blobs whose name begins with the specified prefix.
     * @param maxResults        Specifies the maximum number of blobs to return.
     * @param include           Include this parameter to specify one or more datasets to include in the response.
     * @param timeout           The timeout parameter is expressed in seconds. For more information, see
     *                          &lt;a href="https://docs.microsoft.com/en-us/rest/api/storageservices/setting-timeouts-for-blob-service-operations"&gt;Setting Timeouts for Blob Service Operations.&lt;/a&gt;.
     * @param cancellationToken The token to request cancellation.
     * @return A response object containing a list of blobs.
     */
    public Response<BlobsPage> getBlobsInPageWithRestResponse(String pageId,
                                                              String containerName,
                                                              String prefix,
                                                              Integer maxResults,
                                                              List<ListBlobsIncludeItem> include,
                                                              Integer timeout,
                                                              CancellationToken cancellationToken) {
        ContainersListBlobFlatSegmentResponse result
            = this.storageBlobServiceClient.listBlobFlatSegmentWithRestResponse(pageId,
            containerName,
            prefix,
            maxResults,
            include,
            timeout,
            cancellationToken);
        final List<BlobItem> list;
        if (result.getValue().getSegment() != null
            && result.getValue().getSegment().getBlobItems() != null) {
            list = result.getValue().getSegment().getBlobItems();
        } else {
            list = new ArrayList<>(0);
        }
        BlobsPage blobsPage = new BlobsPage(list, pageId, result.getValue().getNextMarker());

        return new Response<>(null,
            result.getStatusCode(),
            result.getHeaders(),
            blobsPage);
    }

    /**
     * Returns the blob's metadata and properties. For more information, see the
     * <a href="https://docs.microsoft.com/en-us/rest/api/storageservices/get-blob-properties">Azure Docs</a></p>.
     *
     * @param containerName The container name.
     * @param blobName      The blob name.
     * @return The blob's metadata and properties
     */
    @NonNull
    public BlobGetPropertiesHeaders getBlobProperties(@NonNull String containerName, @NonNull String blobName) {
        return this.getBlobPropertiesWithResponse(new BlobGetPropertiesOptions(containerName, blobName))
            .getDeserializedHeaders();
    }

    /**
     * Returns the blob's metadata and properties. For more information, see the
     * <a href="https://docs.microsoft.com/en-us/rest/api/storageservices/get-blob-properties">Azure Docs</a></p>.
     *
     * @param options {@link BlobGetPropertiesOptions}
     * @return The response information returned from the server when getting a blob's properties.
     */
    @NonNull
    public BlobGetPropertiesResponse getBlobPropertiesWithResponse(@NonNull BlobGetPropertiesOptions options) {
        Objects.requireNonNull(options);
        return storageBlobServiceClient.getBlobPropertiesWithRestResponse(options.getContainerName(),
            options.getBlobName(), options.getSnapshot(), options.getTimeout(), options.getRequestConditions(),
            options.getCpkInfo(), options.getCancellationToken());
    }

    /**
     * Changes a blob's HTTP header properties. If only one HTTP header is updated, the others will all be erased. In
     * order to preserve existing values, they must be passed alongside the header being changed.
     *
     * @param containerName The container name.
     * @param blobName      The blob name.
     * @param headers       {@link BlobHttpHeaders}
     */
    @Nullable
    public Void setBlobHttpHeaders(@NonNull String containerName, @NonNull String blobName,
                                   @Nullable BlobHttpHeaders headers) {
        return this.setBlobHttpHeadersWithResponse(new BlobSetHttpHeadersOptions(containerName, blobName, headers))
            .getValue();
    }

    /**
     * Changes a blob's HTTP header properties. If only one HTTP header is updated, the others will all be erased. In
     * order to preserve existing values, they must be passed alongside the header being changed.
     *
     * @param options {@link BlobSetHttpHeadersOptions}
     * @return The response information returned from the server when setting a blob's http headers.
     */
    @NonNull
    public BlobSetHttpHeadersResponse setBlobHttpHeadersWithResponse(@NonNull BlobSetHttpHeadersOptions options) {
        Objects.requireNonNull(options);
        return storageBlobServiceClient.setBlobHttpHeadersWithRestResponse(options.getContainerName(),
            options.getBlobName(), options.getTimeout(), options.getRequestConditions(), options.getHeaders(),
            options.getCancellationToken());
    }

    /**
     * Changes a blob's metadata. The specified metadata in this method will replace existing metadata. If old values
     * must be preserved, they must be downloaded and included in the call to this method.
     * <p>For more information, see the
     * <a href="https://docs.microsoft.com/en-us/rest/api/storageservices/set-blob-metadata">Azure Docs</a></p>
     *
     * @param containerName The container name.
     * @param blobName      The blob name.
     * @param metadata      Metadata to associate with the blob.
     */
    @Nullable
    public Void setBlobMetadata(@NonNull String containerName, @NonNull String blobName,
                                @Nullable Map<String, String> metadata) {
        return this.setBlobMetadataWithResponse(new BlobSetMetadataOptions(containerName, blobName, metadata))
            .getValue();
    }

    /**
     * Changes a blob's metadata. The specified metadata in this method will replace existing metadata. If old values
     * must be preserved, they must be downloaded and included in the call to this method.
     * <p>For more information, see the
     * <a href="https://docs.microsoft.com/en-us/rest/api/storageservices/set-blob-metadata">Azure Docs</a></p>
     *
     * @param options {@link BlobSetMetadataOptions}
     * @return The response information returned from the server when setting a blob's metadata.
     */
    @NonNull
    public BlobSetMetadataResponse setBlobMetadataWithResponse(@NonNull BlobSetMetadataOptions options) {
        return storageBlobServiceClient.setBlobMetadataWithRestResponse(options.getContainerName(),
            options.getBlobName(), options.getTimeout(), options.getRequestConditions(), options.getMetadata(),
            options.getCpkInfo(), options.getCancellationToken());
    }

    /* TODO: (gapra) Should we remove everything related to PageBlobs here? */
    /**
     * Sets the tier on a blob. The operation is allowed on a page blob in a premium storage account or a block blob in
     * a blob storage or GPV2 account. A premium page blob's tier determines the allowed size, IOPS, and bandwidth of
     * the blob. A block blob's tier determines the Hot/Cool/Archive storage type. This does not update the blob's
     * etag.
     * <p>For more information, see the
     * <a href="https://docs.microsoft.com/en-us/rest/api/storageservices/set-blob-tier">Azure Docs</a></p>
     *
     * @param containerName The container name.
     * @param blobName      The blob name.
     * @param tier          The access tier.
     */
    @Nullable
    public Void setBlobAccessTier(@NonNull String containerName, @NonNull String blobName, @Nullable AccessTier tier) {
        return this.setBlobAccessTierWithResponse(new BlobSetAccessTierOptions(containerName, blobName, tier))
            .getValue();
    }

    /**
     * Sets the tier on a blob. The operation is allowed on a page blob in a premium storage account or a block blob in
     * a blob storage or GPV2 account. A premium page blob's tier determines the allowed size, IOPS, and bandwidth of
     * the blob. A block blob's tier determines the Hot/Cool/Archive storage type. This does not update the blob's
     * etag.
     * <p>For more information, see the
     * <a href="https://docs.microsoft.com/en-us/rest/api/storageservices/set-blob-tier">Azure Docs</a></p>
     *
     * @param options {@link BlobSetAccessTierOptions}
     * @return The response information returned from the server when setting a blob's access tier.
     */
    @NonNull
    public BlobSetTierResponse setBlobAccessTierWithResponse(@NonNull BlobSetAccessTierOptions options) {
        Objects.requireNonNull(options);
        ModelHelper.validateRequestConditions(options.getRequestConditions(), false, false, true, true);
        BlobRequestConditions requestConditions = options.getRequestConditions() == null ? new BlobRequestConditions()
            : options.getRequestConditions();

        return storageBlobServiceClient.setBlobTierWithRestResponse(options.getContainerName(), options.getBlobName(),
            options.getAccessTier(), options.getSnapshot(), null /*TODO: (gapra) VersionId?*/ , options.getTimeout(),
            options.getRehydratePriority(), requestConditions.getLeaseId(), requestConditions.getTagsConditions(),
            options.getCancellationToken());
    }

    /**
     * Reads a range of bytes from a blob.
     *
     * <p>
     * This method will execute a raw HTTP GET in order to download a single blob to the destination.
     * It is **STRONGLY** recommended that you use the {@link StorageBlobAsyncClient#download(Context, String, String, File)}
     * or {@link StorageBlobAsyncClient#download(Context, String, String, Uri)} method instead - that method will
     * manage the transfer in the face of changing network conditions, and is able to transfer multiple
     * blocks in parallel.
     *
     * <p>For more information, see the
     * <a href="https://docs.microsoft.com/en-us/rest/api/storageservices/get-blob">Azure Docs</a></p>
     *
     * @param containerName The container name.
     * @param blobName      The blob name.
     * @return The response containing the blob's bytes.
     */
    @NonNull
    public ResponseBody rawDownload(@NonNull String containerName,
                                    @NonNull String blobName) {
        return this.rawDownloadWithResponse(new BlobRawDownloadOptions(containerName, blobName)).getValue();
    }

    /**
     * Reads a range of bytes from a blob.
     *
     * <p>
     * This method will execute a raw HTTP GET in order to download a single blob to the destination.
     * It is **STRONGLY** recommended that you use the {@link StorageBlobAsyncClient#download(Context, String, String, File)}
     * or {@link StorageBlobAsyncClient#download(Context, String, String, Uri)} method instead - that method will
     * manage the transfer in the face of changing network conditions, and is able to transfer multiple
     * blocks in parallel.
     *
     * <p>For more information, see the
     * <a href="https://docs.microsoft.com/en-us/rest/api/storageservices/get-blob">Azure Docs</a></p>
     *
     * @param options {@link BlobRawDownloadOptions}
     * @return The response information returned from the server when downloading a blob.
     */
    @NonNull
    public BlobDownloadResponse rawDownloadWithResponse(@NonNull BlobRawDownloadOptions options) {
        Objects.requireNonNull(options);
        BlobRange range = options.getRange() == null ? new BlobRange(0) : options.getRange();

        return storageBlobServiceClient.downloadWithRestResponse(options.getContainerName(), options.getBlobName(),
            options.getSnapshot(), options.getTimeout(), range.toHeaderValue(), options.isRetrieveContentRangeMd5(),
            options.isRetrieveContentRangeCrc64(), options.getRequestConditions(), options.getCpkInfo(),
            options.getCancellationToken());
    }

    /**
     * Uploads the specified block to the block blob's "staging area" to be later committed by a call to
     * commitBlockList.
     * <p>For more information, see the
     * <a href="https://docs.microsoft.com/rest/api/storageservices/put-block">Azure Docs</a>.</p>
     *
     * @param containerName The container name.
     * @param blobName      The blob name.
     * @param base64BlockId A valid Base64 string value that identifies the block. Prior to encoding, the string must
     *                      be less than or equal to 64 bytes in size. For a given blob, the length of the value specified
     *                      for the base64BlockId parameter must be the same size for each block.
     * @param blockContent  The block content in bytes.
     * @param contentMd5    The transactional MD5 for the body, to be validated by the service.
     */
    @Nullable
    public Void stageBlock(@NonNull String containerName,
                           @NonNull String blobName,
                           @NonNull String base64BlockId,
                           @NonNull byte[] blockContent,
                           @Nullable byte[] contentMd5) {
        return this.storageBlobServiceClient.stageBlock(containerName,
            blobName,
            base64BlockId,
            blockContent,
            contentMd5);
    }

    /**
     * Uploads the specified block to the block blob's "staging area" to be later committed by a call to
     * commitBlockList.
     * <p>For more information, see the
     * <a href="https://docs.microsoft.com/rest/api/storageservices/put-block">Azure Docs</a>.</p>
     *
     * @param options {@link BlockBlobStageBlockOptions}
     * @return The response object.
     */
    @NonNull
    public BlockBlobsStageBlockResponse stageBlockWithResponse(@NonNull BlockBlobStageBlockOptions options) {
        Objects.requireNonNull(options);
        ModelHelper.validateRequestConditions(options.getRequestConditions(), false, false, true, false);
        BlobRequestConditions requestConditions = options.getRequestConditions() == null ? new BlobRequestConditions()
            : options.getRequestConditions();

        return this.storageBlobServiceClient.stageBlockWithRestResponse(options.getContainerName(), options.getBlobName(),
            options.getBase64BlockId(), options.getData(), options.getContentMd5(), options.getContentCrc64(),
            options.isComputeMd5(), options.getTimeout(), requestConditions.getLeaseId(), options.getCpkInfo(),
            options.getCancellationToken());
    }

    /**
     * The Commit Block List operation writes a blob by specifying the list of block IDs that make up the blob.
     * For a block to be written as part of a blob, the block must have been successfully written to the server in a prior
     * {@link StorageBlobClient#stageBlock(String, String, String, byte[], byte[])} operation. You can call commit Block List
     * to update a blob by uploading only those blocks that have changed, then committing the new and existing blocks together.
     * You can do this by specifying whether to commit a block from the committed block list or from the uncommitted block list,
     * or to commit the most recently uploaded version of the block, whichever list it may belong to.
     *
     * @param containerName  The container name.
     * @param blobName       The blob name.
     * @param base64BlockIds The block IDs.
     * @param overwrite      Indicate whether to overwrite the block list if already exists.
     * @return The properties of the block blob
     */
    @NonNull
    public BlockBlobItem commitBlockList(@NonNull String containerName, @NonNull String blobName,
                                         @Nullable List<String> base64BlockIds, boolean overwrite) {
        BlobRequestConditions requestConditions = null;
        if (!overwrite) {
            requestConditions = new BlobRequestConditions().setIfNoneMatch("*");
        }
        return this.commitBlockListWithResponse(new BlockBlobCommitBlockListOptions(containerName, blobName, base64BlockIds)
        .setRequestConditions(requestConditions)).getBlockBlobItem();
    }

    /**
     * The Commit Block List operation writes a blob by specifying the list of block IDs that make up the blob.
     * For a block to be written as part of a blob, the block must have been successfully written to the server in a prior
     * {@link StorageBlobClient#stageBlock(String, String, String, byte[], byte[])} operation. You can call commit Block List
     * to update a blob by uploading only those blocks that have changed, then committing the new and existing blocks together.
     * You can do this by specifying whether to commit a block from the committed block list or from the uncommitted block list,
     * or to commit the most recently uploaded version of the block, whichever list it may belong to.
     *
     * @param options {@link BlockBlobCommitBlockListOptions}
     * @return The response object.
     */
    @NonNull
    public BlockBlobsCommitBlockListResponse commitBlockListWithResponse(@NonNull BlockBlobCommitBlockListOptions options) {
        Objects.requireNonNull(options);
        return this.storageBlobServiceClient.commitBlockListWithRestResponse(options.getContainerName(),
            options.getBlobName(), options.getBase64BlockIds(), options.getContentMd5(), options.getContentCrc64(),
            options.getTimeout(), options.getHeaders(), options.getMetadata(), options.getRequestConditions(),
            options.getCpkInfo(), options.getAccessTier(), options.getCancellationToken());
    }

    /**
     * Deletes the specified blob or snapshot. Note that deleting a blob also deletes all its snapshots.
     * <p>For more information, see the
     * <a href="https://docs.microsoft.com/en-us/rest/api/storageservices/delete-blob">Azure Docs</a></p>
     *
     * @param containerName The container name.
     * @param blobName      The blob name.
     */
    @Nullable
    public Void deleteBlob(@NonNull String containerName, @NonNull String blobName) {
        return this.deleteBlobWithResponse(new BlobDeleteOptions(containerName, blobName)).getValue();
    }

    /**
     * Deletes the specified blob or snapshot. Note that deleting a blob also deletes all its snapshots.
     * <p>For more information, see the
     * <a href="https://docs.microsoft.com/en-us/rest/api/storageservices/delete-blob">Azure Docs</a></p>
     *
     * @param options {@link BlobDeleteOptions}
     * @return A response object containing the details of the delete operation.
     */
    @NonNull
    public BlobDeleteResponse deleteBlobWithResponse(@NonNull BlobDeleteOptions options) {
        return storageBlobServiceClient.deleteBlobWithRestResponse(options.getContainerName(), options.getBlobName(),
            options.getSnapshot(), options.getTimeout(), options.getDeleteSnapshots(), options.getRequestConditions(),
            options.getCancellationToken());
    }

    /**
     * Returns the blob's tags.
     * <p>For more information, see the
     * <a href="https://docs.microsoft.com/en-us/rest/api/storageservices/get-blob-tags">Azure Docs</a></p>
     *
     * @param containerName The container name.
     * @param blobName      The blob name.
     * @return The blob's tags.
     */
    @NonNull
    public Map<String, String> getBlobTags(@NonNull String containerName, @NonNull String blobName) {
        return this.getBlobTagsWithResponse(new BlobGetTagsOptions(containerName, blobName)).getValue();
    }

    /**
     * Returns the blob's tags.
     * <p>For more information, see the
     * <a href="https://docs.microsoft.com/en-us/rest/api/storageservices/get-blob-tags">Azure Docs</a></p>
     *
     * @param options {@link BlobGetTagsOptions}
     * @return A response object containing the blob's tags.
     */
    @NonNull
    public Response<Map<String, String>> getBlobTagsWithResponse(@NonNull BlobGetTagsOptions options) {
        Objects.requireNonNull(options);
        ModelHelper.validateRequestConditions(options.getRequestConditions(), false, false, false, true);
        BlobRequestConditions requestConditions = options.getRequestConditions() == null ? new BlobRequestConditions()
            : options.getRequestConditions();

        BlobGetTagsResponse response = this.storageBlobServiceClient.getTagsWithRestResponse(options.getContainerName(),
            options.getBlobName(), options.getSnapshot(), null, options.getTimeout(),
            requestConditions.getTagsConditions(), options.getCancellationToken());

        /* TODO: Create a ResponseBase type and return that. */
        return new Response<>(null,
            response.getStatusCode(),
            response.getHeaders(),
            ModelHelper.populateBlobTags(response.getValue()));
    }

    /**
     * Sets user defined tags. The specified tags in this method will replace existing tags. If old values must be
     * preserved, they must be downloaded and included in the call to this method.
     * <p>For more information, see the
     * <a href="https://docs.microsoft.com/en-us/rest/api/storageservices/set-blob-tags">Azure Docs</a></p>
     *
     * @param containerName The container name.
     * @param blobName      The blob name.
     * @param tags          Tags to associate with the blob.
     */
    @Nullable
    public Void setBlobTags(@NonNull String containerName, @NonNull String blobName,
                            @Nullable Map<String, String> tags) {
        return this.setBlobTagsWithResponse(new BlobSetTagsOptions(containerName, blobName, tags)).getValue();
    }

    /**
     * Sets user defined tags. The specified tags in this method will replace existing tags. If old values must be
     * preserved, they must be downloaded and included in the call to this method.
     * <p>For more information, see the
     * <a href="https://docs.microsoft.com/en-us/rest/api/storageservices/set-blob-tags">Azure Docs</a></p>
     *
     * @param options {@link BlobSetTagsOptions}
     */
    @NonNull
    public BlobSetTagsResponse setBlobTagsWithResponse(@NonNull BlobSetTagsOptions options) {
        Objects.requireNonNull(options);
        ModelHelper.validateRequestConditions(options.getRequestConditions(), false, false, false, true);
        BlobRequestConditions requestConditions = options.getRequestConditions() == null ? new BlobRequestConditions()
            : options.getRequestConditions();

        return storageBlobServiceClient.setBlobTagsWithRestResponse(options.getContainerName(), options.getBlobName(),
            options.getTimeout(), null, requestConditions.getTagsConditions(), options.getTags(), options.getCancellationToken());
    }

    /**
     * Builder for {@link StorageBlobClient}.
     * A builder to configure and build a {@link StorageBlobClient}.
     */
    public static class Builder {
        private final ServiceClient.Builder serviceClientBuilder;
        private BlobServiceVersion serviceVersion;

        /**
         * Creates a {@link Builder}.
         */
        public Builder() {
            this(new ServiceClient.Builder());
            addStandardInterceptors();
        }

        /**
         * Creates a {@link Builder} that uses the provided {@link com.azure.android.core.http.ServiceClient.Builder}
         * to build a {@link ServiceClient} for the {@link StorageBlobClient}.
         *
         * <p>
         * The builder produced {@link ServiceClient} is used by the {@link StorageBlobClient} to make Rest API calls.
         * Multiple {@link StorageBlobClient} instances can share the same {@link ServiceClient} instance, for e.g.
         * when a new {@link StorageBlobClient} is created from an existing {@link StorageBlobClient} through
         * {@link StorageBlobClient#newBuilder()} ()} then both shares the same {@link ServiceClient}.
         * The {@link ServiceClient} composes HttpClient, HTTP settings (such as connection timeout, interceptors)
         * and Retrofit for Rest calls.
         *
         * @param serviceClientBuilder The {@link com.azure.android.core.http.ServiceClient.Builder}.
         */
        public Builder(ServiceClient.Builder serviceClientBuilder) {
            this.serviceClientBuilder
                = Objects.requireNonNull(serviceClientBuilder, "serviceClientBuilder cannot be null.");
            addStandardInterceptors();
        }

        private void addStandardInterceptors() {
            this.serviceClientBuilder
                .addInterceptor(new RequestIdInterceptor())
                .addInterceptor(new AddDateInterceptor())
                .addInterceptor(new MetadataInterceptor())
                .addInterceptor(new NormalizeEtagInterceptor());
            //.addInterceptor(new ResponseHeadersValidationInterceptor()); // TODO: Uncomment when we add a request id interceptor
        }

        /**
         * Sets the base URL for the {@link StorageBlobClient}.
         *
         * @param blobServiceUrl The blob service base URL.
         * @return An updated {@link Builder} with the provided blob service URL set.
         */
        public Builder setBlobServiceUrl(String blobServiceUrl) {
            Objects.requireNonNull(blobServiceUrl, "blobServiceUrl cannot be null.");
            this.serviceClientBuilder.setBaseUrl(blobServiceUrl);
            return this;
        }

        /**
         * Sets the service version for the {@link StorageBlobClient}.
         *
         * @param serviceVersion {@link BlobServiceVersion}
         * @return An updated {@link StorageBlobClient.Builder} with the provided blob service version set.
         */
        public StorageBlobClient.Builder setServiceVersion(BlobServiceVersion serviceVersion) {
            this.serviceVersion = serviceVersion;
            return this;
        }

        /**
         * Sets an interceptor used to authenticate the blob service request.
         *
         * @param credentialInterceptor The credential interceptor.
         * @return An updated {@link Builder} with the provided credentials interceptor set.
         */
        public Builder setCredentialInterceptor(Interceptor credentialInterceptor) {
            this.serviceClientBuilder.setCredentialsInterceptor(credentialInterceptor);
            return this;
        }

        /**
         * Builds a {@link StorageBlobClient} based on this {@link Builder}'s configuration.
         *
         * @return A {@link StorageBlobClient}.
         */
        public StorageBlobClient build() {
            BlobServiceVersion version = this.serviceVersion == null ? BlobServiceVersion.getLatest()
                : this.serviceVersion;
            StorageBlobClient client = new StorageBlobClient(this.serviceClientBuilder.build(), version.getVersion());
            return client;
        }

        private Builder(final StorageBlobClient storageBlobClient) {
            this(storageBlobClient.serviceClient.newBuilder());
        }
    }
}
