/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mrpg.service.bean;

import java.io.IOException;

/**
 * Interface to specify how to create a JSON builder for Grafana simple json plugin
 * @author Monyo
 */
public interface JsonBuilder {
    /**
     * Build a JSON response
     * @param type Target type from Grfana
     * @return JSON response
     * @throws IOException 
     */
    public String build(String type) throws IOException;
    
}
