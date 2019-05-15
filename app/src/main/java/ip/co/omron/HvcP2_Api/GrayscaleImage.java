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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

/*
 * 8 bit grayscale image.
 */
public class GrayscaleImage {
    private Context context;
    private String strStatus = null;

    int width;
    int height;
    byte[] data;

    public GrayscaleImage(Context cont) {
        width = 0;
        height = 0;
        data = null;

        context = cont;
    }

    public String getStatus() {
        return strStatus;
    }

    public boolean save(String dir, String fname) {
        // if no data, no save.
        if ( (width == 0) || (height == 0) || (data == null) ) return false;

        File folder = new File(Environment.getExternalStorageDirectory().getPath() + dir);

        File file = new File(folder, fname);
        try {
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream out = new FileOutputStream(file);

            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            for (int i = 0; i < data.length; i++) {
                int gray_data = data[i] & 0xFF;
                int gray_argb = Color.argb(255, gray_data, gray_data, gray_data);
                bmp.setPixel(i%width, i/width, gray_argb);
            }

            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);

            out.flush();
            out.close();
        } catch(Exception e) {
            strStatus += "Exception " + e.getMessage() + "!!\n";
            return false;
        }

        String[] paths = {file.getPath()};
        String[] mimeTypes = {"image/jpeg"};
        MediaScannerConnection.scanFile(context,
                paths,
                mimeTypes,
                null);
        return true;
    }
}
