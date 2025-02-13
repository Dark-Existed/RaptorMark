package io.github.devriesl.raptormark.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.devriesl.raptormark.R
import io.github.devriesl.raptormark.ui.benchmark.BenchmarkContent
import io.github.devriesl.raptormark.ui.history.HistoryContent
import io.github.devriesl.raptormark.ui.setting.SettingContent
import io.github.devriesl.raptormark.ui.theme.RaptorMarkTheme
import io.github.devriesl.raptormark.viewmodels.BenchmarkViewModel
import io.github.devriesl.raptormark.viewmodels.HistoryViewModel
import io.github.devriesl.raptormark.viewmodels.MainViewModel
import io.github.devriesl.raptormark.viewmodels.SettingViewModel

@Composable
fun RaptorApp(
    mainViewModel: MainViewModel,
    benchmarkViewModel: BenchmarkViewModel,
    historyViewModel: HistoryViewModel,
    settingViewModel: SettingViewModel
) {
    RaptorMarkTheme {
        val (selectedSection, setSelectedSection) = rememberSaveable(stateSaver = enumSaver()) {
            mutableStateOf(
                AppSections.BENCHMARK
            )
        }
        val sections = AppSections.values()
        val selectedIndex by remember(selectedSection) {
            derivedStateOf { sections.indexOf(selectedSection) }
        }
        val saveableStateHolder = rememberSaveableStateHolder()
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val widthClass = rememberSizeClass(size = maxWidth)
            Scaffold(
                topBar = {
                    Column {
                        AppTopBar(mainViewModel)
                        if (widthClass == SizeClass.Compact) {
                            AppTopTab(
                                selectedIndex = selectedIndex,
                                sections = sections,
                                setSelectedSection = setSelectedSection
                            )
                        }
                    }
                },
                content = { scaffoldPadding ->
                    Row(modifier = Modifier.padding(scaffoldPadding)) {
                        if (widthClass != SizeClass.Compact) {
                            NavigationRail(
                                backgroundColor = MaterialTheme.colors.surface
                            ) {
                                if (widthClass == SizeClass.Expanded) {
                                    AppDrawer(
                                        selectedSectionIndex = selectedIndex,
                                        sections = sections,
                                        setSelectedSection = setSelectedSection,
                                        modifier = Modifier
                                            .width(280.dp)
                                    )
                                } else {
                                    AppNavigationRail(
                                        selectedSectionIndex = selectedIndex,
                                        sections = sections,
                                        setSelectedSection = setSelectedSection,
                                    )
                                }
                            }
                        }
                        Box {
                            saveableStateHolder.SaveableStateProvider(selectedSection) {
                                when (selectedSection) {
                                    AppSections.BENCHMARK -> BenchmarkContent(benchmarkViewModel)
                                    AppSections.HISTORY -> HistoryContent(historyViewModel)
                                    AppSections.SETTING -> SettingContent(settingViewModel)
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}

enum class AppSections(
    @StringRes val title: Int,
    @DrawableRes val icon: Int
) {
    BENCHMARK(R.string.benchmark_page_title, R.drawable.ic_benchmark_tab),
    HISTORY(R.string.history_page_title, R.drawable.ic_history_tab),
    SETTING(R.string.setting_page_title, R.drawable.ic_setting_tab)
}

inline fun <reified Type : Enum<Type>> enumSaver() = Saver<Type, String>(
    save = { it.name },
    restore = { enumValueOf<Type>(it) }
)
