/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android.decode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.client.android.camera.CameraManager;
import com.google.zxing.client.android.decode.DecodeThread;
import com.google.zxing.client.android.decode.ID;

import java.util.Collection;
import java.util.Map;


/**
 * This class handles all the messaging which comprises the state machine for capture.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ScanHandler extends Handler {

    private final OnResultListener listener;
    private final DecodeThread decodeThread;
    private State state;
    private final CameraManager cameraManager;

    private enum State {
        PREVIEW,
        SUCCESS,
        DONE
    }

    public ScanHandler(OnResultListener listener,
                       int barcodeType,
                       Map<DecodeHintType, ?> baseHints,
                       String characterSet,
                       CameraManager cameraManager, ResultPointCallback resultPointCallback) {
        this.listener = listener;
        // Start ourselves capturing previews and decoding.
        this.cameraManager = cameraManager;
        decodeThread = new DecodeThread(cameraManager,
                this, barcodeType, baseHints, characterSet, resultPointCallback);
        decodeThread.start();
        state = State.SUCCESS;
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case ID.restart_preview:
                restartPreviewAndDecode();
                break;
            case ID.decode_succeeded:
                state = State.SUCCESS;
                Bundle bundle = message.getData();
                Bitmap barcode = null;
                float scaleFactor = 1.0f;
                if (bundle != null) {
                    byte[] compressedBitmap = bundle.getByteArray(DecodeThread.BARCODE_BITMAP);
                    if (compressedBitmap != null) {
                        barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.length, null);
                        // Mutable copy:
                        barcode = barcode.copy(Bitmap.Config.ARGB_8888, true);
                    }
                    scaleFactor = bundle.getFloat(DecodeThread.BARCODE_SCALED_FACTOR);
                }
                if (listener != null)
                    listener.onResult((Result) message.obj, barcode, scaleFactor);
                break;
            case ID.decode_failed:
                // We're decoding as fast as possible, so when one decode fails, start another.
                state = State.PREVIEW;
                cameraManager.requestPreviewFrame(decodeThread.getHandler(), ID.decode);
                break;
        }
    }

    public void quitSynchronously() {
        state = State.DONE;
        cameraManager.stopPreview();
        Message quit = Message.obtain(decodeThread.getHandler(), ID.quit);
        quit.sendToTarget();
        try {
            // Wait at most half a second; should be enough time, and onPause() will timeout quickly
            decodeThread.join(500L);
        } catch (InterruptedException e) {
            // continue
        }
        // Be absolutely sure we don't send any queued up messages
        removeMessages(ID.decode_succeeded);
        removeMessages(ID.decode_failed);
    }

    private void restartPreviewAndDecode() {
        if (state == State.SUCCESS) {
            state = State.PREVIEW;
            cameraManager.requestPreviewFrame(decodeThread.getHandler(), ID.decode);
        }
    }

    /**
     * 重新开始扫描
     */
    public void restartScan() {
        sendEmptyMessage(ID.restart_preview);
    }

    /**
     * 一段时间后重新你开始扫描
     *
     * @param delay 时间
     */
    public void restartScanDelay(long delay) {
        sendEmptyMessageDelayed(ID.restart_preview, delay);
    }

    public interface OnResultListener {
        void onResult(Result result, Bitmap barcode, float scaleFactor);
    }

}
