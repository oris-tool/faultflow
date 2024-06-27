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

public class PressureTankSystemBuilder extends SystemBuilder {

    private static PressureTankSystemBuilder single_instance;

    private PressureTankSystemBuilder() {
        faultModes = new HashMap<>();
        failureModes = new HashMap<>();
        errorModes = new HashMap<>();
        propagationPortTypes = new ArrayList<>();

        // Definizione composizione del sistema

        system = new SystemType("PressureTankSystem_SYS");
        //Component petroleumSystem = new Component("PetroleumSystem");
        ComponentType pressureTankSystem = new ComponentType("PressureTankSystem");
        ComponentType relayK1 = new ComponentType("RelayK1");
        ComponentType relayK2 = new ComponentType("RelayK2");
        ComponentType timerRelay = new ComponentType("TimerRelay");
        ComponentType pressureSwitch = new ComponentType("PressureSwitch");
        ComponentType switchS1 = new ComponentType("SwitchS1");

        system.addComponent(pressureTankSystem,
                relayK2, pressureSwitch, switchS1,
                relayK1, timerRelay);
        system.setTopLevelComponent(pressureTankSystem);

        CompositionPortType cp1 = new CompositionPortType(relayK2, pressureTankSystem);
        CompositionPortType cp2 = new CompositionPortType(pressureSwitch, pressureTankSystem);
        CompositionPortType cp3 = new CompositionPortType(switchS1, pressureTankSystem);

        pressureTankSystem.addCompositionPorts(cp1, cp2, cp3);

        CompositionPortType cp4 = new CompositionPortType(switchS1, pressureSwitch);
        pressureSwitch.addCompositionPorts(cp4);

        CompositionPortType cp5 = new CompositionPortType(relayK1, pressureSwitch);
        pressureSwitch.addCompositionPorts(cp5);

        CompositionPortType cp6 = new CompositionPortType(timerRelay, pressureSwitch);
        pressureSwitch.addCompositionPorts(cp6);

        // Definizione di Fault Mode Endogeni

        InternalFaultMode enFM_K1 = new InternalFaultMode("K1");
        enFM_K1.setTimeToFaultPDF("expoly(Exp[-3.82 x] * 14.5924 * x,0,inf)");
        InternalFaultMode enFM_R = new InternalFaultMode("R");
        enFM_R.setTimeToFaultPDF("expoly(Exp[-3.82 x] * 14.5924 * x,0,inf)");
        InternalFaultMode enFM_S1 = new InternalFaultMode("S1");
        enFM_S1.setTimeToFaultPDF("expoly(Exp[-0.8 x] * 0.64 * x,0,inf)");
        InternalFaultMode enFM_S = new InternalFaultMode("S");
        enFM_S.setTimeToFaultPDF("expoly(Exp[-3.4 x] * 11.56 * x,0,inf)");
        InternalFaultMode enFM_K2 = new InternalFaultMode("K2");
        enFM_K2.setTimeToFaultPDF("expoly(Exp[-3.82 x] * 14.5924 * x,0,inf)");
        InternalFaultMode enFM_T = new InternalFaultMode("T");
        enFM_T.setTimeToFaultPDF("expoly(Exp[-0.0218 x] * 0.00047524 * x,0,inf)");

        faultModes.put(enFM_K1.getName(), enFM_K1);
        faultModes.put(enFM_R.getName(), enFM_R);
        faultModes.put(enFM_S1.getName(), enFM_S1);
        faultModes.put(enFM_S.getName(), enFM_S);
        faultModes.put(enFM_K2.getName(), enFM_K2);
        faultModes.put(enFM_T.getName(), enFM_T);

        // Definizione di Fault Mode Esogeni

        ExternalFaultMode exFM_E5 = new ExternalFaultMode("E5");
        ExternalFaultMode exFM_E4 = new ExternalFaultMode("E4");
        ExternalFaultMode exFM_E3 = new ExternalFaultMode("E3");
        ExternalFaultMode exFM_E2 = new ExternalFaultMode("E2");

        faultModes.put(exFM_E5.getName(), exFM_E5);
        faultModes.put(exFM_E4.getName(), exFM_E4);
        faultModes.put(exFM_E3.getName(), exFM_E3);
        faultModes.put(exFM_E2.getName(), exFM_E2);

        //Definizione delle Failure Mode



        FailureMode fM_G104 = new FailureMode("G104Failure");
        ErrorMode eM_K1ROR1 = new ErrorMode("K1ROR1");
        eM_K1ROR1.addInputFaultMode(enFM_K1, enFM_R);
        eM_K1ROR1.setOutgoingFailure(fM_G104);
        eM_K1ROR1.setEnablingCondition("K1 || R", faultModes);
        eM_K1ROR1.setPDF("dirac(0)");
        errorModes.put(eM_K1ROR1.getName(), eM_K1ROR1);
        failureModes.put(fM_G104.getDescription(), fM_G104);

        relayK1.addErrorMode(eM_K1ROR1);

        FailureMode fM_G103 = new FailureMode("G103Failure1");
        ErrorMode eM_S1E5OR1 = new ErrorMode("S1E5OR1");
        eM_S1E5OR1.addInputFaultMode(enFM_S1, exFM_E5);
        eM_S1E5OR1.setOutgoingFailure(fM_G103);
        eM_S1E5OR1.setEnablingCondition("S1 || E5", faultModes);
        eM_S1E5OR1.setPDF("dirac(0)");
        errorModes.put(eM_S1E5OR1.getName(), eM_S1E5OR1);
        failureModes.put(fM_G103.getDescription(), fM_G103);

        switchS1.addErrorMode(eM_S1E5OR1);

        FailureMode fM_G102 = new FailureMode("G102Failure1");
        ErrorMode eM_SE4AND1 = new ErrorMode("SE4AND1");
        eM_SE4AND1.addInputFaultMode(enFM_S, exFM_E4);
        eM_SE4AND1.setOutgoingFailure(fM_G102);
        eM_SE4AND1.setEnablingCondition("S && E4", faultModes);
        eM_SE4AND1.setPDF("dirac(0)");
        errorModes.put(eM_SE4AND1.getName(), eM_SE4AND1);
        failureModes.put(fM_G102.getDescription(), fM_G102);

        pressureSwitch.addErrorMode(eM_SE4AND1);

        FailureMode fM_G101 = new FailureMode("G101Failure1");
        ErrorMode eM_E3K2OR1 = new ErrorMode("E3K2OR1");
        eM_E3K2OR1.addInputFaultMode(exFM_E3, enFM_K2);
        eM_E3K2OR1.setOutgoingFailure(fM_G101);
        eM_E3K2OR1.setEnablingCondition("E3 || K2", faultModes);
        eM_E3K2OR1.setPDF("dirac(0)");
        errorModes.put(eM_E3K2OR1.getName(), eM_E3K2OR1);
        failureModes.put(fM_G101.getDescription(), fM_G101);

        relayK2.addErrorMode(eM_E3K2OR1);

        FailureMode fM_G100 = new FailureMode("G100Failure1");
        ErrorMode eM_TE2OR1 = new ErrorMode("TE2OR1");
        eM_TE2OR1.addInputFaultMode(enFM_T, exFM_E2);
        eM_TE2OR1.setOutgoingFailure(fM_G100);
        eM_TE2OR1.setEnablingCondition("T || E2", faultModes);
        eM_TE2OR1.setPDF("dirac(0)");
        errorModes.put(eM_TE2OR1.getName(), eM_TE2OR1);
        failureModes.put(fM_G100.getDescription(), fM_G100);

        pressureTankSystem.addErrorMode(eM_TE2OR1);

        relayK1.addPropagationPort(
                new PropagationPortType(fM_G104, exFM_E5, switchS1));
        propagationPortTypes.addAll(relayK1.getPropagationPorts());
        switchS1.addPropagationPort(
                new PropagationPortType(fM_G103, exFM_E4, pressureSwitch));
        propagationPortTypes.addAll(switchS1.getPropagationPorts());
        pressureSwitch.addPropagationPort(
                new PropagationPortType(fM_G102, exFM_E3, relayK2));
        propagationPortTypes.addAll(pressureSwitch.getPropagationPorts());
        relayK2.addPropagationPort(
                new PropagationPortType(fM_G101, exFM_E2, pressureTankSystem));
        propagationPortTypes.addAll(relayK2.getPropagationPorts());

    }



