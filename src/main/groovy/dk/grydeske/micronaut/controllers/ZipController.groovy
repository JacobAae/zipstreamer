package dk.grydeske.micronaut.controllers

import dk.grydeske.micronaut.services.ZipService
import groovy.util.logging.Slf4j
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.types.files.StreamedFile
import io.reactivex.Flowable

@Slf4j
@Controller("/zip")
class ZipController {

    ZipService zipService

    ZipController(ZipService zipService) {
        this.zipService = zipService
    }

    @Get("/plain")
    StreamedFile plain() {
        log.debug("Download plain StreamedFile zip")
        zipService.streamedFileZipfile
    }

    @Get("/chunked" )
    @Produces('application/octet-stream')
    @Header(name="Content-Disposition", value='attachment; filename="filename.zip"')
    Flowable<byte[]> chunked()  {
        log.debug("Download chunked/reactive zip")
        zipService.zipInChunks
    }
}