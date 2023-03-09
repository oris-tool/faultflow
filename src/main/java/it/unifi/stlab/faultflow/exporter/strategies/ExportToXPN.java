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

package it.unifi.stlab.faultflow.exporter.strategies;

import it.unifi.stlab.faultflow.exporter.xpn.*;
import org.oristool.math.function.EXP;
import org.oristool.math.function.Erlang;
import org.oristool.math.function.GEN;
import org.oristool.models.stpn.trees.StochasticTransitionFeature;
import org.oristool.petrinet.EnablingFunction;
import org.oristool.petrinet.Marking;

/**
 * Interface that implements some useful methods to give a representation of the Model into an xml file that represent a PetriNet.
 */
public interface ExportToXPN extends ExportStrategy {
    int Y_SPACING = 90;
    int X_SPACING = 180;
    int X_START = 75;
    int Y_START = 100;

    default Arc addArc(TPNEntities tpnEntities, String from, String to) {
        ObjectFactory objectFactory = new ObjectFactory();
        Arc arc = objectFactory.createArc();
        arc.setFrom(from);
        arc.setTo(to);
        tpnEntities.getArc().add(arc);
        return arc;
    }

    default Transition addTransition(TPNEntities tpnEntities, org.oristool.petrinet.Transition transition, int x, int y) {
        ObjectFactory objectFactory = new ObjectFactory();
        Transition t = objectFactory.createTransition();
        t.setUuid(transition.getName());
        t.setX(x);
        t.setY(y);
        t.setProperties(makeTransitionProperties(t, transition));
        t.setFeatures(makeTransitionFeature(t));
        tpnEntities.getTransition().add(t);
        return t;
    }

    default Place addPlace(TPNEntities tpnEntities, org.oristool.petrinet.Place place, Marking marking, int x, int y) {
        ObjectFactory objectFactory = new ObjectFactory();
        Place p = objectFactory.createPlace();
        p.setUuid(place.getName());
        p.setX(x);
        p.setY(y);
        p.setProperties(makePlaceProperties(p, place, marking));
        tpnEntities.getPlace().add(p);
        return p;

    }

    default boolean isPlaceInXML(TPNEntities tpnEntities, String uuid) {
        return getPlace(tpnEntities, uuid) != null;
    }

    default Place getPlace(TPNEntities tpnEntities, String uuid) {
        return tpnEntities.getPlace().stream()
                .filter(place -> uuid.equals(place.getUuid()))
                .findAny()
                .orElse(null);
    }

    default boolean isTransitionInXML(TPNEntities tpnEntities, String uuid) {
        return getTransition(tpnEntities, uuid) != null;
    }

    default Transition getTransition(TPNEntities tpnEntities, String uuid) {
        return tpnEntities.getTransition().stream()
                .filter(transition -> uuid.equals(transition.getUuid()))
                .findAny()
                .orElse(null);
    }

    default void adjustPlacePosition(Place place, int newX, int newY) {
        place.setX(newX);
        place.setY(newY);
        PlaceProperty name = place.getProperties().getProperty().stream()
                                  .filter(property -> property.getId().equals("0.default.name"))
                                  .findAny()
                                  .orElse(null);
        if (name != null) {
            place.getProperties().getProperty().remove(name);
            place.getProperties().getProperty().add(makePlaceName(place));
        }
    }

    default void adjustTransitionPosition(Transition transition, int newX, int newY) {
        transition.setX(newX);
        transition.setY(newY);
        TransitionProperty name = transition.getProperties().getProperty().stream()
                                            .filter(property -> property.getId().equals("0.default.name"))
                                            .findAny()
                                            .orElse(null);
        if (name != null) {
            transition.getProperties().getProperty().remove(name);
            transition.getProperties().getProperty().add(makeTransitionName(transition));
        }

        TransitionProperty stochastic = transition.getProperties().getProperty().stream()
                .filter(property -> property.getId().equals("transition.stochastic"))
                .findAny()
                .orElse(null);
        if(stochastic != null){
            stochastic.setSatelliteX(transition.getX() + 20);
            stochastic.setSatelliteY(transition.getY() + 40);
        }
    }

    default ListTransitionFeatures makeTransitionFeature(Transition transition) {
        ObjectFactory objectFactory = new ObjectFactory();
        ListTransitionFeatures transitionFeatures = objectFactory.createListTransitionFeatures();
        TransitionFeature transitionFeature = objectFactory.createTransitionFeature();
        transitionFeature.setId("transition.stochastic");
        transitionFeatures.setFeature(transitionFeature);
        return transitionFeatures;
    }

    default ListTransitionProperty makeTransitionProperties(Transition transition, org.oristool.petrinet.Transition petriNetTransition) {
        ObjectFactory objectFactory = new ObjectFactory();
        ListTransitionProperty transitionPropertyList = objectFactory.createListTransitionProperty();
        transitionPropertyList.getProperty().add(makeTransitionName(transition));
        transitionPropertyList.getProperty().add(makeTransitionEnablingFunction(petriNetTransition));
        transitionPropertyList.getProperty().add(makeTransitionCDF(transition, petriNetTransition));
        return transitionPropertyList;

    }

