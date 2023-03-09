/*
 * This program is part of the ORIS Tool.
 * Copyright (C) 2011-2023 The ORIS Authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package it.unifi.stlab.faultflow.exporter.xpn;

import javax.xml.bind.annotation.*;


/**
 * Place element have to contains name and marking properties
 *
 * <p>Classe Java per PlaceProperty complex type.
 *
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 *
 * <pre>
 * &lt;complexType name="PlaceProperty">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.oris-tool.org}empty">
 *       &lt;attribute name="id">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="0.default.name"/>
 *             &lt;enumeration value="default.marking"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="satellite-x" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="satellite-y" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="marking" type="{http://www.w3.org/2001/XMLSchema}unsignedShort" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PlaceProperty", namespace = "http://www.oris-tool.org")
public class PlaceProperty
        extends Empty {

    @XmlAttribute(name = "id")
    protected String id;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "satellite-x")
    protected Integer satelliteX;
    @XmlAttribute(name = "satellite-y")
    protected Integer satelliteY;
    @XmlAttribute(name = "marking")
    @XmlSchemaType(name = "unsignedShort")
    protected Integer marking;

    /**
     * Recupera il valore della proprietà id.
     *
     * @return possible object is
     * {@link String }
     */
    public String getId() {
        return id;
    }

    /**
     * Imposta il valore della proprietà id.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Recupera il valore della proprietà name.
     *
     * @return possible object is
     * {@link String }
     */
    public String getName() {
        return name;
    }

    /**
     * Imposta il valore della proprietà name.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Recupera il valore della proprietà satelliteX.
     *
     * @return possible object is
     * {@link Integer }
     */
    public Integer getSatelliteX() {
        return satelliteX;
    }

    /**
     * Imposta il valore della proprietà satelliteX.
     *
     * @param value allowed object is
     *              {@link Integer }
     */
    public void setSatelliteX(Integer value) {
        this.satelliteX = value;
    }

    /**
     * Recupera il valore della proprietà satelliteY.
     *
     * @return possible object is
     * {@link Integer }
     */
    public Integer getSatelliteY() {
        return satelliteY;
    }

    /**
     * Imposta il valore della proprietà satelliteY.
     *
     * @param value allowed object is
     *              {@link Integer }
     */
    public void setSatelliteY(Integer value) {
        this.satelliteY = value;
    }

    /**
     * Recupera il valore della proprietà marking.
     *
     * @return possible object is
     * {@link Integer }
     */
    public Integer getMarking() {
        return marking;
    }

    /**
     * Imposta il valore della proprietà marking.
     *
     * @param value allowed object is
     *              {@link Integer }
     */
    public void setMarking(Integer value) {
        this.marking = value;
    }

}