    public static PressureTankSystemBuilder getInstance() {
        if (single_instance == null)
            single_instance = new PressureTankSystemBuilder();
        return single_instance;
    }

    public static PressureTankSystemBuilder getSecondExample() {
        if (single_instance == null)
            single_instance = new PressureTankSystemBuilder();
        ((InternalFaultMode)faultModes.get("K1")).setTimeToFaultPDF("expoly(Exp[-3.42383 x] * 11.7226 * x,0,inf)");
        ((InternalFaultMode)faultModes.get("K2")).setTimeToFaultPDF("expoly(Exp[-3.42383 x] * 11.7226 * x,0,inf)");
        ((InternalFaultMode)faultModes.get("R")).setTimeToFaultPDF("expoly(Exp[-3.42383 x] * 11.7226 * x,0,inf)");
        ((InternalFaultMode)faultModes.get("S")).setTimeToFaultPDF("expoly(Exp[-10.0701 x] * 101.407 * x,0,inf)");
        ((InternalFaultMode)faultModes.get("S1")).setTimeToFaultPDF("expoly(Exp[-2.60144 x] * 6.76748 * x,0,inf)");
        ((InternalFaultMode)faultModes.get("T")).setTimeToFaultPDF("expoly(Exp[-0.711619 x] * 0.506402 * x,0,inf)");
        return single_instance;
    }

