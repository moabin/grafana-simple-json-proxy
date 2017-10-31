/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mrpg.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author monyo
 */
public class QueryServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger("QueryServlet");

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Headers", "accept, content-type");
        response.setHeader("Access-Control-Allow-Methods", "GET,PUT,POST,HEAD,OPTIONS");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Content-Type", "application/json; charset=utf-8");

        try (PrintWriter out = response.getWriter()) {

            JsonObject requestJson = Json.createReader(request.getInputStream()).readObject();
            JsonArray targets = requestJson.getJsonArray("targets");
            String target = targets.getJsonObject(0).getString("target");
            
            LOG.debug("target = " + target);
            HashMap headers = new HashMap();
            headers.put("Accept", "application/json");

            JsonObject jsonReponse = Json.createReader(Util.doGetStream(Util.getProperties("default").getProperty(target + ".URL"), headers)).readObject();

            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream is = loader.getResourceAsStream(Util.getProperties("default").getProperty("columns.json"));
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

            out.print(baseArray.build());
            out.flush();
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Headers", "accept, content-type");
        response.setHeader("Access-Control-Allow-Methods", "GET,PUT,POST,HEAD,OPTIONS");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Content-Type", "application/json; charset=utf-8");

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
