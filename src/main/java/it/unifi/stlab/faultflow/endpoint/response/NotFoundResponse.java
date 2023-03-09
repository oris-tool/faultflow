package it.unifi.stlab.faultflow.endpoint.response;

import org.json.JSONObject;

import javax.ws.rs.core.Response;

public class NotFoundResponse {

    public static Response create(String entityClass, String entityUUID) {
        JSONObject response = new JSONObject();

        response.put("status", "EntityNotFound")
                .put("entityClass", entityClass)
                .put("entityUUID", entityUUID);

        return Response
                .ok(response)
                .build();
    }
}
