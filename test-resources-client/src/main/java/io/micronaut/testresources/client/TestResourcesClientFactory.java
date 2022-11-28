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
package io.micronaut.testresources.client;

import io.micronaut.context.ApplicationContext;
import io.micronaut.http.client.DefaultHttpClientConfiguration;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.HttpClientConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Properties;

/**
 * A factory responsible for creating a {@link TestResourcesClient}.
 * Because this client is used in services which are loaded via
 * service loading during application context building, we can't use
 * regular dependency injection, so this factory creates an application
 * context to create the client.
 */
public final class TestResourcesClientFactory {
    private static final String DEFAULT_TIMEOUT_SECONDS = "60";

    private TestResourcesClientFactory() {

    }

    static TestResourcesClient configuredAt(URL configFile) {
        Properties props = new Properties();
        try (InputStream input = configFile.openStream()) {
            props.load(input);
        } catch (IOException e) {
            throw new TestResourcesException(e);
        }
        try {
            String serverUri = props.getProperty(TestResourcesClient.SERVER_URI);
            String accessToken = props.getProperty(TestResourcesClient.ACCESS_TOKEN);
            int clientReadTimeout = Integer.parseInt(props.getProperty(TestResourcesClient.CLIENT_READ_TIMEOUT, DEFAULT_TIMEOUT_SECONDS));
            HttpClientConfiguration config = new DefaultHttpClientConfiguration();
            config.setReadTimeout(Duration.of(clientReadTimeout, ChronoUnit.SECONDS));
            HttpClient client = HttpClient.create(new URL(serverUri), config);
            return new DefaultTestResourcesClient(client, accessToken);
        } catch (MalformedURLException e) {
            throw new TestResourcesException(e);
        }
    }

    static Optional<TestResourcesClient> fromSystemProperties() {
        String serverUri = System.getProperty(ConfigFinder.systemPropertyNameOf(TestResourcesClient.SERVER_URI));
        if (serverUri != null) {
            String accessToken = System.getProperty(ConfigFinder.systemPropertyNameOf(TestResourcesClient.ACCESS_TOKEN));
            String clientTimeoutString = System.getProperty(ConfigFinder.systemPropertyNameOf(TestResourcesClient.CLIENT_READ_TIMEOUT), DEFAULT_TIMEOUT_SECONDS);
            int clientReadTimeout = Integer.parseInt(clientTimeoutString);
            HttpClientConfiguration config = new DefaultHttpClientConfiguration();
            config.setReadTimeout(Duration.of(clientReadTimeout, ChronoUnit.SECONDS));
            HttpClient client;
            try {
                client = HttpClient.create(new URL(serverUri), config);
            } catch (MalformedURLException e) {
                return Optional.empty();
            }
            return Optional.of(new DefaultTestResourcesClient(client, accessToken));
        }
        return Optional.empty();
    }

    /**
     * Extracts the {@link TestResourcesClient} from the given {@link ApplicationContext}.
     * @param context the application context
     * @return the test resources client
     */
    public static TestResourcesClient extractFrom(ApplicationContext context) {
        return context.getEnvironment()
            .getPropertySourceLoaders()
            .stream()
            .filter(TestResourcesClientPropertySourceLoader.class::isInstance)
            .map(TestResourcesClientPropertySourceLoader.class::cast)
            .map(TestResourcesClientPropertySourceLoader::getClient)
            .findFirst()
            .orElse(Optional.empty())
            .orElse(null);
    }
}
