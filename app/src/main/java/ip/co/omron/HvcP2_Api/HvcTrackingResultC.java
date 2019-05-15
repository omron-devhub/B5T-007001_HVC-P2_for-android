/*
 * Copyright 2018  OMRON Corporation
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
package jp.co.omron.HvcP2_Api;

public class HvcTrackingResultC {

    // STB Tracking status
    static final int STB_STATUS_NO_DATA = -1;
    static final int STB_STATUS_CALCULATING = 0;
    static final int STB_STATUS_COMPLETE = 1;
    static final int STB_STATUS_FIXED = 2;

    static final int STB_TRID_NOT_TRACKED = -1;

    /*
     * General purpose stabilization result structure
     */
    static class C_RES {
        int status; // STATUS
        int conf;
        int value;
    }

    /*
     * Result of Gaze estimation
     */
    static class C_GAZE {
        int status; // STATUS
        int conf;
        int UD;
        int LR;
    }

    /*
     * Result of Face direction estimation
     */
    static class C_DIRECTION {
        int status; // STATUS
        int conf;
        int yaw;
        int pitch;
        int roll;
    }

    /*
     * Result of Blink estimation
     */
    static class C_BLINK {
        int status; // STATUS
        int ratioL;
        int ratioR;
    }

    /*
     * Detection position structure
     */
    static class C_POS {
        int x;
        int y;
    }

    /*
     * Face stabilization result structure
     */
    static class C_FACE {
        int nDetectID;
        int nTrackingID;
        C_POS center = new C_POS();
        int nSize;
        int conf;
        C_DIRECTION direction = new C_DIRECTION();
        C_RES age = new C_RES();
        C_RES gender = new C_RES();
        C_GAZE gaze = new C_GAZE();
        C_BLINK blink = new C_BLINK();
        C_RES expression = new C_RES();
        C_RES recognition = new C_RES();
    }

    /*
     * Human body stabilization result structure
     */
    static class C_BODY {
        int nDetectID;
        int nTrackingID;
        C_POS center = new C_POS();
        int nSize;
        int conf;
    }
}
