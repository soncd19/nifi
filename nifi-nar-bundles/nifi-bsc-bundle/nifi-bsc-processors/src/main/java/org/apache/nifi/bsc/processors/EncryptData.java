package org.apache.nifi.bsc.processors;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import org.apache.nifi.annotation.behavior.EventDriven;
import org.apache.nifi.annotation.behavior.InputRequirement;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.bsc.utils.ShaUtils;
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
import java.security.NoSuchAlgorithmException;
import java.util.*;

@EventDriven
@InputRequirement(InputRequirement.Requirement.INPUT_REQUIRED)
@Tags({"encrypt"})
@CapabilityDescription("This processor encrypt field in FlowFiles using sha-256")
public class EncryptData extends AbstractProcessor {

    public static final Relationship REL_SUCCESS = new Relationship.Builder()
            .name("success")
            .description("Successfully in Encryption Data from source")
            .build();

    static final Relationship REL_FAILURE = new Relationship.Builder()
            .name("failure")
            .description("Failure in Encryption Data from source")
            .build();

    public static final PropertyDescriptor FIELD_ENCRYPT = new PropertyDescriptor.Builder()
            .name("field encryption")
            .displayName("Field Encrypt")
            .description("Fields need encrypt using sha-256")
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .required(true)
            .build();

    public static final PropertyDescriptor ENCRYPT_PACKAGE = new PropertyDescriptor.Builder()
            .name("Encrypt package")
            .displayName("Encrypt package")
            .description("Encrypt all data in package")
            .allowableValues("true", "false")
            .defaultValue("true")
            .required(false)
            .build();

    public static final PropertyDescriptor TYPE_ENCRYPT = new PropertyDescriptor.Builder()
            .name("Type encryption")
            .displayName("Type Encrypt")
            .description("Type of encrypt using")
            .required(false)
            .defaultValue("SHA256")
            .allowableValues("SHA256", "SHA512", "MD5")
            .build();

    private final static List<PropertyDescriptor> propertyDescriptors;
    private final static Set<Relationship> relationships;
    private final Gson gson = new Gson();

    static {
        List<PropertyDescriptor> _propertyDescriptors = new ArrayList<>();
        _propertyDescriptors.add(FIELD_ENCRYPT);
        _propertyDescriptors.add(ENCRYPT_PACKAGE);
        _propertyDescriptors.add(TYPE_ENCRYPT);
        propertyDescriptors = Collections.unmodifiableList(_propertyDescriptors);

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
    protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return propertyDescriptors;
    }

    @Override
    public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {

        ComponentLog logger = getLogger();

        FlowFile flowFile = session.get();
        if (flowFile == null) {
            return;
        }

        boolean isEncryptPackage = context.getProperty(ENCRYPT_PACKAGE).asBoolean();
        List<String> listField = getFieldData(context.getProperty(FIELD_ENCRYPT).getValue());

        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            session.exportTo(flowFile, stream);
            String dataString = stream.toString(StandardCharsets.UTF_8.name());
            JsonObject jsonData = gson.fromJson(dataString, JsonObject.class);
            JsonObject jsonDataNew = new JsonObject();
            if (isEncryptPackage) {

                jsonDataNew.addProperty("Data", dataString);
                try {
                    String dataSha = ShaUtils.toHexString(ShaUtils.getSHA(dataString));
                    jsonDataNew.addProperty("Data_SHA256", dataSha);

                } catch (NoSuchAlgorithmException e) {
                    logger.error("DuyNVT: Cannot encrypt data");
                }
            }

            if (listField.size() > 0) {

                listField.forEach(field -> {
                    String value = jsonData.get(field).getAsString();
                    try {
                        jsonDataNew.addProperty(field, value);
                        String valueEncrypt = ShaUtils.toHexString(ShaUtils.getSHA(value));
                        jsonDataNew.addProperty(field + "_SHA256", valueEncrypt);

                    } catch (NoSuchAlgorithmException e) {
                        logger.error("DuyNVT: Cannot encrypt data (field)");
                    }
                });

            }

            flowFile = session.write(flowFile, outputStream -> {
                outputStream.write(jsonDataNew.toString().getBytes(StandardCharsets.UTF_8));
            });

            session.transfer(flowFile, REL_SUCCESS);
            session.commit();

        } catch (UnsupportedEncodingException | JsonIOException e) {
            logger.error("DuyNVT: Error unsupported Encoding");
            session.transfer(flowFile, REL_FAILURE);
        }
    }

    public List<String> getFieldData(final String input) {
        if (input == null || input.length() == 0 || input.trim().length() == 0) {
            return null;
        }

        List<String> fields = new ArrayList<>();

        for (String field : input.split(";")) {
            if (field.trim().length() > 0) {
                fields.add(field);
            }
        }
        return fields;
    }
}
