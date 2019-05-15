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

import java.util.ArrayList;

import static jp.co.omron.HvcP2_Api.P2Def.*;
import static jp.co.omron.HvcP2_Api.OkaoResult.*;
import static jp.co.omron.HvcP2_Api.HvcTrackingResultC.*;

/*
 * Class storing tracking result
 */
public class HvcTrackingResult {

    private enum StatusDic {
        STB_STATUS_CALCULATING {
            @Override
            public String toString() {
                return "CALCULATING";
            }
        },
        STB_STATUS_COMPLETE {
            @Override
            public String toString() {
                return "COMPLETE";
            }
        },
        STB_STATUS_FIXED {
            @Override
            public String toString() {
                return "FIXED";
            }
        },
        STB_STATUS_NO_DATA {
            @Override
            public String toString() {
                return "NO_DATA";
            }
        }
    }

    static class TrackingAgeResult {
        AgeResult age = new AgeResult(-1, -1);
        StatusDic tracking_status = StatusDic.STB_STATUS_NO_DATA;

        public TrackingAgeResult(int status, int val, int conf) {
            switch (status) {
                case STB_STATUS_CALCULATING:
                    tracking_status = StatusDic.STB_STATUS_CALCULATING;
                    break;
                case STB_STATUS_COMPLETE:
                    tracking_status = StatusDic.STB_STATUS_COMPLETE;
                    break;
                case STB_STATUS_FIXED:
                    tracking_status = StatusDic.STB_STATUS_FIXED;
                    break;
                default:
                    tracking_status = StatusDic.STB_STATUS_NO_DATA;
                    break;
            }
            age.age = val;
            age.conf = conf;
        }

        public String getString(boolean isSTB) {
            String strResult;
            strResult = age.getString();
            if ( isSTB ) {
                strResult += " Status:" + tracking_status.toString();
            }
            return strResult;
        }
    }

    static class TrackingGenderResult {
        GenderResult gender = new GenderResult(-1,-1);
        StatusDic tracking_status = StatusDic.STB_STATUS_NO_DATA;

        public TrackingGenderResult(int status, int val, int conf) {
            switch (status) {
                case STB_STATUS_CALCULATING:
                    tracking_status = StatusDic.STB_STATUS_CALCULATING;
                    break;
                case STB_STATUS_COMPLETE:
                    tracking_status = StatusDic.STB_STATUS_COMPLETE;
                    break;
                case STB_STATUS_FIXED:
                    tracking_status = StatusDic.STB_STATUS_FIXED;
                    break;
                default:
                    tracking_status = StatusDic.STB_STATUS_NO_DATA;
                    break;
            }
            gender.gender = val;
            gender.conf = conf;
        }

        public String getString(boolean isSTB) {
            String strResult;
            strResult = gender.getString();
            if ( isSTB ) {
                strResult += " Status:" + tracking_status.toString();
            }
            return strResult;
        }
    }

    class TrackingRecognitionResult {
        RecognitionResult recognition = new RecognitionResult(-1,-1);
        StatusDic tracking_status = StatusDic.STB_STATUS_NO_DATA;

        public TrackingRecognitionResult(int status, int uid, int score) {
            switch (status) {
                case STB_STATUS_CALCULATING:
                    tracking_status = StatusDic.STB_STATUS_CALCULATING;
                    break;
                case STB_STATUS_COMPLETE:
                    tracking_status = StatusDic.STB_STATUS_COMPLETE;
                    break;
                case STB_STATUS_FIXED:
                    tracking_status = StatusDic.STB_STATUS_FIXED;
                    break;
                default:
                    tracking_status = StatusDic.STB_STATUS_NO_DATA;
                    break;
            }
            recognition.uid = uid;
            recognition.score = score;
        }

        public String getString(boolean isSTB) {
            String strResult;
            strResult = recognition.getString();
            if ( isSTB ) {
                strResult += " Status:" + tracking_status.toString();
            }
            return strResult;
        }
    }

    class TrackingResult {
        int pos_x;
        int pos_y;
        long size;
        int conf;
        int detection_id;
        int tracking_id;

        TrackingResult() {
        }

        TrackingResult(int x, int y, long s, int c, int id, int tid) {
            pos_x = x;
            pos_y = y;
            size = s;
            conf = c;
            detection_id = id;
            tracking_id = tid;
        }

