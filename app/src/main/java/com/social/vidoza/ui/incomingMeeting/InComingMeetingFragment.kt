package com.social.vidoza.ui.incomingMeeting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.social.vidoza.R

class InComingMeetingFragment : Fragment() {

    companion object {
        fun newInstance() = InComingMeetingFragment()
    }

    private lateinit var viewModel: InComingMeetingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.in_coming_meeting_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(InComingMeetingViewModel::class.java)
        // TODO: Use the ViewModel
    }

}