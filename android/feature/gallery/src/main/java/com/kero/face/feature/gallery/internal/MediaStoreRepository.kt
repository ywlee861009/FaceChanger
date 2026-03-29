package com.kero.face.feature.gallery.internal

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore

internal class MediaStoreRepository(private val contentResolver: ContentResolver) {

    fun loadImages(): List<MediaImage> {
        val images = mutableListOf<MediaImage>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder,
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val bucketIdCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val bucketNameCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id,
                )
                val bucketId = cursor.getLong(bucketIdCol)
                val bucketName = cursor.getString(bucketNameCol) ?: "기타"
                val dateAdded = cursor.getLong(dateCol)

                images.add(
                    MediaImage(
                        id = id,
                        uri = uri,
                        bucketId = bucketId,
                        bucketDisplayName = bucketName,
                        dateAdded = dateAdded,
                    )
                )
            }
        }
        return images
    }

    fun groupByAlbum(images: List<MediaImage>): List<Album> {
        return images
            .groupBy { it.bucketId }
            .map { (bucketId, imgs) ->
                Album(
                    id = bucketId,
                    displayName = imgs.first().bucketDisplayName,
                    coverUri = imgs.first().uri,
                    count = imgs.size,
                )
            }
            .sortedByDescending { it.count }
    }
}
