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
package io.micronaut.testresources.hibernate.reactive.postgresql;

import io.micronaut.testresources.hibernate.reactive.core.AbstractHibernateReactiveTestResourceProvider;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

/**
 * A test resource provider which will spawn a PostgreSQL test container.
 */
public class HibernateReactivePostgreSQLTestResourceProvider extends AbstractHibernateReactiveTestResourceProvider<PostgreSQLContainer<?>> {

    @Override
    protected String getSimpleName() {
        return "postgres";
    }

    @Override
    protected String getDefaultImageName() {
        return "postgres";
    }

    @Override
    protected PostgreSQLContainer<?> createContainer(DockerImageName imageName, Map<String, Object> requestedProperties, Map<String, Object> testResourcesConfiguration) {
        return new PostgreSQLContainer<>(imageName);
    }

}
