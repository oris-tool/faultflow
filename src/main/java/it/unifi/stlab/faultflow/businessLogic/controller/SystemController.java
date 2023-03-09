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

package it.unifi.stlab.faultflow.businessLogic.controller;

import it.unifi.stlab.faultflow.dao.knowledge.*;
import it.unifi.stlab.faultflow.model.knowledge.composition.Component;
import it.unifi.stlab.faultflow.model.knowledge.composition.CompositionPort;
import it.unifi.stlab.faultflow.model.knowledge.composition.System;
import it.unifi.stlab.faultflow.model.knowledge.propagation.ErrorMode;
import it.unifi.stlab.faultflow.model.knowledge.propagation.FaultMode;
import it.unifi.stlab.faultflow.model.knowledge.propagation.PropagationPort;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.transaction.Transactional;

@Dependent
@Default
public class SystemController {

    @Inject
    SystemDao systemDao;
    @Inject
    ComponentDao componentDao;
    @Inject
    ErrorModeDao errorModeDao;
    @Inject
    FaultModeDao faultModeDao;
    @Inject
    FailureModeDao failureModeDao;
    @Inject
    PropagationPortDao propagationPortDao;
    @Inject
    CompositionPortDao compositionPortDao;

    @Transactional
    public System findSystem(String systemUUID) {
        return systemDao.findById(systemUUID);
    }

    @Transactional
    public void persistSystem(System system) {
        for (Component component : system.getComponents()) {
            for (ErrorMode errorMode : component.getErrorModes()) {
                failureModeDao.save(errorMode.getOutgoingFailure());
                for (FaultMode faultMode : errorMode.getInputFaultModes()) {
                    faultModeDao.save(faultMode);
                }
                errorModeDao.save(errorMode);
            }
            componentDao.save(component);
        }
        for (Component component : system.getComponents()) {
            for (CompositionPort compositionPort : component.getChildren()) {
                compositionPortDao.save(compositionPort);
            }
            for (PropagationPort propagationPort : component.getPropagationPorts()) {
                propagationPortDao.save(propagationPort);
            }
        }
        systemDao.save(system);
    }

    public void removeAllSystems() {
        failureModeDao.getAll().forEach(failureMode -> failureModeDao.remove(failureMode));
        errorModeDao.getAll().forEach(errorMode -> errorModeDao.remove(errorMode));
        faultModeDao.getAll().forEach(faultMode -> faultModeDao.remove(faultMode));
        propagationPortDao.getAll().forEach(propagationPort -> propagationPortDao.remove(propagationPort));
        compositionPortDao.getAll().forEach(compositionPort -> compositionPortDao.remove(compositionPort));
        systemDao.getAll().forEach(system -> systemDao.remove(system));
        componentDao.getAll().forEach(component -> componentDao.remove(component));
    }

    public void removeSystem(String systemUUID) throws Exception {
        System system = systemDao.getReferenceById(systemUUID);
        systemDao.remove(system);
        //orphan child removal automatically removes components, compositionPorts and PropagationPorts.
        for (Component component : system.getComponents()) {
            for (ErrorMode errorMode : component.getErrorModes()) {
                failureModeDao.remove(errorMode.getOutgoingFailure());
                for (FaultMode faultMode : errorMode.getInputFaultModes()) {
                    boolean res = true;
                    if (faultModeDao.findById(faultMode.getUuid().toString()) != null)
                        res = faultModeDao.remove(faultModeDao.findById(faultMode.getUuid().toString()));
                    if (!res)
                        throw new Exception("Can't remove:" + faultMode.getName() + ", " + faultMode.getUuid());
                }
            }
        }
    }
}
