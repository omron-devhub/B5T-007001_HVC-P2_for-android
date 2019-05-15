package jp.co.omron.HvcP2_sample;

import android.content.Context;
import android.hardware.usb.UsbManager;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import jp.co.omron.HvcP2_Api.*;

public class MainActivity extends AppCompatActivity {
    private final String SAVE_DIR = "/HVC/";
    private final String logging_fname = "SampleLog.txt";

    private boolean loggingFlash = true;
    private ArrayList<String> loggingArray = new ArrayList<String>();

    /*
     * User Config. Please edit here if you need.
     */
    // Output image file name.
    private final String exec_img_fname = "SampleImage.jpg";
    private final String registerd_img_fname = "registerd_img.jpg";

    // Read timeout value in seconds for serial communication.
    // If you use UART slow baudrate, please edit here.
    private final int timeout = 3;

    // -------------------------
    // B5T-007001 settings
    // -------------------------
    // Execute functions
    private final int exec_func = P2Def.EX_FACE |
                                  P2Def.EX_BODY |
                                  P2Def.EX_HAND |
                                  P2Def.EX_DIRECTION |
                                  P2Def.EX_AGE |
                                  P2Def.EX_GENDER |
                                  P2Def.EX_BLINK |
                                  P2Def.EX_GAZE |
                                  P2Def.EX_EXPRESSION |
                                  P2Def.EX_RECOGNITION;

    // Output image type
    private final int output_img_type = P2Def.// OUT_IMG_TYPE_NONE
                                              // OUT_IMG_TYPE_QQVGA
                                                 OUT_IMG_TYPE_QVGA;

    // HVC camera angle setting
    private final int hvc_camera_angle = P2Def.HVC_CAM_ANGLE_0;
                                            // HVC_CAM_ANGLE_90
                                            // HVC_CAM_ANGLE_180
                                            // HVC_CAM_ANGLE_270

    // Threshold value settings
    private final int body_thresh = 500;         // Threshold for Human body detection [1 to 1000]
    private final int hand_thresh = 500;         // Threshold for Hand detection       [1 to 1000]
    private final int face_thresh = 500;         // Threshold for Face detection       [1 to 1000]
    private final int recognition_thresh = 500;  // Threshold for Recognition          [0 to 1000]

    // Detection size setings
    private final int min_body_size = 30;      // Mininum human body detection size [20 to 8192]
    private final int max_body_size = 8192;    // Maximum human body detection size [20 to 8192]
    private final int min_hand_size = 40;      // Mininum hand detection size       [20 to 8192]
    private final int max_hand_size = 8192;    // Maximum hand detection size       [20 to 8192]
    private final int min_face_size = 64;      // Mininum face detection size       [20 to 8192]
    private final int max_face_size = 8192;    // Maximum face detection size       [20 to 8192]

    // Detection face angle settings
    private final int face_angle_yaw  = P2Def.HVC_FACE_ANGLE_YAW_30;
                                           // HVC_FACE_ANGLE_YAW_60
                                           // HVC_FACE_ANGLE_YAW_90
    private final int face_angle_roll = P2Def.HVC_FACE_ANGLE_ROLL_15;
                                           // HVC_FACE_ANGLE_ROLL_45

    // -------------------------
    // STB library settings
    // -------------------------
    // Tracking parameters
    private final int max_retry_count = 2;          // Maximum tracking retry count            [0 to 30]
    private final int steadiness_param_pos = 30;    // Rectangle position steadiness parameter [0 to 100]
    private final int steadiness_param_size = 30;   // Rectangle size steadiness parameter     [0 to 100]

    // Steadiness parameters for Gender/Age estimation
    private final int pe_threshold_use = 300;  // Estimation result stabilizing threshold value
                                               //                                          [0 to 1000]
    private final int pe_min_UD_angle = -15;   // Minimum up-down angel threshold value    [-90 to 90]
    private final int pe_max_UD_angle = 20;    // Maxmum up-down angel threshold value     [-90 to 90]
    private final int pe_min_LR_angle = -30;   // Minimum left-right angel threshold value [-90 to 90]
    private final int pe_max_LR_angle = 30;    // Maxmum left-right angel threshold value  [-90 to 90]
    private final int pe_complete_frame_count = 5;
                                               // The number of previous frames applying to fix
                                               // stabilization.                           [1 to 20]