        public String getString(boolean isSTB) {
            String strResult;
            if ( isSTB ) {
                strResult = "TrackingID:" + Integer.toString(tracking_id) +
                        "\tX:" + Integer.toString(pos_x) + " Y:" + Integer.toString(pos_y) +
                        ", Size:" + Long.toString(size) + " Conf:" + Integer.toString(conf);
            } else {
                strResult = "DetectionID:" + Integer.toString(detection_id) +
                        "\tX:" + Integer.toString(pos_x) + " Y:" + Integer.toString(pos_y) +
                        ", Size:" + Long.toString(size) + " Conf:" + Integer.toString(conf);
            }
            return strResult;
        }
    }

    class TrackingFaceResult extends TrackingResult {
        DirectionResult direction;
        TrackingAgeResult age;
        TrackingGenderResult gender;
        GazeResult gaze;
        BlinkResult blink;
        ExpressionResult expression;
        TrackingRecognitionResult recognition;

        TrackingFaceResult(int x, int y, long s, int c, int id, int tid) {
            pos_x = x;
            pos_y = y;
            size = s;
            conf = c;
            detection_id = id;
            tracking_id = tid;

            direction = null;
            age = null;
            gender = null;
            gaze = null;
            blink = null;
            expression = null;
            recognition = null;
        }

        public String getString(boolean isSTB) {
            String strResult;
            if ( isSTB ) {
                strResult = "TrackingID:" + Integer.toString(tracking_id) +
                        "\tX:" + Integer.toString(pos_x) + " Y:" + Integer.toString(pos_y) +
                        ", Size:" + Long.toString(size) + " Conf:" + Integer.toString(conf) + "\n";
            } else {
                strResult = "DetectionID:" + Integer.toString(detection_id) +
                        "\tX:" + Integer.toString(pos_x) + " Y:" + Integer.toString(pos_y) +
                        ", Size:" + Long.toString(size) + " Conf:" + Integer.toString(conf) + "\n";
            }
            if (direction != null) {
                strResult += "      " + direction.getString() + "\n";
            }
            if (age != null) {
                strResult += "      " + age.getString(isSTB) + "\n";
            }
            if (gender != null) {
                strResult += "      " + gender.getString(isSTB) + "\n";
            }
            if (gaze != null) {
                strResult += "      " + gaze.getString() + "\n";
            }
            if (blink != null) {
                strResult += "      " + blink.getString() + "\n";
            }
            if (expression != null) {
                strResult += "      " + expression.getString() + "\n";
            }
            if (recognition != null) {
                strResult += "      " + recognition.getString(isSTB);
            }
            return strResult;
        }
    }

    class FaceList extends ArrayList<TrackingFaceResult> {
        /*
         * Appends the result of STB output to this face list.
         */
        void append_C_FACE_RES35(int exec_func, int face_count, C_FACE[] face_res35) {
            for (int i = 0; i < face_count; i++) {
                C_FACE f = face_res35[i];
                TrackingFaceResult tr_f = new TrackingFaceResult(f.center.x, f.center.y, f.nSize,
                        f.conf, f.nDetectID, f.nTrackingID);

                if ((exec_func & EX_AGE) == EX_AGE) {
                    C_RES age = f.age;
                    tr_f.age = new TrackingAgeResult(age.status, age.value, age.conf);
                }

                if ((exec_func & EX_GENDER) == EX_GENDER) {
                    C_RES g = f.gender;
                    tr_f.gender = new TrackingGenderResult(g.status, g.value, g.conf);
                }

                if ((exec_func & EX_RECOGNITION) == EX_RECOGNITION) {
                    C_RES r = f.recognition;
                    tr_f.recognition = new TrackingRecognitionResult(r.status, r.value, r.conf);
                }

                /*
                 * Note: We do not use
                 * the functions (Face direction, Gaze, Blink and
                 * Expression estimation)for STBLib.
                 * So the part of that functions is not implemented here.
                 */

                add(tr_f);
            }
        }

        void append_direction_list(ArrayList<FaceResult> faces) {
            for (int i = 0; i < faces.size(); i++) {
                get(i).direction = faces.get(i).direction;
            }
        }

        void append_gaze_list(ArrayList<FaceResult> faces) {
            for (int i = 0; i < faces.size(); i++) {
                get(i).gaze = faces.get(i).gaze;
            }
        }

        void append_blink_list(ArrayList<FaceResult> faces) {
            for (int i = 0; i < faces.size(); i++) {
                get(i).blink = faces.get(i).blink;
            }
        }

        void append_expression_list(ArrayList<FaceResult> faces) {
            for (int i = 0; i < faces.size(); i++) {
                get(i).expression = faces.get(i).expression;
            }
        }
    }

