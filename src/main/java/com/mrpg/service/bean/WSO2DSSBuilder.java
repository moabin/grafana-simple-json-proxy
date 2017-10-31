/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mrpg.service.bean;

import com.mrpg.service.Util;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;

/**
 *
 * @author Monyo
 */
public class WSO2DSSBuilder implements JsonBuilder{

    @Override
    public String build(String type) throws IOException {
        HashMap headers = new HashMap();
        headers.put("Accept", "application/json");
        JsonObject jsonReponse = Json.createReader(Util.doGetStream(Util.getProperties("default").getProperty(type + ".URL"), headers)).readObject();

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        
        InputStream is = loader.getResourceAsStream(Util.getProperties("default").getProperty(type + "_columns"));
        JsonArray columnsToMatch = Json.createReader(is).readArray();
        //System.out.println(columnsToMatch);

        JsonArray entries = jsonReponse.getJsonObject("entries").getJsonArray("entry");
        Iterator<JsonValue> it = entries.iterator();

        JsonArrayBuilder rowBuilder = Json.createArrayBuilder();
        while (it.hasNext()) {
            JsonValue value = it.next();
            JsonObject v = (JsonObject) value;
            JsonArrayBuilder row = Json.createArrayBuilder();

            Iterator<JsonValue> colIt = columnsToMatch.iterator();
            while (colIt.hasNext()) {
                JsonObject colDef = (JsonObject) colIt.next();
                row.add(v.get(colDef.getString("text")));
            }

            rowBuilder.add(row.build());
        }

        JsonArrayBuilder baseArray = Json.createArrayBuilder();
        JsonArrayBuilder columnsArrays = Json.createArrayBuilder();

        Iterator<JsonValue> colIt = columnsToMatch.iterator();
        while (colIt.hasNext()) {
            JsonObject colDef = (JsonObject) colIt.next();
            columnsArrays.add(Json.createObjectBuilder().add("text", colDef.getString("display")).add("type", colDef.getString("type")).build());
        }

        JsonObject columns = Json.createObjectBuilder().add("columns", columnsArrays.build()).add("rows", rowBuilder.build()).add("type", "table").build();
        baseArray.add(columns);

        return baseArray.build().toString();
    }
}
