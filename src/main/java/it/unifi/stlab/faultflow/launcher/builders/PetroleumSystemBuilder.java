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

package it.unifi.stlab.faultflow.launcher.builders;

import it.unifi.stlab.faultflow.model.knowledge.composition.ComponentType;
import it.unifi.stlab.faultflow.model.knowledge.composition.CompositionPortType;
import it.unifi.stlab.faultflow.model.knowledge.composition.SystemType;
import it.unifi.stlab.faultflow.model.knowledge.propagation.*;

import java.util.ArrayList;
import java.util.HashMap;

public class PetroleumSystemBuilder extends SystemBuilder {

    private static PetroleumSystemBuilder single_instance;

    private PetroleumSystemBuilder() {
        faultModes = new HashMap<>();
        failureModes = new HashMap<>();
        errorModes = new HashMap<>();
        propagationPortTypes = new ArrayList<>();

        // Definizione composizione del sistema

        system = new SystemType("PetroleumSystem_SYS");
        //Component petroleumSystem = new Component("PetroleumSystem");
        ComponentType gasDetectionSystem = new ComponentType("GasDetectionSystem");
        ComponentType gasDetectorA = new ComponentType("GasDetectorA");
        ComponentType gasDetectorB = new ComponentType("GasDetectorB");
        ComponentType gasDetectorC = new ComponentType("GasDetectorC");
        ComponentType initiatorA = new ComponentType("InitiatorA");
        ComponentType initiatorB = new ComponentType("InitiatorB");
        ComponentType initiatorC = new ComponentType("InitiatorC");

        system.addComponent(gasDetectionSystem,
                gasDetectorA, gasDetectorB, gasDetectorC,
                initiatorA, initiatorB, initiatorC);
        system.setTopLevelComponent(gasDetectionSystem);

        CompositionPortType cp1 = new CompositionPortType(gasDetectorA, gasDetectionSystem);
        CompositionPortType cp2 = new CompositionPortType(gasDetectorB, gasDetectionSystem);
        CompositionPortType cp3 = new CompositionPortType(gasDetectorC, gasDetectionSystem);

        gasDetectionSystem.addCompositionPorts(cp1, cp2, cp3);

        CompositionPortType cp4 = new CompositionPortType(initiatorA, gasDetectorA);
        gasDetectorA.addCompositionPorts(cp4);

        CompositionPortType cp5 = new CompositionPortType(initiatorB, gasDetectorB);
        gasDetectorB.addCompositionPorts(cp5);

        CompositionPortType cp6 = new CompositionPortType(initiatorC, gasDetectorC);
        gasDetectorC.addCompositionPorts(cp6);

        // Definizione di Fault Mode Endogeni

        InternalFaultMode enFM_F1 = new InternalFaultMode("F1");
        enFM_F1.setTimeToFaultPDF("exp(0.00004)");
        InternalFaultMode enFM_F2 = new InternalFaultMode("F2");
        enFM_F2.setTimeToFaultPDF("erlang(3,0.0002)");
        InternalFaultMode enFM_F3 = new InternalFaultMode("F3");
        enFM_F3.setTimeToFaultPDF("exp(0.0001)");
        InternalFaultMode enFM_F4 = new InternalFaultMode("F4");
        enFM_F4.setTimeToFaultPDF("erlang(2,0.0002)");
        InternalFaultMode enFM_F5 = new InternalFaultMode("F5");
        enFM_F5.setTimeToFaultPDF("exp(0.0001)");
        InternalFaultMode enFM_F6 = new InternalFaultMode("F6");
        enFM_F6.setTimeToFaultPDF("erlang(2,0.0001)");
        InternalFaultMode enFM_F7 = new InternalFaultMode("F7");
        enFM_F7.setTimeToFaultPDF("exp(0.00008)");
        InternalFaultMode enFM_F8 = new InternalFaultMode("F8");
        enFM_F8.setTimeToFaultPDF("erlang(3,0.0001)");
        InternalFaultMode enFM_F9 = new InternalFaultMode("F9");
        enFM_F9.setTimeToFaultPDF("exp(0.00006)");
        InternalFaultMode enFM_F10 = new InternalFaultMode("F10");
        enFM_F10.setTimeToFaultPDF("erlang(3,0.000015)");
        InternalFaultMode enFM_F11 = new InternalFaultMode("F11");
        enFM_F11.setTimeToFaultPDF("exp(0.0001)");
        InternalFaultMode enFM_F12 = new InternalFaultMode("F12");
        enFM_F12.setTimeToFaultPDF("erlang(2,0.000015)");

        faultModes.put(enFM_F1.getName(), enFM_F1);
        faultModes.put(enFM_F2.getName(), enFM_F2);
        faultModes.put(enFM_F3.getName(), enFM_F3);
        faultModes.put(enFM_F4.getName(), enFM_F4);
        faultModes.put(enFM_F5.getName(), enFM_F5);
        faultModes.put(enFM_F6.getName(), enFM_F6);
        faultModes.put(enFM_F7.getName(), enFM_F7);
        faultModes.put(enFM_F8.getName(), enFM_F8);
        faultModes.put(enFM_F9.getName(), enFM_F9);
        faultModes.put(enFM_F10.getName(), enFM_F10);
        faultModes.put(enFM_F11.getName(), enFM_F11);
        faultModes.put(enFM_F12.getName(), enFM_F12);

        // Definizione di Fault Mode Esogeni

        ExternalFaultMode exFM_GDAEF1 = new ExternalFaultMode("GDAEF1");
        ExternalFaultMode exFM_GDSEF1 = new ExternalFaultMode("GDSEF1");
        ExternalFaultMode exFM_GDBEF1 = new ExternalFaultMode("GDBEF1");
        ExternalFaultMode exFM_GDSEF2 = new ExternalFaultMode("GDSEF2");
        ExternalFaultMode exFM_GDCEF1 = new ExternalFaultMode("GDCEF1");
        ExternalFaultMode exFM_GDSEF3 = new ExternalFaultMode("GDSEF3");

        faultModes.put(exFM_GDAEF1.getName(), exFM_GDAEF1);
        faultModes.put(exFM_GDSEF1.getName(), exFM_GDSEF1);
        faultModes.put(exFM_GDBEF1.getName(), exFM_GDBEF1);
        faultModes.put(exFM_GDSEF2.getName(), exFM_GDSEF2);
        faultModes.put(exFM_GDCEF1.getName(), exFM_GDCEF1);
        faultModes.put(exFM_GDSEF3.getName(), exFM_GDSEF3);

        //Definizione delle Failure Mode



        FailureMode fM_IAF1 = new FailureMode("IAFailure1");
        ErrorMode eM_IAOR1 = new ErrorMode("IAOR1");
        eM_IAOR1.addInputFaultMode(enFM_F1, enFM_F2);
        eM_IAOR1.setOutgoingFailure(fM_IAF1);
        eM_IAOR1.setEnablingCondition("F1 || F2", faultModes);
        eM_IAOR1.setPDF("dirac(0)");
        errorModes.put(eM_IAOR1.getName(), eM_IAOR1);
        failureModes.put(fM_IAF1.getDescription(), fM_IAF1);

        initiatorA.addErrorMode(eM_IAOR1);

        FailureMode fM_IBF1 = new FailureMode("IBFailure1");
        ErrorMode eM_IBOR1 = new ErrorMode("IBOR1");
        eM_IBOR1.addInputFaultMode(enFM_F5, enFM_F6);
        eM_IBOR1.setOutgoingFailure(fM_IBF1);
        eM_IBOR1.setEnablingCondition("F5 || F6", faultModes);
        eM_IBOR1.setPDF("dirac(0)");
        errorModes.put(eM_IBOR1.getName(), eM_IBOR1);
        failureModes.put(fM_IBF1.getDescription(), fM_IBF1);

        initiatorB.addErrorMode(eM_IBOR1);

        FailureMode fM_ICF1 = new FailureMode("ICFailure1");
        ErrorMode eM_ICOR1 = new ErrorMode("ICOR1");
        eM_ICOR1.addInputFaultMode(enFM_F9, enFM_F10);
        eM_ICOR1.setOutgoingFailure(fM_ICF1);
        eM_ICOR1.setEnablingCondition("F9 || F10", faultModes);
        eM_ICOR1.setPDF("dirac(0)");
        errorModes.put(eM_ICOR1.getName(), eM_ICOR1);
        failureModes.put(fM_ICF1.getDescription(), fM_ICF1);

        initiatorC.addErrorMode(eM_ICOR1);

        FailureMode fM_GDAF1 = new FailureMode("GDAFailure1");
        ErrorMode eM_GDAAND1 = new ErrorMode("GDAAND1");
        eM_GDAAND1.addInputFaultMode(exFM_GDAEF1, enFM_F3, enFM_F4);
        eM_GDAAND1.setOutgoingFailure(fM_GDAF1);
        eM_GDAAND1.setEnablingCondition("GDAEF1 && (F3 || F4)", faultModes);
        eM_GDAAND1.setPDF("uniform(0,24)");
        errorModes.put(eM_GDAAND1.getName(), eM_GDAAND1);
        failureModes.put(fM_GDAF1.getDescription(), fM_GDAF1);

        gasDetectorA.addErrorMode(eM_GDAAND1);

        FailureMode fM_GDBF1 = new FailureMode("GDBFailure1");
        ErrorMode eM_GDBAND1 = new ErrorMode("GDBAND1");
        eM_GDBAND1.addInputFaultMode(exFM_GDBEF1, enFM_F7, enFM_F8);
        eM_GDBAND1.setOutgoingFailure(fM_GDBF1);
        eM_GDBAND1.setEnablingCondition("GDBEF1 && (F7 || F8)", faultModes);
        eM_GDBAND1.setPDF("uniform(0,24)");
        errorModes.put(eM_GDBAND1.getName(), eM_GDBAND1);
        failureModes.put(fM_GDBF1.getDescription(), fM_GDBF1);

        gasDetectorB.addErrorMode(eM_GDBAND1);

        FailureMode fM_GDCF1 = new FailureMode("GDCFailure1");
        ErrorMode eM_GDCAND1 = new ErrorMode("GDCAND1");
        eM_GDCAND1.addInputFaultMode(exFM_GDCEF1, enFM_F11, enFM_F12);
        eM_GDCAND1.setOutgoingFailure(fM_GDCF1);
        eM_GDCAND1.setEnablingCondition("GDCEF1 && (F11 || F12)", faultModes);
        eM_GDCAND1.setPDF("uniform(0,24)");
        errorModes.put(eM_GDCAND1.getName(), eM_GDCAND1);
        failureModes.put(fM_GDBF1.getDescription(), fM_GDBF1);

        gasDetectorC.addErrorMode(eM_GDCAND1);

        FailureMode fM_GDSF1 = new FailureMode("GDSFailure1");
        ErrorMode eM_GDSOR1 = new ErrorMode("GDSOR1");
        eM_GDSOR1.addInputFaultMode(exFM_GDSEF1, exFM_GDSEF2, exFM_GDSEF3);
        eM_GDSOR1.setOutgoingFailure(fM_GDSF1);
        eM_GDSOR1.setEnablingCondition("(GDSEF1 && GDSEF2) || GDSEF3)", faultModes);
        eM_GDSOR1.setPDF("dirac(0)");
        errorModes.put(eM_GDSOR1.getName(), eM_GDSOR1);
        failureModes.put(fM_GDSF1.getDescription(), fM_GDSF1);

        gasDetectionSystem.addErrorMode(eM_GDSOR1);

        initiatorA.addPropagationPort(
                new PropagationPortType(fM_IAF1, exFM_GDAEF1, gasDetectorA));
        propagationPortTypes.addAll(initiatorA.getPropagationPorts());
        initiatorB.addPropagationPort(
                new PropagationPortType(fM_IBF1, exFM_GDBEF1, gasDetectorB));
        propagationPortTypes.addAll(initiatorB.getPropagationPorts());
        initiatorC.addPropagationPort(
                new PropagationPortType(fM_ICF1, exFM_GDCEF1, gasDetectorC));
        propagationPortTypes.addAll(initiatorC.getPropagationPorts());
        gasDetectorA.addPropagationPort(
                new PropagationPortType(fM_GDAF1, exFM_GDSEF1, gasDetectionSystem));
        propagationPortTypes.addAll(gasDetectorA.getPropagationPorts());
        gasDetectorB.addPropagationPort(
                new PropagationPortType(fM_GDBF1, exFM_GDSEF2, gasDetectionSystem));
        propagationPortTypes.addAll(gasDetectorB.getPropagationPorts());
        gasDetectorC.addPropagationPort(
                new PropagationPortType(fM_GDCF1, exFM_GDSEF3, gasDetectionSystem));
        propagationPortTypes.addAll(gasDetectorC.getPropagationPorts());

    }



    public static PetroleumSystemBuilder getInstance() {
        if (single_instance == null)
            single_instance = new PetroleumSystemBuilder();
        return single_instance;
    }

    public SystemType getSystem() {
        return system;
    }
}