    public static PressureTankSystemBuilder getThirdExample() {
        if (single_instance == null)
            single_instance = new PressureTankSystemBuilder();
        ((InternalFaultMode)faultModes.get("K1")).setTimeToFaultPDF("expoly(Exp[-8.491523 x] * 72.1059 * x,0,inf)");
        ((InternalFaultMode)faultModes.get("K2")).setTimeToFaultPDF("expoly(Exp[-8.49152 x] * 72.1059 * x,0,inf)");
        ((InternalFaultMode)faultModes.get("R")).setTimeToFaultPDF("expoly(Exp[-8.49152 x] * 72.1059 * x,0,inf)");
        ((InternalFaultMode)faultModes.get("S")).setTimeToFaultPDF("expoly(Exp[-0.521825 x] * 0.272301 * x,0,inf)");
        ((InternalFaultMode)faultModes.get("S1")).setTimeToFaultPDF("expoly(Exp[-0.0383779 x] * 0.00147286 * x,0,inf)");
        ((InternalFaultMode)faultModes.get("T")).setTimeToFaultPDF("expoly(Exp[-0.000673629 x] * 0.000000453776 * x,0,inf)");
        return single_instance;
    }
    public static PressureTankSystemBuilder getFourthExample() {
        if (single_instance == null)
            single_instance = new PressureTankSystemBuilder();
        ((InternalFaultMode)faultModes.get("K1")).setTimeToFaultPDF("expoly(Exp[-4.98739 x] * -5.61691 + Exp[-4.98739 x] * 45.834 * x,0.122549,inf)");
        ((InternalFaultMode)faultModes.get("K2")).setTimeToFaultPDF("expoly(Exp[-4.98739 x] * -5.61691 + Exp[-4.98739 x] * 45.834 * x,0.122549,inf)");
        ((InternalFaultMode)faultModes.get("R")).setTimeToFaultPDF("expoly(Exp[-4.98739 x] * -5.61691 + Exp[-4.98739 x] * 45.834 * x,0.122549,inf)");
        ((InternalFaultMode)faultModes.get("S")).setTimeToFaultPDF("expoly(Exp[-3.65919 x] * -0.649792 + Exp[-3.65919 x] * 15.595 * x,0.0416667,inf)");
        ((InternalFaultMode)faultModes.get("S1")).setTimeToFaultPDF("expoly(Exp[-0.855172 x] * -0.1354 + Exp[-0.855172 x] * 0.839479 * x,0.16129,inf)");
        ((InternalFaultMode)faultModes.get("T")).setTimeToFaultPDF("expoly(Exp[-0.021941 x] * -0.000287545 + Exp[-0.021941 x] * 0.000487676 * x,0.589623,inf)");
        return single_instance;
    }

