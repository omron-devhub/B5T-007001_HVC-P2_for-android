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

public abstract class P2Def extends Object {
    // Exceute function flag definition
    public static final int EX_NONE = 0x00;
    public static final int EX_BODY = 0x01;
    public static final int EX_HAND = 0x02;
    public static final int EX_FACE = 0x04;
    public static final int EX_DIRECTION = 0x08;
    public static final int EX_AGE = 0x10;
    public static final int EX_GENDER = 0x20;
    public static final int EX_GAZE = 0x40;
    public static final int EX_BLINK = 0x80;
    public static final int EX_EXPRESSION = 0x100;
    public static final int EX_RECOGNITION = 0x200;
    public static final int EX_ALL = EX_BODY + EX_HAND + EX_FACE + EX_DIRECTION +
            EX_AGE + EX_GENDER + EX_GAZE + EX_BLINK + EX_EXPRESSION + EX_RECOGNITION;

    // STB library ON/OFF flag
    public static final boolean USE_STB_ON = true;
    public static final boolean USE_STB_OFF = false;

    // Output image type definition
    public static final int OUT_IMG_TYPE_NONE = 0x00;
    public static final int OUT_IMG_TYPE_QVGA = 0x01;
    public static final int OUT_IMG_TYPE_QQVGA = 0x02;

    // HVC camera angle definition.
    public static final int HVC_CAM_ANGLE_0 = 0x00;
    public static final int HVC_CAM_ANGLE_90 = 0x01;
    public static final int HVC_CAM_ANGLE_180 = 0x02;
    public static final int HVC_CAM_ANGLE_270 = 0x03;

    // Face angel definitions.
    public static final int HVC_FACE_ANGLE_YAW_30 = 0x00;      // Yaw angle:-30 to +30 degree (Frontal face)
    public static final int HVC_FACE_ANGLE_YAW_60 = 0x01;      // Yaw angle:-60 to +60 degree (Half-Profile face)
    public static final int HVC_FACE_ANGLE_YAW_90 = 0x02;      // Yaw angle:-90 to +90 degree (Profile face)
    public static final int HVC_FACE_ANGLE_ROLL_15 = 0x00;     // Roll angle:-15 to +15 degree
    public static final int HVC_FACE_ANGLE_ROLL_45 = 0x01;     // Roll angle:-45 to +45 degree

    // Available serial baudrate sets
    public static final int AVAILABLE_BAUD[] = {9600, 38400, 115200, 230400, 460800, 921600};

    public static final int DEFAULT_BAUD = 9600;

    // Response code
    public static final int RESPONSE_CODE_PLURAL_FACE = 0x02;  // Number of faces that can be registerd is 0
    public static final int RESPONSE_CODE_NO_FACE = 0x01;      // Number of detected faces is 2 or more
    public static final int RESPONSE_CODE_NORMAL = 0x00;       // Normal end
    public static final int RESPONSE_CODE_UNDEFINED = 0xFF;    // Undefined error
    public static final int RESPONSE_CODE_INTERNAL = 0xFE;     // Intenal error
    public static final int RESPONSE_CODE_INVALID_CMD = 0xFD;  // Improper command

    public static final int RESPONSE_HEADER_SIZE_ERR = 0x81;   // Response header size is not enough.
    public static final int RESPONSE_HEADER_SYNC_ERR = 0x82;   // Invalid Sync code.
    public static final int RESPONSE_DATA_SIZE_ERR = 0x83;     // Response data size is not enough.

    // Expression result
    public static final int EXP_UNKNOWN = -1;
    public static final int EXP_NEUTRAL = 0;
    public static final int EXP_HAPPINESS = 1;
    public static final int EXP_SURPRISE = 2;
    public static final int EXP_ANGER = 3;
    public static final int EXP_SADNESS = 4;

    // Gender result
    public static final int GENDER_UNKNOWN = -1;
    public static final int GENDER_FEMALE = 0;
    public static final int GENDER_MALE = 1;
};
