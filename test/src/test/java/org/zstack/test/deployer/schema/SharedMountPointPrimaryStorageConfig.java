package org.zstack.test.deployer.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SharedMountPointPrimaryStorageConfig complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="SharedMountPointPrimaryStorageConfig">
 *   &lt;complexContent>
 *     &lt;extension base="{http://zstack.org/schema/zstack}PrimaryStorageConfigBase">
 *       &lt;attribute name="placeHolder" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SharedMountPointPrimaryStorageConfig")
public class SharedMountPointPrimaryStorageConfig
        extends PrimaryStorageConfigBase {

    @XmlAttribute(name = "placeHolder")
    protected String placeHolder;

    /**
     * Gets the value of the placeHolder property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPlaceHolder() {
        return placeHolder;
    }

    /**
     * Sets the value of the placeHolder property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPlaceHolder(String value) {
        this.placeHolder = value;
    }

}
