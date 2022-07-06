package org.apache.nifi.cdc.rdbms.processors;

import io.debezium.embedded.EmbeddedEngine;
import org.apache.nifi.annotation.behavior.*;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.annotation.lifecycle.OnShutdown;
import org.apache.nifi.annotation.lifecycle.OnStopped;
import org.apache.nifi.annotation.lifecycle.OnUnscheduled;
import org.apache.nifi.cdc.rdbms.event.CDCEventListener;
import org.apache.nifi.cdc.rdbms.exception.NifiCreationException;
import org.apache.nifi.cdc.rdbms.listening.ChangeDataCapture;
import org.apache.nifi.cdc.rdbms.listening.MongoDBChangeDataCapture;
import org.apache.nifi.cdc.rdbms.listening.RdbmsChangeDataCapture;
import org.apache.nifi.cdc.rdbms.output.DataOutputType;
import org.apache.nifi.cdc.rdbms.output.JSONOutputType;
import org.apache.nifi.cdc.rdbms.output.XMLOutputType;
import org.apache.nifi.cdc.rdbms.utils.CDCSourceConstants;
import org.apache.nifi.cdc.rdbms.utils.CDCSourceUtil;
import org.apache.nifi.cdc.rdbms.exception.WrongConfigurationException;
import org.apache.nifi.cdc.rdbms.connection.ManagerConnections;
import org.apache.nifi.cdc.rdbms.utils.scd.SlowlyChangingDimensions;
import org.apache.nifi.cdc.rdbms.utils.tables.TablesRdbms;
import org.apache.nifi.cdc.rdbms.utils.tables.TablesRdbmsFactory;
import org.apache.nifi.cdc.rdbms.utils.tables.TablesSqlServer;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.components.state.Scope;
import org.apache.nifi.expression.ExpressionLanguageScope;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.flowfile.attributes.CoreAttributes;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.*;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by SonCD on 4/14/2020
 * Change by DuyNVT on 05/08/2020 (MM/DD/YYYY)
 */
@InputRequirement(InputRequirement.Requirement.INPUT_FORBIDDEN)
@Tags({"cdc", "rdbms", "nosql"})
@CapabilityDescription("Retrieves Change Data Capture (CDC) events from a RDBMS(MySQL, SQL Server, PostgreSQL, Oracle), NoSQL(MongoDB) database. CDC Events include INSERT, UPDATE, DELETE operations. Events "
        + "are output as individual flow files ordered by the time at which the operation occurred.")
@Stateful(scopes = Scope.CLUSTER, description = "Information such as a 'pointer' to the current CDC event in the database is stored by this processor, such "
        + "that it can continue from the same location if restarted.")
@WritesAttributes({
        @WritesAttribute(attribute = "mime.type", description = "The processor outputs flow file content in JSON format, and sets the mime.type attribute to "
                + "application/json")
})
public class CaptureDataChangeSource extends AbstractProcessor implements CDCEventListener<DataOutputType> {

    public static final Relationship REL_SUCCESS = new Relationship.Builder()
            .name("success")
            .description("Successfully created FlowFile from SQL query result set.")
            .build();

    static final PropertyDescriptor DB_TYPE = new PropertyDescriptor.Builder()
            .name("db-type")
            .displayName("Database Type")
            .description("database type for capture change data")
            .required(true)
            .allowableValues(CDCSourceConstants.RDBMS_MYSQL, CDCSourceConstants.RDBMS_SQL_SERVER,
                    CDCSourceConstants.RDBMS_POSTGRESQL, CDCSourceConstants.RDBMS_ORACLE, CDCSourceConstants.NOSQL_MONGODB)
            .build();

    public static final PropertyDescriptor HOST = new PropertyDescriptor.Builder()
            .name("capture-change-rdbms-hostname")
            .displayName("Host Name")
            .description("host to access the MySQL cluster")
            .required(false)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();