    // Steadiness parameters for Recognition
    private final int fr_threshold_use = 300;  // Recognition result stabilizing threshold value
                                               //                                          [0 to 1000]
    private final int fr_min_UD_angle = -15;   // Minimum up-down angel threshold value    [-90 to 90]
    private final int fr_max_UD_angle = 20;    // Maxmum up-down angel threshold value     [-90 to 90]
    private final int fr_min_LR_angle = -30;   // Minimum left-right angel threshold value [-90 to 90]
    private final int fr_max_LR_angle = 30;    // Maxmum left-right angel threshold value  [-90 to 90]
    private final int fr_complete_frame_count = 5;
                                               // The number of previous frames applying to fix
                                               // stabilization.                           [1 to 20]
    private final int fr_min_ratio = 60;       // Minimum account ratio in complete frame count.
                                               //                                          [0 to 100]

    private UsbManager manager;
    private SerialConnector connector;

    private HvcP2Api hvcP2Api;

    private TextView textView;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        Button bt = (Button) findViewById(R.id.buttonConnect);
        bt.setEnabled(true);
        bt.setClickable(true);
        bt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ( event.getAction() == MotionEvent.ACTION_DOWN ) {
                    onClickConnect(v);
                    return true;
                }
                return false;
            }
        });
        bt = (Button) findViewById(R.id.buttonExecute);
        bt.setEnabled(false);
        bt.setClickable(true);
        bt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ( event.getAction() == MotionEvent.ACTION_DOWN ) {
                    onClickExecute(v);
                    return true;
                }
                return false;
            }
        });
        bt = (Button) findViewById(R.id.buttonRegister);
        bt.setEnabled(false);
        bt.setClickable(true);
        bt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ( event.getAction() == MotionEvent.ACTION_DOWN ) {
                    onClickRegister(v);
                    return true;
                }
               return false;
            }
        });

        CheckBox cb = (CheckBox) findViewById(R.id.checkStabilization);
        cb.setEnabled(true);

        manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        connector = new SerialConnector(manager);
        hvcP2Api = new HvcP2Api(connector, exec_func, true);

        File folder = new File(Environment.getExternalStorageDirectory().getPath() + SAVE_DIR);
        if ( !folder.exists() ) {
            folder.mkdirs();
        }

        File file = new File(folder, logging_fname);
        if ( file.exists() ) {
            file.delete();
        }

        ExecuteThread execThread = new ExecuteThread();
        execThread.start();
    }

    private final int EXECUTE_END = 0;
    private final int EXECUTE_STOP = 1;
    private final int EXECUTE_START = 2;
    private final int EXECUTE_CONTINUE = 3;
    private final int EXECUTE_REGISTER1 = 4;
    private final int EXECUTE_REGISTER2 = 5;

    private int cmdExecute = EXECUTE_END;
    private boolean isConnect = false;

    public void onClickConnect(View view) {
        if ( cmdExecute != EXECUTE_END ) {
            return;
        }
        Button bt = (Button) findViewById(R.id.buttonConnect);
        Button btE = (Button) findViewById(R.id.buttonExecute);
        Button btR = (Button) findViewById(R.id.buttonRegister);
        if ( isConnect ) {
            hvcP2Api.disconnect();

            bt.setText(R.string.btnConnect);
            btE.setEnabled(false);
            btR.setEnabled(false);
            isConnect = false;

            loggingArray.clear();
            loggingFlash = false;
        } else {
            if ( !hvcP2Api.connect(timeout) ) {
                addText(connector.getStatus());
                addText("Serial port can not be connected!");
                return;
            }

            if ( checkConnection() ) {
                // Sets HVC-P2 parameters
                setP2Parameters();
                // Sets STB library parameters
                setStbParameters();
            }

            bt.setText(R.string.btnDisconnect);
            btE.setEnabled(true);
            btR.setEnabled(true);
            isConnect = true;
        }
    }

    public void onClickExecute(View view) {
        if ( !isConnect ) {
            return;
        }
        Button bt = (Button) findViewById(R.id.buttonExecute);
        Button btC = (Button) findViewById(R.id.buttonConnect);
        CheckBox cb = (CheckBox) findViewById(R.id.checkStabilization);
        if (cmdExecute == EXECUTE_END) {
            bt.setText(R.string.btnStop);
            btC.setEnabled(false);
            cb.setEnabled(false);

            hvcP2Api.useStabilizer(cb.isChecked());
            if ( hvcP2Api.isUsbStabilizer() == true ) {
                hvcP2Api.reset_tracking();
            }

            cmdExecute = EXECUTE_START;
        } else
        if (cmdExecute == EXECUTE_CONTINUE) {
            bt.setText(R.string.btnExecute);
            btC.setEnabled(true);
            cb.setEnabled(true);
            cmdExecute = EXECUTE_STOP;
        }
    }

    public void onClickRegister(View view) {
        if ( (cmdExecute == EXECUTE_END) || (cmdExecute == EXECUTE_STOP) ) {
            cmdExecute = EXECUTE_REGISTER1;
        } else
        if ( (cmdExecute == EXECUTE_CONTINUE) || (cmdExecute == EXECUTE_START) ) {
            cmdExecute = EXECUTE_REGISTER2;
        }
    }

    public void onCheckStabilization(View view) {
    }

    private void saveText(String data) {
        File folder = new File(Environment.getExternalStorageDirectory().getPath() + SAVE_DIR);
        File file = new File(folder, logging_fname);
        try {
            FileOutputStream out = new FileOutputStream(file,true);
            out.write(data.getBytes());
            out.flush();
            out.close();
        } catch(Exception e) {
        }

        String[] paths = {file.getPath()};
        String[] mimeTypes = {"text/plain"};
        MediaScannerConnection.scanFile(getApplicationContext(),
                paths,
                mimeTypes,
                null);
    }

    private void showToast(final String str) {
        // トースト表示
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if ( !loggingFlash ) {
                    String logText = "";
                    for (int i = 0; i < loggingArray.size(); i++) {
                        logText += loggingArray.get(i);
                    }
                    textView.setText(logText);
                    loggingFlash = true;

                    // Scroll in order to see added part
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                    scrollView.invalidate();
                }
            }
        });
    }
    private void addText(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                saveText(text + "\n");

                loggingArray.add(0,text + "\n");
                if ( loggingArray.size() > 100 ) {
                    loggingArray.remove(loggingArray.size() - 1);
                }
                loggingFlash = false;
            }
        });
    }

    private boolean checkConnection() {
        String[] hvc_type = new String[1];
        int[] major = new int[1];
        int[] minor = new int[1];
        int[] release = new int[1];
        int[] rev = new int[1];
        int res_code = hvcP2Api.get_version(hvc_type, major, minor, release, rev);
        if ( (res_code == P2Def.RESPONSE_CODE_NORMAL) &&
                (hvc_type[0] != null) && (hvc_type[0].startsWith("B5T-007001")) ) {
            addText(hvc_type[0].substring(0,10) + "_" + Integer.toString(major[0]) + "_" +
                                                         Integer.toString(minor[0]) + "_" +
                                                         Integer.toString(release[0]) + "_" +
                                                         Integer.toString(rev[0]));
            return true;
        } else {
            addText("Error: connection failure.");
            return false;
        }
    }

    private boolean setP2Parameters() {
        // Sets camera angle
        int res_code = hvcP2Api.set_camera_angle(hvc_camera_angle);
        if (res_code != P2Def.RESPONSE_CODE_NORMAL ) {
            addText("Error: Invalid camera angle.");
            return false;
        }

        // Sets threshold
        res_code = hvcP2Api.set_threshold(body_thresh, hand_thresh,
                                            face_thresh, recognition_thresh);
        if (res_code != P2Def.RESPONSE_CODE_NORMAL ) {
            addText("Error: Invalid threshold.");
            return false;
        }

        // Sets detection size
        res_code = hvcP2Api.set_detection_size(min_body_size, max_body_size,
                                                 min_hand_size, max_hand_size,
                                                 min_face_size, max_face_size);
        if (res_code != P2Def.RESPONSE_CODE_NORMAL ) {
            addText("Error: Invalid detection size.");
            return false;
        }

        // Sets face angle
        res_code = hvcP2Api.set_face_angle(face_angle_yaw, face_angle_roll);
        if (res_code != P2Def.RESPONSE_CODE_NORMAL ) {
            addText("Error: Invalid face angle.");
            return false;
        }

        return true;
    }

    private boolean setStbParameters() {
        if ( hvcP2Api.isUsbStabilizer() != true ) {
            return true;
        }

        // Sets tracking retry count.
        int ret = hvcP2Api.set_stb_tr_retry_count(max_retry_count);
        if ( ret != 0 ) {
            addText("Error: Invalid parameter. set_stb_tr_retry_count().");
            return false;
        }

        // Sets steadiness parameters
        ret = hvcP2Api.set_stb_tr_steadiness_param(steadiness_param_pos,
                                                     steadiness_param_size);
        if ( ret != 0 ) {
            addText("Error: Invalid parameter. set_stb_tr_steadiness_param().");
            return false;
        }

        // --Sets STB parameters for Gender / Age estimation
        // Sets estimation result stabilizing threshold value
        ret = hvcP2Api.set_stb_pe_threshold_use(pe_threshold_use);
        if ( ret != 0 ) {
            addText("Error: Invalid parameter. set_stb_pe_threshold_use().");
            return false;
        }

        // Sets estimation result stabilizing angle
        ret = hvcP2Api.set_stb_pe_angle_use(pe_min_UD_angle, pe_max_UD_angle,
                                              pe_min_LR_angle, pe_max_UD_angle);
        if ( ret != 0 ) {
            addText("Error: Invalid parameter. set_stb_pe_angle_use().");
            return false;
        }

        // Sets age/gender estimation complete frame count
        ret = hvcP2Api.set_stb_pe_complete_frame_count(pe_complete_frame_count);
        if ( ret != 0 ) {
            addText("Error: Invalid parameter. set_stb_pe_complete_frame_count().");
            return false;
        }

        // --Sets STB parameters for Recognition
        // Sets recognition stabilizing threshold value
        ret = hvcP2Api.set_stb_fr_threshold_use(fr_threshold_use);
        if ( ret != 0 ) {
            addText("Error: Invalid parameter. set_stb_fr_threshold_use().");
            return false;
        }

        // Sets recognition stabilizing angle
        ret = hvcP2Api.set_stb_fr_angle_use(fr_min_UD_angle, fr_max_UD_angle,
                                              fr_min_LR_angle, fr_max_LR_angle);
        if ( ret != 0 ) {
            addText("Error: Invalid parameter. set_stb_fr_angle_use().");
            return false;
        }

        // Sets recognition stabilizing complete frame count
        ret = hvcP2Api.set_stb_fr_complete_frame_count(fr_complete_frame_count);
        if ( ret != 0 ) {
            addText("Error: Invalid parameter. set_stb_fr_complete_frame_count().");
            return false;
        }

        // Sets recognition minimum account ratio
        ret = hvcP2Api.set_stb_fr_min_ratio(fr_min_ratio);
        if ( ret != 0 ) {
            addText("Error: Invalid parameter. set_stb_fr_min_ratio().");
            return false;
        }

        return true;
    }

    private class ExecuteThread extends Thread {
        @Override
        public void run() {
            int res_code;
            int[] stb_ret = new int[1];
            int user_id = 0;
            int data_id = 0;
            long startTime;
            long stopTime;
            HvcTrackingResult hvc_tracking_result = new HvcTrackingResult();
            GrayscaleImage img = new GrayscaleImage(getApplicationContext());

            while (true) {
                do {
                    refreshView();
                    wait(100);
                } while(!loggingFlash);
                switch (cmdExecute) {
                    case EXECUTE_REGISTER1:
                    case EXECUTE_REGISTER2:
                        res_code = hvcP2Api.register_data(user_id, data_id, img);
                        if ( res_code < P2Def.RESPONSE_CODE_NORMAL ) { // error
                            addText("Error: Invalid register album.");
                            break;
                        }
                        if ( res_code == P2Def.RESPONSE_CODE_NO_FACE ) {
                            addText("Failed to register.\nNumber of faces that can be registered is 0.");
                        }
                        if ( res_code == P2Def.RESPONSE_CODE_PLURAL_FACE ) {
                            addText("Failed to register.\nNumber of detected faces is 2 or more.");
                        }
                        if ( res_code == P2Def.RESPONSE_CODE_NORMAL ) { // success
                            img.save(SAVE_DIR, registerd_img_fname);
                            addText("Success to register.\nuser_id=" + Integer.toString(user_id) +
                                                          "  data_id=" + Integer.toString(data_id));
                        }

                        // Saves album to flash ROM on B5T-007001.
                        res_code = hvcP2Api.save_album_to_flash();
                        if ( res_code != P2Def.RESPONSE_CODE_NORMAL ) {
                            addText("Error: Invalid save album to flash.");
                        }

                        if ( cmdExecute == EXECUTE_REGISTER1 ) {
                            cmdExecute = EXECUTE_STOP;
                        } else {
                            cmdExecute = EXECUTE_START;
                        }
                        break;
                    case EXECUTE_END:
                        break;
                    case EXECUTE_STOP:
                        cmdExecute = EXECUTE_END;
                        break;
                    case EXECUTE_START:
                        cmdExecute = EXECUTE_CONTINUE;
                        break;
                    case EXECUTE_CONTINUE:
                        startTime = System.currentTimeMillis();
                        res_code = hvcP2Api.execute(output_img_type, hvc_tracking_result, img, stb_ret);
                        stopTime = System.currentTimeMillis();

                        if ( res_code == P2Def.RESPONSE_CODE_NORMAL ) {
                            if (output_img_type != P2Def.OUT_IMG_TYPE_NONE) {
                                img.save(SAVE_DIR, exec_img_fname);
                            }
                            addText("==== Elapsed time:" + Long.toString(stopTime - startTime) + "[msec] ====\n" +
                                    hvc_tracking_result.getString(hvcP2Api.isUsbStabilizer()));
                        } else {
                            addText("Error: execute failure.\n");
                        }
                        break;
                }
            }
        }

        public void wait(int nWaitCount)
        {
            do {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                }
                nWaitCount--;
            } while ( nWaitCount > 0 );
        }
    }
}
