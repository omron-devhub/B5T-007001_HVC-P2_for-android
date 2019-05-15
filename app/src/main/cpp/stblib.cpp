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
#include <jni.h>
#include <string>

#include "usr_include/STBAPI.h"
#include "usr_include/STBCommonDef.h"
#include "usr_include/STBTypedef.h"

extern "C" JNIEXPORT jint JNICALL Java_jp_co_omron_HvcP2_1Api_Stabilization_getVersion
        (JNIEnv *env, jobject obj, jintArray majorVersion, jintArray minorVersion)
{
    STB_INT32 nRet;
    STB_INT8 nMajorVersion;
    STB_INT8 nMinorVersion;

    if ( (majorVersion == NULL) || (minorVersion == NULL) ) {
        return STB_ERR_INVALIDPARAM;
    }

    nRet = STB_GetVersion(&nMajorVersion, &nMinorVersion);
    if ( STB_NORMAL == nRet ) {
        jint ver;
        ver = nMajorVersion;
        env->SetIntArrayRegion(majorVersion, 0, 1, &ver);
        ver = nMinorVersion;
        env->SetIntArrayRegion(minorVersion, 0, 1, &ver);
    }
    return nRet;
}

/* Create/Delete handle */

extern "C" JNIEXPORT jlong JNICALL Java_jp_co_omron_HvcP2_1Api_Stabilization_createHandle
        (JNIEnv *env, jobject obj, jint nUseFuncFlag)
{
    HSTB hSTB = STB_CreateHandle(nUseFuncFlag);
    return (jlong)hSTB;
}

extern "C" JNIEXPORT jint JNICALL Java_jp_co_omron_HvcP2_1Api_Stabilization_deleteHandle
        (JNIEnv *env, jobject obj, jlong handle)
{
    HSTB hSTB = (HSTB)((long)handle);

    STB_DeleteHandle(hSTB);
    return STB_NORMAL;
}

