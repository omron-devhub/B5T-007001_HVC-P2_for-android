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

import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

/*
 * Serial connector class
 */
public class SerialConnector extends Connector {
    static final int VENDOR_ID = 1424;
    static final int PRODUCT_ID = 202;
    static final int DEFALUT_TIMEOUT = 3 * 1000;

    private String strStatus = null;
    private byte[] rcvBuffer = null;
    private int rcvCount = 0;
    private int rcvOffset = 0;

    /**
     * USB
     */
    private UsbManager usbManager = null;
    private UsbDevice usbDevice = null;
    private UsbDeviceConnection usbConnection = null;

    private UsbEndpoint readEndpoint = null;
    private UsbEndpoint writeEndpoint = null;

    private int cntTimeout = DEFALUT_TIMEOUT;
    private boolean isConnected = false;

    public SerialConnector(UsbManager manager) {
        super();
        usbManager = manager;
        isConnected = false;
    }

    public String getStatus() {
        return strStatus;
    }

    boolean connect(int timeout) {
        if ( usbConnection == null ) {
            // Gets USB Device
            HashMap<String, UsbDevice> map = usbManager.getDeviceList();
            Iterator<UsbDevice> it = map.values().iterator();
            while(it.hasNext()) {
                usbDevice = it.next();
                if ( (usbDevice.getVendorId() == VENDOR_ID) && (usbDevice.getProductId() == PRODUCT_ID) ) {
                    usbConnection = usbManager.openDevice(usbDevice);
                    break;
                }
            }
        }
        if ( usbConnection == null ) {
            strStatus = new String("Can't get usbConnection !!\n");
            return false;
        }

        for (int n = 0; n < usbDevice.getInterfaceCount(); n++) {
            UsbInterface usbInterface = usbDevice.getInterface(n);
            if (!usbConnection.claimInterface(usbInterface, true)) {
                continue;
            }
            for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
                UsbEndpoint endpoint = usbInterface.getEndpoint(i);
                int dir = endpoint.getDirection();
                int type = endpoint.getType();
                if (readEndpoint == null && dir == UsbConstants.USB_DIR_IN && type == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                    readEndpoint = endpoint;
                }
                if (writeEndpoint == null && dir == UsbConstants.USB_DIR_OUT && type == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                    writeEndpoint = endpoint;
                }
            }
            if ( readEndpoint != null && writeEndpoint != null) {
                break;
            }
        }
        if ( readEndpoint == null || writeEndpoint == null) {
            strStatus = new String("Can't get endPoint !!\n");
            return false;
        }

        strStatus = new String("Get usbDevice !!\n");
        strStatus += "Physical Device : " + usbDevice.getDeviceName() + "\n";
        strStatus += "Device Attached : VID_" + usbDevice.getVendorId() + " PID_" + usbDevice.getProductId() + "\n";
        strStatus += "Read endpoint direction: " + readEndpoint.getDirection() + "\n";
        strStatus += "Write endpoint direction: " + writeEndpoint.getDirection() + "\n";

        rcvBuffer = new byte[readEndpoint.getMaxPacketSize() * 256];

        cntTimeout = timeout * 1000;
        isConnected = true;
        return true;
    }

    boolean disconnect() {
        usbConnection = null;
        readEndpoint = null;
        writeEndpoint = null;
        isConnected = false;
        return true;
    }

    private void readBulk() {
        int numBytesRead = usbConnection.bulkTransfer(readEndpoint, rcvBuffer, rcvBuffer.length, cntTimeout);
        strStatus += "  readBulk() : numBytesRead = " + Integer.toString(numBytesRead) + "\n";
        rcvCount = numBytesRead;
        rcvOffset = 0;
    }

    void clear_receive_buffer() {
        if ( !isConnected ) {
            strStatus = "Serial port has not connected yet!\n";
            return;
        }

        strStatus = "clear_receive_buffer()\n";

        int numBytesRead = 0;
        do {
            numBytesRead = usbConnection.bulkTransfer(readEndpoint, rcvBuffer, rcvBuffer.length, 10);
            strStatus += "  readBulk() : numBytesRead = " + Integer.toString(numBytesRead) + "\n";
        } while(numBytesRead >= 0);
        rcvCount = 0;
        rcvOffset = 0;
    }

    byte[] receive_data(int read_byte_size) {
        if ( !isConnected ) {
            strStatus = "Serial port has not connected yet!\n";
            return null;
        }

        strStatus = "receive_data()\n";

        int offset = 0;
        byte[] buf = new byte[read_byte_size];
        do {
            int i;
            for (i = 0; (i < read_byte_size) && (i < rcvCount); i++) {
                buf[offset++] = rcvBuffer[rcvOffset++];
            }
            rcvCount -= i;
            read_byte_size -= i;
            if ( read_byte_size <= 0 ) {
                break;
            }

            long startTime = System.currentTimeMillis();
            while ( true ) {
                long stopTime = System.currentTimeMillis();
                if ( (stopTime - startTime) > 1 ) break;
            }

            readBulk();
            if (rcvCount < 0) {
                if ( offset <= 0 ) {
                    buf = null;
                } else {
                    byte[] rcv = Arrays.copyOf(buf, offset); // Partial copy of array
                    buf = rcv;
                }
                break;
            }
        } while ( true );
        return buf;
    }

    private int writeBulk(byte[] data, int offset) {
        int numBytesWrite = usbConnection.bulkTransfer(writeEndpoint, data, offset, data.length, cntTimeout);
        strStatus += "  writeBulk() : numBytesWrite = " + Integer.toString(numBytesWrite) + "\n";
        return numBytesWrite;
    }

    boolean send_data(byte[] data) {
        if ( !isConnected ) {
            strStatus = "Serial port has not connected yet!\n";
            return false;
        }

        strStatus = "send_data()\n";

        int offset = 0;
        do {
            long startTime = System.currentTimeMillis();
            while ( true ) {
                long stopTime = System.currentTimeMillis();
                if ( (stopTime - startTime) > 1 ) break;
            }
            int cnt = writeBulk(data, offset);
            if ( cnt < 0 ) {
                break;
            }
            offset += cnt;
        } while(offset < data.length);
        return (offset == data.length);
    }
}
