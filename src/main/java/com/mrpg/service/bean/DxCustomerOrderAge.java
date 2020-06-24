/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mrpg.service.bean;

import java.io.IOException;

import com.mrpg.service.Util;

/**
 * Extends base WSO2DSSBuilder but class name needed for instantiation.
 *
 * @author Monyo
 */
public class DxCustomerOrderAge extends CsvBuilder {

    @Override
    public String build(String type) throws IOException {
        int valuetoSelect = 1;
        int random = Util.random();
        return buildMe(type, valuetoSelect, random);
    }

    
}