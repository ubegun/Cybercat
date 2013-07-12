/**Copyright 2013 The Cybercat project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cybercat.automation.addons.media;

import java.io.IOException;
import java.nio.file.Path;

import javax.media.ConfigureCompleteEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.DataSink;
import javax.media.EndOfMediaEvent;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoProcessorException;
import javax.media.NotConfiguredError;
import javax.media.Processor;
import javax.media.RealizeCompleteEvent;
import javax.media.control.TrackControl;
import javax.media.datasink.DataSinkErrorEvent;
import javax.media.datasink.DataSinkEvent;
import javax.media.datasink.DataSinkListener;
import javax.media.datasink.EndOfStreamEvent;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.FileTypeDescriptor;

import org.cybercat.automation.addons.media.events.CloseMediaDemonThread;
import org.cybercat.automation.addons.media.events.InterruptVideoEvent;
import org.cybercat.automation.events.EventManager;


public class JpegImagesToMovie implements ControllerListener, DataSinkListener {

    
    //stream asynchronous state 
    public enum AsyncState{ Unrealized(100), Realizing(200), Realized(300), Prefetching(400), Prefetched(500), Started(600);
        
        private int id;

        AsyncState(int id){
         this.id = id;
        }

        public int getId(){
            return id;
        }
        
       public static AsyncState valueOf(int id){
           AsyncState[] states =  AsyncState.values();
           for(int i=0 ; i< states.length; i++)
               if(states[i].id == id)
                   return states[i];
          return null; 
       }
    }; 
    
    private ImageDataSource dataSource;
    private Path output;
    private Processor processor;
    private DataSink dsink;
    private MediaLocator outML;
    private EventManager eventManager;
    private long threadId;
    
    public JpegImagesToMovie(ImageDataSource dataSource, EventManager eventManager, long threadId) {
        this.dataSource = dataSource;
        this.eventManager = eventManager;
        this.threadId = threadId;
    }

    public void makeMovie() throws NoProcessorException, IOException {
        outML = new MediaLocator(output.toUri().toURL());
        processor = Manager.createProcessor(dataSource);
        processor.addControllerListener(this);
        processor.configure();

    }

    private synchronized void doStart() {
        try {
            dsink = createDataSink(processor, outML);
            dsink.addDataSinkListener(this);
            log("start processing...");
            processor.start();
            dsink.start();
        } catch (Exception e) {
            log("IO error during processing");
            e.printStackTrace();
            eventManager.notify(new InterruptVideoEvent(threadId));
        }
    }

    private synchronized void asynchConfiguration() throws NotConfiguredError {
        processor.setContentDescriptor(new ContentDescriptor(FileTypeDescriptor.QUICKTIME));// FileTypeDescriptor.MSVIDEO
        TrackControl tcs[] = processor.getTrackControls();
        Format f[] = tcs[0].getSupportedFormats();
        if (f == null || f.length <= 0) {
            log("The mux does not support the input format: " + tcs[0].getFormat());
            return;
        }

        tcs[0].setFormat(f[0]);
        log("Setting the track format to: " + f[0]);

        // We are done with programming the processor. Let's just
        // realize it.
        processor.realize();
    }

    /**
     * Cleanup processor.
     */
    private boolean doStop() {
        boolean hasStoppedSuccessfuly = true;
        try {
            dsink.close();
        } catch (Exception e) {
            e.printStackTrace();
            hasStoppedSuccessfuly = false;
        }
        processor.removeControllerListener(this);        
        log("...done processing.");
        eventManager.notify(new CloseMediaDemonThread());
        return hasStoppedSuccessfuly;
    }

    /**
     * Create the DataSink.
     */
    private DataSink createDataSink(Processor p, MediaLocator outML) {
        DataSource ds;
        if ((ds = p.getDataOutput()) == null) {
            log("Something is really wrong: the processor does not have an output DataSource");
            return null;
        }

        DataSink dsink;

        try {
            dsink = Manager.createDataSink(ds, outML);
            dsink.open();
        } catch (Exception e) {
            log("Cannot create the DataSink: " + e);
            eventManager.notify(new InterruptVideoEvent());            
            return null;
        }

        return dsink;
    }

    /**
     * Event handler for the file writer.
     */
    @Override
    public void dataSinkUpdate(DataSinkEvent evt) {
        log("process data sinking");
        if (evt instanceof EndOfStreamEvent || evt instanceof DataSinkErrorEvent) {
            doStop();
        }
    }

    /**
     * Controller Listener.
     */
    @Override
    public void controllerUpdate(ControllerEvent evt) {
        log("Update controller");
        if (evt instanceof ConfigureCompleteEvent) {
            asynchConfiguration();
        } else if (evt instanceof RealizeCompleteEvent) {
            doStart();
        } else if (evt instanceof EndOfMediaEvent) {
            evt.getSourceController().stop();
            evt.getSourceController().close();
        }
    }

    public AsyncState getState(){
        return AsyncState.valueOf(processor.getState());
    }
    
    public boolean isWorking() {
        return processor.getState() > 0 ;
    }

    public void setOutput(Path output) {
        this.output = output;
    }

    private void log(String message){
        System.err.println("Video Log --> Thread#" + threadId + " \t" + message); 
    }
    
}
