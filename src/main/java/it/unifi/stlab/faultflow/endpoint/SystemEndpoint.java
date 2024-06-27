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

package it.unifi.stlab.faultflow.endpoint;

import it.unifi.stlab.faultflow.businessLogic.controller.SystemController;
import it.unifi.stlab.faultflow.dto.inputsystemdto.InputSystemDto;
import it.unifi.stlab.faultflow.dto.system.OutputSystemDto;
import it.unifi.stlab.faultflow.endpoint.response.NotFoundResponse;
import it.unifi.stlab.faultflow.exporter.PetriNetExportMethod;
import it.unifi.stlab.faultflow.exporter.XPNExporter;
import it.unifi.stlab.faultflow.exporter.strategies.OrderByComponentToXPN;
import it.unifi.stlab.faultflow.mapper.FaultTreeMapper;
import it.unifi.stlab.faultflow.mapper.SystemMapper;
import it.unifi.stlab.faultflow.model.knowledge.composition.SystemType;
import it.unifi.stlab.faultflow.translator.PetriNetTranslator;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;

@Path("/system")
public class SystemEndpoint {

    @Inject
    SystemController systemController;

    @POST
    @Path("/xpn")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPetriNetXPN(InputSystemDto inputSystemDto,
                                   @QueryParam("method") @DefaultValue("fa") String method) {
        PetriNetTranslator pnt = new PetriNetTranslator();
        SystemType sys = SystemMapper.BddToSystem(inputSystemDto.getBdd());
        FaultTreeMapper.decorateSystem(inputSystemDto.getFaultTree(), sys);
        File out = new File("PetriNet.xpn");
        try {
            pnt.translate(sys, PetriNetExportMethod.fromString(method));
            XPNExporter.export(out, new OrderByComponentToXPN(sys, pnt.getPetriNet(), pnt.getMarking()));
            return Response.ok(out).header("Content-Disposition", "attachment; filename=" + "PetriNet.xpn").build();
        } catch (FileNotFoundException fnf) {
            throw new NotFoundException("File not Found");
        } catch (JAXBException e) {
            throw new BadRequestException("There's been a problem with the conversion to XPN");
        } catch (Exception e) {
            throw new InternalServerErrorException("Unexpected Server Problem");
        }
    }

    @POST
    @Path("/load")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response persistSystem(InputSystemDto inputSystemDto) {
        SystemType sys = SystemMapper.BddToSystem(inputSystemDto.getBdd());
        FaultTreeMapper.decorateSystem(inputSystemDto.getFaultTree(), sys);
        systemController.persistSystem(sys);
        return Response.ok(FaultTreeMapper.systemToOutputSystem(sys)).build();
    }

    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getSystem(@QueryParam("uuid") String systemUUID) {
        SystemType system = systemController.findSystem(systemUUID);

        if (system != null)
            return Response
                    .ok(new OutputSystemDto(system))
                    .build();
        else
            return NotFoundResponse.create("System", systemUUID);
    }

    @GET
    @Path("/clear")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response clearDatabase() {
        systemController.removeAllSystems();

        return Response.ok().build();
    }

    @GET
    @Path("/remove/{system_uuid}")
    @Transactional
    public Response removeSystem(@PathParam("system_uuid") String systemUUID) {
        if (systemUUID == null) {
            throw new IllegalArgumentException("Please, specify System's UUID as path Parameter!");
        } else {
            try {
                systemController.removeSystem(systemUUID);
            } catch (Exception e) {
                throw new Error(e.getMessage());
            }
        }
        return Response.ok().build();
    }
}
