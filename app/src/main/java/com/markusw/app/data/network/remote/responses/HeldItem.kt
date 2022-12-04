package com.markusw.app.data.network.remote.responses

data class HeldItem(
    val item: Item,
    val version_details: List<VersionDetail>
)