package org.apache.nifi.cdc.rdbms.output;

import org.json.JSONObject;

import java.util.Map;

public class JSONOutputType extends DataOutputType {
    @Override
    public String getValue() {
        Map<String, Object> dataEventChange = getMap();
        JSONObject jsonDataChange = new JSONObject(dataEventChange);
        return jsonDataChange.toString();
    }
}
