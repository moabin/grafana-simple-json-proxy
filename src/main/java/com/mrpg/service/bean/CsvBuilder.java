/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mrpg.service.bean;

import com.mrpg.service.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import org.apache.log4j.Logger;

/**
 * WSO2 DSS web-services are common
 * To add a DSS service add the URL and target name to default.properties
 * Add a class with target name and extend this class
 * Add a json file with the column you want to use as <classname>_columns.json
 * Add volume entry for this json file to docker-compose file pointing to /opt/<json file name>
 * @author Monyo
 */
public class CsvBuilder implements JsonBuilder {

    private static final Logger LOG = Logger.getLogger("CsvBuilder");
    
    SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");  

    @Override
    public String build(String type) throws IOException {
        int valuetoSelect = 1;
        return buildMe(type, valuetoSelect, 0);
    }

    public String buildMe(String type, int valuetoSelect) throws IOException {
        return buildMe(type, valuetoSelect, Util.random());
    }
    
    public String buildMe(String type, int valuetoSelect, int random) throws IOException {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        LOG.info("type = " + type);
        //LOG.info("Using URL: " + Util.getProperties("default").getProperty(type + ".URL"));
        //JsonObject jsonReponse = Json.createReader(Util.doGetStream(Util.getProperties("default").getProperty(type + ".URL"), headers)).readObject();

        //ClassLoader loader = Thread.currentThread().getContextClassLoader();
        //InputStream is = loader.getResourceAsStream(Util.getProperties("default").getProperty(type + "_columns"));
        //File f = new File("/opt/" + Util.getProperties("default").getProperty(type + "_columns"));
        //LOG.info("file = " + f.getAbsolutePath());
        Util u = new Util();
        //LOG.info(type + "_columns.json");
        //BufferedReader reader = u.getFile(type + "_columns.json");
        //JsonArray columnsArrays = Json.createReader(reader).readArray();
        //LOG.info(columnsArrays.toString());
        //JsonArray columnsToMatch = Json.createReader(is).readArray();
        //System.out.println(columnsToMatch);
        
        BufferedReader reader = u.getFile(type + ".csv");

        JsonArrayBuilder rowBuilder = Json.createArrayBuilder();
        JsonArrayBuilder columnsToMatch = Json.createArrayBuilder();
        JsonObjectBuilder timeText = Json.createObjectBuilder();
        timeText.add("text", "Time");
        timeText.add("type", "time");

        columnsToMatch.add(timeText);

        String line = reader.readLine();
         
        Date currentDate = new Date();
        //int j = 1;
        JsonArrayBuilder yaxis = Json.createArrayBuilder();
        yaxis.add(currentDate.getTime());

        while (line != null) {
            /*Calendar cal = Calendar.getInstance();
            // remove next line if you're always using the current time.
            cal.setTime(currentDate); 
            cal.add(Calendar.MINUTE, -(j));
            j = j + 5;
            Date date = cal.getTime();
*/

            String[] cells = line.split(",");

            JsonObjectBuilder xaxis = Json.createObjectBuilder();
            xaxis.add("text", cells[0]);
            xaxis.add("type", "number");

            columnsToMatch.add(xaxis);
            
            //String strDate= formatter.format(date);  
            //xaxis.add(date.getTime());
            //row.add(strDate);
            //for(int i = 1; i < cells.length; i++){
                yaxis.add(cells[valuetoSelect] + Util.random());
            //}
            //row.add(cells[2]);

            //rowBuilder.add(yaxis);
            line = reader.readLine();
        }

        JsonArrayBuilder baseArray = Json.createArrayBuilder();
        //JsonArrayBuilder columnsArrays = Json.createArrayBuilder();

        //Iterator<JsonValue> colIt = columnsToMatch.iterator();
        /*while (colIt.hasNext()) {
            JsonObject colDef = (JsonObject) colIt.next();
            columnsArrays.add(Json.createObjectBuilder().add("text", colDef.getString("display")).add("type", colDef.getString("type")).build());
        }*/

        rowBuilder.add(yaxis.build())   ;     

        JsonObject columns = Json.createObjectBuilder()
        .add("columns", columnsToMatch.build())
        .add("rows", rowBuilder.build())
        .add("type", "table").build();
        baseArray.add(columns);

        String output = baseArray.build().toString();
        LOG.info(output);

        return output;
    }

    
}