/* Set the one frame result of HVC into this library */
extern "C" JNIEXPORT jint JNICALL Java_jp_co_omron_HvcP2_1Api_Stabilization_setFrameResult
        (JNIEnv *env, jobject obj, jlong handle, jobject frameResult)
{
    STB_INT32 nRet;
    STB_FRAME_RESULT stFrameResult;

    if ( frameResult == NULL ) {
        return STB_ERR_INVALIDPARAM;
    }

    jclass clsResult = env->GetObjectClass(frameResult);
    jfieldID field_ID = env->GetFieldID(clsResult, "bodys", "Ljp/co/omron/HvcP2_Api/HvcResultC$C_FRAME_RESULT_BODYS;");
    jobject objBodys = env->GetObjectField(frameResult, field_ID);
    jclass clsBodys = env->GetObjectClass(objBodys);
    field_ID = env->GetFieldID(clsBodys, "nCount", "I");
    stFrameResult.bodys.nCount = env->GetIntField(objBodys, field_ID);
    field_ID = env->GetFieldID(clsBodys, "body", "[Ljp/co/omron/HvcP2_Api/HvcResultC$C_FRAME_RESULT_DETECTION;");
    jobject objBody = env->GetObjectField(objBodys, field_ID);
    jobjectArray *jArray = reinterpret_cast<jobjectArray *>( &objBody );
    for ( int i=0; i<stFrameResult.bodys.nCount; i++ ) {
        jobject objElement = env->GetObjectArrayElement(*jArray, i);
        jclass clsElement = env->GetObjectClass(objElement);

        field_ID = env->GetFieldID(clsElement, "center", "Ljp/co/omron/HvcP2_Api/HvcResultC$C_POINT;");
        jobject objPoint = env->GetObjectField(objElement, field_ID);
        jclass clsPoint = env->GetObjectClass(objPoint);
        field_ID = env->GetFieldID(clsPoint, "nX", "I");
        stFrameResult.bodys.body[i].center.nX = env->GetIntField(objPoint, field_ID);
        field_ID = env->GetFieldID(clsPoint, "nY", "I");
        stFrameResult.bodys.body[i].center.nY = env->GetIntField(objPoint, field_ID);

        field_ID = env->GetFieldID(clsElement, "nSize", "I");
        stFrameResult.bodys.body[i].nSize = env->GetIntField(objElement, field_ID);
        field_ID = env->GetFieldID(clsElement, "nConfidence", "I");
        stFrameResult.bodys.body[i].nConfidence = env->GetIntField(objElement, field_ID);
    }

    field_ID = env->GetFieldID(clsResult, "faces", "Ljp/co/omron/HvcP2_Api/HvcResultC$C_FRAME_RESULT_FACES;");
    jobject objFaces = env->GetObjectField(frameResult, field_ID);
    jclass clsFaces = env->GetObjectClass(objFaces);
    field_ID = env->GetFieldID(clsFaces, "nCount", "I");
    stFrameResult.faces.nCount = env->GetIntField(objFaces, field_ID);
    field_ID = env->GetFieldID(clsFaces, "face", "[Ljp/co/omron/HvcP2_Api/HvcResultC$C_FRAME_RESULT_FACE;");
    jobject objFace = env->GetObjectField(objFaces, field_ID);
    jArray = reinterpret_cast<jobjectArray *>( &objFace );
    for ( int i=0; i<stFrameResult.faces.nCount; i++ ) {
        jobject objElement = env->GetObjectArrayElement(*jArray, i);
        jclass clsElement = env->GetObjectClass(objElement);

        field_ID = env->GetFieldID(clsElement, "center", "Ljp/co/omron/HvcP2_Api/HvcResultC$C_POINT;");
        jobject objPoint = env->GetObjectField(objElement, field_ID);
        jclass clsPoint = env->GetObjectClass(objPoint);
        field_ID = env->GetFieldID(clsPoint, "nX", "I");
        stFrameResult.faces.face[i].center.nX = env->GetIntField(objPoint, field_ID);
        field_ID = env->GetFieldID(clsPoint, "nY", "I");
        stFrameResult.faces.face[i].center.nY = env->GetIntField(objPoint, field_ID);

        field_ID = env->GetFieldID(clsElement, "nSize", "I");
        stFrameResult.faces.face[i].nSize = env->GetIntField(objElement, field_ID);
        field_ID = env->GetFieldID(clsElement, "nConfidence", "I");
        stFrameResult.faces.face[i].nConfidence = env->GetIntField(objElement, field_ID);

        field_ID = env->GetFieldID(clsElement, "direction", "Ljp/co/omron/HvcP2_Api/HvcResultC$C_FRAME_RESULT_DIRECTION;");
        jobject objDirection = env->GetObjectField(objElement, field_ID);
        jclass clsDirection = env->GetObjectClass(objDirection);
        field_ID = env->GetFieldID(clsDirection, "nLR", "I");
        stFrameResult.faces.face[i].direction.nLR = env->GetIntField(objDirection, field_ID);
        field_ID = env->GetFieldID(clsDirection, "nUD", "I");
        stFrameResult.faces.face[i].direction.nUD = env->GetIntField(objDirection, field_ID);
        field_ID = env->GetFieldID(clsDirection, "nRoll", "I");
        stFrameResult.faces.face[i].direction.nRoll = env->GetIntField(objDirection, field_ID);
        field_ID = env->GetFieldID(clsDirection, "nConfidence", "I");
        stFrameResult.faces.face[i].direction.nConfidence = env->GetIntField(objDirection, field_ID);

        field_ID = env->GetFieldID(clsElement, "age", "Ljp/co/omron/HvcP2_Api/HvcResultC$C_FRAME_RESULT_AGE;");
        jobject objAge = env->GetObjectField(objElement, field_ID);
        jclass clsAge = env->GetObjectClass(objAge);
        field_ID = env->GetFieldID(clsAge, "nAge", "I");
        stFrameResult.faces.face[i].age.nAge = env->GetIntField(objAge, field_ID);
        field_ID = env->GetFieldID(clsAge, "nConfidence", "I");
        stFrameResult.faces.face[i].age.nConfidence = env->GetIntField(objAge, field_ID);

        field_ID = env->GetFieldID(clsElement, "gender", "Ljp/co/omron/HvcP2_Api/HvcResultC$C_FRAME_RESULT_GENDER;");
        jobject objGender = env->GetObjectField(objElement, field_ID);
        jclass clsGender = env->GetObjectClass(objGender);
        field_ID = env->GetFieldID(clsGender, "nGender", "I");
        stFrameResult.faces.face[i].gender.nGender = env->GetIntField(objGender, field_ID);
        field_ID = env->GetFieldID(clsGender, "nConfidence", "I");
        stFrameResult.faces.face[i].gender.nConfidence = env->GetIntField(objGender, field_ID);

        field_ID = env->GetFieldID(clsElement, "gaze", "Ljp/co/omron/HvcP2_Api/HvcResultC$C_FRAME_RESULT_GAZE;");
        jobject objGaze = env->GetObjectField(objElement, field_ID);
        jclass clsGaze = env->GetObjectClass(objGaze);
        field_ID = env->GetFieldID(clsGaze, "nLR", "I");
        stFrameResult.faces.face[i].gaze.nLR = env->GetIntField(objGaze, field_ID);
        field_ID = env->GetFieldID(clsGaze, "nUD", "I");
        stFrameResult.faces.face[i].gaze.nUD = env->GetIntField(objGaze, field_ID);

        field_ID = env->GetFieldID(clsElement, "blink", "Ljp/co/omron/HvcP2_Api/HvcResultC$C_FRAME_RESULT_BLINK;");
        jobject objBlink = env->GetObjectField(objElement, field_ID);
        jclass clsBlink = env->GetObjectClass(objBlink);
        field_ID = env->GetFieldID(clsBlink, "nLeftEye", "I");
        stFrameResult.faces.face[i].blink.nLeftEye = env->GetIntField(objBlink, field_ID);
        field_ID = env->GetFieldID(clsBlink, "nRightEye", "I");
        stFrameResult.faces.face[i].blink.nRightEye = env->GetIntField(objBlink, field_ID);

        field_ID = env->GetFieldID(clsElement, "expression", "Ljp/co/omron/HvcP2_Api/HvcResultC$C_FRAME_RESULT_EXPRESSION;");
        jobject objExpression = env->GetObjectField(objElement, field_ID);
        jclass clsExpression = env->GetObjectClass(objExpression);
        field_ID = env->GetFieldID(clsExpression, "anScore", "[I");
        jobject objScore = env->GetObjectField(objExpression, field_ID);
        jintArray *anScores = reinterpret_cast<jintArray *>( &objScore );
        jint *nScore = env->GetIntArrayElements( *anScores, NULL );
        for ( int n=0; n<5; n++ ) {
            stFrameResult.faces.face[i].expression.anScore[n] = nScore[n];
        }
        field_ID = env->GetFieldID(clsExpression, "nDegree", "I");
        stFrameResult.faces.face[i].expression.nDegree = env->GetIntField(objExpression, field_ID);

        field_ID = env->GetFieldID(clsElement, "recognition", "Ljp/co/omron/HvcP2_Api/HvcResultC$C_FRAME_RESULT_RECOGNITION;");
        jobject objRecognition = env->GetObjectField(objElement, field_ID);
        jclass clsRecognition = env->GetObjectClass(objRecognition);
        field_ID = env->GetFieldID(clsRecognition, "nUID", "I");
        stFrameResult.faces.face[i].recognition.nUID = env->GetIntField(objRecognition, field_ID);
        field_ID = env->GetFieldID(clsRecognition, "nScore", "I");
        stFrameResult.faces.face[i].recognition.nScore = env->GetIntField(objRecognition, field_ID);
    }

    HSTB hSTB = (HSTB)((long)handle);

    nRet = STB_SetFrameResult(hSTB, &stFrameResult);
    return nRet;
}

