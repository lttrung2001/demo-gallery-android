package vn.trunglt.demogallery

class ImageMemoryCache {
    companion object {
        private const val MAX_CACHE_MB = 16
        private const val MAX_CACHE_SIZE = MAX_CACHE_MB * 1024 * 1024
    }

    private var limitSize: Long = 0L
}