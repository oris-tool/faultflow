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

package it.unifi.stlab.faultflow.dto.inputsystemdto.faulttree;

import java.math.BigDecimal;
import java.util.List;

public class NodeDto {

	private String externalId;
	private String componentName;
	private String label;
	
	private NodeType nodeType;
	
	private String pdf;
	
	private GateType gateType;
	private Integer k;
	private Integer n; 
	private BigDecimal delay;

	private List<AliasDto> actsAs;
	
	public String getExternalId() {
		return externalId;
	}
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public NodeType getNodeType() {
		return nodeType;
	}
	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}
	public String getPdf() {
		return pdf;
	}
	public void setPdf(String pdf) {
		this.pdf = pdf;
	}
	public GateType getGateType() {
		return gateType;
	}
	public void setGateType(GateType type) {
		this.gateType = type;
	}
	public Integer getK() {
		return k;
	}
	public void setK(Integer k) {
		this.k = k;
	}
	public Integer getN() {
		return n;
	}
	public void setN(Integer n) {
		this.n = n;
	}
	public BigDecimal getDelay() {
		return delay;
	}
	public void setDelay(BigDecimal delay) {
		this.delay = delay;
	}

	public List<AliasDto> getActsAs() {
		return actsAs;
	}

	public void setActsAs(List<AliasDto> actsAs) {
		this.actsAs = actsAs;
	}
}
