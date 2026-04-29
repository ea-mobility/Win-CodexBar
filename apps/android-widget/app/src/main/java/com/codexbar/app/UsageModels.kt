package com.codexbar.app

data class WidgetSnapshot(
    val entries: List<WidgetProviderEntry>,
    val generated_at: String
)

data class WidgetProviderEntry(
    val provider: String,
    val primary: RateWindow?,
    val secondary: RateWindow?,
    val account_email: String?,
    val login_method: String?
)

data class RateWindow(
    val used_percent: Double,
    val remaining: Int,
    val limit: Int
)
