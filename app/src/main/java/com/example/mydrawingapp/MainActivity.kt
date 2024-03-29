package com.example.mydrawingapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.example.mydrawingapp.databinding.ActivityMainBinding
import android.Manifest
import androidx.core.app.ActivityCompat


class MainActivity : AppCompatActivity() {

    private var drawingView: DrawingView?=null
    private lateinit var binding: ActivityMainBinding
    private var mImageButtonCurrentPaint: ImageButton? = null




    val requestPermission: ActivityResultLauncher<Array<String>> =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
                permission->
                permission.entries.forEach{
                    val permissionName = it.key
                    val isGranted=it.value

                    if(isGranted){
                        Toast.makeText(this,
                            "permission granted now you can read the storage files",
                        Toast.LENGTH_SHORT).show()

                    }else{
                        if(permissionName == Manifest.permission.READ_EXTERNAL_STORAGE){
                            Toast.makeText(this@MainActivity,"Oops you just Denied the permission",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawingView =findViewById(R.id.drawingView)
        drawingView?.setSizeForBrush(20.toFloat())

        val linearLayoutPaintColors= findViewById<LinearLayout>(R.id.ll_paint_colors)

        mImageButtonCurrentPaint= linearLayoutPaintColors[1] as ImageButton
        mImageButtonCurrentPaint!!.setImageDrawable(
            ContextCompat.getDrawable(this,R.drawable.pallet_pressed))

        val ibBrush =findViewById<ImageButton>(R.id.ib_brush)
        ibBrush.setOnClickListener{
            showBrushSizeChooserDialog()
        }

        val ibGallery=findViewById<ImageButton>(R.id.ib_gallery)
        ibGallery.setOnClickListener{
            requestStoragePermission()

        }
    }

    private fun showBrushSizeChooserDialog(){
        val brushDialog =Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush Size: ")
        val smallBtn= brushDialog.findViewById<ImageButton>(R.id.ib_small_brush)
        smallBtn.setOnClickListener {
            drawingView?.setSizeForBrush(10.toFloat())
            brushDialog.dismiss()
        }
        val mediumBtn= brushDialog.findViewById<ImageButton>(R.id.ib_medium_brush)
        mediumBtn.setOnClickListener {
            drawingView?.setSizeForBrush(20.toFloat())
            brushDialog.dismiss()
        }
        val largeBtn= brushDialog.findViewById<ImageButton>(R.id.ib_large_brush)
        largeBtn.setOnClickListener {
            drawingView?.setSizeForBrush(30.toFloat())
            brushDialog.dismiss()
        }

        brushDialog.show()
    }

    fun paintClicked(view: View){
        //Toast.makeText(this,"clicked paint", Toast.LENGTH_LONG).show()
        if(view !=mImageButtonCurrentPaint){
            val imageButton = view as ImageButton
            val colorTag=imageButton.tag.toString()
            drawingView?.setColor(colorTag)
            imageButton.setImageDrawable(
                ContextCompat.getDrawable(this,R.drawable.pallet_pressed))

            mImageButtonCurrentPaint?.setImageDrawable(
                ContextCompat.getDrawable(this,R.drawable.pallet_normal))

            mImageButtonCurrentPaint=view

        }

    }

    private fun requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
        ){
            showRationaleDialog("Kids Drawing App","kids Drawing App"+
            "needs to access your External storage")
        }else{
            requestPermission.launch(arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            // TODO - ADD writing external storage permission
            ))
        }
    }

    private fun showRationaleDialog(title: String,message: String){
        val builder: AlertDialog.Builder=AlertDialog.Builder(this)

        builder.setTitle(title).setMessage(message)
            .setPositiveButton("Cancel"){dialog,_->
                dialog.dismiss()
            }.create().show()
    }
}