    public static final PropertyDescriptor PORT = new PropertyDescriptor.Builder()
            .name("capture-change-rdbms-port")
            .displayName("Port")
            .description("port to access the MySQL cluster")
            .required(false)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();

    public static final PropertyDescriptor USERNAME = new PropertyDescriptor.Builder()
            .name("capture-change-rdbms-username")
            .displayName("Username")
            .description("Password to access the MySQL cluster")
            .required(false)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();

    public static final PropertyDescriptor PASSWORD = new PropertyDescriptor.Builder()
            .name("capture-change-rdbms-password")
            .displayName("Password")
            .description("Password to access the MySQL cluster")
            .required(false)
            .sensitive(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .expressionLanguageSupported(ExpressionLanguageScope.VARIABLE_REGISTRY)
            .build();

    public static final PropertyDescriptor DATABASE_NAME_PATTERN = new PropertyDescriptor.Builder()
            .name("capture-change-rdbms-db-name-pattern")
            .displayName("Database/Schema Name Pattern")
            .description("A regular expression (regex) for matching databases (or schemas, depending on your RDBMS' terminology) against the list of CDC events")
            .required(false)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();

    public static final PropertyDescriptor CAPTURE_ALL_TABLES = new PropertyDescriptor.Builder()
            .name("capture-data-change-all-tables")
            .displayName("Capture all tables")
            .description("Allows capture data change all tables in schema")
            .allowableValues("true", "false")
            .defaultValue("false")
            .required(false)
            .build();

    public static final PropertyDescriptor TABLE_NAME_PATTERN = new PropertyDescriptor.Builder()
            .name("capture-change-rdbms-name-pattern")
            .displayName("Table Name Pattern")
            .description("A regular expression (regex) for matching CDC events affecting matching tables. The regex must match the table name as it is stored in the database. "
                    + "If the property is not set, no events will be filtered based on table name.")
            .required(false)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();

    public static final PropertyDescriptor FORMAT_OUTPUT = new PropertyDescriptor.Builder()
            .name("format-output")
            .displayName("Format Output")
            .description("Standard format output is Xml or json. Default is json")
            .allowableValues("json", "xml")
            .defaultValue("json")
            .required(false)
            .build();

    public static final PropertyDescriptor SERVER_ID = new PropertyDescriptor.Builder()
            .name("capture-change-rdbms-server-id")
            .displayName("Server ID")
            .description("The client connecting to the MySQL replication group is actually a simplified slave (server), and the Server ID value must be unique across the whole replication "
                    + "group (i.e. different from any other Server ID being used by any master or slave). Thus, each instance of CaptureChangeMySQL must have a Server ID unique across "
                    + "the replication group. If the Server ID is not specified, it defaults to 65535.")
            .required(false)
            .addValidator(StandardValidators.POSITIVE_LONG_VALIDATOR)
            .build();

    private static List<PropertyDescriptor> propDescriptors;
    protected static Set<Relationship> relationships;

    private String nifiHome;
    private String historyFileDirectory;
    //private ChangeDataCapture changeDataCapture;
    private AtomicBoolean hasRun = new AtomicBoolean(false);

    private volatile LinkedBlockingQueue<DataOutputType> queue = new LinkedBlockingQueue<>();
    private ExecutorService executorService;
    //private EmbeddedEngine engine;

    private List<EmbeddedEngine> engines = new LinkedList<>();

    static {

        final Set<Relationship> r = new HashSet<>();
        r.add(REL_SUCCESS);
        relationships = Collections.unmodifiableSet(r);

        final List<PropertyDescriptor> pds = new ArrayList<>();
        pds.add(DB_TYPE);
        pds.add(HOST);
        pds.add(PORT);
        pds.add(USERNAME);
        pds.add(PASSWORD);
        pds.add(DATABASE_NAME_PATTERN);
        pds.add(CAPTURE_ALL_TABLES);
        pds.add(TABLE_NAME_PATTERN);
        pds.add(FORMAT_OUTPUT);
        pds.add(SERVER_ID);
        propDescriptors = Collections.unmodifiableList(pds);
    }


    @Override
    public Set<Relationship> getRelationships() {
        return relationships;
    }

    @Override
    protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return propDescriptors;
    }

