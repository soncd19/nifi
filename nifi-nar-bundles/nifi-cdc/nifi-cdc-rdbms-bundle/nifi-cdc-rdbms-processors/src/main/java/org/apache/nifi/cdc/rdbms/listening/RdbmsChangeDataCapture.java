package org.apache.nifi.cdc.rdbms.listening;

import io.debezium.data.VariableScaleDecimal;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.DataException;
import org.apache.nifi.cdc.rdbms.output.DataOutputType;
import org.apache.nifi.cdc.rdbms.utils.CDCSourceConstants;
import org.apache.nifi.logging.ComponentLog;
import org.json.JSONObject;
import org.json.XML;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by SonCD on 4/18/2020
 */
public class RdbmsChangeDataCapture extends ChangeDataCapture {
    public RdbmsChangeDataCapture(ComponentLog log, DataOutputType dataOutputType) {
        super(log, dataOutputType);
    }

    @Override
    DataOutputType createMap(ConnectRecord connectRecord) {
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
        long ts_ms = (Long) record.get(CDCSourceConstants.TS_MS);
        dataEventChange.put(CDCSourceConstants.TS_MS, ts_ms);
        Struct rawDetails;
        List<Field> fields;
        String fieldName;
        switch (op) {
            case CDCSourceConstants.CONNECT_RECORD_INSERT_OPERATION:{
                dataEventChange.put(CDCSourceConstants.CDC_OPERATION, CDCSourceConstants.CDC_INSERT);
                //dataEventChange.put(CDCSourceConstants.BEFORE, CDCSourceConstants.EMPTY_STRING);
                rawDetails = (Struct) record.get(CDCSourceConstants.AFTER);
                fields = rawDetails.schema().fields();

                Map<String, Object> dataAfter = new HashMap<>();

                for (Field key : fields) {
                    fieldName = key.name();
                    //detailsMap.put(fieldName, getValue(rawDetails.get(fieldName)));
                    dataAfter.put(fieldName, getValue(rawDetails.get(fieldName)));
                }
                dataEventChange.put(CDCSourceConstants.AFTER, dataAfter);
                break;
            }

            case CDCSourceConstants.CONNECT_RECORD_UPDATE_OPERATION: {
                dataEventChange.put(CDCSourceConstants.CDC_OPERATION, CDCSourceConstants.CDC_UPDATE);
                rawDetails = (Struct) record.get(CDCSourceConstants.BEFORE);
                fields = rawDetails.schema().fields();

                Map<String, Object> dataBefore = new HashMap<>();

                for (Field key : fields) {
                    fieldName = key.name();
                    //detailsMap.put(CDCSourceConstants.BEFORE_PREFIX + fieldName, getValue(rawDetails.get(fieldName)));
                    dataBefore.put(fieldName, getValue(rawDetails.get(fieldName)));

                }
                dataEventChange.put(CDCSourceConstants.BEFORE, dataBefore.toString());

                Map<String, Object> dataAfter = new HashMap<>();

                rawDetails = (Struct) record.get(CDCSourceConstants.AFTER);
                fields = rawDetails.schema().fields();
                for (Field key : fields) {
                    fieldName = key.name();
                    //detailsMap.put(fieldName, getValue(rawDetails.get(fieldName)));
                    dataAfter.put(fieldName, getValue(rawDetails.get(fieldName)));
                }
                dataEventChange.put(CDCSourceConstants.AFTER, dataAfter);
                break;
            }

            case CDCSourceConstants.CONNECT_RECORD_DELETE_OPERATION: {
                dataEventChange.put(CDCSourceConstants.CDC_OPERATION, CDCSourceConstants.CDC_DELETE);
                rawDetails = (Struct) record.get(CDCSourceConstants.BEFORE);
                fields = rawDetails.schema().fields();

                Map<String, Object> dataBefore = new HashMap<>();

                for (Field key : fields) {
                    fieldName = key.name();
                    //detailsMap.put(CDCSourceConstants.BEFORE_PREFIX + fieldName, getValue(rawDetails.get(fieldName)));
                    dataBefore.put(fieldName, getValue(rawDetails.get(fieldName)));
                }
                dataEventChange.put(CDCSourceConstants.BEFORE, dataBefore);
                break;
            }
        }

        DataOutputType dataOutputType = getDataOutputType();
        dataOutputType.setMap(dataEventChange);
        return dataOutputType;
    }


    private Object getValue(Object v) {
        if (v instanceof Struct) {
            Optional<BigDecimal> value = VariableScaleDecimal.toLogical((Struct) v).getDecimalValue();
            BigDecimal bigDecimal = value.orElse(null);
            if (bigDecimal == null) {
                return null;
            }
            return bigDecimal.longValue();
        }
        if (v instanceof Short) {
            return ((Short) v).intValue();
        }
        if (v instanceof Byte) {
            return ((Byte) v).intValue();
        }
        return v;
    }

}
