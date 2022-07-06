package org.apache.nifi.bsc.processors;

import org.apache.nifi.annotation.behavior.*;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.bsc.utils.RangeTimeUtil;
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
 * Change by DuyNVT on 24/06/2020
 */

@EventDriven
@InputRequirement(InputRequirement.Requirement.INPUT_ALLOWED)
@Tags({"generate"})
@CapabilityDescription("This processor creates FlowFiles with iterator date. GenerateFlowFile is useful")
@WritesAttributes({
        @WritesAttribute(attribute = "mime.type", description = "The processor outputs flow file content in JSON format, and sets the mime.type attribute to "
                + "application/json")
})
public class GenerateTimeKey extends AbstractProcessor {

    public static final Relationship REL_SUCCESS = new Relationship.Builder()
            .name("success")
            .description("Successfully created FlowFile from SQL query result set.")
            .build();

    public static final PropertyDescriptor TYPE_RANGE = new PropertyDescriptor.Builder()
            .name("type-range")
            .displayName("Type Range")
            .description("Type of Range")
            .required(false)
            .allowableValues("Day", "Month", "Year")
            .defaultValue("Month")
            .build();

    public static final PropertyDescriptor VALUE_RANGE = new PropertyDescriptor.Builder()
            .name("value-range")
            .displayName("Value Range")
            .description("Value of range")
            .required(false)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES)
            .build();

    private final static List<PropertyDescriptor> propertyDescriptors;
    private final static Set<Relationship> relationships;

    static {
        List<PropertyDescriptor> _propertyDescriptors = new ArrayList<>();
        _propertyDescriptors.add(TYPE_RANGE);
        _propertyDescriptors.add(VALUE_RANGE);
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

        String rangeType = context.getProperty(TYPE_RANGE).getValue();

        int valueRange = context.getProperty(VALUE_RANGE).asInteger();

        FlowFile flowFile;
        Calendar calendar;

        for(int i = 0; i < valueRange; i++){

            flowFile = session.create();
            calendar = Calendar.getInstance();

            switch (rangeType){
                case "Year": {
                    calendar.add(Calendar.YEAR, -i);
                }
                case "Month": {
                    calendar.add(Calendar.MONTH, -i);
                }
                case "Day": {
                    calendar.add(Calendar.DAY_OF_YEAR, -i);
                }
            }

            Map<String, String> sessionAttrs = RangeTimeUtil.getProcessSessionAttrs(rangeType, calendar);
            session.putAllAttributes(flowFile, sessionAttrs);
            session.transfer(flowFile, REL_SUCCESS);
        }
    }
}
