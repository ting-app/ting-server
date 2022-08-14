package ting.service;

/**
 * The access token that is needed to access azure blobs.
 */
public class AzureBlobSas {
    private String containerUrl;

    private String sas;

    public String getContainerUrl() {
        return containerUrl;
    }

    public void setContainerUrl(String containerUrl) {
        this.containerUrl = containerUrl;
    }

    public String getSas() {
        return sas;
    }

    public void setSas(String sas) {
        this.sas = sas;
    }
}
