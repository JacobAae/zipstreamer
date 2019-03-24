package dk.grydeske.micronaut.helpers

import groovy.util.logging.Slf4j

/*
    Get output in chunks - as byte[]
 */
@Slf4j
class ChunkedByteoutputStream extends OutputStream {

    int bufferCapacity
    int currentLocation
    List<byte[]> buffers
    byte[] currentBuffer

    ChunkedByteoutputStream(int bufferCapacity) {
        this.bufferCapacity = bufferCapacity
        buffers = new LinkedList<byte[]>()
        currentBuffer = new byte[bufferCapacity]
    }

    @Override
    synchronized void write(int b) throws IOException {
        if( currentLocation < bufferCapacity) {
            currentBuffer[currentLocation] = (byte) b
            currentLocation++
        }
        // If buffer is full -
        if( currentLocation == bufferCapacity) {
            buffers << currentBuffer
            currentBuffer = new byte[bufferCapacity]
            currentLocation = 0
        }
    }

    synchronized void finishedWithInput() {
        buffers << Arrays.copyOf(currentBuffer, currentLocation)
    }

    boolean hasNext() {
        buffers
    }

    byte[] getNext() {
        buffers.remove(0)
    }
}
