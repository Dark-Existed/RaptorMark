package io.github.devriesl.raptormark.ui.benchmark

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.devriesl.raptormark.R

@Composable
fun TestItem(
    @StringRes title: Int,
    bandwidth: Int?,
    showLatency: Boolean,
    latency: Int?
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth()
    ) {
        val bandwidthText = if (bandwidth != null) {
            stringResource(R.string.sum_of_bw_test_result_format, bandwidth)
        } else {
            String()
        }
        Row {
            Text(
                text = stringResource(title),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = bandwidthText,
                modifier = Modifier.weight(2f),
                textAlign = TextAlign.End
            )
        }
        if (showLatency) {
            val latencyText = if (latency != null) {
                stringResource(R.string.avg_of_4n_lat_result_format, latency)
            } else {
                String()
            }

            Spacer(Modifier.height(4.dp))
            Row {
                Text(
                    text = stringResource(id = R.string.rand_4n_lat_title),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = latencyText,
                    modifier = Modifier.weight(2f),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}