/* Clear frame results */
extern "C" JNIEXPORT jint JNICALL Java_jp_co_omron_HvcP2_1Api_Stabilization_clearFrameResults
        (JNIEnv *env, jobject obj, jlong handle)
{
    STB_INT32 nRet;

    HSTB hSTB = (HSTB)((long)handle);

    nRet = STB_ClearFrameResults(hSTB);
    return nRet;
}

/* Main process execution */
extern "C" JNIEXPORT jint JNICALL Java_jp_co_omron_HvcP2_1Api_Stabilization_execute
        (JNIEnv *env, jobject obj, jlong handle)
{
    STB_INT32 nRet;

    HSTB hSTB = (HSTB)((long)handle);

    nRet = STB_Execute(hSTB);
    return nRet;
}

/* Get the result */

extern "C" JNIEXPORT jint JNICALL Java_jp_co_omron_HvcP2_1Api_Stabilization_getFaces
        (JNIEnv *env, jobject obj, jlong handle, jintArray faceCount, jobjectArray faces_res)
{
    STB_INT32 nRet;
    STB_UINT32 unFaceCount;
    STB_FACE stFace[35];

    if ( faceCount == NULL ) {
        return STB_ERR_INVALIDPARAM;
    }

    HSTB hSTB = (HSTB)((long)handle);

    nRet = STB_GetFaces(hSTB, &unFaceCount, stFace);
    if ( STB_NORMAL == nRet ) {
        env->SetIntArrayRegion(faceCount, 0, 1, (jint *)&unFaceCount);
        for ( int i=0; i<unFaceCount; i++ ) {
            jobject objFace = env->GetObjectArrayElement(faces_res, i);
            jclass clsFace = env->GetObjectClass(objFace);

            jfieldID field_ID = env->GetFieldID(clsFace, "nDetectID", "I");
            env->SetIntField(objFace, field_ID, stFace[i].nDetectID);
            field_ID = env->GetFieldID(clsFace, "nTrackingID", "I");
            env->SetIntField(objFace, field_ID, stFace[i].nTrackingID);

            field_ID = env->GetFieldID(clsFace, "center", "Ljp/co/omron/HvcP2_Api/HvcTrackingResultC$C_POS;");
            jobject objPos = env->GetObjectField(objFace, field_ID);
            jclass clsPos = env->GetObjectClass(objPos);
            field_ID = env->GetFieldID(clsPos, "x", "I");
            env->SetIntField(objPos, field_ID, stFace[i].center.x);
            field_ID = env->GetFieldID(clsPos, "y", "I");
            env->SetIntField(objPos, field_ID, stFace[i].center.y);

            field_ID = env->GetFieldID(clsFace, "nSize", "I");
            env->SetIntField(objFace, field_ID, stFace[i].nSize);
            field_ID = env->GetFieldID(clsFace, "conf", "I");
            env->SetIntField(objFace, field_ID, stFace[i].conf);

            field_ID = env->GetFieldID(clsFace, "direction", "Ljp/co/omron/HvcP2_Api/HvcTrackingResultC$C_DIRECTION;");
            jobject objDirection = env->GetObjectField(objFace, field_ID);
            jclass clsDirection = env->GetObjectClass(objDirection);
            field_ID = env->GetFieldID(clsDirection, "status", "I");
            env->SetIntField(objDirection, field_ID, stFace[i].direction.status);
            field_ID = env->GetFieldID(clsDirection, "conf", "I");
            env->SetIntField(objDirection, field_ID, stFace[i].direction.conf);
            field_ID = env->GetFieldID(clsDirection, "yaw", "I");
            env->SetIntField(objDirection, field_ID, stFace[i].direction.yaw);
            field_ID = env->GetFieldID(clsDirection, "pitch", "I");
            env->SetIntField(objDirection, field_ID, stFace[i].direction.pitch);
            field_ID = env->GetFieldID(clsDirection, "roll", "I");
            env->SetIntField(objDirection, field_ID, stFace[i].direction.roll);

            field_ID = env->GetFieldID(clsFace, "age", "Ljp/co/omron/HvcP2_Api/HvcTrackingResultC$C_RES;");
            jobject objAge = env->GetObjectField(objFace, field_ID);
            jclass clsAge = env->GetObjectClass(objAge);
            field_ID = env->GetFieldID(clsAge, "status", "I");
            env->SetIntField(objAge, field_ID, stFace[i].age.status);
            field_ID = env->GetFieldID(clsAge, "conf", "I");
            env->SetIntField(objAge, field_ID, stFace[i].age.conf);
            field_ID = env->GetFieldID(clsAge, "value", "I");
            env->SetIntField(objAge, field_ID, stFace[i].age.value);

            field_ID = env->GetFieldID(clsFace, "gender", "Ljp/co/omron/HvcP2_Api/HvcTrackingResultC$C_RES;");
            jobject objGender = env->GetObjectField(objFace, field_ID);
            jclass clsGender = env->GetObjectClass(objGender);
            field_ID = env->GetFieldID(clsGender, "status", "I");
            env->SetIntField(objGender, field_ID, stFace[i].gender.status);
            field_ID = env->GetFieldID(clsGender, "conf", "I");
            env->SetIntField(objGender, field_ID, stFace[i].gender.conf);
            field_ID = env->GetFieldID(clsGender, "value", "I");
            env->SetIntField(objGender, field_ID, stFace[i].gender.value);

            field_ID = env->GetFieldID(clsFace, "gaze", "Ljp/co/omron/HvcP2_Api/HvcTrackingResultC$C_GAZE;");
            jobject objGaze = env->GetObjectField(objFace, field_ID);
            jclass clsGaze = env->GetObjectClass(objGaze);
            field_ID = env->GetFieldID(clsGaze, "status", "I");
            env->SetIntField(objGaze, field_ID, stFace[i].gaze.status);
            field_ID = env->GetFieldID(clsGaze, "conf", "I");
            env->SetIntField(objGaze, field_ID, stFace[i].gaze.conf);
            field_ID = env->GetFieldID(clsGaze, "UD", "I");
            env->SetIntField(objGaze, field_ID, stFace[i].gaze.UD);
            field_ID = env->GetFieldID(clsGaze, "LR", "I");
            env->SetIntField(objGaze, field_ID, stFace[i].gaze.LR);

            field_ID = env->GetFieldID(clsFace, "blink", "Ljp/co/omron/HvcP2_Api/HvcTrackingResultC$C_BLINK;");
            jobject objBlink = env->GetObjectField(objFace, field_ID);
            jclass clsBlink = env->GetObjectClass(objBlink);
            field_ID = env->GetFieldID(clsBlink, "status", "I");
            env->SetIntField(objBlink, field_ID, stFace[i].blink.status);
            field_ID = env->GetFieldID(clsBlink, "ratioL", "I");
            env->SetIntField(objBlink, field_ID, stFace[i].blink.ratioL);
            field_ID = env->GetFieldID(clsBlink, "ratioR", "I");
            env->SetIntField(objBlink, field_ID, stFace[i].blink.ratioR);

            field_ID = env->GetFieldID(clsFace, "expression", "Ljp/co/omron/HvcP2_Api/HvcTrackingResultC$C_RES;");
            jobject objExpression = env->GetObjectField(objFace, field_ID);
            jclass clsExpression = env->GetObjectClass(objExpression);
            field_ID = env->GetFieldID(clsExpression, "status", "I");
            env->SetIntField(objExpression, field_ID, stFace[i].expression.status);
            field_ID = env->GetFieldID(clsExpression, "conf", "I");
            env->SetIntField(objExpression, field_ID, stFace[i].expression.conf);
            field_ID = env->GetFieldID(clsExpression, "value", "I");
            env->SetIntField(objExpression, field_ID, stFace[i].expression.value);

            field_ID = env->GetFieldID(clsFace, "recognition", "Ljp/co/omron/HvcP2_Api/HvcTrackingResultC$C_RES;");
            jobject objRecognition = env->GetObjectField(objFace, field_ID);
            jclass clsRecognition = env->GetObjectClass(objRecognition);
            field_ID = env->GetFieldID(clsRecognition, "status", "I");
            env->SetIntField(objRecognition, field_ID, stFace[i].recognition.status);
            field_ID = env->GetFieldID(clsRecognition, "conf", "I");
            env->SetIntField(objRecognition, field_ID, stFace[i].recognition.conf);
            field_ID = env->GetFieldID(clsRecognition, "value", "I");
            env->SetIntField(objRecognition, field_ID, stFace[i].recognition.value);
        }
    }
    return nRet;
}

