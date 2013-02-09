package com.greenlaw110.rythm.sandbox;

import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.template.ITemplate;

import java.util.concurrent.*;

/**
 * A secure executing service run template in a separate thread in case there are infinite loop, and also set
 * the SecurityManager in the executing thread
 */
public class SandboxExecutingService {
    private static ILogger logger = Logger.get(SandboxExecutingService.class);
    private ScheduledExecutorService scheduler = null;
    private long timeout = 1000;

    public SandboxExecutingService(int poolSize, SecurityManager sm, long timeout) {
        scheduler = new ScheduledThreadPoolExecutor(poolSize, new SandboxThreadFactory(sm), new ThreadPoolExecutor.AbortPolicy());
        this.timeout = timeout;
    }

    private Future<Object> exec(final ITemplate t) {
        return scheduler.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                try {
                    return t.render();
                } catch (Exception e) {
                    return e;
                }
            }
        });
    }

    public String execute(ITemplate t) {
        Future<Object> f = null;
        try {
            f = exec(t);
            Object o = f.get(timeout, TimeUnit.MILLISECONDS);
            if (o instanceof RuntimeException) throw (RuntimeException) o;
            if (o instanceof Exchanger) throw new RuntimeException((Exception) o);
            return (null == o) ? "" : o.toString();
        } catch (RuntimeException e) {
            f.cancel(true);
            throw e;
        } catch (Exception e) {
            f.cancel(true);
            throw new RuntimeException(e);
        }
    }


    public Future<Object> executeAsync(final ITemplate t) {
        final Future<Object> f = exec(t);
        // make sure it get cancelled if timeout
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                f.cancel(true);
            }
        }, timeout, TimeUnit.MILLISECONDS);
        return f;
    }

    public void shutdown() {
        SandboxThreadFactory.shutdown();
        scheduler.shutdownNow();
    }

    @Override
    protected void finalize() throws Throwable {
        shutdown();
    }
}
