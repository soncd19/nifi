package org.apache.nifi.cdc.rdbms.listening;

import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.DataException;
import org.apache.nifi.cdc.rdbms.output.DataOutputType;
import org.apache.nifi.cdc.rdbms.utils.CDCSourceConstants;
import org.apache.nifi.logging.ComponentLog;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import java.util.Map;

/**
 * Created by SonCD on 4/18/2020
 */
public class MongoDBChangeDataCapture extends ChangeDataCapture {
    public MongoDBChangeDataCapture(ComponentLog log, DataOutputType dataOutputType) {
        super(log, dataOutputType);
        //this.dataOutputType = dataOutputType;
    }

    @Override
    DataOutputType createMap(ConnectRecord connectRecord) {
        //Map<String, Object> detailsMap = new HashMap<>();
        Struct record = (Struct) connectRecord.value();
        String op;
        try {
            op = (String) record.get(CDCSourceConstants.CONNECT_RECORD_OPERATION);
        } catch (NullPointerException | DataException e) {
            return null;
        }

        Map<String, Object> dataEventChange = new HashMap<>();

        String databaseName = getConfiguration().getString(CDCSourceConstants.CDC_DATABASE_NAME);
        String tableName = getConfiguration().getString(CDCSourceConstants.CDC_TABLE_NAME);
        String db_type = getConfiguration().getString(CDCSourceConstants.DB_TYPE);
        dataEventChange.put(CDCSourceConstants.CDC_DATABASE_NAME, databaseName);
        dataEventChange.put(CDCSourceConstants.CDC_TABLE_NAME, tableName);
        dataEventChange.put(CDCSourceConstants.DB_TYPE, db_type);

        switch (op) {
            case CDCSourceConstants.CONNECT_RECORD_INSERT_OPERATION:{

                String insertString = (String) record.get(CDCSourceConstants.AFTER);
                JSONObject jsonObj = new JSONObject(insertString);

                //detailsMap = getMongoDetailMap(jsonObj);
                dataEventChange.put(CDCSourceConstants.CDC_OPERATION, CDCSourceConstants.CDC_INSERT);
                dataEventChange.put(CDCSourceConstants.AFTER, new JSONObject(getMongoDetailMap(jsonObj)));
                break;
            }
            case CDCSourceConstants.CONNECT_RECORD_DELETE_OPERATION: {
                String deleteDocumentId = (String) ((Struct) connectRecord.key())
                        .get(CDCSourceConstants.MONGO_COLLECTION_ID);
                JSONObject jsonObjId = new JSONObject(deleteDocumentId);
                /*detailsMap.put(CDCSourceConstants.MONGO_COLLECTION_ID,
                        jsonObjId.get(CDCSourceConstants.MONGO_COLLECTION_OBJECT_ID));*/

                dataEventChange.put(CDCSourceConstants.CDC_OPERATION, CDCSourceConstants.CDC_DELETE);
                dataEventChange.put(CDCSourceConstants.MONGO_DELETE_ID, jsonObjId.get(CDCSourceConstants.MONGO_COLLECTION_OBJECT_ID));

                break;
            }
            case CDCSourceConstants.CONNECT_RECORD_UPDATE_OPERATION: {

                String updateDocument = (String) record.get(CDCSourceConstants.MONGO_PATCH);
                JSONObject jsonObj1 = new JSONObject(updateDocument);
                JSONObject setJsonObj = (JSONObject) jsonObj1.get(CDCSourceConstants.MONGO_SET);
                //detailsMap = getMongoDetailMap(setJsonObj);
                String updateDocumentId = (String) ((Struct) connectRecord.key())
                        .get(CDCSourceConstants.MONGO_COLLECTION_ID);
                JSONObject jsonObjId1 = new JSONObject(updateDocumentId);

                /*detailsMap.put(CDCSourceConstants.MONGO_COLLECTION_ID,
                        jsonObjId1.get(CDCSourceConstants.MONGO_COLLECTION_OBJECT_ID));*/

                dataEventChange.put(CDCSourceConstants.CDC_OPERATION, CDCSourceConstants.CDC_UPDATE);
                dataEventChange.put(CDCSourceConstants.MONGO_UPDATE_ID, jsonObjId1.get(CDCSourceConstants.MONGO_COLLECTION_OBJECT_ID));
                dataEventChange.put(CDCSourceConstants.MONGO_PATCH, new JSONObject(getMongoDetailMap(setJsonObj)));

                break;
            }
            default:
                log.info("Provided value for \"op\" : " + op + " is not supported.");
                break;
        }
        DataOutputType dataOutputType = getDataOutputType();
        dataOutputType.setMap(dataEventChange);
        return dataOutputType;
    }


    private Map<String, Object> getMongoDetailMap(JSONObject jsonObj) {
        Map<String, Object> detailsMap = new HashMap<>();
        Iterator<String> keys = jsonObj.keys();
        for (Iterator<String> it = keys; it.hasNext(); ) {
            String key = it.next();
            if (jsonObj.get(key) instanceof Boolean) {
                detailsMap.put(key, jsonObj.getBoolean(key));
            } else if (jsonObj.get(key) instanceof Integer) {
                detailsMap.put(key, jsonObj.getInt(key));
            } else if (jsonObj.get(key) instanceof Long) {
                detailsMap.put(key, jsonObj.getDouble(key));
            } else if (jsonObj.get(key) instanceof Double) {
                detailsMap.put(key, jsonObj.getDouble(key));
            } else if (jsonObj.get(key) instanceof String) {
                detailsMap.put(key, jsonObj.getString(key));
            } else if (jsonObj.get(key) instanceof JSONObject) {
                try {
                    detailsMap.put(key, Long.parseLong((String) jsonObj.getJSONObject(key)
                            .get(CDCSourceConstants.MONGO_OBJECT_NUMBER_LONG)));
                } catch (JSONException notLongObjectEx) {
                    try {
                        detailsMap.put(key, Double.parseDouble((String) jsonObj.getJSONObject(key)
                                .get(CDCSourceConstants.MONGO_OBJECT_NUMBER_DECIMAL)));
                    } catch (JSONException notDoubleObjectEx) {
                        if (key.equals(CDCSourceConstants.MONGO_COLLECTION_INSERT_ID)) {
                            detailsMap.put(CDCSourceConstants.MONGO_COLLECTION_ID, jsonObj.getJSONObject(key)
                                    .get(CDCSourceConstants.MONGO_COLLECTION_OBJECT_ID));
                        } else {
                            detailsMap.put(key, jsonObj.getJSONObject(key).toString());
                        }
                    }
                }
            }
        }
        return detailsMap;
    }
}
