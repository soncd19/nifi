package org.apache.nifi.cdc.rdbms.utils.tables;

import org.apache.nifi.cdc.rdbms.connection.ManagerConnections;
import org.apache.nifi.logging.ComponentLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TablesSqlServer extends TablesRdbms {
    public TablesSqlServer(ComponentLog log){
        super(log);
    }

    @Override
    public List<String> getAllTables(ManagerConnections managerConnections) throws SQLException {

        Connection conn = null;
        ResultSet rs = null;
        List<String> tables = new ArrayList<String>();
        try {
            conn = managerConnections.getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            String[] types = {"TABLE"};

            rs = metaData.getTables(managerConnections.getDbName(), "dbo", "%", types);
            while (rs.next()) {
                String tableName = rs.getString(3);
                if(!tableName.equalsIgnoreCase("systranschemas")) {
                    tables.add(tableName.trim());
                }

                log.info("DuyNVT: table - " + tableName);
            }
            log.info("get all tables in " + managerConnections.getDbName() + " successful");

        } catch (Exception e) {
            log.error("DuyNVT: cannot get all tables in " + managerConnections.getDbName() + " [ " + managerConnections.getDbType() + " ]");
        } finally {
            if(conn != null){
                conn.close();
            }
            if(rs != null){
                rs.close();
            }
        }
        return tables;
    }

    public void enableCDC(ManagerConnections managerConnections, List<String> tables){

        Connection connection = null;

        log.info("Duynvt : focus to TableSqlServer method enableCDC");
        try {

            connection = managerConnections.getConnection();

            log.info("Duynvt focus to TableSqlServer method enableCDC get connection :" + connection.toString());

            if(!hasEnableCDCDb(connection, managerConnections.getDbName())){
                String enableCDCDbString = "EXEC ?";
                PreparedStatement ps = connection.prepareStatement(enableCDCDbString);
                ps.setString(1, "sys.sp_cdc_enable_db");
                ps.execute();

                log.info("DuyNVT: enable CDC for database: " + managerConnections.getDbName() + " success");
            }

            String enableCDCTableString = "EXEC ? " +
                    "@source_schema = ?, " +
                    "@source_name   = ?, " +
                    "@role_name     = ?, " +
                    "@capture_instance = ?, " +
                    "@supports_net_changes = ?" ;

            for(String table : tables){
                log.info("Duynvt focus to TableSqlServer method enableCDC execute for table " + table);

                if(!hasEnableCDCTable(connection, table)){
                    PreparedStatement ps = connection.prepareStatement(enableCDCTableString);
                    ps.setString(1, "sys.sp_cdc_enable_table ");
                    ps.setString(2,"dbo");
                    ps.setString(3, table);
                    ps.setString(4, "NULL");
                    ps.setString(5, "dbo_" + table);
                    ps.setString(6, "0");

                    ps.execute();

                    log.info("DuyNVT: enable cdc in table " + table + " success");
                }
            }

        } catch (SQLException throwables) {
            log.error("Cannot enable cdc in sql server -- error message: " + throwables.getMessage());
        }
        finally {
            if(connection != null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("Duynvt: Cannot close connection Sql server");
                }
            }
        }
    }

    public boolean hasEnableCDCTable(Connection connection, String table){
        ResultSet resultSet = null;

        try (Statement statement = connection.createStatement()) {

            String selectSql = "SELECT name FROM sys.tables WHERE is_tracked_by_cdc = 1";
            resultSet = statement.executeQuery(selectSql);

            while (resultSet.next()) {
                if (resultSet.getString(1).equals(table)) return true;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasEnableCDCDb(Connection connection, String dbName){
        ResultSet resultSet = null;

        try (Statement statement = connection.createStatement()) {

            String selectSql = "SELECT name FROM sys.databases WHERE is_cdc_enabled = 1";
            resultSet = statement.executeQuery(selectSql);

            while (resultSet.next()) {
                if (resultSet.getString(1).equals(dbName)) return true;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
