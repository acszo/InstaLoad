package com.acszo.instaload.data.response

data class ShortcodeMedia(
    val owner: Owner,
    val edge_sidecar_to_children: EdgeSideCarToChildren,
    val thumbnail_src: String,
    val video_url: String
)