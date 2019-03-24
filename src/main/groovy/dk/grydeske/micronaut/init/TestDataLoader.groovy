package dk.grydeske.micronaut.init

import dk.grydeske.micronaut.helpers.FileInfoRepository
import groovy.util.logging.Slf4j
import io.micronaut.context.annotation.Requires
import io.micronaut.context.env.Environment
import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.discovery.event.ServiceStartedEvent
import io.micronaut.scheduling.annotation.Async

import javax.inject.Singleton

@Singleton
@Requires(env = Environment.TEST) // Don't load data in tests.
@Slf4j
class TestDataLoader implements ApplicationEventListener<ServiceStartedEvent> {

    final FileInfoRepository fileInfoRepository

    TestDataLoader(final FileInfoRepository fileInfoRepository) {
        this.fileInfoRepository = fileInfoRepository;
    }

    @Async
    @Override
    public void onApplicationEvent(final ServiceStartedEvent event) {
        log.info("Reading image metadata at startup")
        fileInfoRepository.initialize(2)
    }
}
