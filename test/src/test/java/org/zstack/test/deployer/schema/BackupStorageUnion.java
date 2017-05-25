package org.zstack.test.deployer.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for BackupStorageUnion complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="BackupStorageUnion">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="simulatorBackupStorage" type="{http://zstack.org/schema/zstack}SimulatorBackupStorageConfig" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="sftpBackupStorage" type="{http://zstack.org/schema/zstack}SftpBackupStorageConfig" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="cephBackupStorage" type="{http://zstack.org/schema/zstack}CephBackupStorageConfig" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="imageStoreBackupStorage" type="{http://zstack.org/schema/zstack}ImageStoreBackupStorageConfig" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BackupStorageUnion", propOrder = {
        "simulatorBackupStorage",
        "sftpBackupStorage",
        "cephBackupStorage",
        "imageStoreBackupStorage"
})
public class BackupStorageUnion {

    protected List<SimulatorBackupStorageConfig> simulatorBackupStorage;
    protected List<SftpBackupStorageConfig> sftpBackupStorage;
    protected List<CephBackupStorageConfig> cephBackupStorage;
    protected List<ImageStoreBackupStorageConfig> imageStoreBackupStorage;

    /**
     * Gets the value of the simulatorBackupStorage property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the simulatorBackupStorage property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSimulatorBackupStorage().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SimulatorBackupStorageConfig }
     */
    public List<SimulatorBackupStorageConfig> getSimulatorBackupStorage() {
        if (simulatorBackupStorage == null) {
            simulatorBackupStorage = new ArrayList<SimulatorBackupStorageConfig>();
        }
        return this.simulatorBackupStorage;
    }

    /**
     * Gets the value of the sftpBackupStorage property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sftpBackupStorage property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSftpBackupStorage().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SftpBackupStorageConfig }
     */
    public List<SftpBackupStorageConfig> getSftpBackupStorage() {
        if (sftpBackupStorage == null) {
            sftpBackupStorage = new ArrayList<SftpBackupStorageConfig>();
        }
        return this.sftpBackupStorage;
    }

    /**
     * Gets the value of the cephBackupStorage property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cephBackupStorage property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCephBackupStorage().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CephBackupStorageConfig }
     */
    public List<CephBackupStorageConfig> getCephBackupStorage() {
        if (cephBackupStorage == null) {
            cephBackupStorage = new ArrayList<CephBackupStorageConfig>();
        }
        return this.cephBackupStorage;
    }

    /**
     * Gets the value of the imageStoreBackupStorage property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the imageStoreBackupStorage property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getImageStoreBackupStorage().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ImageStoreBackupStorageConfig }
     */
    public List<ImageStoreBackupStorageConfig> getImageStoreBackupStorage() {
        if (imageStoreBackupStorage == null) {
            imageStoreBackupStorage = new ArrayList<ImageStoreBackupStorageConfig>();
        }
        return this.imageStoreBackupStorage;
    }

}
