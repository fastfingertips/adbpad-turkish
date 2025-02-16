package jp.kaleidot725.adbpad.ui.screen.screenshot

import jp.kaleidot725.adbpad.domain.model.command.ScreenshotCommand
import jp.kaleidot725.adbpad.domain.model.device.Device
import jp.kaleidot725.adbpad.domain.model.os.OSContext
import jp.kaleidot725.adbpad.domain.model.screenshot.Screenshot
import jp.kaleidot725.adbpad.domain.repository.ScreenshotCommandRepository
import jp.kaleidot725.adbpad.domain.usecase.device.GetSelectedDeviceFlowUseCase
import jp.kaleidot725.adbpad.domain.usecase.screenshot.GetScreenshotCommandUseCase
import jp.kaleidot725.adbpad.domain.usecase.screenshot.TakeScreenshotUseCase
import jp.kaleidot725.adbpad.domain.utils.ClipBoardUtils
import jp.kaleidot725.adbpad.ui.common.ChildStateHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.awt.Desktop
import java.io.File

class ScreenshotStateHolder(
    private val takeScreenshotUseCase: TakeScreenshotUseCase,
    private val getScreenshotCommandUseCase: GetScreenshotCommandUseCase,
    private val getSelectedDeviceFlowUseCase: GetSelectedDeviceFlowUseCase,
    private val screenshotCommandRepository: ScreenshotCommandRepository,
) : ChildStateHolder<ScreenshotState> {
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main + Dispatchers.IO)
    private val commands: MutableStateFlow<List<ScreenshotCommand>> = MutableStateFlow(emptyList())
    private val preview: MutableStateFlow<Screenshot> = MutableStateFlow(Screenshot(null))
    private val previews: MutableStateFlow<List<Screenshot>> = MutableStateFlow(emptyList())
    private val isCapturing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val selectedDevice: StateFlow<Device?> =
        getSelectedDeviceFlowUseCase()
            .stateIn(coroutineScope, SharingStarted.WhileSubscribed(), null)

    override val state: StateFlow<ScreenshotState> =
        combine(
            preview,
            previews,
            commands,
            selectedDevice,
            isCapturing,
        ) { preview, previews, commands, selectedDevice, isCapturing ->
            ScreenshotState(preview, previews, commands, selectedDevice, isCapturing)
        }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(), ScreenshotState())

    override fun setup() {
        coroutineScope.launch {
            commands.value = getScreenshotCommandUseCase()
            initPreviews()
        }
    }

    override fun refresh() {
        coroutineScope.launch {
            commands.value = getScreenshotCommandUseCase()
            initPreviews()
        }
    }

    override fun dispose() {
        coroutineScope.cancel()
    }

    fun takeScreenShot(command: ScreenshotCommand) {
        val selectedDevice = state.value.selectedDevice ?: return
        coroutineScope.launch {
            takeScreenshotUseCase(
                device = selectedDevice,
                command = command,
                onStart = {
                    commands.value = getScreenshotCommandUseCase()
                    preview.value = Screenshot.EMPTY
                    isCapturing.value = true
                },
                onFailed = {
                    commands.value = getScreenshotCommandUseCase()
                    preview.value = Screenshot.EMPTY
                    isCapturing.value = false
                },
                onComplete = {
                    commands.value = getScreenshotCommandUseCase()
                    preview.value = it
                    previews.value = screenshotCommandRepository.getScreenshots()
                    isCapturing.value = false
                },
            )
        }
    }

    fun openDirectory() {
        coroutineScope.launch {
            val file = File(OSContext.resolveOSContext().screenshotDirectory)
            Desktop.getDesktop().open(file)
        }
    }

    fun copyScreenShotToClipboard() {
        coroutineScope.launch {
            val file = preview.value.file ?: return@launch
            ClipBoardUtils.copyFile(file)
        }
    }

    fun deleteScreenShotToClipboard() {
        coroutineScope.launch {
            screenshotCommandRepository.delete(preview.value)
            initPreviews()
        }
    }

    fun selectScreenshot(screenshot: Screenshot) {
        preview.value = screenshot
    }

    fun nextScreenshot() {
        val nextIndex = previews.value.indexOf(preview.value) + 1
        val nextPreview = previews.value.getOrNull(nextIndex) ?: return
        preview.value = nextPreview
    }

    fun previousScreenshot() {
        val previousIndex = previews.value.indexOf(preview.value) - 1
        val previousPreview = previews.value.getOrNull(previousIndex) ?: return
        preview.value = previousPreview
    }

    private suspend fun initPreviews() {
        previews.value = screenshotCommandRepository.getScreenshots()
        preview.value = previews.value.firstOrNull() ?: Screenshot(null)
    }
}
