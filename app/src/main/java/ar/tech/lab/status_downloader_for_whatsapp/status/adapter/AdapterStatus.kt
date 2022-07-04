package ar.tech.lab.status_downloader_for_whatsapp.status.adapter

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import ar.tech.lab.status_downloader_for_whatsapp.status.R
import ar.tech.lab.status_downloader_for_whatsapp.status.model.model
import com.bumptech.glide.Glide
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class AdapterStatus(
    private val context: Context,
    private var model: ArrayList<model>
) : RecyclerView.Adapter<AdapterStatus.StatusViewholder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StatusViewholder {
        return StatusViewholder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.design_layout, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: StatusViewholder, position: Int) {
        if (model[position].fileUri.endsWith(".mp4")) {
            holder.iv_status_icon.visibility = View.VISIBLE
        } else {
            holder.iv_status_icon.visibility = View.INVISIBLE

        }

        Glide.with(context).load(Uri.parse(model[position].fileUri)).into(holder.iv_status)

        holder.save.setOnClickListener {
            if (model[position].fileUri.endsWith(".mp4")) {

                val inputStream =
                    context.contentResolver.openInputStream(Uri.parse(model[position].fileUri))
                val fileName = "${System.currentTimeMillis()}.mp4"

                try {
                    val values = ContentValues()
                    values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    values.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
                    values.put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_DOCUMENTS + "/Videos/"
                    )
                    val uri = context.contentResolver.insert(
                        MediaStore.Files.getContentUri("external"),
                        values
                    )
                    val outPutStream = uri?.let {
                        context.contentResolver.openOutputStream(it)
                    }!!
                    if (inputStream != null) {
                        outPutStream.write(inputStream.readBytes())
                    }
                    outPutStream.close()
                    Toast.makeText(context, "Video Saved", Toast.LENGTH_SHORT).show()

                } catch (e: IOException) {
                    Log.d("TAG", "onBindViewHolder: " + e.message)
                }

            } else {

                val bitMap = MediaStore.Images.Media.getBitmap(
                    context.contentResolver,
                    Uri.parse(model[position].fileUri)
                )
                val fileName = "${System.currentTimeMillis()}.jpg"
                var fos: OutputStream? = null
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                    context.contentResolver.also { resolver ->
                        val contentValues = ContentValues().apply {
                            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                            put(
                                MediaStore.MediaColumns.RELATIVE_PATH,
                                Environment.DIRECTORY_PICTURES
                            )

                        }
                        val imageUri: Uri? = resolver.insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            contentValues
                        )
                        fos = imageUri?.let { resolver.openOutputStream(it) }
                    }

                } else {

                    val imagesDir =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    val image = File(imagesDir, fileName)
                    fos = FileOutputStream(image)

                    fos?.use {
                        bitMap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                        Toast.makeText(context, "Image Saved", Toast.LENGTH_SHORT).show()

                    }

                }
            }
        }
    }

    override fun getItemCount(): Int {
        return model.size
    }

    class StatusViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val iv_status: ImageView = itemView.findViewById(R.id.iv_status)
        val iv_status_icon: ImageView = itemView.findViewById(R.id.iv_video_icon)
        val save: Button = itemView.findViewById(R.id.save)

    }
}