    @Override
    protected void init(ProcessorInitializationContext context) {
        nifiHome = CDCSourceUtil.getNifiHome();
    }

    @Override
    public void onTrigger(final ProcessContext context, final ProcessSession session) throws ProcessException {
        final ComponentLog logger = getLogger();
        String formatType = context.getProperty(FORMAT_OUTPUT).getValue();

        SlowlyChangingDimensions scd = new SlowlyChangingDimensions(getLogger());
        while (hasRun.get()) {
            try {
                DataOutputType dataChange = queue.poll();
                if (dataChange != null) {
                    logger.info("SonCD: queue.poll(): " + dataChange.getValue());

                    FlowFile flowFile = session.create();

                    flowFile = session.write(flowFile, outputStream -> {
                        outputStream.write(dataChange.getValue().getBytes(StandardCharsets.UTF_8));
                    });

                    flowFile = session.putAttribute(flowFile, CoreAttributes.MIME_TYPE.key(),
                            formatType.equalsIgnoreCase("json") ? "application/json" : "application/xml");

                    session.transfer(flowFile, REL_SUCCESS);
                    session.commit();
                }
            } catch (Exception e) {
                logger.error("SonCD: Poll data change from queue and transfer error: " + e);
            }
        }
    }


    @OnScheduled
    public void onScheduled(ProcessContext processContext) {
        getLogger().info("SonCD: focus to onScheduled CDCRDBMSSource");
        //this.executorService = Executors.newSingleThreadExecutor();
        this.executorService = new ThreadPoolExecutor(10, 10, 0L,
                TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(10), new ThreadPoolExecutor.CallerRunsPolicy());
        hasRun.set(true);
        onSetup(processContext);
    }

    @OnUnscheduled
    public void onUnscheduled(ProcessContext processContext) {
        getLogger().info("SonCD: focus to onUnscheduled CDCRDBMSSource");
        hasRun.set(false);
    }

