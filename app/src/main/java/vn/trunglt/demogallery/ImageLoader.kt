package vn.trunglt.demogallery

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import android.widget.ImageView
import kotlinx.coroutines.asCoroutineDispatcher

class ImageLoader {
    private val cache = LruCache<String, Bitmap>(10)
    private val tasks = hashMapOf<String, MyRunnable>()

    fun loadImageInto(url: String, view: ImageView) {
        if (cache[url] != null) {
            view.setImageBitmap(cache[url])
            return
        }
        if (view.tag != url) {
            tasks[view.tag]?.shutdown()
        }
        view.tag = url
        MyRunnable {
            val resizedBitmap = view.let {
                cache[url] ?: Bitmap.createScaledBitmap(
                    BitmapFactory.decodeFile(
                        url,
                        BitmapFactory.Options().apply {
                            this.inDensity = it.context.resources.displayMetrics.densityDpi
                            this.inTargetDensity = it.resources.displayMetrics.densityDpi
                            this.inScaled = true
                            this.inBitmap = cache[url]
                        }), it.width, it.height, false
                ).also { b ->
                    cache.put(url, b)
                }
            }
            MyRunnable {
                view.setImageBitmap(resizedBitmap)
                tasks.remove(url)
            }.also {
                tasks[url] = it
                Executors.main.post(it)
            }
        }.also {
            tasks[url] = it
            Executors.io.execute(it)
        }
    }
}