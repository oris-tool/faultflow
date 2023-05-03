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

package it.unifi.stlab.faultflow.model.operational;

import it.unifi.stlab.faultflow.model.knowledge.propagation.FaultMode;
import it.unifi.stlab.faultflow.model.knowledge.propagation.InternalFaultMode;
import it.unifi.stlab.faultflow.model.utils.PDFParser;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "faults")
public class Fault extends Event {

    @ManyToOne
    private FaultMode faultMode;

    public Fault() {
    }

    /**
     * Create a Fault from its description and FaultMode and the moment in which it's expected to occur.
     *
     * @param description a string which must be unique.
     * @param faultMode   the type of the Failure.
     * @param timestamp   the moment in which the Fault it's expected to occur
     */
    public Fault(String description, FaultMode faultMode, BigDecimal timestamp) {
        super.setDescription(description);
        this.faultMode = faultMode;
        super.setTimestamp(timestamp);
    }

    /**
     * Create a Fault just from its description and FaultMode. This can be done only for internalFaults
     * (which means, basic events in the FaultTree). By doing so, the timestamp is automatically generated
     * by sampling the faultMode's PDF.
     *
     * @param description a string which must be unique.
     * @param faultMode   the type of the Failure.
     */

    public Fault(String description, InternalFaultMode faultMode) {
        super.setDescription(description);
        BigDecimal timestamp = PDFParser.generateSample(faultMode.getTimeToFaultPDFToString());
        super.setTimestamp(timestamp);
        this.faultMode = faultMode;

    }

    /**
     * Method occurred is called when a Failure is expected to occur.
     * Sets the FailureMode state to true as a consequence.
     */
    public void occurred() {
        this.faultMode.setState(true);
    }

    public FaultMode getFaultMode() {
        return this.faultMode;
    }

    public void setFaultMode(FaultMode faultMode) {
        this.faultMode = faultMode;
    }
}
