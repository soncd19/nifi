package org.apache.nifi.cdc.rdbms.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.nifi.cdc.rdbms.utils.CDCSourceConstants;
import org.apache.nifi.logging.ComponentLog;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
* @author duynvt
* @since 19/05/2020
* */

public class ManagerConnections {
    private ComponentLog log;
    private String dbType;
    private String host;
    private String port;
    private String dbName;
    private String username;
    private String password;
    private DataSource dataSource;

    public ManagerConnections(ComponentLog log, String dbType, String host, String port,
                              String dbName, String username, String password) {
        this.log = log;
        this.dbType = dbType;
        this.host = host;
        this.port = port;
        this.dbName = dbName;
        this.username = username;
        this.password = password;
        createConnection();
    }

    public ComponentLog getLog() {
        return log;
    }

    public String getDbType() {
        return dbType;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getDbName() {
        return dbName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    private void createConnection() {
        HikariConfig config = new HikariConfig();

        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(5);

        StringBuilder url = new StringBuilder();

        switch (dbType){
            case CDCSourceConstants.RDBMS_MYSQL:

                url.append("jdbc:mysql://").append(host).append(":").
                        append(port).append("/").
                        append(dbName).append("?").
                        append("useSSL").append("=").append(false);

                config.setJdbcUrl(url.toString());
                config.setDriverClassName("com.mysql.jdbc.Driver");

                log.info("DuyNVT: ManagerConnection/createConnection connection Mysql");
                break;

            case CDCSourceConstants.RDBMS_SQL_SERVER:

                url.append("jdbc:sqlserver://").
                        append(host).append(":").
                        append(port).append(";").
                        append("databaseName").append("=").append(dbName).append(";").
                        append("user=").append(username).append(";").
                        append("password").append("=").append(password);

                config.setJdbcUrl(url.toString());
                config.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                log.info("DuyNVT: ManagerConnection/createConnection connection Sql Server");

                break;

            case CDCSourceConstants.RDBMS_POSTGRESQL:

                url.append("jdbc:postgresql://").append(host).append(":").append(port).append("/").append(dbName);

                config.setJdbcUrl(url.toString());
                config.setDriverClassName("org.postgresql.Driver");
                log.info("DuyNVT: ManagerConnection/createConnection connection Postgre");

                break;

            default:
        }

        this.dataSource = new HikariDataSource(config);
    }

    public Connection getConnection(){
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
        } catch (SQLException throwables) {
            log.error("DuyNVT: Focus Class: ManagerConnection & method getConnection -- Connection lost");
        }
        return connection;
    }

}
