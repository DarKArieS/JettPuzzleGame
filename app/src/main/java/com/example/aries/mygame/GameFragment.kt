package com.example.aries.mygame

import android.animation.ObjectAnimator
import android.databinding.DataBindingUtil
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.aries.mygame.databinding.FragmentGameBinding
import android.widget.CompoundButton
import android.graphics.*
import android.view.animation.DecelerateInterpolator
import androidx.navigation.Navigation
import android.animation.Animator

class GameFragment : Fragment(),ImageProvider, TimesUp {
    override fun getDrawable(): Drawable {
        return drawable!!
    }

    private var score : Int = 0

    private lateinit var binding:  FragmentGameBinding
    lateinit var manager : FragmentManager
    private var firstFragment = TargetImageFragment()
    private val secondFragment = ImageSwitcherFragment()

    private val imagesList : List<Int> = listOf(R.drawable.image_quiz1,R.drawable.image_quiz2)
    private var targetImage: Int = 0

    private var drawable:Drawable? = null
    private var answer : Int = 0
    private var answerPosition = floatArrayOf()
    private var subImagesList: MutableList<Drawable> = mutableListOf()

    private var timer : Timer = Timer(this)

    private lateinit var valueRightAnim: ObjectAnimator
    private lateinit var valueLeftAnim: ObjectAnimator
    private lateinit var answerAnim :ObjectAnimator


    private fun objectRightAnimator() {
        valueRightAnim = ObjectAnimator.ofFloat(binding.sheepRightImageView, "translationY", 0.0f, -360.0f, 0.0f)
        valueRightAnim.duration = 300
        valueRightAnim.interpolator = DecelerateInterpolator()
    }

    private fun objectLeftAnimator() {
        valueLeftAnim = ObjectAnimator.ofFloat(binding.sheepLeftImageView, "translationY", 0.0f, -360.0f, 0.0f)
        valueLeftAnim.duration = 300
        valueLeftAnim.interpolator = DecelerateInterpolator()
    }


