package org.apache.nifi.bsc.schema;

/**
 * Created by SonCD on 4/20/2020
 */
public class DataType {

    public enum HiveType {
        RECORD, ENUM, ARRAY, MAP, UNION, FIXED, STRING, VARCHAR, BYTES,
        INT, LONG, FLOAT, DOUBLE, BOOLEAN, DATE, TIMESTAMP, NULL,
        SMALLINT, BIGINT, DECIMAL, BINARY, CHAR;
        private String name;

        private HiveType() {
            this.name = this.name().toLowerCase();
        }

        public String getName() {
            return name;
        }
    }

}
