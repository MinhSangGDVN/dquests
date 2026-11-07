package com.minhsanggd.dquests;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.util.LruCache;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class DquestsCore {

    public static final String APP_VERSION = "1.0";
    public static final String API_BASE = "https://discord.com/api/v9";
    public static final String VERSION_CHECK_URL = "https://raw.githubusercontent.com/MinhSangGDVN/dquests/refs/heads/main/assets/version.txt";
    public static final String RELEASES_URL = "https://github.com/MinhSangGDVN/dquests/releases";
    public static final String GITHUB_PROFILE_URL = "https://github.com/MinhSangGDVN";
    public static final String SOURCE_CODE_URL = "https://github.com/MinhSangGDVN/dquests";
    public static final String TERMS_PRIVACY_URL = "https://github.com/MinhSangGDVN/dquests/blob/main/assets/TAP.md";
        public static final String BADGE_BRAVERY_URL = "https://raw.githubusercontent.com/MinhSangGDVN/dquests/refs/heads/main/assets/hype-squad-bravery.png";
    public static final String BADGE_BRILLIANCE_URL = "https://raw.githubusercontent.com/MinhSangGDVN/dquests/refs/heads/main/assets/hype-squad-brilliance.png";
    public static final String BADGE_BALANCE_URL = "https://raw.githubusercontent.com/MinhSangGDVN/dquests/refs/heads/main/assets/hype-squad-balance.png";

    public static final String[] SUPPORTED_TASKS = new String[]{
            "WATCH_VIDEO",
            "PLAY_ON_DESKTOP",
            "STREAM_ON_DESKTOP",
            "PLAY_ACTIVITY",
            "WATCH_VIDEO_ON_MOBILE"
    };

    public static class I18n {
        public static boolean isEn = false;

        public static String t(String vi, String en) {
            return isEn ? en : vi;
        }
    }

    public static class AccountProfile {
        public String userId = "";
        public String username = "";
        public String email = "";
        public String password = "";
        public String token = "";
        public String avatarUrl = "";
        public boolean tokenExpired = false;
        public boolean loggedIn = false;

        public int maxWorkers = 2;
        public int speedMode = 1;
        public String presenceStatus = "none"; 
        public RpcConfig rpcConfig = new RpcConfig();

        public JSONObject toJson() {
            JSONObject o = new JSONObject();
            try {
                o.put("userId", userId);
                o.put("username", username);
                o.put("email", email);
                o.put("password", password);
                o.put("token", token);
                o.put("avatarUrl", avatarUrl);
                o.put("tokenExpired", tokenExpired);
                o.put("maxWorkers", maxWorkers);
                o.put("speedMode", speedMode);
                o.put("presenceStatus", presenceStatus);
                o.put("rpcConfig", rpcConfig.toJson());
            } catch (Exception ignored) {
            }
            return o;
        }

        public static AccountProfile fromJson(JSONObject o) {
            AccountProfile p = new AccountProfile();
            if (o == null) return p;
            p.userId = o.optString("userId", "");
            p.username = o.optString("username", "User");
            p.email = o.optString("email", "");
            p.password = o.optString("password", "");
            p.token = o.optString("token", "");
            p.avatarUrl = o.optString("avatarUrl", "");
            p.tokenExpired = o.optBoolean("tokenExpired", false);
            p.maxWorkers = o.optInt("maxWorkers", 2);
            p.speedMode = o.optInt("speedMode", 1);
            p.presenceStatus = o.optString("presenceStatus", "none");
            p.rpcConfig = RpcConfig.fromJson(o.optJSONObject("rpcConfig"));
            return p;
        }
    }

    public static class RpcConfig {
        public boolean enabled = false;
        public int activityType = 0; 
        public String applicationId = "";
        public String name = "";
        public String details = "";
        public String state = "";
        public String streamUrl = "";
        public String largeImage = "";
        public String largeText = "";
        public String button1Label = "";
        public String button1Url = "";
        public int timestampMode = 1; 

        public JSONObject toJson() {
            JSONObject o = new JSONObject();
            try {
                o.put("enabled", enabled);
                o.put("activityType", activityType);
                o.put("applicationId", applicationId);
                o.put("name", name);
                o.put("details", details);
                o.put("state", state);
                o.put("streamUrl", streamUrl);
                o.put("largeImage", largeImage);
                o.put("largeText", largeText);
                o.put("button1Label", button1Label);
                o.put("button1Url", button1Url);
                o.put("timestampMode", timestampMode);
            } catch (Exception ignored) {
            }
            return o;
        }

        public static RpcConfig fromJson(JSONObject o) {
            RpcConfig c = new RpcConfig();
            if (o == null) return c;
            c.enabled = o.optBoolean("enabled", false);
            c.activityType = o.optInt("activityType", 0);
            c.applicationId = o.optString("applicationId", "");
            c.name = o.optString("name", "");
            c.details = o.optString("details", "");
            c.state = o.optString("state", "");
            c.streamUrl = o.optString("streamUrl", "");
            c.largeImage = o.optString("largeImage", "");
            c.largeText = o.optString("largeText", "");
            c.button1Label = o.optString("button1Label", "");
            c.button1Url = o.optString("button1Url", "");
            c.timestampMode = o.optInt("timestampMode", 1);
            return c;
        }
    }

    public static class QuestItem {
        public String id;
        public String title;
        public String subtitle;
        public String reward;
        public String taskType;
        public String imageUrl;
        public int targetSeconds;
        public double doneSeconds;
        public boolean enrolled;
        public boolean completed;
        public boolean valid;
        public JSONObject rawJson;
    }

    public static class HttpResponse {
        public int statusCode;
        public String body;

        public HttpResponse(int statusCode, String body) {
            this.statusCode = statusCode;
            this.body = body;
        }
    }

    public static class AuthResult {
        public boolean success;
        public boolean mfaRequired;
        public String mfaTicket;
        public String token;
        public String errorMessage;
    }

    public interface UiCallback {
        void onLog(String msg, String level);
        void onQuestsUpdated(String accountId);
        void onAccountStateChanged(String accountId);
        void onTokenExpired(AccountProfile account);
        void onAllQuestsCompletedCheck();
    }

    public static final int ICON_SCREEN = 1;
    public static final int ICON_BATTERY = 2;
    public static final int ICON_AUTO_EXIT = 3;
    public static final int ICON_HYPESQUAD = 4;
    public static final int ICON_RPC = 5;
    public static final int ICON_UPDATE = 6;
    public static final int ICON_SHARE = 7;
    public static final int ICON_LANGUAGE = 8;
    public static final int ICON_GITHUB = 9;
    public static final int ICON_LOGOUT = 10;
    public static final int ICON_EXIT = 11;
    public static final int ICON_REFRESH = 12;
    public static final int ICON_SOURCE_CODE = 13;
    public static final int ICON_CLOSE_X = 14;
    public static final int ICON_INFO = 15;

    public static Drawable createVectorIconDrawable(final int iconType, final int color) {
        return new Drawable() {
            private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

            {
                paint.setColor(color);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeCap(Paint.Cap.ROUND);
                paint.setStrokeJoin(Paint.Join.ROUND);
            }

            @Override
            public void draw(Canvas canvas) {
                Rect b = getBounds();
                float w = b.width();
                float h = b.height();
                if (w <= 0 || h <= 0) return;
                float s = Math.min(w, h);
                paint.setStrokeWidth(Math.max(2f, s * 0.085f));
                paint.setStyle(Paint.Style.STROKE);

                float cx = b.exactCenterX();
                float cy = b.exactCenterY();
                float r = s * 0.36f;

                switch (iconType) {
                    case ICON_REFRESH: {
                        RectF arcRect = new RectF(cx - r, cy - r, cx + r, cy + r);
                        canvas.drawArc(arcRect, -30, 285, false, paint);
                        Path arrow = new Path();
                        float ax = cx + (float) (r * Math.cos(Math.toRadians(-30)));
                        float ay = cy + (float) (r * Math.sin(Math.toRadians(-30)));
                        arrow.moveTo(ax - s * 0.14f, ay - s * 0.04f);
                        arrow.lineTo(ax + s * 0.03f, ay + s * 0.04f);
                        arrow.lineTo(ax + s * 0.06f, ay - s * 0.14f);
                        canvas.drawPath(arrow, paint);
                        break;
                    }
                    case ICON_SCREEN: {
                        canvas.drawCircle(cx, cy, s * 0.18f, paint);
                        for (int i = 0; i < 8; i++) {
                            double ang = Math.toRadians(i * 45);
                            float x1 = cx + (float) (Math.cos(ang) * s * 0.27f);
                            float y1 = cy + (float) (Math.sin(ang) * s * 0.27f);
                            float x2 = cx + (float) (Math.cos(ang) * s * 0.39f);
                            float y2 = cy + (float) (Math.sin(ang) * s * 0.39f);
                            canvas.drawLine(x1, y1, x2, y2, paint);
                        }
                        break;
                    }
                    case ICON_BATTERY: {
                        RectF body = new RectF(cx - s * 0.24f, cy - s * 0.32f, cx + s * 0.24f, cy + s * 0.38f);
                        canvas.drawRoundRect(body, s * 0.06f, s * 0.06f, paint);
                        canvas.drawLine(cx - s * 0.10f, cy - s * 0.39f, cx + s * 0.10f, cy - s * 0.39f, paint);
                        Path bolt = new Path();
                        bolt.moveTo(cx + s * 0.04f, cy - s * 0.18f);
                        bolt.lineTo(cx - s * 0.09f, cy + s * 0.02f);
                        bolt.lineTo(cx + s * 0.05f, cy + s * 0.02f);
                        bolt.lineTo(cx - s * 0.04f, cy + s * 0.22f);
                        canvas.drawPath(bolt, paint);
                        break;
                    }
                    case ICON_AUTO_EXIT: {
                        canvas.drawCircle(cx, cy, r, paint);
                        Path check = new Path();
                        check.moveTo(cx - s * 0.16f, cy + s * 0.01f);
                        check.lineTo(cx - s * 0.04f, cy + s * 0.13f);
                        check.lineTo(cx + s * 0.18f, cy - s * 0.11f);
                        canvas.drawPath(check, paint);
                        break;
                    }
                    case ICON_HYPESQUAD: {
                        Path shield = new Path();
                        shield.moveTo(cx, cy - r);
                        shield.lineTo(cx + r * 0.88f, cy - r * 0.55f);
                        shield.lineTo(cx + r * 0.72f, cy + r * 0.25f);
                        shield.lineTo(cx, cy + r);
                        shield.lineTo(cx - r * 0.72f, cy + r * 0.25f);
                        shield.lineTo(cx - r * 0.88f, cy - r * 0.55f);
                        shield.close();
                        canvas.drawPath(shield, paint);
                        canvas.drawCircle(cx, cy - r * 0.05f, s * 0.08f, paint);
                        break;
                    }
                    case ICON_RPC: {
                        RectF pad = new RectF(cx - r, cy - s * 0.24f, cx + r, cy + s * 0.24f);
                        canvas.drawRoundRect(pad, s * 0.14f, s * 0.14f, paint);
                        canvas.drawLine(cx - s * 0.18f, cy, cx - s * 0.06f, cy, paint);
                        canvas.drawLine(cx - s * 0.12f, cy - s * 0.06f, cx - s * 0.12f, cy + s * 0.06f, paint);
                        paint.setStyle(Paint.Style.FILL);
                        canvas.drawCircle(cx + s * 0.12f, cy - s * 0.04f, s * 0.035f, paint);
                        canvas.drawCircle(cx + s * 0.20f, cy + s * 0.04f, s * 0.035f, paint);
                        break;
                    }
                    case ICON_UPDATE: {
                        canvas.drawLine(cx, cy - r * 0.85f, cx, cy + r * 0.45f, paint);
                        canvas.drawLine(cx - s * 0.15f, cy + s * 0.02f, cx, cy + r * 0.45f, paint);
                        canvas.drawLine(cx + s * 0.15f, cy + s * 0.02f, cx, cy + r * 0.45f, paint);
                        canvas.drawLine(cx - r * 0.75f, cy + r * 0.85f, cx + r * 0.75f, cy + r * 0.85f, paint);
                        break;
                    }
                    case ICON_SHARE: {
                        float rDot = s * 0.07f;
                        float xL = cx - r * 0.7f, yL = cy;
                        float xT = cx + r * 0.65f, yT = cy - r * 0.7f;
                        float xB = cx + r * 0.65f, yB = cy + r * 0.7f;
                        canvas.drawLine(xL, yL, xT, yT, paint);
                        canvas.drawLine(xL, yL, xB, yB, paint);
                        paint.setStyle(Paint.Style.FILL);
                        canvas.drawCircle(xL, yL, rDot, paint);
                        canvas.drawCircle(xT, yT, rDot, paint);
                        canvas.drawCircle(xB, yB, rDot, paint);
                        break;
                    }
                    case ICON_LANGUAGE: {
                        canvas.drawCircle(cx, cy, r, paint);
                        canvas.drawLine(cx - r, cy, cx + r, cy, paint);
                        RectF oval = new RectF(cx - r * 0.48f, cy - r, cx + r * 0.48f, cy + r);
                        canvas.drawOval(oval, paint);
                        break;
                    }
                    case ICON_GITHUB: {

                        paint.setStyle(Paint.Style.STROKE);
                        canvas.drawCircle(cx, cy, r * 1.02f, paint);

                        Path cat = new Path();

                        cat.moveTo(cx - r * 0.48f, cy - r * 0.05f);
                        cat.lineTo(cx - r * 0.52f, cy - r * 0.58f);
                        cat.lineTo(cx - r * 0.20f, cy - r * 0.38f);
                        cat.quadTo(cx, cy - r * 0.46f, cx + r * 0.20f, cy - r * 0.38f);
                        cat.lineTo(cx + r * 0.52f, cy - r * 0.58f);
                        cat.lineTo(cx + r * 0.48f, cy - r * 0.05f);

                        cat.quadTo(cx + r * 0.58f, cy + r * 0.35f, cx + r * 0.22f, cy + r * 0.46f);
                        cat.lineTo(cx + r * 0.22f, cy + r * 0.95f);
                        cat.moveTo(cx - r * 0.22f, cy + r * 0.95f);
                        cat.lineTo(cx - r * 0.22f, cy + r * 0.46f);
                        cat.quadTo(cx - r * 0.58f, cy + r * 0.35f, cx - r * 0.48f, cy - r * 0.05f);
                        canvas.drawPath(cat, paint);

                        Path tail = new Path();
                        tail.moveTo(cx - r * 0.22f, cy + r * 0.65f);
                        tail.quadTo(cx - r * 0.62f, cy + r * 0.65f, cx - r * 0.68f, cy + r * 0.35f);
                        canvas.drawPath(tail, paint);
                        break;
                    }
                    case ICON_SOURCE_CODE: {
                        Path left = new Path();
                        left.moveTo(cx - s * 0.12f, cy - s * 0.18f);
                        left.lineTo(cx - s * 0.32f, cy);
                        left.lineTo(cx - s * 0.12f, cy + s * 0.18f);
                        canvas.drawPath(left, paint);

                        Path right = new Path();
                        right.moveTo(cx + s * 0.12f, cy - s * 0.18f);
                        right.lineTo(cx + s * 0.32f, cy);
                        right.lineTo(cx + s * 0.12f, cy + s * 0.18f);
                        canvas.drawPath(right, paint);

                        canvas.drawLine(cx + s * 0.05f, cy - s * 0.24f, cx - s * 0.05f, cy + s * 0.24f, paint);
                        break;
                    }
                    case ICON_LOGOUT: {
                        Path door = new Path();
                        door.moveTo(cx - s * 0.05f, cy - r * 0.85f);
                        door.lineTo(cx - r * 0.85f, cy - r * 0.85f);
                        door.lineTo(cx - r * 0.85f, cy + r * 0.85f);
                        door.lineTo(cx - s * 0.05f, cy + r * 0.85f);
                        canvas.drawPath(door, paint);

                        canvas.drawLine(cx - s * 0.15f, cy, cx + r * 0.9f, cy, paint);
                        canvas.drawLine(cx + r * 0.45f, cy - s * 0.15f, cx + r * 0.9f, cy, paint);
                        canvas.drawLine(cx + r * 0.45f, cy + s * 0.15f, cx + r * 0.9f, cy, paint);
                        break;
                    }
                    case ICON_EXIT: {
                        RectF arc = new RectF(cx - r * 0.85f, cy - r * 0.85f, cx + r * 0.85f, cy + r * 0.85f);
                        canvas.drawArc(arc, -55, 290, false, paint);
                        canvas.drawLine(cx, cy - r * 0.95f, cx, cy - s * 0.02f, paint);
                        break;
                    }
                    case ICON_CLOSE_X: {
                        float d = r * 0.68f;
                        canvas.drawLine(cx - d, cy - d, cx + d, cy + d, paint);
                        canvas.drawLine(cx + d, cy - d, cx - d, cy + d, paint);
                        break;
                    }
                    case ICON_INFO: {

                        paint.setStyle(Paint.Style.STROKE);
                        canvas.drawCircle(cx, cy, r * 1.05f, paint);

                        paint.setStyle(Paint.Style.FILL);
                        canvas.drawCircle(cx, cy - r * 0.42f, s * 0.055f, paint);

                        paint.setStyle(Paint.Style.STROKE);
                        canvas.drawLine(cx, cy - r * 0.12f, cx, cy + r * 0.46f, paint);
                        canvas.drawLine(cx - s * 0.08f, cy - r * 0.10f, cx, cy - r * 0.12f, paint);
                        canvas.drawLine(cx - s * 0.10f, cy + r * 0.46f, cx + s * 0.10f, cy + r * 0.46f, paint);
                        break;
                    }
                }
            }

            @Override
            public void setAlpha(int alpha) {
                paint.setAlpha(alpha);
            }

            @Override
            public void setColorFilter(ColorFilter colorFilter) {
                paint.setColorFilter(colorFilter);
            }

            @Override
            public int getOpacity() {
                return PixelFormat.TRANSLUCENT;
            }
        };
    }
    public static Drawable createPresenceStatusDrawable(final String statusType) {
        return new Drawable() {
            private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

            @Override
            public void draw(Canvas canvas) {
                Rect b = getBounds();
                float w = b.width();
                float h = b.height();
                if (w <= 0 || h <= 0) return;
                float cx = b.exactCenterX();
                float cy = b.exactCenterY();
                float r = Math.min(w, h) * 0.40f;

                if ("online".equalsIgnoreCase(statusType)) {
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(0xFF23A55A); 
                    canvas.drawCircle(cx, cy, r, paint);
                } else if ("idle".equalsIgnoreCase(statusType)) {
                    int sc = canvas.saveLayer(0, 0, w, h, null, Canvas.ALL_SAVE_FLAG);
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(0xFFF0B232); 
                    canvas.drawCircle(cx, cy, r, paint);
                    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
                    canvas.drawCircle(cx - r * 0.35f, cy - r * 0.35f, r * 0.72f, paint);
                    paint.setXfermode(null);
                    canvas.restoreToCount(sc);
                } else if ("dnd".equalsIgnoreCase(statusType)) {
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(0xFFF23F43); 
                    canvas.drawCircle(cx, cy, r, paint);
                    paint.setColor(0xFFFFFFFF);
                    float barW = r * 0.56f;
                    float barH = Math.max(2f, r * 0.22f);
                    RectF minus = new RectF(cx - barW, cy - barH, cx + barW, cy + barH);
                    canvas.drawRoundRect(minus, barH, barH, paint);
                } else {

                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(Math.max(2.5f, r * 0.34f));
                    paint.setColor(0xFF94A3B8);
                    canvas.drawCircle(cx, cy, r * 0.82f, paint);
                }
            }

            @Override
            public void setAlpha(int alpha) {
                paint.setAlpha(alpha);
            }

            @Override
            public void setColorFilter(ColorFilter colorFilter) {
                paint.setColorFilter(colorFilter);
            }

            @Override
            public int getOpacity() {
                return PixelFormat.TRANSLUCENT;
            }
        };
    }

    public static void attachSpreadGrayEffect(final View targetView, final int cornerRadiusPx, final View.OnClickListener clickListener) {
        final SpreadGrayDrawable spreadDrawable = new SpreadGrayDrawable(cornerRadiusPx);
        targetView.setBackground(spreadDrawable);
        targetView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getActionMasked();
                if (action == MotionEvent.ACTION_DOWN) {
                    spreadDrawable.startSpread(event.getX());
                } else if (action == MotionEvent.ACTION_UP) {
                    spreadDrawable.fadeOut();
                    if (clickListener != null) {
                        clickListener.onClick(v);
                    }
                } else if (action == MotionEvent.ACTION_CANCEL) {
                    spreadDrawable.fadeOut();
                }
                return true;
            }
        });
    }

    public static class SpreadGrayDrawable extends Drawable {
        private final Paint basePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint spreadPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final int cornerRadius;
        private float touchX = -1f;
        private float progress = 0f;
        private boolean pressed = false;
        private ValueAnimator animator;

        public SpreadGrayDrawable(int cornerRadiusPx) {
            this.cornerRadius = cornerRadiusPx;
            basePaint.setColor(0xFFF8FAFC);
            spreadPaint.setColor(0xFFE2E8F0);
        }

        public void startSpread(float x) {
            this.touchX = x;
            this.pressed = true;
            if (animator != null) animator.cancel();
            animator = ValueAnimator.ofFloat(0.18f, 1.0f);
            animator.setDuration(220);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    progress = (Float) animation.getAnimatedValue();
                    invalidateSelf();
                }
            });
            animator.start();
            invalidateSelf();
        }

        public void fadeOut() {
            this.pressed = false;
            if (animator != null) animator.cancel();
            animator = ValueAnimator.ofFloat(progress, 0f);
            animator.setDuration(150);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    progress = (Float) animation.getAnimatedValue();
                    invalidateSelf();
                }
            });
            animator.start();
            invalidateSelf();
        }

        @Override
        public void draw(Canvas canvas) {
            Rect b = getBounds();
            RectF full = new RectF(b.left, b.top, b.right, b.bottom);
            if (pressed || progress > 0.01f) {
                basePaint.setColor(0xFFF1F5F9);
                canvas.drawRoundRect(full, cornerRadius, cornerRadius, basePaint);
                float cx = (touchX >= 0) ? touchX : b.exactCenterX();
                float maxDist = Math.max(cx - b.left, b.right - cx);
                float curHalfWidth = maxDist * progress;
                float left = Math.max(b.left, cx - curHalfWidth);
                float right = Math.min(b.right, cx + curHalfWidth);
                RectF spreadRect = new RectF(left, b.top, right, b.bottom);
                spreadPaint.setAlpha((int) (255 * Math.min(1f, progress + 0.25f)));
                canvas.drawRoundRect(spreadRect, cornerRadius, cornerRadius, spreadPaint);
            }
        }

        @Override
        public void setAlpha(int alpha) {}

        @Override
        public void setColorFilter(ColorFilter colorFilter) {}

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    public static class SkeletonShimmerDrawable extends Drawable {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final int cornerRadius;
        private float shimmerProgress = 0f;

        public SkeletonShimmerDrawable(int cornerRadiusPx) {
            this.cornerRadius = cornerRadiusPx;
        }

        public void setShimmerProgress(float progress) {
            this.shimmerProgress = progress;
            invalidateSelf();
        }

        @Override
        public void draw(Canvas canvas) {
            Rect b = getBounds();
            float w = b.width();
            float h = b.height();
            if (w <= 0 || h <= 0) return;

            RectF rect = new RectF(b.left, b.top, b.right, b.bottom);
            paint.setShader(null);
            paint.setColor(0xFFE2E8F0);
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint);

            float bandWidth = Math.max(w * 0.55f, 120f);
            float startX = -bandWidth + (w + bandWidth * 2f) * shimmerProgress;
            android.graphics.LinearGradient shader = new android.graphics.LinearGradient(
                    startX, 0, startX + bandWidth, 0,
                    new int[]{0xFFE2E8F0, 0xFFF8FAFC, 0xFFE2E8F0},
                    new float[]{0f, 0.5f, 1f},
                    Shader.TileMode.CLAMP);
            paint.setShader(shader);
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint);
        }

        @Override
        public void setAlpha(int alpha) {}

        @Override
        public void setColorFilter(ColorFilter colorFilter) {}

        @Override
        public int getOpacity() {
            return PixelFormat.OPAQUE;
        }
    }

    public static Bitmap cropCircleBitmap(Bitmap src) {
        if (src == null) return null;
        int size = Math.min(src.getWidth(), src.getHeight());
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Bitmap cropped = Bitmap.createBitmap(src, (src.getWidth() - size) / 2, (src.getHeight() - size) / 2, size, size);
        BitmapShader shader = new BitmapShader(cropped, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);
        return output;
    }

    public static Bitmap createBlurredViewBitmap(View targetView, int downscale, int radius) {
        if (targetView == null || targetView.getWidth() <= 0 || targetView.getHeight() <= 0) {
            return null;
        }
        try {
            int w = Math.max(16, targetView.getWidth() / downscale);
            int h = Math.max(16, targetView.getHeight() / downscale);
            Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);
            canvas.scale(1f / downscale, 1f / downscale);
            targetView.draw(canvas);
            canvas.drawColor(0x22FFFFFF);

            int[] pix = new int[w * h];
            bmp.getPixels(pix, 0, w, 0, 0, w, h);
            int[] temp = new int[w * h];

            for (int pass = 0; pass < 3; pass++) {
                for (int y = 0; y < h; y++) {
                    for (int x = 0; x < w; x++) {
                        int rSum = 0, gSum = 0, bSum = 0, count = 0;
                        for (int k = -radius; k <= radius; k++) {
                            int nx = x + k;
                            if (nx >= 0 && nx < w) {
                                int c = pix[y * w + nx];
                                rSum += (c >> 16) & 0xFF;
                                gSum += (c >> 8) & 0xFF;
                                bSum += c & 0xFF;
                                count++;
                            }
                        }
                        temp[y * w + x] = (0xFF << 24) | ((rSum / count) << 16) | ((gSum / count) << 8) | (bSum / count);
                    }
                }

                for (int x = 0; x < w; x++) {
                    for (int y = 0; y < h; y++) {
                        int rSum = 0, gSum = 0, bSum = 0, count = 0;
                        for (int k = -radius; k <= radius; k++) {
                            int ny = y + k;
                            if (ny >= 0 && ny < h) {
                                int c = temp[ny * w + x];
                                rSum += (c >> 16) & 0xFF;
                                gSum += (c >> 8) & 0xFF;
                                bSum += c & 0xFF;
                                count++;
                            }
                        }
                        pix[y * w + x] = (0xFF << 24) | ((rSum / count) << 16) | ((gSum / count) << 8) | (bSum / count);
                    }
                }
            }
            bmp.setPixels(pix, 0, w, 0, 0, w, h);
            return bmp;
        } catch (Exception e) {
            return null;
        }
    }


    private static volatile int cachedBuildNumber = 0;
    private static volatile String lastLoginSessionCookies = "";

    public static int resolveClientBuild(UiCallback callback) {
        if (cachedBuildNumber > 0) return cachedBuildNumber;
        int fallbackBuild = 504649;
        if (callback != null) {
            callback.onLog(I18n.t("Đang lấy Client Build Number từ discord.com/app...", "Fetching Client Build Number from discord.com/app..."), "info");
        }
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36";
        try {
            HttpResponse appRes = rawHttpRequest("GET", "https://discord.com/app", null, null, userAgent, null);
            if (appRes.statusCode != 200 || TextUtils.isEmpty(appRes.body)) {
                cachedBuildNumber = fallbackBuild;
                return fallbackBuild;
            }
            List<String> assets = new ArrayList<String>();
            Matcher m1 = Pattern.compile("/assets/([a-f0-9]+)\\.js").matcher(appRes.body);
            while (m1.find()) {
                assets.add(m1.group(1));
            }
            if (assets.isEmpty()) {
                Matcher m2 = Pattern.compile("src=\"(/assets/[^\"]+\\.js)\"").matcher(appRes.body);
                while (m2.find()) {
                    String path = m2.group(1);
                    int slash = path.lastIndexOf('/');
                    String name = (slash >= 0) ? path.substring(slash + 1) : path;
                    assets.add(name.replace(".js", ""));
                }
            }
            int startIdx = Math.max(0, assets.size() - 5);
            for (int i = startIdx; i < assets.size(); i++) {
                String asset = assets.get(i);
                try {
                    HttpResponse jsRes = rawHttpRequest("GET", "https://discord.com/assets/" + asset + ".js", null, null, userAgent, null);
                    if (jsRes.statusCode == 200 && !TextUtils.isEmpty(jsRes.body)) {
                        Matcher bm = Pattern.compile("buildNumber[\"\\s:]+[\"\\s]*(\\d{5,7})").matcher(jsRes.body);
                        if (bm.find()) {
                            cachedBuildNumber = Integer.parseInt(bm.group(1));
                            if (callback != null) {
                                callback.onLog(I18n.t("Đã phân giải Client Build: ", "Resolved Client Build: ") + cachedBuildNumber, "ok");
                            }
                            return cachedBuildNumber;
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }
        cachedBuildNumber = fallbackBuild;
        return fallbackBuild;
    }

    public static String generateDesktopSuperProperties(int buildNumber) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("os", "Windows");
            payload.put("browser", "Discord Client");
            payload.put("release_channel", "stable");
            payload.put("client_version", "1.0.9175");
            payload.put("os_version", "10.0.26100");
            payload.put("os_arch", "x64");
            payload.put("app_arch", "x64");
            payload.put("system_locale", "en-US");
            payload.put("browser_user_agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) " +
                    "discord/1.0.9175 Chrome/128.0.6613.186 " +
                    "Electron/32.2.7 Safari/537.36");
            payload.put("browser_version", "32.2.7");
            payload.put("client_build_number", buildNumber);
            payload.put("native_build_number", 59498);
            payload.put("client_event_source", JSONObject.NULL);
            return Base64.encodeToString(payload.toString().getBytes("UTF-8"), Base64.NO_WRAP);
        } catch (Exception e) {
            return "";
        }
    }

    public static AuthResult loginWithEmailPassword(String email, String password, String mfaTicket, String mfaCode) {
        AuthResult result = new AuthResult();
        try {
            if (TextUtils.isEmpty(mfaTicket)) {
                lastLoginSessionCookies = "";
            }
            JSONObject superProps = new JSONObject();
            superProps.put("os", "Windows");
            superProps.put("browser", "Chrome");
            superProps.put("device", "");
            superProps.put("system_locale", "en-US");
            superProps.put("browser_user_agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            superProps.put("browser_version", "120.0.0.0");
            superProps.put("os_version", "10");
            superProps.put("referrer", "");
            superProps.put("referring_domain", "");
            superProps.put("referrer_current", "");
            superProps.put("referring_domain_current", "");
            superProps.put("release_channel", "stable");
            superProps.put("client_build_number", 250000);
            superProps.put("client_event_source", JSONObject.NULL);
            String superPropsB64 = Base64.encodeToString(superProps.toString().getBytes("UTF-8"), Base64.NO_WRAP);

            String ua = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

            String cleanMfaCode = (mfaCode != null) ? mfaCode.replaceAll("\\s+", "").trim() : "";

            if (!TextUtils.isEmpty(mfaTicket) && !TextUtils.isEmpty(cleanMfaCode)) {
                JSONObject mfaPayload = new JSONObject();
                mfaPayload.put("code", cleanMfaCode);
                mfaPayload.put("ticket", mfaTicket.trim());
                mfaPayload.put("login_source", JSONObject.NULL);
                mfaPayload.put("gift_code_sku_id", JSONObject.NULL);

                HttpResponse rMfa = rawHttpJsonPost(API_BASE + "/auth/mfa/totp", null, superPropsB64, ua, "https://discord.com/login", mfaPayload);
                JSONObject data = new JSONObject(rMfa.body);

                if (rMfa.statusCode == 200 && data.has("token") && !data.isNull("token")) {
                    result.success = true;
                    result.token = data.optString("token");
                    return result;
                } else {

                    result.mfaRequired = true;
                    result.mfaTicket = mfaTicket.trim();
                    String msg = data.optString("message", "Invalid two-factor code");
                    result.errorMessage = msg;
                    return result;
                }
            }

            JSONObject loginPayload = new JSONObject();
            loginPayload.put("login", email.trim());
            loginPayload.put("password", password);
            loginPayload.put("undelete", false);
            loginPayload.put("login_source", JSONObject.NULL);
            loginPayload.put("gift_code_sku_id", JSONObject.NULL);

            HttpResponse r = rawHttpJsonPost(API_BASE + "/auth/login", null, superPropsB64, ua, "https://discord.com/login", loginPayload);
            JSONObject data = new JSONObject(r.body);

            if (r.statusCode == 200 && data.has("token") && !data.isNull("token")) {
                result.success = true;
                result.token = data.optString("token");
                return result;
            }

            if (data.has("ticket") && data.optBoolean("mfa", false)) {
                result.mfaRequired = true;
                result.mfaTicket = data.optString("ticket");
                return result;
            }

            if (data.has("captcha_key")) {
                result.errorMessage = I18n.t(
                        "Discord yêu cầu giải Captcha trên IP này. Hãy chuyển sang tab 'Bằng Token' để dán Token trực tiếp!",
                        "Discord requires Captcha on this IP. Please switch to 'By Token' tab to paste your Token directly!");
                return result;
            }

            result.errorMessage = data.optString("message", "HTTP " + r.statusCode + ": " + r.body);
        } catch (Exception e) {
            result.errorMessage = e.getMessage();
        }
        return result;
    }

    public static HttpResponse requestHypeSquadBadge(String token, int houseId) {
        String ua = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)";
        if (houseId <= 0) {
            return rawHttpRequest("DELETE", API_BASE + "/hypesquad/online", token, null, ua, null);
        }
        try {
            JSONObject body = new JSONObject();
            body.put("house_id", houseId);
            return rawHttpJsonPost(API_BASE + "/hypesquad/online", token, null, ua, null, body);
        } catch (Exception e) {
            return new HttpResponse(-1, e.getMessage());
        }
    }

    public static String checkRemoteVersion() {
        HttpResponse res = rawHttpRequest("GET", VERSION_CHECK_URL, null, null, "Mozilla/5.0", null);
        if (res.statusCode == 200 && !TextUtils.isEmpty(res.body)) {
            String[] tokens = res.body.trim().split("\\s+");
            if (tokens.length > 0) {
                return tokens[tokens.length - 1].trim();
            }
        }
        return null;
    }

    public static boolean isNewerVersion(String currentVer, String remoteVer) {
        if (TextUtils.isEmpty(remoteVer)) return false;
        try {
            double c = Double.parseDouble(currentVer.trim());
            double r = Double.parseDouble(remoteVer.trim());
            return r > c;
        } catch (Exception e) {
            return !currentVer.trim().equalsIgnoreCase(remoteVer.trim());
        }
    }

    public static HttpResponse rawHttpRequest(String method, String urlStr, String authToken, String superProps, String customUA, String customReferer) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);

            String ua = (customUA != null) ? customUA :
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) " +
                    "discord/1.0.9175 Chrome/128.0.6613.186 " +
                    "Electron/32.2.7 Safari/537.36";

            conn.setRequestProperty("User-Agent", ua);
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.9");

            if (authToken != null) {
                conn.setRequestProperty("Authorization", authToken);
                conn.setRequestProperty("Content-Type", "application/json");
                if (superProps != null) {
                    conn.setRequestProperty("X-Super-Properties", superProps);
                }
                conn.setRequestProperty("X-Discord-Locale", "en-US");
                conn.setRequestProperty("X-Discord-Timezone", "UTC");
                conn.setRequestProperty("Origin", "https://discord.com");
                conn.setRequestProperty("Referer", customReferer != null ? customReferer : "https://discord.com/channels/@me");
            }

            int code = conn.getResponseCode();
            InputStream is = (code >= 200 && code < 400) ? conn.getInputStream() : conn.getErrorStream();
            return new HttpResponse(code, readStreamToString(is));
        } catch (Exception e) {
            return new HttpResponse(-1, e.getMessage());
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    public static HttpResponse rawHttpJsonPost(String urlStr, String authToken, String superProps, String customUA, String customReferer, JSONObject jsonBody) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
            conn.setDoOutput(true);

            String ua = (customUA != null) ? customUA :
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) " +
                    "discord/1.0.9175 Chrome/128.0.6613.186 " +
                    "Electron/32.2.7 Safari/537.36";

            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
            conn.setRequestProperty("User-Agent", ua);
            conn.setRequestProperty("Origin", "https://discord.com");
            conn.setRequestProperty("Referer", customReferer != null ? customReferer : "https://discord.com/channels/@me");

            if (authToken != null) {
                conn.setRequestProperty("Authorization", authToken);
                conn.setRequestProperty("X-Discord-Locale", "en-US");
                conn.setRequestProperty("X-Discord-Timezone", "UTC");
            }
            if (superProps != null) {
                conn.setRequestProperty("X-Super-Properties", superProps);
            }
            if (!TextUtils.isEmpty(lastLoginSessionCookies)) {
                conn.setRequestProperty("Cookie", lastLoginSessionCookies);
            }

            byte[] payloadBytes = (jsonBody != null ? jsonBody.toString() : "{}").getBytes("UTF-8");
            OutputStream os = conn.getOutputStream();
            os.write(payloadBytes);
            os.flush();
            os.close();

            int code = conn.getResponseCode();

            Map<String, List<String>> headerFields = conn.getHeaderFields();
            if (headerFields != null && headerFields.containsKey("Set-Cookie")) {
                List<String> cookies = headerFields.get("Set-Cookie");
                if (cookies != null && !cookies.isEmpty()) {
                    StringBuilder cookieSb = new StringBuilder();
                    for (String cookie : cookies) {
                        if (cookie != null) {
                            String part = cookie.split(";")[0];
                            if (cookieSb.length() > 0) cookieSb.append("; ");
                            cookieSb.append(part);
                        }
                    }
                    lastLoginSessionCookies = cookieSb.toString();
                }
            }

            InputStream is = (code >= 200 && code < 400) ? conn.getInputStream() : conn.getErrorStream();
            return new HttpResponse(code, readStreamToString(is));
        } catch (Exception e) {
            return new HttpResponse(-1, e.getMessage());
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    private static String readStreamToString(InputStream is) {
        if (is == null) return "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    public static class DiscordGatewaySession {
        private final AccountProfile account;
        private final UiCallback callback;
        private volatile boolean running = false;
        private SSLSocket sslSocket;
        private BufferedOutputStream outStream;
        private Thread readerThread;
        private Thread heartbeatThread;
        private volatile int lastSequence = -1;
        private final SecureRandom secureRandom = new SecureRandom();

        public DiscordGatewaySession(AccountProfile account, UiCallback callback) {
            this.account = account;
            this.callback = callback;
        }

        public synchronized void startOrUpdate() {
            if ("none".equalsIgnoreCase(account.presenceStatus) && !account.rpcConfig.enabled) {
                stop();
                return;
            }
            if (running && sslSocket != null && sslSocket.isConnected() && !sslSocket.isClosed()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendPresenceUpdateFrame();
                    }
                }).start();
                return;
            }
            stop();
            running = true;
            readerThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    runGatewayLoop();
                }
            });
            readerThread.start();
        }

        public synchronized void stop() {
            running = false;
            if (heartbeatThread != null) heartbeatThread.interrupt();
            if (readerThread != null) readerThread.interrupt();
            try {
                if (sslSocket != null) sslSocket.close();
            } catch (Exception ignored) {
            }
            sslSocket = null;
        }

        private void runGatewayLoop() {
            try {
                SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                sslSocket = (SSLSocket) factory.createSocket("gateway.discord.gg", 443);
                sslSocket.setSoTimeout(70000);
                sslSocket.startHandshake();

                BufferedInputStream in = new BufferedInputStream(sslSocket.getInputStream());
                outStream = new BufferedOutputStream(sslSocket.getOutputStream());

                byte[] keyBytes = new byte[16];
                secureRandom.nextBytes(keyBytes);
                String wsKey = Base64.encodeToString(keyBytes, Base64.NO_WRAP);

                String handshake = "GET /?v=9&encoding=json HTTP/1.1\r\n" +
                        "Host: gateway.discord.gg\r\n" +
                        "Upgrade: websocket\r\n" +
                        "Connection: Upgrade\r\n" +
                        "Sec-WebSocket-Key: " + wsKey + "\r\n" +
                        "Sec-WebSocket-Version: 13\r\n" +
                        "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64)\r\n\r\n";
                outStream.write(handshake.getBytes("UTF-8"));
                outStream.flush();

                StringBuilder headerSb = new StringBuilder();
                int b;
                while ((b = in.read()) != -1) {
                    headerSb.append((char) b);
                    int len = headerSb.length();
                    if (len >= 4 && headerSb.substring(len - 4).equals("\r\n\r\n")) {
                        break;
                    }
                }
                if (!headerSb.toString().contains("101")) {
                    running = false;
                    return;
                }

                while (running) {
                    String frameText = readWsTextFrame(in);
                    if (frameText == null) break;
                    JSONObject pkt = new JSONObject(frameText);
                    if (!pkt.isNull("s")) {
                        lastSequence = pkt.optInt("s", lastSequence);
                    }
                    int op = pkt.optInt("op", -1);
                    if (op == 10) { 
                        JSONObject d = pkt.optJSONObject("d");
                        final int hbInterval = (d != null) ? d.optInt("heartbeat_interval", 41250) : 41250;
                        sendIdentifyFrame();
                        startHeartbeatLoop(hbInterval);
                    } else if (op == 11) {

                    }
                }
            } catch (Exception ignored) {
            } finally {
                running = false;
            }
        }

        private void startHeartbeatLoop(final int intervalMs) {
            if (heartbeatThread != null) heartbeatThread.interrupt();
            heartbeatThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (running) {
                        try {
                            Thread.sleep(intervalMs);
                            if (!running) break;
                            JSONObject hb = new JSONObject();
                            hb.put("op", 1);
                            hb.put("d", lastSequence >= 0 ? lastSequence : JSONObject.NULL);
                            sendWsTextFrame(hb.toString());
                        } catch (Exception e) {
                            break;
                        }
                    }
                }
            });
            heartbeatThread.start();
        }

        private void sendIdentifyFrame() {
            try {
                JSONObject props = new JSONObject();
                props.put("$os", "Windows");
                props.put("$browser", "Discord Client");
                props.put("$device", "desktop");

                JSONObject d = new JSONObject();
                d.put("token", account.token);
                d.put("capabilities", 16381);
                d.put("properties", props);
                d.put("presence", buildPresenceObject());

                JSONObject pkt = new JSONObject();
                pkt.put("op", 2);
                pkt.put("d", d);
                sendWsTextFrame(pkt.toString());

                if (callback != null) {
                    callback.onLog("[" + account.username + "] " + I18n.t("Đã đồng bộ Trực tuyến & RPC lên Discord Gateway!", "Synced Presence & RPC to Discord Gateway!"), "ok");
                }
            } catch (Exception ignored) {
            }
        }

        private void sendPresenceUpdateFrame() {
            try {
                JSONObject pkt = new JSONObject();
                pkt.put("op", 3);
                pkt.put("d", buildPresenceObject());
                sendWsTextFrame(pkt.toString());
                if (callback != null) {
                    callback.onLog("[" + account.username + "] " + I18n.t("Đã cập nhật trạng thái Trực tuyến & RPC.", "Updated Presence & RPC status."), "ok");
                }
            } catch (Exception ignored) {
            }
        }

        private JSONObject buildPresenceObject() throws Exception {
            JSONObject presence = new JSONObject();
            String st = account.presenceStatus;
            if ("none".equalsIgnoreCase(st) || TextUtils.isEmpty(st)) {
                st = "online";
            }
            presence.put("status", st);
            presence.put("since", "idle".equalsIgnoreCase(st) ? System.currentTimeMillis() : 0);
            presence.put("afk", "idle".equalsIgnoreCase(st));

            JSONArray activities = new JSONArray();
            RpcConfig rpc = account.rpcConfig;
            if (rpc != null && rpc.enabled && !TextUtils.isEmpty(rpc.name)) {
                JSONObject act = new JSONObject();
                act.put("name", rpc.name.trim());
                act.put("type", rpc.activityType);

                if (!TextUtils.isEmpty(rpc.applicationId)) {
                    act.put("application_id", rpc.applicationId.trim());
                }
                if (!TextUtils.isEmpty(rpc.details)) {
                    act.put("details", rpc.details.trim());
                }
                if (!TextUtils.isEmpty(rpc.state)) {
                    act.put("state", rpc.state.trim());
                }
                if (rpc.activityType == 1 && !TextUtils.isEmpty(rpc.streamUrl)) {
                    act.put("url", rpc.streamUrl.trim());
                }
                if (rpc.timestampMode > 0) {
                    long now = System.currentTimeMillis();
                    if (rpc.timestampMode == 2) now -= 3600000L;
                    else if (rpc.timestampMode == 3) now -= 86400000L;
                    JSONObject ts = new JSONObject();
                    ts.put("start", now);
                    act.put("timestamps", ts);
                }
                if (!TextUtils.isEmpty(rpc.largeImage) || !TextUtils.isEmpty(rpc.largeText)) {
                    JSONObject assets = new JSONObject();
                    if (!TextUtils.isEmpty(rpc.largeImage)) {
                        assets.put("large_image", rpc.largeImage.trim());
                    }
                    if (!TextUtils.isEmpty(rpc.largeText)) {
                        assets.put("large_text", rpc.largeText.trim());
                    }
                    act.put("assets", assets);
                }
                if (!TextUtils.isEmpty(rpc.button1Label) && !TextUtils.isEmpty(rpc.button1Url)) {
                    JSONArray btns = new JSONArray();
                    btns.put(rpc.button1Label.trim());
                    act.put("buttons", btns);

                    JSONObject meta = new JSONObject();
                    JSONArray urls = new JSONArray();
                    urls.put(rpc.button1Url.trim());
                    meta.put("button_urls", urls);
                    act.put("metadata", meta);
                }
                activities.put(act);
            }
            presence.put("activities", activities);
            return presence;
        }

        private synchronized void sendWsTextFrame(String text) throws Exception {
            if (outStream == null) return;
            byte[] payload = text.getBytes("UTF-8");
            int len = payload.length;
            ByteArrayOutputStream frame = new ByteArrayOutputStream();
            frame.write(0x81); 

            if (len <= 125) {
                frame.write(0x80 | len);
            } else if (len <= 65535) {
                frame.write(0x80 | 126);
                frame.write((len >> 8) & 0xFF);
                frame.write(len & 0xFF);
            } else {
                frame.write(0x80 | 127);
                for (int i = 7; i >= 0; i--) {
                    frame.write((int) ((len >> (8 * i)) & 0xFF));
                }
            }

            byte[] mask = new byte[4];
            secureRandom.nextBytes(mask);
            frame.write(mask);
            for (int i = 0; i < len; i++) {
                frame.write(payload[i] ^ mask[i % 4]);
            }

            outStream.write(frame.toByteArray());
            outStream.flush();
        }

        private String readWsTextFrame(BufferedInputStream in) throws Exception {
            int b1 = in.read();
            int b2 = in.read();
            if (b1 == -1 || b2 == -1) return null;
            int opcode = b1 & 0x0F;
            boolean masked = (b2 & 0x80) != 0;
            long len = b2 & 0x7F;

            if (len == 126) {
                len = ((in.read() & 0xFF) << 8) | (in.read() & 0xFF);
            } else if (len == 127) {
                len = 0;
                for (int i = 0; i < 8; i++) {
                    len = (len << 8) | (in.read() & 0xFF);
                }
            }

            byte[] maskKey = new byte[4];
            if (masked) {
                readFully(in, maskKey, 4);
            }

            if (len > 4 * 1024 * 1024 || len < 0) return null;
            byte[] payload = new byte[(int) len];
            readFully(in, payload, (int) len);

            if (masked) {
                for (int i = 0; i < payload.length; i++) {
                    payload[i] ^= maskKey[i % 4];
                }
            }
            if (opcode == 8) return null; 
            return new String(payload, "UTF-8");
        }

        private void readFully(InputStream in, byte[] buf, int length) throws Exception {
            int total = 0;
            while (total < length) {
                int r = in.read(buf, total, length - total);
                if (r == -1) throw new Exception("EOF");
                total += r;
            }
        }
    }

    public static class AccountSession {
        public final AccountProfile profile;
        public final UiCallback callback;
        public final List<QuestItem> questItems = Collections.synchronizedList(new ArrayList<QuestItem>());
        public final Set<String> completedSet = Collections.synchronizedSet(new HashSet<String>());
        public final Set<String> skippedSet = Collections.synchronizedSet(new HashSet<String>());
        public final Set<String> activeRunningSet = Collections.synchronizedSet(new HashSet<String>());
        public final Set<String> forcedPrioritySet = Collections.synchronizedSet(new HashSet<String>());
        public final Map<String, Double> liveProgressMap = new ConcurrentHashMap<String, Double>();
        public final Map<String, JSONObject> latestQuestJsonMap = new ConcurrentHashMap<String, JSONObject>();

        public volatile boolean isLoadingQuests = false;
        private volatile boolean engineRunning = false;
        private Thread loopThread;
        private final ExecutorService forcedPool = Executors.newCachedThreadPool();
        private final DiscordGatewaySession gatewaySession;
        private String superPropsB64 = "";

        public AccountSession(AccountProfile profile, UiCallback callback) {
            this.profile = profile;
            this.callback = callback;
            this.gatewaySession = new DiscordGatewaySession(profile, callback);
        }

        public boolean isFarming() {
            return engineRunning || !activeRunningSet.isEmpty();
        }

        public void syncGatewayPresence() {
            gatewaySession.startOrUpdate();
        }

        public void shutdownAll() {
            stopEngine();
            gatewaySession.stop();
        }

        private void ensureSuperProps() {
            if (TextUtils.isEmpty(superPropsB64)) {
                int build = resolveClientBuild(callback);
                superPropsB64 = generateDesktopSuperProperties(build);
            }
        }

        public HttpResponse apiRequest(String method, String endpoint, JSONObject payload) {
            ensureSuperProps();
            String url = API_BASE + endpoint;
            HttpResponse res;
            if ("POST".equalsIgnoreCase(method)) {
                res = rawHttpJsonPost(url, profile.token, superPropsB64, null, null, payload);
            } else {
                res = rawHttpRequest(method, url, profile.token, superPropsB64, null, null);
            }
            if (res.statusCode == 401) {
                profile.tokenExpired = true;
                profile.loggedIn = false;
                stopEngine();
                if (callback != null) {
                    callback.onTokenExpired(profile);
                }
            }
            return res;
        }

        public boolean verifyAndRefreshProfile() {
            ensureSuperProps();
            HttpResponse res = apiRequest("GET", "/users/@me", null);
            if (res.statusCode == 200 && !TextUtils.isEmpty(res.body)) {
                try {
                    JSONObject data = new JSONObject(res.body);
                    profile.userId = data.optString("id", profile.userId);
                    String globalName = data.optString("global_name", "");
                    String userName = data.optString("username", "User");
                    profile.username = (!TextUtils.isEmpty(globalName) && !"null".equalsIgnoreCase(globalName)) ? globalName : userName;
                    String avHash = data.optString("avatar", "");
                    if (!TextUtils.isEmpty(avHash) && !"null".equalsIgnoreCase(avHash)) {
                        profile.avatarUrl = "https://cdn.discordapp.com/avatars/" + profile.userId + "/" + avHash + ".png?size=128";
                    }
                    profile.tokenExpired = false;
                    profile.loggedIn = true;
                    if (callback != null) {
                        callback.onLog("[" + profile.username + "] " + I18n.t("Xác thực thành công!", "Authenticated successfully!"), "ok");
                        callback.onAccountStateChanged(profile.userId);
                    }
                    syncGatewayPresence();
                    return true;
                } catch (Exception ignored) {
                }
            }
            return false;
        }

        public void refreshQuestsAsync() {
            isLoadingQuests = true;
            if (callback != null) {
                callback.onQuestsUpdated(profile.userId);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!profile.loggedIn) {
                            if (!verifyAndRefreshProfile()) return;
                        }
                        List<JSONObject> raw = fetchActiveQuests();
                        parseAndPublishQuests(raw);
                    } finally {
                        isLoadingQuests = false;
                        if (callback != null) {
                            callback.onQuestsUpdated(profile.userId);
                        }
                    }
                }
            }).start();
        }

        public List<JSONObject> fetchActiveQuests() {
            HttpResponse res = apiRequest("GET", "/quests/@me", null);
            if (res.statusCode == 200 && !TextUtils.isEmpty(res.body)) {
                List<JSONObject> list = new ArrayList<JSONObject>();
                try {
                    String trimmed = res.body.trim();
                    JSONArray arr = null;
                    if (trimmed.startsWith("{")) {
                        arr = new JSONObject(trimmed).optJSONArray("quests");
                    } else if (trimmed.startsWith("[")) {
                        arr = new JSONArray(trimmed);
                    }
                    if (arr != null) {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject q = arr.optJSONObject(i);
                            if (q != null) list.add(q);
                        }
                    }
                } catch (Exception ignored) {
                }
                return list;
            } else if (res.statusCode == 429) {
                int retry = 8;
                try {
                    retry = new JSONObject(res.body).optInt("retry_after", 8);
                } catch (Exception ignored) {
                }
                logAcc(I18n.t("Rate limit khi tải Quests. Đợi ", "Rate limited fetching quests. Waiting ") + retry + "s...", "warn");
                sleepQuietly(retry * 1000L);
                return fetchActiveQuests();
            }
            return new ArrayList<JSONObject>();
        }

        public void toggleSkipQuest(String qid, String title) {
            if (skippedSet.contains(qid)) {
                skippedSet.remove(qid);
                logAcc(I18n.t("Đã tiếp tục nhiệm vụ: ", "Resumed quest: ") + title, "info");
            } else {
                skippedSet.add(qid);
                forcedPrioritySet.remove(qid);
                logAcc(I18n.t("Đã tạm bỏ qua (Skip): ", "Skipped quest: ") + title, "warn");
            }
            notifyProgressChanged();
        }

        public void triggerCoFarmNow(final String qid, final String title) {
            skippedSet.remove(qid);
            forcedPrioritySet.add(qid);
            if (activeRunningSet.contains(qid)) return;
            final JSONObject qObj = latestQuestJsonMap.get(qid);
            if (qObj == null) return;
            logAcc(I18n.t("Đã thêm vào làm cùng lúc ngay: ", "Added to co-farm immediately: ") + title, "ok");
            notifyProgressChanged();

            forcedPool.submit(new Runnable() {
                @Override
                public void run() {
                    JSONObject status = getUserStatus(qObj);
                    if (extractString(status, "enrolledAt", "enrolled_at") == null) {
                        enrollQuest(qObj);
                    }
                    executeQuest(qObj);
                }
            });
        }

        private boolean enrollQuest(JSONObject quest) {
            String title = getQuestTitle(quest);
            String qid = quest.optString("id", "");
            for (int attempt = 1; attempt <= 3; attempt++) {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("location", 11);
                    payload.put("is_targeted", false);
                    payload.put("metadata_raw", JSONObject.NULL);
                    payload.put("metadata_sealed", JSONObject.NULL);
                    Object rawMeta = quest.opt("traffic_metadata_raw");
                    Object sealedMeta = quest.opt("traffic_metadata_sealed");
                    payload.put("traffic_metadata_raw", rawMeta != null ? rawMeta : JSONObject.NULL);
                    payload.put("traffic_metadata_sealed", sealedMeta != null ? sealedMeta : JSONObject.NULL);

                    HttpResponse res = apiRequest("POST", "/quests/" + qid + "/enroll", payload);
                    if (res.statusCode == 429) {
                        int wait = 6;
                        try {
                            wait = new JSONObject(res.body).optInt("retry_after", 5) + 1;
                        } catch (Exception ignored) {
                        }
                        sleepQuietly(wait * 1000L);
                        continue;
                    }
                    if (res.statusCode == 200 || res.statusCode == 201 || res.statusCode == 204) {
                        logAcc(I18n.t("Đã nhận Quest: ", "Enrolled in quest: ") + title, "ok");
                        return true;
                    }
                } catch (Exception ignored) {
                }
                return false;
            }
            return false;
        }

        private void processVideoTask(JSONObject quest) {
            String title = getQuestTitle(quest);
            String qid = quest.optString("id", "");
            int target = getTargetSeconds(quest);
            double done = liveProgressMap.containsKey(qid) ? liveProgressMap.get(qid) : getCompletedSeconds(quest);

            String enrolledStr = extractString(getUserStatus(quest), "enrolledAt", "enrolled_at");
            long enrolledTs = parseIsoTimestampSeconds(enrolledStr);
            if (enrolledTs <= 0) enrolledTs = System.currentTimeMillis() / 1000L;

            final int speedStep = (profile.speedMode == 0) ? 5 : (profile.speedMode == 2 ? 10 : 7);
            Random random = new Random();

            while (done < target && canContinueQuest(qid)) {
                double nowSec = System.currentTimeMillis() / 1000.0;
                double allowed = (nowSec - enrolledTs) + 12.0;
                double ts = done + speedStep;

                if ((allowed - done) >= speedStep) {
                    try {
                        JSONObject payload = new JSONObject();
                        payload.put("timestamp", Math.min((double) target, ts + random.nextDouble()));
                        HttpResponse res = apiRequest("POST", "/quests/" + qid + "/video-progress", payload);
                        if (res.statusCode == 200) {
                            JSONObject bodyJson = new JSONObject(res.body);
                            if (!bodyJson.isNull("completed_at") && !TextUtils.isEmpty(bodyJson.optString("completed_at"))) {
                                markQuestFinished(qid, target, title);
                                return;
                            }
                            done = Math.min((double) target, ts);
                            liveProgressMap.put(qid, done);
                            notifyProgressChanged();
                        } else if (res.statusCode == 429) {
                            int wait = 6;
                            try {
                                wait = new JSONObject(res.body).optInt("retry_after", 5) + 1;
                            } catch (Exception ignored) {
                            }
                            sleepQuietly(wait * 1000L);
                            continue;
                        }
                    } catch (Exception ignored) {
                    }
                }
                logAcc("[" + Math.round(done) + "/" + target + "s] -> " + title, "prog");
                if (ts >= target) break;
                sleepQuietly(1000L);
            }

            if (!canContinueQuest(qid)) return;
            try {
                JSONObject finalPayload = new JSONObject();
                finalPayload.put("timestamp", target);
                apiRequest("POST", "/quests/" + qid + "/video-progress", finalPayload);
            } catch (Exception ignored) {
            }
            markQuestFinished(qid, target, title);
        }

        private void processHeartbeatTask(JSONObject quest) {
            String title = getQuestTitle(quest);
            String qid = quest.optString("id", "");
            String tType = getTaskType(quest);
            int target = getTargetSeconds(quest);
            double done = liveProgressMap.containsKey(qid) ? liveProgressMap.get(qid) : getCompletedSeconds(quest);
            int pid = 1000 + new Random().nextInt(29001);
            int hbInterval = (profile.speedMode == 0) ? 20 : (profile.speedMode == 2 ? 10 : 15);

            while (done < target && canContinueQuest(qid)) {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("stream_key", "call:0:" + pid);
                    payload.put("terminal", false);

                    HttpResponse res = apiRequest("POST", "/quests/" + qid + "/heartbeat", payload);
                    if (res.statusCode == 200 && !TextUtils.isEmpty(res.body)) {
                        JSONObject bodyJson = new JSONObject(res.body);
                        JSONObject prog = bodyJson.optJSONObject("progress");
                        if (prog != null && tType != null && prog.has(tType)) {
                            JSONObject taskProg = prog.optJSONObject(tType);
                            if (taskProg != null) {
                                done = taskProg.optDouble("value", done);
                            }
                        }
                        liveProgressMap.put(qid, done);
                        notifyProgressChanged();
                        logAcc("[" + Math.round(done) + "/" + target + "s] -> " + title, "prog");

                        if ((!bodyJson.isNull("completed_at") && !TextUtils.isEmpty(bodyJson.optString("completed_at"))) || done >= target) {
                            markQuestFinished(qid, target, title);
                            return;
                        }
                    } else if (res.statusCode == 429) {
                        int wait = 11;
                        try {
                            wait = new JSONObject(res.body).optInt("retry_after", 10) + 1;
                        } catch (Exception ignored) {
                        }
                        sleepQuietly(wait * 1000L);
                        continue;
                    }
                } catch (Exception ignored) {
                }

                for (int s = 0; s < hbInterval; s++) {
                    if (!canContinueQuest(qid)) break;
                    sleepQuietly(1000L);
                }
            }

            if (!canContinueQuest(qid)) return;
            try {
                JSONObject termPayload = new JSONObject();
                termPayload.put("stream_key", "call:0:" + pid);
                termPayload.put("terminal", true);
                apiRequest("POST", "/quests/" + qid + "/heartbeat", termPayload);
            } catch (Exception ignored) {
            }
            markQuestFinished(qid, target, title);
        }

        private void markQuestFinished(String qid, int target, String title) {
            liveProgressMap.put(qid, (double) target);
            completedSet.add(qid);
            logAcc(I18n.t("✓ Hoàn thành: ", "✓ Completed: ") + title, "ok");
            notifyProgressChanged();
            if (callback != null) {
                callback.onAllQuestsCompletedCheck();
            }
        }

        private boolean canContinueQuest(String qid) {
            if (skippedSet.contains(qid) || profile.tokenExpired) return false;
            return engineRunning || forcedPrioritySet.contains(qid);
        }

        private void executeQuest(JSONObject quest) {
            String qid = quest.optString("id", "");
            String tType = getTaskType(quest);
            if (TextUtils.isEmpty(tType) || completedSet.contains(qid) || skippedSet.contains(qid)) return;
            if (!activeRunningSet.add(qid)) return;
            notifyProgressChanged();

            try {
                if ("WATCH_VIDEO".equals(tType) || "WATCH_VIDEO_ON_MOBILE".equals(tType)) {
                    processVideoTask(quest);
                } else {
                    processHeartbeatTask(quest);
                }
            } finally {
                activeRunningSet.remove(qid);
                forcedPrioritySet.remove(qid);
                notifyProgressChanged();
            }
        }

        public void startEngine() {
            if (engineRunning) return;
            engineRunning = true;
            if (callback != null) callback.onAccountStateChanged(profile.userId);

            loopThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!profile.loggedIn && !verifyAndRefreshProfile()) {
                        engineRunning = false;
                        if (callback != null) callback.onAccountStateChanged(profile.userId);
                        return;
                    }
                    logAcc(I18n.t("Bắt đầu Auto Farm (", "Started Auto Farm (") + profile.maxWorkers + " workers)", "ok");
                    while (engineRunning && !profile.tokenExpired) {
                        List<JSONObject> quests = fetchActiveQuests();
                        parseAndPublishQuests(quests);

                        boolean enrolledAny = false;
                        for (int i = 0; i < quests.size(); i++) {
                            if (!engineRunning) break;
                            JSONObject q = quests.get(i);
                            String qid = q.optString("id", "");
                            JSONObject status = getUserStatus(q);
                            if (extractString(status, "enrolledAt", "enrolled_at") == null && isQuestValid(q) && !skippedSet.contains(qid)) {
                                if (enrollQuest(q)) enrolledAny = true;
                                sleepQuietly(1800L);
                            }
                        }

                        if (enrolledAny && engineRunning) {
                            quests = fetchActiveQuests();
                            parseAndPublishQuests(quests);
                        }

                        List<JSONObject> queue = new ArrayList<JSONObject>();
                        for (int i = 0; i < quests.size(); i++) {
                            JSONObject q = quests.get(i);
                            String qid = q.optString("id", "");
                            JSONObject status = getUserStatus(q);
                            boolean done = (extractString(status, "completedAt", "completed_at") != null) || completedSet.contains(qid);
                            boolean enrolled = (extractString(status, "enrolledAt", "enrolled_at") != null);
                            if (done) {
                                completedSet.add(qid);
                            } else if (enrolled && isQuestValid(q) && !skippedSet.contains(qid)) {
                                queue.add(q);
                            }
                        }

                        if (!queue.isEmpty() && engineRunning) {
                            int workers = Math.min(Math.max(1, profile.maxWorkers), 10);
                            ExecutorService pool = Executors.newFixedThreadPool(workers);
                            List<Future<?>> futures = new ArrayList<Future<?>>();
                            for (int i = 0; i < queue.size(); i++) {
                                final JSONObject qRun = queue.get(i);
                                futures.add(pool.submit(new Runnable() {
                                    @Override
                                    public void run() {
                                        executeQuest(qRun);
                                    }
                                }));
                            }
                            pool.shutdown();
                            for (int i = 0; i < futures.size(); i++) {
                                if (!engineRunning) break;
                                try {
                                    futures.get(i).get();
                                } catch (Exception ignored) {
                                }
                            }
                            pool.shutdownNow();
                        } else if (queue.isEmpty() && !quests.isEmpty()) {
                            if (callback != null) callback.onAllQuestsCompletedCheck();
                        }

                        for (int s = 0; s < 45; s++) {
                            if (!engineRunning) break;
                            sleepQuietly(1000L);
                        }
                    }
                    engineRunning = false;
                    if (callback != null) callback.onAccountStateChanged(profile.userId);
                }
            });
            loopThread.start();
        }

        public void stopEngine() {
            engineRunning = false;
            forcedPrioritySet.clear();
            if (loopThread != null) loopThread.interrupt();
            notifyProgressChanged();
            if (callback != null) callback.onAccountStateChanged(profile.userId);
        }

        public boolean areAllValidQuestsCompleted() {
            if (questItems.isEmpty()) return false;
            synchronized (questItems) {
                for (int i = 0; i < questItems.size(); i++) {
                    QuestItem item = questItems.get(i);
                    if (!item.completed && !skippedSet.contains(item.id)) {
                        return false;
                    }
                }
            }
            return true;
        }

        private void parseAndPublishQuests(List<JSONObject> rawQuests) {
            List<QuestItem> parsed = new ArrayList<QuestItem>();
            for (int i = 0; i < rawQuests.size(); i++) {
                JSONObject q = rawQuests.get(i);
                String qid = q.optString("id", "");
                boolean valid = isQuestValid(q);
                JSONObject status = getUserStatus(q);
                boolean completed = (extractString(status, "completedAt", "completed_at") != null) || completedSet.contains(qid);

                if (!valid && !completed) continue;

                latestQuestJsonMap.put(qid, q);
                QuestItem item = new QuestItem();
                item.id = qid;
                item.title = getQuestTitle(q);
                item.subtitle = getGameSubtitle(q);
                item.reward = getRewardName(q);
                item.taskType = getTaskType(q);
                item.imageUrl = getQuestImageUrl(q);
                item.targetSeconds = getTargetSeconds(q);

                double apiDone = getCompletedSeconds(q);
                Double liveDone = liveProgressMap.get(qid);
                item.doneSeconds = (liveDone != null && liveDone > apiDone) ? liveDone : apiDone;
                if (completed && item.targetSeconds > 0) {
                    item.doneSeconds = item.targetSeconds;
                    completedSet.add(qid);
                }
                item.enrolled = (extractString(status, "enrolledAt", "enrolled_at") != null);
                item.completed = completed;
                item.valid = valid;
                item.rawJson = q;
                parsed.add(item);
            }

            Collections.sort(parsed, new Comparator<QuestItem>() {
                @Override
                public int compare(QuestItem a, QuestItem b) {
                    return getOrder(a) - getOrder(b);
                }

                private int getOrder(QuestItem item) {
                    if (activeRunningSet.contains(item.id)) return 0;
                    if (!item.completed && !skippedSet.contains(item.id)) return 1;
                    if (skippedSet.contains(item.id)) return 2;
                    return 3;
                }
            });

            synchronized (questItems) {
                questItems.clear();
                questItems.addAll(parsed);
            }
            isLoadingQuests = false;
            if (callback != null) {
                callback.onQuestsUpdated(profile.userId);
            }
        }

        private void notifyProgressChanged() {
            synchronized (questItems) {
                for (int i = 0; i < questItems.size(); i++) {
                    QuestItem item = questItems.get(i);
                    Double live = liveProgressMap.get(item.id);
                    if (live != null && live > item.doneSeconds) {
                        item.doneSeconds = live;
                    }
                    if (completedSet.contains(item.id)) {
                        item.completed = true;
                        if (item.targetSeconds > 0) item.doneSeconds = item.targetSeconds;
                    }
                }
            }
            if (callback != null) {
                callback.onQuestsUpdated(profile.userId);
                callback.onAccountStateChanged(profile.userId);
            }
        }

        private void logAcc(String msg, String level) {
            if (callback != null) {
                String prefix = !TextUtils.isEmpty(profile.username) ? "[" + profile.username + "] " : "";
                callback.onLog(prefix + msg, level);
            }
        }
    }

    public static Object extractKey(JSONObject source, String... keys) {
        if (source == null) return null;
        for (int i = 0; i < keys.length; i++) {
            String k = keys[i];
            if (source.has(k) && !source.isNull(k)) return source.opt(k);
        }
        return null;
    }

    public static JSONObject extractObject(JSONObject source, String... keys) {
        Object val = extractKey(source, keys);
        return (val instanceof JSONObject) ? (JSONObject) val : null;
    }

    public static String extractString(JSONObject source, String... keys) {
        Object val = extractKey(source, keys);
        if (val != null) {
            String s = String.valueOf(val);
            if (!TextUtils.isEmpty(s) && !"null".equalsIgnoreCase(s)) return s;
        }
        return null;
    }

    public static JSONObject getTaskConfig(JSONObject quest) {
        JSONObject cfg = quest.optJSONObject("config");
        return extractObject(cfg, "taskConfig", "task_config", "taskConfigV2", "task_config_v2");
    }

    public static String getQuestTitle(JSONObject quest) {
        JSONObject cfg = quest.optJSONObject("config");
        if (cfg != null) {
            JSONObject msgs = cfg.optJSONObject("messages");
            String title = extractString(msgs, "questName", "quest_name");
            if (!TextUtils.isEmpty(title)) return title.trim();
            String game = extractString(msgs, "gameTitle", "game_title");
            if (!TextUtils.isEmpty(game)) return game.trim();
            JSONObject app = cfg.optJSONObject("application");
            if (app != null && !TextUtils.isEmpty(app.optString("name"))) return app.optString("name").trim();
        }
        return "Quest#" + quest.optString("id", "Unknown");
    }

    public static String getGameSubtitle(JSONObject quest) {
        JSONObject cfg = quest.optJSONObject("config");
        if (cfg != null) {
            JSONObject msgs = cfg.optJSONObject("messages");
            String game = extractString(msgs, "gameTitle", "game_title");
            String pub = extractString(msgs, "gamePublisher", "game_publisher");
            if (!TextUtils.isEmpty(game) && !TextUtils.isEmpty(pub)) return game.trim() + " • " + pub.trim();
            if (!TextUtils.isEmpty(game)) return game.trim();
        }
        return "Discord Quest";
    }

    public static String getRewardName(JSONObject quest) {
        JSONObject cfg = quest.optJSONObject("config");
        if (cfg == null) return "Discord Reward";
        JSONObject rewardsCfg = extractObject(cfg, "rewardsConfig", "rewards_config");
        if (rewardsCfg != null) {
            JSONArray rewards = rewardsCfg.optJSONArray("rewards");
            if (rewards != null && rewards.length() > 0) {
                JSONObject first = rewards.optJSONObject(0);
                if (first != null) {
                    String rName = extractString(first.optJSONObject("messages"), "name", "nameWithArticle", "name_with_article");
                    if (!TextUtils.isEmpty(rName)) return rName.trim();
                }
            }
        }
        return "Discord Quest Reward";
    }

    public static String getQuestImageUrl(JSONObject quest) {
        String qid = quest.optString("id", "");
        JSONObject cfg = quest.optJSONObject("config");
        if (cfg != null) {
            JSONObject assets = cfg.optJSONObject("assets");
            if (assets != null) {
                String path = extractString(assets, "hero", "gameTile", "game_tile", "questBarHero", "quest_bar_hero", "logotype");
                if (!TextUtils.isEmpty(path)) {
                    if (path.startsWith("http")) return path;
                    if (path.startsWith("quests/")) return "https://cdn.discordapp.com/" + path;
                    return "https://cdn.discordapp.com/quests/" + qid + "/" + path;
                }
            }
        }
        return null;
    }

    public static JSONObject getUserStatus(JSONObject quest) {
        JSONObject st = extractObject(quest, "userStatus", "user_status");
        return st != null ? st : new JSONObject();
    }

    public static long parseIsoTimestampSeconds(String iso) {
        if (TextUtils.isEmpty(iso)) return 0L;
        try {
            String clean = iso.trim().replace("Z", "");
            int plus = clean.indexOf('+');
            if (plus > 0) clean = clean.substring(0, plus);
            int dot = clean.indexOf('.');
            if (dot > 0) clean = clean.substring(0, dot);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date d = sdf.parse(clean);
            return d != null ? d.getTime() / 1000L : 0L;
        } catch (ParseException e) {
            return 0L;
        }
    }

    public static boolean isQuestValid(JSONObject quest) {
        JSONObject cfg = quest.optJSONObject("config");
        if (cfg != null) {
            String exp = extractString(cfg, "expiresAt", "expires_at");
            if (!TextUtils.isEmpty(exp)) {
                long expSec = parseIsoTimestampSeconds(exp);
                if (expSec > 0 && expSec <= (System.currentTimeMillis() / 1000L)) return false;
            }
        }
        JSONObject tCfg = getTaskConfig(quest);
        if (tCfg == null) return false;
        JSONObject tasks = tCfg.optJSONObject("tasks");
        if (tasks == null) return false;
        for (int i = 0; i < SUPPORTED_TASKS.length; i++) {
            if (tasks.has(SUPPORTED_TASKS[i]) && !tasks.isNull(SUPPORTED_TASKS[i])) return true;
        }
        return false;
    }

    public static String getTaskType(JSONObject quest) {
        JSONObject tCfg = getTaskConfig(quest);
        if (tCfg == null) return null;
        JSONObject tasks = tCfg.optJSONObject("tasks");
        if (tasks == null) return null;
        for (int i = 0; i < SUPPORTED_TASKS.length; i++) {
            if (tasks.has(SUPPORTED_TASKS[i]) && !tasks.isNull(SUPPORTED_TASKS[i])) return SUPPORTED_TASKS[i];
        }
        return null;
    }

    public static int getTargetSeconds(JSONObject quest) {
        JSONObject tCfg = getTaskConfig(quest);
        String tType = getTaskType(quest);
        if (tCfg == null || tType == null) return 0;
        JSONObject tasks = tCfg.optJSONObject("tasks");
        if (tasks == null) return 0;
        JSONObject obj = tasks.optJSONObject(tType);
        return obj != null ? obj.optInt("target", 0) : 0;
    }

    public static double getCompletedSeconds(JSONObject quest) {
        String tType = getTaskType(quest);
        if (tType == null) return 0;
        JSONObject prog = getUserStatus(quest).optJSONObject("progress");
        if (prog != null && prog.optJSONObject(tType) != null) {
            return prog.optJSONObject(tType).optDouble("value", 0);
        }
        return 0;
    }

    public static void sleepQuietly(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
        }
    }
}
