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
import it.unifi.stlab.faultflow.model.knowledge.propagation.PropagationPortType;
import it.unifi.stlab.faultflow.model.operational.Component;
import org.oristool.models.stpn.RewardRate;
import org.oristool.models.stpn.SteadyStateSolution;
import org.oristool.models.stpn.TransientSolution;
import org.oristool.models.stpn.steady.RegSteadyState;
import org.oristool.models.stpn.trans.RegTransient;
import org.oristool.models.stpn.trees.DeterministicEnablingState;
import org.oristool.petrinet.Marking;
import org.oristool.petrinet.MarkingCondition;
import org.oristool.petrinet.PetriNet;
import org.oristool.petrinet.Place;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PetriNetAnalyzer {

    private final PetriNet petriNet;
    private final Marking marking;

    public PetriNetAnalyzer(PetriNet petriNet, Marking marking) {
        this.petriNet = petriNet;
        this.marking = marking;
    }

    /**
     * Method that performs a regenerative transient analysis given a String containing one or more rewards to calculate
     * at a given time, with a precise step and error
     */
    public TransientSolution<DeterministicEnablingState, RewardRate> regenerativeTransient(String rewards, BigDecimal time,
                                                                                            BigDecimal step, BigDecimal error) {
        RewardRate[] rewardRates = TransientSolution.rewardRates(rewards);
        RegTransient.Builder builder = RegTransient.builder();
        builder.timeBound(time);
        builder.timeStep(step);
        builder.greedyPolicy(time, error);
        builder.stopOn(MarkingCondition.fromString(rewards));
        builder.markingFilter(RewardRate.nonZero(0.0, rewardRates));
        RegTransient analysis = builder.build();

        TransientSolution<DeterministicEnablingState, Marking> transientUnreliability =
                analysis.compute(petriNet, marking);

        return TransientSolution.computeRewards(false, transientUnreliability, rewardRates);
    }

    /**
     * Method that performs a regenerative transient analysis at a given time, with a precise step and error
     */
    public TransientSolution<DeterministicEnablingState, Marking> regenerativeTransient(int time, double step) {
        RegTransient analysis = RegTransient.builder()
                .timeBound(new BigDecimal(time))
                .timeStep(new BigDecimal(step))
                .build();
        return analysis.compute(petriNet, marking);
    }

    /**
     * Method that preforms a regenerative transient analysis on a set of failure modes, by also checking if they
     * propagate to exogenous fault modes (and uses their places to calculate the CDF), at a given time, with a precise
     * step and error
     */
    public Map<String, List<Double>> calculateFailureRewards(List<String> failureNames, BigDecimal time,
                                                              BigDecimal step, BigDecimal error,
                                                              Map<String, String> exoFaults) {
        Map<String, List<Double>> rewards = new HashMap<>();

        for (String failure : failureNames) {
            TransientSolution<DeterministicEnablingState, RewardRate> solution =
                    regenerativeTransient(exoFaults.getOrDefault(failure, failure), time, step, error);

            rewards.put(failure, new ArrayList<>());

            for (int index = 0; index < solution.getSolution().length; index++) {
                rewards.get(failure).add(solution.getSolution()[index][0][0]);
            }
        }

        return rewards;
    }

    public Map<Marking, BigDecimal> regenerativeSteadyState() {
        RegSteadyState analysis = RegSteadyState.builder().build();

        SteadyStateSolution<Marking> result = analysis.compute(petriNet, marking);
        return result.getSteadyState();
    }

    public static Map<String, String> createExoFaultsMap(List<Component> components, List<String> failureNames) {
        Map<String, String> exoFaults = new HashMap<>();

        for (Component component : components) {
            String serial = component.getSerial();

            for (ErrorMode errorMode : component.getComponentType().getErrorModes()) {
                String failureName = errorMode.getOutgoingFailure().getDescription().trim() + "_" + serial;
                failureNames.add(failureName);
            }

            List<PropagationPortType> propagationPortTypes = component.getComponentType().getPropagationPorts();

            for (PropagationPortType propagationPortType : propagationPortTypes) {
                String failureName = propagationPortType.getPropagatedFailureMode().getDescription().trim() + "_" + serial;
                String exoFaultName = propagationPortType.getExternalFaultMode().getName().trim() + "_" + serial;

                exoFaults.put(failureName, exoFaultName);
            }
        }

        return exoFaults;
    }

    public List<Place> getPlacesWithTokens() {
        List<Place> placesWithTokens = new ArrayList<>();

        for (Place place : petriNet.getPlaces()) {
            if (marking.getTokens(place) > 0)
                placesWithTokens.add(place);
        }

        return placesWithTokens;
    }

    public PetriNet getPetriNet() {
        return petriNet;
    }

    public Marking getMarking() {
        return marking;
    }
}
