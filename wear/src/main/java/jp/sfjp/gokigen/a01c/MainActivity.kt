package jp.sfjp.gokigen.a01c

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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

/**
 * メインのActivity
 *
 */
class MainActivity : AppCompatActivity(), IChangeScene, IShowInformation, ICameraStatusReceiver, IDialogDismissedNotifier, IWifiConnection
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

    /**
     *
     */
    override fun onCreate(savedInstanceState: Bundle?)
    {
        Log.v(TAG, "onCreate()")
        super.onCreate(savedInstanceState)

        ///////// SHOW SPLASH SCREEN /////////
        installSplashScreen()

        //  画面全体の設定
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        try
        {
            if (!hasGps())
            {
                // GPS機能が搭載されていない場合...ログに出力する
                Log.d(TAG, " ----- This hardware doesn't have GPS.")
            }
            // パワーマネージャをつかまえる
            powerManager = getSystemService(POWER_SERVICE) as PowerManager

            setupCameraCoordinator()
            setupInitialButtonIcons()
            setupActionListener()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }

        try
        {
            if (allPermissionsGranted())
            {
                wifiConnection = WifiConnection(this.applicationContext, this)
                wifiConnection?.startWatchWifiStatus()
            }
            else
            {
                Log.v(TAG, ">>> Request Permissions...")
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
            }
        }
        catch (ex: Exception)
        {
            ex.printStackTrace()
        }
    }

