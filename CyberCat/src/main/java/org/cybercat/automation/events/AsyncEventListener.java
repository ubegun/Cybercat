package org.cybercat.automation.events;

import org.apache.log4j.Logger;

//TODO: to implement asynchronous events  
public abstract class AsyncEventListener<T extends Event> extends EventListener<T> implements Runnable {

    private static final Logger log = Logger.getLogger(AsyncEventListener.class);

    protected int wait = 10000;

    public AsyncEventListener(Class<T> eventType, int taskPriority) {
        super(eventType, taskPriority);
    }

    @Override
    public void run() {
        try {
            try {
                Thread.sleep(wait);
            } catch (InterruptedException ie) {
                log.info("Interrupt event pause " + this.toString());
            }
            doActon(this.event);
        } catch (Exception e) {           
            throw new RuntimeException("Exception while executing asynchronous event listener: " + this.toString(), e);
        }finally{
            this.event = null;  
        }
    }

    private T event;

    public synchronized void doAsincActon(final T event) throws Exception {
        Thread worker = new Thread(this);
        this.event = event;
        worker.start();
    }

}
