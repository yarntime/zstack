package org.zstack.test.deployer.schema;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for BackupStorageConfigBase complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="BackupStorageConfigBase">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="description" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="url" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="totalCapacity" type="{http://www.w3.org/2001/XMLSchema}string" default="1T" />
 *       &lt;attribute name="availableCapacity" type="{http://www.w3.org/2001/XMLSchema}string" default="1T" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BackupStorageConfigBase")
@XmlSeeAlso({
        SftpBackupStorageConfig.class,
        ImageStoreBackupStorageConfig.class,
        CephBackupStorageConfig.class
})
public class BackupStorageConfigBase {

    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "description")
    protected String description;
    @XmlAttribute(name = "url", required = true)
    protected String url;
    @XmlAttribute(name = "totalCapacity")
    protected String totalCapacity;
    @XmlAttribute(name = "availableCapacity")
    protected String availableCapacity;

    /**
     * Gets the value of the name property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the description property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the url property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the value of the url property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUrl(String value) {
        this.url = value;
    }

    /**
     * Gets the value of the totalCapacity property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTotalCapacity() {
        if (totalCapacity == null) {
            return "1T";
        } else {
            return totalCapacity;
        }
    }

    /**
     * Sets the value of the totalCapacity property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTotalCapacity(String value) {
        this.totalCapacity = value;
    }

    /**
     * Gets the value of the availableCapacity property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getAvailableCapacity() {
        if (availableCapacity == null) {
            return "1T";
        } else {
            return availableCapacity;
        }
    }

    /**
     * Sets the value of the availableCapacity property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setAvailableCapacity(String value) {
        this.availableCapacity = value;
    }

}
