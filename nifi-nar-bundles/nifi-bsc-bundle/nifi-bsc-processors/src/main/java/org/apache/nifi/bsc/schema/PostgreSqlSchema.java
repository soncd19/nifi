package org.apache.nifi.bsc.schema;

/*
* @author DuyNVT
* @since 13/05/2020
* */
public class PostgreSqlSchema extends SchemaFactory{
    @Override
    public HiveSchema.Field getField(String name, String type) {
        DataType.HiveType newType;
        switch (type) {
            case "SMALINT":
                newType = DataType.HiveType.SMALLINT;
                break;
            case "INTEGER":
                newType = DataType.HiveType.INT;
                break;
            case "BIGINT":
                newType = DataType.HiveType.BIGINT;
                break;
            case "DECIMAL":
                newType = DataType.HiveType.DECIMAL;
                break;
            case "NUMERIC":
                newType = DataType.HiveType.DECIMAL; // ?
                break;
            case "REAL":
                newType = DataType.HiveType.FLOAT;
                break;
            case "DOUBLEPRECISION":
                newType = DataType.HiveType.DOUBLE;
                break;
//            case "SMALLSERIAL":
//                // ?
//                break;
//            case "SERIAL":
//                // ?
//                break;
//            case "BIGSERIAL":
//                // ?
//                break;
            case "MONEY":
                newType = DataType.HiveType.DECIMAL; // ?
                break;
            case "BYTEA":
                newType = DataType.HiveType.BINARY; // ?
                break;
            case "BIT":
                newType = DataType.HiveType.BYTES; // ?
                break;
            case "BIT VARYING":
                newType = DataType.HiveType.BYTES; // ?
                break;
            case "CHARACTER":
                newType = DataType.HiveType.CHAR;
                break;
            case "CHARACTER VARYING":
                newType = DataType.HiveType.VARCHAR;
                break;
            case "TEXT":
                newType = DataType.HiveType.STRING;
                break;
            case "TIMESTAMP":
                newType = DataType.HiveType.TIMESTAMP;
                break;
            case "TIMESTAMPTZ":
                newType = DataType.HiveType.TIMESTAMP;
                break;
            case "DATE":
                newType = DataType.HiveType.TIMESTAMP;
                break;
            case "TIME":
                newType = DataType.HiveType.TIMESTAMP; // ?
                break;
            case "TIME WITH TIME ZONE":
                newType = DataType.HiveType.TIMESTAMP; // ?
                break;
            case "INTERVAL":
                newType = DataType.HiveType.TIMESTAMP;
                break;
            case "BOOLEAN":
                newType = DataType.HiveType.BOOLEAN;
                break;
//            case "JSON":
//                // ?
//                break;
//            case "XML":
//                // ?
//                break;
            default:
                newType = DataType.HiveType.STRING;
        }
        return new HiveSchema.Field(name, newType);
    }
}
