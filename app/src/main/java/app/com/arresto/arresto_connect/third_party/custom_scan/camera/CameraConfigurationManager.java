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
import android.hardware.Camera.Area;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Build.VERSION;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CameraConfigurationManager {
    private static final int MAX_SUPPORT_PREVIEW_SIZE = 2073600;
    private static final int MIN_SUPPORT_PREVIEW_SIZE = 307200;
    private Point cameraResolution;
    private final Context context;
    private Point screenResolution;

    CameraConfigurationManager(Context context) {
        this.context = context;
    }

    void initFromCameraParameters(Camera camera, SurfaceHolder surfaceHolder) {
        Parameters parameters = camera.getParameters();
        Point point = new Point();
        Point point2 = new Point();
        point.x = 0;
        if (surfaceHolder != null) {
            Rect surfaceFrame = surfaceHolder.getSurfaceFrame();
            point.x = surfaceFrame.width();
            point.y = surfaceFrame.height();
        }
        ((WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(point2);
        if (point.x == 0) {
            point.x = point2.x;
            point.y = point2.y;
        }
        this.screenResolution = point;
        this.cameraResolution = findBestPreviewSizeValue(parameters);
    }

    void setDesiredCameraParameters(Camera camera) {
        Parameters parameters = camera.getParameters();
        if (parameters != null) {
            try {
                String findSettableValue = findSettableValue(parameters.getSupportedFocusModes(), "auto", "macro");
                if (findSettableValue != null) {
                    parameters.setFocusMode(findSettableValue);
                }
                setFocusArea(parameters);
                camera.setParameters(parameters);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                parameters = camera.getParameters();
                parameters.setPreviewSize(this.cameraResolution.x, this.cameraResolution.y);
                camera.setParameters(parameters);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            Size previewSize = camera.getParameters().getPreviewSize();
            if (!(previewSize == null || (this.cameraResolution.x == previewSize.width && this.cameraResolution.y == previewSize.height))) {
                this.cameraResolution.x = previewSize.width;
                this.cameraResolution.y = previewSize.height;
            }
        }
    }

    Point getCameraResolution() {
        return this.cameraResolution;
    }

    public Point getScreenResolution() {
        return this.screenResolution;
    }

    public void setTorch(Camera camera, boolean z) {
        Parameters parameters = camera.getParameters();
        doSetTorch(parameters, z);
        camera.setParameters(parameters);
    }

    private static void initializeTorch(Parameters parameters) {
        doSetTorch(parameters, true);
    }

    private static void doSetTorch(Parameters parameters, boolean z) {
        String findSettableValue;
        if (z) {
            findSettableValue = findSettableValue(parameters.getSupportedFlashModes(), "torch", "on");
        } else {
            findSettableValue = findSettableValue(parameters.getSupportedFlashModes(), "off");
        }
        if (findSettableValue != null) {
            parameters.setFlashMode(findSettableValue);
        }
    }

    private Point findBestPreviewSizeValue(Parameters parameters) {
        Point point = new Point();
        ((WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(point);
        double d = (double) point.x;
        double d2 = (double) point.y;
        Double.isNaN(d);
        Double.isNaN(d2);
        d /= d2;
        if (point.x > point.y) {
            d = (double) point.y;
            d2 = (double) point.x;
            Double.isNaN(d);
            Double.isNaN(d2);
            d /= d2;
        }
        Size previewSize = parameters.getPreviewSize();
        int i = previewSize.width;
        int i2 = previewSize.height;
        Object obj = null;
        try {
            for (Size size : parameters.getSupportedPreviewSizes()) {
                if (size.height * size.width <= MAX_SUPPORT_PREVIEW_SIZE) {
                    if (size.height * size.width >= MIN_SUPPORT_PREVIEW_SIZE) {
                        double d3 = (double) size.width;
                        double d4 = (double) size.height;
                        Double.isNaN(d3);
                        Double.isNaN(d4);
                        d3 /= d4;
                        if (size.width > size.height) {
                            d3 = (double) size.height;
                            d4 = (double) size.width;
                            Double.isNaN(d3);
                            Double.isNaN(d4);
                            d3 /= d4;
                        }
                        if (d3 == d) {
                            int i3 = i * i2;
                            if (size.height * size.width > i3) {
                                i = size.width;
                                i2 = size.height;
                            } else if (MAX_SUPPORT_PREVIEW_SIZE < i3) {
                                i = size.width;
                                i2 = size.height;
                            }
                            obj = 1;
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
        if (obj != null) {
            return new Point(i, i2);
        }
        try {
            for (Size size2 : parameters.getSupportedPreviewSizes()) {
                if (size2.height * size2.width <= MAX_SUPPORT_PREVIEW_SIZE) {
                    if (size2.height * size2.width >= MIN_SUPPORT_PREVIEW_SIZE) {
                        int i4 = i * i2;
                        if (size2.height * size2.width > i4) {
                            i = size2.width;
                            i2 = size2.height;
                        } else if (MAX_SUPPORT_PREVIEW_SIZE < i4) {
                            i = size2.width;
                            i2 = size2.height;
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return new Point(i, i2);
    }

    private static String findSettableValue(Collection<String> collection, String... strArr) {
        if (collection != null) {
            for (String str : strArr) {
                if (collection.contains(str)) {
                    return str;
                }
            }
        }
        return null;
    }

    private static List<Area> buildMiddleArea(int i) {
        int i2 = -i;
        return Collections.singletonList(new Area(new Rect(i2, i2, i, i), 1));
    }

    private static void setFocusArea(Parameters parameters) {
        if (VERSION.SDK_INT >= 19 && parameters.getMaxNumFocusAreas() > 0) {
            parameters.setFocusAreas(buildMiddleArea(300));
        }
        if (VERSION.SDK_INT >= 19 && parameters.getMaxNumMeteringAreas() > 0) {
            parameters.setMeteringAreas(buildMiddleArea(100));
        }
    }

    static void setFocusArea300(Parameters parameters) {
        if (VERSION.SDK_INT >= 19 && parameters.getMaxNumFocusAreas() > 0) {
            parameters.setFocusAreas(buildMiddleArea(300));
        }
        if (VERSION.SDK_INT >= 19 && parameters.getMaxNumMeteringAreas() > 0) {
            parameters.setMeteringAreas(buildMiddleArea(100));
        }
    }

    static void setFocusArea150(Parameters parameters) {
        if (VERSION.SDK_INT >= 19 && parameters.getMaxNumFocusAreas() > 0) {
            parameters.setFocusAreas(buildMiddleArea(150));
        }
        if (VERSION.SDK_INT >= 19 && parameters.getMaxNumMeteringAreas() > 0) {
            parameters.setMeteringAreas(buildMiddleArea(100));
        }
    }
}
