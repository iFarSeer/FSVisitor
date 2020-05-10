package com.fs.android.sunmi.printer

import android.content.Context
import android.graphics.Bitmap
import android.os.RemoteException
import com.sunmi.peripheral.printer.*

object FSPrinter {
    const val CODE_FAILURE = 0
    const val CODE_SUCCESS = 1

    const val FoundSunmiPrinter = 0x00000000
    const val NoSunmiPrinter = 0x00000001
    const val CheckSunmiPrinter = 0x00000002
    const val LostSunmiPrinter = 0x00000003

    var sunmiPrinter: Int = CheckSunmiPrinter
    private var printerService: SunmiPrinterService? = null

    fun connect(context: Context, callback: (code: Int) -> Unit) {
        try {
            val ret = InnerPrinterManager.getInstance().bindService(context, object : InnerPrinterCallback() {
                override fun onConnected(service: SunmiPrinterService?) {
                    printerService = service
                    if (checkSunmiPrinterService(service)) {
                        sunmiPrinter = FoundSunmiPrinter
                        initPrinter()
                        callback(CODE_SUCCESS)
                    } else {
                        sunmiPrinter = NoSunmiPrinter
                        callback(CODE_FAILURE)
                    }
                }

                override fun onDisconnected() {
                    printerService = null
                    sunmiPrinter = LostSunmiPrinter
                    callback(CODE_FAILURE)
                }
            })
            if (!ret) {
                sunmiPrinter = NoSunmiPrinter
            }
        } catch (e: InnerPrinterException) {
            sunmiPrinter = NoSunmiPrinter
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    fun disconnect(context: Context, callback: (code: Int) -> Unit) {
        try {
            InnerPrinterManager.getInstance().unBindService(context, object : InnerPrinterCallback() {
                override fun onConnected(service: SunmiPrinterService?) {
                    printerService = service
                    if (checkSunmiPrinterService(service)) {
                        sunmiPrinter = FoundSunmiPrinter
                        callback(CODE_SUCCESS)
                    } else {
                        sunmiPrinter = NoSunmiPrinter
                        callback(CODE_FAILURE)
                    }
                }

                override fun onDisconnected() {
                    printerService = null
                    sunmiPrinter = LostSunmiPrinter
                    callback(CODE_FAILURE)
                }
            })
            printerService = null
            sunmiPrinter = LostSunmiPrinter
        } catch (e: InnerPrinterException) {
            sunmiPrinter = NoSunmiPrinter
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    private fun initPrinter() {
        try {
            printerService?.printerInit(null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * print text
     * setPrinterStyle Api require V4.2.22 or later, So use esc cmd instead when not supported
     * More settings reference documentation [WoyouConsts]
     */
    fun printText(content: String, size: Float = 24F, isBold: Boolean = false, isUnderLine: Boolean = false) {
        try {
            try {
                printerService?.setPrinterStyle(WoyouConsts.ENABLE_BOLD, if (isBold) WoyouConsts.ENABLE else WoyouConsts.DISABLE)
            } catch (e: RemoteException) {
                if (isBold) {
                    printerService?.sendRAWData(ESCUtil.boldOn(), null)
                } else {
                    printerService?.sendRAWData(ESCUtil.boldOff(), null)
                }
            }
            try {
                printerService?.setPrinterStyle(WoyouConsts.ENABLE_UNDERLINE, if (isUnderLine) WoyouConsts.ENABLE else WoyouConsts.DISABLE)
            } catch (e: RemoteException) {
                if (isUnderLine) {
                    printerService?.sendRAWData(ESCUtil.underlineWithOneDotWidthOn(), null)
                } else {
                    printerService?.sendRAWData(ESCUtil.underlineOff(), null)
                }
            }
            printerService?.printTextWithFont(content, null, size, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * Print a row of a table
     */
    fun printTable(txts: Array<String?>?, width: IntArray?, align: IntArray?) {
        try {
            printerService?.printColumnsString(txts, width, align, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * print Bar Code
     */
    fun printBarCode(data: String, symbology: Int, height: Int, width: Int, textposition: Int) {
        try {
            printerService?.printBarCode(data, symbology, height, width, textposition, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * print Qr Code
     */
    fun printQr(data: String, modulesize: Int, errorlevel: Int) {
        try {
            printerService?.printQRCode(data, modulesize, errorlevel, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * Print pictures and text in the specified orde
     * After the picture is printed,
     * the line feed output needs to be called,
     * otherwise it will be saved in the cache
     * In this example, the image will be printed because the print text content is added
     */
    fun printBitmap(bitmap: Bitmap, orientation: Int) {
        try {
            if (orientation == 0) {
                printerService?.printBitmap(bitmap, null)
//                printerService?.printText("横向排列\n", null)
//                printerService?.printBitmap(bitmap, null)
//                printerService?.printText("横向排列\n", null)
            } else {
                val tempBitmap = BitmapUtil.zoomImg(bitmap, 380)
                printerService?.printBitmap(tempBitmap, null)
//                printerService?.printText("\n纵向排列\n", null)
//                printerService?.printBitmap(bitmap, null)
//                printerService?.printText("\n纵向排列\n", null)
            }
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * paper feed three lines
     * Not disabled when line spacing is set to 0
     */
    fun print3Line() {
        try {
            printerService?.lineWrap(3, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * Set printer alignment
     */
    fun setAlign(align: Int) {
        try {
            printerService?.setAlignment(align, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * Due to the distance between the paper hatch and the print head,
     * the paper needs to be fed out automatically
     * But if the Api does not support it, it will be replaced by printing three lines
     */
    fun feedPaper() {
        try {
            printerService?.autoOutPaper(null)
        } catch (e: RemoteException) {
            print3Line()
        }
    }

    fun cutPaper() {
        try {
            printerService?.cutPaper(null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    private fun checkSunmiPrinterService(service: SunmiPrinterService?): Boolean {
        return try {
            InnerPrinterManager.getInstance().hasPrinter(service)
        } catch (e: InnerPrinterException) {
            e.printStackTrace()
            false
        }
    }

}