    default TransitionProperty makeTransitionCDF(Transition transition, org.oristool.petrinet.Transition petriNetTransition) {
        //In this particular case only Erlang, Immediate and Deterministic CDF are supported
        ObjectFactory objectFactory = new ObjectFactory();
        TransitionProperty stochastic = objectFactory.createTransitionProperty();
        stochastic.setId("transition.stochastic");
        StochasticTransitionFeature stochasticTransitionFeature = petriNetTransition.getFeature(StochasticTransitionFeature.class);
        if (Erlang.class.equals(stochasticTransitionFeature.density().getClass())) {
            stochastic.setK(((Erlang) stochasticTransitionFeature.density()).getShape());
            stochastic.setLambda(((Erlang) stochasticTransitionFeature.density()).getLambda());
            stochastic.setPropertyDataType("4.type.erlang");
        }
        else if (GEN.class.equals(stochasticTransitionFeature.density().getClass())) {
            if(!((GEN)stochasticTransitionFeature.density()).getDensity().isConstant())
            {
                stochastic.setPropertyDataType("5.type.expolynomial");
                stochastic.setExpressions(((GEN) stochasticTransitionFeature.density()).getDensity().toString());
                stochastic.setEfts(stochasticTransitionFeature.density().getDomainsEFT().toString());
                stochastic.setLfts(stochasticTransitionFeature.density().getDomainsLFT().toString());
                //stochastic.setNormalizationFactor(stochasticTransitionFeature.density());
            }
            else {
                String domain = ((GEN) stochasticTransitionFeature.density()).getDomain().toString().replaceAll(" ", "").replace("\n", "");
                String[] bounds = domain.split("<=");
                if (bounds[0].equals(bounds[2])) {
                    if (bounds[0].equals("0"))
                        stochastic.setPropertyDataType("0.type.immediate");
                    else {
                        stochastic.setPropertyDataType("2.type.deterministic");
                        stochastic.setValue((int) Double.parseDouble(bounds[0]));
                    }
                } else {
                    stochastic.setPropertyDataType("1.type.uniform");
                    stochastic.setEft(Float.valueOf(bounds[0]));
                    stochastic.setLft(Float.valueOf(bounds[2]));
                }
            }
            stochastic.setWeight(1);
        } else if (EXP.class.equals(stochasticTransitionFeature.density().getClass())) {
            stochastic.setLambda(((EXP) stochasticTransitionFeature.density()).getLambda());
            stochastic.setPropertyDataType("3.type.exponential");
        } else
            throw new UnsupportedOperationException("This type of StochasticTransitionFeature is unsupported");
        stochastic.setSatelliteX(transition.getX() + 20);
        stochastic.setSatelliteY(transition.getY() + 40);
        return stochastic;
    }

    default TransitionProperty makeTransitionEnablingFunction(org.oristool.petrinet.Transition petriNetTransition) {
        ObjectFactory objectFactory = new ObjectFactory();
        TransitionProperty enablingFunction = objectFactory.createTransitionProperty();
        enablingFunction.setId("10.default.enablingFunction");
        EnablingFunction eF = petriNetTransition.getFeature(EnablingFunction.class);
        if (eF != null) {
            enablingFunction.setEnablingFunction(eF.toString());
        }
        return enablingFunction;
    }

    default TransitionProperty makeTransitionName(Transition transition) {
        ObjectFactory objectFactory = new ObjectFactory();
        TransitionProperty name = objectFactory.createTransitionProperty();
        name.setId("0.default.name");
        name.setName(transition.getUuid());
        name.setSatelliteX(transition.getX() + 70);
        name.setSatelliteY(transition.getY() - 25);
        return name;
    }

    default ListPlaceProperty makePlaceProperties(Place place, org.oristool.petrinet.Place petriNetPlace, Marking marking) {
        ObjectFactory objectFactory = new ObjectFactory();
        ListPlaceProperty placePropertyList = objectFactory.createListPlaceProperty();
        placePropertyList.getProperty().add(makePlaceName(place));
        placePropertyList.getProperty().add(makeMarking(petriNetPlace, marking));
        return placePropertyList;

    }

    default PlaceProperty makePlaceName(Place place) {
        ObjectFactory objectFactory = new ObjectFactory();
        PlaceProperty name = objectFactory.createPlaceProperty();
        name.setId("0.default.name");
        name.setName(place.getUuid());
        name.setSatelliteX(place.getX() + 10);
        name.setSatelliteY(place.getY() + 25);
        return name;
    }

    default PlaceProperty makeMarking(org.oristool.petrinet.Place place, Marking marking) {
        ObjectFactory objectFactory = new ObjectFactory();
        PlaceProperty marks = objectFactory.createPlaceProperty();
        marks.setId("default.marking");
        marks.setMarking(marking.getTokens(place));
        return marks;
    }
}
