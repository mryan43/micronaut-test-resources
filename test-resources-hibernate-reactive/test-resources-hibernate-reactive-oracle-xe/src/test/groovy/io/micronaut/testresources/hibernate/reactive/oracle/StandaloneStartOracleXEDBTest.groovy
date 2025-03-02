package io.micronaut.testresources.hibernate.reactive.oracle

import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.testresources.hibernate.reactive.core.Book
import io.micronaut.testresources.hibernate.reactive.core.BookRepository
import io.micronaut.testresources.testcontainers.AbstractTestContainersSpec
import jakarta.inject.Inject

import java.time.Duration
import java.time.temporal.ChronoUnit

@MicronautTest(transactional = false, environments = "standalone")
class StandaloneStartOracleXEDBTest extends AbstractTestContainersSpec {
    private static final Duration TEST_TIMEOUT = Duration.of(10, ChronoUnit.SECONDS)

    @Inject
    BookRepository repository

    def "starts a Oracle container"() {
        def book = new Book(title: "Micronaut for Spring developers")
        repository.save(book).block(TEST_TIMEOUT)

        when:
        def books = repository.findAll().collectList().block(TEST_TIMEOUT)

        then:
        books.size() == 1

        and:
        listContainers().size() == 1
    }

    @Override
    String getImageName() {
        "oracle"
    }
}
