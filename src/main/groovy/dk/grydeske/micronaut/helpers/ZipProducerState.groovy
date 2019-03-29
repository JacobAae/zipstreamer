package dk.grydeske.micronaut.helpers

import groovy.util.logging.Slf4j

import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Slf4j
class ZipProducerState {

    List<String> files
    FileInfoRepository fileInfoRepository
    ChunkedByteoutputStream outputStream
    ZipOutputStream zipStream
    Integer currentFileIndex
    InputStream currentFileStream
    Integer bufferCapacity = 4096

    Boolean done = false

    ZipProducerState(List<String> files, FileInfoRepository fileInfoRepository) {
        this.files = files
        this.fileInfoRepository = fileInfoRepository
        currentFileIndex = 0
        outputStream = new ChunkedByteoutputStream(bufferCapacity)
        zipStream = new ZipOutputStream(outputStream)

        gotoNextFile()
    }

    void produceNext() {
        while( !done && !outputStream.hasNext() ) {
            produceNextChunk()
        }
    }

    boolean hasNext() {
        outputStream.hasNext()
    }

    byte[] getNext() {
        outputStream.getNext()
    }

    // This can produce small chunks if images are very small
    private void produceNextChunk() {
        if( done ) { return }
        byte[] tempBuffer = new byte[bufferCapacity]

        int read = currentFileStream.read(tempBuffer)
        if( read > 0) {
            if( read < bufferCapacity) {
                zipStream.write(Arrays.copyOf(tempBuffer, read))
            } else {
                zipStream.write(tempBuffer)
            }
        } else {
            gotoNextFile()
        }
    }

    private gotoNextFile() {
        log.trace("gotoNextFile")
        if( currentFileIndex < files.size() ) {
            String filename = files[currentFileIndex]
            URL resource = this.class.classLoader.getResource(filename)
            currentFileStream = resource.openStream()

            ZipEntry zipEntry = new ZipEntry(filename)
            zipEntry.setMethod(ZipEntry.STORED)
            zipEntry.crc = fileInfoRepository.getCrc(filename)
            zipEntry.size = fileInfoRepository.getSize(filename)

            zipStream.putNextEntry(zipEntry)

            currentFileIndex++
        } else {
            zipStream.closeEntry()
            zipStream.finish()
            zipStream.close()

            outputStream.finishedWithInput()
            done = true
        }
    }
}
