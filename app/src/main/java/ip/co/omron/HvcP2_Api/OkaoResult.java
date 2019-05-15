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

import static jp.co.omron.HvcP2_Api.P2Def.*;

class OkaoResult {
    private enum GenderDic {
        GENDER_UNKNOWN {
            @Override
            public String toString() {
                return "Unknown";
            }
        },
        GENDER_FEMALE {
            @Override
            public String toString() {
                return "Female";
            }
        },
        GENDER_MALE {
            @Override
            public String toString() {
                return "Male";
            }
        }
    }

    private enum ExpDic {
        EXP_UNKNOWN {
            @Override
            public String toString() {
                return "Unknown";
            }
        },
        EXP_NEUTRAL {
            @Override
            public String toString() {
                return "Neutral";
            }
        },
        EXP_HAPPINESS {
            @Override
            public String toString() {
                return "Happiness";
            }
        },
        EXP_SURPRISE {
            @Override
            public String toString() {
                return "Surprise";
            }
        },
        EXP_ANGER {
            @Override
            public String toString() {
                return "Anger";
            }
        },
        EXP_SADNESS {
            @Override
            public String toString() {
                return "Sadness";
            }
        }
    }

    /*
     * General purpose detection result
     */
    static class DetectionResult {
        int pos_x;
        int pos_y;
        int size;
        int conf;

        DetectionResult() {
            pos_x = -1;
            pos_y = -1;
            size = -1;
            conf = -1;
        }

        public String getString() {
            String strResult;
            strResult = "X:" + Integer.toString(pos_x) + " " +
                        "Y:" + Integer.toString(pos_y) + " " +
                        "Size:" + Integer.toString(size) + " " +
                        "Conf:" + Integer.toString(conf);
            return strResult;
        }
    }

    /*
     * Detection result for face
     */
    static class FaceResult extends DetectionResult {
        DirectionResult direction;
        AgeResult age;
        GenderResult gender;
        GazeResult gaze;
        BlinkResult blink;
        ExpressionResult expression;
        RecognitionResult recognition;

        FaceResult() {
            direction = null;
            age = null;
            gender = null;
            gaze = null;
            blink = null;
            expression = null;
            recognition = null;
        }

        public String getString() {
            String strResult;
            strResult = "X:" + Integer.toString(pos_x) + " " +
                        "Y:" + Integer.toString(pos_y) + " " +
                        "Size:" + Integer.toString(size) + " " +
                        "Conf:" + Integer.toString(conf) + "\n";
            if (direction != null) {
                strResult += "\t\t" + direction.getString() + "\n";
            }
            if (age != null) {
                strResult += "\t\t" + age.getString() + "\n";
            }
            if (gender != null) {
                strResult += "\t\t" + gender.getString() + "\n";
            }
            if (gaze != null) {
                strResult += "\t\t" + gaze.getString() + "\n";
            }
            if (blink != null) {
                strResult += "\t\t" + blink.getString() + "\n";
            }
            if (expression != null) {
                strResult += "\t\t" + expression.getString() + "\n";
            }
            if (recognition != null) {
                strResult += "\t\t" + recognition.getString() + "\n";
            }
            return strResult;
        }
    }

    /*
     * Result for Facial direction estimation
     */
    static class DirectionResult {
        int LR;
        int UD;
        int roll;
        int conf;

        DirectionResult(int lr, int ud, int r, int c) {
            LR = lr;
            UD = ud;
            roll = r;
            conf = c;
        }

        public String getString() {
            String strResult;
            strResult = "Direction     LR:" + Integer.toString(LR) + " " +
                        "UD:" + Integer.toString(UD) + " " +
                        "Roll:" + Integer.toString(roll) + " " +
                        "Conf:" + Integer.toString(conf);
            return strResult;
        }
    }

    /*
     * Result of Age estimation
     */
    static class AgeResult {
        int age;
        int conf;

        AgeResult(int a, int c) {
            age = a;
            conf = c;
        }