extern "C" JNIEXPORT jint JNICALL Java_jp_co_omron_HvcP2_1Api_Stabilization_getBodies
        (JNIEnv *env, jobject obj, jlong handle, jintArray bodyCount, jobjectArray bodies_res)
{
    STB_INT32 nRet;
    STB_UINT32 unBodyCount;
    STB_BODY stBody[35];

    if ( bodyCount == NULL ) {
        return STB_ERR_INVALIDPARAM;
    }

    HSTB hSTB = (HSTB)((long)handle);

    nRet = STB_GetBodies(hSTB, &unBodyCount, stBody);
    if ( STB_NORMAL == nRet ) {
        env->SetIntArrayRegion(bodyCount, 0, 1, (jint *)&unBodyCount);
        for ( int i=0; i<unBodyCount; i++ ) {
            jobject objBody = env->GetObjectArrayElement(bodies_res, i);
            jclass clsBody = env->GetObjectClass(objBody);

            jfieldID field_ID = env->GetFieldID(clsBody, "nDetectID", "I");
            env->SetIntField(objBody, field_ID, stBody[i].nDetectID);
            field_ID = env->GetFieldID(clsBody, "nTrackingID", "I");
            env->SetIntField(objBody, field_ID, stBody[i].nTrackingID);

            field_ID = env->GetFieldID(clsBody, "center", "Ljp/co/omron/HvcP2_Api/HvcTrackingResultC$C_POS;");
            jobject objPos = env->GetObjectField(objBody, field_ID);
            jclass clsPos = env->GetObjectClass(objPos);
            field_ID = env->GetFieldID(clsPos, "x", "I");
            env->SetIntField(objPos, field_ID, stBody[i].center.x);
            field_ID = env->GetFieldID(clsPos, "y", "I");
            env->SetIntField(objPos, field_ID, stBody[i].center.y);

            field_ID = env->GetFieldID(clsBody, "nSize", "I");
            env->SetIntField(objBody, field_ID, stBody[i].nSize);
            field_ID = env->GetFieldID(clsBody, "conf", "I");
            env->SetIntField(objBody, field_ID, stBody[i].conf);
        }
    }
    return nRet;
}

