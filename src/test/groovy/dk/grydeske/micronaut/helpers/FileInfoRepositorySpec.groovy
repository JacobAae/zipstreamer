package dk.grydeske.micronaut.helpers

import spock.lang.Shared
import spock.lang.Specification

class FileInfoRepositorySpec extends Specification {

    @Shared
    FileInfoRepository fileInfoRepository

    void setupSpec() {
        fileInfoRepository = new FileInfoRepository()
    }

    void "preprocessing of file works when file found"() {
        expect:
        fileInfoRepository.preprocesFile("1.jpg")
    }

    void "preprocessing of file does nothing when file not found"() {
        expect:
        fileInfoRepository.preprocesFile("unknown")
    }

    void "crc and size is available after preprocessing"() {
        setup:
        String filename = "20.jpg"

        expect:
        !fileInfoRepository.getCrc(filename)
        !fileInfoRepository.getSize(filename)

        when:
        fileInfoRepository.preprocesFile(filename)

        then:
        fileInfoRepository.getCrc(filename) == 3538479249
        fileInfoRepository.getSize(filename) == 1908506
    }

}
