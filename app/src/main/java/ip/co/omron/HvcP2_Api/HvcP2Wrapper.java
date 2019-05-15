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

import java.io.IOException;
import java.util.ArrayList;

import static jp.co.omron.HvcP2_Api.P2Def.*;

/*
 * HVC-P2(B5T-007001) command wrapper class.
        This class provides all commands of HVC-P2.
 */
class HvcP2Wrapper {
    static final int RESPONSE_HEADER_SIZE = 6;
    static final int SYNC_CODE = 0xFE;

    // UART baudrate definition.  : for set_uart_baudrate()
    static final int HVC_UART_BAUD_9600 = 0x00;     //   9600 baud
    static final int HVC_UART_BAUD_38400 = 0x01;    //  38400 baud
    static final int HVC_UART_BAUD_115200 = 0x02;   // 115200 baud
    static final int HVC_UART_BAUD_230400 = 0x03;   // 230400 baud
    static final int HVC_UART_BAUD_460800 = 0x04;   // 460800 baud
    static final int HVC_UART_BAUD_921600 = 0x05;   // 921600 baud

    // HVC command header fixed part definition
    static final byte[] HVC_CMD_HDR_GETVERSION = {(byte)0xFE, 0x00, 0x00, 0x00};
    static final byte[] HVC_CMD_HDR_SET_CAMERA_ANGLE = {(byte)0xFE, 0x01, 0x01, 0x00, 0x00};
    static final byte[] HVC_CMD_HDR_GET_CAMERA_ANGLE = {(byte)0xFE, 0x02, 0x00, 0x00};
    static final byte[] HVC_CMD_HDR_EXECUTE = {(byte)0xFE, 0x04, 0x03, 0x00, 0x00, 0x00, 0x00};
    static final byte[] HVC_CMD_HDR_SET_THRESHOLD = {(byte)0xFE, 0x05, 0x08, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    static final byte[] HVC_CMD_HDR_GET_THRESHOLD = {(byte)0xFE, 0x06, 0x00, 0x00};
    static final byte[] HVC_CMD_HDR_SET_DETECTION_SIZE = {(byte)0xFE, 0x07, 0x0C,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    static final byte[] HVC_CMD_HDR_GET_DETECTION_SIZE = {(byte)0xFE, 0x08, 0x00, 0x00};
    static final byte[] HVC_CMD_HDR_SET_FACE_ANGLE = {(byte)0xFE, 0x09, 0x02, 0x00, 0x00, 0x00};
    static final byte[] HVC_CMD_HDR_GET_FACE_ANGLE = {(byte)0xFE, 0x0A, 0x00, 0x00};
    static final byte[] HVC_CMD_HDR_SET_UART_BAUDRATE = {(byte)0xFE, 0x0E, 0x01, 0x00, 0x00};
    static final byte[] HVC_CMD_HDR_REGISTER_DATA = {(byte)0xFE, 0x10, 0x03, 0x00, 0x00, 0x00, 0x00};
    static final byte[] HVC_CMD_HDR_DELETE_DATA = {(byte)0xFE, 0x11, 0x03, 0x00, 0x00, 0x00, 0x00};
    static final byte[] HVC_CMD_HDR_DELETE_USER = {(byte)0xFE, 0x12, 0x02, 0x00, 0x00, 0x00};
    static final byte[] HVC_CMD_HDR_DELETE_ALL_DATA = {(byte)0xFE, 0x13, 0x00, 0x00};
    static final byte[] HVC_CMD_HDR_USER_DATA = {(byte)0xFE, 0x15, 0x02, 0x00, 0x00, 0x00};
    static final byte[] HVC_CMD_HDR_SAVE_ALBUM = {(byte)0xFE, 0x20, 0x00, 0x00};
    static final byte[] HVC_CMD_HDR_LOAD_ALBUM = {(byte)0xFE, 0x21, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00};
    static final byte[] HVC_CMD_HDR_SAVE_ALBUM_ON_FLASH = {(byte)0xFE, 0x22, 0x00, 0x00};
    static final byte[] HVC_CMD_HDR_REFORMAT_FLASH = {(byte)0xFE, 0x30, 0x00, 0x00};

    private Connector hvcConnector;
    private String strStatus = null;
    private byte[] rcvBuffer = null;

    public HvcP2Wrapper(Connector connector) {
        hvcConnector = connector;
    }

    String getStatus() {
        return strStatus;
    }

    /*
     * Connects to HVC-P2 by COM port via USB or UART interface.
     */
    boolean connect(int timeout) {
        return hvcConnector.connect(timeout);
    }

    /*
     * Disconnects to HVC-P2.
     */
    boolean disconnect() {
        return hvcConnector.disconnect();
    }

    /*
     * Gets the device's model name, version and revision.
     */
    int get_version(String[] hvc_type, int[] major, int[] minor, int[] release, int[] revision) {
        byte[] cmd = HVC_CMD_HDR_GETVERSION;
        int response_code = sendCommand(cmd);
        if ( (response_code == 0x00) && (rcvBuffer != null) ) { // Success
            hvc_type[0] = null;
            try {
                hvc_type[0] = new String(rcvBuffer, 0, 12, "UTF-8");
            } catch (IOException e) {
            }
            major[0] = (rcvBuffer[12] & 0xff);
            minor[0] = (rcvBuffer[13] & 0xff);
            release[0] = (rcvBuffer[14] & 0xff);
            revision[0] = ((rcvBuffer[15] & 0xff) + (rcvBuffer[16] << 8) + (rcvBuffer[17] << 16) + (rcvBuffer[18] << 24));
        } else { // error
            hvc_type[0] = null;
            major[0] = 0;
            minor[0] = 0;
            release[0] = 0;
            revision[0] = 0;
        }
        return response_code;
    }

    /*
     * Sets camera angle.
     */
    int set_camera_angle(int camera_angle) {
        byte[] cmd = HVC_CMD_HDR_SET_CAMERA_ANGLE;
        cmd[4] = (byte)(camera_angle & 0xff);
        int response_code = sendCommand(cmd);
        return response_code;
    }

    /*
     * Gets camera angle.
     */
    int get_camera_angle(int[] camera_angle) {
        byte[] cmd = HVC_CMD_HDR_GET_CAMERA_ANGLE;
        int response_code = sendCommand(cmd);
        if ( (response_code == 0x00) && (rcvBuffer != null) ) { // Success
            camera_angle[0] = (rcvBuffer[0] & 0xff);
        } else { // error
            camera_angle[0] = 0;
        }
        return response_code;
    }

    /*
     * Executes specified functions. e.g. Face detection, Age estimation, etc
     */
    int execute(int exec_func, int out_img_type, HvcResult frame_result, GrayscaleImage img) {
        byte[] cmd = HVC_CMD_HDR_EXECUTE;

        // Adds face flag if using facial estimation function
        if ( (exec_func & (EX_DIRECTION|EX_AGE|EX_GENDER|EX_GAZE|EX_BLINK|EX_EXPRESSION)) != 0 ) {
            exec_func |= EX_FACE + EX_DIRECTION;
        }

        cmd[4] = (byte)(exec_func & 0xff);
        cmd[5] = (byte)((exec_func >> 8) & 0xff);
        cmd[6] = (byte)(out_img_type & 0xff);
        int response_code = sendCommand(cmd);
        if ( (response_code == 0x00) && (rcvBuffer != null) ) { // Success
            int rc = frame_result.read_from_buffer(exec_func, rcvBuffer);
            if ( out_img_type != OUT_IMG_TYPE_NONE ) {
                int width = ((rcvBuffer[rc] & 0xff) + (rcvBuffer[rc+1] << 8));
                int height = ((rcvBuffer[rc+2] & 0xff) + (rcvBuffer[rc+3] << 8));
                img.width = width;
                img.height = height;
                img.data = new byte[rcvBuffer.length - (rc+4)];
                System.arraycopy(rcvBuffer, rc+4, img.data, 0, rcvBuffer.length - (rc+4));
            }
        }
        return response_code;
    }

    /*
     * Sets the thresholds value for Human body detection, Hand detection,
     * Face detection and/or Recongnition.
     */
    int set_threshold(int body_thresh, int hand_thresh, int face_thresh, int recognition_thresh) {
        byte[] cmd = HVC_CMD_HDR_SET_THRESHOLD;
        cmd[4] = (byte)(body_thresh & 0xff);
        cmd[5] = (byte)((body_thresh >> 8) & 0xff);
        cmd[6] = (byte)(hand_thresh & 0xff);
        cmd[7] = (byte)((hand_thresh >> 8) & 0xff);
        cmd[8] = (byte)(face_thresh & 0xff);
        cmd[9] = (byte)((face_thresh >> 8) & 0xff);
        cmd[10] = (byte)(recognition_thresh & 0xff);
        cmd[11] = (byte)((recognition_thresh >> 8) & 0xff);
        int response_code = sendCommand(cmd);
        return response_code;
    }

    /*
     * Gets the thresholds value for Human body detection, Hand detection,
     *  Face detection and/or Recongnition.
     */
    int get_threshold(int[] body_thresh, int[] hand_thresh, int[] face_thresh, int[] recognition_thresh) {
        byte[] cmd = HVC_CMD_HDR_GET_THRESHOLD;
        int response_code = sendCommand(cmd);
        if ( (response_code == 0x00) && (rcvBuffer != null) ) { // Success
            body_thresh[0] = ((rcvBuffer[0] & 0xff) + (rcvBuffer[1] << 8));
            hand_thresh[0] = ((rcvBuffer[2] & 0xff) + (rcvBuffer[3] << 8));
            face_thresh[0] = ((rcvBuffer[4] & 0xff) + (rcvBuffer[5] << 8));
            recognition_thresh[0] = ((rcvBuffer[6] & 0xff) + (rcvBuffer[7] << 8));
        } else { // error
            body_thresh[0] = 0;
            hand_thresh[0] = 0;
            face_thresh[0] = 0;
            recognition_thresh[0] = 0;
        }
        return response_code;
    }


    /*
     * Sets the detection size for Human body detection, Hand detection
     * and/or Face detection
     */
    int set_detection_size(int min_body, int max_body, int min_hand, int max_hand, int min_face, int max_face) {
        byte[] cmd = HVC_CMD_HDR_SET_DETECTION_SIZE;
        cmd[4] = (byte)(min_body & 0xff);
        cmd[5] = (byte)((min_body >> 8) & 0xff);
        cmd[6] = (byte)(max_body & 0xff);
        cmd[7] = (byte)((max_body >> 8) & 0xff);
        cmd[8] = (byte)(min_hand & 0xff);
        cmd[9] = (byte)((min_hand >> 8) & 0xff);
        cmd[10] = (byte)(max_hand & 0xff);
        cmd[11] = (byte)((max_hand >> 8) & 0xff);
        cmd[12] = (byte)(min_face & 0xff);
        cmd[13] = (byte)((min_face >> 8) & 0xff);
        cmd[14] = (byte)(max_face & 0xff);
        cmd[15] = (byte)((max_face >> 8) & 0xff);
        int response_code = sendCommand(cmd);
        return response_code;
    }

    /*
     * Gets the detection size for Human body detection, Hand detection
     * and/or Face detection
     */
    int get_detection_size(int[] min_body, int[] max_body, int[] min_hand, int[] max_hand, int[] min_face, int[] max_face) {
        byte[] cmd = HVC_CMD_HDR_GET_DETECTION_SIZE;
        int response_code = sendCommand(cmd);
        if ( (response_code == 0x00) && (rcvBuffer != null) ) { // Success
            min_body[0] = ((rcvBuffer[0] & 0xff) + (rcvBuffer[1] << 8));
            max_body[0] = ((rcvBuffer[2] & 0xff) + (rcvBuffer[3] << 8));
            min_hand[0] = ((rcvBuffer[4] & 0xff) + (rcvBuffer[5] << 8));
            max_hand[0] = ((rcvBuffer[6] & 0xff) + (rcvBuffer[7] << 8));
            min_face[0] = ((rcvBuffer[8] & 0xff) + (rcvBuffer[9] << 8));
            max_face[0] = ((rcvBuffer[10] & 0xff) + (rcvBuffer[11] << 8));
        } else { // error
            min_body[0] = 0;
            max_body[0] = 0;
            min_hand[0] = 0;
            max_hand[0] = 0;
            min_face[0] = 0;
            max_face[0] = 0;
        }
        return response_code;
    }

    /*
     * Sets the face angle, i.e. the yaw angle range and the roll angle
     * range for Face detection.
     */
    int set_face_angle(int yaw_angle, int roll_angle) {
        byte[] cmd = HVC_CMD_HDR_SET_FACE_ANGLE;
        cmd[4] = (byte)(yaw_angle & 0xff);
        cmd[5] = (byte)(roll_angle & 0xff);
        int response_code = sendCommand(cmd);
        return response_code;
    }

    /*
     * Gets the face angle range for Face detection.
     */
    int get_face_angle(int[] yaw_angle, int[] roll_angle) {
        byte[] cmd = HVC_CMD_HDR_GET_FACE_ANGLE;
        int response_code = sendCommand(cmd);
        if ( (response_code == 0x00) && (rcvBuffer != null) ) { // Success
            yaw_angle[0] = (rcvBuffer[0] & 0xff);
            roll_angle[0] = (rcvBuffer[1] & 0xff);
        } else { // error
            yaw_angle[0] = 0;
            roll_angle[0] = 0;
        }
        return response_code;
    }

    /*
     * Sets the UART baudrate.
     */
    int set_uart_baudrate(int baudrate) {
        int baud_index = 0;
        for ( int i=0; i<AVAILABLE_BAUD.length; i++ ) {
            if ( baudrate == AVAILABLE_BAUD[i] ) {
                baud_index = i;
                break;
            }

            if ( i == AVAILABLE_BAUD.length-1 ) {
                // raise ValueError ("Invalid baudrate:{0!r}".format(baudrate));
                return RESPONSE_CODE_INVALID_CMD;
            }
        }

        byte[] cmd = HVC_CMD_HDR_SET_UART_BAUDRATE;
        cmd[4] = (byte)(baud_index & 0xff);
        int response_code = sendCommand(cmd);
        return response_code;
    }

    /*
     * Registers data for Recognition and gets a normalized image.
     */
    int register_data(int user_id, int data_id, GrayscaleImage img) {
        byte[] cmd = HVC_CMD_HDR_REGISTER_DATA;
        cmd[4] = (byte)(user_id & 0xff);
        cmd[5] = (byte)((user_id >> 8) & 0xff);
        cmd[6] = (byte)(data_id & 0xff);
        int response_code = sendCommand(cmd);
        if ( (response_code == 0x00) && (rcvBuffer != null) ) { // Success
            int width = ((rcvBuffer[0] & 0xff) + (rcvBuffer[1] << 8));
            int height = ((rcvBuffer[2] & 0xff) + (rcvBuffer[3] << 8));
            img.width = width;
            img.height = height;
            img.data = new byte[rcvBuffer.length - 4];
            System.arraycopy(rcvBuffer, 4, img.data, 0, rcvBuffer.length - 4);
        }
        return response_code;
    }

    /*
     * Deletes a specified registered data.
     */
    int delete_data(int user_id, int data_id) {
        byte[] cmd = HVC_CMD_HDR_DELETE_DATA;
        cmd[4] = (byte)(user_id & 0xff);
        cmd[5] = (byte)((user_id >> 8) & 0xff);
        cmd[6] = (byte)(data_id & 0xff);
        int response_code = sendCommand(cmd);
        return response_code;
    }

    /*
     * Deletes a specified registered user. 
     */
    int delete_user(int user_id) {
        byte[] cmd = HVC_CMD_HDR_DELETE_USER;
        cmd[4] = (byte)(user_id & 0xff);
        cmd[5] = (byte)((user_id >> 8) & 0xff);
        int response_code = sendCommand(cmd);
        return response_code;
    }

    /*
     * Deletes all the registered data.
     */
    int delete_all_data() {
        byte[] cmd = HVC_CMD_HDR_DELETE_ALL_DATA;
        int response_code = sendCommand(cmd);
        return response_code;
    }

    /*
     * Gets the registration info of a specified user.
     */
    ArrayList<Integer> get_user_data(int user_id) {
        ArrayList<Integer> data_list = new ArrayList<Integer>();

        byte[] cmd = HVC_CMD_HDR_USER_DATA;
        cmd[4] = (byte)(user_id & 0xff);
        cmd[5] = (byte)((user_id >> 8) & 0xff);
        int response_code = sendCommand(cmd);
        if ( (response_code == 0x00) && (rcvBuffer != null) ) { // Success
            int registration_info = ((rcvBuffer[0] & 0xff) + (rcvBuffer[1] << 8));
            for ( int i=0; i<10; i++ ) {
                data_list.add((registration_info & (1 << i)) >> i);
            }
        } else { // error
            data_list = null;
        }
        return data_list;
    }

    /*
     * Saves the album on the host side.
     */
    byte[] save_album() {
        byte[] cmd = HVC_CMD_HDR_SAVE_ALBUM;
        int response_code = sendCommand(cmd);
        if ( (response_code == 0x00) && (rcvBuffer != null) ) { // Success
            return rcvBuffer;
        }
        return null;
    }

    /*
     * Loads the album from the host side to the device.
     */
    int load_album(byte[] album) {
        byte[] cmd = new byte[HVC_CMD_HDR_LOAD_ALBUM.length + album.length];
        System.arraycopy(HVC_CMD_HDR_LOAD_ALBUM, 0, cmd, 0, HVC_CMD_HDR_LOAD_ALBUM.length);
        cmd[4] = (byte)(album.length & 0xff);
        cmd[5] = (byte)((album.length >> 8) & 0xff);
        cmd[6] = (byte)((album.length >> 16) & 0xff);
        cmd[7] = (byte)((album.length >> 24) & 0xff);
        System.arraycopy(album, 0, cmd, HVC_CMD_HDR_LOAD_ALBUM.length, album.length);
        int response_code = sendCommand(cmd);
        return response_code;
    }

    /*
     * Saves the album on the flash ROM.
     */
    int save_album_to_flash() {
        byte[] cmd = HVC_CMD_HDR_SAVE_ALBUM_ON_FLASH;
        int response_code = sendCommand(cmd);
        return response_code;
    }

    /*
     * Reformats the album save area on the flash ROM.
     */
    int reformat_flash() {
        byte[] cmd = HVC_CMD_HDR_REFORMAT_FLASH;
        int response_code = sendCommand(cmd);
        return response_code;
    }

    private int receiveHeader(int[] data_len) {
        byte[] buf = hvcConnector.receive_data(RESPONSE_HEADER_SIZE);
        strStatus += hvcConnector.getStatus();
        if ( (buf == null) || (buf.length != RESPONSE_HEADER_SIZE) ) {
            strStatus += "Response header size is not enough.\n";
            return RESPONSE_HEADER_SIZE_ERR;
        }

        int sync_code = (buf[0] & 0xff);
        if ( sync_code != SYNC_CODE ) {
            strStatus += "Invalid Sync code.\n";
            return RESPONSE_HEADER_SYNC_ERR;
        }

        int response_code = (buf[1] & 0xff);
        data_len[0] = ((buf[2] & 0xff) + (buf[3] << 8) + (buf[4] << 16) + (buf[5] << 24));
        // strStatus += "Response header : response_code = " + Integer.toString(response_code) + "\n";
        return response_code;
    }

    private byte[] receiveData(int data_len) {
        byte[] buf = hvcConnector.receive_data(data_len);
        strStatus += hvcConnector.getStatus();
        if ( buf.length < data_len ) {
            strStatus += "Response data size is not enough.\n";
            return null;
        }
        // strStatus += "Response data : buf.length = " + Integer.toString(buf.length) + "\n";
        return buf;
    }

    private int sendCommand(byte[] data) {
        int[] data_len = new int[1];
        hvcConnector.clear_receive_buffer();
        strStatus = hvcConnector.getStatus();
        hvcConnector.send_data(data);
        strStatus += hvcConnector.getStatus();
        int response_code = receiveHeader(data_len);
        if ( response_code == 0x00 ) { // Success
            rcvBuffer = receiveData(data_len[0]);
            if ( rcvBuffer == null ) {
                response_code = RESPONSE_DATA_SIZE_ERR;
            }
        } else { // error
            rcvBuffer = null;
        }
        return response_code;
    }
}