/* Setting/Getting functions for tracking */

extern "C" JNIEXPORT jint JNICALL Java_jp_co_omron_HvcP2_1Api_Stabilization_setTrRetryCount
(JNIEnv *env, jobject obj, jlong handle, jint nMaxRetryCount)
{
    STB_INT32 nRet;

    HSTB hSTB = (HSTB)((long)handle);

    nRet = STB_SetTrRetryCount(hSTB, nMaxRetryCount);
    return nRet;
}

extern "C" JNIEXPORT jint JNICALL Java_jp_co_omron_HvcP2_1Api_Stabilization_getTrRetryCount
(JNIEnv *env, jobject obj, jlong handle, jintArray maxRetryCount)
{
    STB_INT32 nRet;
    STB_INT32 nMaxRetryCount;

    if ( maxRetryCount == NULL ) {
        return STB_ERR_INVALIDPARAM;
    }

    HSTB hSTB = (HSTB)((long)handle);

    nRet = STB_GetTrRetryCount(hSTB, &nMaxRetryCount);
    if ( STB_NORMAL == nRet ) {
        env->SetIntArrayRegion(maxRetryCount, 0, 1, &nMaxRetryCount);
    }
    return nRet;
}

extern "C" JNIEXPORT jint JNICALL Java_jp_co_omron_HvcP2_1Api_Stabilization_setTrSteadinessParam
(JNIEnv *env, jobject obj, jlong handle, jint nPosSteadinessParam, jint nSizeSteadinessParam)
{
    STB_INT32 nRet;

    HSTB hSTB = (HSTB)((long)handle);

    nRet = STB_SetTrSteadinessParam(hSTB, nPosSteadinessParam, nSizeSteadinessParam);
    return nRet;
}

