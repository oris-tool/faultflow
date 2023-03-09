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

package it.unifi.stlab.faultflow.exporter;

import it.unifi.stlab.faultflow.exporter.strategies.BasicExportToXPN;
import it.unifi.stlab.faultflow.exporter.strategies.OrderByComponentToXPN;
import it.unifi.stlab.faultflow.model.knowledge.composition.System;
import it.unifi.stlab.faultflow.translator.PetriNetTranslator;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;

public class PetriNetExporter {
    public static PetriNetTranslator exportPetriNetFromSystem(System system, PetriNetExportMethod method) throws JAXBException, FileNotFoundException {
        PetriNetTranslator pnt = new PetriNetTranslator();
        pnt.translate(system, method);
        File dir = new File("export/");
        dir.mkdir();
        XPNExporter.export(new File("export/"+system.getName() +"_"+ method.toString() + "_Fault2Failure.xpn"),
                new OrderByComponentToXPN(system, pnt.getPetriNet(), pnt.getMarking()));
        XPNExporter.export(new File("export/"+system.getName() +"_"+ method.toString() + "_Fault2Failure_Basic.xpn"),
                new BasicExportToXPN(pnt.getPetriNet(), pnt.getMarking()));
        return pnt;
    }
    public static void exportPetriNet(PetriNetTranslator pnt) throws JAXBException, FileNotFoundException{
        XPNExporter.export(new File("export/PetriNet_"+pnt.getName()+".xpn"),
                new BasicExportToXPN(pnt.getPetriNet(), pnt.getMarking()));
    }

    public static void exportPetriNetOrderedByComponent(PetriNetTranslator pnt, System system) throws JAXBException, FileNotFoundException{
        XPNExporter.export(new File("export/"+system.getName() +"_ByComponent_Fault2Failure.xpn"),
                new OrderByComponentToXPN(system, pnt.getPetriNet(), pnt.getMarking()));
    }
}
