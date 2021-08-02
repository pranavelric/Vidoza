package com.social.vidoza.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.social.vidoza.R
import com.social.vidoza.data.model.User
import com.social.vidoza.databinding.UserListItemBinding

class UserListAdapter :
    ListAdapter<User?, UserListAdapter.UserListViewHolder>(MyDiffutilCallback()) {

    lateinit var context: Context

    inner class UserListViewHolder(private val binding: UserListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cp: User?, position: Int) {


            Glide.with(context).load(cp?.imageUrl).error(R.drawable.ic_baseline_person_24)
                .into(binding.profilePic)

            if (!cp?.name.isNullOrBlank())
                binding.userName.text = cp?.name
            else {
                binding.userName.text = "Unknown Name"
            }
            binding.userPhoneNumber.text = cp?.phoneNumber


            binding.call.setOnClickListener {
                onCallItemClickListener?.let { click ->
                    click(cp, position)

                }
            }
            binding.videoCall.setOnClickListener {
                onVideoItemClickListener?.let { click ->
                    click(cp, position)

                }
            }


        }

    }


    open class MyDiffutilCallback : DiffUtil.ItemCallback<User?>() {
        override fun areItemsTheSame(
            oldItem: User,
            newItem: User
        ): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(
            oldItem: User,
            newItem: User
        ): Boolean {
            return oldItem == newItem
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        context = parent.context
        val binding: UserListItemBinding =
            UserListItemBinding.inflate(layoutInflater, parent, false)
        return UserListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }


    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    private var onItemClickListener: ((User?, Int) -> Unit)? = null
    fun setOnItemClickListener(listener: (User?, Int) -> Unit) {
        onItemClickListener = listener
    }

    private var onCallItemClickListener: ((User?, Int) -> Unit)? = null
    fun setOnCallItemClickListener(listener: (User?, Int) -> Unit) {
        onCallItemClickListener = listener
    }

    private var onVideoItemClickListener: ((User?, Int) -> Unit)? = null
    fun setOnVideoItemClickListener(listener: (User?, Int) -> Unit) {
        onVideoItemClickListener = listener
    }


}