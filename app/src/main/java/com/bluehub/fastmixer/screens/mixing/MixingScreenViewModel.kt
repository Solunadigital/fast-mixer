package com.bluehub.fastmixer.screens.mixing

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bluehub.fastmixer.MixerApplication
import com.bluehub.fastmixer.common.permissions.PermissionViewModel
import com.bluehub.fastmixer.common.utils.PermissionManager
import com.bluehub.fastmixer.common.utils.ScreenConstants
import timber.log.Timber
import java.io.File
import java.util.*
import javax.inject.Inject

class MixingScreenViewModel(override val context: Context, mixerApplication: MixerApplication, override val tag: String): PermissionViewModel(context, mixerApplication, tag) {
    override var TAG: String = javaClass.simpleName

    var audioFiles: MutableList<AudioFile> = mutableListOf()
    val audioFilesLiveData = MutableLiveData<MutableList<AudioFile>>(mutableListOf())

    @Inject
    override lateinit var permissionManager: PermissionManager

    @Inject
    lateinit var mixingRepository: MixingRepository

    private val _eventRecord = MutableLiveData<Boolean>()
    val eventRecord: LiveData<Boolean>
        get() = _eventRecord

    private val _eventRead = MutableLiveData<Boolean>()
    val eventRead: LiveData<Boolean>
        get() = _eventRead

    init {
        Timber.d("Creating mixing viewmodel")
        getViewModelComponent().inject(this)
        mixingRepository.createMixingEngine()
    }

    fun onRecord() {
        _eventRecord.value = true
    }

    fun onReadFromDisk() {
        if(!checkReadFilePermission()) {
            setRequestReadFilePermission(ScreenConstants.READ_FROM_FILE)
            return
        }
    }

    fun onSaveToDisk() {
        if(!checkWriteFilePermission()) {
            setRequestWriteFilePermission(ScreenConstants.WRITE_TO_FILE)
            return
        }
    }

    fun onRecordNavigated() {
        _eventRecord.value = false
    }

    fun addRecordedFilePath(filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            audioFiles.add(AudioFile(UUID.randomUUID().toString(), filePath, AudioFileType.RECORDED))
            audioFilesLiveData.value = audioFiles
        }
    }

    fun readSamples(filePath: String):  Array<Float> = mixingRepository.readSamples(filePath)


    fun getTotalSamples(filePath: String): Int = mixingRepository.getTotalSamples(filePath)

    override fun onCleared() {
        super.onCleared()
        Timber.d("Mixing engine getting deleted")
        mixingRepository.deleteMixingEngine()
    }
}