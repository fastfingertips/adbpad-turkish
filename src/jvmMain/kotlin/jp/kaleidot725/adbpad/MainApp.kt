package jp.kaleidot725.adbpad

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.malinskiy.adam.request.device.Device
import jp.kaleidot725.adbpad.model.data.Dialog
import jp.kaleidot725.adbpad.model.data.Menu
import jp.kaleidot725.adbpad.view.resource.AppTheme
import jp.kaleidot725.adbpad.view.screen.CommandScreen
import jp.kaleidot725.adbpad.view.screen.InputTextScreen
import jp.kaleidot725.adbpad.view.screen.MenuScreen
import jp.kaleidot725.adbpad.view.screen.ScreenLayout
import jp.kaleidot725.adbpad.view.screen.ScreenshotScreen
import jp.kaleidot725.adbpad.view.screen.command.CommandStateHolder

@Composable
fun MainApp(
    state: MainState,
    onSelectDevice: (Device) -> Unit,
    onSelectMenu: (Menu) -> Unit,
    onChangeInputText: (String) -> Unit,
    onSendInputText: (String) -> Unit,
    onSaveInputText: (String) -> Unit,
    onDeleteInputText: (String) -> Unit,
    onTakeScreenshot: () -> Unit,
    onTakeThemeScreenshot: () -> Unit,
    onShowSettingDialog: () -> Unit,
    onCloseDialog: () -> Unit
) {
    AppTheme {
        ScreenLayout(
            leftPane = {
                MenuScreen(
                    devices = state.devices,
                    selectedDevice = state.selectedDevice,
                    onSelectDevice = onSelectDevice,
                    menus = state.menus,
                    selectedMenu = state.selectedMenu,
                    onSelectMenu = onSelectMenu,
                    onShowSetting = onShowSettingDialog,
                    modifier = Modifier
                        .width(250.dp)
                        .fillMaxHeight()
                        .padding(horizontal = 12.dp, vertical = 16.dp)
                )
            },
            rightPane = {
                when (state.selectedMenu) {
                    Menu.Command -> {
                        val stateHolder by remember { mutableStateOf(CommandStateHolder()) }
                        val state by stateHolder.state.collectAsState()
                        DisposableEffect(stateHolder) {
                            stateHolder.setup()
                            onDispose { stateHolder.dispose() }
                        }
                        CommandScreen(
                            commands = state.commands,
                            onExecute = { stateHolder.executeCommand(it) }
                        )
                    }

                    Menu.InputText -> {
                        InputTextScreen(
                            inputText = state.userInputText,
                            onTextChange = onChangeInputText,
                            inputTexts = state.inputTexts,
                            onSend = onSendInputText,
                            canSend = state.canSendUserInputText,
                            onSave = onSaveInputText,
                            canSave = state.canSaveUserInputText,
                            onDelete = onDeleteInputText
                        )
                    }

                    Menu.Screenshot -> {
                        ScreenshotScreen(
                            image1 = state.imageFile1,
                            image2 = state.imageFile2,
                            onTakeScreenShot = onTakeScreenshot,
                            onTakeThemeScreenshot = onTakeThemeScreenshot
                        )
                    }

                    null -> Unit
                }
            },
            notificationArea = {
                Box(Modifier.fillMaxWidth().height(25.dp).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text("Sample text", style = MaterialTheme.typography.caption)
                }
            },
            dialog = {
                when (state.dialog) {
                    Dialog.Setting -> {
                        Box(Modifier.background(Color.DarkGray.copy(alpha = 0.5f))) {
                            Card(Modifier.fillMaxSize().padding(32.dp)) {
                                Button(onClick = { onCloseDialog() }) {
                                    Text("Close")
                                }
                            }
                        }
                    }

                    null -> Unit
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
