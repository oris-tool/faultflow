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

import it.unifi.stlab.faultflow.model.knowledge.composition.Component;
import it.unifi.stlab.faultflow.model.knowledge.composition.CompositionPort;
import it.unifi.stlab.faultflow.model.knowledge.composition.System;
import it.unifi.stlab.faultflow.model.knowledge.propagation.*;

import java.util.ArrayList;
import java.util.HashMap;

public class FlightControlSystemBuilder extends SystemBuilder {
    private static FlightControlSystemBuilder single_instance;

    private FlightControlSystemBuilder() {
        faultModes = new HashMap<>();
        failureModes = new HashMap<>();
        errorModes = new HashMap<>();
        propagationPorts = new ArrayList<>();

        // Definizione composizione del sistema

        system = new System("FlightControlSystem_SYS");
        Component flightControlSystem = new Component("FlightControlSystem");
        Component leadingEdgeFlap = new Component("LeadingEdgeFlap");
        Component leftAsymmetricControlUnit = new Component("LeftAsymmetricControlUnit");
        Component rightAsymmetricControlUnit = new Component("RightAsymmetricControlUnit");
        Component leftLeadingEdgeFlap = new Component("LeftLeadingEdgeFlap");
        Component rightLeadingEdgeFlap = new Component("RightLeadingEdgeFlap");
        Component leftServoDriveUnit = new Component("LeftServoDriveUnit");
        Component rightServoDriveUnit = new Component("RightServoDriveUnit");
        Component flightControlComponentA = new Component("FlightControlComponentA");
        Component flightControlComponentB = new Component("FlightControlComponentB");
        system.addComponent(flightControlSystem,
                leadingEdgeFlap,
                leftAsymmetricControlUnit, rightAsymmetricControlUnit, leftLeadingEdgeFlap, rightLeadingEdgeFlap,
                leftServoDriveUnit, rightServoDriveUnit,
                flightControlComponentA, flightControlComponentB);
        system.setTopLevelComponent(flightControlSystem);


        CompositionPort cp1 = new CompositionPort(leadingEdgeFlap, flightControlSystem);
        flightControlSystem.addCompositionPorts(cp1);

        CompositionPort cp2 = new CompositionPort(leftAsymmetricControlUnit, leadingEdgeFlap);
        CompositionPort cp3 = new CompositionPort(rightAsymmetricControlUnit, leadingEdgeFlap);
        CompositionPort cp4 = new CompositionPort(leftLeadingEdgeFlap, leadingEdgeFlap);
        CompositionPort cp5 = new CompositionPort(rightLeadingEdgeFlap, leadingEdgeFlap);
        leadingEdgeFlap.addCompositionPorts(cp2, cp3, cp4, cp5);

        CompositionPort cp6 = new CompositionPort(leftServoDriveUnit, leftLeadingEdgeFlap);
        leftLeadingEdgeFlap.addCompositionPorts(cp6);
        CompositionPort cp7 = new CompositionPort(rightServoDriveUnit, rightLeadingEdgeFlap);
        rightLeadingEdgeFlap.addCompositionPorts(cp7);

        CompositionPort cp8 = new CompositionPort(flightControlComponentA, leftServoDriveUnit);
        leftServoDriveUnit.addCompositionPorts(cp8);
        CompositionPort cp9 = new CompositionPort(flightControlComponentB, rightServoDriveUnit);
        rightServoDriveUnit.addCompositionPorts(cp9);

        // Definizione di Fault Mode Endogeni

        InternalFaultMode enFM_F1 = new InternalFaultMode("F1");
        enFM_F1.setTimeToFaultPDF("erlang(4,0.0008)");
        InternalFaultMode enFM_F2 = new InternalFaultMode("F2");
        enFM_F2.setTimeToFaultPDF("erlang(2,0.0005)");


        //Definizione Internal Fault F3 F4 F13 F14
        InternalFaultMode enFM_F3 = new InternalFaultMode("F3");
        enFM_F3.setTimeToFaultPDF("exp(0.00005)");
        InternalFaultMode enFM_F4 = new InternalFaultMode("F4");
        enFM_F4.setTimeToFaultPDF("erlang(2,0.00005)");
        InternalFaultMode enFM_F13 = new InternalFaultMode("F13");
        enFM_F13.setTimeToFaultPDF("exp(0.0001)");
        InternalFaultMode enFM_F14 = new InternalFaultMode("F14");
        enFM_F14.setTimeToFaultPDF("erlang(4,0.0008)");
        //Fine Definizione


        InternalFaultMode enFM_F5 = new InternalFaultMode("F5");
        enFM_F5.setTimeToFaultPDF("exp(0.00004)");
        InternalFaultMode enFM_F6 = new InternalFaultMode("F6");
        enFM_F6.setTimeToFaultPDF("erlang(2,0.00004)");
        InternalFaultMode enFM_F7 = new InternalFaultMode("F7");
        enFM_F7.setTimeToFaultPDF("exp(0.0001)");
        InternalFaultMode enFM_F8 = new InternalFaultMode("F8");
        enFM_F8.setTimeToFaultPDF("erlang(2,0.00005)");
        InternalFaultMode enFM_F9 = new InternalFaultMode("F9");
        enFM_F9.setTimeToFaultPDF("erlang(4,0.0008)");
        InternalFaultMode enFM_F10 = new InternalFaultMode("F10");
        enFM_F10.setTimeToFaultPDF("erlang(2,0.0004)");
        InternalFaultMode enFM_F11 = new InternalFaultMode("F11");
        enFM_F11.setTimeToFaultPDF("exp(0.0002)");
        InternalFaultMode enFM_F12 = new InternalFaultMode("F12");
        enFM_F12.setTimeToFaultPDF("erlang(2,0.00004)");

        InternalFaultMode enFM_F15 = new InternalFaultMode("F15");
        enFM_F15.setTimeToFaultPDF("erlang(2,0.0005)");
        InternalFaultMode enFM_F16 = new InternalFaultMode("F16");
        enFM_F16.setTimeToFaultPDF("exp(0.00005)");
        InternalFaultMode enFM_F17 = new InternalFaultMode("F17");
        enFM_F17.setTimeToFaultPDF("exp(0.0002)");
        InternalFaultMode enFM_F18 = new InternalFaultMode("F18");
        enFM_F18.setTimeToFaultPDF("erlang(4,0.0005)");
        InternalFaultMode enFM_F19 = new InternalFaultMode("F19");
        enFM_F19.setTimeToFaultPDF("erlang(2,0.00004)");
        InternalFaultMode enFM_F20 = new InternalFaultMode("F20");
        enFM_F20.setTimeToFaultPDF("exp(0.00004)");
        InternalFaultMode enFM_F21 = new InternalFaultMode("F21");
        enFM_F21.setTimeToFaultPDF("exp(0.0005)");
        InternalFaultMode enFM_F22 = new InternalFaultMode("F22");
        enFM_F22.setTimeToFaultPDF("erlang(2,0.00005)");
        InternalFaultMode enFM_F23 = new InternalFaultMode("F23");
        enFM_F23.setTimeToFaultPDF("exp(0.0004)");
        InternalFaultMode enFM_F24 = new InternalFaultMode("F24");
        enFM_F24.setTimeToFaultPDF("erlang(2,0.00004)");

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
        faultModes.put(enFM_F13.getName(), enFM_F13);
        faultModes.put(enFM_F14.getName(), enFM_F14);
        faultModes.put(enFM_F15.getName(), enFM_F15);
        faultModes.put(enFM_F16.getName(), enFM_F16);
        faultModes.put(enFM_F17.getName(), enFM_F17);
        faultModes.put(enFM_F18.getName(), enFM_F18);
        faultModes.put(enFM_F19.getName(), enFM_F19);
        faultModes.put(enFM_F20.getName(), enFM_F20);
        faultModes.put(enFM_F21.getName(), enFM_F21);
        faultModes.put(enFM_F22.getName(), enFM_F22);
        faultModes.put(enFM_F23.getName(), enFM_F23);
        faultModes.put(enFM_F24.getName(), enFM_F24);

        // Definizione di Fault Mode Esogeni

        ExternalFaultMode exFM_FCCAF3 = new ExternalFaultMode("Fcca3fail");
        ExternalFaultMode exFM_FCCAF2 = new ExternalFaultMode("Fcca2fail");
        ExternalFaultMode exFM_FCCAF1 = new ExternalFaultMode("Fcca1fail");
        ExternalFaultMode exFM_FCCBF3 = new ExternalFaultMode("Fccb3fail");
        ExternalFaultMode exFM_FCCBF2 = new ExternalFaultMode("Fccb2fail");
        ExternalFaultMode exFM_FCCBF1 = new ExternalFaultMode("Fccb1fail");
        ExternalFaultMode exFM_AsymlF2 = new ExternalFaultMode("Asyml2fail");
        ExternalFaultMode exFM_AsymlF1 = new ExternalFaultMode("Asyml1fail");
        ExternalFaultMode exFM_AsymrF2 = new ExternalFaultMode("Asymr2fail");
        ExternalFaultMode exFM_AsymrF1 = new ExternalFaultMode("Asymr1fail");
        ExternalFaultMode exFM_ServlF1 = new ExternalFaultMode("Servl1fail");
        ExternalFaultMode exFM_ServrF1 = new ExternalFaultMode("Servr1fail");
        ExternalFaultMode exFM_LlefF1 = new ExternalFaultMode("Llef1fail");
        ExternalFaultMode exFM_RlefF1 = new ExternalFaultMode("Rlef1fail");


        faultModes.put(exFM_FCCAF3.getName(), exFM_FCCAF3);
        faultModes.put(exFM_FCCAF2.getName(), exFM_FCCAF2);
        faultModes.put(exFM_FCCAF1.getName(), exFM_FCCAF1);
        faultModes.put(exFM_FCCBF3.getName(), exFM_FCCBF3);
        faultModes.put(exFM_FCCBF2.getName(), exFM_FCCBF2);
        faultModes.put(exFM_FCCBF1.getName(), exFM_FCCBF1);
        faultModes.put(exFM_AsymlF2.getName(), exFM_AsymlF2);
        faultModes.put(exFM_AsymlF1.getName(), exFM_AsymlF1);
        faultModes.put(exFM_AsymrF2.getName(), exFM_AsymrF2);
        faultModes.put(exFM_AsymrF1.getName(), exFM_AsymrF1);
        faultModes.put(exFM_ServlF1.getName(), exFM_ServlF1);
        faultModes.put(exFM_ServrF1.getName(), exFM_ServrF1);
        faultModes.put(exFM_LlefF1.getName(), exFM_LlefF1);
        faultModes.put(exFM_RlefF1.getName(), exFM_RlefF1);

        // Definizione delle Failure Mode

        FailureMode fM_FCCAF1 = new FailureMode("Fcca1Failure");
        ErrorMode eM_fccaOR1 = new ErrorMode("fccaOR1");
        eM_fccaOR1.addInputFaultMode(enFM_F1, enFM_F2);
        eM_fccaOR1.setOutgoingFailure(fM_FCCAF1);
        eM_fccaOR1.setEnablingCondition("F1 || F2", faultModes);
        eM_fccaOR1.setPDF("dirac(0)");
        errorModes.put(eM_fccaOR1.getName(), eM_fccaOR1);
        failureModes.put(fM_FCCAF1.getDescription(), fM_FCCAF1);

        flightControlComponentA.addErrorMode(eM_fccaOR1);

        //Definizione di FCCA Failure 2

        FailureMode fM_FCCAF2 = new FailureMode("Fcca2Failure");
        ErrorMode eM_fccaAND1 = new ErrorMode("fccaAND1");
        eM_fccaAND1.addInputFaultMode(enFM_F3, enFM_F4);
        eM_fccaAND1.setOutgoingFailure(fM_FCCAF2);
        eM_fccaAND1.setEnablingCondition("F3 && F4", faultModes);
        eM_fccaAND1.setPDF("dirac(0)");
        errorModes.put(eM_fccaAND1.getName(), eM_fccaAND1);
        failureModes.put(fM_FCCAF2.getDescription(), fM_FCCAF2);

        flightControlComponentA.addErrorMode(eM_fccaAND1);

        //Fine Definizione


        FailureMode fM_FCCAF3 = new FailureMode("Fcca3Failure");
        ErrorMode eM_fccaAND2 = new ErrorMode("fccaAND2");
        eM_fccaAND2.addInputFaultMode(enFM_F5, enFM_F6);
        eM_fccaAND2.setOutgoingFailure(fM_FCCAF3);
        eM_fccaAND2.setEnablingCondition("F5 && F6", faultModes);
        eM_fccaAND2.setPDF("dirac(0)");
        errorModes.put(eM_fccaAND2.getName(), eM_fccaAND2);
        failureModes.put(fM_FCCAF3.getDescription(), fM_FCCAF3);

        flightControlComponentA.addErrorMode(eM_fccaAND2);

        FailureMode fM_FCCBF2 = new FailureMode("Fccb2Failure");
        ErrorMode eM_fccbOR1 = new ErrorMode("fccbOR1");
        eM_fccbOR1.addInputFaultMode(enFM_F9, enFM_F10);
        eM_fccbOR1.setOutgoingFailure(fM_FCCBF2);
        eM_fccbOR1.setEnablingCondition("F9 || F10", faultModes);
        eM_fccbOR1.setPDF("dirac(0)");
        errorModes.put(eM_fccbOR1.getName(), eM_fccbOR1);
        failureModes.put(fM_FCCBF2.getDescription(), fM_FCCBF2);

        flightControlComponentB.addErrorMode(eM_fccbOR1);

        FailureMode fM_FCCBF1 = new FailureMode("Fccb1Failure");
        ErrorMode eM_fccbAND1 = new ErrorMode("fccbAND1");
        eM_fccbAND1.addInputFaultMode(enFM_F7, enFM_F8);
        eM_fccbAND1.setOutgoingFailure(fM_FCCBF1);
        eM_fccbAND1.setEnablingCondition("F7 && F8", faultModes);
        eM_fccbAND1.setPDF("dirac(0)");
        errorModes.put(eM_fccbAND1.getName(), eM_fccbAND1);
        failureModes.put(fM_FCCBF1.getDescription(), fM_FCCBF1);

        flightControlComponentB.addErrorMode(eM_fccbAND1);

        FailureMode fM_FCCBF3 = new FailureMode("Fccb3Failure");
        ErrorMode eM_fccbAND2 = new ErrorMode("fccbAND2");
        eM_fccbAND2.addInputFaultMode(enFM_F11, enFM_F12);
        eM_fccbAND2.setOutgoingFailure(fM_FCCBF3);
        eM_fccbAND2.setEnablingCondition("F11 && F12", faultModes);
        eM_fccbAND2.setPDF("dirac(0)");
        errorModes.put(eM_fccbAND2.getName(), eM_fccbAND2);
        failureModes.put(fM_FCCBF3.getDescription(), fM_FCCBF3);

        flightControlComponentB.addErrorMode(eM_fccbAND2);

        //Definizione di Asyml Failure 1

        FailureMode fM_Asyml1F = new FailureMode("Asyml1Failure");
        ErrorMode eM_asymlOR1 = new ErrorMode("asymlOR1");
        eM_asymlOR1.addInputFaultMode(enFM_F13, enFM_F14, exFM_FCCAF2);
        eM_asymlOR1.setOutgoingFailure(fM_Asyml1F);
        eM_asymlOR1.setEnablingCondition("(F13 && F14) || Fcca2fail", faultModes);
        eM_asymlOR1.setPDF("dirac(0)");
        errorModes.put(eM_asymlOR1.getName(), eM_asymlOR1);
        failureModes.put(fM_Asyml1F.getDescription(), fM_Asyml1F);

        leftAsymmetricControlUnit.addErrorMode(eM_asymlOR1);

        //Fine Definizione

        FailureMode fM_Asyml2F = new FailureMode("Asyml2Failure");
        ErrorMode eM_asymlOR2 = new ErrorMode("asymlOR2");
        eM_asymlOR2.addInputFaultMode(enFM_F15, enFM_F16, exFM_FCCBF1);
        eM_asymlOR2.setOutgoingFailure(fM_Asyml2F);
        eM_asymlOR2.setEnablingCondition("(F15 && F16) || Fccb1fail", faultModes);
        eM_asymlOR2.setPDF("dirac(0)");
        errorModes.put(eM_asymlOR2.getName(), eM_asymlOR2);
        failureModes.put(fM_Asyml2F.getDescription(), fM_Asyml2F);

        leftAsymmetricControlUnit.addErrorMode(eM_asymlOR2);


        FailureMode fM_Asymr1F = new FailureMode("Asymr1Failure");
        ErrorMode eM_asymrOR1 = new ErrorMode("asymrOR1");
        eM_asymrOR1.addInputFaultMode(enFM_F17, enFM_F18, exFM_FCCAF3);
        eM_asymrOR1.setOutgoingFailure(fM_Asymr1F);
        eM_asymrOR1.setEnablingCondition("(F17 && F18) || Fcca3fail", faultModes);
        eM_asymrOR1.setPDF("dirac(0)");
        errorModes.put(eM_asymrOR1.getName(), eM_asymrOR1);
        failureModes.put(fM_Asymr1F.getDescription(), fM_Asymr1F);

        rightAsymmetricControlUnit.addErrorMode(eM_asymrOR1);

        FailureMode fM_Asymr2F = new FailureMode("Asymr2Failure");
        ErrorMode eM_asymrOR2 = new ErrorMode("asymrOR2");
        eM_asymrOR2.addInputFaultMode(enFM_F19, enFM_F20, exFM_FCCBF3);
        eM_asymrOR2.setOutgoingFailure(fM_Asymr2F);
        eM_asymrOR2.setEnablingCondition("(F19 && F20) || Fccb3fail", faultModes);
        eM_asymrOR2.setPDF("dirac(0)");
        errorModes.put(eM_asymrOR2.getName(), eM_asymrOR2);
        failureModes.put(fM_Asymr2F.getDescription(), fM_Asymr2F);

        rightAsymmetricControlUnit.addErrorMode(eM_asymrOR2);

        FailureMode fM_ServlF = new FailureMode("Servl1Failure");
        ErrorMode eM_servlAND1 = new ErrorMode("servlAND1");
        eM_servlAND1.addInputFaultMode(enFM_F21, enFM_F22, exFM_FCCAF1);
        eM_servlAND1.setOutgoingFailure(fM_ServlF);
        eM_servlAND1.setEnablingCondition("F21 && F22 && Fcca1fail", faultModes);
        eM_servlAND1.setPDF("dirac(0)");
        errorModes.put(eM_servlAND1.getName(), eM_servlAND1);
        failureModes.put(fM_ServlF.getDescription(), fM_ServlF);

        leftServoDriveUnit.addErrorMode(eM_servlAND1);

        FailureMode fM_ServrF = new FailureMode("Servr1Failure");
        ErrorMode eM_servrAND1 = new ErrorMode("servrAND1");
        eM_servrAND1.addInputFaultMode(enFM_F23, enFM_F24, exFM_FCCBF2);
        eM_servrAND1.setOutgoingFailure(fM_ServrF);
        eM_servrAND1.setEnablingCondition("F23 && F24 && Fccb2fail", faultModes);
        eM_servrAND1.setPDF("dirac(0)");
        errorModes.put(eM_servrAND1.getName(), eM_servrAND1);
        failureModes.put(fM_ServrF.getDescription(), fM_ServrF);

        rightServoDriveUnit.addErrorMode(eM_servrAND1);

        FailureMode fM_LlefF = new FailureMode("Llef1Failure");
        ErrorMode eM_llefAND1 = new ErrorMode("llefAND1");
        eM_llefAND1.addInputFaultMode(exFM_ServlF1, exFM_AsymlF1, exFM_AsymlF2);
        eM_llefAND1.setOutgoingFailure(fM_LlefF);
        eM_llefAND1.setEnablingCondition("Servl1fail && (Asyml1fail || Asyml2fail)", faultModes);
        eM_llefAND1.setPDF("dirac(0)");
        errorModes.put(eM_llefAND1.getName(), eM_llefAND1);
        failureModes.put(fM_LlefF.getDescription(), fM_LlefF);

        leftLeadingEdgeFlap.addErrorMode(eM_llefAND1);

        FailureMode fM_RlefF = new FailureMode("Rlef1Failure");
        ErrorMode eM_rlefAND1 = new ErrorMode("rlefAND1");
        eM_rlefAND1.addInputFaultMode(exFM_ServrF1, exFM_AsymrF1, exFM_AsymlF1);
        eM_rlefAND1.setOutgoingFailure(fM_RlefF);
        eM_rlefAND1.setEnablingCondition("Servr1fail && (Asymr1fail && Asymr2fail)", faultModes);
        eM_rlefAND1.setPDF("dirac(0)");
        errorModes.put(eM_rlefAND1.getName(), eM_rlefAND1);
        failureModes.put(fM_RlefF.getDescription(), fM_RlefF);

        rightLeadingEdgeFlap.addErrorMode(eM_rlefAND1);

        FailureMode fM_lefF = new FailureMode("Lef1Failure");
        ErrorMode eM_lefOR1 = new ErrorMode("lefOR1");
        eM_lefOR1.addInputFaultMode(exFM_LlefF1, exFM_RlefF1);
        eM_lefOR1.setOutgoingFailure(fM_lefF);
        eM_lefOR1.setEnablingCondition("Llef1fail || Rlef1fail", faultModes);
        eM_lefOR1.setPDF("dirac(0)");
        errorModes.put(eM_lefOR1.getName(), eM_lefOR1);
        failureModes.put(fM_lefF.getDescription(), fM_lefF);

        leadingEdgeFlap.addErrorMode(eM_lefOR1);


        flightControlComponentA.addPropagationPort(
                new PropagationPort(fM_FCCAF1, exFM_FCCAF1, leftServoDriveUnit));
        flightControlComponentA.addPropagationPort(
                new PropagationPort(fM_FCCAF2, exFM_FCCAF2, leftAsymmetricControlUnit));
        flightControlComponentA.addPropagationPort(
                new PropagationPort(fM_FCCAF3, exFM_FCCAF3, rightAsymmetricControlUnit));
        propagationPorts.addAll(flightControlComponentA.getPropagationPorts());

        flightControlComponentB.addPropagationPort(
                new PropagationPort(fM_FCCBF1, exFM_FCCBF1, leftAsymmetricControlUnit));
        flightControlComponentB.addPropagationPort(
                new PropagationPort(fM_FCCBF2, exFM_FCCBF2, rightServoDriveUnit));
        flightControlComponentB.addPropagationPort(
                new PropagationPort(fM_FCCBF3, exFM_FCCBF3, rightAsymmetricControlUnit));
        propagationPorts.addAll(flightControlComponentB.getPropagationPorts());


        leftAsymmetricControlUnit.addPropagationPort(
                new PropagationPort(fM_Asyml1F, exFM_AsymlF1, leftLeadingEdgeFlap)
        );
        leftAsymmetricControlUnit.addPropagationPort(
                new PropagationPort(fM_Asyml2F, exFM_AsymlF2, leftLeadingEdgeFlap)
        );
        propagationPorts.addAll(leftAsymmetricControlUnit.getPropagationPorts());


        rightAsymmetricControlUnit.addPropagationPort(
                new PropagationPort(fM_Asymr1F, exFM_AsymrF1, rightLeadingEdgeFlap)
        );
        rightAsymmetricControlUnit.addPropagationPort(
                new PropagationPort(fM_Asymr2F, exFM_AsymrF2, rightLeadingEdgeFlap)
        );
        propagationPorts.addAll(rightAsymmetricControlUnit.getPropagationPorts());


        leftServoDriveUnit.addPropagationPort(
                new PropagationPort(fM_ServlF, exFM_ServlF1, leftLeadingEdgeFlap)
        );
        propagationPorts.addAll(leftServoDriveUnit.getPropagationPorts());
        rightServoDriveUnit.addPropagationPort(
                new PropagationPort(fM_ServrF, exFM_ServrF1, rightLeadingEdgeFlap)
        );
        propagationPorts.addAll(rightServoDriveUnit.getPropagationPorts());


        leftLeadingEdgeFlap.addPropagationPort(
                new PropagationPort(fM_LlefF, exFM_LlefF1, leadingEdgeFlap)
        );
        propagationPorts.addAll(leftLeadingEdgeFlap.getPropagationPorts());

        rightLeadingEdgeFlap.addPropagationPort(
                new PropagationPort(fM_RlefF, exFM_RlefF1, leadingEdgeFlap)
        );
        propagationPorts.addAll(rightLeadingEdgeFlap.getPropagationPorts());

    }

    public static FlightControlSystemBuilder getInstance() {
        if (single_instance == null)
            single_instance = new FlightControlSystemBuilder();
        return single_instance;
    }}
