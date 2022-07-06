package org.apache.nifi.cdc.rdbms.utils.tables;

import org.apache.nifi.logging.ComponentLog;

public class TablesRdbmsFactory {
    public static TablesRdbms getRdbms(String type, ComponentLog componentLog){
        if ("mysql".equalsIgnoreCase(type)){
            return new TablesMysql(componentLog);
        }
        else if ("sqlserver".equalsIgnoreCase(type)){
            return new TablesSqlServer(componentLog);
        }
        else if ("postgresql".equalsIgnoreCase(type)){
            return new TablesPostgreSql(componentLog);
        }
        return null;
    }
}
