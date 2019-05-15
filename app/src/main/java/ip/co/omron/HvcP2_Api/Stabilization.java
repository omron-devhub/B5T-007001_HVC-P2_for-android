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

import static jp.co.omron.HvcP2_Api.HvcTrackingResultC.*;

/*
 * Wrapper class for STB library in C language.
 */
class Stabilization {
    static final int STB_EX_BODY = 0x01;
    static final int STB_EX_FACE = 0x04;
    static final int STB_EX_DIRECTION = 0x08;
    static final int STB_EX_AGE = 0x10;
    static final int STB_EX_GENDER = 0x20;
    //static final int STB_EX_GAZE = 0x40; // Not use now.
    //static final int STB_EX_BLINK = 0x80; // Not use now.
    //static final int STB_EX_EXPRESSION = 0x100; // Not use now.
    static final int STB_EX_RECOGNITION = 0x200;
    static final int STB_EX_FUNC_ALL = STB_EX_BODY | STB_EX_FACE | STB_EX_DIRECTION |
            STB_EX_AGE | STB_EX_GENDER | STB_EX_RECOGNITION;

    // STB error code definition
    static final int STB_RET_NORMAL = 0x00; // Normal end
    static final int STB_RET_ERR_INITIALIZE = -0x02; // Initializing error
    static final int STB_RET_ERR_INVALIDPARAM = -0x03; // Parameter error
    static final int STB_RET_ERR_NOHANDLE = -0x07; // Handle error
    static final int STB_RET_ERR_PROCESSCONDITION = -0x08; // Processing condition error

    long hSTB;

    private native long createHandle(int exec_func);

    static {
        // Load shared library
        System.loadLibrary("stb");
    }

    public Stabilization(int exec_func) {
        hSTB = createHandle(exec_func);
    }

    private native int setFrameResult(long hSTB, HvcResultC frame_res);
    private native int execute(long hSTB);
    private native int getFaces(long hSTB, int[] face_count, C_FACE[] faces_res);
    private native int getBodies(long hSTB, int[] body_count, C_BODY[] bodies_res);

    /*
     * Executes stabilization process.
     * In:
     *     -each frame result by frame_res (HvcResultC) input argument
     * Out:
     *     -face count
     *     -stabilized face result by faces_res (C_FACE) output argument
     *     -body count
     *     -stabilized body result by bodies_res (C_BODY) output argument
     *
     * Args:
     *     frame_res(HvcResultC):input one frame result for STBLib.
     *         Set the information of face central coordinate, size and
     *         direction to stabilize age, gender and face recognition.
     *     face_count(int[1]):face count
     *     faces_res(C_FACE[35]):output result stabilized face data.
     *     body_count(int[1]):body count
     *     bodies_res(C_BODY[35]):output result stabilized body data.
     *
     * Returns:
     *     int: return value of STB library
     */
    int execute(HvcResultC frame_res, int[] face_count, C_FACE[] faces_res, int[] body_count, C_BODY[] bodies_res) {
        int ret = setFrameResult(hSTB, frame_res);
        if (ret != STB_RET_NORMAL)
            return ret;

        ret = execute(hSTB);
        if (ret != STB_RET_NORMAL)
            return ret;

        ret = getFaces(hSTB, face_count, faces_res);
        if (ret != STB_RET_NORMAL)
            return ret;

        ret = getBodies(hSTB, body_count, bodies_res);
        if (ret != STB_RET_NORMAL)
            return ret;

        return STB_RET_NORMAL;
    }

    private native int getVersion(int[] _major_version, int[] _minor_version);

    int get_stb_version(int[] _major_version, int[] _minor_version) {
        return getVersion(_major_version, _minor_version);
    }

    private native int clearFrameResults(long hSTB);

    int clear_stb_frame_results() {
        return clearFrameResults(hSTB);
    }

    private native int setTrRetryCount(long hSTB, int max_retry_count);

    int set_stb_tr_retry_count(int max_retry_count) {
        return setTrRetryCount(hSTB, max_retry_count);
    }

    private native int getTrRetryCount(long hSTB, int[] _max_retry_count);

    int get_stb_tr_retry_count(int[] _max_retry_count) {
        return getTrRetryCount(hSTB, _max_retry_count);
    }

    private native int setTrSteadinessParam(long hSTB, int pos_steadiness_param, int size_steadiness_param);

