package au.com.redenergy.camel

import groovy.util.logging.Slf4j
import org.apache.camel.processor.idempotent.FileIdempotentRepository

@Slf4j
class Main {
  static void main(String... args) {
    log.info("Starting app $args")
    org.apache.camel.main.Main main = new org.apache.camel.main.Main()
    main.bind("myRepo", FileIdempotentRepository.fileIdempotentRepository(new File("./idempotent.file.store"), 100000, 1000000))
    log.info("Adding build")
    main.addRouteBuilder(new FileMoveRouteBuilder())
    log.info("Running camel")
    main.run()
  }
}