extern "C" JNIEXPORT jint JNICALL Java_jp_co_omron_HvcP2_1Api_Stabilization_getTrSteadinessParam
(JNIEnv *env, jobject obj, jlong handle, jintArray posSteadinessParam, jintArray sizeSteadinessParam)
{
    STB_INT32 nRet;
    STB_INT32 nPosSteadinessParam;
    STB_INT32 nSizeSteadinessParam;

    if ( (posSteadinessParam == NULL) || (sizeSteadinessParam == NULL) ) {
        return STB_ERR_INVALIDPARAM;
    }

    HSTB hSTB = (HSTB)((long)handle);

    nRet = STB_GetTrSteadinessParam(hSTB, &nPosSteadinessParam, &nSizeSteadinessParam);
    if ( STB_NORMAL == nRet ) {
        env->SetIntArrayRegion(posSteadinessParam, 0, 1, &nPosSteadinessParam);
        env->SetIntArrayRegion(sizeSteadinessParam, 0, 1, &nSizeSteadinessParam);
    }
    return nRet;
}

/* Setting/Getting functions for property */

extern "C" JNIEXPORT jint JNICALL Java_jp_co_omron_HvcP2_1Api_Stabilization_setPeThresholdUse
(JNIEnv *env, jobject obj, jlong handle, jint nThreshold)
{
    STB_INT32 nRet;

    HSTB hSTB = (HSTB)((long)handle);

    nRet = STB_SetPeThresholdUse(hSTB, nThreshold);
    return nRet;
}

extern "C" JNIEXPORT jint JNICALL Java_jp_co_omron_HvcP2_1Api_Stabilization_getPeThresholdUse
(JNIEnv *env, jobject obj, jlong handle, jintArray threshold)
{
    STB_INT32 nRet;
    STB_INT32 nThreshold;

    if ( threshold == NULL ) {
        return STB_ERR_INVALIDPARAM;
    }

    HSTB hSTB = (HSTB)((long)handle);

    nRet = STB_GetPeThresholdUse(hSTB, &nThreshold);
    if ( STB_NORMAL == nRet ) {
        env->SetIntArrayRegion(threshold, 0, 1, &nThreshold);
    }
    return nRet;
}

