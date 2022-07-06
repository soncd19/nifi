package org.apache.nifi.cdc.rdbms.utils;

/**
 * Created by SonCD on 4/14/2020
 */
public class CDCSourceConstants {

    public static final String RDBMS_MYSQL ="mysql";
    public static final String RDBMS_SQL_SERVER ="sqlserver";
    public static final String RDBMS_POSTGRESQL ="postgresql";
    public static final String RDBMS_ORACLE ="oracle";
    public static final String NOSQL_MONGODB ="mongodb";


    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String DATABASE_CONNECTION_URL = "url";
    public static final String POOL_PROPERTIES = "pool.properties";
    public static final String OPERATION = "operation";
    public static final String DB_TYPE = "db_type";
    public static final String TS_MS = "ts_ms";
    public static final String DATABASE_HISTORY_FILEBASE_HISTORY = "io.debezium.relational.history.FileDatabaseHistory";
    public static final String DATABASE_HISTORY_FILE_NAME = "database.history.file.filename";
    public static final String OFFSET_STORAGE_FILE_NAME= "offset.storage.file.filename";
    public static final String DATABASE_SERVER_NAME = "database.server.name";
    public static final String DATABASE_SERVER_ID = "database.server.id";
    public static final String SERVER_ID = "server.id";
    public static final String TABLE_NAME = "table.name";
    public static final String CONNECTOR_PROPERTIES = "connector.properties";
    public static final String EMPTY_STRING = "";
    public static final String INSERT = "insert";
    public static final String UPDATE = "update";
    public static final String DELETE = "delete";
    public static final String CONNECTOR_CLASS = "connector.class";
    public static final String DATABASE_PORT = "database.port";
    public static final String TABLE_WHITELIST = "table.whitelist";
    public static final String DATABASE_DBNAME = "database.dbname";
    public static final String DATABASE_HOSTNAME = "database.hostname";
    public static final String DATABASE_USER = "database.user";
    public static final String DATABASE_PASSWORD = "database.password";
    public static final String OFFSET_STORAGE = "offset.storage";
    public static final String CDC_SOURCE_OBJECT = "cdc.source.object";
    public static final String DATABASE_HISTORY = "database.history";
    public static final String DATABASE_PDB_NAME = "database.pdb.name";
    public static final String DATABASE_OUT_SERVER_NAME = "database.out.server.name";
    public static final String TIME_PRECISION_MODE = "time.precision.mode";
    public static final String ORACLE_OUTSERVER_PROPERTY_NAME = "database.out.server.name";
    public static final String ORACLE_PDB_PROPERTY_NAME = "database.pdb.name";
    public static final String CONNECTOR_NAME = "name";


    public static final String MYSQL_CONNECTOR_CLASS = "io.debezium.connector.mysql.MySqlConnector";
    public static final String POSTGRESQL_CONNECTOR_CLASS = "io.debezium.connector.postgresql.PostgresConnector";
    public static final String SQLSERVER_CONNECTOR_CLASS = "io.debezium.connector.sqlserver.SqlServerConnector";
    public static final String ORACLE_CONNECTOR_CLASS = "io.debezium.connector.oracle.OracleConnector";
    public static final String MONGODB_CONNECTOR_CLASS = "io.debezium.connector.mongodb.MongoDbConnector";


    public static final String BEFORE_PREFIX = "before_";
    public static final String CACHE_OBJECT = "cacheObj";
    public static final String LAST_OFFSET = "last.offset";
    public static final int DEFAULT_SERVER_ID = -1;
    public static final String CONNECT_RECORD_OPERATION = "op";
    public static final String CONNECT_RECORD_INSERT_OPERATION = "c";
    public static final String CONNECT_RECORD_UPDATE_OPERATION = "u";
    public static final String CONNECT_RECORD_DELETE_OPERATION = "d";
    public static final String BEFORE = "before";
    public static final String AFTER = "after";
    public static final String JDBC_DRIVER_NAME = "jdbc.driver.name";
    public static final String DATASOURCE_NAME = "datasource.name";
    public static final String USER_DIRECTORY = "user.dir";

    public static final String CDC_DATABASE_NAME ="database_name";
    public static final String CDC_TABLE_NAME ="table_name";
    public static final String CDC_OPERATION ="operation";
    public static final String CDC_INSERT ="insert";
    public static final String CDC_UPDATE ="update";
    public static final String CDC_DELETE ="delete";

    public static final String CDC_DATA ="data";


    //MongoDB
    public static final String MONGODB_USER = "mongodb.user";
    public static final String MONGODB_PASSWORD = "mongodb.password";
    public static final String MONGODB_HOSTS = "mongodb.hosts";
    public static final String MONGODB_NAME = "mongodb.name";
    public static final String MONGODB_COLLECTION_WHITELIST = "collection.whitelist";

    public static final String MONGO_COLLECTION_OBJECT_ID = "$oid";
    public static final String MONGO_COLLECTION_ID = "id";
    public static final String MONGO_COLLECTION_INSERT_ID = "_id";
    public static final String MONGO_PATCH = "patch";
    public static final String MONGO_FILTER = "filter";
    public static final String MONGO_SET = "$set";
    public static final String MONGO_OBJECT_NUMBER_LONG = "$numberLong";
    public static final String MONGO_OBJECT_NUMBER_DECIMAL = "$numberDecimal";
    public static final String MONGO_UPDATE_ID = "update_id";
    public static final String MONGO_DELETE_ID = "delete_id";
}
