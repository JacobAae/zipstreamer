package dk.grydeske.micronaut.helpers

import groovy.util.logging.Slf4j

import javax.inject.Singleton
import java.util.zip.CRC32

@Slf4j
@Singleton
class FileInfoRepository {

    int numberOfFiles
    Map<String,Long> crcValues = [:]
    Map<String,Long> sizes = [:]

    void initialize(int numberOfFiles = 20) {
        this.numberOfFiles = numberOfFiles
        filenames.each { String filename ->
            log.debug("Adding entry: $filename")
            preprocesFile(filename)
        }
    }

    private void preprocesFile(String filename) {
        URL resource = this.class.classLoader.getResource(filename)
        if( !resource) {
            return
        }
        InputStream stream = resource.openStream()

        CRC32 crc = new CRC32()
        int currentByte = stream.read()
        Long size = 0
        while ( currentByte != -1) {
            size++
            crc.update(currentByte)
            currentByte = stream.read()
        }
        crcValues[filename] = crc.value
        sizes[filename] = size
    }

    List<String> getFilenames() {
        (1..numberOfFiles).collect { "${it}.jpg"}
    }

    Long getCrc(String filename) {
        crcValues[filename]
    }

    Long getSize(String filename) {
        sizes[filename]
    }
}
