package org.apache.nifi.cdc.rdbms.output;

import java.util.Map;

public abstract class DataOutputType {

    private Map<String, Object> dataEventChange;

    public void setMap(Map<String, Object> map) {
        this.dataEventChange = map;
    }

    public Map<String, Object> getMap() {
        return dataEventChange;
    }

    public abstract String getValue();

}
