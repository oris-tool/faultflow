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

package it.unifi.stlab.faultflow.model.knowledge.propagation;

import it.unifi.stlab.faultflow.model.utils.PDFParser;
import org.apache.commons.math3.distribution.RealDistribution;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "internalFaultModes")
public class InternalFaultMode extends FaultMode {
    @Column(name = "timeToFault_PDF")
    private String timeToFaultPDF;

    public InternalFaultMode() {
        super.name = "";
        this.timeToFaultPDF = null;
    }

    public InternalFaultMode(String name) {
        this();
        this.name = name;
    }

    public InternalFaultMode(String name, String timeToFaultPDF) {
        this(name);
        this.timeToFaultPDF = timeToFaultPDF;
    }

    public RealDistribution getTimeToFaultPDF() {
        if (this.timeToFaultPDF != null)
            return PDFParser.parseStringToRealDistribution(timeToFaultPDF);
        else
            return null;
    }


    public void setTimeToFaultPDF(String timeToFaultPDF) {
        this.timeToFaultPDF = timeToFaultPDF;
    }

    public String getTimeToFaultPDFToString() {
        return this.timeToFaultPDF;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        InternalFaultMode that = (InternalFaultMode) o;
        return Objects.equals(timeToFaultPDF, that.timeToFaultPDF);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), timeToFaultPDF);
    }

    @Override
    public String toBracketFormat() {
        return toSimpleString();
    }
}

