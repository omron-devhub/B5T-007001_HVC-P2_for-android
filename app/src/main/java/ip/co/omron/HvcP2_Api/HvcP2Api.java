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
import static jp.co.omron.HvcP2_Api.HvcTrackingResultC.*;

/*
 * This class provide python full API for HVC-P2(B5T-007001) with STB library.
 */
public class HvcP2Api {
    private boolean use_stb;
    private Stabilization stb;
    private HvcP2Wrapper hvcP2Wrapper;
    private int exec_func;

    /*
     * Constructor
     * 
     * Args:
     *     connector(SerialConnector):serial connector
     *     exec_function(int):functions flag to be executed
     *                      (e.g. p2def.EX_FACE | p2def.EX_AGE)
     *     use_stb(boolean):use STB library
     * 
     * Returns:
     *     void
    */
    public HvcP2Api(Connector connector, int exec_function, boolean use_stabilizer) {
        hvcP2Wrapper = new HvcP2Wrapper(connector);

        // Disable to use STB if using Hand detection only.
        if ( (use_stabilizer == P2Def.USE_STB_ON) && (exec_function == P2Def.EX_HAND) ) {
            use_stb = P2Def.USE_STB_OFF;
        } else {
            use_stb = use_stabilizer;
        }

        // Adds face flag if using facial estimation function
        if ( (exec_function & (P2Def.EX_DIRECTION |
                               P2Def.EX_AGE |
                               P2Def.EX_GENDER |
                               P2Def.EX_GAZE |
                               P2Def.EX_BLINK |
                               P2Def.EX_RECOGNITION |
                               P2Def.EX_EXPRESSION)) != 0 ) {
            exec_function |= P2Def.EX_FACE + P2Def.EX_DIRECTION;
        }
        exec_func = exec_function;

        if ( use_stb ) {
            stb = new Stabilization(exec_func);
        }
    }

    String getStatus() {
        return hvcP2Wrapper.getStatus();
    }

    public boolean isUsbStabilizer() {
        return use_stb;
    }
    public void useStabilizer(boolean use_stabilizer) {
        // Disable to use STB if using Hand detection only.
        if ( (use_stabilizer == P2Def.USE_STB_ON) && (exec_func == P2Def.EX_HAND) ) {
            use_stb = P2Def.USE_STB_OFF;
        } else {
            use_stb = use_stabilizer;
        }

        if ( !use_stb && (stb != null) ) {
            stb = null;
        }
        if ( use_stb && (stb == null) ) {
            stb = new Stabilization(exec_func);
        }
    }

    /*
     * Connects to HVC-P2 by COM port via USB or UART interface.
     * 
     * Args:
     *     timeout (int): timeout period(sec) for serial communication
     * 
     * Returns:
     *     boolean: status
     */
    public boolean connect(int timeout) {
        return hvcP2Wrapper.connect(timeout);
    }

    /*
     * Disconnects to HVC-P2.
     * 
     * Args:
     *     void
     * 
     * Returns:
     *     boolean: status
     */
    public boolean disconnect() {
        return hvcP2Wrapper.disconnect();
    }

    /*
     * Gets the device's model name, version and revision.
     * 
     * Args:
     *     hvc_type (String[1]): model name(12 characters) "B5T-007001  "
     *     major    (int[1]): major version number.
     *     minor    (int[1]): minor version number.
     *     release  (int[1]): release number.
     *     revision (int[1]): revision number.
     * 
     * Returns:
     *     int: response_code form B5T-007001.
     */
    public int get_version(String[] hvc_type, int[] major, int[] minor, int[] release, int[] revision) {
        return hvcP2Wrapper.get_version(hvc_type, major, minor, release, revision);
    }

    /*
     * Sets camera angle.
     * 
     * Args:
     *     camera_angle (int): the angle used when facing the camera
     *             HVC_CAM_ANGLE_0   (00h):   0 degree
     *             HVC_CAM_ANGLE_90  (01h):  90 degree
     *             HVC_CAM_ANGEL_180 (02h): 180 degree
     *             HVC_CAM_ANGEL_270 (03h): 270 degree
     * 
     * Returns:
     *     int: response_code form B5T-007001.
     */
    public int set_camera_angle(int camera_angle) {
        return hvcP2Wrapper.set_camera_angle(camera_angle);
    }

