package org.apache.nifi.cdc.rdbms.utils.scd;


import org.apache.nifi.cdc.rdbms.utils.CDCSourceConstants;
import org.apache.nifi.logging.ComponentLog;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class SlowlyChangingDimensions {

    private ComponentLog log;
    public SlowlyChangingDimensions(ComponentLog log){
        this.log = log;
    }
    public void enforceSCD(Map<String, Object> dataChange){

//        TablesRdbms tablesMysql = new TablesMysql(log);
//        try {
//            List<String> tables = tablesMysql.getAllTables(managerConnections);
//        } catch (SQLException throwables) {
//            log.error("DuyNVT: focus enforceSCD in SlowlyChangingDimensions -- can not get all tables");
//        }
//        Connection connection = managerConnections.getConnection();

        log.info("DuyNVT: focus SCD begin enforceSCD");

        Connection connection = getConnection();

        String sql = "INSERT INTO dim_employees (id, name, current_type, historical_type, active_start, active_end, current_flag)" +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        if (CDCSourceConstants.INSERT.equalsIgnoreCase((String) dataChange.get(CDCSourceConstants.OPERATION))) {
            Map<String, Object> record = (Map<String, Object>) dataChange.get(CDCSourceConstants.AFTER);

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dateInsertData = new Date();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setObject(1, record.get("id"));
                preparedStatement.setObject(2, record.get("name"));
                preparedStatement.setString(3, CDCSourceConstants.INSERT);
                preparedStatement.setString(4, "N/A");
                preparedStatement.setString(5, dateFormat.format(dateInsertData));
                preparedStatement.setString(6, "9999-12-31");
                preparedStatement.setString(7, "Y");
                preparedStatement.executeUpdate();

            } catch (SQLException throwables) {
                log.error("DuyNVT: focus SCD enforceSCD has error " + throwables);
            }
        }
        else if (CDCSourceConstants.UPDATE.equalsIgnoreCase((String) dataChange.get(CDCSourceConstants.OPERATION))) {

            Map<String, Object> recordAfter = (Map<String, Object>) dataChange.get(CDCSourceConstants.AFTER);
            Map<String, Object> recordBefore = (Map<String, Object>) dataChange.get(CDCSourceConstants.BEFORE);

            try {
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setObject(1, recordBefore.get("id"));
                preparedStatement.setObject(2, recordAfter.get("name"));
                preparedStatement.setString(3, CDCSourceConstants.UPDATE);
                //preparedStatement.setString(4, recordBefore.get(""));
                preparedStatement.setString(5, "");
                preparedStatement.setString(6, "");
                preparedStatement.setString(7, "");
                preparedStatement.executeUpdate();

            } catch (SQLException throwables) {
                log.error("DuyNVT: focus SCD enforceSCD has error " + throwables);
            }
        }
    }

    private Connection getConnection(){

        log.info("DuyNVT: focus SCD begin getConnection");
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/scd","root","123456a@");
        } catch (Exception e) {
            log.error("DuyNVT: focus SCD enforceSCD cannot connection to mysql");
        }
        return connection;
    }
}
