package com.example.aries.mygame


import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.aries.mygame.databinding.FragmentTargetImageBinding


class TargetImageFragment : Fragment() {

    //var showImage = R.drawable.image_quiz1
//    lateinit var showImage2 : Drawable
    private lateinit var binding : FragmentTargetImageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val drawable = (parentFragment as ImageProvider).getDrawable()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_target_image, container, false)
//        binding.imageView.setImageResource(showImage)
        binding.imageView.setImageDrawable(drawable)

        return binding.root
    }


}