extern "C" JNIEXPORT jint JNICALL Java_jp_co_omron_HvcP2_1Api_Stabilization_setPeAngleUse
(JNIEnv *env, jobject obj, jlong handle, jint nMinUDAngle, jint nMaxUDAngle, jint nMinLRAngle, jint nMaxLRAngle)
{
    STB_INT32 nRet;

    HSTB hSTB = (HSTB)((long)handle);

    nRet = STB_SetPeAngleUse(hSTB, nMinUDAngle, nMaxUDAngle, nMinLRAngle, nMaxLRAngle);
    return nRet;
}

extern "C" JNIEXPORT jint JNICALL Java_jp_co_omron_HvcP2_1Api_Stabilization_getPeAngleUse
(JNIEnv *env, jobject obj, jlong handle, jintArray minUDAngle, jintArray maxUDAngle, jintArray minLRAngle, jintArray maxLRAngle)
{
    STB_INT32 nRet;
    STB_INT32 nMinUDAngle;
    STB_INT32 nMaxUDAngle;
    STB_INT32 nMinLRAngle;
    STB_INT32 nMaxLRAngle;

    if ( (minUDAngle == NULL) || (maxUDAngle == NULL) || (minLRAngle == NULL) || (maxLRAngle == NULL) ) {
        return STB_ERR_INVALIDPARAM;
    }

    HSTB hSTB = (HSTB)((long)handle);

    nRet = STB_GetPeAngleUse(hSTB, &nMinUDAngle, &nMaxUDAngle, &nMinLRAngle, &nMaxLRAngle);
    if ( STB_NORMAL == nRet ) {
        env->SetIntArrayRegion(minUDAngle, 0, 1, &nMinUDAngle);
        env->SetIntArrayRegion(maxUDAngle, 0, 1, &nMaxUDAngle);
        env->SetIntArrayRegion(minLRAngle, 0, 1, &nMinLRAngle);
        env->SetIntArrayRegion(maxLRAngle, 0, 1, &nMaxLRAngle);
    }
    return nRet;
}

extern "C" JNIEXPORT jint JNICALL Java_jp_co_omron_HvcP2_1Api_Stabilization_setPeCompleteFrameCount
(JNIEnv *env, jobject obj, jlong handle, jint nFrameCount)
{
    STB_INT32 nRet;

    HSTB hSTB = (HSTB)((long)handle);

    nRet = STB_SetPeCompleteFrameCount(hSTB, nFrameCount);
    return nRet;
}

extern "C" JNIEXPORT jint JNICALL Java_jp_co_omron_HvcP2_1Api_Stabilization_getPeCompleteFrameCount
(JNIEnv *env, jobject obj, jlong handle, jintArray frameCount)
{
    STB_INT32 nRet;
    STB_INT32 nFrameCount;

    if ( frameCount == NULL ) {
        return STB_ERR_INVALIDPARAM;
    }

    HSTB hSTB = (HSTB)((long)handle);

    nRet = STB_GetPeCompleteFrameCount(hSTB, &nFrameCount);
    if ( STB_NORMAL == nRet ) {
        env->SetIntArrayRegion(frameCount, 0, 1, &nFrameCount);
    }
    return nRet;
}

/* Setting/Getting function for recognition */

extern "C" JNIEXPORT jint JNICALL Java_jp_co_omron_HvcP2_1Api_Stabilization_setFrThresholdUse
(JNIEnv *env, jobject obj, jlong handle, jint nThreshold)
{
    STB_INT32 nRet;

    HSTB hSTB = (HSTB)((long)handle);

    nRet = STB_SetFrThresholdUse(hSTB, nThreshold);
    return nRet;
}

extern "C" JNIEXPORT jint JNICALL Java_jp_co_omron_HvcP2_1Api_Stabilization_getFrThresholdUse
(JNIEnv *env, jobject obj, jlong handle, jintArray threshold)
{
    STB_INT32 nRet;
    STB_INT32 nThreshold;

    if ( threshold == NULL ) {
        return STB_ERR_INVALIDPARAM;
    }

    HSTB hSTB = (HSTB)((long)handle);

    nRet = STB_GetFrThresholdUse(hSTB, &nThreshold);
    if ( STB_NORMAL == nRet ) {
        env->SetIntArrayRegion(threshold, 0, 1, &nThreshold);
    }
    return nRet;
}

