package com.mycompany;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
    A class demonstrating usage of a SLF4J logging framework.
   @author <A HREF="mailto:lxegre@gmail.com">lxegre</A>
   @version $Revision: 1.0 $ $Date: 2010/12/05$
**/
public class LogCombo {
    // Logger to be used for all logging messages (ERROR>WARN>INFO>DEBUG>TRACE)
    private final static Logger LOG = LoggerFactory.getLogger(LogCombo.class);

    public static void main( String[] args ) {
        // Print logger internal state if debug is enabled
        if (LOG.isDebugEnabled()) {
            LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
            StatusPrinter.print(lc);
        }

        LOG.info("Application starting...");
        
        // Do something 10 times
        doSomething(10);

        // Do something -1 (will cause error)
        doSomething(-1);

        LOG.info("Application ending...");
    }

    private static void doSomething(int times) {
        LOG.debug("Doing something {} times", Integer.valueOf(times));

        if (times < 0) {
            LOG.error("Invalid argument: times must be positive.");
            return;
        }

        for (int i=0; i<times; i++) {
            LOG.trace("Doing it - {}", Integer.valueOf(i));
            try {
                // Doing something
                Thread.sleep(500L);
            } catch (Exception ex) {
               LOG.error("{}",ex.getStackTrace());
            }     
        }
    }
}
