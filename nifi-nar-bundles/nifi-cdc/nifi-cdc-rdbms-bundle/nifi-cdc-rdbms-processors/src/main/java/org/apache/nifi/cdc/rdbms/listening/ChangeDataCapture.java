package org.apache.nifi.cdc.rdbms.listening;

import io.debezium.config.Configuration;
import io.debezium.embedded.EmbeddedEngine;
import io.debezium.embedded.spi.OffsetCommitPolicy;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.nifi.cdc.rdbms.event.CDCEventListener;
import org.apache.nifi.cdc.rdbms.exception.NifiCreationException;
import org.apache.nifi.cdc.rdbms.output.DataOutputType;
import org.apache.nifi.logging.ComponentLog;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by SonCD on 4/15/2020
 *
 */
public abstract class ChangeDataCapture {

    private Configuration configuration;
    protected ComponentLog log;
    private CDCEventListener<DataOutputType> eventListener;
    private DataOutputType dataOutputType;

    public ChangeDataCapture(ComponentLog log, DataOutputType dataOutputType) {
        this.log = log;
        this.dataOutputType = dataOutputType;
    }

    public void setEventListener(CDCEventListener<DataOutputType> eventListener) {
        this.eventListener = eventListener;
    }

    public void setConfig(Map<String, Object> configMap) {
        configuration = Configuration.empty();
        for (Map.Entry<String, Object> entry : configMap.entrySet()) {
            configuration = configuration.edit().with(entry.getKey(), entry.getValue()).build();
        }
    }

    public EmbeddedEngine getEngine(EmbeddedEngine.CompletionCallback completionCallback) {
        EmbeddedEngine.Builder builder = EmbeddedEngine.create()
                .using(OffsetCommitPolicy.always())
                .using(completionCallback)
                .using(configuration);
        if (builder == null) {
            log.error("SonCD: Cannot create EmbeddedEngine for CDC");
            throw new NifiCreationException("create embddedEngine error");
        } else {
            return builder.notifying(this::handleEvent).build();
        }
    }

    public void handleEvent(ConnectRecord connectRecord) {
        DataOutputType dataChange;
        dataChange = createMap(connectRecord);
//        if (!dataChange.isEmpty()) {
//            eventListener.onEvent(dataChange);
//        }
        eventListener.onEvent(dataChange);
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public DataOutputType getDataOutputType() {
        return dataOutputType;
    }

    abstract DataOutputType createMap(ConnectRecord connectRecord);


}