/*
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
*/
    private fun allPermissionsGranted() : Boolean
    {
        var result = true
        for (param in REQUIRED_PERMISSIONS)
        {
            if (ContextCompat.checkSelfPermission(baseContext, param) != PackageManager.PERMISSION_GRANTED)
            {
                if ((param == Manifest.permission.NEARBY_WIFI_DEVICES)&&(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU))
                {
                    // NEARBY_WIFI_DEVICESが TIRAMISUより小さい場合は、権限付与の判断を除外 (SDK: 33より下はエラーになるため)
                }
                else
                {
                    Log.v(TAG, " Permission: $param : ${Build.VERSION.SDK_INT}")
                    result = false
                }
            }
        }
        return (result)
    }

    /**
     *
     */
    override fun onResume()
    {
        super.onResume()
        Log.v(TAG, "onResume()")
        try
        {
            if (wifiConnection != null)
            {
                wifiConnection?.startWatchWifiStatus()
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    /**
     *
     */
    override fun onPause()
    {
        super.onPause()
        Log.v(TAG, "onPause()")
    }

    /**
     *
     *
     */
    public override fun onStart()
    {
        super.onStart()
        Log.v(TAG, "onStart()")
    }

    /**
     *
     *
     */
    public override fun onStop()
    {
        super.onStop()
        Log.v(TAG, "onStop()")
        exitApplication()
    }

    /**
     * ボタンが押された、画面がタッチされた、、は、リスナクラスで処理するよう紐づける
     *
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setupActionListener()
    {
        try
        {
            Log.v(TAG, "setupActionListener()")
            val btn1 = findViewById<ImageButton>(R.id.btn_1)
            btn1.setOnClickListener(listener)
            btn1.setOnLongClickListener(listener)
            val btn2 = findViewById<ImageButton>(R.id.btn_2)
            btn2.setOnClickListener(listener)
            btn2.setOnLongClickListener(listener)
            val btn3 = findViewById<ImageButton>(R.id.btn_3)
            btn3.setOnClickListener(listener)
            btn3.setOnLongClickListener(listener)
            val btn4 = findViewById<ImageButton>(R.id.btn_4)
            btn4.setOnClickListener(listener)
            btn4.setOnLongClickListener(listener)
            val btn5 = findViewById<ImageButton>(R.id.btn_5)
            btn5.setOnClickListener(listener)
            btn5.setOnLongClickListener(listener)
            val btn6 = findViewById<ImageButton>(R.id.btn_6)
            btn6.setOnClickListener(listener)
            btn6.setOnLongClickListener(listener)
            val textArea1 = findViewById<ImageButton>(R.id.btn_021)
            textArea1.setOnClickListener(listener)
            textArea1.setOnLongClickListener(listener)
            val textArea2 = findViewById<ImageButton>(R.id.btn_022)
            textArea2.setOnClickListener(listener)
            textArea2.setOnLongClickListener(listener)
            val textArea3 = findViewById<ImageButton>(R.id.btn_023)
            textArea3.setOnClickListener(listener)
            textArea3.setOnLongClickListener(listener)
            val textArea4 = findViewById<ImageButton>(R.id.btn_024)
            textArea4.setOnClickListener(listener)
            textArea4.setOnLongClickListener(listener)
            if (liveView == null)
            {
                liveView = findViewById(R.id.liveview)
            }
            liveView?.setOnTouchListener(listener)
            messageDrawer = liveView?.messageDrawer
            messageDrawer?.levelGauge = currentCoordinator?.levelGauge


        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    /**
     * ボタンアイコンの初期設定
     *
     */
    private fun setupInitialButtonIcons()
    {
        try
        {
            if (currentCoordinator != null)
            {
                val resId: Int
                val preferences = PreferenceManager.getDefaultSharedPreferences(this)
                resId = if (preferences.getBoolean(
                        IPreferenceCameraPropertyAccessor.SHOW_GRID_STATUS,
                        true
                    )
                ) {
                    // ボタンをGrid OFFアイコンにする
                    R.drawable.btn_ic_grid_off
                } else {
                    // ボタンをGrid ONアイコンにする
                    R.drawable.btn_ic_grid_on
                }
                setButtonDrawable(IShowInformation.BUTTON_1, resId)
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    /**
     * Intentを使ってWiFi設定画面を開く
     *
     */
    private fun launchWifiSettingScreen(): Boolean
    {
        Log.v(TAG, "launchWifiSettingScreen() : ACTION_WIFI_SETTINGS")
        try
        {
            // Wifi 設定画面を表示する
            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))

            return true
        }
        catch (_: Exception)
        {
            try
            {
                Log.v(TAG, "launchWifiSettingScreen() : ADD_NETWORK_SETTINGS")
                startActivity(Intent("com.google.android.clockwork.settings.connectivity.wifi.ADD_NETWORK_SETTINGS"))
                return true
            }
            catch (_: Exception)
            {
                Log.v(
                    TAG,
                    "android.content.ActivityNotFoundException... " + "com.google.android.clockwork.settings.connectivity.wifi.ADD_NETWORK_SETTINGS"
                )
                try {
                    // SONY Smart Watch 3で開く場合のIntent...
                    val intent =
                        Intent("com.google.android.clockwork.settings.connectivity.wifi.ADD_NETWORK_SETTINGS")
                    intent.setClassName(
                        "com.google.android.apps.wearable.settings",
                        "com.google.android.clockwork.settings.wifi.WifiSettingsActivity"
                    )
                    startActivity(intent)
                    return true
                } catch (_: Exception) {
                    try {
                        // Wifi 設定画面を表示する...普通のAndroidの場合
                        startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                        return true
                    } catch (ee: Exception) {
                        ee.printStackTrace()
                        try {
                            // LG G Watch Rで開く場合のIntent...
                            val intent = Intent("android.intent.action.MAIN")
                            intent.setClassName(
                                "com.google.android.apps.wearable.settings",
                                "com.google.android.clockwork.settings.MainSettingsActivity"
                            )
                            startActivity(intent)
                            return true
                        } catch (ex3: ActivityNotFoundException) {
                            ex3.printStackTrace()
                        }
                    }
                }
            }
        }
        return false
    }

    /**
     * Olympus Cameraクラスとのやりとりをするクラスを準備する
     * （カメラとの接続も、ここでスレッドを起こして開始する）
     */
    private fun setupCameraCoordinator()
    {
        try
        {
            preferences = PreferenceAccessWrapper(this)
            preferences.initialize()
            val connectionMethod = preferences.getString(
                IPreferenceCameraPropertyAccessor.CONNECTION_METHOD,
                IPreferenceCameraPropertyAccessor.CONNECTION_METHOD_DEFAULT_VALUE
            )
            if (liveView == null)
            {
                liveView = findViewById(R.id.liveview)
                liveView?.visibility = View.VISIBLE
            }
            if (liveView != null)
            {
                liveViewListener = CameraLiveViewListenerImpl(liveView!!)
            }
            if (glView == null)
            {
                glView = findViewById(R.id.glview)
            }
            if (glView != null)
            {
                if (gestureParser == null)
                {
                    gestureParser = GestureParser(applicationContext, glView!!)
                }
                enableGlView = preferences.getBoolean(IPreferenceCameraPropertyAccessor.THETA_GL_VIEW, false)
                if (enableGlView && connectionMethod.contains(IPreferenceCameraPropertyAccessor.CONNECTION_METHOD_THETA))
                {
                    // GL VIEW に切り替える
                    glView?.setImageProvider(liveViewListener!!)
                    glView?.visibility = View.VISIBLE
                    liveView?.visibility = View.GONE
                }
            }
            olyAirCoordinator = OlyCameraCoordinator(this, liveView, this, this)
            thetaCoordinator = ThetaCameraController(this, this, this)
            currentCoordinator =
                if (connectionMethod.contains(IPreferenceCameraPropertyAccessor.CONNECTION_METHOD_THETA)) thetaCoordinator else olyAirCoordinator
            currentCoordinator?.setLiveViewListener(liveViewListener!!)
            listener = CameraLiveViewOnTouchListener(
                this, currentCoordinator?.getFeatureDispatcher(
                    this, this,
                    currentCoordinator!!,
                    preferences,
                    liveView!!
                ), this
            )
            selectionDialog = FavoriteSettingSelectionDialog(
                this,
                currentCoordinator?.cameraPropertyLoadSaveOperations,
                this
            )
            connectToCamera()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * カメラと接続する
     *
     */
    private fun connectToCamera()
    {
        val thread = Thread { currentCoordinator?.connectionInterface?.connect() }
        try
        {
            thread.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    /**
     * カメラの電源をOFFいして、アプリを抜ける処理
     *
     */
    override fun exitApplication()
    {
        try {
            Log.v(TAG, "exitApplication()")

            // パワーマネージャを確認し、interactive modeではない場合は、ライブビューも止めず、カメラの電源も切らない
            if (powerManager?.isInteractive != true)
            {
                Log.v(TAG, "not interactive, keep live view.")
                return
            }

            // ライブビューを停止させる
            currentCoordinator?.stopLiveView()

            // ステータス監視を止める
            val watcher = currentCoordinator?.statusWatcher
            watcher?.stopStatusWatch()

            //  パラメータを確認し、カメラの電源を切る
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                    IPreferenceCameraPropertyAccessor.EXIT_APPLICATION_WITH_DISCONNECT,
                    true
                )
            ) {
                Log.v(TAG, "Shutdown camera...")

                // カメラの電源をOFFにする
                currentCoordinator?.connectionInterface?.disconnect(true)
            }
            //finish();
            //finishAndRemoveTask();
            //android.os.Process.killProcess(android.os.Process.myPid());
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 接続機能を確認する
     */
    override fun checkConnectionFeature(id: Int, btnId: Int): Boolean
    {
        var ret = false
        if (id == 0)
        {
            // Wifi 設定画面を開く
            ret = launchWifiSettingScreen()
        } else if (id == 1) {
            // 接続の変更を確認する
            changeConnectionMethod()
        }
        return ret
    }

    /**
     * 画面をタッチした場所を受信する
     *
     * @param posX  X座標位置 (0.0f - 1.0f)
     * @param posY  Y座標位置 (0.0f - 1.0f)
     * @return true / false
     */
    override fun touchedPosition(posX: Float, posY: Float): Boolean
    {
        Log.v(TAG, "touchedPosition ($posX, $posY)")
        return (liveView?.touchedPosition(posX, posY) ?: false)
    }

    /**
     * 接続状態を見る or 再接続する
     */
    override fun showConnectionStatus(): Boolean
    {
        try
        {
            if (listener?.isEnabledOperation == operation.ONLY_CONNECT && cameraDisconnectedHappened) {
                // カメラが切断されたとき、再接続を指示する
                connectToCamera()
                cameraDisconnectedHappened = false
                return true
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return false
    }

    /**
     *
     */
    override fun onStatusNotify(message: String)
    {
        setMessage(IShowInformation.AREA_C, Color.WHITE, message)
    }

    /**
     *
     */
    override fun onCameraConnected()
    {
        Log.v(TAG, "onCameraConnected()")
        try
        {
            // ライブビューの開始 ＆ タッチ/ボタンの操作を可能にする
            currentCoordinator?.connectFinished()
            currentCoordinator?.startLiveView()
            currentCoordinator?.setRecViewMode(false)
            listener!!.setEnableOperation(operation.ENABLE)
            setMessage(IShowInformation.AREA_C, Color.WHITE, "")
            currentCoordinator?.updateStatusAll()
            val watcher = currentCoordinator?.statusWatcher
            watcher?.startStatusWatch()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    /**
     * カメラとの接続が切れたとき...何もしない
     *
     */
    override fun onCameraDisconnected()
    {
        Log.v(TAG, "onCameraDisconnected()")
        try
        {
            setMessage(
                IShowInformation.AREA_C,
                Color.YELLOW,
                getString(R.string.camera_disconnected)
            )
            listener?.setEnableOperation(operation.ONLY_CONNECT)
            cameraDisconnectedHappened = true
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    /**
     * カメラと接続失敗
     */
    override fun onCameraConnectError(message: String)
    {
        Log.v(TAG, "onCameraOccursException()")
        try
        {
            setMessage(IShowInformation.AREA_C, Color.YELLOW, message)
            listener?.setEnableOperation(operation.ONLY_CONNECT)
            cameraDisconnectedHappened = true
        }
        catch (ee: Exception)
        {
            ee.printStackTrace()
        }
    }

    /**
     * カメラに例外発生
     */
    override fun onCameraOccursException(message: String, e: Exception)
    {
        Log.v(TAG, "onCameraOccursException()")
        try
        {
            setMessage(IShowInformation.AREA_C, Color.YELLOW, message)
            listener?.setEnableOperation(operation.ONLY_CONNECT)
            cameraDisconnectedHappened = true
        }
        catch (ee: Exception)
        {
            e.printStackTrace()
            ee.printStackTrace()
        }
    }

    /**
     * メッセージの表示
     *
     * @param area    表示エリア (AREA_1 ～ AREA_6, AREA_C)
     * @param color　 表示色
     * @param message 表示するメッセージ
     */
    override fun setMessage(area: Int, color: Int, message: String)
    {
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
            IShowInformation.AREA_NONE -> {}
            else -> {}
        }

        if (messageDrawer != null)
        {
            if (area == IShowInformation.AREA_C)
            {
                messageDrawer?.setMessageToShow(
                    IMessageDrawer.MessageArea.CENTER,
                    color,
                    IMessageDrawer.SIZE_LARGE,
                    message
                )
                return
            }
            if (area == IShowInformation.AREA_5)
            {
                messageDrawer?.setMessageToShow(
                    IMessageDrawer.MessageArea.UPLEFT,
                    color,
                    IMessageDrawer.SIZE_STD,
                    message
                )
                return
            }
            if (area == IShowInformation.AREA_6)
            {
                messageDrawer?.setMessageToShow(
                    IMessageDrawer.MessageArea.LOWLEFT,
                    color,
                    IMessageDrawer.SIZE_STD,
                    message
                )
                return
            }
            if (area == IShowInformation.AREA_7)
            {
                messageDrawer?.setMessageToShow(
                    IMessageDrawer.MessageArea.UPRIGHT,
                    color,
                    IMessageDrawer.SIZE_STD,
                    message
                )
                return
            }
            if (area == IShowInformation.AREA_8)
            {
                messageDrawer?.setMessageToShow(
                    IMessageDrawer.MessageArea.LOWRIGHT,
                    color,
                    IMessageDrawer.SIZE_STD,
                    message
                )
                return
            }
            if (area == IShowInformation.AREA_9)
            {
                messageDrawer?.setMessageToShow(
                    IMessageDrawer.MessageArea.UPCENTER,
                    color,
                    IMessageDrawer.SIZE_STD,
                    message
                )
                return
            }
            if (area == IShowInformation.AREA_A)
            {
                messageDrawer?.setMessageToShow(
                    IMessageDrawer.MessageArea.LOWCENTER,
                    color,
                    IMessageDrawer.SIZE_STD,
                    message
                )
                return
            }
            if (area == IShowInformation.AREA_B)
            {
                messageDrawer?.setMessageToShow(
                    IMessageDrawer.MessageArea.CENTERLEFT,
                    color,
                    IMessageDrawer.SIZE_STD,
                    message
                )
                return
            }
            if (area == IShowInformation.AREA_D)
            {
                messageDrawer?.setMessageToShow(
                    IMessageDrawer.MessageArea.CENTERRIGHT,
                    color,
                    IMessageDrawer.SIZE_STD,
                    message
                )
                return
            }
            if (id == 0)
            {
                // 描画エリアが不定の場合...
                return
            }
        }
        val areaId = id
        runOnUiThread {
            val textArea = findViewById<TextView>(areaId)
            if (textArea != null)
            {
                textArea.setTextColor(color)
                textArea.text = message
                textArea.invalidate()
            }
        }
    }

    /**
     * ボタンの表示イメージを変更する
     *
     * @param button  ボタンの場所
     * @param labelId 変更する内容
     */
    override fun setButtonDrawable(button: Int, labelId: Int)
    {
        val id = when (button)
        {
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
            try
            {
                val btn = findViewById<ImageButton>(id)
                val drawTarget = ContextCompat.getDrawable(applicationContext, labelId)
                if (btn != null)
                {
                    btn.setImageDrawable(drawTarget)
                    btn.invalidate()
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
        }
    }

    /**
     *
     * @return true GPS搭載, false GPS非搭載
     */
    private fun hasGps(): Boolean
    {
        return (packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS))
    }

    /**
     * タッチイベントをフックする
     *
     *
     */
    override fun dispatchTouchEvent(event: MotionEvent): Boolean
    {
        //Log.v(TAG, " dispatchTouchEvent() ");
        if (enableGlView)
        {
            //Log.v(TAG, " onTouch() ");
            gestureParser?.onTouch(event)
        }
        return (super.dispatchTouchEvent(event))
    }

    override fun vibrate(vibratePattern: Int)
    {
        try
        {
            // バイブレータをつかまえる
            val vibrator  = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            {
                val vibratorManager =  this.getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            }
            else
            {
                @Suppress("DEPRECATION")
                getSystemService(VIBRATOR_SERVICE) as Vibrator
            }
            if (!vibrator.hasVibrator())
            {
                Log.v(TAG, " not have Vibrator...")
                return
            }
            @Suppress("DEPRECATION") val thread = Thread {
                try
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    {
                        vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
                    }
                    else
                    {
                        when (vibratePattern)
                        {
                            IShowInformation.VIBRATE_PATTERN_SIMPLE_SHORT -> vibrator.vibrate(30)
                            IShowInformation.VIBRATE_PATTERN_SIMPLE_MIDDLE -> vibrator.vibrate(80)
                            IShowInformation.VIBRATE_PATTERN_SIMPLE_LONG ->  vibrator.vibrate(150)
                            IShowInformation.VIBRATE_PATTERN_SIMPLE_LONGLONG ->  vibrator.vibrate(300)
                            IShowInformation.VIBRATE_PATTERN_SHORT_DOUBLE -> {
                                val pattern = longArrayOf(10, 35, 30, 35, 0)
                                vibrator.vibrate(pattern, -1)
                            }
                            else -> { }
                        }
                    }
                }
                catch (e : Exception)
                {
                    e.printStackTrace()
                }
            }
            thread.start()
        }
        catch (e: java.lang.Exception)
        {
            e.printStackTrace()
        }
    }

    override fun setEnabledOperation(operation: operation)
    {
        listener?.setEnableOperation(operation)
    }

    /**
     * 「お気に入り設定」表示画面を開く
     *
     */
    override fun showFavoriteSettingsDialog()
    {
        if ((liveView != null)&&(listener != null)&&(listener!!.isEnabledOperation != operation.ONLY_CONNECT))
        {
            listener?.setEnableOperation(operation.ENABLE_ONLY_TOUCHED_POSITION)
            liveView?.showDialog(selectionDialog)
        }
    }

    override fun showToast(rscId: Int, appendMessage: String, duration: Int)
    {
        try
        {
            runOnUiThread {
                try
                {
                    val message = if (rscId != 0) getString(rscId) + appendMessage else appendMessage
                    Toast.makeText(applicationContext, message, duration).show()
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun invalidate()
    {
        try
        {
            runOnUiThread { liveView?.invalidate() }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    /**
     * 「お気に入り設定」表示画面を閉じる
     *
     */
    override fun dialogDismissed(isExecuted: Boolean)
    {
        try
        {
            if ((liveView != null) && (listener != null))
            {
                liveView?.hideDialog()
                listener?.setEnableOperation(operation.ENABLE)
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun updateConnectionMethodMessage()
    {
        try
        {
            val connectionMethod = preferences.getString(
                IPreferenceCameraPropertyAccessor.CONNECTION_METHOD,
                IPreferenceCameraPropertyAccessor.CONNECTION_METHOD_DEFAULT_VALUE
            )
            val methodId = if (connectionMethod.contains(IPreferenceCameraPropertyAccessor.CONNECTION_METHOD_THETA)) R.string.connection_method_theta else R.string.connection_method_opc
            setMessage(IShowInformation.AREA_7, Color.MAGENTA, getString(methodId))
            liveView?.setupInitialBackgroundImage(this)
            liveView?.visibility = View.VISIBLE
            liveView?.invalidate()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun updateConnectionMethod(parameter: String, method: ICameraController?)
    {
        try
        {
            currentCoordinator = method
            preferences.putString(IPreferenceCameraPropertyAccessor.CONNECTION_METHOD, parameter)
            vibrate(IShowInformation.VIBRATE_PATTERN_SHORT_DOUBLE)
            enableGlView = preferences.getBoolean(IPreferenceCameraPropertyAccessor.THETA_GL_VIEW, false)
            if ((enableGlView)&&(parameter.contains(IPreferenceCameraPropertyAccessor.CONNECTION_METHOD_THETA)))
            {
                if (glView == null)
                {
                    // GL VIEW に切り替える
                    glView = findViewById(R.id.glview)
                }
                if (glView != null)
                {
                    // GL VIEW に切り替える
                    gestureParser = GestureParser(applicationContext, glView!!)
                    glView?.setImageProvider(liveViewListener!!)
                    glView?.visibility = View.VISIBLE
                    liveView?.visibility = View.GONE
                }
            }
            else
            {
                if (liveView == null)
                {
                    liveView = findViewById(R.id.liveview)
                }
                if (liveView != null)
                {
                    glView?.visibility = View.GONE
                    liveView?.visibility = View.VISIBLE
                }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    /**
     * 接続方式を変更するか確認する (OPC ⇔ THETA)
     *
     */
    private fun changeConnectionMethod() {
        val activity: AppCompatActivity = this
        runOnUiThread {
            try {
                var titleId = R.string.change_title_from_opc_to_theta
                var messageId = R.string.change_message_from_opc_to_theta
                var method = false
                val connectionMethod = preferences.getString(
                    IPreferenceCameraPropertyAccessor.CONNECTION_METHOD,
                    IPreferenceCameraPropertyAccessor.CONNECTION_METHOD_DEFAULT_VALUE
                )
                if (connectionMethod.contains(IPreferenceCameraPropertyAccessor.CONNECTION_METHOD_THETA)) {
                    titleId = R.string.change_title_from_theta_to_opc
                    messageId = R.string.change_message_from_theta_to_opc
                    method = true
                }
                val isTheta = method
                val confirmation = ConfirmationDialog(activity)
                confirmation.show(titleId, messageId) {
                    Log.v(TAG, " --- CONFIRMED! --- (theta:$isTheta)")
                    if (isTheta) {
                        // 接続方式を OPC に切り替える
                        updateConnectionMethod(
                            IPreferenceCameraPropertyAccessor.CONNECTION_METHOD_OPC,
                            olyAirCoordinator
                        )
                    } else {
                        // 接続方式を Theta に切り替える
                        updateConnectionMethod(
                            IPreferenceCameraPropertyAccessor.CONNECTION_METHOD_THETA,
                            thetaCoordinator
                        )
                    }
                    updateConnectionMethodMessage()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onConnectedToWifi()
    {
        Log.v(TAG, "onConnectedToWifi()")
    }

    override fun onNetworkAvailable()
    {
        Log.v(TAG, "onNetworkAvailable()")
    }

    override fun onNetworkLost()
    {
        Log.v(TAG, "onNetworkLost()")
    }

    override fun onNetworkConnectionTimeout()
    {
        Log.v(TAG, "onNetworkConnectionTimeout()")
    }

    override fun onError(message: String?)
    {
        Log.v(TAG, "onNetworkConnectionTimeout() $message")
    }

    companion object
    {
        private val TAG = MainActivity::class.java.simpleName
        //const val REQUEST_NEED_PERMISSIONS = 1010
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
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
        } else {
            arrayOf(
                Manifest.permission.VIBRATE,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
            )
        }
    }
}
