package org.apache.nifi.bsc.schema;

import org.apache.nifi.bsc.utils.BSCConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by SonCD on 4/20/2020
 */
public abstract class SchemaFactory {

    public static SchemaFactory getSchemaFactory(String productName) {
        SchemaFactory schemaFactory = null;
        switch (productName) {
            case BSCConstants.MYSQL:
                schemaFactory = new MySQLSchema();
                break;
            case BSCConstants.MS_SQL:
                schemaFactory = new SQLServerSchema();
                break;
        }

        return schemaFactory;
    }

    public HiveSchema createHiveSchema(Map<String, String> columns) {
        List<HiveSchema.Field> fields = new ArrayList<>();
        for (Map.Entry<String, String> entry : columns.entrySet()) {
            String name = entry.getKey();
            String type = entry.getValue();
            HiveSchema.Field field = getField(name, type);
            fields.add(field);
        }

        return new HiveSchema(fields);
    }

    public abstract HiveSchema.Field getField(String name, String type);


}
