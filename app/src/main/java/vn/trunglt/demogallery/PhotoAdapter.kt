package vn.trunglt.demogallery

import android.content.ContentResolver
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import vn.trunglt.demogallery.databinding.ItemPhotoBinding

class PhotoAdapter :
    RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {
    val mList = mutableListOf<Photo>()
    val imageLoader = ImageLoader()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(
            ItemPhotoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    fun addData(data: List<Photo>) {
        val preSize = mList.size
        mList.addAll(data)
        notifyItemRangeInserted(preSize, mList.size)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind()
    }

    inner class PhotoViewHolder(private val binding: ItemPhotoBinding) : ViewHolder(binding.root) {
        fun bind() {
            val photo = mList[adapterPosition]
            imageLoader.loadImageInto(photo.path, binding.root)
//            Glide.with(binding.root)
//                .load(photo.path)
//                .into(binding.root)
        }
    }
}

fun findPhotos(
    context: Context,
    path: String,
    page: Int,
    limit: Int
): List<Photo> {
    val photoList: ArrayList<Photo> = ArrayList()
    val columns = arrayOf(
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATA,
        MediaStore.Images.Media._ID
    )
    val sort = MediaStore.Images.ImageColumns.DATE_TAKEN
//    val selection = MediaStore.Files.FileColumns.RELATIVE_PATH + " like ? "
//    val selectionArgs = arrayOf("%$path%")
    val selection = null
    val selectionArgs = null
    val imageCursor = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            columns,
            selection,
            selectionArgs,
            "$sort DESC LIMIT $limit OFFSET ${page * limit}"
        )
    } else {
        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, Bundle().apply {
                putInt(ContentResolver.QUERY_ARG_LIMIT, limit)
                putInt(ContentResolver.QUERY_ARG_OFFSET, page * limit)
            }, null
        )
    }
    imageCursor?.let { cs ->
        for (i in 0 until cs.count) {
            cs.moveToPosition(i)
            val idColumnIndex = cs.getColumnIndex(MediaStore.Images.Media._ID)
            val nameColumnIndex = cs.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
            val dataColumnIndex =
                cs.getColumnIndex(MediaStore.Images.Media.DATA)
            photoList.add(
                Photo(
                    cs.getInt(idColumnIndex),
                    cs.getString(nameColumnIndex),
                    cs.getString(dataColumnIndex)
                )
            )
        }
    }
    imageCursor?.close()
    return photoList
}