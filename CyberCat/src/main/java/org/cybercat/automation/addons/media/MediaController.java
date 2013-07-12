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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.cybercat.automation.Configuration;
import org.cybercat.automation.addons.media.JpegImagesToMovie.AsyncState;
import org.cybercat.automation.addons.media.events.CloseMediaDemonThread;
import org.cybercat.automation.addons.media.events.InterruptVideoEvent;
import org.cybercat.automation.addons.media.events.StartMediaEvent;
import org.cybercat.automation.addons.media.events.StopMediaEvent;
import org.cybercat.automation.addons.media.events.UpdateMediaStream;
import org.cybercat.automation.core.AddonContainer;
import org.cybercat.automation.events.EventListener;
import org.cybercat.automation.events.EventManager;
import org.cybercat.automation.events.EventStartTest;
import org.cybercat.automation.events.EventStopTest;
import org.cybercat.automation.persistence.TestArtifactManager;
import org.cybercat.automation.persistence.model.TestCase;
import org.cybercat.automation.utils.WorkFolder;

public class MediaController implements AddonContainer {

    public final static String VIDEO = "Video";

    public enum CState {
        createted, init, started, closed
    }

    private MediaMetaData metaData;
    private MediaDemonTrhead mdt;
    private List<EventListener<?>> listeners;
    private JpegImagesToMovie jpegImagesToMovie;
    private EventManager eventManager;
    private FrameSet frameSet;

    public MediaController(MediaMetaData mediaMetaData, EventManager eventManager) {
        this.metaData = mediaMetaData;
        this.eventManager = eventManager;
    }

    public void initVideoStream() {
        listeners = new ArrayList<EventListener<?>>();
        long threadId = Thread.currentThread().getId();
        frameSet = new FrameSet(threadId);
        ImageBufferStream buffer = new ImageBufferStream(metaData, frameSet, threadId);

        ImageDataSource imageDataSource = new ImageDataSource(buffer);
        jpegImagesToMovie = new JpegImagesToMovie(imageDataSource, eventManager, threadId);
        mdt = new MediaDemonTrhead(jpegImagesToMovie);

        listeners.add(new ImagePreProcessor(metaData, eventManager));
        listeners.addAll(frameSet.createListeners());
        listeners.addAll(Arrays.asList(buffer.createListeners()));
        listeners.add(new EventListener<EventStartTest>(EventStartTest.class, 10) {

            @Override
            public void doActon(EventStartTest event) throws Exception {
                Path path = Paths.get(WorkFolder.MediaFolder.toString(), event.getTestClass().getName(), event.getTestClass().getSimpleName()
                        + ".mov");
                jpegImagesToMovie.setOutput(path);
                mdt.setCState(CState.init);
                // update artifact info
                TestCase test = new TestCase(event.getTestClass().getName());
                test.setVideo(path.toString());
                TestArtifactManager.updateTestInfo(test);
            }

        });

        listeners.add(new EventListener<UpdateMediaStream>(UpdateMediaStream.class, 10) {

            @Override
            public void doActon(UpdateMediaStream event) throws Exception {
                if (mdt.getCState().equals(CState.init)) {
                    mdt.setCState(CState.started);
                    mdt.start();
                    eventManager.notify(new StartMediaEvent());
                }
            }

        });

        listeners.add(new EventListener<CloseMediaDemonThread>(CloseMediaDemonThread.class, 10) {

            @Override
            public void doActon(CloseMediaDemonThread event) throws Exception {
                mdt.setCState(CState.closed);
            }

        });

        listeners.add(new EventListener<EventStopTest>(EventStopTest.class, 10) {

            @Override
            public void doActon(EventStopTest event) throws Exception {
                eventManager.notify(new StopMediaEvent());
                stop();
            }
        });

        listeners.add(new EventListener<InterruptVideoEvent>(InterruptVideoEvent.class, 1) {

            @Override
            public void doActon(InterruptVideoEvent event) throws Exception {
                for (EventListener<?> listener : listeners) {
                    if (eventManager.unsubscribe(listener)) {
                        System.err.println(listener.getEventType().toString() + " listener successfully removed.");
                    } else {
                        System.err.println("WARNING!!  " + listener.getEventType().toString() + " listener not found.");
                    }
                }
                frameSet.finalize();
            }
        });

    }

    public void stop() {
        mdt.setCState(CState.init);
        int i = 10;
        do {
            try {
                mdt.join(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.err.println("JMF controller state:" + mdt.getPState());
        } while (mdt.isAlive() && !mdt.getCState().equals(CState.closed) && i-- > 0);
        System.out.println("Done");
    }

    private class MediaDemonTrhead extends Thread {
        private JpegImagesToMovie moveMaker;

        private CState state;

        public MediaDemonTrhead(JpegImagesToMovie moveMaker) {
            setCState(CState.createted);
            this.setDaemon(true);
            this.moveMaker = moveMaker;
            this.setName("Media demon process");
        }

        public AsyncState getPState() {
            return moveMaker.getState();
        }

        @Override
        public void run() {
            try {
                moveMaker.makeMovie();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            do {
                yield();
            } while (!state.equals(CState.closed));
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!111");

        }

        public CState getCState() {
            return state;
        }

        public void setCState(CState state) {
            this.state = state;
        }
    }

    @Override
    public Collection<EventListener<?>> createListeners(Configuration config) {
        if (!config.hasFeature(VIDEO)) {
            return new ArrayList<EventListener<?>>();
        }
        initVideoStream();
        return listeners;
    }

    @Override
    public String[] getSupportedFeatures() {
        return new String[] { VIDEO };
    }

}
