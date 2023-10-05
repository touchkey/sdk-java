/*
 * Copyright 2018-Present The CloudEvents Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package io.cloudevents.core.v1;

import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.builder.CloudEventBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.URI;

import static io.cloudevents.core.test.Data.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;


/**
 * @author fabiojose
 */
public class CloudEventBuilderTest {

    @ParameterizedTest()
    @MethodSource("io.cloudevents.core.test.Data#v1Events")
    void testCopyWithBuilder(CloudEvent event) {
        assertThat(CloudEventBuilder.v1(event).build()).isEqualTo(event);
    }

    @Test
    void testToV03() {
        CloudEvent input = CloudEventBuilder.v1()
            .withId(ID)
            .withType(TYPE)
            .withSource(SOURCE)
            .withData(DATACONTENTTYPE_JSON, DATASCHEMA, DATA_JSON_SERIALIZED)
            .withSubject(SUBJECT)
            .withTime(TIME)
            .withExtension("astring", "aaa")
            .withExtension("aboolean", "true")
            .withExtension("anumber", "10")
            .build();

        CloudEvent expected = CloudEventBuilder.v03()
            .withId(ID)
            .withType(TYPE)
            .withSource(SOURCE)
            .withData(DATACONTENTTYPE_JSON, DATASCHEMA, DATA_JSON_SERIALIZED)
            .withSubject(SUBJECT)
            .withTime(TIME)
            .withExtension("astring", "aaa")
            .withExtension("aboolean", "true")
            .withExtension("anumber", "10")
            .build();

        CloudEvent actual = CloudEventBuilder.v03(input).build();

        assertThat(expected.getSpecVersion())
            .isEqualTo(SpecVersion.V03);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testCopyExtensions() {
        io.cloudevents.core.v1.CloudEventBuilder templateBuilder = CloudEventBuilder.v1()
            .withId(ID)
            .withType(TYPE)
            .withSource(SOURCE)
            .withData(DATACONTENTTYPE_JSON, DATASCHEMA, DATA_JSON_SERIALIZED)
            .withSubject(SUBJECT)
            .withTime(TIME)
            .withExtension("astring", "aaa")
            .withExtension("aboolean", "true")
            .withExtension("anumber", "10");

        CloudEvent event = templateBuilder.build();
        CloudEvent cloned = new io.cloudevents.core.v1.CloudEventBuilder(event).build();

        assertThat(cloned).isEqualTo(event);
    }

    @Test
    void testNewBuilder() {
        io.cloudevents.core.v1.CloudEventBuilder templateBuilder = CloudEventBuilder.v1()
            .withId(ID)
            .withType(TYPE)
            .withSource(SOURCE)
            .withData(DATACONTENTTYPE_JSON, DATASCHEMA, DATA_JSON_SERIALIZED)
            .withSubject(SUBJECT)
            .withTime(TIME)
            .withExtension("astring", "aaa")
            .withExtension("aboolean", "true")
            .withExtension("anumber", "10");

        CloudEvent event = templateBuilder.build();
        CloudEvent cloned = templateBuilder.newBuilder().build();

        assertThat(cloned).isEqualTo(event);
    }

    @Test
    void testMissingId() {
        assertThatCode(() -> CloudEventBuilder
            .v1()
            .withSource(URI.create("http://localhost"))
            .withType("aaa")
            .build()
        ).hasMessageContaining("Attribute 'id' cannot be null");
    }

    @Test
    void testMissingSource() {
        assertThatCode(() -> CloudEventBuilder
            .v1()
            .withId("000")
            .withType("aaa")
            .build()
        ).hasMessageContaining("Attribute 'source' cannot be null");
    }

    @Test
    void testMissingType() {
        assertThatCode(() -> CloudEventBuilder
            .v1()
            .withId("000")
            .withSource(URI.create("http://localhost"))
            .build()
        ).hasMessageContaining("Attribute 'type' cannot be null");
    }

    @Test
    void testClassNotFoundExceptionForValidator(){

        System.setProperty("header.validator.class", "io.cloudevents.core.v1.CustomCloudEventValidatorTest");
            assertThatCode(() -> CloudEventBuilder
            .v1()
            .withId("000")
            .withSource(URI.create("http://localhost"))
            .withType("aaa")
            .withExtension("astring", 10)
            .build()
        ).hasMessageContaining("Unable to load the header.validator.class passed as vm argument");
    }

    @Test
    void testClassCastExceptionForValidator(){

        System.setProperty("header.validator.class", "io.cloudevents.core.v1.CustomCloudEventValidator");
        assertThatCode(() -> CloudEventBuilder
            .v1()
            .withId("000")
            .withSource(URI.create("http://localhost"))
            .withType("aaa")
            .withExtension("astring", 10)
            .build()
        ).hasMessageContaining("Passed class is not an instance of CloudEventValidator");
    }

    /**
     * This test is to check for the mandatory extension 'namespace' as per Organization need
     */
    @Test
    void testMissingNamespaceExtension(){

        System.setProperty("header.validator.class", "io.cloudevents.core.v1.CustomCloudEventValidatorImpl");
        assertThatCode(() -> CloudEventBuilder
            .v1()
            .withId("000")
            .withSource(URI.create("http://localhost"))
            .withType("aaa")
            .build()
        ).hasMessageContaining("Extension 'namespace' cannot be null");
    }

}
