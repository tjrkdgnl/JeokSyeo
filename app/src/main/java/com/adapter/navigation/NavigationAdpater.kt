package com.adapter.navigation

import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapter.viewholder.NavigationViewHolder
import com.jeoksyeo.wet.activity.editprofile.EditProfile
import com.jeoksyeo.wet.activity.login.Login
import com.model.navigation.NavigationItem

class NavigationAdpater(
    private val context: Context ,
    private val lst:MutableList<NavigationItem>) : RecyclerView.Adapter<NavigationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavigationViewHolder {
        return NavigationViewHolder(parent)
    }

    override fun onBindViewHolder(holder: NavigationViewHolder, position: Int) {
        holder.bind(lst[position])
        holder.getViewBinding().navigationLinearLayout.setOnClickListener {

            when(position){
                0 ->{}
                1->{context.startActivity(Intent(context,EditProfile::class.java))}
                2->{}
                3->{}
                4->{}
                5->{}
                6->{context.startActivity(Intent(context, Login::class.java))}
            }
        }
    }

    override fun getItemCount(): Int{
        return lst.size
    }
}