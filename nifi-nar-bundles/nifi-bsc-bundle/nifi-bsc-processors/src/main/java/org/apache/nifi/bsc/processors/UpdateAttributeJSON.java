package org.apache.nifi.bsc.processors;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import org.apache.nifi.annotation.behavior.*;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.bsc.utils.BSCConstants;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.expression.ExpressionLanguageScope;
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
 * Created by SonCD on 4/27/20
 */
@EventDriven
@SideEffectFree
@SupportsBatching
@InputRequirement(InputRequirement.Requirement.INPUT_REQUIRED)
@Tags({"update", "record", "json"})
@CapabilityDescription("Update attribute to json(json_object, json_array)")
@WritesAttribute(attribute = "record.index", description = "This attribute provides the current row index and is only available inside the literal value expression.")

public class UpdateAttributeJSON extends AbstractProcessor {


    public static final Relationship REL_SUCCESS = new Relationship.Builder()
            .name("success")
            .description("Successfully update attribute.")
            .build();

    public static final PropertyDescriptor ATTRIBUTE_TYPE = new PropertyDescriptor.Builder()
            .name("attribute-type")
            .displayName("Attribute Type")
            .description("attribute type can be update (JSON_OBJECT, JSON_ARRAY)")
            .required(true)
            .allowableValues(BSCConstants.ATTRIBUTE_JSON_OBJECT, BSCConstants.ATTRIBUTE_JSON_ARRAY)
            .build();


    public static final PropertyDescriptor ATTRIBUTE_UPDATE = new PropertyDescriptor.Builder()
            .name("attribute-update")
            .displayName("Attribute Update")
            .description("Attribute from flow file can be update")
            .required(false)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES)
            .build();

    public static final PropertyDescriptor ATTRIBUTE_OUTPUT = new PropertyDescriptor.Builder()
            .name("attribute-output")
            .displayName("Attribute Input")
            .description("Attribute output after  update")
            .required(false)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES)
            .build();

    static final PropertyDescriptor IS_SPLIT = new PropertyDescriptor.Builder()
            .name("split-field-json")
            .displayName("Split Field")
            .description("Split field json")
            .allowableValues("true", "false")
            .defaultValue("true")
            .build();

    public static final PropertyDescriptor ATTRIBUTE_SPLIT = new PropertyDescriptor.Builder()
            .name("attribute-split")
            .displayName("Attribute Split")
            .description("Attribute from flow file can be split")
            .required(false)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES)
            .build();

    private final static List<PropertyDescriptor> propertyDescriptors;
    private final static Set<Relationship> relationships;
    private Gson gson = new Gson();

    static {
        List<PropertyDescriptor> _propertyDescriptors = new ArrayList<>();
        _propertyDescriptors.add(ATTRIBUTE_TYPE);
        _propertyDescriptors.add(ATTRIBUTE_UPDATE);
        _propertyDescriptors.add(ATTRIBUTE_OUTPUT);
        _propertyDescriptors.add(IS_SPLIT);
        _propertyDescriptors.add(ATTRIBUTE_SPLIT);
        propertyDescriptors = Collections.unmodifiableList(_propertyDescriptors);

        Set<Relationship> _relationships = new HashSet<>();
        _relationships.add(REL_SUCCESS);
        relationships = Collections.unmodifiableSet(_relationships);
    }

    @Override
    public Set<Relationship> getRelationships() {
        return relationships;
    }

    @Override
    protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return propertyDescriptors;
    }

    @Override
    public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {

        ComponentLog log = getLogger();
        FlowFile flowFileProcess = session.get();
        if (flowFileProcess == null) {
            return;
        }
        String attributeType = context.getProperty(ATTRIBUTE_TYPE).getValue();
        List<String> attributeUpdates = getAtrributes(context.getProperty(ATTRIBUTE_UPDATE).getValue());
        String attributeInput = context.getProperty(ATTRIBUTE_OUTPUT).getValue();
        boolean isSplit = context.getProperty(IS_SPLIT).asBoolean();
        List<String> fieldSplits = getAtrributes(context.getProperty(ATTRIBUTE_SPLIT).getValue());
        StringBuilder sb = new StringBuilder();

        try {
            String dataString = null;
            if (attributeInput == null || attributeInput.isEmpty()) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                session.exportTo(flowFileProcess, stream);
                dataString = stream.toString(StandardCharsets.UTF_8.name());
            } else {
                dataString = flowFileProcess.getAttribute(attributeInput);
            }

            if (attributeType.contains(BSCConstants.ATTRIBUTE_JSON_ARRAY)) {

                JsonArray jsonArrAttribute = gson.fromJson(dataString, JsonArray.class);
                if (attributeUpdates != null && attributeUpdates.size() > 0) {

                    FlowFile finalFlowFileProcess = flowFileProcess;
                    attributeUpdates.forEach(attributeUpdate -> {
                        String dataAttribute = finalFlowFileProcess.getAttribute(attributeUpdate);
                        for (int i = 0; i < jsonArrAttribute.size(); i++) {
                            jsonArrAttribute.get(i).getAsJsonObject().addProperty(attributeUpdate, dataAttribute);
                        }
                    });
                }
                if (isSplit) {
                    for (int i = 0; i < jsonArrAttribute.size(); i++) {
                        fieldSplits.forEach(jsonArrAttribute.get(i).getAsJsonObject()::remove);
                    }
                }
                sb.append(jsonArrAttribute.toString());

            } else {

                JsonObject jsonObjAttribute = gson.fromJson(dataString, JsonObject.class);

                if (attributeUpdates != null && attributeUpdates.size() > 0) {

                    FlowFile finalFlowFileProcess1 = flowFileProcess;
                    attributeUpdates.forEach(attributeUpdate -> {
                        String dataAttribute = finalFlowFileProcess1.getAttribute(attributeUpdate);
                        jsonObjAttribute.addProperty(attributeUpdate, dataAttribute);
                    });
                }
                if (isSplit) {
                    for (int i = 0; i < jsonObjAttribute.size(); i++) {
                        fieldSplits.forEach(jsonObjAttribute::remove);
                    }
                }
                sb.append(jsonObjAttribute.toString());
            }
        } catch (ProcessException | UnsupportedEncodingException | JsonIOException e) {
            log.error("SonCD: Transfer json attribute error: " + e);
        }

        flowFileProcess = session.write(flowFileProcess, outputStream -> {
            outputStream.write(sb.toString().getBytes(StandardCharsets.UTF_8));
        });

        session.transfer(flowFileProcess, REL_SUCCESS);
        session.commit();

    }


    protected List<String> getAtrributes(final String value) {
        if (value == null || value.length() == 0 || value.trim().length() == 0) {
            return new ArrayList<>();
        }
        final List<String> attributes = new LinkedList<>();
        for (String attribute : value.split(";")) {
            if (attribute.trim().length() > 0) {
                attributes.add(attribute.trim());
            }
        }
        return attributes;
    }
}
