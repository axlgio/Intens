package com.proyect.intents

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.proyect.intents.databinding.ActivityMainBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

const val AUTHORITY = "com.dspm.intents.fileprovider"
const val SUFFIX = ".jpg"
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var photoFile: File
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    if (result.data?.data != null) {
                        binding.ivPhoto.setImageURI(result.data?.data)
                        binding.ivPhoto.rotation = 0f
                    } else {
                        println(photoFile.absolutePath)
                        val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                        binding.ivPhoto.setImageBitmap(bitmap)
                        binding.ivPhoto.rotation = 90f
                    }
                }
            }

        binding.btnGellery.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            resultLauncher.launch(gallery)
        }

        binding.btnCamera.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = generateFile()
            val fileProvider = FileProvider.getUriForFile(this, AUTHORITY, photoFile)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            resultLauncher.launch(cameraIntent)
        }
    }
    private fun generateFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", SUFFIX, storageDirectory)
    }
}