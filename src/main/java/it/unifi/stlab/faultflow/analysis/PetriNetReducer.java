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

package it.unifi.stlab.faultflow.analysis;

import it.unifi.stlab.faultflow.model.knowledge.propagation.ErrorMode;
import it.unifi.stlab.faultflow.model.knowledge.propagation.FaultMode;
import it.unifi.stlab.faultflow.model.knowledge.propagation.InternalFaultMode;
import it.unifi.stlab.faultflow.model.knowledge.propagation.PropagationPort;
import org.oristool.petrinet.Marking;
import org.oristool.petrinet.PetriNet;
import org.oristool.petrinet.Place;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PetriNetReducer {

    private final PetriNet petriNet;
    private final Marking marking;

    public PetriNetReducer(PetriNet petriNet, Marking marking) {
        this.petriNet = petriNet;
        this.marking = new Marking(marking);
    }

    public void reduce(String failureModeName, List<PropagationPort> propagationPorts, List<ErrorMode> errorModes) {
        List<String> contributingPlaces = getThreatChainEntityNames(failureModeName, propagationPorts, errorModes);
        List<Place> placesWithTokens = petriNet.getPlaces()
                .stream()
                .filter(place -> marking.getTokens(place.getName()) > 0).collect(Collectors.toList());

        for (Place netPlace : placesWithTokens) {
            if (contributingPlaces.stream().noneMatch(place -> netPlace.getName().contains(place))) {
                marking.setTokens(netPlace, 0);
            }
        }
    }

    private List<String> getThreatChainEntityNames(String  failureModeName, List<PropagationPort> propagationPorts, List<ErrorMode> errorModes) {
        List<String> contributingPlaces = new ArrayList<>();

        for(ErrorMode errorMode: errorModes) {
            if (errorMode.getOutgoingFailure().getDescription().equals(failureModeName)) {
                contributingPlaces.add(errorMode.getName().trim());
                for (FaultMode faultMode : errorMode.getInputFaultModes()) {
                    if (faultMode instanceof InternalFaultMode) {
                        contributingPlaces.add(faultMode.getName().trim());
                    } else {
                        for(PropagationPort pp : propagationPorts){
                            if(pp.getExternalFaultMode().equals(faultMode)){
                                contributingPlaces.addAll(getThreatChainEntityNames(pp.getPropagatedFailureMode().getDescription(), propagationPorts, errorModes));
                            }
                        }
                    }
                }
            }
        }
        return contributingPlaces;
    }

    public PetriNet getPetriNet() {
        return petriNet;
    }

    public Marking getMarking() {
        return marking;
    }
}
