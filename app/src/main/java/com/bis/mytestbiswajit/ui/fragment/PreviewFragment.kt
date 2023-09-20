package com.bis.mytestbiswajit.ui.fragment


import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bis.mytestbiswajit.R
import com.bis.mytestbiswajit.databinding.FragmentPreviewBinding
import com.bis.mytestbiswajit.ui.activity.MainActivity
import com.bis.mytestbiswajit.ui.base.BaseFragment
import com.bis.mytestbiswajit.utils.MyConstants.STATIC_OBJ.isVideo
import com.bis.mytestbiswajit.viewModel.MainViewModel


class PreviewFragment : BaseFragment() {
    lateinit var binding: FragmentPreviewBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    var mediaControls: MediaController? = null
    var path:Uri?=null
    private lateinit var countDownTimer: CountDownTimer
    private val initialCountDown: Long = 30000
    private val countDownInterval: Long = 1000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_preview, container, false)

        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        onViewClick()
    }
    @RequiresApi(Build.VERSION_CODES.P)
    fun init(){
        context?.let { ctx-> mediaControls=MediaController(ctx) }

        mainViewModel.apply {

            filePath.observe(viewLifecycleOwner){
                path=it

                if (!isVideo){
                    binding.imgPreview.visibility=View.VISIBLE
                    binding.videoview.visibility=View.GONE
                    /*Glide
                        .with(requireActivity())
                        .load(it.toString())
                        .into(binding.imgPreview)*/
                    val imageUri: Uri = it
                    val source = ImageDecoder.createSource(requireActivity().contentResolver, imageUri)
                    val bitmap=ImageDecoder.decodeBitmap(source)
                    binding.imgPreview.setImageBitmap(bitmap)
                }
                else{
                    startCountdown(it)
                }
             }
            }

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun onViewClick(){
        binding.appCompatButton.setOnClickListener{
            findNavController().navigate(R.id.action_previewFragment_to_homeFragment)
        }

    }


    private fun startCountdown(uri: Uri) {
        binding.cardview.visibility=View.VISIBLE
        binding.Prog.max=30
        countDownTimer = object : CountDownTimer(initialCountDown, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                binding.Prog.progress=secondsRemaining.toInt()
            }

            override fun onFinish() {
                binding.apply {
                    binding.videoview.visibility=View.VISIBLE
                    imgPreview.visibility=View.GONE
                    videoview.setVideoURI(uri)
                    textView.setText(uri.toString())

                    mediaControls?.let{mc->
                        mc.setAnchorView(videoview)
                        mc.setMediaPlayer(videoview)
                        videoview.setMediaController(mc)
                        binding.cardview.visibility=View.GONE
                        videoview.start()
                    }
                }
            }


        }

        countDownTimer.start()
    }
}