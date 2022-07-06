package org.apache.nifi.bsc.processors;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.nifi.annotation.behavior.EventDriven;
import org.apache.nifi.annotation.behavior.InputRequirement;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.bsc.utils.BSCConstants;
import org.apache.nifi.bsc.utils.JSONValidUtil;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.dbcp.hive.Hive3DBCPService;
import org.apache.nifi.expression.ExpressionLanguageScope;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;
import org.apache.nifi.schema.access.InferenceSchemaStrategy;
import org.apache.nifi.schema.access.SchemaNotFoundException;
import org.apache.nifi.serialization.record.RecordField;
import org.apache.nifi.serialization.record.RecordSchema;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by SonCD on 08/05/2020
 */

@EventDriven
@InputRequirement(InputRequirement.Requirement.INPUT_ALLOWED)
@Tags({"json", "sql", "hive", "convert"})
@CapabilityDescription("Execute provided convert json data to table hive struct")
public class JsonSchemaToHiveSchema extends AbstractProcessor {

    public static final Relationship REL_SUCCESS = new Relationship.Builder()
            .name("success")
            .description("Successfully convert json schema to hive table.")
            .build();

    static final Relationship REL_FAILURE = new Relationship.Builder()
            .name("failure")
            .description("A FlowFile is routed to this relationship if it cannot be create hive table")
            .build();

    public static final PropertyDescriptor HIVE_DBCP_SERVICE = new PropertyDescriptor.Builder()
            .name("Hive Database Connection Pooling Service")
            .description("The Hive Controller Service that is used to obtain connection(s) to the Hive database")
            .required(true)
            .identifiesControllerService(Hive3DBCPService.class)
            .build();

    public static final PropertyDescriptor TABLE_NAME_HIVE = new PropertyDescriptor.Builder()
            .name("table-name-hive")
            .displayName("Table Name Hive")
            .description("The table name will be used to create")
            .required(false)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES)
            .build();

    public static final PropertyDescriptor JSON_SCHEMA = new PropertyDescriptor.Builder()
            .name("json-schema")
            .displayName("Json Schema")
            .description("Attribute json schema")
            .required(false)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES)
            .build();

    private final static List<PropertyDescriptor> propertyDescriptors;
    private final static Set<Relationship> relationships;
    private InferenceSchemaStrategy strategy;
    private Hive3DBCPService hiveDBCPService;
    private Map<String, String> mapTblNames = new HashMap<>();
    private final Gson gson = new Gson();

    static {
        List<PropertyDescriptor> _propertyDescriptors = new ArrayList<>();
        _propertyDescriptors.add(HIVE_DBCP_SERVICE);
        _propertyDescriptors.add(TABLE_NAME_HIVE);
        _propertyDescriptors.add(JSON_SCHEMA);
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

    @OnScheduled
    public void onScheduled(ProcessContext processContext) {
        strategy = new InferenceSchemaStrategy();
        hiveDBCPService = processContext.getProperty(HIVE_DBCP_SERVICE).asControllerService(Hive3DBCPService.class);
    }

    @Override
    public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
        FlowFile flowFile = session.get();
        if (flowFile == null) {
            return;
        }
        final ComponentLog log = getLogger();

        final Map<String, String> new_attributes = new HashMap<>();

        String tableName = context.getProperty(TABLE_NAME_HIVE).evaluateAttributeExpressions(flowFile).getValue();
        String jsonSchema = context.getProperty(JSON_SCHEMA).evaluateAttributeExpressions(flowFile).getValue();
        List<RecordField> recordFields = null;

        try {
            if (JSONValidUtil.isJSONObjectValid(jsonSchema)) {
                recordFields = getRecordField(jsonSchema);

            } else if (JSONValidUtil.isJSONArrayValid(jsonSchema)) {
                JsonArray jsonArraySchema = gson.fromJson(jsonSchema, JsonArray.class);
                JsonObject jsonObjectSchema = jsonArraySchema.get(0).getAsJsonObject();
                recordFields = getRecordField(jsonObjectSchema.toString());
            }

            if (!mapTblNames.containsKey(tableName)) {
                if (recordFields != null && recordFields.size() > 0) {
                    List<String> hiveColumns = new ArrayList<>();
                    StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
                    sb.append(tableName).append("(");

                    hiveColumns.addAll(recordFields.stream().map(field -> field.getFieldName() + " " + field.getDataType()).collect(Collectors.toList()));
                    sb.append(StringUtils.join(hiveColumns, ","));
                    sb.append(") STORED AS ORC");
                    String hiveTableSchema = sb.toString();

                    new_attributes.put(BSCConstants.HIVE_TABLE_SCHEMA, hiveTableSchema);

                    try (final Connection conn = hiveDBCPService.getConnection()) {
                        PreparedStatement statement = conn.prepareStatement(hiveTableSchema);
                        statement.execute();
                        flowFile = session.putAllAttributes(flowFile, new_attributes);
                    } catch (Exception e) {
                        getLogger().error("SonCD: Cannot creat table hive: " + e);
                    }
                }
            }

            session.transfer(flowFile, REL_SUCCESS);
            session.commit();

        } catch (ProcessException e) {
            log.error("SonCD: JsonConvertToHive Error: " + e);
            session.transfer(flowFile, REL_FAILURE);
            session.commit();
        }
    }

    private List<RecordField> getRecordField(String jsonSchema) {
        byte[] dataByte = jsonSchema.getBytes();
        try (final InputStream inputStream = new ByteArrayInputStream(dataByte)) {
            RecordSchema schema = strategy.getSchema(new HashMap<>(), inputStream, null);
            return schema.getFields();

        } catch (IOException | SchemaNotFoundException e) {
            getLogger().error("SonCD: String to Byte error: " + e);
            return new ArrayList<>();
        }
    }

}