        public String getString() {
            String strResult;
            strResult = "Age           Age:" + Integer.toString(age) + " " +
                        "Conf:" + Integer.toString(conf);
            return strResult;
        }
    }

    /*
     * Result of Gender estimation
     */
    static class GenderResult {
        int gender;
        int conf;

        GenderResult(int g, int c) {
            gender = g;
            conf = c;
        }

        public String getString() {
            String strResult;
            GenderDic dic_key;
            switch (gender) {
                case GENDER_FEMALE:
                    dic_key = GenderDic.GENDER_FEMALE;
                    break;
                case GENDER_MALE:
                    dic_key = GenderDic.GENDER_MALE;
                    break;
                default:
                    dic_key = GenderDic.GENDER_UNKNOWN;
                    break;
            }
            strResult = "Gender        Gender:" + dic_key.toString() + " " +
                        "Conf:" + Integer.toString(conf);
            return strResult;
        }
    }

    /*
     * Result of Gaze estimation
     */
    static class GazeResult {
        int gazeLR;
        int gazeUD;

        GazeResult(int lr, int ud) {
            gazeLR = lr;
            gazeUD = ud;
        }

        public String getString() {
            String strResult;
            strResult = "Gaze          LR:" + Integer.toString(gazeLR) + " " +
                        "UD:" + Integer.toString(gazeUD);
            return strResult;
        }
    }

    /*
     * Result of Blink estimation
     */
    static class BlinkResult {
        int ratioR;
        int ratioL;

        BlinkResult(int l, int r) {
            ratioL = l;
            ratioR = r;
        }

        public String getString() {
            String strResult;
            strResult = "Blink         R:" + Integer.toString(ratioR) + " " +
                        "L:" + Integer.toString(ratioL);
            return strResult;
        }
    }

    /*
     * Result of Expression estimation
     */
    static class ExpressionResult {
        int neutral;
        int happiness;
        int surprise;
        int anger;
        int sadness;
        int neg_pos;

        ExpressionResult(int neu, int hap, int sur, int ang, int sad, int neg) {
            neutral = neu;
            happiness = hap;
            surprise = sur;
            anger = ang;
            sadness = sad;
            neg_pos = neg;
        }

        void getTop1(String[] exp_str, int[] max_score) {
            ExpDic max_idx;
            max_score[0] = neutral;
            max_idx = ExpDic.EXP_NEUTRAL;
            if (happiness > max_score[0]) {
                max_score[0] = happiness;
                max_idx = ExpDic.EXP_HAPPINESS;
            }
            if (surprise > max_score[0]) {
                max_score[0] = surprise;
                max_idx = ExpDic.EXP_SURPRISE;
            }
            if (anger > max_score[0]) {
                max_score[0] = anger;
                max_idx = ExpDic.EXP_ANGER;
            }
            if (sadness > max_score[0]) {
                max_score[0] = sadness;
                max_idx = ExpDic.EXP_SADNESS;
            }

            if ( max_score[0] == -128 ) {
                max_score[0] = 0;
                max_idx = ExpDic.EXP_UNKNOWN;
            }

            exp_str[0] = max_idx.toString();
        }

        public String getString() {
            String strResult;
            String[] top1_exp = new String[1];
            int[] top1_score = new int[1];
            getTop1(top1_exp, top1_score);
            strResult = "Expression    Exp:" + top1_exp[0] + " " +
                        "Score:" + Integer.toString(top1_score[0]) + " " +
                        "NegPos:" + Integer.toString(neg_pos);
            return strResult;
        }
    }

    /*
     * Result of Recognition
     */
    static class RecognitionResult {
        int uid;
        int score;

        RecognitionResult(int u, int s) {
            uid = u;
            score = s;
        }

        public String getString() {
            String strResult;
            strResult = "Recognition   Uid:" + Integer.toString(uid) + " " +
                        "Score:" + Integer.toString(score);
            return strResult;
        }
    }
}
