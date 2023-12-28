package vn.trunglt.demogallery

import android.graphics.Bitmap

data class Photo(
    val id: Int,
    val name: String,
    val path: String,
    var bitmap: Bitmap? = null
)