    /*
     * Gets camera angle.
     * 
     * Args:
     *     camera_angle (int[1]): the angle used when facing the camera
     * 
     * Returns:
     *     int: response_code form B5T-007001.
     */
    public int get_camera_angle(int[] camera_angle) {
        return hvcP2Wrapper.get_camera_angle(camera_angle);
    }

    /*
     * Executes functions specified in the constructor.
     *  e.g. Face detection, Age estimation etc.
     * 
     * Args:
     *     out_img_type (int): output image type
     *         OUT_IMG_TYPE_NONE  (00h): no image output
     *         OUT_IMG_TYPE_QVGA  (01h): 320x240 pixel resolution(QVGA)
     *         OUT_IMG_TYPE_QQVGA (02h): 160x120 pixel resolution(QQVGA)
     *     tracking_result (HVCTrackingResult): the tracking result is stored
     *     out_img (GrayscaleImage): output image
     *     stb_return (int[1]): return status of STB library
     * 
     * Returns:
     *     int: response_code form B5T-007001.
     */
    public int execute(int out_img_type, HvcTrackingResult tracking_result, GrayscaleImage out_img, int[] stb_return) {
        HvcResult frame_result = new HvcResult();
        int response_code = hvcP2Wrapper.execute(exec_func, out_img_type, frame_result, out_img);

        tracking_result.clear();
        if ( use_stb && (exec_func != P2Def.EX_NONE) ) {
            HvcResultC stb_in = new HvcResultC();
            frame_result.export_to_C_FRAME_RESULT(stb_in);

            int[] face_count = new int[1];
            int[] body_count = new int[1];
            C_FACE[] stb_out_f = new C_FACE[35];
            C_BODY[] stb_out_b = new C_BODY[35];
            for ( int i=0; i<35; i++ ) {
                stb_out_f[i] = new C_FACE();
                stb_out_b[i] = new C_BODY();
            }

            stb_return[0] = stb.execute(stb_in, face_count, stb_out_f, body_count, stb_out_b);
            if (stb_return[0] < 0) { // STB error
                return response_code;
            }

            tracking_result.faces.append_C_FACE_RES35(exec_func, face_count[0], stb_out_f);

            if ( (exec_func & P2Def.EX_DIRECTION) == P2Def.EX_DIRECTION )
                tracking_result.faces.append_direction_list(frame_result.faces);

            if ( (exec_func & P2Def.EX_GAZE) == P2Def.EX_GAZE )
                tracking_result.faces.append_gaze_list(frame_result.faces);

            if ( (exec_func & P2Def.EX_BLINK) == P2Def.EX_BLINK )
                tracking_result.faces.append_blink_list(frame_result.faces);

            if ( (exec_func & P2Def.EX_EXPRESSION) == P2Def.EX_EXPRESSION )
                tracking_result.faces.append_expression_list(frame_result.faces);

            tracking_result.bodies.append_BODY_RES35(exec_func, body_count[0], stb_out_b);
            tracking_result.hands.append_hand_list(frame_result.hands);
        } else {
            tracking_result.appned_FRAME_RESULT(frame_result);
            stb_return[0] = 0;
        }
        return response_code;
    }

    /*
     * Resets tracking.
     * Note:
     *     The tracking status will be cleared(i.e. TrackingID will be cleared),
     *     but other settings will not cleared.
     * 
     * Args:
     *     void
     * 
     * Returns:
     *     int: return status
     */
    public int reset_tracking() {
        return stb.clear_stb_frame_results();
    }

    /*
     * Sets the thresholds value for Human body detection, Hand detection,
     *    Face detection and/or Recongnition.
     * 
     * Args:
     *     body_thresh (int):Threshold value for Human body detection[1-1000]
     *     hand_thresh (int):Threshold value for Hand detection[1-1000]
     *     face_thresh (int):Threshold value for Face detection[1-1000]
     *     recognition_thresh (int):Threshold value for Recognition[0-1000]
     * 
     * Returns:
     *     int: response_code form B5T-007001.
     */
    public int set_threshold(int body_thresh, int hand_thresh, int face_thresh, int recognition_thresh) {
       return hvcP2Wrapper.set_threshold(body_thresh, hand_thresh, face_thresh, recognition_thresh);
    }

