package org.apache.nifi.cdc.rdbms.utils.tables;

import org.apache.nifi.cdc.rdbms.connection.ManagerConnections;
import org.apache.nifi.cdc.rdbms.utils.CDCSourceConstants;
import org.apache.nifi.logging.ComponentLog;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class TablesRdbms implements TablesDatabase{
    protected ComponentLog log;

    public TablesRdbms(ComponentLog log){
        this.log = log;
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

            rs = metaData.getTables(managerConnections.getDbName(), null, "%", types);
            while (rs.next()) {
                String tableName = rs.getString(3);
                tables.add(tableName.trim());
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
}
