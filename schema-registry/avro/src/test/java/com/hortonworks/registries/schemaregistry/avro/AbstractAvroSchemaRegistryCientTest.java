/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hortonworks.registries.schemaregistry.avro;

import com.hortonworks.registries.schemaregistry.client.SchemaRegistryClient;
import com.hortonworks.registries.schemaregistry.webservice.SchemaRegistryApplication;
import com.hortonworks.registries.schemaregistry.webservice.SchemaRegistryConfiguration;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.TestName;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Random;

/**
 *
 */
public class AbstractAvroSchemaRegistryCientTest {

    @ClassRule
    public static final DropwizardAppRule<SchemaRegistryConfiguration> DROPWIZARD_APP_RULE
            = new DropwizardAppRule<>(SchemaRegistryApplication.class, ResourceHelpers.resourceFilePath("schema-registry-test.yaml"));

    protected final String rootUrl = String.format("http://localhost:%d/api/v1", DROPWIZARD_APP_RULE.getLocalPort());
    protected final Map<String, String> SCHEMA_REGISTRY_CLIENT_CONF = Collections.singletonMap(SchemaRegistryClient.Options.SCHEMA_REGISTRY_URL, rootUrl);
    ;

    @Rule
    public TestName TEST_NAME_RULE = new TestName();

    protected SchemaRegistryClient schemaRegistryClient;


    @Before
    public void setup() throws IOException {
        schemaRegistryClient = new SchemaRegistryClient(SCHEMA_REGISTRY_CLIENT_CONF);
    }

    protected Object[] generatePrimitivePayloads() {
        Random random = new Random();
        byte[] bytes = new byte[4];
        random.nextBytes(bytes);
        return new Object[]{random.nextBoolean(), random.nextDouble(), random.nextLong(), random.nextInt(), "Stringpayload:" + new Date()};
    }

    protected Object createGenericAvroRecord(String schemaText) {
        Schema schema = new Schema.Parser().parse(schemaText);

        GenericRecord avroRecord = new GenericData.Record(schema);
        avroRecord.put("xid", System.currentTimeMillis());
        avroRecord.put("name", "foo-" + System.currentTimeMillis());
        avroRecord.put("version", new Random().nextInt());
        avroRecord.put("timestamp", System.currentTimeMillis());

        return avroRecord;
    }

    protected String getSchema(String schemaFileName) throws IOException {
        InputStream avroSchemaStream = AvroSchemaRegistryClientTest.class.getResourceAsStream(schemaFileName);
        org.apache.avro.Schema.Parser parser = new org.apache.avro.Schema.Parser();
        return parser.parse(avroSchemaStream).toString();
    }

}