    private fun correctAnim(){
        binding.answerAnimImageView.setImageResource(R.drawable.correct)
        answerAnim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                binding.answerAnimImageView.visibility = View.VISIBLE
            }
            override fun onAnimationEnd(animation: Animator) {
                binding.answerAnimImageView.visibility = View.GONE
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })
        answerAnim.start()
    }

    private fun wrongAnim(){
        binding.answerAnimImageView.setImageResource(R.drawable.wrong)
        answerAnim.addListener(object : Animator.AnimatorListener {

            override fun onAnimationStart(animation: Animator) {
                binding.answerAnimImageView.visibility = View.VISIBLE
            }
            override fun onAnimationEnd(animation: Animator) {
                binding.answerAnimImageView.visibility = View.GONE
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })
        answerAnim.start()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)

        objectLeftAnimator()
        objectRightAnimator()
        answerAnim = ObjectAnimator.ofFloat(binding.answerAnimImageView, "alpha", 0.8f, 0f)
            .setDuration(800)

        binding.imageToggleButton.setOnCheckedChangeListener(ToggleButtonOnCheckedChangeListener())
        binding.imageToggleButton.setOnClickListener { valueRightAnim.start() }
        binding.answerButton.setOnClickListener {
            valueLeftAnim.start()
            ansButtonClick()
        }

        binding.checkBox1.setOnClickListener { view:View->
            //println(binding.checkBox1.isChecked)
            if(binding.checkBox1.isChecked){
                binding.checkBox2.isChecked = false
                binding.checkBox3.isChecked = false
                binding.checkBox4.isChecked = false
            }
        }

        binding.checkBox2.setOnClickListener { view:View->
            if(binding.checkBox2.isChecked){
                binding.checkBox1.isChecked = false
                binding.checkBox3.isChecked = false
                binding.checkBox4.isChecked = false
            }
        }

        binding.checkBox3.setOnClickListener { view:View->
            if(binding.checkBox3.isChecked){
                binding.checkBox1.isChecked = false
                binding.checkBox2.isChecked = false
                binding.checkBox4.isChecked = false
            }
        }

        binding.checkBox4.setOnClickListener { view:View->
            if(binding.checkBox4.isChecked){
                binding.checkBox1.isChecked = false
                binding.checkBox2.isChecked = false
                binding.checkBox3.isChecked = false
            }
        }

        updateOption()

        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.imageFrameLayout, firstFragment).commit()

        timer.threshHold = 30
        if ((activity as MainActivity).itemChooseArray[0]){
            timer.threshHold += 30
        }
        setShowTime()
        timer.startTimer()

        score = 0

        return binding.root
    }

    private fun updateOption(){
        // Set chosen Drawable
        targetImage = imagesList.random()
        answer = (0..3).random()
        answerPosition = floatArrayOf(0.1f , 0.1f)

        // Generate random split images and answer
        subImagesList = mutableListOf()
        val bmp: Bitmap = BitmapFactory.decodeResource(resources,targetImage)
        val bmpTmp:MutableList<Bitmap> = mutableListOf()

        for (i in 0..3){
            val splitPositionX = (5..65).random() * 0.01
            val splitPositionY = (5..65).random() * 0.01

            bmpTmp.add(Bitmap.createBitmap(bmp,
                (bmp.width*splitPositionX).toInt(),(bmp.height*splitPositionY).toInt(),
                (bmp.width*(0.3f)).toInt(),(bmp.height*(0.3f)).toInt()))

            subImagesList.add(BitmapDrawable(resources, bmpTmp[bmpTmp.size-1]))
            if(i==answer) {
                answerPosition[0]= splitPositionX.toFloat()
                answerPosition[1]= splitPositionY.toFloat()
            }
        }
        println("answer")
        println(answer)

        // use glasses, remove one check box
        if((activity as MainActivity).itemChooseArray[3]){
            // refresh
            binding.checkBox1.alpha = 1f
            binding.checkBox2.alpha = 1f
            binding.checkBox3.alpha = 1f
            binding.checkBox4.alpha = 1f
            binding.checkBox1.isClickable = true
            binding.checkBox2.isClickable = true
            binding.checkBox3.isClickable = true
            binding.checkBox4.isClickable = true

            // remove
            var removedOption:Int
            while(true){
                removedOption = (0..3).random()
                if(removedOption!=answer)break
            }

            when(removedOption){
                0 -> {
                    binding.checkBox1.alpha = 0.3f
                    binding.checkBox1.isClickable = false
                }
                1 -> {
                    binding.checkBox2.alpha = 0.3f
                    binding.checkBox2.isClickable = false
                }
                2 -> {
                    binding.checkBox3.alpha = 0.3f
                    binding.checkBox3.isClickable = false
                }
                3 -> {
                    binding.checkBox4.alpha = 0.3f
                    binding.checkBox4.isClickable = false
                }
            }
        }


        // send sub images
        // need to use provider or bundle, but i have no time :(
        secondFragment.imagesList = subImagesList

        // Show target image fragment and black box
        manager = childFragmentManager
        val bmpTarget = bmp.copy(Bitmap.Config.ARGB_8888, true)
        val paint = Paint()
        paint.style = Paint.Style.FILL
        paint.color = Color.BLACK
        val canvas = Canvas(bmpTarget)
        canvas.drawRect(
            (bmpTarget.width * answerPosition[0]),(bmpTarget.height * answerPosition[1]),
            (bmpTarget.width * (answerPosition[0]+0.3f)),(bmpTarget.height * (answerPosition[1]+0.3f)),paint)

        drawable = BitmapDrawable(resources, bmpTarget)
    }

    private inner class ToggleButtonOnCheckedChangeListener : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {

            if (isChecked)
            //當按鈕狀態為選取時
            {
                val transaction = manager.beginTransaction()
                transaction.replace(R.id.imageFrameLayout, secondFragment).addToBackStack(null).commit()

            } else
            //當按鈕狀態為未選取時
            {
//                val transaction = manager.beginTransaction()
//                transaction.replace(R.id.imageFrameLayout, firstFragment).commit()
                manager.popBackStack()
            }
        }
    }

    private fun ansButtonClick(){
        if (answer==0){
            if( binding.checkBox1.isChecked &&
                !binding.checkBox2.isChecked &&
                !binding.checkBox3.isChecked &&
                !binding.checkBox4.isChecked ){
                correctAnim()
                score +=2
                if((activity as MainActivity).itemChooseArray[2]) score +=2
            }else{wrongAnim()
                if(!(activity as MainActivity).itemChooseArray[1])score --}

        }else if (answer==1){
            if( !binding.checkBox1.isChecked &&
                binding.checkBox2.isChecked &&
                !binding.checkBox3.isChecked &&
                !binding.checkBox4.isChecked ){
                correctAnim()
                score +=2
                if((activity as MainActivity).itemChooseArray[2]) score +=2
            }else{wrongAnim()
                if(!(activity as MainActivity).itemChooseArray[1])score --}

        }else if (answer==2){
            if( !binding.checkBox1.isChecked &&
                !binding.checkBox2.isChecked &&
                binding.checkBox3.isChecked &&
                !binding.checkBox4.isChecked ){
                correctAnim()
                score +=2
                if((activity as MainActivity).itemChooseArray[2]) score +=2
            }else{wrongAnim()
                if(!(activity as MainActivity).itemChooseArray[1])score --}
        }else if (answer==3){
            if( !binding.checkBox1.isChecked &&
                !binding.checkBox2.isChecked &&
                !binding.checkBox3.isChecked &&
                binding.checkBox4.isChecked ){
                correctAnim()
                score +=2
                if((activity as MainActivity).itemChooseArray[2]) score +=2
            }else{wrongAnim()
                if(!(activity as MainActivity).itemChooseArray[1])score --}
        }
        if(score<0) score = 0

        binding.showScoreTextView.text = score.toString()

        secondFragment.currentImage = 0

        binding.checkBox1.isChecked = false
        binding.checkBox2.isChecked = false
        binding.checkBox3.isChecked = false
        binding.checkBox4.isChecked = false

        updateOption()
        //manager.popBackStack()
        firstFragment = TargetImageFragment()
        val transaction = manager.beginTransaction()

        //transaction.remove(firstFragment)
        transaction.replace(R.id.imageFrameLayout,firstFragment)
        binding.imageToggleButton.isChecked = false
//        transaction.replace(R.id.imageFrameLayout, firstFragment)
//        transaction.detach(firstFragment)
//        transaction.attach(firstFragment)
        transaction.commit()
    }

    override fun setShowTime() {
        binding.showTimeTextView.text = (timer.threshHold - timer.secondsCount).toString()
    }

    override fun gameGG() {
        timer.stopTimer()
        (activity as MainActivity).currentScore = score
        Navigation.findNavController(view!!).navigate(R.id.action_gameFragment_to_gameOverFragment)
    }

    override fun onDestroy() {
        println("gameFragment destroyed!")
        timer.stopTimer()
        super.onDestroy()
    }
}
