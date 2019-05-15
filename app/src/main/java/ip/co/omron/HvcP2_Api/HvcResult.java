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
import static jp.co.omron.HvcP2_Api.HvcResultC.*;

/*
 * Class storing the detection/estimation result of HVC-P2
 */
public class HvcResult {
    ArrayList<FaceResult> faces;
    ArrayList<DetectionResult> bodies;
    ArrayList<DetectionResult> hands;

    public HvcResult() {
        faces = new ArrayList<FaceResult>();
        bodies = new ArrayList<DetectionResult>();
        hands = new ArrayList<DetectionResult>();
    }

    public int read_from_buffer(int exec_func, byte[] data) {
        int cur = 0;
        int body_count = (data[cur + 0] & 0xff);
        int hand_count = (data[cur + 1] & 0xff);
        int face_count = (data[cur + 2] & 0xff);

        cur += 4;   // for reserved

        // Human body detection
        for (int i = 0; i < body_count; i++) {
            DetectionResult res = new DetectionResult();
            res.pos_x = ((data[cur + 0] & 0xff) + (data[cur + 1] << 8));
            res.pos_y = ((data[cur + 2] & 0xff) + (data[cur + 3] << 8));
            res.size = ((data[cur + 4] & 0xff) + (data[cur + 5] << 8));
            res.conf = ((data[cur + 6] & 0xff) + (data[cur + 7] << 8));
            bodies.add(res);
            cur += 8;
        }

        // Hand detection
        for (int i = 0; i < hand_count; i++) {
            DetectionResult res = new DetectionResult();
            res.pos_x = ((data[cur + 0] & 0xff) + (data[cur + 1] << 8));
            res.pos_y = ((data[cur + 2] & 0xff) + (data[cur + 3] << 8));
            res.size = ((data[cur + 4] & 0xff) + (data[cur + 5] << 8));
            res.conf = ((data[cur + 6] & 0xff) + (data[cur + 7] << 8));
            hands.add(res);
            cur += 8;
        }

        // Face detection
        for (int i = 0; i < face_count; i++) {
            FaceResult res = new FaceResult();

            res.pos_x = ((data[cur + 0] & 0xff) + (data[cur + 1] << 8));
            res.pos_y = ((data[cur + 2] & 0xff) + (data[cur + 3] << 8));
            res.size = ((data[cur + 4] & 0xff) + (data[cur + 5] << 8));
            res.conf = ((data[cur + 6] & 0xff) + (data[cur + 7] << 8));
            cur += 8;

            // Face direction
            if ((exec_func & EX_DIRECTION) == EX_DIRECTION) {
                DirectionResult dir = new DirectionResult(-1,-1,-1,-1);
                dir.LR = (short) ((data[cur + 0] & 0xff) + (data[cur + 1] << 8));
                dir.UD = (short) ((data[cur + 2] & 0xff) + (data[cur + 3] << 8));
                dir.roll = (short) ((data[cur + 4] & 0xff) + (data[cur + 5] << 8));
                dir.conf = (short) ((data[cur + 6] & 0xff) + (data[cur + 7] << 8));
                res.direction = dir;
                cur += 8;
            }

            // Age estimation
            if ((exec_func & EX_AGE) == EX_AGE) {
                AgeResult age = new AgeResult(-1,-1);
                age.age = data[cur + 0];
                age.conf = (short) ((data[cur + 1] & 0xff) + (data[cur + 2] << 8));
                res.age = age;
                cur += 3;
            }

            // Gender estimation
            if ((exec_func & EX_GENDER) == EX_GENDER) {
                GenderResult gender = new GenderResult(-1,-1);
                gender.gender = data[cur + 0];
                gender.conf = (short) ((data[cur + 1] & 0xff) + (data[cur + 2] << 8));
                res.gender = gender;
                cur += 3;
            }

            // Gaze estimation
            if ((exec_func & EX_GAZE) == EX_GAZE) {
                GazeResult gaze = new GazeResult(-1,-1);
                gaze.gazeLR = data[cur + 0];
                gaze.gazeUD = data[cur + 1];
                res.gaze = gaze;
                cur += 2;
            }

            // Blink estimation
            if ((exec_func & EX_BLINK) == EX_BLINK) {
                BlinkResult blink = new BlinkResult(-1,-1);
                blink.ratioL = (short) ((data[cur + 0] & 0xff) + (data[cur + 1] << 8));
                blink.ratioR = (short) ((data[cur + 2] & 0xff) + (data[cur + 3] << 8));
                res.blink = blink;
                cur += 4;
            }

            // Expression estimation
            if ((exec_func & EX_EXPRESSION) == EX_EXPRESSION) {
                ExpressionResult expression = new ExpressionResult(-1,-1,-1,-1,-1,-1);
                expression.neutral = data[cur + 0];
                expression.happiness = data[cur + 1];
                expression.surprise = data[cur + 2];
                expression.anger = data[cur + 3];
                expression.sadness = data[cur + 4];
                expression.neg_pos = data[cur + 5];
                res.expression = expression;
                cur += 6;
            }

            // Face recognition
            if ((exec_func & EX_RECOGNITION) == EX_RECOGNITION) {
                RecognitionResult recognition = new RecognitionResult(-1,-1);
                recognition.uid = (short) ((data[cur + 0] & 0xff) + (data[cur + 1] << 8));
                recognition.score = (short) ((data[cur + 2] & 0xff) + (data[cur + 3] << 8));
                res.recognition = recognition;
                cur += 4;
            }
            faces.add(res);
        }
        return cur;
    }

