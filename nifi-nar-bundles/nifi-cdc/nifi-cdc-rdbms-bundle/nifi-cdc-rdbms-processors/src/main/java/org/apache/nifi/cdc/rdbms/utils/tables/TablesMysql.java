package org.apache.nifi.cdc.rdbms.utils.tables;

import org.apache.nifi.cdc.rdbms.connection.ManagerConnections;
import org.apache.nifi.logging.ComponentLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TablesMysql extends TablesRdbms {
    private ComponentLog log;
    public TablesMysql(ComponentLog log){
        super(log);
    }
}
