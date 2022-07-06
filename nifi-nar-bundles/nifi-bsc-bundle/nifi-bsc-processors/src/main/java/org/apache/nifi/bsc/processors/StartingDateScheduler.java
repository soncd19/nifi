package org.apache.nifi.bsc.processors;

import org.apache.nifi.annotation.behavior.*;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.bsc.utils.FormattedDateMatcher;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.expression.AttributeExpression;
import org.apache.nifi.expression.ExpressionLanguageScope;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.*;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.io.OutputStreamCallback;
import org.apache.nifi.processor.util.StandardValidators;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.*;

/*
 * @author DuyNVT
 * @since 12/09/2020
 *
 */
@EventDriven
@InputRequirement(InputRequirement.Requirement.INPUT_FORBIDDEN)
@Tags({"Scheduler", "Starting"})
@CapabilityDescription("Executed in for loop, counter variable between start and end param")

public class StartingDateScheduler extends AbstractProcessor {

    public static final Relationship REL_CONTINUE = new Relationship.Builder()
            .name("continue")
            .description("Continue with the condition is met")
            .build();

    public static final PropertyDescriptor VARIABLE_START = new PropertyDescriptor.Builder()
            .name("Starting")
            .description("Variable starting for loop")
            .required(true)
            .defaultValue("0")
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();

    public static final PropertyDescriptor VARIABLE_FINISH = new PropertyDescriptor.Builder()
            .name("Finishing")
            .description("Variable finishing for loop")
            .required(true)
            .defaultValue("1")
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();

    public static final PropertyDescriptor INCREMENTS_NUMBER = new PropertyDescriptor.Builder()
            .name("Increments number")
            .description("Increments number in for loop")
            .required(false)
            .defaultValue("1")
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();

    public static final PropertyDescriptor CUSTOM_TEXT = new PropertyDescriptor.Builder()
            .displayName("Custom Text")
            .name("custom-text")
            .description("Custom text(JSON/XML/CSV.....)")
            .required(false)
            .expressionLanguageSupported(ExpressionLanguageScope.VARIABLE_REGISTRY)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();

    private final static List<PropertyDescriptor> propertyDescriptors;
    private final static Set<Relationship> relationships;

    static {
        List<PropertyDescriptor> _propertyDescriptors = new ArrayList<>();
        _propertyDescriptors.add(VARIABLE_START);
        _propertyDescriptors.add(VARIABLE_FINISH);
        _propertyDescriptors.add(INCREMENTS_NUMBER);
        _propertyDescriptors.add(CUSTOM_TEXT);
        propertyDescriptors = Collections.unmodifiableList(_propertyDescriptors);

        Set<Relationship> _relationships = new HashSet<>();
        _relationships.add(REL_CONTINUE);
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
    protected PropertyDescriptor getSupportedDynamicPropertyDescriptor(String propertyDescriptorName) {
        return new PropertyDescriptor.Builder()
                .name(propertyDescriptorName)
                .required(false)
                .addValidator(StandardValidators.createAttributeExpressionLanguageValidator(AttributeExpression.ResultType.STRING, true))
                .addValidator(StandardValidators.ATTRIBUTE_KEY_PROPERTY_NAME_VALIDATOR)
                .expressionLanguageSupported(ExpressionLanguageScope.VARIABLE_REGISTRY)
                .dynamic(true)
                .build();
    }

    public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {

        final ComponentLog logger = getLogger();
        Map<String, String> generatedAttributes = new HashMap<String, String>();
        String customText = context.getProperty(CUSTOM_TEXT).evaluateAttributeExpressions().getValue();

        List<FlowFile> flowFileToTransfer = new LinkedList<>();
        String startDate = context.getProperty(VARIABLE_START).getValue();
        String finishDate = context.getProperty(VARIABLE_FINISH).getValue();
        int incrementsNumber = context.getProperty(INCREMENTS_NUMBER).asInteger();
        long starting = generateStartDate(startDate);
        long finishing = generateFinishDate(finishDate);

        logger.debug("starting with... " + starting);

        Map<PropertyDescriptor, String> processorProperties = context.getProperties();

        for (final Map.Entry<PropertyDescriptor, String> entry : processorProperties.entrySet()) {
            PropertyDescriptor property = entry.getKey();
            if (property.isDynamic() && property.isExpressionLanguageSupported()) {
                String dynamicValue = context.getProperty(property).evaluateAttributeExpressions().getValue();
                generatedAttributes.put(property.getName(), dynamicValue);
            }
        }

        for (long i = starting; i < finishing; i += incrementsNumber) {
            FlowFile flowFile = session.create();

             if (customText != null && !customText.isEmpty()) {
                flowFile = session.write(flowFile, new OutputStreamCallback() {
                    @Override
                    public void process(final OutputStream out) throws IOException {
                        out.write(customText.getBytes());
                    }
                });
            }

            flowFile = session.putAllAttributes(flowFile, generatedAttributes);
            flowFile = session.putAttribute(flowFile, "i", String.valueOf(i));
            flowFileToTransfer.add(flowFile);
            logger.debug("Duynvt -- check increments number in StartingDateScheduler " + i);
        }
        session.transfer(flowFileToTransfer, REL_CONTINUE);
        logger.debug("finishing with... " + finishing);
    }

    private long generateStartDate(String startDate) {
        if (FormattedDateMatcher.matchesNumber(startDate)) {
            return Long.parseLong(startDate);

        } else if (FormattedDateMatcher.matchesDate(startDate)) {
            return FormattedDateMatcher.numDate(startDate);
        }
        return 0;
    }

    private long generateFinishDate(String endDate) {

        if (FormattedDateMatcher.matchesNumber(endDate)) {
            return Long.parseLong(endDate);

        } else if (FormattedDateMatcher.matchesDate(endDate)) {
            return FormattedDateMatcher.numDate(endDate) + 1;
        }
        return 1;
    }

}
