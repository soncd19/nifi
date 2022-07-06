package org.apache.nifi.bsc.schema;

/**
 * Created by SonCD on 4/20/2020
 */
public class MySQLSchema extends SchemaFactory {

    @Override
    public HiveSchema.Field getField(String name, String type) {
        DataType.HiveType newType;
        switch (type) {
            case "INT":
                newType = DataType.HiveType.INT;
                break;
            case "DOUBLE":
                newType = DataType.HiveType.DOUBLE;
                break;
            case "FLOAT":
                newType = DataType.HiveType.FLOAT;
                break;
            case "BIT":
                newType = DataType.HiveType.BOOLEAN;
                break;
            case "VARCHAR":
                newType = DataType.HiveType.STRING;
                break;
            case "DATE":
                newType = DataType.HiveType.DATE;
                break;
            case "DATETIME":
                newType = DataType.HiveType.TIMESTAMP;
                break;
            default:
                newType = DataType.HiveType.STRING;
        }
        return new HiveSchema.Field(name, newType);
    }
}
