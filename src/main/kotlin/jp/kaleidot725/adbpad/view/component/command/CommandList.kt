package jp.kaleidot725.adbpad.view.component.command

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import jp.kaleidot725.adbpad.model.data.Command

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CommandList(
    commands: List<Command>,
    onExecute: (Command) -> Unit,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = verticalArrangement,
        modifier = modifier
    ) {
        commands.forEach { command ->
            CommandItem(
                title = command.toTitle(),
                detail = command.toDetail(),
                onExecute = { onExecute(command) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

fun Command.toTitle(): String {
    return when (this) {
        Command.DarkThemeOn -> "ダークテーマON"
        Command.DarkThemeOff -> "ダークテーマOFF"
        Command.WifiOn -> "Wi-Fi ON"
        Command.WifiOff -> "Wi-Fi OFF"
        Command.DataOn -> "データ通信 ON"
        Command.DataOff -> "データ通信 OFF"
        Command.WifiAndDataOn -> "Wi-Fi＆データ通信 ON"
        Command.WifiAndDataOff -> "Wi-Fi&データ通信 OFF"
    }
}

fun Command.toDetail(): String {
    return when (this) {
        Command.DarkThemeOn -> "端末のダークテーマ設定をONにします"
        Command.DarkThemeOff -> "端末のダークテーマ設定をOFFにします"
        Command.WifiOn -> "端末のWi-Fi設定をONにします"
        Command.WifiOff -> "端末のWi-Fi設定をOFFにします"
        Command.DataOn -> "端末のデータ通信設定をONにします"
        Command.DataOff -> "端末のデータ通信設定をOFFにします"
        Command.WifiAndDataOn -> "端末のWi-Fi設定とデータ通信設定の両方をONにします"
        Command.WifiAndDataOff -> "端末のWi-Fi設定とデータ通信設定の両方をOFFにします"
    }
}