    /*
     * Gets the thresholds value for Human body detection, Hand detection,
     *    Face detection and/or Recongnition.
     * 
     * Args:
     *     body_thresh (int[1]):Threshold value for Human body detection
     *     hand_thresh (int[1]):Threshold value for Hand detection
     *     face_thresh (int[1]):Threshold value for Face detection
     *     recognition_thresh (int):Threshold value for Recognition
     * 
     * Returns:
     *     int: response_code form B5T-007001.
     */
    public int get_threshold(int[] body_thresh, int[] hand_thresh, int[] face_thresh, int[] recognition_thresh) {
        return hvcP2Wrapper.get_threshold(body_thresh, hand_thresh, face_thresh, recognition_thresh);
    }

    /*
     * Sets the detection size for Human body detection, Hand detection
     *    and/or Face detection
     * 
     * Args:
     *     min_body (int): Minimum detection size for Human body detection
     *     max_body (int): Maximum detection size for Human body detection
     *     min_hand (int): Minimum detection size for Hand detection
     *     max_hand (int): Maximum detection size for Hand detection
     *     min_face (int): Minimum detection size for Face detection
     *     max_face (int): Maximum detection size for Face detection
     * 
     * Returns:
     *     int: response_code form B5T-007001.
     */
    public int set_detection_size(int min_body, int max_body, int min_hand, int max_hand, int min_face, int max_face) {
        return hvcP2Wrapper.set_detection_size(min_body, max_body, min_hand, max_hand, min_face, max_face);
    }

    /*
     * Gets the detection size for Human body detection, Hand detection
     *    and/or Face detection
     * 
     * Args:
     *     min_body (int[1]): Minimum detection size for Human body detection
     *     max_body (int[1]): Maximum detection size for Human body detection
     *     min_hand (int[1]): Minimum detection size for Hand detection
     *     max_hand (int[1]): Maximum detection size for Hand detection
     *     min_face (int[1]): Minimum detection size for Face detection
     *     max_face (int[1]): Maximum detection size for Face detection
     * 
     * Returns:
     *     int: response_code form B5T-007001.
     */
    public int get_detection_size(int[] min_body, int[] max_body, int[] min_hand, int[] max_hand, int[] min_face, int[] max_face) {
        return hvcP2Wrapper.get_detection_size(min_body, max_body, min_hand, max_hand, min_face, max_face);
    }

    /*
     * Sets the face angle, i.e. the yaw angle range and the roll angle
     *    range for Face detection.
     * 
     * Args:
     *     yaw_angle (int): face direction yaw angle range.
     *             HVC_FACE_ANGLE_YAW_30 (00h): +/-30 degree (frontal face)
     *             HVC_FACE_ANGLE_YAW_60 (01h): +/-60 degree (half-profile face)
     *             HVC_FACE_ANGLE_YAW_90 (02h): +/-90 degree (profile face)
     *     roll_angle (int): face inclination roll angle range.
     *             HVC_FACE_ANGLE_ROLL_15 (00h): +/-15 degree
     *             HVC_FACE_ANGLE_ROLL_45 (01h): +/-45 degree
     * 
     * Returns:
     *     int: response_code form B5T-007001.
     */
    public int set_face_angle(int yaw_angle, int roll_angle) {
        return hvcP2Wrapper.set_face_angle(yaw_angle, roll_angle);
    }

    /*
     * Gets the face angle set for Face detection.
     * 
     * Args:
     *     yaw_angle (int[1]): face direction yaw angle range.
     *     roll_angle (int[1]): face inclination roll angle range.
     * 
     * Returns:
     *     int: response_code form B5T-007001.
     */
    public int get_face_angle(int[] yaw_angle, int[] roll_angle) {
        return hvcP2Wrapper.get_face_angle(yaw_angle, roll_angle);
    }

