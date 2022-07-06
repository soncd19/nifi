package org.apache.nifi.cdc.rdbms.utils.tables;

import org.apache.nifi.cdc.rdbms.connection.ManagerConnections;

import java.sql.SQLException;
import java.util.List;

public interface TablesDatabase {
    List<String> getAllTables(ManagerConnections managerConnections) throws SQLException;
}
