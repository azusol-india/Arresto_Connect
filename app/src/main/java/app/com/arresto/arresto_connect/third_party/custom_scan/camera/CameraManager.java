/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.third_party.custom_scan.camera;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import com.google.zxing.PlanarYUVLuminanceSource;

import java.io.IOException;

public class CameraManager {
    private final AutoFocusCallback autoFocusCallback = new AutoFocusCallback();
    private Camera camera;
    private CameraConfigurationManager configManager;
    private boolean flSupport = true;
    private int focusCount;
    private Rect framingRect;
    private Rect framingRectInPreview;
    private boolean initialized;
    private PreviewCallback previewCallback;
    private boolean previewing;
    private int requestedFramingRectHeight;
    private int requestedFramingRectWidth;

    public void openDriver(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        if (camera != null) {
            if (previewing) {
                camera.stopPreview();
                previewing = false;
            }
            try {
                camera.setPreviewDisplay(holder);
                configManager.initFromCameraParameters(camera, holder);
                previewCallback = new PreviewCallback(configManager);
                configManager.setDesiredCameraParameters(camera);
                setManualFramingRect(requestedFramingRectWidth, requestedFramingRectHeight);
                camera.autoFocus(autoFocusCallback);
                initialized = true;
            } catch (Exception e) {
                Log.d("teg ", "Error starting camera preview: " + e.getMessage());
            }
        }
    }


    public CameraManager(Context context) {
        configManager = new CameraConfigurationManager(context);
    }

    public CameraConfigurationManager getConfigurationManager() {
        return configManager;
    }

    public Camera getCamera() {
        return camera;
    }

    public boolean getFlashlightSupport() {
        return flSupport;
    }

    public void openCameraFacingBack() throws IOException {
        if (camera == null) {
            camera = Camera.open(0);
            if (camera == null) {
                throw new IOException();
            }
        }
    }

    public void closeDriver() {
        if (camera != null) {
            camera.release();
            camera = null;
            framingRect = null;
            framingRectInPreview = null;
        }
    }

    public void startPreview() {
        if (camera != null && !previewing) {
            int cameraDegrees = 90;
            camera.setDisplayOrientation(cameraDegrees);
            camera.startPreview();
            previewing = true;
        }
    }

    public void stopPreview() {
        if (camera != null && previewing) {
            try {
                camera.stopPreview();
            } catch (Exception ignored) {
            }
            previewCallback.setHandler(null, 0);
            autoFocusCallback.setHandler(null, 0);
            previewing = false;
        }
    }

    public void requestPreviewFrame(Handler handler, int i) {
        if (camera != null && previewing && handler != null) {
            if (previewCallback != null) {
                previewCallback.setHandler(handler, i);
                try {
                    camera.setOneShotPreviewCallback(previewCallback);
                } catch (Exception ignored) {
                }
            }
        }
    }

    public void requestAutoFocus(Handler handler, int i) {
        if (camera != null && previewing) {
            try {
                if (configManager != null) {
                    Parameters parameters = camera.getParameters();
                    if (parameters != null && focusCount == 0) {
                        focusCount = 1;
                        CameraConfigurationManager.setFocusArea150(parameters);
                        camera.setParameters(parameters);
                    } else if (parameters != null && focusCount == 1) {
                        focusCount = 0;
                        CameraConfigurationManager.setFocusArea300(parameters);
                        camera.setParameters(parameters);
                    }
                }
                autoFocusCallback.setHandler(handler, i);
                camera.autoFocus(autoFocusCallback);
            } catch (Exception ignored) {
            }
        }
    }

    public Rect getFramingRect() {
        if (framingRect == null) {
            CameraConfigurationManager cameraConfigurationManager = configManager;
            if (cameraConfigurationManager == null || cameraConfigurationManager.getScreenResolution() == null) {
                return new Rect(0, 0, 50, 50);
            }
            Point screenResolution = configManager.getScreenResolution();
            int i = screenResolution.x / 2;
            int i2 = screenResolution.y / 2;
            if (i > i2 || i2 <= i) {
                i = i2;
            }
            i2 = (screenResolution.x * 3) / 4;
            int i3 = (screenResolution.x - i2) / 2;
            int i4 = (screenResolution.y - i) / 2;
            framingRect = new Rect(i3, i4, i2 + i3, i + i4);
        }
        return framingRect;
    }

    private Rect getFramingRectInPreview() {
        if (framingRectInPreview == null) {
            Rect framingRect = getFramingRect();
            if (framingRect == null) {
                return null;
            }
            Rect rect = new Rect(framingRect);
            Point cameraResolution = configManager.getCameraResolution();
            Point screenResolution = configManager.getScreenResolution();
            if (cameraResolution == null || screenResolution == null) {
                return null;
            }
            if (screenResolution.x < screenResolution.y) {
                rect.left = (rect.left * cameraResolution.y) / screenResolution.x;
                rect.right = (rect.right * cameraResolution.y) / screenResolution.x;
                rect.top = (rect.top * cameraResolution.x) / screenResolution.y;
                rect.bottom = (rect.bottom * cameraResolution.x) / screenResolution.y;
            } else {
                rect.left = (rect.left * cameraResolution.x) / screenResolution.x;
                rect.right = (rect.right * cameraResolution.x) / screenResolution.x;
                rect.top = (rect.top * cameraResolution.y) / screenResolution.y;
                rect.bottom = (rect.bottom * cameraResolution.y) / screenResolution.y;
            }
            framingRectInPreview = rect;
        }
        return framingRectInPreview;
    }

    private void setManualFramingRect(int i, int i2) {
        if (initialized) {
            Point screenResolution = configManager.getScreenResolution();
            if (i > screenResolution.x) {
                i = screenResolution.x;
            }
            if (i2 > screenResolution.y) {
                i2 = screenResolution.y;
            }
            int i3 = (screenResolution.x - i) / 2;
            int i4 = (screenResolution.y - i2) / 2;
            framingRect = new Rect(i3, i4, i + i3, i2 + i4);
            framingRectInPreview = null;
            return;
        }
        requestedFramingRectWidth = i;
        requestedFramingRectHeight = i2;
    }

    public PlanarYUVLuminanceSource buildLuminanceSource(byte[] bArr, int i, int i2) {
        Rect framingRectInPreview = getFramingRectInPreview();
        if (framingRectInPreview == null) {
            return null;
        }
        return new PlanarYUVLuminanceSource(bArr, i, i2, framingRectInPreview.left, framingRectInPreview.top, framingRectInPreview.width(), framingRectInPreview.height(), false);
    }

    public PlanarYUVLuminanceSource buildLuminanceSourceRotation(byte[] bArr, int i, int i2) {
        Rect framingRectInPreview = getFramingRectInPreview();
        if (framingRectInPreview == null) {
            return null;
        }
        return new PlanarYUVLuminanceSource(bArr, i, i2, framingRectInPreview.top, framingRectInPreview.left, framingRectInPreview.height(), framingRectInPreview.width(), false);
    }

}