    /*
     * Sets the UART baudrate.
     * Note:
     *     The setting can be done when the USB is connected and will have
     *     no influence on the transmission speed as this is a command for UART
     *     connection.
     * 
     * Args:
     *     baudrate (int): UART baudrate in bps.
     *             (9600/38400/115200/230400/460800/921600)
     * 
     * Returns:
     *     int: response_code form B5T-007001.
     */
    public int set_uart_baudrate(int baudrate) {
        return hvcP2Wrapper.set_uart_baudrate(baudrate);
    }

    //==========================================================================
    // APIs for Album operation of Face recognition
    //==========================================================================

    /*
     * Registers data for Recognition and gets a normalized image.
     * 
     * Args:
     *     user_id (int): User ID [0-9]
     *     data_id (int): Data ID [0-99]
     *     out_register_image (GrayscaleImage): normalized face image
     * 
     * Returns:
     *     int: response_code form B5T-007001.
     */
    public int register_data(int user_id, int data_id, GrayscaleImage out_register_image) {
        return hvcP2Wrapper.register_data(user_id, data_id, out_register_image);
    }

    /*
     * Deletes a specified registered data. (Recognition)
     * 
     * Args:
     *     user_id (int): User ID [0-9]
     *     data_id (int): Data ID [0-99]
     * 
     * Returns:
     *     int: response_code form B5T-007001.
     */
    public int delete_data(int user_id, int data_id) {
        return hvcP2Wrapper.delete_data(user_id, data_id);
    }

    /*
     * Deletes a specified registerd user. (Recognition)
     * 
     * Args:
     *     user_id (int): User ID [0-9]
     * 
     * Returns:
     *     int: response_code form B5T-007001.
     */
    public int delete_user(int user_id) {
        return hvcP2Wrapper.delete_user(user_id);
    }

    /*
     * Deletes all the registerd data. (Recognition)
     * 
     * Args:
     *     void
     * 
     * Returns:
     *     int: response_code form B5T-007001.
     */
    public int delete_all_data() {
        return hvcP2Wrapper.delete_all_data();
    }

    /*
     * Gets the registration info of a specified user. (Recognition)
     * i.e. the presence or absence of registered data, for the specified user.
     * 
     * Args:
     *     user_id (int): User ID [0-9]
     * 
     * Returns:
     *     ArrayList<Integer>: data presence of registered data.
     *                         if error, return is null.
     */
    public ArrayList<Integer> get_user_data(int user_id) {
        return hvcP2Wrapper.get_user_data(user_id);
    }

    /*
     * Saves the album on the host side. (Recognition)
     * 
     * Args:
     *     void
     * 
     * Returns:
     *     byte[]: album
     *             if error, return is null.
     */
    public byte[] save_album() {
        return hvcP2Wrapper.save_album();
    }

    /*
     * Loads the album on the host side. (Recognition)
     * 
     * Args:
     *     album (byte[]): album
     * 
     * Returns:
     *     int: response_code form B5T-007001.
     */
    public int load_album(byte[] album) {
        return hvcP2Wrapper.load_album(album);
    }

    /*
     * Saves the album on the flash ROM.  (Recognition)
     * Note:
     *     The processing time will be longer if there is a lot of data.
     *     Album data already present on the flash ROM of the device will be
     *     overwritten.
     * 
     * Args:
     *     void
     * 
     * Returns:
     *     int: response_code form B5T-007001.
     */
    public int save_album_to_flash() {
       return hvcP2Wrapper.save_album_to_flash();
    }

    /*
     * Reformats the album save area on the flash ROM. (Recognition)
     * 
     * Args:
     *     void
     * 
     * Returns:
     *     int: response_code form B5T-007001.
     */
    public int reformat_flash() {
        return hvcP2Wrapper.reformat_flash();
    }

    //==========================================================================
    // APIs for STB library
    //==========================================================================

    /*
     * Gets the version number of STB library.
     * 
     * Args:
     *     major (int[1]): major version number of STB library.
     *     minor (int[1]): minor version number of STB library.
     * 
     * Returns:
     *     int: return value of STB library
     */
    public int get_stb_version(int[] major, int[] minor) {
        if ( stb == null ) {
            return -1;
        }
        return stb.get_stb_version(major, minor);
    }

