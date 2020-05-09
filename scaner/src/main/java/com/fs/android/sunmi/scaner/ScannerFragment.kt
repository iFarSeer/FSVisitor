package com.fs.android.sunmi.scaner

import android.hardware.Camera
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.sunmi.scan.Config
import com.sunmi.scan.Image
import com.sunmi.scan.ImageScanner
import com.sunmi.scan.Symbol
import kotlin.math.abs


/**
 *
 */
class ScannerFragment : Fragment(), SurfaceHolder.Callback {

    private var mCamera: Camera? = null
    private lateinit var surfaceView: SurfaceView
    private lateinit var textView: TextView

    private lateinit var scanner: ImageScanner

    private var autoFocusHandler: Handler? = null
    private var soundUtils: SoundUtils? = null
    private var useAutoFocus = false //T1/T2 mini定焦摄像头没有对焦功能

    private var decodeCount = 0
    //预览分辨率设置，T1/T2 mini设置640x480，其他手持机可选取640x480,800x480,1280x720
    private var previewSizeWidth = 480
    private var previewSizeHeight = 640
    private lateinit var imageData : Image
    private var sb = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        soundUtils = SoundUtils(context, SoundUtils.RING_SOUND)
        soundUtils?.putSound(0, R.raw.beep)
        return inflater.inflate(R.layout.fragment_scanner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
    }

    override fun onDestroy() {
        super.onDestroy()
        soundUtils?.release()
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        if (surfaceView.holder.surface == null) {
            return
        }
        try {
            mCamera!!.stopPreview()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        try {
            val parameters = mCamera!!.parameters
            mCamera!!.setDisplayOrientation(90)//手持机使用，竖屏显示,T1/T2 mini需要屏蔽掉
            computePreviewSize(mCamera!!)
            Log.i("previewSize", "previewSizeWidth = $previewSizeWidth")
            Log.i("previewSize", "previewSizeHeight = $previewSizeHeight")
            imageData = Image(previewSizeWidth, previewSizeHeight, "Y800")
            parameters.setPreviewSize(previewSizeWidth, previewSizeHeight) //设置预览分辨率
            if (useAutoFocus) parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
            mCamera!!.parameters = parameters
            mCamera!!.setPreviewDisplay(surfaceView.holder)
            mCamera!!.setPreviewCallback { data: ByteArray?, camera: Camera? ->
                imageData.data = data
                val startTimeMillis = System.currentTimeMillis()
                //解码，返回值为0代表失败，>0表示成功
                //解码，返回值为0代表失败，>0表示成功
                val nsyms = scanner.scanImage(imageData)
                val endTimeMillis = System.currentTimeMillis()
                val cost_time = endTimeMillis - startTimeMillis
                sb.append("计数: " + decodeCount++)
                sb.append("\n耗时: $cost_time ms\n")

                if (nsyms != 0) {
                    playBeepSoundAndVibrate() //解码成功播放提示音
                    val syms = scanner.results //获取解码结果
                    //如果允许识读多个条码，则解码结果可能不止一个
                    for (sym in syms) {
                        sb.append("码制: " + sym.symbolName + "\n")
                        sb.append("容量: " + sym.dataLength + "\n")
                        sb.append("内容: " + sym.result)
                        activity?.apply {
                            Log.d("ScannerViewModel", "通知数据变化")
                            ViewModelProviders.of(this).get(ScannerViewModel::class.java).scanResult.value = sym.result
                        }
                        break
                    }
                }
                textView.visibility = if (BuildConfig.DEBUG) {
                    textView.text = sb.toString()
                    View.VISIBLE
                } else View.GONE
                sb.delete(0, sb.length)
            }
            mCamera!!.startPreview()
        } catch (e: java.lang.Exception) {
            Log.d("DBG", "Error starting camera preview: " + e.message)
        }
    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {
        mCamera?.setPreviewCallback(null)
        mCamera?.release()
        mCamera = null
    }

    override fun surfaceCreated(p0: SurfaceHolder?) {
        mCamera = try {
            Camera.open()
        } catch (e: Exception) {
            null
        }
    }

    private fun initViews(view: View) {
        surfaceView = view.findViewById(R.id.surfaceView)
        textView = view.findViewById(R.id.textView)
        surfaceView.holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        surfaceView.holder.addCallback(this)
        scanner = ImageScanner() //创建扫描器
        scanner.setConfig(Symbol.NONE, Config.ENABLE_MULTILESYMS, 0) //是否开启同一幅图一次解多个条码,0表示只解一个，1为多个,默认0：禁止
        scanner.setConfig(Symbol.QRCODE, Config.ENABLE, 1) //允许识读QR码，默认1:允许
        scanner.setConfig(Symbol.PDF417, Config.ENABLE, 1) //允许识读PDF417码，默认0：禁止
        scanner.setConfig(Symbol.DataMatrix, Config.ENABLE, 1) //允许识读DataMatrix码，默认0：禁止
        scanner.setConfig(Symbol.AZTEC, Config.ENABLE, 1) //允许识读AZTEC码，默认0：禁止
        if (useAutoFocus) autoFocusHandler = Handler()
        decodeCount = 0
    }

    private fun computePreviewSize(camera: Camera) {
        try {
            activity?.application
            var diffs = Int.MAX_VALUE
            val previewSizes: List<Camera.Size> = camera.parameters.supportedPreviewSizes ?: mutableListOf()
            val outMetrics = DisplayMetrics()
            activity!!.windowManager.defaultDisplay.getRealMetrics(outMetrics)
            val widthPixel = outMetrics.widthPixels
            val heightPixel = outMetrics.heightPixels
            Log.i("previewSize",  "widthPixel = $widthPixel, heightPixel = $heightPixel")
            for (previewSize in previewSizes) {
                Log.i("previewSize", previewSize.width.toString() + " x " + previewSize.height)
                var mCameraPreviewWidth = previewSize.height
                var mCameraPreviewHeight = previewSize.width
                var flag = false
                if (previewSize.height > previewSize.width
                        && heightPixel > widthPixel) {
                    mCameraPreviewWidth = previewSize.width
                    mCameraPreviewHeight = previewSize.height
                    flag = true
                }
                val newDiffs: Int = abs(mCameraPreviewWidth - widthPixel) + abs(mCameraPreviewHeight - heightPixel)
                Log.v("previewSize", "newDiffs = $newDiffs")
                if (newDiffs == 0) {
                    previewSizeWidth = if(flag) mCameraPreviewWidth else mCameraPreviewHeight
                    previewSizeHeight = if(flag) mCameraPreviewHeight else mCameraPreviewWidth
                    break
                }
                if (diffs > newDiffs) {
                    previewSizeWidth = if(flag) mCameraPreviewWidth else mCameraPreviewHeight
                    previewSizeHeight = if(flag) mCameraPreviewHeight else mCameraPreviewWidth
                    diffs = newDiffs
                }
            }
        } catch (e: Exception) {
        }
    }

    private fun playBeepSoundAndVibrate() {
        soundUtils?.playSound(0, SoundUtils.SINGLE_PLAY)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                ScannerFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
