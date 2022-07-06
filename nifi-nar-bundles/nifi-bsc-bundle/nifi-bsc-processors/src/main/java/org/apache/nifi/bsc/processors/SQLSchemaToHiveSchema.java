package org.apache.nifi.bsc.processors;

import org.apache.nifi.annotation.behavior.EventDriven;
import org.apache.nifi.annotation.behavior.InputRequirement;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.bsc.exception.BuildHiveSchemaException;
import org.apache.nifi.bsc.utils.BSCConstants;
import org.apache.nifi.bsc.utils.ConvertColumns;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.dbcp.DBCPService;
import org.apache.nifi.dbcp.hive.Hive3DBCPService;
import org.apache.nifi.dbcp.hive.HiveDBCPService;
import org.apache.nifi.expression.ExpressionLanguageScope;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by SonCD on 4/20/2020
 */

@EventDriven
@InputRequirement(InputRequirement.Requirement.INPUT_ALLOWED)
@Tags({"sql", "hive", "convert"})
@CapabilityDescription("Execute provided convert table struct from RDBMS to table hive struct")

public class SQLSchemaToHiveSchema extends AbstractProcessor {


    public static final Relationship REL_SUCCESS = new Relationship.Builder()
            .name("success")
            .description("Successfully convert table rdbms to hive.")
            .build();

    public static final Relationship REL_FAILURE = new Relationship.Builder()
            .name("failure")
            .description("convert table rdbms to hive failed.")
            .build();

    public static final PropertyDescriptor HIVE_DBCP_SERVICE = new PropertyDescriptor.Builder()
            .name("Hive Database Connection Pooling Service")
            .description("The Hive Controller Service that is used to obtain connection(s) to the Hive database")
            .required(true)
            .identifiesControllerService(Hive3DBCPService.class)
            .build();

    public static final PropertyDescriptor DBCP_SERVICE = new PropertyDescriptor.Builder()
            .name("Database Connection Pool Service")
            .description("The Controller Service that is used to obtain connection to database")
            .required(true)
            .identifiesControllerService(DBCPService.class)
            .build();

    static final PropertyDescriptor ALL_TABLE = new PropertyDescriptor.Builder()
            .name("convert-all-table")
            .displayName("Convert All Tables")
            .description("Convert All table from RDBMS to Hive")
            .required(true)
            .addValidator(StandardValidators.BOOLEAN_VALIDATOR)
            .allowableValues("true", "false")
            .defaultValue("false")
            .build();


