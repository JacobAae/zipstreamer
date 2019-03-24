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
        if( done ) {
            return
        }
        byte[] tempBuffer
        tempBuffer = new byte[bufferCapacity]

        int read = currentFileStream.read(tempBuffer)
        if( read > 0) {
            if( read < bufferCapacity) {
                byte[] temp = Arrays.copyOf(tempBuffer, read)
                zipStream.write(temp)
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
            URL resource = this.class.classLoader.getResource(files[currentFileIndex])
            currentFileStream = resource.openStream()

            ZipEntry zipEntry = new ZipEntry(files[currentFileIndex])
            zipEntry.setMethod(ZipEntry.STORED)
            zipEntry.crc = fileInfoRepository.getCrc(files[currentFileIndex])
            zipEntry.size = fileInfoRepository.getSize(files[currentFileIndex])

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
