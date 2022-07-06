package org.apache.nifi.bsc.processors;

import org.apache.nifi.annotation.behavior.*;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.expression.ExpressionLanguageScope;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.*;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * Created by SonCD on 08/06/2020
 */

@EventDriven
@InputRequirement(InputRequirement.Requirement.INPUT_ALLOWED)
@Tags({"generate"})
@CapabilityDescription("This processor creates FlowFiles with iterator date. GenerateFlowFile is useful")
@WritesAttributes({
        @WritesAttribute(attribute = "mime.type", description = "The processor outputs flow file content in JSON format, and sets the mime.type attribute to "
                + "application/json")
})
public class GenerateListTableName extends AbstractProcessor {

    public static final Relationship REL_SUCCESS = new Relationship.Builder()
            .name("success")
            .description("Successfully created FlowFile from SQL query result set.")
            .build();

    public static final PropertyDescriptor LIST_TABLE_NAME = new PropertyDescriptor.Builder()
            .name("list-table-name")
            .displayName("List Table Name")
            .description("List Table Name on Database")
            .required(false)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES)
            .build();

    private final static List<PropertyDescriptor> propertyDescriptors;
    private final static Set<Relationship> relationships;

    static {
        List<PropertyDescriptor> _propertyDescriptors = new ArrayList<>();
        _propertyDescriptors.add(LIST_TABLE_NAME);
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
        FlowFile flowFile;
        List<String> tbl_names = getTableNames(context.getProperty(LIST_TABLE_NAME).getValue());
        for (int i = 0; i < tbl_names.size(); i++) {
            flowFile = session.create();
            String tbl_name = tbl_names.get(i);
            session.putAttribute(flowFile, "tbl_name", tbl_name);
            session.transfer(flowFile, REL_SUCCESS);
        }
    }

    protected List<String> getTableNames(final String value) {
        if (value == null || value.length() == 0 || value.trim().length() == 0) {
            return null;
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
