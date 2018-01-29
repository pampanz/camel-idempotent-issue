package au.com.redenergy.camel

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.processor.idempotent.FileIdempotentRepository

class FileMoveRouteBuilder extends RouteBuilder {

  @Override
  void configure() throws Exception {
    from("file://./fromSource?delay=5000&idempotent=true&idempotentKey=\$simple{file:name}&idempotentRepository=#myRepo")
        .autoStartup(true)
        .routeId("source")
        .log("Received file [\${file:name}], proceed to move to target")
        .to("direct:dedup")

    from("file://./fromSource2?delay=5000&idempotent=true&idempotentKey=\$simple{file:name}&idempotentRepository=#myRepo")
        .autoStartup(true)
        .routeId("source2")
        .log("Received file [\${file:name}], proceed to move to target")
        .to("direct:dedup")

    from("direct:dedup")
        .routeId("Deduping and moving")
        .log("Deduping file [\${file:name}]")
        .idempotentConsumer(simple('${file:name}'), FileIdempotentRepository.fileIdempotentRepository(new File("dedup.idempotent.file.store"), 100000, 1000000))
        .setHeader("CamelFileName",simple('${file:name}-${date:now:yyyyMMddhhmmss}'))
        .log("Final file to the target is [\${file:name}]")
        .to("file://./toTarget")
  }
}
