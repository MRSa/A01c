package jp.sfjp.gokigen.a01c

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import jp.sfjp.gokigen.a01c.IShowInformation.operation
import jp.sfjp.gokigen.a01c.liveview.*
import jp.sfjp.gokigen.a01c.liveview.dialog.FavoriteSettingSelectionDialog
import jp.sfjp.gokigen.a01c.liveview.dialog.IDialogDismissedNotifier
import jp.sfjp.gokigen.a01c.liveview.glview.GokigenGLView
import jp.sfjp.gokigen.a01c.olycamerawrapper.OlyCameraCoordinator
import jp.sfjp.gokigen.a01c.preference.IPreferenceCameraPropertyAccessor
import jp.sfjp.gokigen.a01c.preference.PreferenceAccessWrapper
import jp.sfjp.gokigen.a01c.thetacamerawrapper.ThetaCameraController
import jp.sfjp.gokigen.a01c.utils.GestureParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(),
    IChangeScene,
    IShowInformation,
    ICameraStatusReceiver,
    IDialogDismissedNotifier,
    IWifiConnection
{
    private lateinit var preferences: PreferenceAccessWrapper
    private var liveView: CameraLiveImageView? = null
    private var glView: GokigenGLView? = null
    private var powerManager: PowerManager? = null
    private var gestureParser: GestureParser? = null
    private var currentCoordinator: ICameraController? = null
    private var olyAirCoordinator: ICameraController? = null
    private var thetaCoordinator: ICameraController? = null
    private var messageDrawer: IMessageDrawer? = null
    private var listener: CameraLiveViewOnTouchListener? = null
    private var selectionDialog: FavoriteSettingSelectionDialog? = null
    private var cameraDisconnectedHappened = false
    private var wifiConnection: WifiConnection? = null
    private var liveViewListener: CameraLiveViewListenerImpl? = null
    private var enableGlView = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        Log.v(TAG, "onCreate()")

        // スプラッシュ画面の表示 (super.onCreate より前に呼び出し)
        installSplashScreen()

        // Edge-to-Edge を有効化 (super.onCreate より前を推奨)
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        val mainScreen = findViewById<View>(R.id.main_screen)
        mainScreen?.let { view ->
            ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

                // ステータスバーを非表示にするため、top は 0 に固定（あるいは左右・下のみ適用）
                v.setPadding(
                    systemBars.left,
                    0, // top を 0 にして上部の余白を詰める
                    systemBars.right,
                    systemBars.bottom
                )
                insets
            }
            view.keepScreenOn = true
        }

        runCatching {
            if (!hasGps()) {
                Log.d(TAG, " ----- This hardware doesn't have GPS.")
            }
            powerManager = getSystemService(POWER_SERVICE) as? PowerManager

            setupCameraCoordinator()
            setupInitialButtonIcons()
            setupActionListener()
        }.onFailure { e ->
            Log.e(TAG, "Error in setup", e)
        }

        runCatching {
            if (allPermissionsGranted()) {
                wifiConnection = WifiConnection(applicationContext, this).apply {
                    startWatchWifiStatus()
                }
            } else {
                Log.v(TAG, ">>> Request Permissions...")
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
            }
        }.onFailure { ex ->
            Log.e(TAG, "Error in permission check", ex)
        }
    }

    private fun allPermissionsGranted(): Boolean {
        return REQUIRED_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(baseContext, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onResume() {
        super.onResume()
        Log.v(TAG, "onResume()")
        runCatching {
            wifiConnection?.startWatchWifiStatus()
        }.onFailure { e ->
            Log.e(TAG, "onResume error", e)
        }
    }

    override fun onPause() {
        super.onPause()
        Log.v(TAG, "onPause()")
    }

    override fun onStart() {
        super.onStart()
        Log.v(TAG, "onStart()")
    }

    override fun onStop() {
        super.onStop()
        Log.v(TAG, "onStop()")

        // パワーマネージャを確認し、interactive modeではない場合はライブビューやカメラ電源を維持
        if (powerManager?.isInteractive != true) {
            Log.v(TAG, "not interactive, keep live view.")
            return
        }

        runCatching {
            currentCoordinator?.stopLiveView()
            currentCoordinator?.getStatusWatcher()?.stopStatusWatch()
        }.onFailure { e ->
            Log.v(TAG, " onStop error: ${e.localizedMessage}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "onDestroy()")
        exitApplication()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupActionListener() {
        runCatching {
            Log.v(TAG, "setupActionListener()")
            val btnIds = intArrayOf(
                R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4, R.id.btn_5, R.id.btn_6,
                R.id.btn_021, R.id.btn_022, R.id.btn_023, R.id.btn_024
            )
            for (id in btnIds) {
                findViewById<ImageButton>(id)?.apply {
                    setOnClickListener(listener)
                    setOnLongClickListener(listener)
                }
            }

            if (liveView == null) {
                liveView = findViewById(R.id.liveview)
            }
            liveView?.apply {
                setOnTouchListener(listener)
                this@MainActivity.messageDrawer = this.messageDrawer?.also { drawer ->
                    drawer.levelGauge = currentCoordinator?.getLevelGauge()
                }
            }
        }.onFailure { e ->
            Log.e(TAG, "setupActionListener error", e)
        }
    }

    private fun setupInitialButtonIcons() {
        runCatching {
            if (currentCoordinator != null) {
                val defaultPrefs = PreferenceManager.getDefaultSharedPreferences(this)
                val showGrid = defaultPrefs.getBoolean(
                    IPreferenceCameraPropertyAccessor.SHOW_GRID_STATUS,
                    true
                )
                val resId = if (showGrid) R.drawable.btn_ic_grid_off else R.drawable.btn_ic_grid_on
                setButtonDrawable(IShowInformation.BUTTON_1, resId)
            }
        }.onFailure { e ->
            Log.e(TAG, "setupInitialButtonIcons error", e)
        }
    }

    private fun launchWifiSettingScreen(): Boolean {
        val intents = listOf(
            Intent(Settings.ACTION_WIFI_SETTINGS),
            Intent("com.google.android.clockwork.settings.connectivity.wifi.ADD_NETWORK_SETTINGS"),
            Intent("com.google.android.clockwork.settings.connectivity.wifi.ADD_NETWORK_SETTINGS").apply {
                setClassName("com.google.android.apps.wearable.settings", "com.google.android.clockwork.settings.wifi.WifiSettingsActivity")
            },
            Intent("android.intent.action.MAIN").apply {
                setClassName("com.google.android.apps.wearable.settings", "com.google.android.clockwork.settings.MainSettingsActivity")
            }
        )

        for (intent in intents) {
            try {
                startActivity(intent)
                return true
            } catch (_: ActivityNotFoundException) {
                // 次の Intent を試行
            }
        }
        return false
    }

    private fun setupCameraCoordinator() {
        runCatching {
            preferences = PreferenceAccessWrapper(this).apply { initialize() }
            val connectionMethod = preferences.getString(
                IPreferenceCameraPropertyAccessor.CONNECTION_METHOD,
                IPreferenceCameraPropertyAccessor.CONNECTION_METHOD_DEFAULT_VALUE
            )

            val lv = liveView ?: findViewById<CameraLiveImageView>(R.id.liveview)?.also { liveView = it }
            lv?.visibility = View.VISIBLE
            lv?.let { liveViewListener = CameraLiveViewListenerImpl(it) }

            val gv = glView ?: findViewById<GokigenGLView>(R.id.glview)?.also { glView = it }
            gv?.let { view ->
                if (gestureParser == null) {
                    gestureParser = GestureParser(applicationContext, view)
                }
                enableGlView = preferences.getBoolean(IPreferenceCameraPropertyAccessor.THETA_GL_VIEW, false)
                if (enableGlView && connectionMethod.contains(IPreferenceCameraPropertyAccessor.CONNECTION_METHOD_THETA)) {
                    liveViewListener?.let { view.setImageProvider(it) }
                    view.visibility = View.VISIBLE
                    lv?.visibility = View.GONE
                }
            }

            olyAirCoordinator = OlyCameraCoordinator(this, liveView, this, this)
            thetaCoordinator = ThetaCameraController(this, this, this)

            val coordinator = if (connectionMethod.contains(IPreferenceCameraPropertyAccessor.CONNECTION_METHOD_THETA)) {
                thetaCoordinator
            } else {
                olyAirCoordinator
            }
            currentCoordinator = coordinator

            liveViewListener?.let { coordinator?.setLiveViewListener(it) }

            val lvNonNull = lv ?: return@runCatching
            val dispatcher = coordinator?.getFeatureDispatcher(
                this,
                this,
                coordinator,
                preferences,
                lvNonNull
            )
            listener = CameraLiveViewOnTouchListener(this, dispatcher!!, this)
            selectionDialog = FavoriteSettingSelectionDialog(
                this,
                coordinator.getCameraPropertyLoadSaveOperations(),
                this
            )
            connectToCamera()

        }.onFailure { e ->
            Log.e(TAG, "setupCameraCoordinator error", e)
        }
    }

    private fun connectToCamera() {
        lifecycleScope.launch(Dispatchers.IO) {
            currentCoordinator?.getConnectionInterface()?.connect()
        }
    }

    override fun exitApplication() {
        runCatching {
            Log.v(TAG, "exitApplication()")

            if (powerManager?.isInteractive != true) {
                Log.v(TAG, "not interactive, keep live view.")
                return
            }

            currentCoordinator?.stopLiveView()
            currentCoordinator?.getStatusWatcher()?.stopStatusWatch()

            val disconnectOnExit = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                IPreferenceCameraPropertyAccessor.EXIT_APPLICATION_WITH_DISCONNECT,
                true
            )
            if (disconnectOnExit) {
                Log.v(TAG, "Shutdown camera...")
                currentCoordinator?.getConnectionInterface()?.disconnect(true)
            }
        }.onFailure { e ->
            Log.e(TAG, "exitApplication error", e)
        }
    }

    override fun checkConnectionFeature(id: Int, btnId: Int): Boolean {
        return when (id) {
            0 -> launchWifiSettingScreen()
            1 -> {
                changeConnectionMethod()
                true
            }
            else -> false
        }
    }

    override fun touchedPosition(posX: Float, posY: Float): Boolean {
        Log.v(TAG, "touchedPosition ($posX, $posY)")
        return liveView?.touchedPosition(posX, posY) ?: false
    }

    override fun showConnectionStatus(): Boolean {
        return runCatching {
            if (listener?.isEnabledOperation == operation.ONLY_CONNECT && cameraDisconnectedHappened) {
                connectToCamera()
                cameraDisconnectedHappened = false
                true
            } else {
                false
            }
        }.getOrDefault(false)
    }

    override fun onStatusNotify(message: String) {
        setMessage(IShowInformation.AREA_C, Color.WHITE, message)
    }

    override fun onCameraConnected() {
        Log.v(TAG, "onCameraConnected()")
        runCatching {
            currentCoordinator?.apply {
                connectFinished()
                startLiveView()
                setRecViewMode(false)
                updateStatusAll()
                getStatusWatcher()?.startStatusWatch()
            }
            listener?.setEnableOperation(operation.ENABLE)
            setMessage(IShowInformation.AREA_C, Color.WHITE, "")
        }.onFailure { e ->
            Log.e(TAG, "onCameraConnected error", e)
        }
    }

    override fun onCameraDisconnected() {
        Log.v(TAG, "onCameraDisconnected()")
        runCatching {
            setMessage(
                IShowInformation.AREA_C,
                Color.YELLOW,
                getString(R.string.camera_disconnected)
            )
            listener?.setEnableOperation(operation.ONLY_CONNECT)
            cameraDisconnectedHappened = true
        }.onFailure { e ->
            Log.e(TAG, "onCameraDisconnected error", e)
        }
    }

    override fun onCameraConnectError(message: String) {
        Log.v(TAG, "onCameraConnectError()")
        runCatching {
            setMessage(IShowInformation.AREA_C, Color.YELLOW, message)
            listener?.setEnableOperation(operation.ONLY_CONNECT)
            cameraDisconnectedHappened = true
        }.onFailure { e ->
            Log.e(TAG, "onCameraConnectError error", e)
        }
    }

    override fun onCameraOccursException(message: String, e: Exception) {
        Log.v(TAG, "onCameraOccursException(): $message", e)
        runCatching {
            setMessage(IShowInformation.AREA_C, Color.YELLOW, message)
            listener?.setEnableOperation(operation.ONLY_CONNECT)
            cameraDisconnectedHappened = true
        }.onFailure { ee ->
            Log.e(TAG, "onCameraOccursException fallback error", ee)
        }
    }

    override fun setMessage(area: Int, color: Int, message: String) {
        var id = 0
        when (area) {
            IShowInformation.AREA_1 -> {
                id = R.id.text_1
                setMessage(IShowInformation.AREA_1_2, color, message)
            }
            IShowInformation.AREA_2 -> {
                id = R.id.text_2
                setMessage(IShowInformation.AREA_2_2, color, message)
            }
            IShowInformation.AREA_3 -> {
                id = R.id.text_3
                setMessage(IShowInformation.AREA_3_2, color, message)
            }
            IShowInformation.AREA_4 -> {
                id = R.id.text_4
                setMessage(IShowInformation.AREA_4_2, color, message)
                setMessage(IShowInformation.AREA_5_2, color, message)
            }
            IShowInformation.AREA_1_2 -> id = R.id.text_11
            IShowInformation.AREA_2_2 -> id = R.id.text_12
            IShowInformation.AREA_3_2 -> id = R.id.text_13
            IShowInformation.AREA_4_2 -> id = R.id.text_14
            IShowInformation.AREA_5_2 -> id = R.id.text_15
        }

        if (messageDrawer != null) {
            val drawerArea = when (area) {
                IShowInformation.AREA_C -> IMessageDrawer.MessageArea.CENTER
                IShowInformation.AREA_5 -> IMessageDrawer.MessageArea.UPLEFT
                IShowInformation.AREA_6 -> IMessageDrawer.MessageArea.LOWLEFT
                IShowInformation.AREA_7 -> IMessageDrawer.MessageArea.UPRIGHT
                IShowInformation.AREA_8 -> IMessageDrawer.MessageArea.LOWRIGHT
                IShowInformation.AREA_9 -> IMessageDrawer.MessageArea.UPCENTER
                IShowInformation.AREA_A -> IMessageDrawer.MessageArea.LOWCENTER
                IShowInformation.AREA_B -> IMessageDrawer.MessageArea.CENTERLEFT
                IShowInformation.AREA_D -> IMessageDrawer.MessageArea.CENTERRIGHT
                else -> null
            }
            if (drawerArea != null) {
                val size = if (area == IShowInformation.AREA_C) IMessageDrawer.SIZE_LARGE else IMessageDrawer.SIZE_STD
                messageDrawer?.setMessageToShow(drawerArea, color, size, message)
                return
            }
            if (id == 0) return
        }

        val areaId = id
        runOnUiThread {
            findViewById<TextView>(areaId)?.apply {
                setTextColor(color)
                text = message
                invalidate()
            }
        }
    }

    override fun setButtonDrawable(button: Int, labelId: Int) {
        val id = when (button) {
            IShowInformation.BUTTON_1 -> R.id.btn_1
            IShowInformation.BUTTON_2 -> R.id.btn_2
            IShowInformation.BUTTON_3 -> R.id.btn_3
            IShowInformation.BUTTON_4 -> R.id.btn_4
            IShowInformation.BUTTON_5 -> R.id.btn_5
            IShowInformation.BUTTON_6 -> R.id.btn_6
            IShowInformation.BUTTON_7 -> R.id.btn_025
            IShowInformation.BUTTON_8 -> R.id.btn_026
            else -> R.id.btn_6
        }
        runOnUiThread {
            runCatching {
                findViewById<ImageButton>(id)?.apply {
                    setImageDrawable(ContextCompat.getDrawable(applicationContext, labelId))
                    invalidate()
                }
            }.onFailure { e ->
                Log.e(TAG, "setButtonDrawable error", e)
            }
        }
    }

    private fun hasGps(): Boolean {
        return packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (enableGlView) {
            gestureParser?.onTouch(event)
        }
        return super.dispatchTouchEvent(event)
    }

    override fun vibrate(vibratePattern: Int) {
        runCatching {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                (getSystemService(VIBRATOR_MANAGER_SERVICE) as? VibratorManager)?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getSystemService(VIBRATOR_SERVICE) as? Vibrator
            } ?: return

            if (!vibrator.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val duration = when (vibratePattern) {
                    IShowInformation.VIBRATE_PATTERN_SIMPLE_SHORT -> 30L
                    IShowInformation.VIBRATE_PATTERN_SIMPLE_MIDDLE -> 80L
                    IShowInformation.VIBRATE_PATTERN_SIMPLE_LONG -> 150L
                    IShowInformation.VIBRATE_PATTERN_SIMPLE_LONGLONG -> 300L
                    else -> 30L
                }
                if (vibratePattern == IShowInformation.VIBRATE_PATTERN_SHORT_DOUBLE) {
                    val pattern = longArrayOf(10, 35, 30, 35)
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
                } else {
                    vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
                }
            } else {
                @Suppress("DEPRECATION")
                when (vibratePattern) {
                    IShowInformation.VIBRATE_PATTERN_SIMPLE_SHORT -> vibrator.vibrate(30)
                    IShowInformation.VIBRATE_PATTERN_SIMPLE_MIDDLE -> vibrator.vibrate(80)
                    IShowInformation.VIBRATE_PATTERN_SIMPLE_LONG -> vibrator.vibrate(150)
                    IShowInformation.VIBRATE_PATTERN_SIMPLE_LONGLONG -> vibrator.vibrate(300)
                    IShowInformation.VIBRATE_PATTERN_SHORT_DOUBLE -> {
                        val pattern = longArrayOf(10, 35, 30, 35)
                        vibrator.vibrate(pattern, -1)
                    }
                }
            }
        }.onFailure { e ->
            Log.e(TAG, "vibrate error", e)
        }
    }

    override fun setEnabledOperation(operation: operation) {
        listener?.setEnableOperation(operation)
    }

    override fun showFavoriteSettingsDialog() {
        if (liveView != null && listener != null && listener?.isEnabledOperation != operation.ONLY_CONNECT) {
            listener?.setEnableOperation(operation.ENABLE_ONLY_TOUCHED_POSITION)
            liveView?.showDialog(selectionDialog)
        }
    }

    override fun showToast(rscId: Int, appendMessage: String, duration: Int) {
        runOnUiThread {
            runCatching {
                val message = if (rscId != 0) getString(rscId) + appendMessage else appendMessage
                Toast.makeText(applicationContext, message, duration).show()
            }.onFailure { e ->
                Log.e(TAG, "showToast error", e)
            }
        }
    }

    override fun invalidate() {
        runOnUiThread {
            runCatching { liveView?.invalidate() }
        }
    }

    override fun dialogDismissed(isExecuted: Boolean) {
        runCatching {
            if (liveView != null && listener != null) {
                liveView?.hideDialog()
                listener?.setEnableOperation(operation.ENABLE)
            }
        }.onFailure { e ->
            Log.e(TAG, "dialogDismissed error", e)
        }
    }

    private fun updateConnectionMethodMessage() {
        runCatching {
            val connectionMethod = preferences.getString(
                IPreferenceCameraPropertyAccessor.CONNECTION_METHOD,
                IPreferenceCameraPropertyAccessor.CONNECTION_METHOD_DEFAULT_VALUE
            )
            val methodId = if (connectionMethod.contains(IPreferenceCameraPropertyAccessor.CONNECTION_METHOD_THETA)) {
                R.string.connection_method_theta
            } else {
                R.string.connection_method_opc
            }
            setMessage(IShowInformation.AREA_7, Color.MAGENTA, getString(methodId))
            liveView?.apply {
                setupInitialBackgroundImage(this@MainActivity)
                visibility = View.VISIBLE
                invalidate()
            }
        }.onFailure { e ->
            Log.e(TAG, "updateConnectionMethodMessage error", e)
        }
    }

    private fun updateConnectionMethod(parameter: String, method: ICameraController?) {
        runCatching {
            currentCoordinator = method
            preferences.putString(IPreferenceCameraPropertyAccessor.CONNECTION_METHOD, parameter)
            vibrate(IShowInformation.VIBRATE_PATTERN_SHORT_DOUBLE)
            enableGlView = preferences.getBoolean(IPreferenceCameraPropertyAccessor.THETA_GL_VIEW, false)

            if (enableGlView && parameter.contains(IPreferenceCameraPropertyAccessor.CONNECTION_METHOD_THETA)) {
                val gv = glView ?: findViewById<GokigenGLView>(R.id.glview)?.also { glView = it }
                gv?.let { view ->
                    gestureParser = GestureParser(applicationContext, view)
                    liveViewListener?.let { view.setImageProvider(it) }
                    view.visibility = View.VISIBLE
                    liveView?.visibility = View.GONE
                }
            } else {
                if (liveView == null) {
                    liveView = findViewById(R.id.liveview)
                }
                glView?.visibility = View.GONE
                liveView?.visibility = View.VISIBLE
            }
        }.onFailure { e ->
            Log.e(TAG, "updateConnectionMethod error", e)
        }
    }

    private fun changeConnectionMethod() {
        runOnUiThread {
            runCatching {
                val connectionMethod = preferences.getString(
                    IPreferenceCameraPropertyAccessor.CONNECTION_METHOD,
                    IPreferenceCameraPropertyAccessor.CONNECTION_METHOD_DEFAULT_VALUE
                )
                val isTheta = connectionMethod.contains(IPreferenceCameraPropertyAccessor.CONNECTION_METHOD_THETA)

                val titleId = if (isTheta) R.string.change_title_from_theta_to_opc else R.string.change_title_from_opc_to_theta
                val messageId = if (isTheta) R.string.change_message_from_theta_to_opc else R.string.change_message_from_opc_to_theta

                ConfirmationDialog(this).show(titleId, messageId) {
                    Log.v(TAG, " --- CONFIRMED! --- (theta:$isTheta)")
                    if (isTheta) {
                        updateConnectionMethod(
                            IPreferenceCameraPropertyAccessor.CONNECTION_METHOD_OPC,
                            olyAirCoordinator
                        )
                    } else {
                        updateConnectionMethod(
                            IPreferenceCameraPropertyAccessor.CONNECTION_METHOD_THETA,
                            thetaCoordinator
                        )
                    }
                    updateConnectionMethodMessage()
                }
            }.onFailure { e ->
                Log.e(TAG, "changeConnectionMethod error", e)
            }
        }
    }

    override fun onConnectedToWifi() {
        Log.v(TAG, "onConnectedToWifi()")
    }

    override fun onNetworkAvailable() {
        Log.v(TAG, "onNetworkAvailable()")
    }

    override fun onNetworkLost() {
        Log.v(TAG, "onNetworkLost()")
    }

    override fun onNetworkConnectionTimeout() {
        Log.v(TAG, "onNetworkConnectionTimeout()")
    }

    override fun onError(message: String?) {
        Log.v(TAG, "onError(): $message")
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val REQUEST_CODE_PERMISSIONS = 10

        private val REQUIRED_PERMISSIONS = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.CINNAMON_BUN -> arrayOf(
                Manifest.permission.VIBRATE,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
                Manifest.permission.NEARBY_WIFI_DEVICES,
                Manifest.permission.ACCESS_LOCAL_NETWORK
            )
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(
                Manifest.permission.VIBRATE,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
                Manifest.permission.NEARBY_WIFI_DEVICES
            )
            else -> arrayOf(
                Manifest.permission.VIBRATE,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.CHANGE_WIFI_MULTICAST_STATE
            )
        }
    }
}
