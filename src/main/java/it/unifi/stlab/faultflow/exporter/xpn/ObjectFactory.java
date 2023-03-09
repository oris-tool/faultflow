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

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the JavaToXpn package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: JavaToXpn
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TpnEditor }
     */
    public TpnEditor createTpnEditor() {
        return new TpnEditor();
    }

    /**
     * Create an instance of {@link TPNEntities }
     */
    public TPNEntities createTPNEntities() {
        return new TPNEntities();
    }

    /**
     * Create an instance of {@link ListResourceProperty }
     */
    public ListResourceProperty createListResourceProperty() {
        return new ListResourceProperty();
    }

    /**
     * Create an instance of {@link TransitionFeature }
     */
    public TransitionFeature createTransitionFeature() {
        return new TransitionFeature();
    }

    /**
     * Create an instance of {@link PlaceProperty }
     */
    public PlaceProperty createPlaceProperty() {
        return new PlaceProperty();
    }

    /**
     * Create an instance of {@link Resource }
     */
    public Resource createResource() {
        return new Resource();
    }

    /**
     * Create an instance of {@link ListTransitionFeatures }
     */
    public ListTransitionFeatures createListTransitionFeatures() {
        return new ListTransitionFeatures();
    }

    /**
     * Create an instance of {@link TransitionProperty }
     */
    public TransitionProperty createTransitionProperty() {
        return new TransitionProperty();
    }

    /**
     * Create an instance of {@link Joint }
     */
    public Joint createJoint() {
        return new Joint();
    }

    /**
     * Create an instance of {@link NoteConnector }
     */
    public NoteConnector createNoteConnector() {
        return new NoteConnector();
    }

    /**
     * Create an instance of {@link InhibitorArc }
     */
    public InhibitorArc createInhibitorArc() {
        return new InhibitorArc();
    }

    /**
     * Create an instance of {@link Empty }
     */
    public Empty createEmpty() {
        return new Empty();
    }

    /**
     * Create an instance of {@link ResourceProperty }
     */
    public ResourceProperty createResourceProperty() {
        return new ResourceProperty();
    }

    /**
     * Create an instance of {@link ListPlaceProperty }
     */
    public ListPlaceProperty createListPlaceProperty() {
        return new ListPlaceProperty();
    }

    /**
     * Create an instance of {@link ListTransitionProperty }
     */
    public ListTransitionProperty createListTransitionProperty() {
        return new ListTransitionProperty();
    }

    /**
     * Create an instance of {@link Arc }
     */
    public Arc createArc() {
        return new Arc();
    }

    /**
     * Create an instance of {@link Note }
     */
    public Note createNote() {
        return new Note();
    }

    /**
     * Create an instance of {@link Transition }
     */
    public Transition createTransition() {
        return new Transition();
    }

    /**
     * Create an instance of {@link Place }
     */
    public Place createPlace() {
        return new Place();
    }

}
