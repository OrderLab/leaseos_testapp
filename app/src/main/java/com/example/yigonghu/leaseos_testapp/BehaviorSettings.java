/*
 *  @author Yigong Hu <hyigong1@cs.jhu.edu>
 *
 *  The LeaseOS Project
 *
 *  Copyright (c) 2018, Johns Hopkins University - Order Lab.
 *      All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.example.yigonghu.leaseos_testapp;

import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;

/**
 *
 */
public class BehaviorSettings extends Preference.BaseSavedState {

    public static final boolean BEHAVIOR_ENABLED_DEFAULT = true;
    public static final int BEHAVIOR_TYPE_DEFAULT = 3;
    public static final long HOLD_LIMIT_DEFAULT = 1 * TimeUtils.MILLIS_PER_MINUTE; // 1 minutes
    public static final long WAIT_REATE_DEFAULT = 1 * TimeUtils.MILLIS_PER_MINUTE; // 3 minutes
    //public static final BehaviorSettings DEFAULT_SETTINGS = getDefaultSettings();

    public boolean behaviorEnabled;
    public int behaviorType;
    public long holdLimit;;
    public long waitRate;

    public BehaviorSettings(Parcelable superState) {
        super(superState);
    }

    public BehaviorSettings(Parcel in) {
        super(in);
        behaviorEnabled = (in.readInt() != 0);
        behaviorType = in.readInt();
        holdLimit = in.readLong();
        waitRate = in.readLong();

    }
/*
    public static BehaviorSettings getDefaultSettings() {
        BehaviorSettings settings = new BehaviorSettings();
        settings.behaviorEnabled = BEHAVIOR_ENABLED_DEFAULT;
        settings.behaviorType = BEHAVIOR_TYPE_DEFAULT;
        settings.holdLimit = HOLD_LIMIT_DEFAULT;
        settings.waitRate = WAIT_REATE_DEFAULT;

        return settings;
    }*/

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(behaviorEnabled ? 1 : 0);
        dest.writeInt(behaviorType);
        dest.writeLong(holdLimit);
        dest.writeLong(waitRate);

    }

    public static final Creator<BehaviorSettings> CREATOR = new Creator<BehaviorSettings>() {
        @Override
        public BehaviorSettings createFromParcel(Parcel source) {
            return new BehaviorSettings(source);
        }

        @Override
        public BehaviorSettings[] newArray(int size) {
            return new BehaviorSettings[size];
        }
    };
}