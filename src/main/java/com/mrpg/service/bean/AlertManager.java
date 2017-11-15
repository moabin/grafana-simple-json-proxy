/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mrpg.service.bean;

import com.mrpg.service.Util;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;
import org.apache.log4j.Logger;

/**
 * Handle AlertManager target
 * Calls Prometheus AlertManager API to retrieve active alerts
 * @author Monyo
 */
public class AlertManager implements JsonBuilder {

    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger("AlertManager");

    /**
     * Build response table.
     * @param type Target type
     * @return JSON response
     * @throws java.io.IOException
     */
    @Override
    public String build(final String type) throws IOException {
        HashMap headers = new HashMap();
        headers.put("Accept", "application/json, text/plain, */*");
        LOG.debug("Using URL: " + Util.getProperties("default").getProperty(type + ".URL"));
        JsonObject jsonReponse = 
                Json.createReader(Util.doGetStream(Util.getProperties("default").getProperty(type + ".URL"), headers))
                        .readObject();

        //Following is built from all possible vlues in the reposne from alert manager
        //A table of columns and rows in Json is created
        //Only alertname, severity, and startsAt are added to the rows
        
        String status = jsonReponse.getString("status");

        JsonArray dataArr = jsonReponse.getJsonArray("data");
        Iterator<JsonValue> it = dataArr.iterator();

        //represents the rows in the table
        JsonArrayBuilder rowBuilder = Json.createArrayBuilder();

        //for each alert get the data
        while (it.hasNext()) {
            JsonValue value = it.next();
            JsonObject data = (JsonObject) value;

            //JsonObject data = dataArr.getJsonObject(0);//start loop
            JsonObject labels = data.getJsonObject("labels");

            String alertname = labels.getString("alertname");
            String instance = labels.getString("instance");
            String job = labels.getString("job");
            String monitor = labels.getString("monitor");
            //String outboundmessageclasscode = labels.getString("outboundmessageclasscode");

            String severity = "";
            try {
                severity = labels.getString("severity");
            } catch (NullPointerException npe) {
            }

            JsonObject annotations = data.getJsonObject("annotations");

            try {
                String summary = annotations.getString("summary");
                String description = annotations.getString("description");
            } catch (Exception e) {

            }

            String startsAt = data.getString("startsAt");
            String endsAt = data.getString("endsAt");
            String generatorURL = data.getString("generatorURL");

            JsonObject statusObj = data.getJsonObject("status");

            try {
                String state = statusObj.getString("state");
                JsonArray silencedBy = statusObj.getJsonArray("silencedBy");
                JsonArray inhibitedBy = statusObj.getJsonArray("inhibitedBy");

            } catch (Exception e) {

            }

            JsonArray receivers = data.getJsonArray("receivers");
            String fingerprint = data.getString("fingerprint");

            JsonArrayBuilder row = Json.createArrayBuilder();

            //add row with just the columns we need
            row.add(alertname);
            row.add(severity);
            row.add(startsAt);

            rowBuilder.add(row.build());
        }

        JsonArrayBuilder baseArray = Json.createArrayBuilder();
        
        //representes the columns in the table
        JsonArrayBuilder columnsArrays = Json.createArrayBuilder();
        //add the column by specifiying the text, and type
        columnsArrays.add(Json.createObjectBuilder().add("text", "AlertName").add("type", "string").build());
        columnsArrays.add(Json.createObjectBuilder().add("text", "Severity").add("type", "string").build());
        columnsArrays.add(Json.createObjectBuilder().add("text", "Time").add("type", "date").build());

        //build the table by adding columns and row arrays
        JsonObject table = Json.createObjectBuilder().add("columns", columnsArrays.build()).add("rows", rowBuilder.build()).add("type", "table").build();
        baseArray.add(table);

        return baseArray.build().toString();
    }
}