    int set_stb_tr_steadiness_param(int pos_steadiness_param, int size_steadiness_param) {
        return setTrSteadinessParam(hSTB, pos_steadiness_param, size_steadiness_param);
    }

    private native int getTrSteadinessParam(long hSTB, int[] _pos_steadiness_param, int[] _size_steadiness_param);

    int get_stb_tr_steadiness_param(int[] _pos_steadiness_param, int[] _size_steadiness_param) {
        return getTrSteadinessParam(hSTB, _pos_steadiness_param, _size_steadiness_param);
    }

    private native int setPeThresholdUse(long hSTB, int threshold);

    int set_stb_pe_threshold_use(int threshold) {
        return setPeThresholdUse(hSTB, threshold);
    }

    private native int getPeThresholdUse(long hSTB, int[] _threshold);

    int get_stb_pe_threshold_use(int[] _threshold) {
        return getPeThresholdUse(hSTB, _threshold);
    }

    private native int setPeAngleUse(long hSTB, int min_UD_angle, int max_UD_angle, int min_LR_angle, int max_LR_angle);

    int set_stb_pe_angle_use(int min_UD_angle, int max_UD_angle, int min_LR_angle, int max_LR_angle) {
        return setPeAngleUse(hSTB, min_UD_angle, max_UD_angle, min_LR_angle, max_LR_angle);
    }

    private native int getPeAngleUse(long hSTB, int[] _min_UD_angle, int[] _max_UD_angle, int[] _min_LR_angle, int[] _max_LR_angle);

    int get_stb_pe_angle_use(int[] _min_UD_angle, int[] _max_UD_angle, int[] _min_LR_angle, int[] _max_LR_angle) {
        return getPeAngleUse(hSTB, _min_UD_angle, _max_UD_angle, _min_LR_angle, _max_LR_angle);
    }

    private native int setPeCompleteFrameCount(long hSTB, int frame_count);

    int set_stb_pe_complete_frame_count(int frame_count) {
        return setPeCompleteFrameCount(hSTB, frame_count);
    }

    private native int getPeCompleteFrameCount(long hSTB, int[] _frame_count);

    int get_stb_pe_complete_frame_count(int[] _frame_count) {
        return getPeCompleteFrameCount(hSTB, _frame_count);
    }

    private native int setFrThresholdUse(long hSTB, int threshold);

    int set_stb_fr_threshold_use(int threshold) {
        return setFrThresholdUse(hSTB, threshold);
    }

    private native int getFrThresholdUse(long hSTB, int[] _threshold);

    int get_stb_fr_threshold_use(int[] _threshold) {
        return getFrThresholdUse(hSTB, _threshold);
    }

    private native int setFrAngleUse(long hSTB, int min_UD_angle, int max_UD_angle, int min_LR_angle, int max_LR_angle);

    int set_stb_fr_angle_use(int min_UD_angle, int max_UD_angle, int min_LR_angle, int max_LR_angle) {
        return setFrAngleUse(hSTB, min_UD_angle, max_UD_angle, min_LR_angle, max_LR_angle);
    }

    private native int getFrAngleUse(long hSTB, int[] _min_UD_angle, int[] _max_UD_angle, int[] _min_LR_angle, int[] _max_LR_angle);

    int get_stb_fr_angle_use(int[] _min_UD_angle, int[] _max_UD_angle, int[] _min_LR_angle, int[] _max_LR_angle) {
        return getFrAngleUse(hSTB, _min_UD_angle, _max_UD_angle, _min_LR_angle, _max_LR_angle);
    }

    private native int setFrCompleteFrameCount(long hSTB, int frame_count);

    int set_stb_fr_complete_frame_count(int frame_count) {
        return setFrCompleteFrameCount(hSTB, frame_count);
    }

    private native int getFrCompleteFrameCount(long hSTB, int[] _frame_count);

    int get_stb_fr_complete_frame_count(int[] _frame_count) {
        return getFrCompleteFrameCount(hSTB, _frame_count);
    }

    private native int setFrMinRatio(long hSTB, int min_ratio);

    int set_stb_fr_min_ratio(int min_ratio) {
        return setFrMinRatio(hSTB, min_ratio);
    }

    private native int getFrMinRatio(long hSTB, int[] _min_ratio);

    int get_stb_fr_min_ratio(int[] _min_ratio) {
        return getFrMinRatio(hSTB, _min_ratio);
    }
}
