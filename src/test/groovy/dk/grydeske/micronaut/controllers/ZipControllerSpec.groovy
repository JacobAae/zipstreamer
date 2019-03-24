package dk.grydeske.micronaut.controllers

import io.micronaut.context.ApplicationContext
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

class ZipControllerSpec extends Specification {

    @Shared @AutoCleanup EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer)
    @Shared @AutoCleanup RxHttpClient client = embeddedServer.applicationContext.createBean(RxHttpClient, embeddedServer.getURL())


    void "test plain zip"() {
        given:
        HttpResponse response = client.toBlocking().exchange("/zip/plain")

        expect:
        response.status == HttpStatus.OK
    }

    void "test chunked zip"() {
        given:
        HttpResponse response = client.toBlocking().exchange("/zip/chunked")

        expect:
        response.status == HttpStatus.OK
    }

}
