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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Classe Java per TPN-entities complex type.
 *
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 *
 * <pre>
 * &lt;complexType name="TPN-entities">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="joint" type="{http://www.oris-tool.org}Joint" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="place" type="{http://www.oris-tool.org}Place" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="resource" type="{http://www.oris-tool.org}Resource" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="transition" type="{http://www.oris-tool.org}Transition" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="note" type="{http://www.oris-tool.org}Note" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="inhibitor-arc" type="{http://www.oris-tool.org}Inhibitor-arc" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="arc" type="{http://www.oris-tool.org}Arc" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="note-connector" type="{http://www.oris-tool.org}Note-connector" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TPN-entities", namespace = "http://www.oris-tool.org", propOrder = {
        "joint",
        "place",
        "resource",
        "transition",
        "note",
        "inhibitorArc",
        "arc",
        "noteConnector"
})
public class TPNEntities {

    @XmlElement(namespace = "http://www.oris-tool.org")
    protected List<Joint> joint;
    @XmlElement(namespace = "http://www.oris-tool.org")
    protected List<Place> place;
    @XmlElement(namespace = "http://www.oris-tool.org")
    protected List<Resource> resource;
    @XmlElement(namespace = "http://www.oris-tool.org")
    protected List<Transition> transition;
    @XmlElement(namespace = "http://www.oris-tool.org")
    protected List<Note> note;
    @XmlElement(name = "inhibitor-arc", namespace = "http://www.oris-tool.org")
    protected List<InhibitorArc> inhibitorArc;
    @XmlElement(namespace = "http://www.oris-tool.org")
    protected List<Arc> arc;
    @XmlElement(name = "note-connector", namespace = "http://www.oris-tool.org")
    protected List<NoteConnector> noteConnector;

    /**
     * Gets the value of the joint property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the joint property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getJoint().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Joint }
     */
    public List<Joint> getJoint() {
        if (joint == null) {
            joint = new ArrayList<>();
        }
        return this.joint;
    }

    /**
     * Gets the value of the place property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the place property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPlace().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Place }
     */
    public List<Place> getPlace() {
        if (place == null) {
            place = new ArrayList<>();
        }
        return this.place;
    }

    /**
     * Gets the value of the resource property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the resource property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResource().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Resource }
     */
    public List<Resource> getResource() {
        if (resource == null) {
            resource = new ArrayList<>();
        }
        return this.resource;
    }

    /**
     * Gets the value of the transition property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the transition property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransition().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Transition }
     */
    public List<Transition> getTransition() {
        if (transition == null) {
            transition = new ArrayList<>();
        }
        return this.transition;
    }

    /**
     * Gets the value of the note property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the note property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNote().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Note }
     */
    public List<Note> getNote() {
        if (note == null) {
            note = new ArrayList<>();
        }
        return this.note;
    }

    /**
     * Gets the value of the inhibitorArc property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the inhibitorArc property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInhibitorArc().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InhibitorArc }
     */
    public List<InhibitorArc> getInhibitorArc() {
        if (inhibitorArc == null) {
            inhibitorArc = new ArrayList<>();
        }
        return this.inhibitorArc;
    }

    /**
     * Gets the value of the arc property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the arc property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getArc().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Arc }
     */
    public List<Arc> getArc() {
        if (arc == null) {
            arc = new ArrayList<>();
        }
        return this.arc;
    }

    /**
     * Gets the value of the noteConnector property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the noteConnector property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNoteConnector().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NoteConnector }
     */
    public List<NoteConnector> getNoteConnector() {
        if (noteConnector == null) {
            noteConnector = new ArrayList<>();
        }
        return this.noteConnector;
    }

}