    /*
     * Sets maximum tracking retry count.
     * Set the number of maximum retry when not finding a face/human body while
     * tracking. Terminates tracking as lost object when keeps failing for this
     * maximum retry count.
     * 
     * Args:
     *     max_retry_count (int): maximum tracking retry count. [0-300]
     * 
     * Returns:
     *     int: return value of STB library
     */
    public int set_stb_tr_retry_count(int max_retry_count) {
        return stb.set_stb_tr_retry_count(max_retry_count);
    }

    /*
     * Gets maximum retry count.
     * 
     * Args:
     *     max_retry_count (int[1]): maximum tracking retry count.
     * 
     * Returns:
     *     int: return value of STB library
     */
    public int get_stb_tr_retry_count(int[] max_retry_count) {
        return stb.get_stb_tr_retry_count(max_retry_count);
    }

    /*
     * Sets steadiness parameter of position and size.
     * 
     * -- pos_steadiness_param
     * For example, outputs the previous position coordinate data if the
     * shifting measure is within 30%, existing position coordinate data if it
     * has shift more than 30% when the rectangle position steadiness
     * parameter has set as initial value of 30.
     *
     * -- size_steadiness_param
     * For example, outputs the previous detecting size data if the changing
     * measure is within 30%, existing size data if it has changed more than
     * 30% when the rectangle size steadiness parameter has set as initial
     * value of 30.
     *
     * Args:
     *     pos_steadiness_param (int): rectangle position steadiness parameter
     *                                 [0-100]
     *     size_steadiness_param (int): rectangle size steadiness parameter
     *                                 [0-100]
     * Returns:
     *     int: return value of STB library
     */
    public int set_stb_tr_steadiness_param(int pos_steadiness_param, int size_steadiness_param) {
        return stb.set_stb_tr_steadiness_param(pos_steadiness_param, size_steadiness_param);
    }

    /*
     * Gets steadiness parameter of position and size.
     * 
     * Args:
     *     pos_steadiness_param (int[1]): rectangle position steadiness parameter
     *     size_steadiness_param (int[1]): rectangle size steadiness parameter
     * 
     * Returns:
     *     int: return value of STB library
     */
    public int get_stb_tr_steadiness_param(int[] pos_steadiness_param, int[] size_steadiness_param) {
        return stb.get_stb_tr_steadiness_param(pos_steadiness_param, size_steadiness_param);
    }

    /*
     * Sets estimation result stabilizing threshold value.
     * 
     * Sets the stabilizing threshold value of Face direction confidence.
     * Eliminates face data with lower confidence than the value set at this
     * function for accuracy improvement of result stabilizing.
     * For example, the previous data confidence with below 500 will not be
     * applied for stabilizing when the face direction confidence threshold
     * value has set as 500.
     * 
     * * This is for the three functions of age, gender and face direction
     *   estimation functions.
     * 
     * Args:
     *     threshold (int): face direction confidence threshold value.[0-1000]
     * 
     * Returns:
     *     int: return value of STB library
     */
    public int set_stb_pe_threshold_use(int threshold) {
        return stb.set_stb_pe_threshold_use(threshold);
    }

    /*
     * Gets estimation result stabilizing threshold value.
     * 
     * Args:
     *     threshold (int[1]): face direction confidence threshold value
     * 
     * Returns:
     *     int: return value of STB library
     */
    public int get_stb_pe_threshold_use(int[] threshold) {
        return stb.get_stb_pe_threshold_use(threshold);
    }

