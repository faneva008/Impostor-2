package com.imposteurjuridique.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Insets;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceError;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;

/** Offline-only game. All assets are served from the APK, never from a server. */
public class MainActivity extends Activity {
    private static final String HOST = "appassets.androidplatform.net";
    private static final String START_URL = "https://" + HOST + "/assets/index.html";
    private static final int BACKGROUND = Color.rgb(250, 246, 249);
    private FrameLayout root;
    private WebView webView;
    private View privacyCover;
    private boolean resumed;
    private boolean pageReady;
    private boolean backPending;

    @SuppressLint("SetJavaScriptEnabled")
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = new FrameLayout(this);
        root.setBackgroundColor(BACKGROUND);
        setContentView(root);
        configureInsets();
        // Keep screenshots usable for visual feedback, but hide the task thumbnail.
        if (Build.VERSION.SDK_INT >= 33) {
            setRecentsScreenshotEnabled(false);
            getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                android.window.OnBackInvokedDispatcher.PRIORITY_DEFAULT, this::handleBack);
        }
        webView = new WebView(this);
        webView.setBackgroundColor(BACKGROUND);
        root.addView(webView, new FrameLayout.LayoutParams(-1, -1));
        privacyCover = new View(this);
        privacyCover.setBackgroundColor(BACKGROUND);
        privacyCover.setClickable(true);
        privacyCover.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
        root.addView(privacyCover, new FrameLayout.LayoutParams(-1, -1));
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setBlockNetworkLoads(true);
        settings.setGeolocationEnabled(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        settings.setSupportMultipleWindows(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setTextZoom(100);
        // Preserve the original light palette, including on Android dark mode.
        if (Build.VERSION.SDK_INT >= 33) settings.setAlgorithmicDarkeningAllowed(false);
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        webView.setWebChromeClient(new WebChromeClient()); // Local alert/confirm dialogs.
        webView.setWebViewClient(new WebViewClient() {
            @Override public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if (!"GET".equals(request.getMethod()) || !"https".equals(uri.getScheme()) || !HOST.equals(uri.getHost())) return denied();
                String path = uri.getPath();
                if (path == null || !path.startsWith("/assets/") || path.contains("..") || path.contains("\\")) return denied();
                String asset = path.substring("/assets/".length());
                try {
                    String type = mime(asset);
                    String encoding = type.startsWith("text/") || type.equals("application/javascript") ? "UTF-8" : null;
                    WebResourceResponse response = new WebResourceResponse(type, encoding, getAssets().open(asset));
                    response.setResponseHeaders(Collections.singletonMap("Cache-Control", "no-store"));
                    return response;
                } catch (IOException e) { return denied(); }
            }
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return !START_URL.equals(request.getUrl().toString());
            }
            @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return !START_URL.equals(url);
            }
            @Override public void onPageFinished(WebView view, String url) {
                pageReady = START_URL.equals(url);
                concealSecretThenUncover();
            }
            @Override public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (!request.isForMainFrame()) return;
                pageReady = false;
                new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Affichage indisponible")
                    .setMessage("Impossible de charger le jeu local. Réessayez. Si le problème persiste, vérifiez la mise à jour d’Android System WebView.")
                    .setPositiveButton("Réessayer", (d, w) -> view.loadUrl(START_URL))
                    .setNegativeButton("Fermer", (d, w) -> finish()).show();
            }
            @Override public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
                pageReady = false;
                root.removeView(view);
                view.destroy();
                webView = null;
                privacyCover.setVisibility(View.VISIBLE);
                new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Relancer le jeu")
                    .setMessage("Android a arrêté l’affichage du jeu. Les réglages sont conservés, mais la partie en cours doit être recommencée.")
                    .setCancelable(false)
                    .setPositiveButton("Relancer", (d, w) -> recreate()).show();
                return true;
            }
        });
        webView.loadUrl(START_URL);
    }

    private static String mime(String path) {
        if (path.endsWith(".html")) return "text/html";
        if (path.endsWith(".css")) return "text/css";
        if (path.endsWith(".js")) return "application/javascript";
        if (path.endsWith(".woff2")) return "font/woff2";
        if (path.endsWith(".ttf")) return "font/ttf";
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        return "text/plain";
    }
    private static WebResourceResponse denied() {
        return new WebResourceResponse("text/plain", "UTF-8", 403, "Blocked", Collections.emptyMap(), new ByteArrayInputStream(new byte[0]));
    }

    private void configureInsets() {
        if (Build.VERSION.SDK_INT >= 30) {
            getWindow().setDecorFitsSystemWindows(false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarContrastEnforced(false);
            WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                int light = WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS | WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS;
                controller.setSystemBarsAppearance(light, light);
            }
            root.setOnApplyWindowInsetsListener((v, insets) -> {
                Insets bars = insets.getInsets(WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout());
                Insets keyboard = insets.getInsets(WindowInsets.Type.ime());
                v.setPadding(bars.left, bars.top, bars.right, Math.max(bars.bottom, keyboard.bottom));
                return WindowInsets.CONSUMED;
            });
            root.requestApplyInsets();
        } else if (Build.VERSION.SDK_INT >= 26) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
    }

    @Override public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        root.requestApplyInsets();
        if (webView != null && pageReady) webView.evaluateJavascript("hideSecret();", null);
    }
    @Override protected void onPause() {
        resumed = false;
        if (privacyCover != null) privacyCover.setVisibility(View.VISIBLE);
        if (webView != null) {
            webView.evaluateJavascript("if(typeof hideSecret==='function')hideSecret();", null);
            webView.onPause();
        }
        super.onPause();
    }
    @Override protected void onResume() {
        super.onResume();
        resumed = true;
        if (webView != null) { webView.onResume(); concealSecretThenUncover(); }
    }
    private void concealSecretThenUncover() {
        if (!resumed || !pageReady || webView == null) return;
        webView.evaluateJavascript("(function(){if(typeof hideSecret!=='function')return false;hideSecret();return true;})()", result -> {
            if (resumed && pageReady && "true".equals(result)) privacyCover.setVisibility(View.GONE);
        });
    }
    private void handleBack() {
        if (webView == null || !pageReady || backPending) return;
        backPending = true;
        webView.evaluateJavascript("handleAndroidBack()", result -> {
            backPending = false;
            if (isFinishing() || isDestroyed()) return;
            if (!"true".equals(result)) new AlertDialog.Builder(this)
                .setTitle("Quitter le jeu ?")
                .setMessage("Vos joueurs et vos réglages resteront enregistrés sur ce téléphone.")
                .setNegativeButton("Rester", null)
                .setPositiveButton("Quitter", (d, w) -> finish()).show();
        });
    }
    @Override public void onBackPressed() { handleBack(); }
    @Override protected void onDestroy() {
        if (webView != null) { root.removeView(webView); webView.destroy(); webView = null; }
        super.onDestroy();
    }
}
