package io.github.devriesl.raptormark.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.devriesl.raptormark.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BenchmarkViewModel @Inject constructor(
    private val settingSharedPrefs: SettingSharedPrefs,
    private val testRecordRepo: TestRecordRepo
) : ViewModel() {
    @Volatile
    private var forceStop = false;
    private val mutableBenchmarkState = MutableStateFlow(BenchmarkState())

    val testItems: List<BenchmarkTest> =
        TestCases.values().map { BenchmarkTest(it, settingSharedPrefs) }

    val benchmarkState: StateFlow<BenchmarkState>
        get() = mutableBenchmarkState

    fun onTestStart() {
        mutableBenchmarkState.value = mutableBenchmarkState.value.copy(running = true)
        val testRecord = TestRecord()
        NativeHandler.postNativeThread {
            testItems.forEach {
                try {
                    if (forceStop) return@forEach

                    testRecord.setResult(it.testCase, it.runTest())
                } catch (ex: Exception) {
                    Log.e(it.testCase.name, "Error running test", ex)
                    return@forEach
                }
            }
            viewModelScope.launch(Dispatchers.IO) {
                testRecordRepo.insertTestRecord(testRecord)
            }
            forceStop = false
            mutableBenchmarkState.value = mutableBenchmarkState.value.copy(running = false)
        }
    }

    fun onTestStop() {
        forceStop = true
    }
}

data class BenchmarkState(
    val running: Boolean = false
)
