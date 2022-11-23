/*
 * Copyright 2017-2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.testresources.postgres;

import io.micronaut.testresources.jdbc.AbstractJdbcTestResourceProvider;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A test resource provider which will spawn a MySQL test container.
 */
public class PostgreSQLTestResourceProvider extends AbstractJdbcTestResourceProvider<PostgreSQLContainer<?>> {
    private static final List<String> SUPPORTED_DB_TYPES = Collections.unmodifiableList(
        Arrays.asList("postgresql", "postgres", "pg")
    );

    @Override
    protected String getSimpleName() {
        return "postgres";
    }

    @Override
    protected String getDefaultImageName() {
        return "postgres";
    }

    @Override
    protected List<String> getDbTypes() {
        return SUPPORTED_DB_TYPES;
    }

    @Override
    protected PostgreSQLContainer<?> createContainer(DockerImageName imageName, Map<String, Object> requestedProperties, Map<String, Object> testResourcesConfiguration) {
        return new PostgreSQLContainer<>(imageName);
    }

}
