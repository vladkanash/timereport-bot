def CONSOLE = 'CONSOLE'
def PATTERN = '%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n'

appender(CONSOLE, ConsoleAppender.class) {
    encoder(PatternLayoutEncoder) {
        pattern = PATTERN
    }
}

root(INFO, [CONSOLE])