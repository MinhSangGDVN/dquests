package com.minhsanggd.dquests;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.animation.LinearInterpolator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.util.LruCache;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.minhsanggd.dquests.DquestsCore.AccountProfile;
import com.minhsanggd.dquests.DquestsCore.AccountSession;
import com.minhsanggd.dquests.DquestsCore.AuthResult;
import com.minhsanggd.dquests.DquestsCore.HttpResponse;
import com.minhsanggd.dquests.DquestsCore.I18n;
import com.minhsanggd.dquests.DquestsCore.QuestItem;
import com.minhsanggd.dquests.DquestsCore.RpcConfig;
import com.minhsanggd.dquests.DquestsCore.UiCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity implements UiCallback {

    private static final String PREFS_NAME = "dquests_v2_prefs";
    private static final String KEY_ACCOUNTS_JSON = "accounts_json";
    private static final String KEY_ACTIVE_USER_ID = "active_user_id";
    private static final String KEY_BANNER_DISMISSED = "banner_dismissed_once";
    private static final String KEY_KEEP_SCREEN = "keep_screen_on";
    private static final String KEY_AUTO_EXIT_100 = "auto_exit_when_done";
    private static final String KEY_LANG_EN = "lang_is_en";
    private static final String KEY_FIRST_LAUNCH_DONE = "first_launch_done";

    private FrameLayout layoutWelcome;
    private TextView tvWelcomeAppTitle;
    private TextView tvWelcomeSubtitle;
    private LinearLayout welcomeLangSwitcher;
    private TextView btnWelcomeLangVi;
    private TextView btnWelcomeLangEn;
    private LinearLayout cardWelcomeFeatures;
    private TextView tvWelcomeFeatTitle;
    private TextView tvWelcomeFeatDesc;
    private LinearLayout cardWelcomePrivacy;
    private TextView tvWelcomePrivTitle;
    private TextView tvWelcomePrivDesc;
    private LinearLayout cardWelcomeRisk;
    private TextView tvWelcomeRiskTitle;
    private TextView tvWelcomeRiskDesc;
    private TextView btnWelcomeTerms;
    private TextView btnWelcomeConfirm;

    private LinearLayout mainContentLayer;
    private TextView tvAppTitle;
    private TextView tvAuthorBadge;
    private TextView tvUserStatus;
    private FrameLayout btnRefreshIcon;
    private ImageView ivRefreshVector;
    private FrameLayout btnUserAvatar;
    private TextView tvHeaderAvatarFallback;
    private ImageView ivHeaderAvatar;
    private View viewHeaderPresenceDot;

    private LinearLayout bannerKeepAlive;
    private TextView tvBannerTitle;
    private TextView tvBannerDesc;
    private TextView btnDismissBanner;

    private ScrollView layoutAccountPicker;
    private LinearLayout cardAccountPicker;
    private TextView tvPickerTitle;
    private TextView tvPickerSubtitle;
    private LinearLayout containerSavedAccountsStartup;
    private TextView btnStartupFarmAll;
    private TextView btnStartupAddAccount;

    private ScrollView layoutLogin;
    private LinearLayout cardLogin;
    private TextView tvLoginCardTitle;
    private TextView tvLoginCardSubtitle;
    private TextView btnBackToAccounts;
    private LinearLayout loginModeSwitcher;
    private TextView tabLoginCredentials;
    private TextView tabLoginToken;
    private LinearLayout sectionLoginCredentials;
    private TextView tvLabelEmail;
    private LinearLayout boxEmailInput;
    private EditText etEmail;
    private TextView tvLabelPassword;
    private RelativeLayout boxPasswordInput;
    private EditText etPassword;
    private TextView btnTogglePassword;
    private LinearLayout sectionMfaInput;
    private TextView tvLabelMfa;
    private LinearLayout boxMfaInput;
    private EditText etMfaCode;
    private LinearLayout sectionLoginToken;
    private TextView tvLabelToken;
    private RelativeLayout boxTokenInput;
    private EditText etToken;
    private TextView btnToggleToken;
    private TextView tvLoginStatus;
    private FrameLayout btnConnectContainer;
    private TextView tvBtnConnectLabel;
    private ProgressBar pbConnectSpinner;
    private LinearLayout cardInfo;
    private TextView tvInfoTitle;
    private TextView tvInfoDesc;

    private LinearLayout layoutWorkspace;
    private LinearLayout btnWorkerDropdown;
    private TextView tvWorkerLabelTop;
    private TextView tvWorkerSelected;
    private TextView btnStartStopFarm;
    private LinearLayout tabBarContainer;
    private TextView tabQuests;
    private TextView tabLogs;
    private FrameLayout rootContainer;
    private FrameLayout overlayAccountLogoutPopup;
    private FrameLayout pageQuests;
    private ListView lvQuests;
    private LinearLayout skeletonQuestsContainer;
    private final List<DquestsCore.SkeletonShimmerDrawable> skeletonDrawables = new ArrayList<DquestsCore.SkeletonShimmerDrawable>();
    private ValueAnimator skeletonAnimator;

    private LinearLayout customTipBar;
    private TextView tvCustomTipText;
    private Runnable hideTipRunnable;

    private LinearLayout emptyQuestsView;
    private TextView tvEmptyTitle;
    private TextView tvEmptyDesc;
    private LinearLayout pageLogs;
    private LinearLayout cardLogConsole;
    private TextView tvLogHeaderTitle;
    private TextView btnClearLogs;
    private ScrollView svLogs;
    private TextView tvLogsContent;

    private FrameLayout overlayWorkerSelector;
    private LinearLayout sheetWorkerSelector;
    private TextView tvWorkerModalTitle;
    private TextView tvSpeedSectionLabel;
    private LinearLayout containerSpeedOptions;
    private LinearLayout boxWorkerWarning;
    private TextView tvWorkerWarnTitle;
    private TextView tvWorkerWarnDesc;
    private LinearLayout containerWorkerOptions;
    private TextView btnCloseWorkerSheet;
    private TextView btnConfirmStartFarm;

    private FrameLayout overlayProfileMenu;
    private LinearLayout sheetProfileMenu;
    private LinearLayout dragHandleArea;
    private View dragHandlePill;
    private LinearLayout boxMenuCurrentUser;
    private TextView tvMenuAvatarFallback;
    private ImageView ivMenuAvatar;
    private TextView tvMenuUsername;
    private TextView tvMenuUserid;
    private LinearLayout boxMenuMultiAccounts;
    private TextView tvMenuAccountsHeader;
    private TextView btnMenuAddAccount;
    private LinearLayout containerMenuAccountsList;
    private TextView btnMenuFarmAllAccounts;
    private LinearLayout containerMenuItems;

    private FrameLayout overlayHypesquad;
    private LinearLayout sheetHypesquad;
    private TextView tvHypesquadTitle;
    private TextView tvHypesquadSubtitle;
    private LinearLayout containerHypesquadHouses;
    private TextView tvHypesquadStatus;
    private TextView btnHypesquadRemove;
    private TextView btnHypesquadClose;
    private TextView btnHypesquadConfirm;

    private FrameLayout overlayRpc;
    private LinearLayout sheetRpc;
    private TextView tvRpcTitle;
    private TextView tvRpcStatusLabel;
    private LinearLayout containerPresenceTypes;
    private TextView tvRpcCustomHeader;
    private TextView btnRpcPickInstalledApp;
    private TextView btnRpcImportAppid;
    private LinearLayout containerInstalledAppsPicker;
    private TextView tvRpcActTypeLabel;
    private LinearLayout containerRpcActivityTypes;
    private EditText etRpcAppId;
    private EditText etRpcName;
    private EditText etRpcDetails;
    private EditText etRpcState;
    private EditText etRpcStreamUrl;
    private EditText etRpcLargeImage;
    private EditText etRpcLargeText;
    private EditText etRpcBtn1Label;
    private EditText etRpcBtn1Url;
    private TextView tvRpcTimeLabel;
    private LinearLayout containerRpcTimestamps;
    private TextView btnRpcStop;
    private TextView btnRpcClose;
    private TextView btnRpcApply;

    private Handler uiHandler;
    private SharedPreferences prefs;
    private LruCache<String, Bitmap> imageCache;
    private ExecutorService imageExecutor;

    private final Map<String, AccountSession> accountSessions = new LinkedHashMap<String, AccountSession>();
    private AccountSession currentSession;
    private QuestListAdapter questAdapter;
    private final StringBuilder logHtmlBuffer = new StringBuilder();

    private boolean isLoginByTokenMode = false;
    private boolean isPasswordVisible = false;
    private boolean isTokenVisible = false;
    private volatile boolean isNetworkConnected = true;
    private Thread networkMonitorThread;
    private String pendingMfaTicket = null;
    private boolean keepScreenOn = true;
    private boolean autoExitOn100 = false;
    private boolean isPromptingForStart = false;
    private boolean isShowingQuestsTab = true;

    private int tempSelectedWorkers = 2;
    private int tempSelectedSpeed = 1;
    private int selectedHypesquadHouse = 2;
    private String tempPresenceStatus = "none";
    private int tempRpcActivityType = 0;
    private int tempRpcTimestampMode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uiHandler = new Handler(Looper.getMainLooper());
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        I18n.isEn = prefs.getBoolean(KEY_LANG_EN, false);
        keepScreenOn = prefs.getBoolean(KEY_KEEP_SCREEN, true);
        autoExitOn100 = prefs.getBoolean(KEY_AUTO_EXIT_100, false);

        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        imageCache = new LruCache<String, Bitmap>(maxMemory / 8) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
        imageExecutor = Executors.newFixedThreadPool(4);

        bindViews();
        applyCustomStyling();
        setupInteractions();
        setupDraggableBottomSheet();
        applyKeepScreenOnFlag();

        loadSavedAccountsFromPrefs();
        applyLanguageStrings();

        startNetworkStatusMonitor();

        boolean firstLaunchDone = prefs.getBoolean(KEY_FIRST_LAUNCH_DONE, false);
        if (!firstLaunchDone) {
            showWelcomeScreen();
        } else if (!accountSessions.isEmpty()) {
            showStartupAccountPicker();
        } else {
            showLoginScreen(null);
        }
        checkAppUpdateSilentlyOnLaunch();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkMonitorThread != null) {
            networkMonitorThread.interrupt();
        }
        for (AccountSession session : accountSessions.values()) {
            session.shutdownAll();
        }
        if (imageExecutor != null) {
            imageExecutor.shutdownNow();
        }
    }

    @Override
    public void onBackPressed() {
        if (overlayAccountLogoutPopup != null && overlayAccountLogoutPopup.getVisibility() == View.VISIBLE) {
            overlayAccountLogoutPopup.setVisibility(View.GONE);
            return;
        }
        if (overlayHypesquad.getVisibility() == View.VISIBLE) {
            overlayHypesquad.setVisibility(View.GONE);
            return;
        }
        if (overlayRpc.getVisibility() == View.VISIBLE) {
            overlayRpc.setVisibility(View.GONE);
            return;
        }
        if (overlayWorkerSelector.getVisibility() == View.VISIBLE) {
            overlayWorkerSelector.setVisibility(View.GONE);
            return;
        }
        if (overlayProfileMenu.getVisibility() == View.VISIBLE) {
            closeProfileMenuAnimated();
            return;
        }
        super.onBackPressed();
    }

    private void bindViews() {
        rootContainer = (FrameLayout) findViewById(R.id.root_container);
        mainContentLayer = (LinearLayout) findViewById(R.id.main_content_layer);

        layoutWelcome = (FrameLayout) findViewById(R.id.layout_welcome);
        tvWelcomeAppTitle = (TextView) findViewById(R.id.tv_welcome_app_title);
        tvWelcomeSubtitle = (TextView) findViewById(R.id.tv_welcome_subtitle);
        welcomeLangSwitcher = (LinearLayout) findViewById(R.id.welcome_lang_switcher);
        btnWelcomeLangVi = (TextView) findViewById(R.id.btn_welcome_lang_vi);
        btnWelcomeLangEn = (TextView) findViewById(R.id.btn_welcome_lang_en);
        cardWelcomeFeatures = (LinearLayout) findViewById(R.id.card_welcome_features);
        tvWelcomeFeatTitle = (TextView) findViewById(R.id.tv_welcome_feat_title);
        tvWelcomeFeatDesc = (TextView) findViewById(R.id.tv_welcome_feat_desc);
        cardWelcomePrivacy = (LinearLayout) findViewById(R.id.card_welcome_privacy);
        tvWelcomePrivTitle = (TextView) findViewById(R.id.tv_welcome_priv_title);
        tvWelcomePrivDesc = (TextView) findViewById(R.id.tv_welcome_priv_desc);
        cardWelcomeRisk = (LinearLayout) findViewById(R.id.card_welcome_risk);
        tvWelcomeRiskTitle = (TextView) findViewById(R.id.tv_welcome_risk_title);
        tvWelcomeRiskDesc = (TextView) findViewById(R.id.tv_welcome_risk_desc);
        btnWelcomeTerms = (TextView) findViewById(R.id.btn_welcome_terms);
        btnWelcomeConfirm = (TextView) findViewById(R.id.btn_welcome_confirm);
        tvAppTitle = (TextView) findViewById(R.id.tv_app_title);
        tvAuthorBadge = (TextView) findViewById(R.id.tv_author_badge);
        tvUserStatus = (TextView) findViewById(R.id.tv_user_status);
        btnRefreshIcon = (FrameLayout) findViewById(R.id.btn_refresh_icon);
        ivRefreshVector = (ImageView) findViewById(R.id.iv_refresh_vector);
        btnUserAvatar = (FrameLayout) findViewById(R.id.btn_user_avatar);
        tvHeaderAvatarFallback = (TextView) findViewById(R.id.tv_header_avatar_fallback);
        ivHeaderAvatar = (ImageView) findViewById(R.id.iv_header_avatar);
        viewHeaderPresenceDot = findViewById(R.id.view_header_presence_dot);

        bannerKeepAlive = (LinearLayout) findViewById(R.id.banner_keep_alive);
        tvBannerTitle = (TextView) findViewById(R.id.tv_banner_title);
        tvBannerDesc = (TextView) findViewById(R.id.tv_banner_desc);
        btnDismissBanner = (TextView) findViewById(R.id.btn_dismiss_banner);

        layoutAccountPicker = (ScrollView) findViewById(R.id.layout_account_picker);
        cardAccountPicker = (LinearLayout) findViewById(R.id.card_account_picker);
        tvPickerTitle = (TextView) findViewById(R.id.tv_picker_title);
        tvPickerSubtitle = (TextView) findViewById(R.id.tv_picker_subtitle);
        containerSavedAccountsStartup = (LinearLayout) findViewById(R.id.container_saved_accounts_startup);
        btnStartupFarmAll = (TextView) findViewById(R.id.btn_startup_farm_all);
        btnStartupAddAccount = (TextView) findViewById(R.id.btn_startup_add_account);

        layoutLogin = (ScrollView) findViewById(R.id.layout_login);
        cardLogin = (LinearLayout) findViewById(R.id.card_login);
        tvLoginCardTitle = (TextView) findViewById(R.id.tv_login_card_title);
        tvLoginCardSubtitle = (TextView) findViewById(R.id.tv_login_card_subtitle);
        btnBackToAccounts = (TextView) findViewById(R.id.btn_back_to_accounts);
        loginModeSwitcher = (LinearLayout) findViewById(R.id.login_mode_switcher);
        tabLoginCredentials = (TextView) findViewById(R.id.tab_login_credentials);
        tabLoginToken = (TextView) findViewById(R.id.tab_login_token);
        sectionLoginCredentials = (LinearLayout) findViewById(R.id.section_login_credentials);
        tvLabelEmail = (TextView) findViewById(R.id.tv_label_email);
        boxEmailInput = (LinearLayout) findViewById(R.id.box_email_input);
        etEmail = (EditText) findViewById(R.id.et_email);
        tvLabelPassword = (TextView) findViewById(R.id.tv_label_password);
        boxPasswordInput = (RelativeLayout) findViewById(R.id.box_password_input);
        etPassword = (EditText) findViewById(R.id.et_password);
        btnTogglePassword = (TextView) findViewById(R.id.btn_toggle_password);
        sectionMfaInput = (LinearLayout) findViewById(R.id.section_mfa_input);
        tvLabelMfa = (TextView) findViewById(R.id.tv_label_mfa);
        boxMfaInput = (LinearLayout) findViewById(R.id.box_mfa_input);
        etMfaCode = (EditText) findViewById(R.id.et_mfa_code);
        sectionLoginToken = (LinearLayout) findViewById(R.id.section_login_token);
        tvLabelToken = (TextView) findViewById(R.id.tv_label_token);
        boxTokenInput = (RelativeLayout) findViewById(R.id.box_token_input);
        etToken = (EditText) findViewById(R.id.et_token);
        btnToggleToken = (TextView) findViewById(R.id.btn_toggle_token);
        tvLoginStatus = (TextView) findViewById(R.id.tv_login_status);
        btnConnectContainer = (FrameLayout) findViewById(R.id.btn_connect_container);
        tvBtnConnectLabel = (TextView) findViewById(R.id.tv_btn_connect_label);
        pbConnectSpinner = (ProgressBar) findViewById(R.id.pb_connect_spinner);
        cardInfo = (LinearLayout) findViewById(R.id.card_info);
        tvInfoTitle = (TextView) findViewById(R.id.tv_info_title);
        tvInfoDesc = (TextView) findViewById(R.id.tv_info_desc);

        layoutWorkspace = (LinearLayout) findViewById(R.id.layout_workspace);
        btnWorkerDropdown = (LinearLayout) findViewById(R.id.btn_worker_dropdown);
        tvWorkerLabelTop = (TextView) findViewById(R.id.tv_worker_label_top);
        tvWorkerSelected = (TextView) findViewById(R.id.tv_worker_selected);
        btnStartStopFarm = (TextView) findViewById(R.id.btn_start_stop_farm);
        tabBarContainer = (LinearLayout) findViewById(R.id.tab_bar_container);
        tabQuests = (TextView) findViewById(R.id.tab_quests);
        tabLogs = (TextView) findViewById(R.id.tab_logs);
        pageQuests = (FrameLayout) findViewById(R.id.page_quests);
        lvQuests = (ListView) findViewById(R.id.lv_quests);
        emptyQuestsView = (LinearLayout) findViewById(R.id.empty_quests_view);
        tvEmptyTitle = (TextView) findViewById(R.id.tv_empty_title);
        tvEmptyDesc = (TextView) findViewById(R.id.tv_empty_desc);
        pageLogs = (LinearLayout) findViewById(R.id.page_logs);
        cardLogConsole = (LinearLayout) findViewById(R.id.card_log_console);
        tvLogHeaderTitle = (TextView) findViewById(R.id.tv_log_header_title);
        btnClearLogs = (TextView) findViewById(R.id.btn_clear_logs);
        svLogs = (ScrollView) findViewById(R.id.sv_logs);
        tvLogsContent = (TextView) findViewById(R.id.tv_logs_content);

        overlayWorkerSelector = (FrameLayout) findViewById(R.id.overlay_worker_selector);
        sheetWorkerSelector = (LinearLayout) findViewById(R.id.sheet_worker_selector);
        tvWorkerModalTitle = (TextView) findViewById(R.id.tv_worker_modal_title);
        tvSpeedSectionLabel = (TextView) findViewById(R.id.tv_speed_section_label);
        containerSpeedOptions = (LinearLayout) findViewById(R.id.container_speed_options);
        boxWorkerWarning = (LinearLayout) findViewById(R.id.box_worker_warning);
        tvWorkerWarnTitle = (TextView) findViewById(R.id.tv_worker_warn_title);
        tvWorkerWarnDesc = (TextView) findViewById(R.id.tv_worker_warn_desc);
        containerWorkerOptions = (LinearLayout) findViewById(R.id.container_worker_options);
        btnCloseWorkerSheet = (TextView) findViewById(R.id.btn_close_worker_sheet);
        btnConfirmStartFarm = (TextView) findViewById(R.id.btn_confirm_start_farm);

        overlayProfileMenu = (FrameLayout) findViewById(R.id.overlay_profile_menu);
        sheetProfileMenu = (LinearLayout) findViewById(R.id.sheet_profile_menu);
        dragHandleArea = (LinearLayout) findViewById(R.id.drag_handle_area);
        dragHandlePill = findViewById(R.id.drag_handle_pill);
        boxMenuCurrentUser = (LinearLayout) findViewById(R.id.box_menu_current_user);
        tvMenuAvatarFallback = (TextView) findViewById(R.id.tv_menu_avatar_fallback);
        ivMenuAvatar = (ImageView) findViewById(R.id.iv_menu_avatar);
        tvMenuUsername = (TextView) findViewById(R.id.tv_menu_username);
        tvMenuUserid = (TextView) findViewById(R.id.tv_menu_userid);
        boxMenuMultiAccounts = (LinearLayout) findViewById(R.id.box_menu_multi_accounts);
        tvMenuAccountsHeader = (TextView) findViewById(R.id.tv_menu_accounts_header);
        btnMenuAddAccount = (TextView) findViewById(R.id.btn_menu_add_account);
        containerMenuAccountsList = (LinearLayout) findViewById(R.id.container_menu_accounts_list);
        btnMenuFarmAllAccounts = (TextView) findViewById(R.id.btn_menu_farm_all_accounts);
        containerMenuItems = (LinearLayout) findViewById(R.id.container_menu_items);

        overlayHypesquad = (FrameLayout) findViewById(R.id.overlay_hypesquad);
        sheetHypesquad = (LinearLayout) findViewById(R.id.sheet_hypesquad);
        tvHypesquadTitle = (TextView) findViewById(R.id.tv_hypesquad_title);
        tvHypesquadSubtitle = (TextView) findViewById(R.id.tv_hypesquad_subtitle);
        containerHypesquadHouses = (LinearLayout) findViewById(R.id.container_hypesquad_houses);
        tvHypesquadStatus = (TextView) findViewById(R.id.tv_hypesquad_status);
        btnHypesquadRemove = (TextView) findViewById(R.id.btn_hypesquad_remove);
        btnHypesquadClose = (TextView) findViewById(R.id.btn_hypesquad_close);
        btnHypesquadConfirm = (TextView) findViewById(R.id.btn_hypesquad_confirm);

        overlayRpc = (FrameLayout) findViewById(R.id.overlay_rpc);
        sheetRpc = (LinearLayout) findViewById(R.id.sheet_rpc);
        tvRpcTitle = (TextView) findViewById(R.id.tv_rpc_title);
        tvRpcStatusLabel = (TextView) findViewById(R.id.tv_rpc_status_label);
        containerPresenceTypes = (LinearLayout) findViewById(R.id.container_presence_types);
        tvRpcCustomHeader = (TextView) findViewById(R.id.tv_rpc_custom_header);
        btnRpcPickInstalledApp = (TextView) findViewById(R.id.btn_rpc_pick_installed_app);
        btnRpcImportAppid = (TextView) findViewById(R.id.btn_rpc_import_appid);
        containerInstalledAppsPicker = (LinearLayout) findViewById(R.id.container_installed_apps_picker);
        tvRpcActTypeLabel = (TextView) findViewById(R.id.tv_rpc_act_type_label);
        containerRpcActivityTypes = (LinearLayout) findViewById(R.id.container_rpc_activity_types);
        etRpcAppId = (EditText) findViewById(R.id.et_rpc_app_id);
        etRpcName = (EditText) findViewById(R.id.et_rpc_name);
        etRpcDetails = (EditText) findViewById(R.id.et_rpc_details);
        etRpcState = (EditText) findViewById(R.id.et_rpc_state);
        etRpcStreamUrl = (EditText) findViewById(R.id.et_rpc_stream_url);
        etRpcLargeImage = (EditText) findViewById(R.id.et_rpc_large_image);
        etRpcLargeText = (EditText) findViewById(R.id.et_rpc_large_text);
        etRpcBtn1Label = (EditText) findViewById(R.id.et_rpc_btn1_label);
        etRpcBtn1Url = (EditText) findViewById(R.id.et_rpc_btn1_url);
        tvRpcTimeLabel = (TextView) findViewById(R.id.tv_rpc_time_label);
        containerRpcTimestamps = (LinearLayout) findViewById(R.id.container_rpc_timestamps);
        btnRpcStop = (TextView) findViewById(R.id.btn_rpc_stop);
        btnRpcClose = (TextView) findViewById(R.id.btn_rpc_close);
        btnRpcApply = (TextView) findViewById(R.id.btn_rpc_apply);

        questAdapter = new QuestListAdapter();
        lvQuests.setAdapter(questAdapter);

        setupSkeletonQuestsView();
        setupCustomFloatingTipBar();
        setupAccountLogoutPopupView();
    }

    private void applyCustomStyling() {
        tvAuthorBadge.setBackground(createRoundedRect(0xFFEEF2FF, dp(6), 1, 0xFFC7D2FE));
        btnRefreshIcon.setBackground(createRoundedRect(0xFFF8FAFC, dp(20), 1, 0xFFCBD5E1));
        ivRefreshVector.setImageDrawable(DquestsCore.createVectorIconDrawable(DquestsCore.ICON_REFRESH, 0xFF334155));

        welcomeLangSwitcher.setBackground(createRoundedRect(0xFFF1F5F9, dp(10), 1, 0xFFE2E8F0));
        cardWelcomeFeatures.setBackground(createRoundedRect(0xFFEEF2FF, dp(14), 1, 0xFFC7D2FE));
        cardWelcomePrivacy.setBackground(createRoundedRect(0xFFECFDF5, dp(14), 1, 0xFFA7F3D0));
        cardWelcomeRisk.setBackground(createRoundedRect(0xFFFFFBEB, dp(14), 1, 0xFFFDE68A));
        btnWelcomeConfirm.setBackground(createRoundedRect(0xFF5865F2, dp(10), 0, 0));
        updateWelcomeLangSwitcher();

        tvHeaderAvatarFallback.setBackground(createOvalDrawable(0xFF5865F2, 2, 0xFFE2E8F0));
        tvMenuAvatarFallback.setBackground(createOvalDrawable(0xFF5865F2, 0, 0));

        bannerKeepAlive.setBackground(createRoundedRect(0xFFFFFBEB, dp(10), 1, 0xFFFDE68A));
        btnDismissBanner.setBackground(null);
        btnDismissBanner.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        if (prefs.getBoolean(KEY_BANNER_DISMISSED, false)) {
            bannerKeepAlive.setVisibility(View.GONE);
        }

        cardAccountPicker.setBackground(createRoundedRect(0xFFFFFFFF, dp(16), 1, 0xFFE2E8F0));
        btnStartupFarmAll.setBackground(createRoundedRect(0xFF10B981, dp(10), 0, 0));
        btnStartupAddAccount.setBackground(createRoundedRect(0xFFEEF2FF, dp(10), 1, 0xFFC7D2FE));

        cardLogin.setBackground(createRoundedRect(0xFFFFFFFF, dp(16), 1, 0xFFE2E8F0));
        btnBackToAccounts.setBackground(createRoundedRect(0xFFEEF2FF, dp(8), 1, 0xFFC7D2FE));
        loginModeSwitcher.setBackground(createRoundedRect(0xFFF1F5F9, dp(10), 1, 0xFFE2E8F0));
        boxEmailInput.setBackground(createRoundedRect(0xFFF8FAFC, dp(10), 1, 0xFFCBD5E1));
        boxPasswordInput.setBackground(createRoundedRect(0xFFF8FAFC, dp(10), 1, 0xFFCBD5E1));
        boxMfaInput.setBackground(createRoundedRect(0xFFFFFBEB, dp(10), 1, 0xFFF59E0B));
        boxTokenInput.setBackground(createRoundedRect(0xFFF8FAFC, dp(10), 1, 0xFFCBD5E1));
        btnConnectContainer.setBackground(createRoundedRect(0xFF5865F2, dp(10), 0, 0));
        if (pbConnectSpinner.getIndeterminateDrawable() != null) {
            pbConnectSpinner.getIndeterminateDrawable().setColorFilter(0xFFFFFFFF, PorterDuff.Mode.SRC_IN);
        }
        cardInfo.setBackground(createRoundedRect(0xFFFFFFFF, dp(14), 1, 0xFFE2E8F0));

        btnWorkerDropdown.setBackground(createRoundedRect(0xFFF8FAFC, dp(10), 1, 0xFFCBD5E1));
        btnStartStopFarm.setBackground(createRoundedRect(0xFF5865F2, dp(10), 0, 0));
        tabBarContainer.setBackground(createRoundedRect(0xFFF1F5F9, dp(10), 1, 0xFFE2E8F0));
        cardLogConsole.setBackground(createRoundedRect(0xFFFFFFFF, dp(12), 1, 0xFFE2E8F0));
        btnClearLogs.setBackground(createRoundedRect(0xFFF1F5F9, dp(6), 1, 0xFFE2E8F0));

        sheetWorkerSelector.setBackground(createRoundedRect(0xFFFFFFFF, dp(16), 1, 0xFFE2E8F0));
        boxWorkerWarning.setBackground(createRoundedRect(0xFFFFFBEB, dp(10), 1, 0xFFFDE68A));
        btnCloseWorkerSheet.setBackground(createRoundedRect(0xFFF1F5F9, dp(8), 1, 0xFFE2E8F0));
        btnConfirmStartFarm.setBackground(createRoundedRect(0xFF5865F2, dp(8), 0, 0));

        GradientDrawable bottomSheetBg = new GradientDrawable();
        bottomSheetBg.setShape(GradientDrawable.RECTANGLE);
        bottomSheetBg.setColor(0xFFFFFFFF);
        float rTop = dp(22);
        bottomSheetBg.setCornerRadii(new float[]{rTop, rTop, rTop, rTop, 0, 0, 0, 0});
        bottomSheetBg.setStroke(1, 0xFFE2E8F0);
        sheetProfileMenu.setBackground(bottomSheetBg);
        dragHandlePill.setBackground(createRoundedRect(0xFFCBD5E1, dp(3), 0, 0));
        boxMenuCurrentUser.setBackground(createRoundedRect(0xFFF8FAFC, dp(12), 1, 0xFFE2E8F0));
        boxMenuMultiAccounts.setBackground(createRoundedRect(0xFFF8FAFC, dp(12), 1, 0xFFE2E8F0));
        btnMenuAddAccount.setBackground(createRoundedRect(0xFFEEF2FF, dp(6), 1, 0xFFC7D2FE));
        btnMenuFarmAllAccounts.setBackground(createRoundedRect(0xFF10B981, dp(8), 0, 0));

        sheetHypesquad.setBackground(createRoundedRect(0xFFFFFFFF, dp(16), 1, 0xFFE2E8F0));
        btnHypesquadRemove.setBackground(createRoundedRect(0xFFFEF2F2, dp(8), 1, 0xFFFECACA));
        btnHypesquadClose.setBackground(createRoundedRect(0xFFF1F5F9, dp(8), 1, 0xFFE2E8F0));
        btnHypesquadConfirm.setBackground(createRoundedRect(0xFF5865F2, dp(8), 0, 0));

        sheetRpc.setBackground(createRoundedRect(0xFFFFFFFF, dp(16), 1, 0xFFE2E8F0));
        btnRpcPickInstalledApp.setBackground(createRoundedRect(0xFFEEF2FF, dp(8), 1, 0xFFC7D2FE));
        btnRpcImportAppid.setBackground(createRoundedRect(0xFFECFDF5, dp(8), 1, 0xFFA7F3D0));
        styleRpcEditText(etRpcAppId);
        styleRpcEditText(etRpcName);
        styleRpcEditText(etRpcDetails);
        styleRpcEditText(etRpcState);
        styleRpcEditText(etRpcStreamUrl);
        styleRpcEditText(etRpcLargeImage);
        styleRpcEditText(etRpcLargeText);
        styleRpcEditText(etRpcBtn1Label);
        styleRpcEditText(etRpcBtn1Url);
        btnRpcStop.setBackground(createRoundedRect(0xFFFEF2F2, dp(8), 1, 0xFFFECACA));
        btnRpcClose.setBackground(createRoundedRect(0xFFF1F5F9, dp(8), 1, 0xFFE2E8F0));
        btnRpcApply.setBackground(createRoundedRect(0xFF5865F2, dp(8), 0, 0));

        updateLoginModeTabs();
        updateWorkspaceTabs(true);
    }

    private void styleRpcEditText(EditText et) {
        et.setBackground(createRoundedRect(0xFFF8FAFC, dp(8), 1, 0xFFCBD5E1));
    }

    private void setupInteractions() {
        btnWelcomeLangVi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                I18n.isEn = false;
                prefs.edit().putBoolean(KEY_LANG_EN, false).apply();
                updateWelcomeLangSwitcher();
                applyLanguageStrings();
            }
        });

        btnWelcomeLangEn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                I18n.isEn = true;
                prefs.edit().putBoolean(KEY_LANG_EN, true).apply();
                updateWelcomeLangSwitcher();
                applyLanguageStrings();
            }
        });

        btnWelcomeTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrlInBrowser(DquestsCore.TERMS_PRIVACY_URL);
            }
        });

        btnWelcomeConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                prefs.edit().putBoolean(KEY_FIRST_LAUNCH_DONE, true).apply();
                layoutWelcome.setVisibility(View.GONE);

                if (!accountSessions.isEmpty()) {
                    showStartupAccountPicker();
                } else {
                    showLoginScreen(null);
                }
            }
        });

        btnDismissBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bannerKeepAlive.setVisibility(View.GONE);
                prefs.edit().putBoolean(KEY_BANNER_DISMISSED, true).apply();
            }
        });

        btnRefreshIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivRefreshVector.animate().rotationBy(360f).setDuration(450).start();
                if (currentSession != null) {
                    showCustomTip(I18n.t("Đang làm mới danh sách Quests...", "Refreshing quests list..."));
                    currentSession.refreshQuestsAsync();
                }
            }
        });

        btnUserAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfileMenuAnimated();
            }
        });

        btnStartupFarmAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                farmAllAccountsSimultaneously();
                if (!accountSessions.isEmpty()) {
                    AccountSession first = accountSessions.values().iterator().next();
                    switchToAccountSession(first);
                }
            }
        });

        btnStartupAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginScreen(null);
            }
        });

        btnBackToAccounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!accountSessions.isEmpty()) {
                    showStartupAccountPicker();
                }
            }
        });

        tabLoginCredentials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLoginByTokenMode = false;
                updateLoginModeTabs();
            }
        });

        tabLoginToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLoginByTokenMode = true;
                updateLoginModeTabs();
            }
        });

        btnTogglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPasswordVisible = !isPasswordVisible;
                etPassword.setInputType(isPasswordVisible
                        ? (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
                        : (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS));
                btnTogglePassword.setText(isPasswordVisible ? I18n.t("ẨN", "HIDE") : I18n.t("HIỆN", "SHOW"));
                etPassword.setSelection(etPassword.getText().length());
            }
        });

        btnToggleToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTokenVisible = !isTokenVisible;
                etToken.setInputType(isTokenVisible
                        ? (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
                        : (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS));
                btnToggleToken.setText(isTokenVisible ? I18n.t("ẨN", "HIDE") : I18n.t("HIỆN", "SHOW"));
                etToken.setSelection(etToken.getText().length());
            }
        });

        btnConnectContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLoginAction();
            }
        });

        btnWorkerDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWorkerAndSpeedModal(false);
            }
        });

        btnStartStopFarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSession == null) return;
                if (currentSession.isFarming()) {
                    currentSession.stopEngine();
                    updateHeaderAndWorkspaceForCurrentAccount();
                } else {
                    showWorkerAndSpeedModal(true);
                }
            }
        });

        tabQuests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWorkspaceTabs(true);
            }
        });

        tabLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWorkspaceTabs(false);
            }
        });

        btnClearLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logHtmlBuffer.setLength(0);
                tvLogsContent.setText("");
                onLog(I18n.t("Đã xoá nhật ký.", "Cleared activity logs."), "info");
            }
        });

        overlayWorkerSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlayWorkerSelector.setVisibility(View.GONE);
            }
        });

        btnCloseWorkerSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlayWorkerSelector.setVisibility(View.GONE);
            }
        });

        btnConfirmStartFarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlayWorkerSelector.setVisibility(View.GONE);
                if (currentSession != null) {
                    currentSession.profile.maxWorkers = tempSelectedWorkers;
                    currentSession.profile.speedMode = tempSelectedSpeed;
                    saveAccountsToPrefs();
                    updateWorkerLabel();
                    if (isPromptingForStart && !currentSession.isFarming()) {
                        currentSession.startEngine();
                    }
                    updateHeaderAndWorkspaceForCurrentAccount();
                }
            }
        });

        btnMenuAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeProfileMenuAnimated();
                showLoginScreen(null);
            }
        });

        btnMenuFarmAllAccounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                farmAllAccountsSimultaneously();
                populateProfileMenuContent();
            }
        });

        overlayHypesquad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlayHypesquad.setVisibility(View.GONE);
            }
        });
        btnHypesquadClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlayHypesquad.setVisibility(View.GONE);
            }
        });
        btnHypesquadRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitHypeSquadChoice(0);
            }
        });
        btnHypesquadConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitHypeSquadChoice(selectedHypesquadHouse);
            }
        });

        overlayRpc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlayRpc.setVisibility(View.GONE);
            }
        });
        btnRpcClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlayRpc.setVisibility(View.GONE);
            }
        });
        btnRpcPickInstalledApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleInstalledAppsList();
            }
        });
        btnRpcImportAppid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importRpcFromDiscordAppId();
            }
        });
        btnRpcStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSession != null) {
                    currentSession.profile.presenceStatus = "none";
                    currentSession.profile.rpcConfig.enabled = false;
                    saveAccountsToPrefs();
                    currentSession.syncGatewayPresence();
                    updateHeaderAndWorkspaceForCurrentAccount();
                    showCustomTip(I18n.t("Đã tắt Trực tuyến & RPC.", "Disabled Presence & RPC."));
                }
                overlayRpc.setVisibility(View.GONE);
            }
        });
        btnRpcApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyRpcSettings();
            }
        });
    }

    private void setupDraggableBottomSheet() {
        overlayProfileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeProfileMenuAnimated();
            }
        });

        dragHandleArea.setOnTouchListener(new View.OnTouchListener() {
            private float downRawY = 0f;
            private float startTranslationY = 0f;
            private boolean moved = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getActionMasked();
                if (action == MotionEvent.ACTION_DOWN) {
                    downRawY = event.getRawY();
                    startTranslationY = sheetProfileMenu.getTranslationY();
                    moved = false;
                    return true;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    float dy = event.getRawY() - downRawY;
                    if (Math.abs(dy) > dp(6)) moved = true;
                    float newTrans = Math.max(0f, startTranslationY + dy);
                    sheetProfileMenu.setTranslationY(newTrans);

                    int h = Math.max(1, sheetProfileMenu.getHeight());
                    float ratio = 1f - Math.min(1f, newTrans / h);
                    if (overlayProfileMenu.getBackground() != null) {
                        overlayProfileMenu.getBackground().setAlpha((int) (255 * ratio));
                    }
                    return true;
                } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                    if (!moved) {

                        closeProfileMenuAnimated();
                    } else {
                        int h = sheetProfileMenu.getHeight();
                        if (sheetProfileMenu.getTranslationY() > h * 0.30f) {
                            closeProfileMenuAnimated();
                        } else {

                            sheetProfileMenu.animate().translationY(0f).setDuration(180).start();
                            if (overlayProfileMenu.getBackground() != null) {
                                overlayProfileMenu.getBackground().setAlpha(255);
                            }
                        }
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void openProfileMenuAnimated() {
        populateProfileMenuContent();
        applyBlurBackground(overlayProfileMenu);
        overlayProfileMenu.setVisibility(View.VISIBLE);
        if (overlayProfileMenu.getBackground() != null) {
            overlayProfileMenu.getBackground().setAlpha(255);
        }
        sheetProfileMenu.post(new Runnable() {
            @Override
            public void run() {
                int h = sheetProfileMenu.getHeight();
                if (h <= 0) h = dp(520);
                sheetProfileMenu.setTranslationY(h);
                sheetProfileMenu.animate().translationY(0f).setDuration(240).start();
            }
        });
    }

    private void closeProfileMenuAnimated() {
        int h = sheetProfileMenu.getHeight();
        if (h <= 0) h = dp(520);
        sheetProfileMenu.animate().translationY(h).setDuration(200).withEndAction(new Runnable() {
            @Override
            public void run() {
                overlayProfileMenu.setVisibility(View.GONE);
                sheetProfileMenu.setTranslationY(0f);
            }
        }).start();
    }

    private void loadSavedAccountsFromPrefs() {
        accountSessions.clear();
        String raw = prefs.getString(KEY_ACCOUNTS_JSON, "[]");
        String lastActiveId = prefs.getString(KEY_ACTIVE_USER_ID, "");
        try {
            JSONArray arr = new JSONArray(raw);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.optJSONObject(i);
                if (o != null) {
                    AccountProfile p = AccountProfile.fromJson(o);
                    if (!TextUtils.isEmpty(p.userId) && !TextUtils.isEmpty(p.token)) {
                        AccountSession session = new AccountSession(p, this);
                        accountSessions.put(p.userId, session);
                        if (p.userId.equals(lastActiveId)) {
                            currentSession = session;
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
        if (currentSession == null && !accountSessions.isEmpty()) {
            currentSession = accountSessions.values().iterator().next();
        }
    }

    private void saveAccountsToPrefs() {
        JSONArray arr = new JSONArray();
        for (AccountSession s : accountSessions.values()) {
            arr.put(s.profile.toJson());
        }
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString(KEY_ACCOUNTS_JSON, arr.toString());
        if (currentSession != null) {
            ed.putString(KEY_ACTIVE_USER_ID, currentSession.profile.userId);
        }
        ed.apply();
    }

    private void showWelcomeScreen() {
        layoutWelcome.setVisibility(View.VISIBLE);
        layoutAccountPicker.setVisibility(View.GONE);
        layoutLogin.setVisibility(View.GONE);
        layoutWorkspace.setVisibility(View.GONE);
        btnRefreshIcon.setVisibility(View.GONE);
        updateWelcomeLangSwitcher();
    }

    private void updateWelcomeLangSwitcher() {
        if (!I18n.isEn) {
            btnWelcomeLangVi.setBackground(createRoundedRect(0xFFFFFFFF, dp(8), 1, 0xFFE2E8F0));
            btnWelcomeLangVi.setTextColor(0xFF0F172A);
            btnWelcomeLangEn.setBackgroundColor(Color.TRANSPARENT);
            btnWelcomeLangEn.setTextColor(0xFF64748B);
        } else {
            btnWelcomeLangEn.setBackground(createRoundedRect(0xFFFFFFFF, dp(8), 1, 0xFFE2E8F0));
            btnWelcomeLangEn.setTextColor(0xFF0F172A);
            btnWelcomeLangVi.setBackgroundColor(Color.TRANSPARENT);
            btnWelcomeLangVi.setTextColor(0xFF64748B);
        }
    }

    private void showStartupAccountPicker() {
        layoutAccountPicker.setVisibility(View.VISIBLE);
        layoutLogin.setVisibility(View.GONE);
        layoutWorkspace.setVisibility(View.GONE);
        btnRefreshIcon.setVisibility(View.GONE);

        containerSavedAccountsStartup.removeAllViews();
        for (final AccountSession session : accountSessions.values()) {
            View row = buildAccountRowView(session, false);
            containerSavedAccountsStartup.addView(row);
        }
        updateHeaderAndWorkspaceForCurrentAccount();
    }

    private void showLoginScreen(AccountProfile expiredAccountToUpdate) {
        layoutAccountPicker.setVisibility(View.GONE);
        layoutWorkspace.setVisibility(View.GONE);
        layoutLogin.setVisibility(View.VISIBLE);
        btnRefreshIcon.setVisibility(View.GONE);
        btnBackToAccounts.setVisibility(accountSessions.isEmpty() ? View.GONE : View.VISIBLE);

        pendingMfaTicket = null;
        sectionMfaInput.setVisibility(View.GONE);
        etMfaCode.setText("");

        if (expiredAccountToUpdate != null) {
            tvLoginStatus.setVisibility(View.VISIBLE);
            tvLoginStatus.setTextColor(0xFFDC2626);
            tvLoginStatus.setText(I18n.t(
                    "Phiên đăng nhập của @" + expiredAccountToUpdate.username + " đã hết hạn. Vui lòng nhập lại mật khẩu hoặc Token mới!",
                    "Session for @" + expiredAccountToUpdate.username + " expired. Please re-enter password or new Token!"));
            if (!TextUtils.isEmpty(expiredAccountToUpdate.email)) {
                isLoginByTokenMode = false;
                etEmail.setText(expiredAccountToUpdate.email);
                etPassword.setText(expiredAccountToUpdate.password);
            } else {
                isLoginByTokenMode = true;
                etToken.setText("");
            }
            updateLoginModeTabs();
        } else {
            tvLoginStatus.setVisibility(View.GONE);
        }
        updateHeaderAndWorkspaceForCurrentAccount();
    }

    private void updateLoginModeTabs() {
        if (!isLoginByTokenMode) {
            tabLoginCredentials.setBackground(createRoundedRect(0xFFFFFFFF, dp(8), 1, 0xFFE2E8F0));
            tabLoginCredentials.setTextColor(0xFF0F172A);
            tabLoginToken.setBackgroundColor(Color.TRANSPARENT);
            tabLoginToken.setTextColor(0xFF64748B);
            sectionLoginCredentials.setVisibility(View.VISIBLE);
            sectionLoginToken.setVisibility(View.GONE);
        } else {
            tabLoginToken.setBackground(createRoundedRect(0xFFFFFFFF, dp(8), 1, 0xFFE2E8F0));
            tabLoginToken.setTextColor(0xFF0F172A);
            tabLoginCredentials.setBackgroundColor(Color.TRANSPARENT);
            tabLoginCredentials.setTextColor(0xFF64748B);
            sectionLoginCredentials.setVisibility(View.GONE);
            sectionLoginToken.setVisibility(View.VISIBLE);
        }
    }

    private void setLoginButtonLoading(boolean loading) {
        btnConnectContainer.setEnabled(!loading);
        tvBtnConnectLabel.setVisibility(loading ? View.GONE : View.VISIBLE);
        pbConnectSpinner.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void performLoginAction() {
        tvLoginStatus.setVisibility(View.GONE);
        if (isLoginByTokenMode) {
            final String token = etToken.getText().toString().trim();
            if (TextUtils.isEmpty(token)) {
                showLoginError(I18n.t("Vui lòng dán Discord Token hợp lệ.", "Please paste a valid Discord Token."));
                return;
            }
            setLoginButtonLoading(true);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    completeLoginWithToken(token, "", "");
                }
            }).start();
        } else {
            final String email = etEmail.getText().toString().trim();
            final String password = etPassword.getText().toString();
            final String mfaCode = etMfaCode.getText().toString().trim();
            final String ticketToSend = pendingMfaTicket;

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                showLoginError(I18n.t("Vui lòng nhập đầy đủ Email và Mật khẩu.", "Please enter both Email and Password."));
                return;
            }
            if (sectionMfaInput.getVisibility() == View.VISIBLE && TextUtils.isEmpty(mfaCode)) {
                showLoginError(I18n.t("Vui lòng nhập mã xác thực 2FA từ Authenticator.", "Please enter the 2FA code from Authenticator."));
                etMfaCode.requestFocus();
                return;
            }

            setLoginButtonLoading(true);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final AuthResult res = DquestsCore.loginWithEmailPassword(email, password, ticketToSend, mfaCode);
                    if (res.success && !TextUtils.isEmpty(res.token)) {
                        completeLoginWithToken(res.token, email, password);
                    } else {
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                setLoginButtonLoading(false);
                                if (res.mfaRequired) {
                                    pendingMfaTicket = res.mfaTicket;
                                    sectionMfaInput.setVisibility(View.VISIBLE);
                                    etMfaCode.requestFocus();
                                    
                                    if (TextUtils.isEmpty(mfaCode)) {
                                        showCustomTip(I18n.t("Tài khoản bật 2FA. Vui lòng nhập mã 6 số rồi bấm tiếp!", "2FA enabled. Enter the 6-digit code and tap continue!"));
                                    } else {
                                        showLoginError(res.errorMessage != null ? res.errorMessage : I18n.t("Mã 2FA không hợp lệ!", "Invalid 2FA code!"));
                                    }
                                } else {

                                    pendingMfaTicket = null;
                                    showLoginError(res.errorMessage != null ? res.errorMessage : I18n.t("Đăng nhập thất bại!", "Login failed!"));
                                }
                            }
                        });
                    }
                }
            }).start();
        }
    }

    private void completeLoginWithToken(String token, String email, String password) {
        AccountProfile tempProfile = new AccountProfile();
        tempProfile.token = token;
        tempProfile.email = email;
        tempProfile.password = password;

        final AccountSession session = new AccountSession(tempProfile, this);
        final boolean ok = session.verifyAndRefreshProfile();

        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                setLoginButtonLoading(false);
                if (ok && !TextUtils.isEmpty(session.profile.userId)) {
                    pendingMfaTicket = null;
                    sectionMfaInput.setVisibility(View.GONE);

                    AccountSession existing = accountSessions.get(session.profile.userId);
                    if (existing != null) {
                        existing.profile.token = session.profile.token;
                        existing.profile.username = session.profile.username;
                        existing.profile.avatarUrl = session.profile.avatarUrl;
                        existing.profile.tokenExpired = false;
                        existing.profile.loggedIn = true;
                        if (!TextUtils.isEmpty(session.profile.email)) {
                            existing.profile.email = session.profile.email;
                            existing.profile.password = session.profile.password;
                        }
                        switchToAccountSession(existing);
                    } else {
                        accountSessions.put(session.profile.userId, session);
                        switchToAccountSession(session);
                    }
                    saveAccountsToPrefs();
                } else {
                    showLoginError(I18n.t("Token không hợp lệ hoặc đã hết hạn!", "Invalid or expired Discord Token!"));
                }
            }
        });
    }

    private void showLoginError(String msg) {
        tvLoginStatus.setVisibility(View.VISIBLE);
        tvLoginStatus.setTextColor(0xFFEF4444);
        tvLoginStatus.setText(msg);
    }

    private void switchToAccountSession(AccountSession session) {
        this.currentSession = session;
        saveAccountsToPrefs();

        layoutAccountPicker.setVisibility(View.GONE);
        layoutLogin.setVisibility(View.GONE);
        layoutWorkspace.setVisibility(View.VISIBLE);
        btnRefreshIcon.setVisibility(View.VISIBLE);

        updateWorkerLabel();
        updateHeaderAndWorkspaceForCurrentAccount();
        session.refreshQuestsAsync();
    }

    private void farmAllAccountsSimultaneously() {
        if (accountSessions.isEmpty()) return;
        int startedCount = 0;
        for (AccountSession s : accountSessions.values()) {
            if (!s.isFarming()) {
                s.startEngine();
                startedCount++;
            }
        }
        showCustomTip(I18n.t("Đã kích hoạt Auto Farm đồng loạt cho " + accountSessions.size() + " tài khoản!",
                        "Activated Auto Farm across all " + accountSessions.size() + " accounts!"));
        updateHeaderAndWorkspaceForCurrentAccount();
    }

    private void populateProfileMenuContent() {
        if (currentSession != null) {
            AccountProfile p = currentSession.profile;
            tvMenuUsername.setText(p.username);
            tvMenuUserid.setText("ID: " + p.userId);
            loadAvatarIntoViews(p.avatarUrl, p.username, ivMenuAvatar, tvMenuAvatarFallback);
        } else {
            tvMenuUsername.setText(I18n.t("Chưa đăng nhập", "Not logged in"));
            tvMenuUserid.setText("ID: ---");
            ivMenuAvatar.setVisibility(View.GONE);
            tvMenuAvatarFallback.setVisibility(View.VISIBLE);
            tvMenuAvatarFallback.setText("?");
        }

        containerMenuAccountsList.removeAllViews();
        for (AccountSession session : accountSessions.values()) {
            containerMenuAccountsList.addView(buildAccountRowView(session, true));
        }
        btnMenuFarmAllAccounts.setVisibility(accountSessions.size() > 0 ? View.VISIBLE : View.GONE);
        containerMenuItems.removeAllViews();
        containerMenuItems.addView(createMenuOptionRow(
                DquestsCore.ICON_SCREEN,
                I18n.t("Giữ sáng màn hình trong app", "Keep screen awake in app"),
                keepScreenOn ? I18n.t("Đang BẬT", "ON") : I18n.t("Đang TẮT", "OFF"),
                keepScreenOn ? 0xFF10B981 : 0xFF64748B,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        keepScreenOn = !keepScreenOn;
                        prefs.edit().putBoolean(KEY_KEEP_SCREEN, keepScreenOn).apply();
                        applyKeepScreenOnFlag();
                        populateProfileMenuContent();
                    }
                }));

        containerMenuItems.addView(createMenuOptionRow(
                DquestsCore.ICON_BATTERY,
                I18n.t("Tắt tối ưu hoá pin (Chạy nền)", "Disable Battery Optimization"),
                I18n.t("Mở cài đặt", "Settings"),
                0xFFD97706,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeProfileMenuAnimated();
                        requestIgnoreBatteryOptimizations();
                    }
                }));

        containerMenuItems.addView(createMenuOptionRow(
                DquestsCore.ICON_AUTO_EXIT,
                I18n.t("Tự đóng app khi xong 100% Quest", "Auto-exit app when 100% done"),
                autoExitOn100 ? I18n.t("Đang BẬT", "ON") : I18n.t("Đang TẮT", "OFF"),
                autoExitOn100 ? 0xFF10B981 : 0xFF64748B,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        autoExitOn100 = !autoExitOn100;
                        prefs.edit().putBoolean(KEY_AUTO_EXIT_100, autoExitOn100).apply();
                        populateProfileMenuContent();
                    }
                }));

        containerMenuItems.addView(createMenuOptionRow(
                DquestsCore.ICON_HYPESQUAD,
                I18n.t("Chọn huy hiệu HypeSquad", "Claim HypeSquad Badge"),
                "Bravery / Brilliance / Balance",
                0xFF5865F2,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeProfileMenuAnimated();
                        openHypeSquadModal();
                    }
                }));

        String curPres = (currentSession != null) ? currentSession.profile.presenceStatus.toUpperCase(Locale.US) : "NONE";
        containerMenuItems.addView(createMenuOptionRow(
                DquestsCore.ICON_RPC,
                I18n.t("Tuỳ chỉnh Trực tuyến & RPC", "Custom Presence & Rich Presence"),
                curPres,
                0xFF059669,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeProfileMenuAnimated();
                        openRpcModal();
                    }
                }));

        containerMenuItems.addView(createMenuOptionRow(
                DquestsCore.ICON_UPDATE,
                I18n.t("Kiểm tra cập nhật", "Check for updates"),
                "v" + DquestsCore.APP_VERSION,
                0xFF475569,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkAppUpdateFromGithub();
                    }
                }));

        containerMenuItems.addView(createMenuOptionRow(
                DquestsCore.ICON_SHARE,
                I18n.t("Chia sẻ ứng dụng Dquests", "Share Dquests App"),
                "Socials",
                0xFF475569,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeProfileMenuAnimated();
                        shareApplicationIntent();
                    }
                }));

        containerMenuItems.addView(createMenuOptionRow(
                DquestsCore.ICON_LANGUAGE,
                I18n.t("Ngôn ngữ (Language)", "Language (Ngôn ngữ)"),
                I18n.isEn ? "English" : "Tiếng Việt",
                0xFF5865F2,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        I18n.isEn = !I18n.isEn;
                        prefs.edit().putBoolean(KEY_LANG_EN, I18n.isEn).apply();
                        applyLanguageStrings();
                        populateProfileMenuContent();
                    }
                }));

        containerMenuItems.addView(createMenuOptionRow(
                DquestsCore.ICON_GITHUB,
                "GitHub: MinhSangGDVN",
                "@MinhSangGDVN",
                0xFF0F172A,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeProfileMenuAnimated();
                        openUrlInBrowser(DquestsCore.GITHUB_PROFILE_URL);
                    }
                }));

        containerMenuItems.addView(createMenuOptionRow(
                DquestsCore.ICON_SOURCE_CODE,
                I18n.t("Mã nguồn (Source Code)", "Source Code"),
                "v" + DquestsCore.APP_VERSION,
                0xFF5865F2,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeProfileMenuAnimated();
                        openUrlInBrowser(DquestsCore.SOURCE_CODE_URL);
                    }
                }));

        if (currentSession != null) {
            containerMenuItems.addView(createMenuOptionRow(
                    DquestsCore.ICON_LOGOUT,
                    I18n.t("Đăng xuất tài khoản hiện tại", "Log out current account"),
                    "@" + currentSession.profile.username,
                    0xFFEF4444,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            closeProfileMenuAnimated();
                            logoutCurrentAccount();
                        }
                    }));
        }

        containerMenuItems.addView(createMenuOptionRow(
                DquestsCore.ICON_EXIT,
                I18n.t("Thoát ứng dụng hoàn toàn", "Exit Application"),
                I18n.t("Đóng tất cả", "Close all"),
                0xFFDC2626,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (AccountSession s : accountSessions.values()) {
                            s.shutdownAll();
                        }
                        finish();
                        System.exit(0);
                    }
                }));
    }

    private View buildAccountRowView(final AccountSession session, final boolean insideMenu) {
        final AccountProfile p = session.profile;
        final boolean isCurrent = (currentSession != null && p.userId.equals(currentSession.profile.userId));
        final boolean farming = session.isFarming();

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(10), dp(8), dp(10), dp(8));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dp(6);
        row.setLayoutParams(lp);

        if (isCurrent && insideMenu) {
            row.setBackground(createRoundedRect(0xFFEEF2FF, dp(10), 1, 0xFF5865F2));
        } else {
            row.setBackground(createRoundedRect(0xFFFFFFFF, dp(10), 1, 0xFFE2E8F0));
        }

        FrameLayout avBox = new FrameLayout(this);
        LinearLayout.LayoutParams avLp = new LinearLayout.LayoutParams(dp(36), dp(36));
        avLp.rightMargin = dp(10);
        avBox.setLayoutParams(avLp);

        TextView tvFb = new TextView(this);
        tvFb.setGravity(Gravity.CENTER);
        tvFb.setTextColor(0xFFFFFFFF);
        tvFb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tvFb.setTypeface(null, Typeface.BOLD);
        tvFb.setBackground(createOvalDrawable(0xFF5865F2, 0, 0));
        avBox.addView(tvFb, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        ImageView ivAv = new ImageView(this);
        ivAv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        avBox.addView(ivAv, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        loadAvatarIntoViews(p.avatarUrl, p.username, ivAv, tvFb);

        LinearLayout infoCol = new LinearLayout(this);
        infoCol.setOrientation(LinearLayout.VERTICAL);
        infoCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvName = new TextView(this);
        tvName.setText(p.username);
        tvName.setTextColor(0xFF0F172A);
        tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tvName.setTypeface(null, Typeface.BOLD);

        TextView tvSt = new TextView(this);
        tvSt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        if (p.tokenExpired) {
            tvSt.setText(I18n.t("● Chưa đăng nhập (Hết hạn Token)", "● Not logged in (Token expired)"));
            tvSt.setTextColor(0xFFEF4444);
        } else if (farming) {
            tvSt.setText(I18n.t("● Đang Auto Farm ngầm...", "● Farming in background..."));
            tvSt.setTextColor(0xFF10B981);
        } else {
            tvSt.setText(I18n.t("● Đã đăng nhập (Chưa bấm Farm)", "● Logged in (Idle)"));
            tvSt.setTextColor(0xFF64748B);
        }

        infoCol.addView(tvName);
        infoCol.addView(tvSt);

        TextView tvAction = new TextView(this);
        tvAction.setPadding(dp(8), dp(4), dp(8), dp(4));
        tvAction.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        tvAction.setTypeface(null, Typeface.BOLD);
        if (p.tokenExpired) {
            tvAction.setText(I18n.t("CẬP NHẬT", "UPDATE"));
            tvAction.setTextColor(0xFFDC2626);
            tvAction.setBackground(createRoundedRect(0xFFFEE2E2, dp(6), 0, 0));
        } else if (isCurrent && insideMenu) {
            tvAction.setText(I18n.t("ĐANG XEM", "ACTIVE"));
            tvAction.setTextColor(0xFF5865F2);
            tvAction.setBackground(createRoundedRect(0xFFE0E7FF, dp(6), 0, 0));
        } else {
            tvAction.setText(I18n.t("CHUYỂN QUA", "SWITCH"));
            tvAction.setTextColor(0xFF475569);
            tvAction.setBackground(createRoundedRect(0xFFF1F5F9, dp(6), 0, 0));
        }

        row.addView(avBox);
        row.addView(infoCol);
        row.addView(tvAction);

        row.setOnTouchListener(new View.OnTouchListener() {
            private final Handler longPressHandler = new Handler(Looper.getMainLooper());
            private boolean isLongPressedTriggered = false;
            private float downX, downY;
            private float touchRawX, touchRawY;

            private final Runnable longPressRunnable = new Runnable() {
                @Override
                public void run() {
                    isLongPressedTriggered = true;
                    showAccountLogoutPopup(session, touchRawX, touchRawY);
                }
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getActionMasked();
                if (action == MotionEvent.ACTION_DOWN) {
                    downX = event.getX();
                    downY = event.getY();
                    touchRawX = event.getRawX();
                    touchRawY = event.getRawY();
                    isLongPressedTriggered = false;
                    longPressHandler.postDelayed(longPressRunnable, 800L);
                    row.setPressed(true);
                    return true;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    float dx = Math.abs(event.getX() - downX);
                    float dy = Math.abs(event.getY() - downY);
                    if (dx > dp(10) || dy > dp(10)) {
                        longPressHandler.removeCallbacks(longPressRunnable);
                        row.setPressed(false);
                    } else {
                        touchRawX = event.getRawX();
                        touchRawY = event.getRawY();
                    }
                    return true;
                } else if (action == MotionEvent.ACTION_UP) {
                    longPressHandler.removeCallbacks(longPressRunnable);
                    row.setPressed(false);
                    if (!isLongPressedTriggered) {
                        if (insideMenu) {
                            closeProfileMenuAnimated();
                        }
                        if (p.tokenExpired) {
                            showLoginScreen(p);
                        } else {
                            switchToAccountSession(session);
                        }
                    }
                    return true;
                } else if (action == MotionEvent.ACTION_CANCEL) {
                    longPressHandler.removeCallbacks(longPressRunnable);
                    row.setPressed(false);
                    return true;
                }
                return false;
            }
        });
        return row;
    }

    private View createMenuOptionRow(int iconType, String title, String rightBadgeText, int rightColor, View.OnClickListener onClick) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(12), dp(12), dp(12), dp(12));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dp(4);
        row.setLayoutParams(lp);

        ImageView ivIcon = new ImageView(this);
        LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(dp(22), dp(22));
        iconLp.rightMargin = dp(12);
        ivIcon.setLayoutParams(iconLp);
        ivIcon.setImageDrawable(DquestsCore.createVectorIconDrawable(iconType, 0xFF0F172A));

        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setTextColor(0xFF0F172A);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvRight = new TextView(this);
        tvRight.setText(rightBadgeText);
        tvRight.setTextColor(rightColor);
        tvRight.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        tvRight.setTypeface(null, Typeface.BOLD);

        row.addView(ivIcon);
        row.addView(tvTitle);
        row.addView(tvRight);

        DquestsCore.attachSpreadGrayEffect(row, dp(10), onClick);
        return row;
    }

    private void showWorkerAndSpeedModal(boolean triggerStart) {
        this.isPromptingForStart = triggerStart;
        if (currentSession != null) {
            tempSelectedWorkers = currentSession.profile.maxWorkers;
            tempSelectedSpeed = currentSession.profile.speedMode;
        }
        btnConfirmStartFarm.setText(triggerStart
                ? I18n.t("Xác nhận & Chạy ngay", "Confirm & Start Now")
                : I18n.t("Lưu cấu hình", "Save Config"));
        populateSpeedSelectorRow();
        populateWorkerSelectorRows();
        applyBlurBackground(overlayWorkerSelector);
        overlayWorkerSelector.setVisibility(View.VISIBLE);
    }

    private void populateSpeedSelectorRow() {
        containerSpeedOptions.removeAllViews();
        String[] labels = new String[]{
                I18n.t("An toàn (5s)", "Safe (5s)"),
                I18n.t("Chuẩn (7s)", "Standard (7s)"),
                I18n.t("Nhanh (10s)", "Fast (10s)")
        };
        for (int i = 0; i < labels.length; i++) {
            final int mode = i;
            boolean sel = (tempSelectedSpeed == mode);
            TextView chip = new TextView(this);
            chip.setGravity(Gravity.CENTER);
            chip.setPadding(dp(8), dp(8), dp(8), dp(8));
            chip.setText(labels[i]);
            chip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            chip.setTypeface(null, Typeface.BOLD);
            chip.setTextColor(sel ? 0xFFFFFFFF : 0xFF334155);
            chip.setBackground(createRoundedRect(sel ? 0xFF5865F2 : 0xFFF1F5F9, dp(8), 1, sel ? 0xFF5865F2 : 0xFFE2E8F0));

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            if (i > 0) lp.leftMargin = dp(6);
            chip.setLayoutParams(lp);

            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tempSelectedSpeed = mode;
                    populateSpeedSelectorRow();
                }
            });
            containerSpeedOptions.addView(chip);
        }
    }

    private void populateWorkerSelectorRows() {
        containerWorkerOptions.removeAllViews();
        for (int i = 1; i <= 10; i++) {
            final int count = i;
            boolean isSelected = (count == tempSelectedWorkers);
            boolean isSafe = (count <= 4);

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(dp(12), dp(9), dp(12), dp(9));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = dp(6);
            row.setLayoutParams(lp);
            row.setBackground(createRoundedRect(
                    isSelected ? 0xFFEEF2FF : 0xFFF8FAFC, dp(10), isSelected ? 2 : 1, isSelected ? 0xFF5865F2 : 0xFFE2E8F0));

            LinearLayout col = new LinearLayout(this);
            col.setOrientation(LinearLayout.VERTICAL);
            col.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

            TextView tvT = new TextView(this);
            tvT.setText(count + I18n.t(" nhiệm vụ cùng lúc", " parallel quests"));
            tvT.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            tvT.setTypeface(null, Typeface.BOLD);
            tvT.setTextColor(isSelected ? 0xFF4338CA : 0xFF0F172A);

            TextView tvS = new TextView(this);
            tvS.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            if (isSafe) {
                tvS.setText(I18n.t("Khuyên dùng an toàn (1 - 4 nhiệm vụ)", "Recommended safe range (1 - 4 quests)"));
                tvS.setTextColor(0xFF10B981);
            } else {
                tvS.setText(I18n.t("Cảnh báo: Dễ bị Discord Rate Limit!", "Warning: High risk of Discord Rate Limit!"));
                tvS.setTextColor(0xFFEF4444);
            }
            col.addView(tvT);
            col.addView(tvS);

            TextView badge = new TextView(this);
            badge.setPadding(dp(8), dp(3), dp(8), dp(3));
            badge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            badge.setTypeface(null, Typeface.BOLD);
            badge.setText(isSafe ? "1-4 SAFE" : "RISK");
            badge.setTextColor(isSafe ? 0xFF059669 : 0xFFDC2626);
            badge.setBackground(createRoundedRect(isSafe ? 0xFFD1FAE5 : 0xFFFEE2E2, dp(6), 0, 0));

            row.addView(col);
            row.addView(badge);
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tempSelectedWorkers = count;
                    populateWorkerSelectorRows();
                }
            });
            containerWorkerOptions.addView(row);
        }
    }

    private void openHypeSquadModal() {
        tvHypesquadStatus.setVisibility(View.GONE);
        populateHypeSquadHouses();
        applyBlurBackground(overlayHypesquad);
        overlayHypesquad.setVisibility(View.VISIBLE);
    }

    private void populateHypeSquadHouses() {
        containerHypesquadHouses.removeAllViews();
        final int[] ids = new int[]{1, 2, 3};
        final String[] names = new String[]{
                "HypeSquad Bravery (House 1)",
                "HypeSquad Brilliance (House 2)",
                "HypeSquad Balance (House 3)"
        };
        final String[] urls = new String[]{
                DquestsCore.BADGE_BRAVERY_URL,
                DquestsCore.BADGE_BRILLIANCE_URL,
                DquestsCore.BADGE_BALANCE_URL
        };

        for (int i = 0; i < 3; i++) {
            final int houseId = ids[i];
            boolean sel = (selectedHypesquadHouse == houseId);

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(dp(12), dp(10), dp(12), dp(10));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = dp(8);
            row.setLayoutParams(lp);
            row.setBackground(createRoundedRect(
                    sel ? 0xFFEEF2FF : 0xFFF8FAFC, dp(10), sel ? 2 : 1, sel ? 0xFF5865F2 : 0xFFE2E8F0));

            ImageView ivBadge = new ImageView(this);
            LinearLayout.LayoutParams ivLp = new LinearLayout.LayoutParams(dp(36), dp(36));
            ivLp.rightMargin = dp(12);
            ivBadge.setLayoutParams(ivLp);
            ivBadge.setTag(urls[i]);
            loadImageAsync(urls[i], ivBadge, null, false);

            TextView tvName = new TextView(this);
            tvName.setText(names[i]);
            tvName.setTextColor(0xFF0F172A);
            tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            tvName.setTypeface(null, Typeface.BOLD);
            tvName.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

            row.addView(ivBadge);
            row.addView(tvName);
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedHypesquadHouse = houseId;
                    populateHypeSquadHouses();
                }
            });
            containerHypesquadHouses.addView(row);
        }
    }

    private void submitHypeSquadChoice(final int houseId) {
        if (currentSession == null) return;
        tvHypesquadStatus.setVisibility(View.VISIBLE);
        tvHypesquadStatus.setTextColor(0xFF5865F2);
        tvHypesquadStatus.setText(I18n.t("Đang gửi yêu cầu tới Discord API...", "Sending request to Discord API..."));

        new Thread(new Runnable() {
            @Override
            public void run() {
                final HttpResponse res = DquestsCore.requestHypeSquadBadge(currentSession.profile.token, houseId);
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (res.statusCode == 200 || res.statusCode == 204) {
                            tvHypesquadStatus.setTextColor(0xFF059669);
                            if (houseId > 0) {
                                tvHypesquadStatus.setText(I18n.t(
                                        "✓ Thành công! Hãy tắt hoàn toàn ứng dụng Discord và mở lại, sau đó bấm vào Hồ sơ (Profile) bạn sẽ thấy huy hiệu HypeSquad mới của mình!",
                                        "✓ Success! Please completely close and reopen your Discord app, then check your Profile to see your new HypeSquad badge!"));
                            } else {
                                tvHypesquadStatus.setText(I18n.t(
                                        "✓ Đã gỡ huy hiệu HypeSquad thành công! Hãy khởi động lại Discord để cập nhật.",
                                        "✓ HypeSquad badge removed! Restart Discord to see changes."));
                            }
                        } else {
                            tvHypesquadStatus.setTextColor(0xFFEF4444);
                            tvHypesquadStatus.setText("HTTP " + res.statusCode + ": " + res.body);
                        }
                    }
                });
            }
        }).start();
    }

    private void openRpcModal() {
        if (currentSession == null) return;
        AccountProfile p = currentSession.profile;
        tempPresenceStatus = p.presenceStatus;
        RpcConfig rpc = p.rpcConfig;
        tempRpcActivityType = rpc.activityType;
        tempRpcTimestampMode = rpc.timestampMode;

        etRpcAppId.setText(rpc.applicationId);
        etRpcName.setText(rpc.name);
        etRpcDetails.setText(rpc.details);
        etRpcState.setText(rpc.state);
        etRpcStreamUrl.setText(rpc.streamUrl);
        etRpcLargeImage.setText(rpc.largeImage);
        etRpcLargeText.setText(rpc.largeText);
        etRpcBtn1Label.setText(rpc.button1Label);
        etRpcBtn1Url.setText(rpc.button1Url);

        containerInstalledAppsPicker.setVisibility(View.GONE);
        populatePresenceTypeRows();
        populateRpcActivityTypesRow();
        populateRpcTimestampsRow();
        applyBlurBackground(overlayRpc);
        overlayRpc.setVisibility(View.VISIBLE);
    }

    private void populatePresenceTypeRows() {
        containerPresenceTypes.removeAllViews();
        final String[] codes = new String[]{"none", "online", "idle", "dnd"};
        final String[] titles = new String[]{
                I18n.t("Không bật (Mặc định)", "Disabled (Default)"),
                I18n.t("Trực tuyến trên máy tính (Online Desktop)", "Online on Desktop"),
                I18n.t("Trực tuyến treo máy (AFK / Idle)", "Idle / AFK"),
                I18n.t("Không làm phiền (Do Not Disturb - DND)", "Do Not Disturb (DND)")
        };

        for (int i = 0; i < codes.length; i++) {
            final String code = codes[i];
            boolean sel = code.equalsIgnoreCase(tempPresenceStatus);

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(dp(12), dp(9), dp(12), dp(9));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = dp(6);
            row.setLayoutParams(lp);
            row.setBackground(createRoundedRect(
                    sel ? 0xFFEEF2FF : 0xFFF8FAFC, dp(8), sel ? 2 : 1, sel ? 0xFF5865F2 : 0xFFE2E8F0));

            View iconView = new View(this);
            LinearLayout.LayoutParams icLp = new LinearLayout.LayoutParams(dp(18), dp(18));
            icLp.rightMargin = dp(10);
            iconView.setLayoutParams(icLp);
            iconView.setBackground(DquestsCore.createPresenceStatusDrawable(code));

            TextView tv = new TextView(this);
            tv.setText(titles[i]);
            tv.setTextColor(0xFF0F172A);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            tv.setTypeface(null, Typeface.BOLD);

            row.addView(iconView);
            row.addView(tv);
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tempPresenceStatus = code;
                    populatePresenceTypeRows();
                }
            });
            containerPresenceTypes.addView(row);
        }
    }

    private void populateRpcActivityTypesRow() {
        containerRpcActivityTypes.removeAllViews();
        final int[] types = new int[]{0, 1, 2, 3, 5};
        final String[] names = new String[]{
                I18n.t("Chơi", "Playing"),
                "Stream",
                I18n.t("Nghe", "Listening"),
                I18n.t("Xem", "Watching"),
                I18n.t("Thi đấu", "Competing")
        };

        for (int i = 0; i < types.length; i++) {
            final int t = types[i];
            boolean sel = (tempRpcActivityType == t);
            TextView chip = new TextView(this);
            chip.setGravity(Gravity.CENTER);
            chip.setPadding(dp(4), dp(7), dp(4), dp(7));
            chip.setText(names[i]);
            chip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            chip.setTypeface(null, Typeface.BOLD);
            chip.setTextColor(sel ? 0xFFFFFFFF : 0xFF334155);
            chip.setBackground(createRoundedRect(sel ? 0xFF5865F2 : 0xFFF1F5F9, dp(6), 0, 0));

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            if (i > 0) lp.leftMargin = dp(4);
            chip.setLayoutParams(lp);
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tempRpcActivityType = t;
                    populateRpcActivityTypesRow();
                }
            });
            containerRpcActivityTypes.addView(chip);
        }
    }

    private void populateRpcTimestampsRow() {
        containerRpcTimestamps.removeAllViews();
        final String[] labels = new String[]{
                I18n.t("Tắt", "Off"),
                I18n.t("Hiện tại", "Now"),
                I18n.t("1 giờ trước", "1h ago"),
                I18n.t("24 giờ trước", "24h ago")
        };
        for (int i = 0; i < labels.length; i++) {
            final int mode = i;
            boolean sel = (tempRpcTimestampMode == mode);
            TextView chip = new TextView(this);
            chip.setGravity(Gravity.CENTER);
            chip.setPadding(dp(4), dp(7), dp(4), dp(7));
            chip.setText(labels[i]);
            chip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            chip.setTypeface(null, Typeface.BOLD);
            chip.setTextColor(sel ? 0xFFFFFFFF : 0xFF334155);
            chip.setBackground(createRoundedRect(sel ? 0xFF5865F2 : 0xFFF1F5F9, dp(6), 0, 0));

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            if (i > 0) lp.leftMargin = dp(4);
            chip.setLayoutParams(lp);
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tempRpcTimestampMode = mode;
                    populateRpcTimestampsRow();
                }
            });
            containerRpcTimestamps.addView(chip);
        }
    }

    private void toggleInstalledAppsList() {
        if (containerInstalledAppsPicker.getVisibility() == View.VISIBLE) {
            containerInstalledAppsPicker.setVisibility(View.GONE);
            return;
        }
        containerInstalledAppsPicker.removeAllViews();
        containerInstalledAppsPicker.setVisibility(View.VISIBLE);

        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);
        int added = 0;
        for (int i = 0; i < apps.size() && added < 25; i++) {
            ApplicationInfo info = apps.get(i);
            if ((info.flags & ApplicationInfo.FLAG_SYSTEM) != 0 && (info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0) {
                continue;
            }
            final String label = String.valueOf(pm.getApplicationLabel(info));
            final String pkg = info.packageName;
            added++;

            TextView item = new TextView(this);
            item.setPadding(dp(10), dp(7), dp(10), dp(7));
            item.setText("• " + label + " (" + pkg + ")");
            item.setTextColor(0xFF334155);
            item.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etRpcName.setText(label);
                    etRpcDetails.setText("Playing on Android");
                    containerInstalledAppsPicker.setVisibility(View.GONE);
                }
            });
            containerInstalledAppsPicker.addView(item);
        }
    }

    private void importRpcFromDiscordAppId() {
        final String appId = etRpcAppId.getText().toString().trim();
        if (TextUtils.isEmpty(appId)) {
            showCustomTip(I18n.t("Hãy nhập Application ID vào ô bên dưới trước!", "Please enter an Application ID below first!"));
            etRpcAppId.requestFocus();
            return;
        }
        showCustomTip(I18n.t("Đang lấy thông tin Application từ Discord...", "Fetching Application info from Discord..."));
        new Thread(new Runnable() {
            @Override
            public void run() {
                final HttpResponse res = DquestsCore.rawHttpRequest(
                        "GET", DquestsCore.API_BASE + "/applications/" + appId + "/rpc", null, null, null, null);
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (res.statusCode == 200 && !TextUtils.isEmpty(res.body)) {
                            try {
                                JSONObject obj = new JSONObject(res.body);
                                String name = obj.optString("name", "");
                                String desc = obj.optString("description", "");
                                String icon = obj.optString("icon", "");
                                if (!TextUtils.isEmpty(name)) etRpcName.setText(name);
                                if (!TextUtils.isEmpty(desc) && TextUtils.isEmpty(etRpcDetails.getText().toString())) {
                                    etRpcDetails.setText(desc);
                                }
                                if (!TextUtils.isEmpty(icon)) {
                                    etRpcLargeImage.setText("https://cdn.discordapp.com/app-icons/" + appId + "/" + icon + ".png");
                                }
                                showCustomTip(I18n.t("Đã tự động điền thông tin từ App ID!", "Imported Application info!"));
                            } catch (Exception ignored) {
                            }
                        } else {
                            showCustomTip(I18n.t("Không tìm thấy Application ID này.", "Application ID not found."));
                        }
                    }
                });
            }
        }).start();
    }

    private void applyRpcSettings() {
        if (currentSession == null) return;
        AccountProfile p = currentSession.profile;
        p.presenceStatus = tempPresenceStatus;
        RpcConfig rpc = p.rpcConfig;
        rpc.activityType = tempRpcActivityType;
        rpc.timestampMode = tempRpcTimestampMode;
        rpc.applicationId = etRpcAppId.getText().toString().trim();
        rpc.name = etRpcName.getText().toString().trim();
        rpc.details = etRpcDetails.getText().toString().trim();
        rpc.state = etRpcState.getText().toString().trim();
        rpc.streamUrl = etRpcStreamUrl.getText().toString().trim();
        rpc.largeImage = etRpcLargeImage.getText().toString().trim();
        rpc.largeText = etRpcLargeText.getText().toString().trim();
        rpc.button1Label = etRpcBtn1Label.getText().toString().trim();
        rpc.button1Url = etRpcBtn1Url.getText().toString().trim();
        rpc.enabled = !TextUtils.isEmpty(rpc.name);

        if ("none".equalsIgnoreCase(p.presenceStatus) && rpc.enabled) {
            p.presenceStatus = "online";
        }
        saveAccountsToPrefs();
        currentSession.syncGatewayPresence();
        updateHeaderAndWorkspaceForCurrentAccount();
        overlayRpc.setVisibility(View.GONE);
        showCustomTip(I18n.t("Đã áp dụng Trực tuyến & Custom RPC!", "Applied Presence & Custom RPC!"));
    }


    private void applyLanguageStrings() {

        tvWelcomeSubtitle.setText(I18n.t("Discord Quests & RPC Engine • by minhsanggd", "Discord Quests & RPC Engine • by minhsanggd"));
        tvWelcomeFeatTitle.setText(I18n.t("TÍNH NĂNG CỐT LÕI", "CORE FEATURES"));
        tvWelcomeFeatDesc.setText(I18n.t(
                "• Tự động hoàn thành nhiệm vụ Discord Quests (Video, Play, Stream)\n• Quản lý và Farm song song nhiều tài khoản độc lập\n• Kích hoạt huy hiệu HypeSquad (Bravery, Brilliance, Balance)\n• Tùy chỉnh trạng thái Trực tuyến & Custom Rich Presence (RPC)",
                "• Auto-complete Discord Quests (Video, Play, Stream tasks)\n• Multi-Account Manager with parallel farming\n• Claim HypeSquad Badges (Bravery, Brilliance, Balance)\n• Custom Online Presence & Rich Presence (RPC)"));

        tvWelcomePrivTitle.setText(I18n.t("BẢO MẬT TUYỆT ĐỐI • 100% CỤC BỘ (LOCAL)", "ABSOLUTE PRIVACY • 100% LOCAL"));
        tvWelcomePrivDesc.setText(I18n.t(
                "Ứng dụng hoạt động hoàn toàn cục bộ trên điện thoại cá nhân của bạn. Mọi kết nối đều gửi trực tiếp tới máy chủ Discord mà KHÔNG đi qua bất kỳ server trung gian nào. Token và mật khẩu của bạn không bao giờ bị chia sẻ cho bất kỳ bên thứ ba nào.",
                "This application runs entirely locally on your personal device. All requests connect directly to Discord servers without passing through any intermediate servers. Your Token and credentials are never shared with any third party."));

        tvWelcomeRiskTitle.setText(I18n.t("CẢNH BÁO RỦI RO TÀI KHOẢN", "ACCOUNT RISK DISCLAIMER"));
        tvWelcomeRiskDesc.setText(I18n.t(
                "Việc tự động hóa các tác vụ Quests có thể bị hệ thống bảo mật của Discord phát hiện và áp dụng các biện pháp hạn chế (như Rate Limit hoặc khóa tạm thời). Bạn tự chịu hoàn toàn trách nhiệm và rủi ro đối với tài khoản của mình khi sử dụng công cụ này.",
                "Automating Quests tasks may be detected by Discord safety systems, resulting in limitations (such as Rate Limits or temporary account locks). You assume full responsibility and risk for your account when using this tool."));

        btnWelcomeTerms.setText(I18n.t("Điều khoản & Chính sách bảo mật (Terms & Privacy) ↗", "Terms & Privacy Policy ↗"));
        btnWelcomeConfirm.setText(I18n.t("Tôi đã hiểu & Bắt đầu sử dụng", "I Understand & Get Started"));

        tvBannerTitle.setText(I18n.t("Mẹo duy trì Auto Farm chạy nền ổn định", "Tip to keep Auto Farm running stably"));
        tvBannerDesc.setText(I18n.t(
                "Hãy chạm vào ảnh đại diện (Avatar) ở góc trên bên phải để bật Giữ sáng màn hình, Tắt tối ưu hoá pin hoặc mở Cửa sổ nổi.",
                "Tap your Avatar at the top-right corner to toggle Keep Screen On, Disable Battery Optimization, or use Floating Window."));

        tvPickerTitle.setText(I18n.t("Chọn tài khoản Discord", "Select Discord Account"));
        tvPickerSubtitle.setText(I18n.t(
                "Chọn một tài khoản đã lưu để vào nhanh, bấm Farm tất cả cùng lúc hoặc thêm tài khoản mới.",
                "Select a saved account, farm all accounts simultaneously, or add a new account."));
        btnStartupFarmAll.setText(I18n.t("Farm đồng loạt tất cả tài khoản", "Farm All Accounts Simultaneously"));
        btnStartupAddAccount.setText(I18n.t("+ Thêm tài khoản Discord mới", "+ Add New Discord Account"));

        tvLoginCardTitle.setText(I18n.t("Đăng nhập TK Discord", "Discord Account Login"));
        tvLoginCardSubtitle.setText(I18n.t(
                "Đăng nhập bằng Email & Mật khẩu để tự động lấy Token hoặc chuyển sang nhập Token trực tiếp.",
                "Sign in with Email & Password to fetch Token automatically, or switch to direct Token input."));
        btnBackToAccounts.setText(I18n.t("Danh sách Acc", "Accounts"));
        tabLoginCredentials.setText(I18n.t("Email & Mật khẩu", "Email & Password"));
        tabLoginToken.setText(I18n.t("Bằng Token", "By Token"));
        tvLabelEmail.setText(I18n.t("EMAIL HOẶC SỐ ĐIỆN THOẠI", "EMAIL OR PHONE NUMBER"));
        etEmail.setHint(I18n.t("Nhập email tài khoản Discord...", "Enter Discord email..."));
        tvLabelPassword.setText(I18n.t("MẬT KHẨU", "PASSWORD"));
        etPassword.setHint(I18n.t("Nhập mật khẩu...", "Enter password..."));
        tvLabelMfa.setText(I18n.t("MÃ XÁC THỰC 2 LỚP (2FA 6 SỐ)", "2FA AUTHENTICATOR CODE (6 DIGITS)"));
        etToken.setHint(I18n.t("Dán token vào đây (MTA... / MTI...)", "Paste token here (MTA... / MTI...)"));
        tvBtnConnectLabel.setText(I18n.t("Đăng nhập & Quét Quests", "Sign In & Scan Quests"));

        tvInfoTitle.setText(I18n.t("Tính năng nổi bật trên Dquests", "Key Features in Dquests"));
        tvInfoDesc.setText(I18n.t(
                "• Hỗ trợ Farm đa tài khoản song song không ngắt quãng\n• Điều chỉnh tốc độ Farm & số nhiệm vụ cùng lúc (1-4 an toàn)\n• Nhận huy hiệu HypeSquad (Bravery, Brilliance, Balance)\n• Tuỳ chỉnh trạng thái Trực tuyến & Custom Rich Presence (RPC)",
                "• Seamless parallel Multi-Account Auto Farming\n• Adjustable Farm Speed & parallel workers (1-4 safe)\n• Claim HypeSquad Badges (Bravery, Brilliance, Balance)\n• Custom Online Presence & Rich Presence (RPC)"));

        tvWorkerLabelTop.setText(I18n.t("CẤU HÌNH LUỒNG & TỐC ĐỘ", "WORKERS & FARM SPEED"));
        tabLogs.setText(I18n.t("Nhật ký hoạt động", "Activity Logs"));
        tvEmptyTitle.setText(I18n.t("Chưa có dữ liệu Quests", "No Quests Found"));
        tvEmptyDesc.setText(I18n.t(
                "Nhấn biểu tượng Làm mới hoặc Bắt đầu Farm để quét các nhiệm vụ hiện có trên tài khoản Discord của bạn.",
                "Tap the Refresh icon or Start Farm to scan active quests on your Discord account."));
        tvLogHeaderTitle.setText(I18n.t("NHẬT KÝ HỆ THỐNG (REAL-TIME)", "SYSTEM LOGS (REAL-TIME)"));
        btnClearLogs.setText(I18n.t("Xoá nhật ký", "Clear Logs"));

        tvWorkerModalTitle.setText(I18n.t("Cấu hình Số luồng & Tốc độ Farm", "Configure Workers & Farm Speed"));
        tvSpeedSectionLabel.setText(I18n.t("CHỌN TỐC ĐỘ GỬI TIẾN ĐỘ (FARM SPEED)", "SELECT FARMING SPEED"));
        tvWorkerWarnTitle.setText(I18n.t("KHUYẾN CÁO AN TOÀN (1 - 4 NHIỆM VỤ)", "SAFETY RECOMMENDATION (1 - 4 QUESTS)"));
        tvWorkerWarnDesc.setText(I18n.t(
                "Chỉ nên chọn từ 1 - 4 nhiệm vụ cùng lúc. Chạy quá nhiều luồng có thể gây Rate Limit (HTTP 429).",
                "Recommended: 1 - 4 quests at once. Too many parallel tasks may trigger Discord Rate Limits (HTTP 429)."));
        btnCloseWorkerSheet.setText(I18n.t("Đóng", "Close"));

        tvMenuAccountsHeader.setText(I18n.t("QUẢN LÝ ĐA TÀI KHOẢN", "MULTI-ACCOUNT MANAGER"));
        btnMenuAddAccount.setText(I18n.t("+ Thêm Acc", "+ Add Acc"));
        btnMenuFarmAllAccounts.setText(I18n.t("Farm tất cả tài khoản cùng lúc", "Farm All Accounts Simultaneously"));

        tvHypesquadTitle.setText(I18n.t("Chọn Huy hiệu HypeSquad", "Select HypeSquad Badge"));
        tvHypesquadSubtitle.setText(I18n.t(
                "Chọn 1 trong 3 nhà HypeSquad bên dưới hoặc gỡ huy hiệu hiện tại qua API trực tiếp.",
                "Pick one of the 3 HypeSquad houses below or remove your current badge via direct API."));
        btnHypesquadRemove.setText(I18n.t("Xoá Badge", "Remove Badge"));
        btnHypesquadClose.setText(I18n.t("Đóng", "Close"));
        btnHypesquadConfirm.setText(I18n.t("Xác nhận", "Confirm"));

        tvRpcTitle.setText(I18n.t("Trạng thái Trực tuyến & Custom RPC", "Online Presence & Custom RPC"));
        tvRpcStatusLabel.setText(I18n.t("LOẠI TRỰC TUYẾN (PRESENCE STATUS)", "ONLINE PRESENCE STATUS"));
        tvRpcCustomHeader.setText(I18n.t("CẤU HÌNH HOẠT ĐỘNG CHI TIẾT (RICH PRESENCE)", "CUSTOM RICH PRESENCE CONFIGURATION"));
        btnRpcPickInstalledApp.setText(I18n.t("Chọn App trên máy", "Pick Installed App"));
        btnRpcImportAppid.setText(I18n.t("Import từ App ID", "Import by App ID"));
        tvRpcActTypeLabel.setText(I18n.t("KIỂU HOẠT ĐỘNG", "ACTIVITY TYPE"));
        tvRpcTimeLabel.setText(I18n.t("THỜI GIAN ĐANG CHƠI (ELAPSED TIMESTAMP)", "ELAPSED TIMESTAMP"));
        btnRpcStop.setText(I18n.t("Tắt RPC", "Stop RPC"));
        btnRpcClose.setText(I18n.t("Đóng", "Close"));
        btnRpcApply.setText(I18n.t("Áp dụng ngay", "Apply Now"));

        updateWorkerLabel();
        updateHeaderAndWorkspaceForCurrentAccount();
    }

    private void updateHeaderAndWorkspaceForCurrentAccount() {
        if (!isNetworkConnected) {
            tvUserStatus.setText(I18n.t("● Không thể kết nối mạng", "● No internet connection"));
            tvUserStatus.setTextColor(0xFFEF4444); 
        } else if (currentSession != null) {
            AccountProfile p = currentSession.profile;
            tvUserStatus.setText(p.username);
            tvUserStatus.setTextColor(0xFF64748B);
            loadAvatarIntoViews(p.avatarUrl, p.username, ivHeaderAvatar, tvHeaderAvatarFallback);
            viewHeaderPresenceDot.setBackground(DquestsCore.createPresenceStatusDrawable(p.presenceStatus));

            boolean farming = currentSession.isFarming();
            if (farming) {
                btnStartStopFarm.setText(I18n.t("Dừng Auto Farm", "Stop Farm"));
                btnStartStopFarm.setBackground(createRoundedRect(0xFFEF4444, dp(10), 0, 0));
            } else {
                btnStartStopFarm.setText(I18n.t("Bắt đầu Farm", "Start Farm"));
                btnStartStopFarm.setBackground(createRoundedRect(0xFF5865F2, dp(10), 0, 0));
            }
            updateQuestListUi();
        } else {
            tvUserStatus.setText(I18n.t("Chưa đăng nhập", "Not logged in"));
            ivHeaderAvatar.setVisibility(View.GONE);
            tvHeaderAvatarFallback.setVisibility(View.VISIBLE);
            tvHeaderAvatarFallback.setText("D");
            viewHeaderPresenceDot.setBackground(DquestsCore.createPresenceStatusDrawable("none"));
        }
    }

    private void updateWorkerLabel() {
        int w = (currentSession != null) ? currentSession.profile.maxWorkers : 2;
        int sp = (currentSession != null) ? currentSession.profile.speedMode : 1;
        String spStr = (sp == 0) ? I18n.t("An toàn", "Safe") : (sp == 2 ? I18n.t("Nhanh", "Fast") : I18n.t("Chuẩn", "Standard"));
        tvWorkerSelected.setText(w + " NV • " + spStr);
        tvWorkerSelected.setTextColor(w <= 4 ? 0xFF0F172A : 0xFFDC2626);
    }

    private void updateWorkspaceTabs(boolean showQuests) {
        this.isShowingQuestsTab = showQuests;
        if (showQuests) {
            tabQuests.setBackground(createRoundedRect(0xFFFFFFFF, dp(8), 1, 0xFFE2E8F0));
            tabQuests.setTextColor(0xFF0F172A);
            tabLogs.setBackgroundColor(Color.TRANSPARENT);
            tabLogs.setTextColor(0xFF64748B);
            pageQuests.setVisibility(View.VISIBLE);
            pageLogs.setVisibility(View.GONE);
        } else {
            tabLogs.setBackground(createRoundedRect(0xFFFFFFFF, dp(8), 1, 0xFFE2E8F0));
            tabLogs.setTextColor(0xFF0F172A);
            tabQuests.setBackgroundColor(Color.TRANSPARENT);
            tabQuests.setTextColor(0xFF64748B);
            pageQuests.setVisibility(View.GONE);
            pageLogs.setVisibility(View.VISIBLE);
            svLogs.post(new Runnable() {
                @Override
                public void run() {
                    svLogs.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }
    }

    private void updateQuestListUi() {
        List<QuestItem> items = (currentSession != null) ? currentSession.questItems : new ArrayList<QuestItem>();
        boolean isLoading = (currentSession != null && currentSession.isLoadingQuests);
        int unfinished = 0;
        synchronized (items) {
            for (int i = 0; i < items.size(); i++) {
                if (!items.get(i).completed) unfinished++;
            }
        }
        tabQuests.setText(I18n.t("Danh sách Quests (", "Quests List (") + unfinished + ")");

        if (isLoading && items.isEmpty()) {
            setSkeletonLoadingVisible(true);
            emptyQuestsView.setVisibility(View.GONE);
            lvQuests.setVisibility(View.GONE);
        } else {
            setSkeletonLoadingVisible(false);
            if (items.isEmpty()) {
                emptyQuestsView.setVisibility(View.VISIBLE);
                lvQuests.setVisibility(View.GONE);
            } else {
                emptyQuestsView.setVisibility(View.GONE);
                lvQuests.setVisibility(View.VISIBLE);
            }
        }
        questAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLog(final String msg, final String level) {
        final String ts = new SimpleDateFormat("HH:mm:ss", Locale.US).format(new Date());
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                String badgeColor = "#5865F2";
                String badgeLabel = "INFO";
                if ("ok".equalsIgnoreCase(level)) {
                    badgeColor = "#10B981";
                    badgeLabel = " OK ";
                } else if ("warn".equalsIgnoreCase(level)) {
                    badgeColor = "#D97706";
                    badgeLabel = "WARN";
                } else if ("error".equalsIgnoreCase(level)) {
                    badgeColor = "#EF4444";
                    badgeLabel = "ERR ";
                } else if ("prog".equalsIgnoreCase(level)) {
                    badgeColor = "#6366F1";
                    badgeLabel = "PROG";
                }

                String line = "<font color='#94A3B8'>[" + ts + "]</font> "
                        + "<b><font color='" + badgeColor + "'>" + badgeLabel + "</font></b> "
                        + "<font color='#1E293B'>" + TextUtils.htmlEncode(msg) + "</font><br/>";

                if (logHtmlBuffer.length() > 28000) {
                    logHtmlBuffer.delete(0, 8000);
                }
                logHtmlBuffer.append(line);
                if (Build.VERSION.SDK_INT >= 24) {
                    tvLogsContent.setText(Html.fromHtml(logHtmlBuffer.toString(), Html.FROM_HTML_MODE_LEGACY));
                } else {
                    tvLogsContent.setText(Html.fromHtml(logHtmlBuffer.toString()));
                }
                if (!isShowingQuestsTab) {
                    svLogs.post(new Runnable() {
                        @Override
                        public void run() {
                            svLogs.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onQuestsUpdated(final String accountId) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (currentSession != null && accountId.equals(currentSession.profile.userId)) {
                    updateQuestListUi();
                }
            }
        });
    }

    @Override
    public void onAccountStateChanged(final String accountId) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                saveAccountsToPrefs();
                updateHeaderAndWorkspaceForCurrentAccount();
                if (overlayProfileMenu.getVisibility() == View.VISIBLE) {
                    populateProfileMenuContent();
                }
                if (layoutAccountPicker.getVisibility() == View.VISIBLE) {
                    showStartupAccountPicker();
                }
            }
        });
    }

    @Override
    public void onTokenExpired(final AccountProfile account) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                saveAccountsToPrefs();
                showCustomTip(I18n.t("Token của @" + account.username + " đã hết hạn! Hãy cập nhật lại.",
                                "Token for @" + account.username + " expired! Please update credentials."));
                if (currentSession != null && account.userId.equals(currentSession.profile.userId)) {
                    showLoginScreen(account);
                }
            }
        });
    }

    @Override
    public void onAllQuestsCompletedCheck() {
        if (!autoExitOn100) return;
        for (AccountSession s : accountSessions.values()) {
            if (s.isFarming() && !s.areAllValidQuestsCompleted()) {
                return;
            }
        }
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                showCustomTip(I18n.t("Đã hoàn thành 100% nhiệm vụ! Đang tự động đóng ứng dụng...",
                                "100% Quests completed! Auto-closing application..."));
                uiHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        System.exit(0);
                    }
                }, 2000L);
            }
        });
    }
    private void checkAppUpdateSilentlyOnLaunch() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2500L);
                    final String remoteVer = DquestsCore.checkRemoteVersion();
                    if (remoteVer != null && DquestsCore.isNewerVersion(DquestsCore.APP_VERSION, remoteVer)) {
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                showCustomTip(I18n.t(
                                        "Đã có phiên bản mới (v" + remoteVer + ")! Hãy mở Menu để cập nhật.",
                                        "New version (v" + remoteVer + ") available! Check Menu to update."));
                            }
                        });
                    }
                } catch (Exception ignored) {
                }
            }
        }).start();
    }

    private void checkAppUpdateFromGithub() {
        showCustomTip(I18n.t("Đang kiểm tra bản cập nhật mới...", "Checking for updates..."));

        new Thread(new Runnable() {
            @Override
            public void run() {
                final String remoteVer = DquestsCore.checkRemoteVersion();
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (remoteVer == null) {
                            showCustomTip(I18n.t("Không thể kết nối máy chủ kiểm tra cập nhật.", "Failed to check for updates."));
                        } else if (DquestsCore.isNewerVersion(DquestsCore.APP_VERSION, remoteVer)) {
                            showCustomTip(I18n.t("Có bản cập nhật mới v" + remoteVer + "! Đang mở trang tải xuống...",
                                            "New update v" + remoteVer + " available! Opening releases page..."));
                            openUrlInBrowser(DquestsCore.RELEASES_URL);
                        } else {
                            showCustomTip(I18n.t("Bạn đang dùng phiên bản mới nhất (v" + DquestsCore.APP_VERSION + ")!",
                                            "You are on the latest version (v" + DquestsCore.APP_VERSION + ")!"));
                        }
                    }
                });
            }
        }).start();
    }

    private void shareApplicationIntent() {
        String shareText = I18n.t(
                "Trải nghiệm ngay ứng dụng Dquests (by minhsanggd) - Tự động Farm Discord Quests đa tài khoản, nhận huy hiệu HypeSquad & tuỳ chỉnh Custom RPC ngay trên điện thoại!\nTải xuống tại: " + DquestsCore.RELEASES_URL,
                "Check out Dquests (by minhsanggd) - Auto Farm Discord Quests across multiple accounts, claim HypeSquad badges & customize Rich Presence on Android!\nDownload here: " + DquestsCore.RELEASES_URL);
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(sendIntent, I18n.t("Chia sẻ Dquests qua", "Share Dquests via")));
    }

    private void logoutCurrentAccount() {
        if (currentSession == null) return;
        currentSession.shutdownAll();
        accountSessions.remove(currentSession.profile.userId);
        currentSession = accountSessions.isEmpty() ? null : accountSessions.values().iterator().next();
        saveAccountsToPrefs();

        if (currentSession != null) {
            switchToAccountSession(currentSession);
        } else {
            showLoginScreen(null);
        }
    }

    private class QuestListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return (currentSession != null) ? currentSession.questItems.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return currentSession.questItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder h;
            if (convertView == null) {
                h = createQuestCardView();
                convertView = h.cardRoot;
                convertView.setTag(h);
            } else {
                h = (ViewHolder) convertView.getTag();
            }

            final QuestItem item = currentSession.questItems.get(position);
            final boolean isRunning = currentSession.activeRunningSet.contains(item.id);
            final boolean isSkipped = currentSession.skippedSet.contains(item.id);
            final boolean isDone = item.completed || currentSession.completedSet.contains(item.id);

            if (isRunning) {
                h.cardRoot.setBackground(createRoundedRect(0xFFFFFFFF, dp(14), 2, 0xFF5865F2));
            } else if (isDone) {
                h.cardRoot.setBackground(createRoundedRect(0xFFF8FAFC, dp(14), 1, 0xFFBBF7D0));
            } else if (isSkipped) {
                h.cardRoot.setBackground(createRoundedRect(0xFFF8FAFC, dp(14), 1, 0xFFFDE68A));
            } else {
                h.cardRoot.setBackground(createRoundedRect(0xFFFFFFFF, dp(14), 1, 0xFFE2E8F0));
            }

            h.tvTitle.setText(item.title);
            h.tvSubtitle.setText(item.subtitle);
            h.tvReward.setText(I18n.t("Phần thưởng: ", "Reward: ") + item.reward);
            h.tvTaskBadge.setText(item.taskType != null ? item.taskType : "QUEST");
            h.tvTaskBadge.setBackground(createRoundedRect(0xFFF1F5F9, dp(6), 0, 0));

            if (isDone) {
                h.tvStatusBadge.setText(I18n.t("HOÀN THÀNH", "COMPLETED"));
                h.tvStatusBadge.setTextColor(0xFF059669);
                h.tvStatusBadge.setBackground(createRoundedRect(0xFFD1FAE5, dp(6), 0, 0));
            } else if (isRunning) {
                h.tvStatusBadge.setText(I18n.t("● ĐANG FARM", "● FARMING"));
                h.tvStatusBadge.setTextColor(0xFFFFFFFF);
                h.tvStatusBadge.setBackground(createRoundedRect(0xFF5865F2, dp(6), 0, 0));
            } else if (isSkipped) {
                h.tvStatusBadge.setText(I18n.t("ĐÃ TẠM BỎ QUA", "SKIPPED"));
                h.tvStatusBadge.setTextColor(0xFFB45309);
                h.tvStatusBadge.setBackground(createRoundedRect(0xFFFEF3C7, dp(6), 0, 0));
            } else {
                h.tvStatusBadge.setText(I18n.t("CHỜ THỰC THI", "QUEUED"));
                h.tvStatusBadge.setTextColor(0xFF475569);
                h.tvStatusBadge.setBackground(createRoundedRect(0xFFE2E8F0, dp(6), 0, 0));
            }

            int target = Math.max(1, item.targetSeconds);
            int done = (int) Math.min(target, Math.round(item.doneSeconds));
            int pct = (int) Math.min(100, Math.round((done * 100.0) / target));
            h.progressBar.setMax(100);
            h.progressBar.setProgress(pct);
            h.tvProgressLeft.setText(I18n.t("Tiến độ: ", "Progress: ") + formatDuration(done) + " / " + formatDuration(item.targetSeconds));
            h.tvProgressRight.setText(pct + "%");

            String init = !TextUtils.isEmpty(item.title) ? item.title.substring(0, 1).toUpperCase(Locale.US) : "Q";
            h.tvAvatarFallback.setText(init);
            loadImageAsync(item.imageUrl, h.ivThumb, h.tvAvatarFallback, false);

            if (isDone) {
                h.actionRow.setVisibility(View.GONE);
            } else {
                h.actionRow.setVisibility(View.VISIBLE);
                h.btnSkip.setText(isSkipped ? I18n.t("Tiếp tục làm", "Resume") : I18n.t("Tạm bỏ qua (Skip)", "Skip Quest"));
                h.btnSkip.setTextColor(isSkipped ? 0xFF059669 : 0xFFB45309);
                h.btnSkip.setBackground(createRoundedRect(isSkipped ? 0xFFECFDF5 : 0xFFFFFBEB, dp(8), 1, isSkipped ? 0xFFA7F3D0 : 0xFFFDE68A));

                h.btnCoFarm.setText(isRunning ? I18n.t("Đang thực thi...", "Running...") : I18n.t("+ Làm cùng lúc ngay", "+ Co-Farm Now"));
                h.btnCoFarm.setTextColor(isRunning ? 0xFF64748B : 0xFF5865F2);
                h.btnCoFarm.setBackground(createRoundedRect(isRunning ? 0xFFF1F5F9 : 0xFFEEF2FF, dp(8), 1, isRunning ? 0xFFE2E8F0 : 0xFFC7D2FE));
                h.btnCoFarm.setEnabled(!isRunning);

                h.btnSkip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentSession.toggleSkipQuest(item.id, item.title);
                    }
                });
                h.btnCoFarm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentSession.triggerCoFarmNow(item.id, item.title);
                    }
                });
            }
            return convertView;
        }

        private ViewHolder createQuestCardView() {
            ViewHolder h = new ViewHolder();
            LinearLayout root = new LinearLayout(MainActivity.this);
            root.setOrientation(LinearLayout.VERTICAL);
            root.setPadding(dp(14), dp(14), dp(14), dp(14));
            h.cardRoot = root;

            LinearLayout topRow = new LinearLayout(MainActivity.this);
            topRow.setOrientation(LinearLayout.HORIZONTAL);
            topRow.setGravity(Gravity.CENTER_VERTICAL);

            FrameLayout thumbBox = new FrameLayout(MainActivity.this);
            LinearLayout.LayoutParams thumbLp = new LinearLayout.LayoutParams(dp(52), dp(52));
            thumbLp.rightMargin = dp(12);
            thumbBox.setLayoutParams(thumbLp);
            thumbBox.setBackground(createRoundedRect(0xFFEEF2FF, dp(12), 1, 0xFFE2E8F0));

            TextView tvFb = new TextView(MainActivity.this);
            tvFb.setGravity(Gravity.CENTER);
            tvFb.setTextColor(0xFF5865F2);
            tvFb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
            tvFb.setTypeface(null, Typeface.BOLD);
            thumbBox.addView(tvFb, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            ImageView ivThumb = new ImageView(MainActivity.this);
            ivThumb.setScaleType(ImageView.ScaleType.CENTER_CROP);
            thumbBox.addView(ivThumb, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            h.tvAvatarFallback = tvFb;
            h.ivThumb = ivThumb;

            LinearLayout titleCol = new LinearLayout(MainActivity.this);
            titleCol.setOrientation(LinearLayout.VERTICAL);
            titleCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

            LinearLayout badgesLine = new LinearLayout(MainActivity.this);
            badgesLine.setOrientation(LinearLayout.HORIZONTAL);
            TextView tvSt = new TextView(MainActivity.this);
            tvSt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            tvSt.setTypeface(null, Typeface.BOLD);
            tvSt.setPadding(dp(7), dp(2), dp(7), dp(2));

            TextView tvTk = new TextView(MainActivity.this);
            tvTk.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            tvTk.setTextColor(0xFF475569);
            tvTk.setTypeface(null, Typeface.BOLD);
            tvTk.setPadding(dp(7), dp(2), dp(7), dp(2));
            LinearLayout.LayoutParams tkLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tkLp.leftMargin = dp(6);
            tvTk.setLayoutParams(tkLp);
            badgesLine.addView(tvSt);
            badgesLine.addView(tvTk);
            h.tvStatusBadge = tvSt;
            h.tvTaskBadge = tvTk;

            TextView tvTitle = new TextView(MainActivity.this);
            tvTitle.setTextColor(0xFF0F172A);
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            tvTitle.setTypeface(null, Typeface.BOLD);
            tvTitle.setPadding(0, dp(4), 0, 0);

            TextView tvSub = new TextView(MainActivity.this);
            tvSub.setTextColor(0xFF64748B);
            tvSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

            titleCol.addView(badgesLine);
            titleCol.addView(tvTitle);
            titleCol.addView(tvSub);
            h.tvTitle = tvTitle;
            h.tvSubtitle = tvSub;

            topRow.addView(thumbBox);
            topRow.addView(titleCol);
            root.addView(topRow);

            TextView tvReward = new TextView(MainActivity.this);
            tvReward.setTextColor(0xFF334155);
            tvReward.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            tvReward.setTypeface(null, Typeface.BOLD);
            tvReward.setPadding(dp(10), dp(7), dp(10), dp(7));
            tvReward.setBackground(createRoundedRect(0xFFF8FAFC, dp(8), 1, 0xFFE2E8F0));
            LinearLayout.LayoutParams rwLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rwLp.topMargin = dp(10);
            tvReward.setLayoutParams(rwLp);
            root.addView(tvReward);
            h.tvReward = tvReward;

            RelativeLayout progHeader = new RelativeLayout(MainActivity.this);
            LinearLayout.LayoutParams phLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            phLp.topMargin = dp(10);
            progHeader.setLayoutParams(phLp);

            TextView tvPL = new TextView(MainActivity.this);
            tvPL.setTextColor(0xFF475569);
            tvPL.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            tvPL.setTypeface(null, Typeface.BOLD);
            progHeader.addView(tvPL);

            TextView tvPR = new TextView(MainActivity.this);
            tvPR.setTextColor(0xFF5865F2);
            tvPR.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            tvPR.setTypeface(null, Typeface.BOLD);
            RelativeLayout.LayoutParams rLp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            tvPR.setLayoutParams(rLp);
            progHeader.addView(tvPR);
            root.addView(progHeader);
            h.tvProgressLeft = tvPL;
            h.tvProgressRight = tvPR;

            ProgressBar pb = new ProgressBar(MainActivity.this, null, android.R.attr.progressBarStyleHorizontal);
            LinearLayout.LayoutParams pbLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(8));
            pbLp.topMargin = dp(5);
            pb.setLayoutParams(pbLp);
            pb.setProgressDrawable(createCustomProgressDrawable());
            root.addView(pb);
            h.progressBar = pb;

            LinearLayout actRow = new LinearLayout(MainActivity.this);
            actRow.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams actLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            actLp.topMargin = dp(12);
            actRow.setLayoutParams(actLp);

            TextView btnSk = new TextView(MainActivity.this);
            btnSk.setGravity(Gravity.CENTER);
            btnSk.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            btnSk.setTypeface(null, Typeface.BOLD);
            LinearLayout.LayoutParams skLp = new LinearLayout.LayoutParams(0, dp(36), 1f);
            skLp.rightMargin = dp(6);
            btnSk.setLayoutParams(skLp);

            TextView btnCo = new TextView(MainActivity.this);
            btnCo.setGravity(Gravity.CENTER);
            btnCo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            btnCo.setTypeface(null, Typeface.BOLD);
            LinearLayout.LayoutParams coLp = new LinearLayout.LayoutParams(0, dp(36), 1f);
            coLp.leftMargin = dp(6);
            btnCo.setLayoutParams(coLp);

            actRow.addView(btnSk);
            actRow.addView(btnCo);
            root.addView(actRow);
            h.actionRow = actRow;
            h.btnSkip = btnSk;
            h.btnCoFarm = btnCo;
            return h;
        }
    }

    private static class ViewHolder {
        LinearLayout cardRoot;
        ImageView ivThumb;
        TextView tvAvatarFallback;
        TextView tvStatusBadge;
        TextView tvTaskBadge;
        TextView tvTitle;
        TextView tvSubtitle;
        TextView tvReward;
        TextView tvProgressLeft;
        TextView tvProgressRight;
        ProgressBar progressBar;
        LinearLayout actionRow;
        TextView btnSkip;
        TextView btnCoFarm;
    }

    private void loadAvatarIntoViews(String url, String username, ImageView iv, TextView fallbackTv) {
        String init = !TextUtils.isEmpty(username) ? username.substring(0, 1).toUpperCase(Locale.US) : "U";
        fallbackTv.setText(init);
        loadImageAsync(url, iv, fallbackTv, true);
    }

    private void loadImageAsync(final String urlStr, final ImageView targetIv, final TextView fallbackTv, final boolean circleCrop) {
        targetIv.setTag(urlStr);
        if (TextUtils.isEmpty(urlStr)) {
            targetIv.setVisibility(View.GONE);
            if (fallbackTv != null) fallbackTv.setVisibility(View.VISIBLE);
            return;
        }
        final String cacheKey = circleCrop ? ("circle_" + urlStr) : urlStr;
        Bitmap cached = imageCache.get(cacheKey);
        if (cached != null) {
            targetIv.setImageBitmap(cached);
            targetIv.setVisibility(View.VISIBLE);
            if (fallbackTv != null) fallbackTv.setVisibility(View.GONE);
            return;
        }
        targetIv.setVisibility(View.GONE);
        if (fallbackTv != null) fallbackTv.setVisibility(View.VISIBLE);

        imageExecutor.submit(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                try {
                    URL url = new URL(urlStr);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                    if (conn.getResponseCode() == 200) {
                        InputStream is = conn.getInputStream();
                        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                        byte[] data = new byte[4096];
                        int n;
                        while ((n = is.read(data, 0, data.length)) != -1) {
                            buffer.write(data, 0, n);
                        }
                        byte[] bytes = buffer.toByteArray();
                        Bitmap rawBmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        if (rawBmp != null) {
                            final Bitmap finalBmp = circleCrop ? DquestsCore.cropCircleBitmap(rawBmp) : rawBmp;
                            imageCache.put(cacheKey, finalBmp);
                            uiHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Object tag = targetIv.getTag();
                                    if (tag != null && urlStr.equals(tag.toString())) {
                                        targetIv.setImageBitmap(finalBmp);
                                        targetIv.setVisibility(View.VISIBLE);
                                        if (fallbackTv != null) fallbackTv.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    }
                } catch (Exception ignored) {
                } finally {
                    if (conn != null) conn.disconnect();
                }
            }
        });
    }

    private void applyBlurBackground(View overlay) {
        if (overlay == null) return;
        Bitmap blurred = DquestsCore.createBlurredViewBitmap(mainContentLayer, 8, 4);
        if (blurred != null) {
            overlay.setBackground(new BitmapDrawable(getResources(), blurred));
        } else {
            overlay.setBackgroundColor(0x33FFFFFF);
        }
    }

    private void applyKeepScreenOnFlag() {
        if (keepScreenOn) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void requestIgnoreBatteryOptimizations() {
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                if (pm != null && !pm.isIgnoringBatteryOptimizations(getPackageName())) {
                    Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                } else {
                    showCustomTip(I18n.t("Ứng dụng đã được tắt tối ưu hoá pin!", "Battery optimization is already disabled!"));
                }
            } catch (Exception e) {
                try {
                    startActivity(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS));
                } catch (Exception ignored) {
                }
            }
        }
    }

    private void openUrlInBrowser(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception ignored) {
        }
    }

    private String formatDuration(int totalSeconds) {
        if (totalSeconds <= 0) return "0s";
        int mins = totalSeconds / 60;
        int secs = totalSeconds % 60;
        return (mins > 0) ? (mins + "m " + secs + "s") : (secs + "s");
    }

    private int dp(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    private GradientDrawable createRoundedRect(int fillColor, int radiusPx, int strokeWidthPx, int strokeColor) {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setColor(fillColor);
        gd.setCornerRadius(radiusPx);
        if (strokeWidthPx > 0) gd.setStroke(strokeWidthPx, strokeColor);
        return gd;
    }

    private GradientDrawable createOvalDrawable(int fillColor, int strokeWidthPx, int strokeColor) {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.OVAL);
        gd.setColor(fillColor);
        if (strokeWidthPx > 0) gd.setStroke(strokeWidthPx, strokeColor);
        return gd;
    }

    private Drawable createCustomProgressDrawable() {
        GradientDrawable bg = createRoundedRect(0xFFE2E8F0, dp(4), 0, 0);
        GradientDrawable fg = createRoundedRect(0xFF5865F2, dp(4), 0, 0);
        ClipDrawable clip = new ClipDrawable(fg, Gravity.LEFT, ClipDrawable.HORIZONTAL);
        LayerDrawable layer = new LayerDrawable(new Drawable[]{bg, clip});
        layer.setId(0, android.R.id.background);
        layer.setId(1, android.R.id.progress);
        return layer;
    }

    private void setupSkeletonQuestsView() {
        skeletonQuestsContainer = new LinearLayout(this);
        skeletonQuestsContainer.setOrientation(LinearLayout.VERTICAL);
        skeletonQuestsContainer.setPadding(dp(14), dp(14), dp(14), dp(14));
        skeletonQuestsContainer.setVisibility(View.GONE);
        skeletonDrawables.clear();

        for (int i = 0; i < 3; i++) {
            skeletonQuestsContainer.addView(buildSingleSkeletonQuestCard());
        }

        pageQuests.addView(skeletonQuestsContainer, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        skeletonAnimator = ValueAnimator.ofFloat(0f, 1f);
        skeletonAnimator.setDuration(1150);
        skeletonAnimator.setRepeatCount(ValueAnimator.INFINITE);
        skeletonAnimator.setInterpolator(new LinearInterpolator());
        skeletonAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = (Float) animation.getAnimatedValue();
                for (int i = 0; i < skeletonDrawables.size(); i++) {
                    skeletonDrawables.get(i).setShimmerProgress(val);
                }
            }
        });
    }

    private View buildSingleSkeletonQuestCard() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14), dp(14), dp(14), dp(14));
        card.setBackground(createRoundedRect(0xFFFFFFFF, dp(14), 1, 0xFFE2E8F0));
        LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardLp.bottomMargin = dp(12);
        card.setLayoutParams(cardLp);

        LinearLayout topRow = new LinearLayout(this);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(Gravity.CENTER_VERTICAL);

        View thumb = createShimmerBlock(dp(52), dp(52), dp(12));
        LinearLayout.LayoutParams thumbLp = (LinearLayout.LayoutParams) thumb.getLayoutParams();
        thumbLp.rightMargin = dp(12);

        LinearLayout rightCol = new LinearLayout(this);
        rightCol.setOrientation(LinearLayout.VERTICAL);
        rightCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        View badgeLine = createShimmerBlock(dp(110), dp(14), dp(5));
        View titleLine = createShimmerBlock(ViewGroup.LayoutParams.MATCH_PARENT, dp(16), dp(6));
        ((LinearLayout.LayoutParams) titleLine.getLayoutParams()).topMargin = dp(6);
        View subLine = createShimmerBlock(dp(150), dp(12), dp(5));
        ((LinearLayout.LayoutParams) subLine.getLayoutParams()).topMargin = dp(5);

        rightCol.addView(badgeLine);
        rightCol.addView(titleLine);
        rightCol.addView(subLine);

        topRow.addView(thumb);
        topRow.addView(rightCol);
        card.addView(topRow);

        View rewardBlock = createShimmerBlock(ViewGroup.LayoutParams.MATCH_PARENT, dp(32), dp(8));
        ((LinearLayout.LayoutParams) rewardBlock.getLayoutParams()).topMargin = dp(10);
        card.addView(rewardBlock);

        View progBlock = createShimmerBlock(ViewGroup.LayoutParams.MATCH_PARENT, dp(10), dp(5));
        ((LinearLayout.LayoutParams) progBlock.getLayoutParams()).topMargin = dp(12);
        card.addView(progBlock);

        LinearLayout btnRow = new LinearLayout(this);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams btnRowLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnRowLp.topMargin = dp(12);
        btnRow.setLayoutParams(btnRowLp);

        View btn1 = createShimmerBlock(0, dp(34), dp(8));
        LinearLayout.LayoutParams b1Lp = new LinearLayout.LayoutParams(0, dp(34), 1f);
        b1Lp.rightMargin = dp(6);
        btn1.setLayoutParams(b1Lp);

        View btn2 = createShimmerBlock(0, dp(34), dp(8));
        LinearLayout.LayoutParams b2Lp = new LinearLayout.LayoutParams(0, dp(34), 1f);
        b2Lp.leftMargin = dp(6);
        btn2.setLayoutParams(b2Lp);

        btnRow.addView(btn1);
        btnRow.addView(btn2);
        card.addView(btnRow);

        return card;
    }

    private View createShimmerBlock(int widthPx, int heightPx, int radiusPx) {
        View v = new View(this);
        v.setLayoutParams(new LinearLayout.LayoutParams(widthPx, heightPx));
        DquestsCore.SkeletonShimmerDrawable d = new DquestsCore.SkeletonShimmerDrawable(radiusPx);
        skeletonDrawables.add(d);
        v.setBackground(d);
        return v;
    }

    private void setSkeletonLoadingVisible(boolean visible) {
        if (skeletonQuestsContainer == null) return;
        if (visible) {
            skeletonQuestsContainer.setVisibility(View.VISIBLE);
            if (!skeletonAnimator.isStarted()) {
                skeletonAnimator.start();
            }
        } else {
            skeletonQuestsContainer.setVisibility(View.GONE);
            if (skeletonAnimator.isStarted()) {
                skeletonAnimator.cancel();
            }
        }
    }

    private void setupCustomFloatingTipBar() {
        customTipBar = new LinearLayout(this);
        customTipBar.setOrientation(LinearLayout.HORIZONTAL);
        customTipBar.setGravity(Gravity.CENTER_VERTICAL);
        customTipBar.setPadding(dp(14), dp(11), dp(10), dp(11));

        customTipBar.setBackground(createRoundedRect(0xFFFFFFFF, dp(14), 1, 0xFFE2E8F0));
        if (Build.VERSION.SDK_INT >= 21) {
            customTipBar.setElevation(dp(8));
        }

        FrameLayout.LayoutParams barLp = new FrameLayout.LayoutParams(dp(350), ViewGroup.LayoutParams.WRAP_CONTENT);
        barLp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        barLp.bottomMargin = dp(26);
        customTipBar.setLayoutParams(barLp);

        ImageView ivInfoIcon = new ImageView(this);
        LinearLayout.LayoutParams infoLp = new LinearLayout.LayoutParams(dp(20), dp(20));
        infoLp.rightMargin = dp(10);
        ivInfoIcon.setLayoutParams(infoLp);
        ivInfoIcon.setImageDrawable(DquestsCore.createVectorIconDrawable(DquestsCore.ICON_INFO, 0xFF5865F2));

        tvCustomTipText = new TextView(this);
        tvCustomTipText.setTextColor(0xFF0F172A);
        tvCustomTipText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tvCustomTipText.setTypeface(null, Typeface.BOLD);
        tvCustomTipText.setMaxLines(3);
        tvCustomTipText.setEllipsize(TextUtils.TruncateAt.END);
        tvCustomTipText.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        FrameLayout btnCloseTip = new FrameLayout(this);
        LinearLayout.LayoutParams closeLp = new LinearLayout.LayoutParams(dp(26), dp(26));
        closeLp.leftMargin = dp(8);
        btnCloseTip.setLayoutParams(closeLp);
        btnCloseTip.setBackground(null);

        ImageView ivCloseX = new ImageView(this);

        FrameLayout.LayoutParams xLp = new FrameLayout.LayoutParams(dp(15), dp(15), Gravity.CENTER);
        ivCloseX.setLayoutParams(xLp);
        ivCloseX.setImageDrawable(DquestsCore.createVectorIconDrawable(DquestsCore.ICON_CLOSE_X, 0xFF64748B));
        btnCloseTip.addView(ivCloseX);

        btnCloseTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissCustomTipAnimated();
            }
        });

        customTipBar.addView(ivInfoIcon);
        customTipBar.addView(tvCustomTipText);
        customTipBar.addView(btnCloseTip);

        customTipBar.setVisibility(View.GONE);
        customTipBar.setTranslationY(dp(120));
        rootContainer.addView(customTipBar);

        hideTipRunnable = new Runnable() {
            @Override
            public void run() {
                dismissCustomTipAnimated();
            }
        };
    }

    public void showCustomTip(final String message) {
        if (TextUtils.isEmpty(message)) return;
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (customTipBar == null) return;
                uiHandler.removeCallbacks(hideTipRunnable);
                tvCustomTipText.setText(message);

                customTipBar.animate().cancel();
                if (customTipBar.getVisibility() != View.VISIBLE) {
                    customTipBar.setTranslationY(dp(110));
                    customTipBar.setAlpha(0f);
                    customTipBar.setVisibility(View.VISIBLE);
                }

                customTipBar.animate()
                        .translationY(0f)
                        .alpha(1f)
                        .setDuration(240)
                        .start();

                uiHandler.postDelayed(hideTipRunnable, 3200L);
            }
        });
    }

    private void dismissCustomTipAnimated() {
        if (customTipBar == null || customTipBar.getVisibility() != View.VISIBLE) return;
        uiHandler.removeCallbacks(hideTipRunnable);
        customTipBar.animate().cancel();
        customTipBar.animate()
                .translationY(dp(110))
                .alpha(0f)
                .setDuration(190)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        customTipBar.setVisibility(View.GONE);
                    }
                })
                .start();
    }

    private void setupAccountLogoutPopupView() {
        overlayAccountLogoutPopup = new FrameLayout(this);
        overlayAccountLogoutPopup.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        overlayAccountLogoutPopup.setBackgroundColor(Color.TRANSPARENT);
        overlayAccountLogoutPopup.setVisibility(View.GONE);
        overlayAccountLogoutPopup.setClickable(true);

        overlayAccountLogoutPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlayAccountLogoutPopup.setVisibility(View.GONE);
            }
        });

        rootContainer.addView(overlayAccountLogoutPopup);
    }

    private void showAccountLogoutPopup(final AccountSession targetSession, float touchRawX, float touchRawY) {
        if (targetSession == null || overlayAccountLogoutPopup == null) return;

        overlayAccountLogoutPopup.removeAllViews();

        final LinearLayout popupCard = new LinearLayout(this);
        popupCard.setOrientation(LinearLayout.VERTICAL);
        popupCard.setGravity(Gravity.CENTER_HORIZONTAL);
        popupCard.setPadding(dp(14), dp(12), dp(14), dp(12));
        popupCard.setBackground(createRoundedRect(0xFFFFFFFF, dp(14), 1, 0xFFCBD5E1));
        if (Build.VERSION.SDK_INT >= 21) {
            popupCard.setElevation(dp(10));
        }

        final int popupWidth = dp(230);
        final FrameLayout.LayoutParams cardLp = new FrameLayout.LayoutParams(popupWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardLp.gravity = Gravity.TOP | Gravity.LEFT;

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        int posX = (int) (touchRawX - (popupWidth / 2f));
        if (posX < dp(14)) posX = dp(14);
        if (posX + popupWidth > screenWidth - dp(14)) {
            posX = screenWidth - popupWidth - dp(14);
        }

        int posY = (int) touchRawY + dp(10);
        if (posY > screenHeight - dp(160)) {
            posY = (int) touchRawY - dp(120);
        }

        cardLp.leftMargin = posX;
        cardLp.topMargin = Math.max(dp(20), posY);
        popupCard.setLayoutParams(cardLp);
        popupCard.setClickable(true);

        TextView tvTitle = new TextView(this);
        tvTitle.setText("@" + targetSession.profile.username);
        tvTitle.setTextColor(0xFF0F172A);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setSingleLine(true);
        tvTitle.setEllipsize(TextUtils.TruncateAt.END);

        TextView tvSub = new TextView(this);
        tvSub.setText(I18n.t("Tuỳ chọn tài khoản", "Account option"));
        tvSub.setTextColor(0xFF64748B);
        tvSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        tvSub.setPadding(0, dp(2), 0, dp(12));

        LinearLayout btnLogout = new LinearLayout(this);
        btnLogout.setOrientation(LinearLayout.HORIZONTAL);
        btnLogout.setGravity(Gravity.CENTER);
        btnLogout.setPadding(dp(14), dp(10), dp(14), dp(10));
        btnLogout.setBackground(createRoundedRect(0xFFFEF2F2, dp(10), 1, 0xFFFECACA));
        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnLogout.setLayoutParams(btnLp);

        ImageView ivLogout = new ImageView(this);
        LinearLayout.LayoutParams icLp = new LinearLayout.LayoutParams(dp(18), dp(18));
        icLp.rightMargin = dp(8);
        ivLogout.setLayoutParams(icLp);
        ivLogout.setImageDrawable(DquestsCore.createVectorIconDrawable(DquestsCore.ICON_LOGOUT, 0xFFEF4444));

        TextView tvBtnText = new TextView(this);
        tvBtnText.setText(I18n.t("Đăng xuất tài khoản này", "Log out this account"));
        tvBtnText.setTextColor(0xFFEF4444);
        tvBtnText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tvBtnText.setTypeface(null, Typeface.BOLD);

        btnLogout.addView(ivLogout);
        btnLogout.addView(tvBtnText);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlayAccountLogoutPopup.setVisibility(View.GONE);
                logoutSpecificAccount(targetSession);
            }
        });

        popupCard.addView(tvTitle);
        popupCard.addView(tvSub);
        popupCard.addView(btnLogout);

        overlayAccountLogoutPopup.addView(popupCard);
        overlayAccountLogoutPopup.setVisibility(View.VISIBLE);
    }

    private void logoutSpecificAccount(AccountSession sessionToLogout) {
        if (sessionToLogout == null) return;
        String removedName = sessionToLogout.profile.username;
        sessionToLogout.shutdownAll();
        accountSessions.remove(sessionToLogout.profile.userId);

        if (currentSession != null && currentSession.profile.userId.equals(sessionToLogout.profile.userId)) {
            currentSession = accountSessions.isEmpty() ? null : accountSessions.values().iterator().next();
        }
        saveAccountsToPrefs();

        showCustomTip(I18n.t("Đã đăng xuất @" + removedName, "Logged out @" + removedName));

        if (overlayProfileMenu.getVisibility() == View.VISIBLE) {
            populateProfileMenuContent();
        }
        if (layoutAccountPicker.getVisibility() == View.VISIBLE) {
            showStartupAccountPicker();
        }

        if (currentSession != null) {
            updateHeaderAndWorkspaceForCurrentAccount();
        } else {
            if (overlayProfileMenu.getVisibility() == View.VISIBLE) {
                closeProfileMenuAnimated();
            }
            showLoginScreen(null);
        }
    }

    private void startNetworkStatusMonitor() {
        networkMonitorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    boolean online = checkInternetByVersionTxt();
                    if (online != isNetworkConnected) {
                        isNetworkConnected = online;
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                updateHeaderAndWorkspaceForCurrentAccount();
                                if (!isNetworkConnected) {
                                    showCustomTip(I18n.t("Không thể kết nối mạng!", "No internet connection!"));
                                } else {
                                    showCustomTip(I18n.t("Đã kết nối mạng trở lại!", "Internet connection restored!"));
                                    if (currentSession != null) {
                                        currentSession.refreshQuestsAsync();
                                    }
                                }
                            }
                        });
                    }
                    try {

                        Thread.sleep(5000L);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        });
        networkMonitorThread.start();
    }

    private boolean checkInternetByVersionTxt() {
        try {
            java.net.URL url = new java.net.URL(DquestsCore.VERSION_CHECK_URL);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(4000);
            conn.setReadTimeout(4000);
            conn.setUseCaches(false);
            int code = conn.getResponseCode();
            conn.disconnect();
            return (code >= 200 && code < 400);
        } catch (Exception e) {
            return false;
        }
    }
}