    private void onSetup(ProcessContext context) {
        getLogger().info("SonCD: focus to onSetup");
        String connectorProperties = CDCSourceConstants.EMPTY_STRING;

        String dbType = context.getProperty(DB_TYPE).getValue();
        String host = context.getProperty(HOST).getValue();
        String port = context.getProperty(PORT).getValue();

        String userName = context.getProperty(USERNAME).getValue();
        String password = context.getProperty(PASSWORD).getValue();

        String serverId = context.getProperty(SERVER_ID).getValue();
        String databaseName = context.getProperty(DATABASE_NAME_PATTERN).getValue();
        //String tableName = context.getProperty(TABLE_NAME_PATTERN).getValue();

        boolean captureAllTables = context.getProperty(CAPTURE_ALL_TABLES).asBoolean();
        String formatType = context.getProperty(FORMAT_OUTPUT).getValue();

        List<String> tables = new LinkedList<>();

        TablesRdbms tablesInDatabase = TablesRdbmsFactory.getRdbms(dbType, getLogger());
        ManagerConnections managerConn = new ManagerConnections(getLogger(), dbType, host, port, databaseName, userName, password);
        if(captureAllTables){
            try {
                tables = tablesInDatabase.getAllTables(managerConn);
            } catch (SQLException throwables) {
                getLogger().error("Duynvt: Cannot get all table in database " + databaseName + "[" + dbType +"]");
            }
        }
        else {
            tables = CDCSourceUtil.getTables(context.getProperty(TABLE_NAME_PATTERN).getValue());
        }

        // check sql server enable cdc table
        if(tablesInDatabase instanceof TablesSqlServer){
            getLogger().info("DuyNVT: Focus onSetup in CaptureDataChangeSource enable cdc Sql Server");
            ((TablesSqlServer) tablesInDatabase).enableCDC(managerConn, tables);
        }

        historyFileDirectory = nifiHome + File.separator + "cdc" + File.separator + "history"
                + File.separator + databaseName + File.separator;
        File directory = new File(historyFileDirectory);
        if (!directory.exists()) {
            boolean isCreate = directory.mkdirs();
            if (isCreate) {
                getLogger().info("created directory: " + historyFileDirectory);
            }
        }

        if (tables.size() > 0) {
            tables.forEach(table -> {
                try {
                    ChangeDataCapture changeDataCapture = dbType.toLowerCase(Locale.ENGLISH).contains(CDCSourceConstants.NOSQL_MONGODB)
                            ? new MongoDBChangeDataCapture(getLogger(), formatType.equalsIgnoreCase("xml") ? new XMLOutputType() : new JSONOutputType())
                            : new RdbmsChangeDataCapture(getLogger(), formatType.equalsIgnoreCase("xml") ? new XMLOutputType() : new JSONOutputType());

                    changeDataCapture.setEventListener(this);

                    Map<String, Object> configMap = CDCSourceUtil.getConfigMap(dbType, host, port, userName, password, databaseName, table, historyFileDirectory,
                            (serverId == null || serverId.isEmpty()) ? 1 : Integer.parseInt(serverId), "", connectorProperties, this.hashCode());
                    changeDataCapture.setConfig(configMap);

                    EmbeddedEngine.CompletionCallback completionCallback = (success, message, error) -> {
                        if (!success) {
                            getLogger().error("Connection to the database lost.", error);
                        }
                    };
                    EmbeddedEngine engine = changeDataCapture.getEngine(completionCallback);
                    engines.add(engine);
                    executorService.execute(engine);

                    getLogger().info("SonCD: focus to onSetup: run executorService success");

                } catch (WrongConfigurationException ex) {
                    queue.clear();
                    throw new NifiCreationException("The cdc source couldn't get started because of invalid" +
                            " configurations. Found configurations: {username='" + userName + "', password=******," +
                            " host='" + host + "', tablename='" + table + "'," +
                            " connetorProperties='" + connectorProperties + "'}", ex);
                }
            });
        }
    }


    @OnStopped
    public void onStop(ProcessContext processContext) {
        getLogger().info("SonCD: focus to onStop CDCRDBMSSource");
        queue.clear();
        //boolean isStop = engine.stop();
        if (engines.size() > 0) {
            engines.forEach(EmbeddedEngine::stop);
        }
        getLogger().info("SonCD: onStop EmbeddedEngine");
        hasRun.set(false);
        try {
            executorService.shutdown();
            while (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                getLogger().info("Waiting another 5 seconds for the embedded engine to shut down");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

//        if (isStop) {
//            getLogger().info("SonCD: onStop EmbeddedEngine");
//        }
    }

    @OnShutdown
    public void onShutdown(ProcessContext context) {
        getLogger().info("SonCD: focus to onShutdown CDCRDBMSSource");
        queue.clear();
        //boolean isStop = engine.stop();
        if (engines.size() > 0) {
            engines.forEach(EmbeddedEngine::stop);
        }
        getLogger().info("SonCD: onShutdown EmbeddedEngine");
        try {
            executorService.shutdown();
            while (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                getLogger().info("Waiting another 5 seconds for the embedded engine to shut down");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
//        if (isStop) {
//            getLogger().info("SonCD: onShutdown EmbeddedEngine");

//        }
    }

    @Override
    public void onEvent(DataOutputType event) {
        try {
            if (event != null && event.getMap().size() > 0){
                queue.put(event);
            }
        } catch (InterruptedException e) {
            getLogger().error("SonCD: add dataCDCEvent error " + e);
        }
    }

}
