/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mrpg.service;

import com.mrpg.service.bean.JsonBuilder;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 * Reponds to query request from grafana
 * @author monyo
 */
public class QueryServlet extends HttpServlet {

    /**
     * Logger.
     */
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
        //Add XSS bypass headers
        response.setHeader("Access-Control-Allow-Headers", "accept, content-type");
        response.setHeader("Access-Control-Allow-Methods", "GET,PUT,POST,HEAD,OPTIONS");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Content-Type", "application/json; charset=utf-8");
        
        LOG.debug("calling QueryServlet");
        try (PrintWriter out = response.getWriter()) {
            LOG.debug("reading POST data");
            //get and parse JSON data posted to the service
            JsonObject requestJson = Json.createReader(request.getInputStream()).readObject();
            JsonArray targets = requestJson.getJsonArray("targets");
            String target = targets.getJsonObject(0).getString("target");
            
            //target is one of string in jsonarray defined in default.properties under search.patterns 
            //the targets are presented in SearchSerlet to grafana
            LOG.debug("target = " + target);

            try {
                //create object of the target type in com.mrpg.service.bean package
                Class targetClass = Class.forName("com.mrpg.service.bean." + target);
                JsonBuilder builder = (JsonBuilder) targetClass.newInstance();

                String output = builder.build(target);
                LOG.info(output);
                out.print(output);
                out.flush();
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                java.util.logging.Logger.getLogger(QueryServlet.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Handle OPTION call; called before the POST or GET by grafana to make sure the context exists
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //called before the POST or GET by grafana to make sure the context exists
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
