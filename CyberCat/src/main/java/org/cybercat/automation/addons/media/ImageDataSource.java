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

import javax.media.Time;
import javax.media.protocol.PullBufferDataSource;
import javax.media.protocol.PullBufferStream;

public class ImageDataSource extends PullBufferDataSource {

    ImageBufferStream stream;
    
    public ImageDataSource(ImageBufferStream stream) {
        this.stream = stream;
    }

    @Override
    public PullBufferStream[] getStreams() {
        return new ImageBufferStream[]{stream};
    }

    @Override
    public void connect() throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void disconnect() {
        // TODO Auto-generated method stub

    }

    @Override
    public String getContentType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getControl(String arg0) {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.media.protocol.DataSource#getControls()
     */
    @Override
    public Object[] getControls() {
        return new Object[0];
    }

    /* (non-Javadoc)
     * @see javax.media.protocol.DataSource#getDuration()
     */
    @Override
    public Time getDuration() {
        return new Time(1);
    }

    @Override
    public void start() throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void stop() throws IOException {
        // TODO Auto-generated method stub

    }

}
