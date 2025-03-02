package io.micronaut.testresources.client

import io.micronaut.context.ApplicationContext
import io.micronaut.context.exceptions.ConfigurationException
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification
import spock.lang.TempDir

import java.nio.file.Path

@MicronautTest
class TestResourcesClientTest extends Specification {

    @TempDir
    Path tempDir

    @Inject
    EmbeddedServer server

    def "property source loader registers properties from server"() {
        def app = createApplication()

        expect:
        app.getProperty("dummy1", String).get() == 'value for dummy1'
        app.getProperty("dummy2", String).get() == 'value for dummy2'

        when:
        app.getProperty("missing", String).empty

        then:
        ConfigurationException e = thrown()
        e.message == 'Could not resolve placeholder ${auto.test.resources.missing}'
    }

    private ApplicationContext createApplication() {
        def propertiesFile = tempDir.resolve("test-resources.properties").toFile()
        def cl = new URLClassLoader([] as URL[], this.class.classLoader) {
            @Override
            URL findResource(String name) {
                if ("/test-resources.properties" == name) {
                    return propertiesFile.toURI().toURL()
                }
                return super.findResource(name)
            }
        }

        propertiesFile << """
            ${TestResourcesClient.SERVER_URI}=${server.getURI()}
        """.stripIndent()
        def app = ApplicationContext.builder()
                .classLoader(cl)
                .properties(['server': 'false'])
                .start()
        assert !app.findBean(TestServer).present
        return app
    }

}
