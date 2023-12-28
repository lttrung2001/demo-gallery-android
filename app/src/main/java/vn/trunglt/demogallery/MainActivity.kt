package vn.trunglt.demogallery

import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import vn.trunglt.demogallery.databinding.ActivityMainBinding
import java.util.concurrent.ThreadPoolExecutor

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val photoAdapter by lazy { PhotoAdapter() }
    private var page = 0
    private var isLoading = false
    private var cache = hashMapOf<String, Bitmap>()
    private val onScrollListener by lazy {
        object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && !isLoading) {
                    Executors.io.execute {
                        isLoading = true
                        val photos = findPhotos(this@MainActivity, "", page, 18)
                        Executors.main.post {
                            photoAdapter.addData(photos)
                            isLoading = false
                        }
                    }
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
        initListener()
        requestPermissions(arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_MEDIA_IMAGES
        ), 0)
        photoAdapter.addData(findPhotos(this@MainActivity, "", page, 18))
    }

    private fun initListener() {
        binding.apply {
            rcv.addOnScrollListener(onScrollListener)
        }
    }

    private fun initView() {
        binding.rcv.apply {
            setHasFixedSize(true)
            adapter = photoAdapter
        }
    }
}

object Executors {
    val io by lazy { newFixedThreadPoolContext(4, "IO").executor }
    val main by lazy { Handler(Looper.getMainLooper()) }
}