package org.apache.nifi.bsc.processors;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import org.apache.nifi.annotation.behavior.EventDriven;
import org.apache.nifi.annotation.behavior.InputRequirement;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.bsc.utils.BSCConstants;
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
 * Created by SonCD on 13/07/2020
 */
@EventDriven
@InputRequirement(InputRequirement.Requirement.INPUT_ALLOWED)
@Tags({"json", "convert"})
@CapabilityDescription("Build json struct")
public class RebuildJSONData extends AbstractProcessor {

    public static final Relationship REL_SUCCESS = new Relationship.Builder()
            .name("success")
            .description("Successfully building json schema")
            .build();

    static final Relationship REL_FAILURE = new Relationship.Builder()
            .name("failure")
            .description("A FlowFile is routed to this relationship if it cannot be building json schema")
            .build();

    public static final PropertyDescriptor ATTRIBUTE_TYPE = new PropertyDescriptor.Builder()
            .name("attribute-type")
            .displayName("Attribute Type")
            .description("attribute type can be update (JSON_OBJECT, JSON_ARRAY)")
            .required(true)
            .allowableValues(BSCConstants.ATTRIBUTE_JSON_OBJECT, BSCConstants.ATTRIBUTE_JSON_ARRAY)
            .build();

    public static final PropertyDescriptor REBUILD_KEY_NAME = new PropertyDescriptor.Builder()
            .name("rebuild-key-name-json")
            .displayName("Rebuild Key Name Json")
            .description("Rebuild Key Name Json can be build JSON data")
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
        pds.add(ATTRIBUTE_TYPE);
        pds.add(REBUILD_KEY_NAME);
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
            String attributeType = context.getProperty(ATTRIBUTE_TYPE).getValue();
            List<String> rebuildKeyNames = getKeyName(context.getProperty(REBUILD_KEY_NAME).getValue());
            StringBuilder sb = new StringBuilder();

            if (rebuildKeyNames != null && rebuildKeyNames.size() > 0) {
                JsonArray jsonArrNew = new JsonArray();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                session.exportTo(flowFile, stream);
                String dataString = stream.toString(StandardCharsets.UTF_8.name());

                if (attributeType.contains(BSCConstants.ATTRIBUTE_JSON_ARRAY)) {
                    JsonArray jsonArr = gson.fromJson(dataString, JsonArray.class);
                    for (int i = 0; i < jsonArr.size(); i++) {
                        JsonObject jsonElement = jsonArr.get(i).getAsJsonObject();

                        rebuildKeyNames.forEach(rebuildKeyName -> {
                            Iterator<String> keys = jsonElement.get(rebuildKeyName).getAsJsonObject().keySet().iterator();
                            while (keys.hasNext()) {
                                String key = keys.next();
                                String new_key = rebuildKeyName + "_" + key;
                                jsonElement.add(new_key, jsonElement.get(rebuildKeyName).getAsJsonObject().get(key));
                            }

                            jsonElement.remove(rebuildKeyName);
                        });
                        jsonArrNew.add(jsonElement);
                    }
                    sb.append(jsonArrNew);

                } else {
                    JsonObject jsonObject = gson.fromJson(dataString, JsonObject.class);
                    rebuildKeyNames.forEach(rebuildKeyName -> {
                        Iterator<String> keys = jsonObject.get(rebuildKeyName).getAsJsonObject().keySet().iterator();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            String new_key = rebuildKeyName + "_" + key;
                            jsonObject.add(new_key, jsonObject.get(rebuildKeyName).getAsJsonObject().get(key));
                        }

                        jsonObject.remove(rebuildKeyName);
                    });

                    sb.append(jsonObject.toString());
                }
            }

            flowFile = session.write(flowFile, outputStream -> {
                outputStream.write(sb.toString().getBytes(StandardCharsets.UTF_8));
            });

            session.transfer(flowFile, REL_SUCCESS);
        } catch (ProcessException | UnsupportedEncodingException | JsonIOException ex) {
            log.error("SonCD: Building new json schema error: " + ex);
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