    public static PressureTankSystemBuilder getFifthExample() {
        if (single_instance == null)
            single_instance = new PressureTankSystemBuilder();
        ((InternalFaultMode)faultModes.get("K1")).setTimeToFaultPDF("exp(1.91)");
        ((InternalFaultMode)faultModes.get("K2")).setTimeToFaultPDF("exp(1.91)");
        ((InternalFaultMode)faultModes.get("R")).setTimeToFaultPDF("exp(1.91)");
        ((InternalFaultMode)faultModes.get("S")).setTimeToFaultPDF("exp(1.70)");
        ((InternalFaultMode)faultModes.get("S1")).setTimeToFaultPDF("exp(0.40)");
        ((InternalFaultMode)faultModes.get("T")).setTimeToFaultPDF("exp(0.109)");
        return single_instance;
    }

    public static PressureTankSystemBuilder getSixthExample() {
        if (single_instance == null)
            single_instance = new PressureTankSystemBuilder();
        ((InternalFaultMode)faultModes.get("K1")).setTimeToFaultPDF("exp(1.79)");
        ((InternalFaultMode)faultModes.get("K2")).setTimeToFaultPDF("exp(1.79)");
        ((InternalFaultMode)faultModes.get("R")).setTimeToFaultPDF("exp(1.79)");
        ((InternalFaultMode)faultModes.get("S")).setTimeToFaultPDF("exp(0.11)");
        ((InternalFaultMode)faultModes.get("S1")).setTimeToFaultPDF("exp(0.00809)");
        ((InternalFaultMode)faultModes.get("T")).setTimeToFaultPDF("exp(0.000142)");
        return single_instance;
    }

    public static PressureTankSystemBuilder getSeventhExample() {
        if (single_instance == null)
            single_instance = new PressureTankSystemBuilder();
        ((InternalFaultMode)faultModes.get("K1")).setTimeToFaultPDF("pieceWise(Exp[-2.85884 x] * x,0,0.490196;Exp[6.1204 x] * x,0.490196,0.558659;Exp[-8.90495 x] * 96.0395 * x,0.558659,inf)");
        ((InternalFaultMode)faultModes.get("K2")).setTimeToFaultPDF("pieceWise(Exp[-2.85884 x] * x,0,0.490196;Exp[6.1204 x] * x,0.490196,0.558659;Exp[-8.90495 x] * 96.0395 * x,0.558659,inf)");
        ((InternalFaultMode)faultModes.get("R")).setTimeToFaultPDF("pieceWise(Exp[-2.85884 x] * x,0,0.490196;Exp[6.1204 x] * x,0.490196,0.558659;Exp[-8.90495 x] * 96.0395 * x,0.558659,inf)");
        ((InternalFaultMode)faultModes.get("S")).setTimeToFaultPDF("pieceWise(Exp[10.7895 x] * x,0,0.166667;Exp[0.515452 x] * x * 0.253289,0.166667,inf)");
        ((InternalFaultMode)faultModes.get("S1")).setTimeToFaultPDF("expoly(Exp[-0.0383779 x] * 0.00147286 * x,0,inf)");
        ((InternalFaultMode)faultModes.get("T")).setTimeToFaultPDF("expoly(Exp[-0.000673629 x] * 0.000000453776 * x,0,inf)");
        return single_instance;
    }

    public SystemType getSystem() {
        return system;
    }
}