    public static final PropertyDescriptor DATABASE_NAME_DBCP = new PropertyDescriptor.Builder()
            .name("database-name-rdbms")
            .displayName("Database Name Source RDBMS")
            .description("The database name from the rdbms source will be used to get the data tables")
            .required(false)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES)
            .build();

    public static final PropertyDescriptor DATABASE_NAME_HIVE = new PropertyDescriptor.Builder()
            .name("database-name-hive")
            .displayName("Database Name Hive")
            .description("The database name from the hhive target will be used to create tables")
            .required(false)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES)
            .build();

    public static final PropertyDescriptor TABLES_NAME = new PropertyDescriptor.Builder()
            .name("tables-name")
            .displayName("Tables Name")
            .description("List Table Name on RDBMS will be used create table to hive")
            .required(false)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES)
            .build();

    private final static List<PropertyDescriptor> propertyDescriptors;
    private final static Set<Relationship> relationships;

    static {
        List<PropertyDescriptor> _propertyDescriptors = new ArrayList<>();
        _propertyDescriptors.add(HIVE_DBCP_SERVICE);
        _propertyDescriptors.add(DBCP_SERVICE);
        _propertyDescriptors.add(ALL_TABLE);
        _propertyDescriptors.add(DATABASE_NAME_DBCP);
        _propertyDescriptors.add(DATABASE_NAME_HIVE);
        _propertyDescriptors.add(TABLES_NAME);
        propertyDescriptors = Collections.unmodifiableList(_propertyDescriptors);

        Set<Relationship> _relationships = new HashSet<>();
        _relationships.add(REL_SUCCESS);
        _relationships.add(REL_FAILURE);
        relationships = Collections.unmodifiableSet(_relationships);
    }

    @Override
    public Set<Relationship> getRelationships() {
        return relationships;
    }

    @Override
    protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return propertyDescriptors;
    }

    @Override
    public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
        final ComponentLog log = getLogger();

        log.info("SonCD forcus to SQLConvertToHive.onTrigger ");

        FlowFile flowfile = session.create();

        List<String> hivesQL = new ArrayList<>();

        Hive3DBCPService hiveDBCPService = context.getProperty(HIVE_DBCP_SERVICE).asControllerService(Hive3DBCPService.class);
        DBCPService rdbmsDBCPService = context.getProperty(DBCP_SERVICE).asControllerService(DBCPService.class);
        boolean isAllTable = context.getProperty(ALL_TABLE).asBoolean();

        String rdbmsDatabaseName = context.getProperty(DATABASE_NAME_DBCP).getValue();
        String hiveDatabaseName = context.getProperty(DATABASE_NAME_HIVE).getValue();

        try (Connection connection = rdbmsDBCPService.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String product_name = metaData.getDatabaseProductName();
            List<String> tableNames = new ArrayList<>();
            if (!isAllTable) {
                tableNames = getTables(context.getProperty(TABLES_NAME).getValue());
            } else {
                ResultSet tableSchema = metaData.getTables(rdbmsDatabaseName, null, "%", new String[]{"TABLE"});
                while (tableSchema.next()) {
                    tableNames.add(tableSchema.getString("TABLE_NAME"));
                }
            }

            if (tableNames.size() <= 0) {
                log.info("SonCD: List Table is empty");
                return;
            }
            FileWriter fileWriter = createSchemaFile(rdbmsDatabaseName);
            tableNames.forEach(table -> {
                try {
                    Map<String, String> columnsMap = new LinkedHashMap<>();

                    ResultSet rsColumns = metaData.getColumns(rdbmsDatabaseName, null, table, null);
                    while (rsColumns.next()) {
                        columnsMap.put(rsColumns.getString(BSCConstants.COLUMN_NAME), rsColumns.getString(BSCConstants.TYPE_NAME));
                    }

                    try {
                        String createTableHiveQL = ConvertColumns.create()
                                .componentLog(log)
                                .productName(product_name)
                                .targetDatabaseName(hiveDatabaseName)
                                .tableName(table)
                                .columns(columnsMap)
                                .build();
                        hivesQL.add(createTableHiveQL);

                        fileWriter.write(createTableHiveQL + "\n");
                    } catch (BuildHiveSchemaException | IOException e) {
                        log.error("SonCD: cannot create schema from table: " + table);
                    }
                    rsColumns.close();

                } catch (SQLException e) {
                    log.error("SonCD: tableName = " + table + " is not exits");
                }
            });
            fileWriter.close();

        } catch (ProcessException | SQLException | IOException e) {
            log.error("SonCD: create connection to RDMBS error " + e.getMessage());
        }
        if (hivesQL.size() > 0) {
            try (final Connection conn = hiveDBCPService.getConnection()) {
                hivesQL.forEach(sql -> {
                    try {
                        PreparedStatement statement = conn.prepareStatement(sql);
                        statement.execute();
                    } catch (SQLException e) {
                        log.error("SonCD: cannot execute hive schema: " + sql + " on hive database: " + hiveDatabaseName);
                    }
                });
            } catch (ProcessException | SQLException e) {
                log.error("SonCD: create connection to hiveDBCPService error " + e.getMessage());
            }
        }
        session.transfer(flowfile, REL_SUCCESS);
    }


    private FileWriter createSchemaFile(String dbName) throws IOException {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        String schemaFile = System.getProperty("user.dir") + File.separator + "schema_store" + File.separator +
                dbName + File.separator + "table-schema-" + dateFormat.format(cal.getTime()) + ".txt";
        return new FileWriter(new File(schemaFile));
    }


    protected List<String> getTables(final String value) {
        if (value == null || value.length() == 0 || value.trim().length() == 0) {
            return null;
        }
        final List<String> queries = new LinkedList<>();
        for (String query : value.split(";")) {
            if (query.trim().length() > 0) {
                queries.add(query.trim());
            }
        }
        return queries;
    }
}