    /*
     * Sets estimation result stabilizing angle
     * 
     * Sets angle threshold value of Face direction.
     * 
     * Eliminates face data with out of the set angle at this function for
     * accuracy improvement of result stabilizing.
     * For example, the previous data with up-down angle of below -16 degree
     * and over 21 degree will not be applied for stabilizing when the up-down
     * 
     * * This is for the three functions of age, gender and face direction
     *   estimation functions.
     * 
     * Args:
     *     min_UD_angle (int): minimum up-down angle of the face [-90 to 90]
     *     max_UD_angle (int): maximum up-down angle of the face [-90 to 90]
     *     min_LR_angle (int): minimum left-right angle of the face [-90 to 90]
     *     max_LR_angle (int): maximum left-right angle of the face [-90 to 90]
     * 
     *     min_UD_angle ≦ max_UD_angle
     *     min_LR_angle ≦ max_LR_angle
     * 
     * Returns:
     *     int: return value of STB library
     */
    public int set_stb_pe_angle_use(int min_UD_angle, int max_UD_angle, int min_LR_angle, int max_LR_angle) {
        return stb.set_stb_pe_angle_use(min_UD_angle, max_UD_angle, min_LR_angle, max_LR_angle);
    }

    /*
     * Gets estimation result stabilizing angle
     * 
     * Args:
     *     min_UD_angle (int[1]): minimum up-down angle of the face
     *     max_UD_angle (int[1]): maximum up-down angle of the face
     *     min_LR_angle (int[1]): minimum left-right angle of the face
     *     max_LR_angle (int[1]): maximum left-right angle of the face
     * 
     * Returns:
     *     int: return value of STB library
     */
    public int get_stb_pe_angle_use(int[] min_UD_angle, int[] max_UD_angle, int[] min_LR_angle, int[] max_LR_angle) {
        return stb.get_stb_pe_angle_use(min_UD_angle, max_UD_angle, min_LR_angle, max_LR_angle);
    }

    /*
     * Sets age/gender estimation complete frame count
     * 
     * Sets the number of previous frames applying to fix stabilization.
     * The data used for stabilizing process (=averaging) is only the one
     * fulfilled the set_stb_pe_threshold_use() and set_stb_pe_angle_use()
     * condition.
     * Stabilizing process will be completed with data more than the number of
     * frames set at this function and it won't be done with less data.
     * 
     * * This is for the two functions of age and gender estimation.
     * 
     * Args:
     *     frame_count (int): the number of previous frames applying to fix
     *                        the result [1-20]
     * 
     * Returns:
     *     int: return value of STB library
     */
    public int set_stb_pe_complete_frame_count(int frame_count) {
        return stb.set_stb_pe_complete_frame_count(frame_count);
    }

    /*
     * Gets age/gender estimation complete frame count.
     * 
     * Args:
     *     frame_count (int[1]): the number of previous frames applying to fix
     *                           the result
     * 
     * Returns:
     *     int: return value of STB library
     */
    public int get_stb_pe_complete_frame_count(int[] frame_count) {
        return stb.get_stb_pe_complete_frame_count(frame_count);
    }

    /*
     * Sets recognition stabilizing threshold value
     * 
     * Sets stabilizing threshold value of Face direction confidence to improve
     * recognition stabilization.
     * Eliminates face data with lower confidence than the value set at this
     * function.
     * For example, the previous data confidence with below 500 will not be
     * applied for stabilizing when the face direction confidence threshold
     * value has set as 500.
     * 
     * Args:
     *     threshold (int): face direction confidence threshold value [0-1000]
     * 
     * Returns:
     *     int: return value of STB library
     */
    public int set_stb_fr_threshold_use(int threshold) {
        return stb.set_stb_fr_threshold_use(threshold);
    }

    /*
     * Gets recognition stabilizing threshold value
     * 
     * Args:
     *     threshold (int[1]): face direction confidence threshold value
     * 
     * Returns:
     *     int: return value of STB library
     */
    public int get_stb_fr_threshold_use(int[] threshold) {
        return stb.get_stb_fr_threshold_use(threshold);
    }

