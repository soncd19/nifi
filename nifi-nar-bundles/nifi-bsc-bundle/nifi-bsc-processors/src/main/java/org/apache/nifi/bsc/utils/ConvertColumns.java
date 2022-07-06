package org.apache.nifi.bsc.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.nifi.bsc.exception.BuildHiveSchemaException;
import org.apache.nifi.bsc.schema.HiveSchema;
import org.apache.nifi.bsc.schema.SchemaFactory;
import org.apache.nifi.logging.ComponentLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by SonCD on 4/20/2020
 */
public class ConvertColumns {

    public static interface Builder {

        Builder componentLog(ComponentLog log);

        Builder productName(String productName);

        Builder targetDatabaseName(String dbName);

        Builder tableName(String tableName);

        Builder columns(Map<String, String> columns);

        String build() throws BuildHiveSchemaException;
    }


    public static Builder create() {
        return new Builder() {
            private ComponentLog log;
            private String productName;
            private String dbName;
            private String tableName;
            private Map<String, String> columns;

            @Override
            public Builder componentLog(ComponentLog log) {
                this.log = log;
                return this;
            }

            @Override
            public Builder productName(String productName) {
                this.productName = productName;
                return this;
            }

            @Override
            public Builder targetDatabaseName(String dbName) {
                this.dbName = dbName;
                return this;
            }

            @Override
            public Builder tableName(String tableName) {
                this.tableName = tableName;
                return this;
            }

            @Override
            public Builder columns(Map<String, String> columns) {
                this.columns = columns;
                return this;
            }

            @Override
            public String build() throws BuildHiveSchemaException {
                try {

                    log.info("SonCD: focus to ConvertColumns.Builder.build");

                    SchemaFactory schemaFactory = SchemaFactory.getSchemaFactory(this.productName);
                    HiveSchema hiveSchema = schemaFactory.createHiveSchema(columns);
                    List<String> hiveColumns = new ArrayList<>();
                    List<HiveSchema.Field> fields = hiveSchema.getFields();

                    StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
                    sb.append(dbName).append(".").append(tableName).append("(");

                    if (fields != null && fields.size() > 0) {
                        hiveColumns.addAll(fields.stream().map(field -> field.name + " " + field.type).collect(Collectors.toList()));
                        sb.append(StringUtils.join(hiveColumns, ","));
                        sb.append(") STORED AS ORC");
                    }
                    return sb.toString();
                } catch (BuildHiveSchemaException e) {
                    log.error("SonCD: BuildHiveSchemaException: " + e);
                    throw new BuildHiveSchemaException("SonCD: cannot build hive schema: " + e);
                }
            }
        };
    }




}
