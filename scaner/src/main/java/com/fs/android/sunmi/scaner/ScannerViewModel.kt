package com.fs.android.sunmi.scaner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class ScannerViewModel(application: Application) : AndroidViewModel(application) {

    var scanResult = MutableLiveData<String>()
}