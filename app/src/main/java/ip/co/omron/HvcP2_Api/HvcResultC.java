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

/*
 * FRAME result (1 frame)
 */
public class HvcResultC {

    static class C_POINT {
        int nX;
        int nY;
    }

    /*
     * Face direction estimation
     */
    static class C_FRAME_RESULT_DIRECTION {
        int nLR;
        int nUD;
        int nRoll;
        int nConfidence;
    }

    /*
     * Age estimation
     */
    static class C_FRAME_RESULT_AGE {
        int nAge;
        int nConfidence;
    }

    /*
     * Gender estimation
     */
    static class C_FRAME_RESULT_GENDER {
        int nGender;
        int nConfidence;
    }

    /*
     * Gaze estimation
     */
    static class C_FRAME_RESULT_GAZE {
        int nLR;
        int nUD;
    }

    /*
     * Blink estimation
     */
    static class C_FRAME_RESULT_BLINK {
        int nLeftEye;
        int nRightEye;
    }

    /*
     * Facial expression estimation
     */
    static class C_FRAME_RESULT_EXPRESSION {
        int[] anScore = new int[5];
        int nDegree;
    }

    /*
     * Face recognition
     */
    static class C_FRAME_RESULT_RECOGNITION {
        int nUID;
        int nScore;
    }

    /*
     * One detection result
     */
    static class C_FRAME_RESULT_DETECTION {
        C_POINT center = new C_POINT();
        int nSize;
        int nConfidence;
    }

    /*
     * Face detection and post-processing result (1 person)
     */
    static class C_FRAME_RESULT_FACE {
        C_POINT center = new C_POINT();
        int nSize;
        int nConfidence;
        C_FRAME_RESULT_DIRECTION direction = new C_FRAME_RESULT_DIRECTION();
        C_FRAME_RESULT_AGE age = new C_FRAME_RESULT_AGE();
        C_FRAME_RESULT_GENDER gender = new C_FRAME_RESULT_GENDER();
        C_FRAME_RESULT_GAZE gaze = new C_FRAME_RESULT_GAZE();
        C_FRAME_RESULT_BLINK blink = new C_FRAME_RESULT_BLINK();
        C_FRAME_RESULT_EXPRESSION expression = new C_FRAME_RESULT_EXPRESSION();
        C_FRAME_RESULT_RECOGNITION recognition = new C_FRAME_RESULT_RECOGNITION();
    }

    /*
     * One Human body detection result
     */
    static class C_FRAME_RESULT_BODYS {
        int nCount;
        C_FRAME_RESULT_DETECTION[] body = new C_FRAME_RESULT_DETECTION[35];

        public C_FRAME_RESULT_BODYS() {
            for ( int i=0; i<body.length; i++ ) {
                body[i] = new C_FRAME_RESULT_DETECTION();
            }
        }
    }

    /*
     * Face detection and post-processing result (1 frame)
     */
    static class C_FRAME_RESULT_FACES {
        int nCount;
        C_FRAME_RESULT_FACE[] face = new C_FRAME_RESULT_FACE[35];

        public C_FRAME_RESULT_FACES() {
            for ( int i=0; i<face.length; i++ ) {
                face[i] = new C_FRAME_RESULT_FACE();
            }
        }
    }

    C_FRAME_RESULT_BODYS bodys = new C_FRAME_RESULT_BODYS();
    C_FRAME_RESULT_FACES faces = new C_FRAME_RESULT_FACES();
}
