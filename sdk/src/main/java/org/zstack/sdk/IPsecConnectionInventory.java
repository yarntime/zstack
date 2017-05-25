package org.zstack.sdk;

public class IPsecConnectionInventory  {

    public java.lang.String uuid;
    public void setUuid(java.lang.String uuid) {
        this.uuid = uuid;
    }
    public java.lang.String getUuid() {
        return this.uuid;
    }

    public java.lang.String name;
    public void setName(java.lang.String name) {
        this.name = name;
    }
    public java.lang.String getName() {
        return this.name;
    }

    public java.lang.String description;
    public void setDescription(java.lang.String description) {
        this.description = description;
    }
    public java.lang.String getDescription() {
        return this.description;
    }

    public java.lang.String l3NetworkUuid;
    public void setL3NetworkUuid(java.lang.String l3NetworkUuid) {
        this.l3NetworkUuid = l3NetworkUuid;
    }
    public java.lang.String getL3NetworkUuid() {
        return this.l3NetworkUuid;
    }

    public java.lang.String peerAddress;
    public void setPeerAddress(java.lang.String peerAddress) {
        this.peerAddress = peerAddress;
    }
    public java.lang.String getPeerAddress() {
        return this.peerAddress;
    }

    public java.lang.String authMode;
    public void setAuthMode(java.lang.String authMode) {
        this.authMode = authMode;
    }
    public java.lang.String getAuthMode() {
        return this.authMode;
    }

    public java.lang.String authKey;
    public void setAuthKey(java.lang.String authKey) {
        this.authKey = authKey;
    }
    public java.lang.String getAuthKey() {
        return this.authKey;
    }

    public java.lang.String vipUuid;
    public void setVipUuid(java.lang.String vipUuid) {
        this.vipUuid = vipUuid;
    }
    public java.lang.String getVipUuid() {
        return this.vipUuid;
    }

    public java.lang.String ikeAuthAlgorithm;
    public void setIkeAuthAlgorithm(java.lang.String ikeAuthAlgorithm) {
        this.ikeAuthAlgorithm = ikeAuthAlgorithm;
    }
    public java.lang.String getIkeAuthAlgorithm() {
        return this.ikeAuthAlgorithm;
    }

    public java.lang.String ikeEncryptionAlgorithm;
    public void setIkeEncryptionAlgorithm(java.lang.String ikeEncryptionAlgorithm) {
        this.ikeEncryptionAlgorithm = ikeEncryptionAlgorithm;
    }
    public java.lang.String getIkeEncryptionAlgorithm() {
        return this.ikeEncryptionAlgorithm;
    }

    public java.lang.Integer ikeDhGroup;
    public void setIkeDhGroup(java.lang.Integer ikeDhGroup) {
        this.ikeDhGroup = ikeDhGroup;
    }
    public java.lang.Integer getIkeDhGroup() {
        return this.ikeDhGroup;
    }

    public java.lang.String policyAuthAlgorithm;
    public void setPolicyAuthAlgorithm(java.lang.String policyAuthAlgorithm) {
        this.policyAuthAlgorithm = policyAuthAlgorithm;
    }
    public java.lang.String getPolicyAuthAlgorithm() {
        return this.policyAuthAlgorithm;
    }

    public java.lang.String policyEncryptionAlgorithm;
    public void setPolicyEncryptionAlgorithm(java.lang.String policyEncryptionAlgorithm) {
        this.policyEncryptionAlgorithm = policyEncryptionAlgorithm;
    }
    public java.lang.String getPolicyEncryptionAlgorithm() {
        return this.policyEncryptionAlgorithm;
    }

    public java.lang.String pfs;
    public void setPfs(java.lang.String pfs) {
        this.pfs = pfs;
    }
    public java.lang.String getPfs() {
        return this.pfs;
    }

    public java.lang.String policyMode;
    public void setPolicyMode(java.lang.String policyMode) {
        this.policyMode = policyMode;
    }
    public java.lang.String getPolicyMode() {
        return this.policyMode;
    }

    public java.lang.String transformProtocol;
    public void setTransformProtocol(java.lang.String transformProtocol) {
        this.transformProtocol = transformProtocol;
    }
    public java.lang.String getTransformProtocol() {
        return this.transformProtocol;
    }

    public java.lang.String state;
    public void setState(java.lang.String state) {
        this.state = state;
    }
    public java.lang.String getState() {
        return this.state;
    }

    public java.lang.String status;
    public void setStatus(java.lang.String status) {
        this.status = status;
    }
    public java.lang.String getStatus() {
        return this.status;
    }

    public java.sql.Timestamp createDate;
    public void setCreateDate(java.sql.Timestamp createDate) {
        this.createDate = createDate;
    }
    public java.sql.Timestamp getCreateDate() {
        return this.createDate;
    }

    public java.sql.Timestamp lastOpDate;
    public void setLastOpDate(java.sql.Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }
    public java.sql.Timestamp getLastOpDate() {
        return this.lastOpDate;
    }

    public java.util.List<IPsecPeerCidrInventory> peerCidrs;
    public void setPeerCidrs(java.util.List<IPsecPeerCidrInventory> peerCidrs) {
        this.peerCidrs = peerCidrs;
    }
    public java.util.List<IPsecPeerCidrInventory> getPeerCidrs() {
        return this.peerCidrs;
    }

}
