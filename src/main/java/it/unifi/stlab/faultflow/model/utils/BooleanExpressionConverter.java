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

package it.unifi.stlab.faultflow.model.utils;

import it.unifi.stlab.faultflow.model.knowledge.propagation.BooleanExpression;
import it.unifi.stlab.faultflow.model.knowledge.propagation.ExternalFaultMode;
import it.unifi.stlab.faultflow.model.knowledge.propagation.FaultMode;
import it.unifi.stlab.faultflow.model.knowledge.propagation.InternalFaultMode;
import org.json.JSONObject;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.persistence.AttributeConverter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Dependent
@Default
public class BooleanExpressionConverter implements AttributeConverter<BooleanExpression, String> {

//    @Inject
//    FaultModeDao faultModeDao = new FaultModeDao();

    @Override
    public String convertToDatabaseColumn(BooleanExpression booleanExpression) {
        JSONObject dbDataObject = new JSONObject();
        if (booleanExpression == null) {
            return "";
        } else {
            dbDataObject.append("expression", booleanExpression.toSimpleString());
            HashMap<String, String> inputFaults = new HashMap<>();
            for (FaultMode faultMode : booleanExpression.extractIncomingFaults()) {
                String keyValue = faultMode.getUuid() + "£" + faultMode.getClass().getSimpleName();
                if (faultMode.getClass().getSimpleName().equals("InternalFaultMode"))
                    keyValue += "£" + ((InternalFaultMode) faultMode).getArisingPDFToString();
                inputFaults.put(faultMode.getName(), keyValue);
            }
            dbDataObject.append("inputFaults", inputFaults);
            return dbDataObject.toString();
        }
    }

    @Override
    public BooleanExpression convertToEntityAttribute(String dbData) {
        if ("{}".equals(dbData)) {
            return null;
        }
        BooleanExpression be;
        JSONObject dbDataObject = new JSONObject(dbData);
        String expression = dbDataObject.getJSONArray("expression").getString(0);
        JSONObject inputFaults = (JSONObject) dbDataObject.getJSONArray("inputFaults").get(0);
        Map<String, FaultMode> faultmodes = new HashMap<>();
        Iterator<String> fault = inputFaults.keys();
        while (fault.hasNext()) {
            String key = fault.next();
            String[] values = inputFaults.get(key).toString().split("£");
            String uuid = values[0];
            if (values[1].equals("InternalFaultMode")) {
                InternalFaultMode internalFaultMode = new InternalFaultMode(key, values[2]);
                internalFaultMode.setUuid(uuid);
                faultmodes.put(key, internalFaultMode);
            } else {
                ExternalFaultMode externalFaultMode = new ExternalFaultMode(key);
                externalFaultMode.setUuid(uuid);
                faultmodes.put(key, externalFaultMode);
            }
//            FaultMode faultMode = faultModeDao.findById(UUID.fromString(uuid));
//            faultmodes.put(key, faultMode);
        }
        be = BooleanExpression.config(expression, faultmodes);
        return be;
    }
}
