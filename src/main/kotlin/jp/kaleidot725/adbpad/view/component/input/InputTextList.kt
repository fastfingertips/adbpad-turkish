package jp.kaleidot725.adbpad.view.component.input

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import jp.kaleidot725.adbpad.model.data.InputText
import jp.kaleidot725.adbpad.view.resource.String

@Composable
fun InputTextList(
    inputTexts: List<InputText>,
    onExecute: (InputText) -> Unit,
    onDelete: (InputText) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        if (inputTexts.isNotEmpty()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                inputTexts.forEach { text ->
                    InputTestItem(
                        text = text,
                        onExecute = { onExecute(text) },
                        onDelete = { onDelete(text) },
                        modifier = Modifier.height(60.dp).fillMaxWidth()
                    )
                }
            }
        } else {
            Text(
                text = String.NOT_FOUND_INPUT_TEXT,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Preview
@Composable
private fun InputTextList_Preview() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        InputTextList(
            inputTexts = listOf(InputText("A"), InputText("B"), InputText("C")),
            onExecute = {},
            onDelete = {},
            modifier = Modifier.fillMaxWidth().weight(0.5f, true)
        )

        InputTextList(
            inputTexts = emptyList(),
            onExecute = {},
            onDelete = {},
            modifier = Modifier.fillMaxWidth().weight(0.5f, true).background(Color.LightGray)
        )
    }
}
