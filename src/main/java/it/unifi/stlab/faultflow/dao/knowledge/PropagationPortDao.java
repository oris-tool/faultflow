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

package it.unifi.stlab.faultflow.dao.knowledge;

import it.unifi.stlab.faultflow.dao.BaseDao;
import it.unifi.stlab.faultflow.model.knowledge.propagation.PropagationPort;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import java.util.List;

@Default
@Dependent
public class PropagationPortDao extends BaseDao<PropagationPort> {
    public PropagationPortDao() {
        super(PropagationPort.class);
    }

    public List<PropagationPort> getAll() {
        return entityManager.createQuery("SELECT pp FROM PropagationPort pp", PropagationPort.class).getResultList();
    }

    public List<PropagationPort> getPropagationPortsByExoFaultMode(String exoFaultUUID) {
        return entityManager.createQuery("SELECT pp FROM PropagationPort pp " +
                "JOIN FETCH pp.externalFaultMode " +
                "JOIN FETCH pp.propagatedFailureMode " +
                "JOIN FETCH pp.affectedComponent " +
                "WHERE pp.externalFaultMode.uuid=:uuid", PropagationPort.class)
                .setParameter("uuid", exoFaultUUID).getResultList();
    }
}
