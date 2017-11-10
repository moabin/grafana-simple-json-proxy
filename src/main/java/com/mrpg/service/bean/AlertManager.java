/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mrpg.service.bean;

import com.mrpg.service.Util;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;
import org.apache.log4j.Logger;

/**
 *
 * @author Monyo
 */
public class AlertManager implements JsonBuilder {

    private static final Logger LOG = Logger.getLogger("AlertManager");

    @Override
    public String build(String type) throws IOException {
        HashMap headers = new HashMap();
        headers.put("Accept", "application/json, text/plain, */*");
        LOG.info("Using URL: " + Util.getProperties("default").getProperty(type + ".URL"));
        JsonObject jsonReponse = Json.createReader(Util.doGetStream(Util.getProperties("default").getProperty(type + ".URL"), headers)).readObject();

        String status = jsonReponse.getString("status");

        JsonArray dataArr = jsonReponse.getJsonArray("data");
        Iterator<JsonValue> it = dataArr.iterator();

        JsonArrayBuilder rowBuilder = Json.createArrayBuilder();
        
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
            
            String severity  = "";
            try{
            severity = labels.getString("severity");
            }catch (NullPointerException npe){}

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
            
            row.add(alertname);
            row.add(severity);
            row.add(startsAt);
            
            rowBuilder.add(row.build());
        }

        JsonArrayBuilder baseArray = Json.createArrayBuilder();
        JsonArrayBuilder columnsArrays = Json.createArrayBuilder();

        columnsArrays.add(Json.createObjectBuilder().add("text", "AlertName").add("type", "string").build());
        columnsArrays.add(Json.createObjectBuilder().add("text", "Severity").add("type", "string").build());
        columnsArrays.add(Json.createObjectBuilder().add("text", "Time").add("type", "date").build());
        
        JsonObject columns = Json.createObjectBuilder().add("columns", columnsArrays.build()).add("rows", rowBuilder.build()).add("type", "table").build();
        baseArray.add(columns);

        return baseArray.build().toString();
    }
}
