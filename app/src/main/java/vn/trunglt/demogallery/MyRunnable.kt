package vn.trunglt.demogallery

class MyRunnable(private val task: () -> Unit) : Runnable {
    @Volatile
    private var shutdown = false
    override fun run() {
        while (!shutdown) {
            task.invoke()
            return
        }
    }

    fun shutdown() {
        shutdown = true
    }
}