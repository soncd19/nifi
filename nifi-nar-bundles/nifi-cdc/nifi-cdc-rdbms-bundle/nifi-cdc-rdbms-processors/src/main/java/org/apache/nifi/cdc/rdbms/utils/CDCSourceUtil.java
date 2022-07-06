package org.apache.nifi.cdc.rdbms.utils;

import org.apache.kafka.connect.storage.FileOffsetBackingStore;
import org.apache.nifi.cdc.rdbms.exception.NifiCDCValidationException;
import org.apache.nifi.cdc.rdbms.exception.WrongConfigurationException;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by SonCD on 4/14/2020
 */
public class CDCSourceUtil {
    public static Map<String, Object> getConfigMap(String dbType, String host, String port, String username,
                                                   String password, String databaseName, String tableName,
                                                   String historyFileDirectory, int serverId, String serverName,
                                                   String connectorProperties, int cdcSourceHashCode)
            throws WrongConfigurationException {
        AtomicBoolean isNoSQL = new AtomicBoolean(false);
        Map<String, Object> configMap = new HashMap<>();

        String hostt;
        int portt;

        switch (dbType) {
            case CDCSourceConstants.RDBMS_MYSQL: {
                String regex = "jdbc:mysql://(\\w*|[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}):(\\d++)";
                Pattern p = Pattern.compile(regex);
                String url = "jdbc:mysql://" + host + ":" + port;
                Matcher matcher = p.matcher(url);
                if (matcher.find()) {
                    hostt = matcher.group(1);
                    portt = Integer.parseInt(matcher.group(2));

                } else {
                    throw new WrongConfigurationException("Invalid JDBC url: " + url + " received for mysql "
                            + ". Expected url format: jdbc:mysql://<host>:<port>/<database_name>");
                }

                configMap.put(CDCSourceConstants.DATABASE_HOSTNAME, hostt);
                configMap.put(CDCSourceConstants.DATABASE_PORT, portt);
                configMap.put(CDCSourceConstants.TABLE_WHITELIST, databaseName + "." + tableName);

                configMap.put(CDCSourceConstants.CONNECTOR_CLASS, CDCSourceConstants.MYSQL_CONNECTOR_CLASS);

                break;
            }
            case CDCSourceConstants.RDBMS_SQL_SERVER: {

                String regex = "jdbc:sqlserver://(\\w*|[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}):(\\d++)";
                Pattern p = Pattern.compile(regex);
                String url = "jdbc:sqlserver://" + host + ":" + port;
                Matcher matcher = p.matcher(url);
                if (matcher.find()) {
                    hostt = matcher.group(1);
                    portt = Integer.parseInt(matcher.group(2));
                } else {
                    throw new WrongConfigurationException("Invalid JDBC url: " + url + " received for sql server "
                            + " Expected url format: jdbc:sqlserver://<host>:<port>;" +
                            "databaseName=<database_name>");
                }

                configMap.put(CDCSourceConstants.DATABASE_HOSTNAME, hostt);
                configMap.put(CDCSourceConstants.DATABASE_PORT, portt);
                configMap.put(CDCSourceConstants.TABLE_WHITELIST, "dbo." + tableName);
                configMap.put(CDCSourceConstants.DATABASE_DBNAME, databaseName);
                configMap.put(CDCSourceConstants.TIME_PRECISION_MODE, "adaptive");

                configMap.put(CDCSourceConstants.CONNECTOR_CLASS, CDCSourceConstants.SQLSERVER_CONNECTOR_CLASS);

                break;
            }
            case CDCSourceConstants.RDBMS_POSTGRESQL: {
                String regex = "jdbc:postgresql://(\\w*|[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}):(\\d++)";
                Pattern p = Pattern.compile(regex);
                String url = "jdbc:postgresql://" + host + ":" + port;
                Matcher matcher = p.matcher(url);
                if (matcher.find()) {
                    hostt = matcher.group(1);
                    portt = Integer.parseInt(matcher.group(2));
                } else {
                    throw new WrongConfigurationException("Invalid JDBC url: " + url + " received for postgresql "
                            + " Expected url format: jdbc:postgresql://<host>:<port>/" +
                            "<database_name>");
                }

                configMap.put(CDCSourceConstants.DATABASE_HOSTNAME, hostt);
                configMap.put(CDCSourceConstants.DATABASE_PORT, portt);
                configMap.put(CDCSourceConstants.DATABASE_DBNAME, databaseName);
                configMap.put(CDCSourceConstants.TABLE_WHITELIST, "public." + tableName);

                configMap.put(CDCSourceConstants.CONNECTOR_CLASS, CDCSourceConstants.POSTGRESQL_CONNECTOR_CLASS);

                break;
            }

            case CDCSourceConstants.RDBMS_ORACLE: {
                String regex = "jdbc:oracle:(\\w*):\\/\\/([a-zA-Z0-9-_\\.]+):(\\d+)([\\/]?)([a-zA-Z0-9-_\\.]*)";
                Pattern p = Pattern.compile(regex);
                String url = "jdbc:oracle://" + host + ":" + port;
                Matcher matcher = p.matcher(url);

                if (matcher.find()) {
                    hostt = matcher.group(2);
                    portt = Integer.parseInt(matcher.group(3));

                } else {
                    throw new WrongConfigurationException("Invalid JDBC url: " + url + " received for oracle "
                            + " Expected url format: jdbc:oracle:<driver>://<host>:<port>/<sid>");
                }

                Map<String, String> connectorPropertiesMap = getConnectorPropertiesMap(connectorProperties);

                if (!connectorPropertiesMap.containsKey(CDCSourceConstants.ORACLE_OUTSERVER_PROPERTY_NAME)) {
                    throw new WrongConfigurationException("Required properties " +
                            CDCSourceConstants.ORACLE_OUTSERVER_PROPERTY_NAME + " is missing in the " +
                            CDCSourceConstants.CONNECTOR_PROPERTIES + " configurations.");
                }

                String pdbName = connectorPropertiesMap.get(CDCSourceConstants.ORACLE_PDB_PROPERTY_NAME);

                configMap.put(CDCSourceConstants.DATABASE_HOSTNAME, hostt);
                configMap.put(CDCSourceConstants.DATABASE_PORT, portt);
                configMap.put(CDCSourceConstants.TABLE_WHITELIST,
                        String.format("%s.%s", pdbName != null ? pdbName : databaseName, tableName));
                configMap.put(CDCSourceConstants.DATABASE_DBNAME, databaseName);
                configMap.put(CDCSourceConstants.CONNECTOR_CLASS, CDCSourceConstants.ORACLE_CONNECTOR_CLASS);

                break;
            }

            case CDCSourceConstants.NOSQL_MONGODB: {
                isNoSQL.set(true);
                String regex = "jdbc:mongodb://(\\w*)/([a-zA-Z0-9-_\\.]+):(\\d++)/(\\w*)";
                Pattern pattern = Pattern.compile(regex);
                String url = "jdbc:mongodb://" + host + ":" + port + "/" + databaseName;
                Matcher matcher = pattern.matcher(url);
                String replicaSetName;
                if (matcher.find()) {

                    replicaSetName = matcher.group(1);
                    hostt = matcher.group(2);
                    portt = Integer.parseInt(matcher.group(3));
                } else {
                    throw new WrongConfigurationException("Invalid MongoDB uri: received for stream:" +
                            " Expected uri format: jdbc:mongodb://<replica_set_name>/<host>:<port>/" + "<database_name>");
                }

                configMap.put(CDCSourceConstants.CONNECTOR_CLASS, CDCSourceConstants.MONGODB_CONNECTOR_CLASS);
                configMap.put(CDCSourceConstants.MONGODB_HOSTS, hostt + ":" + portt);
                configMap.put(CDCSourceConstants.MONGODB_NAME, replicaSetName);
                configMap.put(CDCSourceConstants.MONGODB_COLLECTION_WHITELIST, databaseName + "." + tableName);

                break;
            }

            default: {
                throw new WrongConfigurationException("unsupported schema: Exception schema type: " + dbType + ", host: " + host + ", port: " + port);
            }
        }

        if (!isNoSQL.get()) {
            configMap.put(CDCSourceConstants.DATABASE_USER, username);
            configMap.put(CDCSourceConstants.DATABASE_PASSWORD, password);
        } else {
            configMap.put(CDCSourceConstants.MONGODB_USER, username);
            configMap.put(CDCSourceConstants.MONGODB_PASSWORD, password);
        }


        if (serverId == CDCSourceConstants.DEFAULT_SERVER_ID) {
            Random random = new Random();
            configMap.put(CDCSourceConstants.SERVER_ID, random.nextInt(1001) + 5400);
        } else {
            configMap.put(CDCSourceConstants.SERVER_ID, serverId);
        }

        if (serverName.equals("")) {
            configMap.put(CDCSourceConstants.DATABASE_SERVER_NAME, host + "_" + port);
        } else {
            configMap.put(CDCSourceConstants.DATABASE_SERVER_NAME, serverName);
        }

        configMap.put(CDCSourceConstants.OFFSET_STORAGE, FileOffsetBackingStore.class.getName());
        configMap.put(CDCSourceConstants.OFFSET_STORAGE_FILE_NAME, historyFileDirectory + "offsets.dat");
        configMap.put(CDCSourceConstants.CDC_SOURCE_OBJECT, cdcSourceHashCode);

        configMap.put(CDCSourceConstants.DATABASE_HISTORY, CDCSourceConstants.DATABASE_HISTORY_FILEBASE_HISTORY);
        configMap.put(CDCSourceConstants.DATABASE_HISTORY_FILE_NAME,
                historyFileDirectory + databaseName + tableName + ".dat");

        configMap.put(CDCSourceConstants.CONNECTOR_NAME, databaseName + tableName);

        configMap.put(CDCSourceConstants.CDC_DATABASE_NAME, databaseName);
        configMap.put(CDCSourceConstants.CDC_TABLE_NAME, tableName);
        configMap.put(CDCSourceConstants.DB_TYPE, dbType);

        for (Map.Entry<String, String> entry : getConnectorPropertiesMap(connectorProperties).entrySet()) {
            configMap.put(entry.getKey(), entry.getValue());
        }

        return configMap;
    }

    private static Map<String, String> getConnectorPropertiesMap(String connectorProperties) {

        Map<String, String> connectorPropertiesMap = new HashMap<>();

        if (!connectorProperties.isEmpty()) {
            String[] keyValuePairs = connectorProperties.split(",");
            for (String keyValuePair : keyValuePairs) {
                String[] keyAndValue = keyValuePair.split("=");
                if (keyAndValue.length != 2) {
                    throw new NifiCDCValidationException("connector.properties input is invalid. Check near :" +
                            keyValuePair);
                } else {
                    connectorPropertiesMap.put(keyAndValue[0], keyAndValue[1]);
                }
            }
        }
        return connectorPropertiesMap;
    }

    public static String getNifiHome() {
        return System.getProperty(CDCSourceConstants.USER_DIRECTORY);
    }


    public static List<String> getTables(final String value) {
        if (value == null || value.length() == 0 || value.trim().length() == 0) {
            return null;
        }
        final List<String> tables = new LinkedList<>();
        for (String table : value.split(";")) {
            if (table.trim().length() > 0) {
                tables.add(table.trim());
            }
        }
        return tables;
    }
}
