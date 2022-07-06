/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.bsc.processors;

import org.apache.nifi.annotation.behavior.*;
import org.apache.nifi.annotation.behavior.InputRequirement.Requirement;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.components.*;
import org.apache.nifi.components.state.Scope;
import org.apache.nifi.expression.AttributeExpression;
import org.apache.nifi.expression.ExpressionLanguageScope;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.util.StandardValidators;

import java.util.*;

@EventDriven
@SideEffectFree
@SupportsBatching
@InputRequirement(Requirement.INPUT_REQUIRED)
@Tags({"attributes", "modification", "update", "delete", "Attribute Expression Language", "state"})
@CapabilityDescription("Updates the Attributes for a FlowFile by using the Attribute Expression Language and/or deletes the attributes based on a regular expression")
@DynamicProperty(name = "A FlowFile attribute to update", value = "The value to set it to", expressionLanguageScope = ExpressionLanguageScope.FLOWFILE_ATTRIBUTES,
        description = "Updates a FlowFile attribute specified by the Dynamic Property's key with the value specified by the Dynamic Property's value")
@WritesAttribute(attribute = "See additional details", description = "This processor may write or remove zero or more attributes as described in additional details")
@Stateful(scopes = {Scope.LOCAL}, description = "Gives the option to store values not only on the FlowFile but as stateful variables to be referenced in a recursive manner.")
public class UpdateUtilityAttributes extends AbstractProcessor {

    private static final String DATEKEY_FORMAT_TYPE_1 = "${now():toNumber():format('yyyyMMdd')}";
    private static final String DATEKEY_FORMAT_TYPE_2 = "${now():toNumber():minus(${i:multiply(86400000)}):format('yyyyMMdd')}";

    // relationships
    public static final Relationship REL_SUCCESS = new Relationship.Builder()
            .description("All successful FlowFiles are routed to this relationship").name("success").build();

    public static final PropertyDescriptor URL = new PropertyDescriptor.Builder()
            .name("url")
            .displayName("url")
            .description("Link URL của API cần lấy dữ liệu")
            .required(false)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES)
            .build();

    public static final PropertyDescriptor DELETE_SQL = new PropertyDescriptor.Builder()
            .name("delete_sql")
            .displayName("delete_sql")
            .description("Khai báo câu lệnh xóa trong SQL")
            .required(false)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES)
            .build();

    public static final PropertyDescriptor DATEKEY = new PropertyDescriptor.Builder()
            .name("datekey")
            .displayName("datekey")
            .description("datekey")
            .required(false)
            .defaultValue(DATEKEY_FORMAT_TYPE_2)
            .addValidator(StandardValidators.createAttributeExpressionLanguageValidator(AttributeExpression.ResultType.STRING, true))
            .addValidator(StandardValidators.ATTRIBUTE_KEY_PROPERTY_NAME_VALIDATOR)
            .expressionLanguageSupported(ExpressionLanguageScope.VARIABLE_REGISTRY)
            .build();

    public static final PropertyDescriptor TABLE_NAME = new PropertyDescriptor.Builder()
            .name("table_name")
            .displayName("table_name")
            .description("Tên bảng cần đưa dữ liệu vào")
            .required(false)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();


    private final static List<PropertyDescriptor> propertyDescriptors;
    private final static Set<Relationship> relationships;

    static {
        List<PropertyDescriptor> descriptors = new ArrayList<>();
        descriptors.add(URL);
        descriptors.add(DELETE_SQL);
        descriptors.add(DATEKEY);
        descriptors.add(TABLE_NAME);

        propertyDescriptors = Collections.unmodifiableList(descriptors);

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
    protected PropertyDescriptor getSupportedDynamicPropertyDescriptor(final String propertyDescriptorName) {
        return new PropertyDescriptor.Builder()
                .name(propertyDescriptorName)
                .required(false)
                .addValidator(StandardValidators.createAttributeExpressionLanguageValidator(AttributeExpression.ResultType.STRING, true))
                .addValidator(StandardValidators.ATTRIBUTE_KEY_PROPERTY_NAME_VALIDATOR)
                .expressionLanguageSupported(ExpressionLanguageScope.VARIABLE_REGISTRY)
                .dynamic(true)
                .build();
    }

    @Override
    public void onTrigger(final ProcessContext context, final ProcessSession session) {
        final ComponentLog logger = getLogger();
        Map<String, String> dynamicAttributes = new HashMap<>();

        FlowFile incomingFlowFile = session.get();
        if (incomingFlowFile == null) {
            return;
        }

        String url        = context.getProperty(URL).getValue();
        String delete_sql = context.getProperty(DELETE_SQL).getValue();
        String datekey    = context.getProperty(DATEKEY).evaluateAttributeExpressions().getValue();
        String table_name = context.getProperty(TABLE_NAME).getValue();

        if (url != null) session.putAttribute(incomingFlowFile, "url", url);
        if (delete_sql != null) session.putAttribute(incomingFlowFile, "delete_sql", delete_sql);
        if (datekey != null) session.putAttribute(incomingFlowFile, "datekey", datekey);
        if (table_name != null) session.putAttribute(incomingFlowFile, "table_name", table_name);

        // dynamic properties
        Map<PropertyDescriptor, String> processorProperties = context.getProperties();

        for (final Map.Entry<PropertyDescriptor, String> entry : processorProperties.entrySet()) {
            PropertyDescriptor property = entry.getKey();
            if (property.isDynamic() && property.isExpressionLanguageSupported()) {
                String dynamicValue = context.getProperty(property).evaluateAttributeExpressions().getValue();
                dynamicAttributes.put(property.getName(), dynamicValue);
            }
        }
        session.putAllAttributes(incomingFlowFile, dynamicAttributes);
        session.transfer(incomingFlowFile, REL_SUCCESS);
    }
}
