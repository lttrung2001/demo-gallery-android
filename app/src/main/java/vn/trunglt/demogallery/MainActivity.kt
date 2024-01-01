package vn.trunglt.demogallery

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.newFixedThreadPoolContext
import vn.trunglt.demogallery.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object {
        const val PAGE_LIMIT = 12
    }

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val photoAdapter by lazy { PhotoAdapter() }
    private var page = 0
    private var isLoading = false
    private val onScrollListener by lazy {
        object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && !isLoading) {
                    Executors.io.execute {
                        isLoading = true
                        val photos = findPhotos(this@MainActivity, "", page, PAGE_LIMIT)
                        Executors.main.post {
                            photoAdapter.addData(photos)
                            isLoading = false
                            page++
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
        requestPermissions(
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_MEDIA_IMAGES
            ), 0
        )
        photoAdapter.addData(findPhotos(this@MainActivity, "", page, PAGE_LIMIT))
    }

    private fun initListener() {
        binding.apply {
            rcv.addOnScrollListener(onScrollListener)
        }
    }

    private fun initView() {
        binding.rcv.apply {
            adapter = photoAdapter
            setHasFixedSize(true)
        }
    }
}

object Executors {
    val io by lazy {
        println(Thread.activeCount())
        newFixedThreadPoolContext(Thread.activeCount() / 2, "IO").executor
    }
    val main by lazy { Handler(Looper.getMainLooper()) }
    private val pools by lazy {

    }
}