/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mrpg.service.bean;

import java.io.IOException;

/**
 * Extends base WSO2DSSBuilder but class name needed for instantiation.
 *
 * @author Monyo
 */
public class DxValue extends CsvBuilder {

    @Override
    public String build(String type) throws IOException {
        int valuetoSelect = 3;
        return buildMe(type, valuetoSelect);
    }
}