package com.example.aries.mygame


import android.content.res.Resources
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.example.aries.mygame.databinding.FragmentImageSwitcherBinding
import android.graphics.drawable.Drawable
import android.R.attr.bitmap
import android.graphics.drawable.BitmapDrawable



class ImageSwitcherFragment : Fragment() {
    var currentImage : Int = 0
    private lateinit var binding: FragmentImageSwitcherBinding

    var imagesList: MutableList<Drawable> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_image_switcher, container, false)

        //currentImage = 0
        binding.currentImageTextView.text = (currentImage+1).toString()
        binding.imageSwitcher.setFactory { ImageView(context) }
        binding.imageSwitcher.setImageDrawable(imagesList[currentImage])

        binding.preButton.setOnClickListener {
            if (currentImage != 0){
                currentImage--
                binding.currentImageTextView.text = (currentImage+1).toString()
                binding.imageSwitcher.setInAnimation(context,R.anim.slide_in_left)
                binding.imageSwitcher.setOutAnimation(context,R.anim.slide_out_right)
                binding.imageSwitcher.setImageDrawable(imagesList[currentImage])
            }
        }

        binding.nextButton.setOnClickListener {
            if (currentImage != imagesList.size-1){
                currentImage++
                binding.currentImageTextView.text = (currentImage+1).toString()
                binding.imageSwitcher.setInAnimation(context,R.anim.slide_in_right)
                binding.imageSwitcher.setOutAnimation(context,R.anim.slide_out_left)
                binding.imageSwitcher.setImageDrawable(imagesList[currentImage])
            }
        }

        return binding.root
    }


}
