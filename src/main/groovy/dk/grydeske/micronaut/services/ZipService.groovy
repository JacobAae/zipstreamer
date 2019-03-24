package dk.grydeske.micronaut.services

import dk.grydeske.micronaut.helpers.FileInfoRepository
import dk.grydeske.micronaut.helpers.ZipProducerState
import groovy.util.logging.Slf4j
import io.micronaut.http.server.types.files.StreamedFile
import io.reactivex.Flowable

import javax.inject.Singleton
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Singleton
@Slf4j
class ZipService {

    FileInfoRepository fileInfoRepository

    ZipService(FileInfoRepository fileInfoRepository) {
        this.fileInfoRepository = fileInfoRepository
    }

    StreamedFile getStreamedFileZipfile() {
        log.debug("Producing zipfile as StreamedFile")

        ByteArrayOutputStream baos = createZipOutput(fileInfoRepository.filenames)
        def inputstream = new ByteArrayInputStream(baos.toByteArray())

        new StreamedFile(inputstream, "download.zip")
    }

    ByteArrayOutputStream createZipOutput(List<String> filenames) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ZipOutputStream myzipFile = new ZipOutputStream(baos)

        // Add the images
        filenames.each { String filename ->
            myzipFile.putNextEntry(new ZipEntry(filename))
            URL resource = this.class.classLoader.getResource(filename)

            myzipFile.write(resource.openStream().bytes)
            myzipFile.closeEntry()
        }

        myzipFile.finish()
        myzipFile.close()

        return baos
    }

    Flowable<byte[]> getZipInChunks() {
        log.debug("Producing zip in chunks")

        Flowable.generate (
            { ->
                log.debug("Generating initial state")
                new ZipProducerState(fileInfoRepository.filenames, fileInfoRepository)
            },
            { ZipProducerState state, emitter ->
                log.debug("Emit next chunk")
                state.produceNext()
                if( state.hasNext() ) {
                    emitter.onNext( state.getNext() )
                } else {
                    emitter.onComplete()
                }
                state
            } as io.reactivex.functions.BiConsumer
        )
    }

}
