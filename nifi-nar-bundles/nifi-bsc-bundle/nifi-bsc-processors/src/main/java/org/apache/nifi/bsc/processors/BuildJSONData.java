package org.apache.nifi.bsc.processors;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.nifi.annotation.behavior.EventDriven;
import org.apache.nifi.annotation.behavior.InputRequirement;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.bsc.utils.BSCConstants;
import org.apache.nifi.bsc.utils.JSONValidUtil;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by SonCD on 10/06/2020
 */

@EventDriven
@InputRequirement(InputRequirement.Requirement.INPUT_ALLOWED)
@Tags({"json", "convert"})
@CapabilityDescription("Build json struct")
public class BuildJSONData extends AbstractProcessor {

    public static final Relationship REL_SUCCESS = new Relationship.Builder()
            .name("success")
            .description("Successfully building json schema")
            .build();

    static final Relationship REL_FAILURE = new Relationship.Builder()
            .name("failure")
            .description("A FlowFile is routed to this relationship if it cannot be building json schema")
            .build();

    public static final PropertyDescriptor KEY_NAME = new PropertyDescriptor.Builder()
            .name("key-name-json")
            .displayName("Key Name Json")
            .description("Key Name Json can be build JSON data")
            .required(false)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();

    private static final List<PropertyDescriptor> propDescriptors;
    protected static Set<Relationship> relationships;
    private final Gson gson = new Gson();

    static {
        final Set<Relationship> r = new HashSet<>();
        r.add(REL_SUCCESS);
        r.add(REL_FAILURE);
        relationships = Collections.unmodifiableSet(r);

        final List<PropertyDescriptor> pds = new ArrayList<>();
        pds.add(KEY_NAME);
        propDescriptors = Collections.unmodifiableList(pds);
    }

    @Override
    public Set<Relationship> getRelationships() {
        return relationships;
    }

    @Override
    protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return propDescriptors;
    }

    @Override
    public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
        FlowFile flowFile = session.get();
        if (flowFile == null) {
            return;
        }
        final ComponentLog log = getLogger();
        try {
            List<String> keyNames = getKeyName(context.getProperty(KEY_NAME).getValue());
            if (keyNames != null && keyNames.size() > 0) {
                JsonObject jsonData = new JsonObject();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                session.exportTo(flowFile, stream);
                String dataString = stream.toString(StandardCharsets.UTF_8.name());

                if (JSONValidUtil.isJSONObjectValid(dataString)) {
                    jsonData.add(BSCConstants.DATA, gson.fromJson(dataString, JsonObject.class));
                } else if (JSONValidUtil.isJSONArrayValid(dataString)) {
                    jsonData.add(BSCConstants.DATA, gson.fromJson(dataString, JsonArray.class));
                }
                FlowFile finalFlowFile = flowFile;
                keyNames.forEach(keyName -> {
                    jsonData.addProperty(keyName, finalFlowFile.getAttribute(keyName));
                });
                flowFile = session.write(flowFile, outputStream -> {
                    outputStream.write(jsonData.toString().getBytes(StandardCharsets.UTF_8));
                });
            }
            session.transfer(flowFile, REL_SUCCESS);
            session.commit();
        } catch (ProcessException | UnsupportedEncodingException ex) {
            log.error("SonCD: Bulding new json schema error: " + ex);
            session.transfer(flowFile, REL_FAILURE);
        }
    }


    public static List<String> getKeyName(final String value) {
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