    public void export_to_C_FRAME_RESULT(HvcResultC frame_result) {
        // Human body detection result
        frame_result.bodys.nCount = bodies.size();
        for (int i = 0; i < bodies.size(); i++) {
            DetectionResult src_body = bodies.get(i);
            C_FRAME_RESULT_DETECTION dst_body = frame_result.bodys.body[i];

            dst_body.center.nX = src_body.pos_x;
            dst_body.center.nY = src_body.pos_y;
            dst_body.nSize = src_body.size;
            dst_body.nConfidence = src_body.conf;
        }

        // Face detection result
        frame_result.faces.nCount = faces.size();
        for (int i = 0; i < faces.size(); i++) {
            FaceResult src_face = faces.get(i);
            C_FRAME_RESULT_FACE dst_face = frame_result.faces.face[i];

            dst_face.center.nX = src_face.pos_x;
            dst_face.center.nY = src_face.pos_y;
            dst_face.nSize = src_face.size;
            dst_face.nConfidence = src_face.conf;

            // Face direction result
            if ( src_face.direction != null ) {
                dst_face.direction.nLR = src_face.direction.LR;
                dst_face.direction.nUD = src_face.direction.UD;
                dst_face.direction.nRoll = src_face.direction.roll;
                dst_face.direction.nConfidence = src_face.direction.conf;
            }
            // Age estimation result
            if ( src_face.age != null ) {
                dst_face.age.nAge = src_face.age.age;
                dst_face.age.nConfidence = src_face.age.conf;
            }
            // Gender estimation result
            if ( src_face.gender != null ) {
                dst_face.gender.nGender = src_face.gender.gender;
                dst_face.gender.nConfidence = src_face.gender.conf;
            }
            // Gaze estimation result
            if ( src_face.gaze != null ) {
                dst_face.gaze.nLR = src_face.gaze.gazeLR;
                dst_face.gaze.nUD = src_face.gaze.gazeUD;
            }
            // Blink estimation result
            if ( src_face.blink != null ) {
                dst_face.blink.nLeftEye = src_face.blink.ratioL;
                dst_face.blink.nRightEye = src_face.blink.ratioR;
            }
            // Expression estimation result
            if ( src_face.expression != null ) {
                dst_face.expression.anScore[0] = src_face.expression.neutral;
                dst_face.expression.anScore[1] = src_face.expression.happiness;
                dst_face.expression.anScore[2] = src_face.expression.surprise;
                dst_face.expression.anScore[3] = src_face.expression.anger;
                dst_face.expression.anScore[4] = src_face.expression.sadness;
                dst_face.expression.nDegree = src_face.expression.neg_pos;
            }
            // Recognition result
            if ( src_face.recognition != null ) {
                dst_face.recognition.nUID = src_face.recognition.uid;
                dst_face.recognition.nScore = src_face.recognition.score;
            }
        }
    }

    public String getString() {
        String strResult;
        strResult = "Face count= " + Integer.toString(faces.size()) + "\n";
        for (int i = 0; i < faces.size(); i++) {
            FaceResult face = faces.get(i);
            strResult += "\t[" + Integer.toString(i) + "]\t" + face.getString() + "\n";
        }
        strResult += "Body count= " + Integer.toString(bodies.size()) + "\n";
        for (int i = 0; i < bodies.size(); i++) {
            strResult += "\t[" + Integer.toString(i) + "]\t" + bodies.get(i).getString() + "\n";
        }
        strResult += "Hand count= " + Integer.toString(hands.size())  + "\n";
        for (int i = 0; i < hands.size(); i++) {
            strResult += "\t[" + Integer.toString(i) + "]\t" + hands.get(i).getString() + "\n";
        }
        return strResult;
    }
}