    class BodyList extends ArrayList<TrackingResult> {
        void append_BODY_RES35(int exec_func, int body_count, C_BODY[] body_res35) {
            for (int i = 0; i < body_count; i++) {
                C_BODY b = body_res35[i];
                TrackingResult tr_b = new TrackingResult(b.center.x, b.center.y, b.nSize,
                                                            b.conf, b.nDetectID, b.nTrackingID);
                add(tr_b);
            }
        }
    }

    class HandList extends ArrayList<TrackingResult> {
        void append_hand_list(ArrayList<DetectionResult> hands) {
            for (int i = 0; i < hands.size(); i++) {
                DetectionResult h = hands.get(i);
                TrackingResult hand_res = new TrackingResult(h.pos_x, h.pos_y, h.size, h.conf, i, -1);
                add(hand_res);
            }
        }
    }

    FaceList faces;
    BodyList bodies;
    HandList hands;

    public HvcTrackingResult() {
        faces = new FaceList();
        bodies = new BodyList();
        hands = new HandList();
    }

    public String getString(boolean isSTB) {
        String strResult;
        strResult = "Face Count = " + Integer.toString(faces.size()) + "\n";
        for ( int i=0; i<faces.size(); i++ ) {
            strResult += "  [" + Integer.toString(i) + "] " + faces.get(i).getString(isSTB) + "\n";
        }
        strResult += "Body Count = " + Integer.toString(bodies.size()) + "\n";
        for ( int i=0; i<bodies.size(); i++ ) {
            strResult += "  [" + Integer.toString(i) + "] " + bodies.get(i).getString(isSTB) + "\n";
        }
        strResult += "Hand Count = " + Integer.toString(hands.size()) + "\n";
        for ( int i=0; i<hands.size(); i++ ) {
            strResult += "  [" + Integer.toString(i) + "] " + hands.get(i).getString(false) + "\n";
        }
        return strResult;
    }

    void clear() {
        faces.clear();
        bodies.clear();
        hands.clear();
    }

    void appned_FRAME_RESULT(HvcResult frame_result) {
        // Body detection result
        for ( int i=0; i<frame_result.bodies.size(); i++ ) {
            DetectionResult b = frame_result.bodies.get(i);
            TrackingResult body_res = new TrackingResult(b.pos_x, b.pos_y, b.size, b.conf, i,
                    STB_TRID_NOT_TRACKED);
            bodies.add(body_res);
        }
        // Hand detection result
        for ( int i=0; i<frame_result.hands.size(); i++ ) {
            DetectionResult h = frame_result.hands.get(i);
            TrackingResult hand_res = new TrackingResult(h.pos_x, h.pos_y, h.size, h.conf, i,
                    STB_TRID_NOT_TRACKED);
            hands.add(hand_res);
        }

        // Face detection result
        for ( int i=0; i<frame_result.faces.size(); i++ ) {
            FaceResult f = frame_result.faces.get(i);
            TrackingFaceResult face_res = new TrackingFaceResult(f.pos_x, f.pos_y, f.size, f.conf, i,
                    STB_TRID_NOT_TRACKED);
            // Face direction result
            if ( f.direction != null ) {
                face_res.direction = new DirectionResult(f.direction.LR,
                        f.direction.UD,
                        f.direction.roll,
                        f.direction.conf);
            }
            // Age estimation result
            if ( f.age != null ) {
                face_res.age = new TrackingAgeResult(STB_STATUS_NO_DATA, f.age.age, f.age.conf);
            }
            // Gender estimation result
            if ( f.gender != null ) {
                face_res.gender = new TrackingGenderResult(STB_STATUS_NO_DATA, f.gender.gender, f.gender.conf);
            }
            // Gaze estimation result
            if ( f.gaze != null ) {
                face_res.gaze = new GazeResult(f.gaze.gazeLR, f.gaze.gazeUD);
            }
            // Blink estimation result
            if ( f.blink != null ) {
                face_res.blink = new BlinkResult(f.blink.ratioR, f.blink.ratioL);
            }
            // Expression estimation result
            if ( f.expression != null ) {
                face_res.expression = new ExpressionResult(f.expression.neutral,
                        f.expression.happiness,
                        f.expression.surprise,
                        f.expression.anger,
                        f.expression.sadness,
                        f.expression.neg_pos);
            }
            // Face recognition result
            if ( f.recognition != null ) {
                face_res.recognition = new TrackingRecognitionResult(STB_STATUS_NO_DATA, f.recognition.uid,
                        f.recognition.score);
            }

            // Appends to face list.
            faces.add(face_res);
        }
    }
}
