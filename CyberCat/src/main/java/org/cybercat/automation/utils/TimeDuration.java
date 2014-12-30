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
package org.cybercat.automation.utils;

import java.util.concurrent.TimeUnit;

public class TimeDuration {

    public static final String SPLIT_SYMBOL = ":";
    private final long hours;
    private final long minutes;
    private final long seconds;
    private final long totalSeconds;

    public TimeDuration(long totalSeconds) {
        this.totalSeconds = totalSeconds;
        this.hours = TimeUnit.SECONDS.toHours(totalSeconds);
        this.minutes = TimeUnit.SECONDS.toMinutes(totalSeconds) - this.hours * 60;
        this.seconds = totalSeconds - TimeUnit.SECONDS.toMinutes(totalSeconds) * 60;
    }

    public TimeDuration(long hours, long minutes, long seconds) {
        this.totalSeconds = toTotalSeconds(this.hours = hours, this.minutes = minutes, this.seconds = seconds);
    }

    public TimeDuration(String strDuration) {
        String[] timeComponents = strDuration.split(SPLIT_SYMBOL);
        int index = 0;
        if (timeComponents.length == 3) {
            this.hours = Integer.valueOf(timeComponents[index++]);
        } else {
            this.hours = 0;
        }
        this.minutes = Integer.valueOf(timeComponents[index++]);
        this.seconds = Integer.valueOf(timeComponents[index]);
        this.totalSeconds = toTotalSeconds(this.hours, this.minutes, this.seconds);
    }

    public long getHours() {
        return hours;
    }

    public long getMinutes() {
        return minutes;
    }

    public long getSeconds() {
        return seconds;
    }

    public long getTotalSeconds() {
        return totalSeconds;
    }

    public String getStrHours() {
        return timeToString(hours);
    }

    public String getStrMinutes() {
        return timeToString(minutes);
    }

    public String getStrSeconds() {
        return timeToString(seconds);
    }

    private String timeToString(long timeComponent) {
        return timeComponent < 10 ? "0" + String.valueOf(timeComponent) : String.valueOf(timeComponent);
    }

    public static long toTotalSeconds(long hours, long minutes, long seconds) {
        return (hours * 60 + minutes) * 60 + seconds;
    }

    @Override
    public String toString() {
        return (getHours() == 0 ? "" : getStrHours() + SPLIT_SYMBOL) + getStrMinutes() + SPLIT_SYMBOL + getStrSeconds();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeDuration that = (TimeDuration) o;

        if (hours != that.hours) return false;
        if (minutes != that.minutes) return false;
        if (seconds != that.seconds) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (hours ^ (hours >>> 32));
        result = 31 * result + (int) (minutes ^ (minutes >>> 32));
        result = 31 * result + (int) (seconds ^ (seconds >>> 32));
        return result;
    }
}