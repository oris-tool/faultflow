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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;


/**
 * Every transition element have to contains name, enabling function, marking update and reset transition properties.
 * Stochastic, timed and preemptive transition element have to contains (respectively) stochastic, timed and preemptive properties
 *
 * <p>Classe Java per TransitionProperty complex type.
 *
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 *
 * <pre>
 * &lt;complexType name="TransitionProperty">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.oris-tool.org}empty">
 *       &lt;attribute name="id" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="0.default.name"/>
 *             &lt;enumeration value="10.default.enablingFunction"/>
 *             &lt;enumeration value="11.default.markingUpdate"/>
 *             &lt;enumeration value="12.default.resetTransitions"/>
 *             &lt;enumeration value="transition.stochastic"/>
 *             &lt;enumeration value="transition.timed"/>
 *             &lt;enumeration value="transition.preemptive"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="satellite-x" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="satellite-y" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="enabling-function" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="marking-update" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="reset-transitions" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="property-data-type">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="0.type.immediate"/>
 *             &lt;enumeration value="1.type.uniform"/>
 *             &lt;enumeration value="2.type.deterministic"/>
 *             &lt;enumeration value="3.type.exponential"/>
 *             &lt;enumeration value="4.type.erlang"/>
 *             &lt;enumeration value="5.type.expolynomial"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="priority" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="weight" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="eft" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="lft" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="lambda" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="k" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="efts" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="lfts" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="expressions" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="normalizationFactor" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="resource" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransitionProperty", namespace = "http://www.oris-tool.org")
public class TransitionProperty
        extends Empty {

    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "satellite-x")
    protected Integer satelliteX;
    @XmlAttribute(name = "satellite-y")
    protected Integer satelliteY;
    @XmlAttribute(name = "enabling-function")
    protected String enablingFunction;
    @XmlAttribute(name = "marking-update")
    protected String markingUpdate;
    @XmlAttribute(name = "reset-transitions")
    protected String resetTransitions;
    @XmlAttribute(name = "property-data-type")
    protected String propertyDataType;
    @XmlAttribute(name = "priority")
    protected Integer priority;
    @XmlAttribute(name = "weight")
    protected Integer weight;
    @XmlAttribute(name = "eft")
    protected Float eft;
    @XmlAttribute(name = "lft")
    protected Float lft;
    @XmlAttribute(name = "value")
    protected Integer value;
    @XmlAttribute(name = "lambda")
    protected BigDecimal lambda;
    @XmlAttribute(name = "k")
    protected Integer k;
    @XmlAttribute(name = "efts")
    protected String efts;
    @XmlAttribute(name = "lfts")
    protected String lfts;
    @XmlAttribute(name = "expressions")
    protected String expressions;
    @XmlAttribute(name = "normalizationFactor")
    protected Float normalizationFactor;
    @XmlAttribute(name = "resource")
    protected String resource;

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
     * Recupera il valore della proprietà enablingFunction.
     *
     * @return possible object is
     * {@link String }
     */
    public String getEnablingFunction() {
        return enablingFunction;
    }

    /**
     * Imposta il valore della proprietà enablingFunction.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setEnablingFunction(String value) {
        this.enablingFunction = value;
    }

    /**
     * Recupera il valore della proprietà markingUpdate.
     *
     * @return possible object is
     * {@link String }
     */
    public String getMarkingUpdate() {
        return markingUpdate;
    }

    /**
     * Imposta il valore della proprietà markingUpdate.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMarkingUpdate(String value) {
        this.markingUpdate = value;
    }

    /**
     * Recupera il valore della proprietà resetTransitions.
     *
     * @return possible object is
     * {@link String }
     */
    public String getResetTransitions() {
        return resetTransitions;
    }

    /**
     * Imposta il valore della proprietà resetTransitions.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setResetTransitions(String value) {
        this.resetTransitions = value;
    }

    /**
     * Recupera il valore della proprietà propertyDataType.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPropertyDataType() {
        return propertyDataType;
    }

    /**
     * Imposta il valore della proprietà propertyDataType.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPropertyDataType(String value) {
        this.propertyDataType = value;
    }

    /**
     * Recupera il valore della proprietà priority.
     *
     * @return possible object is
     * {@link Integer }
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * Imposta il valore della proprietà priority.
     *
     * @param value allowed object is
     *              {@link Integer }
     */
    public void setPriority(Integer value) {
        this.priority = value;
    }

    /**
     * Recupera il valore della proprietà weight.
     *
     * @return possible object is
     * {@link Integer }
     */
    public Integer getWeight() {
        return weight;
    }

    /**
     * Imposta il valore della proprietà weight.
     *
     * @param value allowed object is
     *              {@link Integer }
     */
    public void setWeight(Integer value) {
        this.weight = value;
    }

    /**
     * Recupera il valore della proprietà eft.
     *
     * @return possible object is
     * {@link Float }
     */
    public Float getEft() {
        return eft;
    }

    /**
     * Imposta il valore della proprietà eft.
     *
     * @param value allowed object is
     *              {@link Float }
     */
    public void setEft(Float value) {
        this.eft = value;
    }

    /**
     * Recupera il valore della proprietà lft.
     *
     * @return possible object is
     * {@link Float }
     */
    public Float getLft() {
        return lft;
    }

    /**
     * Imposta il valore della proprietà lft.
     *
     * @param value allowed object is
     *              {@link Float }
     */
    public void setLft(Float value) {
        this.lft = value;
    }

    /**
     * Recupera il valore della proprietà value.
     *
     * @return possible object is
     * {@link Integer }
     */
    public Integer getValue() {
        return value;
    }

    /**
     * Imposta il valore della proprietà value.
     *
     * @param value allowed object is
     *              {@link Integer }
     */
    public void setValue(Integer value) {
        this.value = value;
    }

    /**
     * Recupera il valore della proprietà lambda.
     *
     * @return possible object is
     * {@link Integer }
     */
    public BigDecimal getLambda() {
        return lambda;
    }

    /**
     * Imposta il valore della proprietà lambda.
     *
     * @param value allowed object is
     *              {@link Integer }
     */
    public void setLambda(BigDecimal value) {
        this.lambda = value;
    }

    /**
     * Recupera il valore della proprietà k.
     *
     * @return possible object is
     * {@link Integer }
     */
    public Integer getK() {
        return k;
    }

    /**
     * Imposta il valore della proprietà k.
     *
     * @param value allowed object is
     *              {@link Integer }
     */
    public void setK(Integer value) {
        this.k = value;
    }

    /**
     * Recupera il valore della proprietà efts.
     *
     * @return possible object is
     * {@link String }
     */
    public String getEfts() {
        return efts;
    }

    /**
     * Imposta il valore della proprietà efts.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setEfts(String value) {
        this.efts = value;
    }

    /**
     * Recupera il valore della proprietà lfts.
     *
     * @return possible object is
     * {@link String }
     */
    public String getLfts() {
        return lfts;
    }

    /**
     * Imposta il valore della proprietà lfts.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setLfts(String value) {
        this.lfts = value;
    }

    /**
     * Recupera il valore della proprietà expressions.
     *
     * @return possible object is
     * {@link String }
     */
    public String getExpressions() {
        return expressions;
    }

    /**
     * Imposta il valore della proprietà expressions.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setExpressions(String value) {
        this.expressions = value;
    }

    /**
     * Recupera il valore della proprietà normalizationFactor.
     *
     * @return possible object is
     * {@link Float }
     */
    public Float getNormalizationFactor() {
        return normalizationFactor;
    }

    /**
     * Imposta il valore della proprietà normalizationFactor.
     *
     * @param value allowed object is
     *              {@link Float }
     */
    public void setNormalizationFactor(Float value) {
        this.normalizationFactor = value;
    }

    /**
     * Recupera il valore della proprietà resource.
     *
     * @return possible object is
     * {@link String }
     */
    public String getResource() {
        return resource;
    }

    /**
     * Imposta il valore della proprietà resource.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setResource(String value) {
        this.resource = value;
    }

}
