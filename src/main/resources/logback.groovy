// LogBack configuration file for enabling logging in LogCombo example application

import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.FileAppender
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.classic.net.SocketAppender
import ch.qos.logback.core.spi.LifeCycle
import ch.qos.logback.classic.filter.ThresholdFilter
import de.huxhorn.lilith.logback.appender.ClassicMultiplexSocketAppender
import de.huxhorn.lilith.logback.encoder.ClassicLilithEncoder

import static ch.qos.logback.classic.Level.ERROR
import static ch.qos.logback.classic.Level.WARN
import static ch.qos.logback.classic.Level.INFO
import static ch.qos.logback.classic.Level.DEBUG
import static ch.qos.logback.classic.Level.TRACE

// Retrieve DEV env
def DEV = System.getenv("DEV")

// Resolve log base name
def baseName = "LogCombo"
if (DEV == 1) {
    println "Setting development log profile."
    baseName = "LogComboTest"
}

// Invoking the scan() method instructs logback to periodically scan the logback.groovy file for changes( default = 1min).
scan("120 seconds")

// A time stamp
def bySecond = timestamp("yyyyMMdd'T'HHmmss")

// Get user name
def USER = System.getenv("USER")
def HOSTNAME = hostname // a hack trick to resolve visibility


// Standard ouput (console) log
appender("CONSOLE_LOG", ConsoleAppender) {
    filter(ThresholdFilter) {
        level = INFO
    }
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} [${USER}@${HOSTNAME}] [%thread] %-5level %logger{36} - %msg%n"
    }

}

// A global application log file, with rolling policy
appender("APP_LOG", RollingFileAppender) {
    //file = "${baseName}-${bySecond}.log"
    file = "${baseName}.log"
    rollingPolicy(FixedWindowRollingPolicy) {
        fileNamePattern = "${baseName}.%i.log.zip"
        minIndex = 1
        maxIndex = 9
    }
    triggeringPolicy(SizeBasedTriggeringPolicy) {
        maxFileSize = "2MB"
    }
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} [${USER}@${HOSTNAME}] [%thread] %-5level %logger{36} - %msg%n"
    }
    append = true
}

// A simple log file in USER home directory
def USER_HOME = System.getProperty("user.home");

appender("USER_LOG", RollingFileAppender) {
    file = "${USER_HOME}/${baseName}.log"
    rollingPolicy(FixedWindowRollingPolicy) {
        fileNamePattern = "${USER_HOME}/${baseName}.%i.log.zip"
        minIndex = 1
        maxIndex = 2
    }
    triggeringPolicy(SizeBasedTriggeringPolicy) {
        maxFileSize = "1MB"
    }
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} - %msg%n"
    }
    filter(ThresholdFilter) {
        level = INFO
    }
}

appender("LILITH_LOG", RollingFileAppender) {
    file = "${baseName}.lilith"
    encoder(ClassicLilithEncoder) {
        includeCallerData = true
    }
    rollingPolicy(FixedWindowRollingPolicy) {
        fileNamePattern = "${baseName}.%i.lilith.zip"
        minIndex = 1
        maxIndex = 9
    }
    triggeringPolicy(SizeBasedTriggeringPolicy) {
        maxFileSize = "2MB"
    }
        append = true
}

// Connection to lilith socket appender (better than standart, compressed serialized java data is sent)
appender("LILITH_SOCKET", ClassicMultiplexSocketAppender) {
    compressing = true
    reconnectionDelay = 10000
    includeCallerData = true
    remoteHost = "localhost"
    // For multiple hosts : You MUST add an import declaration as appropriate for [RemoteHosts]
    /*
    RemoteHosts... aRemoteHosts = new RemoteHosts()
    aRemoteHosts.remoteHost = "localhost"
    if(aRemoteHosts instanceof LifeCycle)
    aRemoteHosts.start()
    RemoteHosts = aRemoteHosts
     */
}

// Registers log events to Appenders
root(INFO, ["CONSOLE_LOG","APP_LOG", "USER_LOG","LILITH_LOG", "LILITH_SOCKET"])

// Example of logger for a specific class/package
//logger("com.mycompany.${baseName}", DEBUG, ["STDOUT"])