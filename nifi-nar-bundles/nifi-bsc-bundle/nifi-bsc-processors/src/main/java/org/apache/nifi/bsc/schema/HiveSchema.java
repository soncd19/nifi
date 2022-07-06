package org.apache.nifi.bsc.schema;

import java.util.List;

/**
 * Created by SonCD on 4/20/2020
 */
public class HiveSchema {

    private List<Field> fields;

    public HiveSchema(List<Field> fields) {
        this.fields = fields;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public static class Field {
        public String name;
        public DataType.HiveType type;

        public Field(String name, DataType.HiveType type) {
            this.name = name;
            this.type = type;
        }

    }
}
