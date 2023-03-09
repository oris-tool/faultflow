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

package it.unifi.stlab.faultflow.exporter.xpn;

import javax.xml.bind.annotation.*;


/**
 * <p>Classe Java per anonymous complex type.
 *
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="tpn-entities" type="{http://www.oris-tool.org}TPN-entities"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "tpnEntities"
})
@XmlRootElement(name = "tpn-editor", namespace = "http://www.oris-tool.org")
public class TpnEditor {

    @XmlElement(name = "tpn-entities", namespace = "http://www.oris-tool.org", required = true)
    protected TPNEntities tpnEntities;

    /**
     * Recupera il valore della proprietà tpnEntities.
     *
     * @return possible object is
     * {@link TPNEntities }
     */
    public TPNEntities getTpnEntities() {
        return tpnEntities;
    }

    /**
     * Imposta il valore della proprietà tpnEntities.
     *
     * @param value allowed object is
     *              {@link TPNEntities }
     */
    public void setTpnEntities(TPNEntities value) {
        this.tpnEntities = value;
    }

}
