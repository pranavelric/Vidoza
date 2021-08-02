package com.social.vidoza.ui.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.social.vidoza.R
import com.social.vidoza.databinding.SettingsFragmentBinding
import com.social.vidoza.ui.activity.MainActivity
import com.social.vidoza.utils.getBackgroundImage

class SettingsFragment : Fragment() {


    private lateinit var binding: SettingsFragmentBinding
    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SettingsFragmentBinding.inflate(inflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        setData()
    }


    private fun setData() {
        binding.expandedImage.getBackgroundImage(Uri.parse((activity as MainActivity).sharedPrefrences.getBrackgroundImage()))



        binding.nightmodeSwitch.isChecked =
            (activity as MainActivity).sharedPrefrences.getIsNightModeEnabled()


    }

    private fun setClickListeners() {
        binding.nightmodeSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            setNightMode(isChecked)
        }

        binding.background.setOnClickListener {

            selectImage()


        }


    }

    private fun selectImage() {

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(intent, 9998)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 9998) {

            val uri: Uri = data?.data ?: return

            (activity as MainActivity).sharedPrefrences.setBackgroundImage(uri.toString())


            binding.expandedImage.getBackgroundImage(uri)
        }
    }


    private fun setNightMode(checked: Boolean) {
        if (checked) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            (activity as MainActivity).sharedPrefrences.setNightModeEnabled(true)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            (activity as MainActivity).sharedPrefrences.setNightModeEnabled(false)
        }
    }



}