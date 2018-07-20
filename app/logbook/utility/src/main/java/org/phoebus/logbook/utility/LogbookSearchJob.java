package org.phoebus.logbook.utility;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.phoebus.framework.jobs.Job;
import org.phoebus.framework.jobs.JobManager;
import org.phoebus.framework.jobs.JobMonitor;
import org.phoebus.framework.jobs.JobRunnable;
import org.phoebus.logbook.LogClient;
import org.phoebus.logbook.LogEntry;

/**
 * Background job for searching channels from the channelfinder directory
 * service
 * 
 * @author Kunal Shroff, Kay Kasemir
 */
public class LogbookSearchJob implements JobRunnable {
    private final LogClient client;
    private final String pattern;
    private final Consumer<List<LogEntry>> logentry_handler;
    private final BiConsumer<String, Exception> error_handler;

    public static Job submit(LogClient client, final String pattern,
            final Consumer<List<LogEntry>> channel_handler, final BiConsumer<String, Exception> error_handler) {
        return JobManager.schedule("searching Channelfinder for : " + pattern,
                new LogbookSearchJob(client, pattern, channel_handler, error_handler));
    }

    private LogbookSearchJob(LogClient client, String pattern, Consumer<List<LogEntry>> channel_handler,
            BiConsumer<String, Exception> error_handler) {
        super();
        this.client = client;
        this.pattern = pattern;
        this.logentry_handler = channel_handler;
        this.error_handler = error_handler;
    }

    @Override
    public void run(JobMonitor monitor) throws Exception {
        monitor.beginTask("searching for log entires : " + pattern);
        List<LogEntry> channels = client.findLogsBySearch(pattern);
        logentry_handler.accept(channels);
    }
}