extern "C" JNIEXPORT jint JNICALL Java_jp_co_omron_HvcP2_1Api_Stabilization_setFrAngleUse
(JNIEnv *env, jobject obj, jlong handle, jint nMinUDAngle, jint nMaxUDAngle, jint nMinLRAngle, jint nMaxLRAngle)
{
    STB_INT32 nRet;

    HSTB hSTB = (HSTB)((long)handle);

    nRet = STB_SetFrAngleUse(hSTB, nMinUDAngle, nMaxUDAngle, nMinLRAngle, nMaxLRAngle);
    return nRet;
}

extern "C" JNIEXPORT jint JNICALL Java_jp_co_omron_HvcP2_1Api_Stabilization_getFrAngleUse
(JNIEnv *env, jobject obj, jlong handle, jintArray minUDAngle, jintArray maxUDAngle, jintArray minLRAngle, jintArray maxLRAngle)
{
    STB_INT32 nRet;
    STB_INT32 nMinUDAngle;
    STB_INT32 nMaxUDAngle;
    STB_INT32 nMinLRAngle;
    STB_INT32 nMaxLRAngle;

    if ( (minUDAngle == NULL) || (maxUDAngle == NULL) || (minLRAngle == NULL) || (maxLRAngle == NULL) ) {
        return STB_ERR_INVALIDPARAM;
    }

    HSTB hSTB = (HSTB)((long)handle);

    nRet = STB_GetFrAngleUse(hSTB, &nMinUDAngle, &nMaxUDAngle, &nMinLRAngle, &nMaxLRAngle);
    if ( STB_NORMAL == nRet ) {
        env->SetIntArrayRegion(minUDAngle, 0, 1, &nMinUDAngle);
        env->SetIntArrayRegion(maxUDAngle, 0, 1, &nMaxUDAngle);
        env->SetIntArrayRegion(minLRAngle, 0, 1, &nMinLRAngle);
        env->SetIntArrayRegion(maxLRAngle, 0, 1, &nMaxLRAngle);
    }
    return nRet;
}

extern "C" JNIEXPORT jint JNICALL Java_jp_co_omron_HvcP2_1Api_Stabilization_setFrCompleteFrameCount
(JNIEnv *env, jobject obj, jlong handle, jint nFrameCount)
{
    STB_INT32 nRet;

    HSTB hSTB = (HSTB)((long)handle);

    nRet = STB_SetFrCompleteFrameCount(hSTB, nFrameCount);
    return nRet;
}

extern "C" JNIEXPORT jint JNICALL Java_jp_co_omron_HvcP2_1Api_Stabilization_getFrCompleteFrameCount
(JNIEnv *env, jobject obj, jlong handle, jintArray frameCount)
{
    STB_INT32 nRet;
    STB_INT32 nFrameCount;

    if ( frameCount == NULL ) {
        return STB_ERR_INVALIDPARAM;
    }

    HSTB hSTB = (HSTB)((long)handle);

    nRet = STB_GetFrCompleteFrameCount(hSTB, &nFrameCount);
    if ( STB_NORMAL == nRet ) {
        env->SetIntArrayRegion(frameCount, 0, 1, &nFrameCount);
    }
    return nRet;
}

extern "C" JNIEXPORT jint JNICALL Java_jp_co_omron_HvcP2_1Api_Stabilization_setFrMinRatio
(JNIEnv *env, jobject obj, jlong handle, jint nMinRatio)
{
    STB_INT32 nRet;

	HSTB hSTB = (HSTB)((long)handle);

	nRet = STB_SetFrMinRatio(hSTB, nMinRatio);
	return nRet;
}

extern "C" JNIEXPORT jint JNICALL Java_jp_co_omron_HvcP2_1Api_Stabilization_getFrMinRatio
(JNIEnv *env, jobject obj, jlong handle, jintArray minRatio)
{
	STB_INT32 nRet;
	STB_INT32 nMinRatio;

	if ( minRatio == NULL ) {
		return STB_ERR_INVALIDPARAM;
	}

	HSTB hSTB = (HSTB)((long)handle);

	nRet = STB_GetFrMinRatio(hSTB, &nMinRatio);
	if ( STB_NORMAL == nRet ) {
		env->SetIntArrayRegion(minRatio, 0, 1, &nMinRatio);
	}
	return nRet;
}

