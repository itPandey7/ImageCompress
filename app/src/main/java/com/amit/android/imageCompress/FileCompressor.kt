package com.amit.android.imageCompress

import android.content.Context
import android.graphics.Bitmap.CompressFormat
import android.graphics.Bitmap
import io.reactivex.Flowable
import kotlin.jvm.JvmOverloads

import java.io.File
import java.io.IOException
import java.util.concurrent.Callable

class FileCompressor(context: Context) {
    //max width and height values of the compressed image is taken as 612x816
    private var maxWidth = 2000
    private var maxHeight = 2000
    private var compressFormat = CompressFormat.JPEG
    private var quality = 80
    private var destinationDirectoryPath: String
    fun setMaxWidth(maxWidth: Int): FileCompressor {
        this.maxWidth = maxWidth
        return this
    }

    fun setMaxHeight(maxHeight: Int): FileCompressor {
        this.maxHeight = maxHeight
        return this
    }

    fun setCompressFormat(compressFormat: CompressFormat): FileCompressor {
        this.compressFormat = compressFormat
        return this
    }

    fun setQuality(quality: Int): FileCompressor {
        this.quality = quality
        return this
    }

    fun setDestinationDirectoryPath(destinationDirectoryPath: String): FileCompressor {
        this.destinationDirectoryPath = destinationDirectoryPath
        return this
    }

    @JvmOverloads
    @Throws(IOException::class)
    fun compressToFile(imageFile: File, compressedFileName: String = imageFile.name): File {
        return ImageUtil.compressImage(
            imageFile, maxWidth, maxHeight, compressFormat, quality,
            destinationDirectoryPath + File.separator + compressedFileName
        )
    }

    @Throws(IOException::class)
    fun compressToBitmap(imageFile: File?): Bitmap {
        return ImageUtil.decodeSampledBitmapFromFile(imageFile!!, maxWidth, maxHeight)
    }

    @JvmOverloads
    fun compressToFileAsFlowable(
        imageFile: File,
        compressedFileName: String = imageFile.name
    ): Flowable<File> {
        return Flowable.defer(Callable {
            try {
                return@Callable Flowable.just(compressToFile(imageFile, compressedFileName))
            } catch (e: IOException) {
                return@Callable Flowable.error<File>(e)
            }
        })
    }

    fun compressToBitmapAsFlowable(imageFile: File?): Flowable<Bitmap> {
        return Flowable.defer(Callable {
            try {
                return@Callable Flowable.just(compressToBitmap(imageFile))
            } catch (e: IOException) {
                return@Callable Flowable.error<Bitmap>(e)
            }
        })
    }

    init {
        destinationDirectoryPath = context.cacheDir.path + File.separator + "images"
    }
}