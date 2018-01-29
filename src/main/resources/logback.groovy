import ch.qos.logback.classic.encoder.PatternLayoutEncoder

import static ch.qos.logback.classic.Level.TRACE

def LOGGING_PATTERN = "%date{ISO8601} [%thread] %-5level %logger{50} - %message%n"

// For Production set redrouter.logging.appenders = FILE,EMAIL.
def loggingPath = "./testIdempotent.log"

appender("CONSOLE", ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = LOGGING_PATTERN
  }
}

appender("FILE", RollingFileAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = LOGGING_PATTERN
  }
  file = loggingPath
  rollingPolicy(TimeBasedRollingPolicy) {
    fileNamePattern = "${loggingPath}.%d{yyyy-MM-dd}"
  }
}

root(TRACE, ["CONSOLE", "FILE"])