    /*
     * Sets recognition stabilizing angle
     * 
     * Sets angle threshold value of Face direction for accuracy improvement of
     * recognition stabilizing.
     * Eliminates face data with out of the set angle at this function.
     * For example, the previous data with up-down angle of below -16degree and
     * over 21 degree will not be applied for stabilizing when the up-down
     * angle threshold value of Face direction has set as 15 for minimum and
     * 21 for maximum.
     * 
     * Args:
     *     min_UD_angle (int): minimum up-down angle of the face [-90 to 90]
     *     max_UD_angle (int): maximum up-down angle of the face [-90 to 90]
     *     min_LR_angle (int): minimum left-right angle of the face [-90 to 90]
     *     max_LR_angle (int): maximum left-right angle of the face [-90 to 90]
     * 
     *     min_UD_angle ≦ max_UD_angle
     *     min_LR_angle ≦ max_LR_angle
     * 
     * Returns:
     *     int: return value of STB library
     */
    public int set_stb_fr_angle_use(int min_UD_angle, int max_UD_angle, int min_LR_angle, int max_LR_angle) {
        return stb.set_stb_fr_angle_use(min_UD_angle, max_UD_angle, min_LR_angle, max_LR_angle);
    }

    /*
     * Gets recognition stabilizing angle
     * 
     * Args:
     *     min_UD_angle (int[1]): minimum up-down angle of the face
     *     max_UD_angle (int[1]): maximum up-down angle of the face
     *     min_LR_angle (int[1]): minimum left-right angle of the face
     *     max_LR_angle (int[1]): maximum left-right angle of the face
     * 
     * Returns:
     *     int: return value of STB library
     */
    public int get_stb_fr_angle_use(int[] min_UD_angle, int[] max_UD_angle, int[] min_LR_angle, int[] max_LR_angle) {
        return stb.get_stb_fr_angle_use(min_UD_angle, max_UD_angle, min_LR_angle, max_LR_angle);
    }

    /*
     * Sets recognition stabilizing complete frame count
     * 
     * Sets the number of previous frames applying to fix the recognition
     * stabilizing.
     * The data used for stabilizing process (=averaging) is only the one
     * fulfilled the STB_SetFrThresholdUse and STB_SetFrAngleUse condition.
     * Stabilizing process will be completed with a recognition ID fulfilled
     * seizing ratio in result fixing frames and will not be done without one.
     * 
     * * Refer set_stb_fr_min_ratio function for account ratio function
     * 
     * Args:
     *     frame_count (int): the number of previous frames applying to fix
     *                        the result. [0-20]
     * 
     * Returns:
     *     int: return value of STB library
     */
    public int set_stb_fr_complete_frame_count(int frame_count) {
        return stb.set_stb_fr_complete_frame_count(frame_count);
    }

    /*
     * Gets recognition stabilizing complete frame count
     * 
     * Args:
     *     frame_count (int[1]): the number of previous frames applying to fix
     *                           the result
     * 
     * Returns:
     *     int: return value of STB library
     */
    public int get_stb_fr_complete_frame_count(int[] frame_count) {
        return stb.get_stb_fr_complete_frame_count(frame_count);
    }

    /*
     * Sets recognition minimum account ratio
     * 
     * Sets minimum account ratio in complete frame count for accuracy
     * improvement of recognition stabilizing.
     * For example, when there are 7 frames of extracted usable data in
     * referred previous 20 frames, STB_SetFrCompleteFrameCount function has
     * set "10"for the complete frame count and "60" for the recognition
     * minimum account ratio.
     * Creates frequency distribution of recognition result in the set 10 frames.
     *     Recognized as "Mr. A"(1 frame)
     *     Recognized as "Mr. B"(4 frames)
     *     Recognized as "Mr. C"(4 frames)
     * In this case, the most account ratio “Mr. B” will be output as
     * stabilized result.
     * However, this recognition status will be output as "STB_STAUS_CALCULATING"
     * since the account ratio is about57%(= 4 frames/10 frames) ,
     * (Mr. B seizing ratio=) 57% < recognition account ratio (=60%).
     * 
     * Args:
     *     min_ratio (int): recognition minimum account ratio [0-100]
     * 
     * Returns:
     *     int: return value of STB library
     */
    public int set_stb_fr_min_ratio(int min_ratio) {
        return stb.set_stb_fr_min_ratio(min_ratio);
    }

    /*
     * Gets recognition minimum account ratio
     * 
     * Args:
     *     min_ratio (int[1]): recognition minimum account ratio
     * 
     * Returns:
     *     int: return value of STB library
     */
    public int get_stb_fr_min_ratio(int[] min_ratio) {
        return stb.get_stb_fr_min_ratio(min_ratio);
    }
}
