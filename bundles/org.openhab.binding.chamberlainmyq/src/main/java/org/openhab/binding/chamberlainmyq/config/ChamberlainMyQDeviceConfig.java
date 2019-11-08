/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.chamberlainmyq.config;

import static org.openhab.binding.chamberlainmyq.ChamberlainMyQBindingConstants.*;

import java.util.HashMap;
import java.util.Iterator; 
import java.util.Map; 

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.OpenClosedType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.thing.ThingStatus;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The {@link ChamberlainMyQDeviceConfig} class represents the common configuration
 * parameters of a MyQ Device
 *
 * @author Scott Hanson - Initial contribution
 *
 */
public class ChamberlainMyQDeviceConfig {
    private String deviceType;
    private String deviceFamily;
    private String state;
    private String name;
    private String serialNumber;
    private boolean online;
    
    private Logger logger = LoggerFactory.getLogger(ChamberlainMyQDeviceConfig.class);

    public ChamberlainMyQDeviceConfig(Map<String, String> properties) {
        this.deviceType = properties.get(MYQ_TYPE).toString().replaceAll("\"", "");
        this.state = properties.get(MYQ_STATE).toString().replaceAll("\"", "");
        this.name = properties.get(MYQ_NAME).toString().replaceAll("\"", "");
        this.serialNumber = properties.get(MYQ_SERIAL).toString().replaceAll("\"", "");
        this.deviceFamily = properties.get(MYQ_FAMILY).toString().replaceAll("\"", "");

        this.online = Boolean.valueOf(properties.get(MYQ_ONLINE).toString());
    }

    public ChamberlainMyQDeviceConfig(JsonObject jsonConfig) {
        this.serialNumber = jsonConfig.get(MYQ_SERIAL).toString().replaceAll("\"", "");
        readConfigFromJson(jsonConfig);
    }

    public void readConfigFromJson(JsonObject jsonConfig) {
        this.deviceType = jsonConfig.get(MYQ_TYPE).toString().replaceAll("\"", "");
        this.serialNumber = jsonConfig.get(MYQ_SERIAL).toString().replaceAll("\"", "");
        this.name = jsonConfig.get(MYQ_NAME).toString().toString().replaceAll("\"", "");
        this.deviceFamily = jsonConfig.get(MYQ_FAMILY).toString().replaceAll("\"", "");

        logger.trace("readConfigFromJson, reading states"); 
        
        JsonObject jsonState = jsonConfig.get(MYQ_STATE).getAsJsonObject();
        
        if(jsonState != null){
            logger.trace("jsonState {}",jsonState); 
        }else{
            logger.trace("jsonState is null"); 
        }
        if(jsonState != null && jsonState.has(MYQ_DOORSTATE))
        {
            logger.trace("has {}",MYQ_DOORSTATE); 
            this.state = jsonState.get(MYQ_DOORSTATE).getAsString();
        }
        if(jsonState != null && jsonState.has(MYQ_LAMPSTATE))
        {
            logger.trace("has {}",MYQ_LAMPSTATE); 
            this.state = jsonState.get(MYQ_LAMPSTATE).getAsString();
        }
        if(jsonState != null && jsonState.has(MYQ_ONLINE))
        {
            logger.trace("has {}",MYQ_ONLINE); 
            this.online = Boolean.valueOf(jsonState.get(MYQ_ONLINE).toString());
        }
    }

    public String asString() {
        return ("Device Serial: " + serialNumber + "\n" + "Device Type: " + deviceType + "\n" 
                + "Device Family: " + deviceFamily + "\n" 
                + "\n" + "Name:  " + name + "\n" + "State:  " + state + "\n" + "Online:  " + online + "\n");
    }

    public boolean validateConfig() {
        if (this.serialNumber == null) {
            return false;
        }
        return true;
    }

    public String getDeviceStatus() {
                return state;
    }

    public OnOffType getDoorStatusOnOff() {
        if (isDoorClosed()) {
            return OnOffType.OFF;
        } else {
            return OnOffType.ON;
        }
    }

    public OnOffType getLightStatusOnOff() {
        if (state.compareTo("on") == 0) {
            return OnOffType.ON;
        } else {
            return OnOffType.OFF;
        }
    }

    public PercentType getDeviceStatusPercent() {
        if (isDoorOpen()) {
            return PercentType.ZERO;
        }
        if (isDoorClosed()) {
            return PercentType.HUNDRED;
        }
        return new PercentType(50);
    }

    public OpenClosedType isDoorClosedContact() {
        if (isDoorClosed()) {
            return OpenClosedType.CLOSED;
        }
        return OpenClosedType.OPEN;
    }

    public OpenClosedType isDoorOpenContact() {
        if (isDoorOpen()) {
            return OpenClosedType.CLOSED;
        }
        return OpenClosedType.OPEN;
    }

    public boolean isDoorClosed() {
        if (state.compareTo("closed") == 0) {
            return true;
        }
        return false;
    }

    public boolean isDoorOpen() {
        if (state.compareTo("open") == 0) {
            return true;
        }
        return false;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getName() {
        return name;
    }

    public String getDeviceType() {
        return deviceType;
    }
    
    public String getDeviceFamily() {
        return deviceFamily;
    }

    public String getState() {
        return state;
    }

    public boolean getOnline() {
        return online;
    }

    public ThingStatus getThingOnline() {
        if (online) {
            return ThingStatus.ONLINE;
        } else {
            return ThingStatus.OFFLINE;
        }
    }

    public Map<String, Object> getProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(MYQ_TYPE, getDeviceType());
        properties.put(MYQ_SERIAL, getSerialNumber());
        properties.put(MYQ_STATE, getState());
        properties.put(MYQ_NAME, getName());
        properties.put(MYQ_ONLINE, getOnline());
        properties.put(MYQ_FAMILY, getDeviceFamily());
        return properties;
    }
}
