package org.apache.nifi.bsc.processors;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.nifi.annotation.behavior.EventDriven;
import org.apache.nifi.annotation.behavior.InputRequirement;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by SonCD on 01/06/2020
 */

@EventDriven
@InputRequirement(InputRequirement.Requirement.INPUT_ALLOWED)
@Tags({"avro", "sql", "convert"})
@CapabilityDescription("Execute provided convert avro schema to sql schema")
public class AvroConvertToSQL extends AbstractProcessor {

    static final Relationship REL_SUCCESS = new Relationship.Builder()
            .name("success")
            .description("A FlowFile is routed to this relationship after it has been create table in hive")
            .build();
    static final Relationship REL_FAILURE = new Relationship.Builder()
            .name("failure")
            .description("A FlowFile is routed to this relationship if it cannot be create hive table")
            .build();

    private final static Set<Relationship> relationships;

    static {
        Set<Relationship> _relationships = new HashSet<>();
        _relationships.add(REL_SUCCESS);
        _relationships.add(REL_FAILURE);
        relationships = Collections.unmodifiableSet(_relationships);
    }

    @Override
    public Set<Relationship> getRelationships() {
        return relationships;
    }

    @Override
    public void onTrigger(ProcessContext processContext, ProcessSession session) throws ProcessException {
        FlowFile flowFile = session.get();
        if (flowFile == null) {
            return;
        }
        final ComponentLog logger = getLogger();

        try {

            final AtomicReference<Schema> schema = new AtomicReference<>(null);
            try(final InputStream rawIn = session.read(flowFile)) {
                final InputStream in = new BufferedInputStream(rawIn);
                final DataFileStream<GenericRecord> reader = new DataFileStream<>(in, new GenericDatumReader<>());
                Schema avroSchema = reader.getSchema();
                schema.set(avroSchema);
            }catch (IOException e) {
                logger.error("SonCD: AvroConvertToSQL can not get schema");
            }

        }catch (ProcessException ex) {
            logger.error("SonCD: cannot convert avro schema to sql schema " + ex.getMessage());
            session.transfer(flowFile, REL_FAILURE);
        }

    }
}
