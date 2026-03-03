package com.dhananjaysaini.musicplayerapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dhananjaysaini.musicplayerapp.R
import com.dhananjaysaini.musicplayerapp.model.AppTheme

class ThemeAdapter(
    private val context: Context,
    private val themeList: List<AppTheme>
) : RecyclerView.Adapter<ThemeAdapter.ThemeViewHolder>() {

    var onThemeClick: ((AppTheme) -> Unit)? = null

    inner class ThemeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val preview: View = view.findViewById(R.id.themePreview)
        val name: TextView = view.findViewById(R.id.themeName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_theme, parent, false)
        return ThemeViewHolder(view)
    }

    override fun getItemCount() = themeList.size

    override fun onBindViewHolder(holder: ThemeViewHolder, position: Int) {
        val theme = themeList[position]

        holder.preview.setBackgroundResource(theme.drawableRes)
        holder.name.text = theme.name

        holder.itemView.setOnClickListener {
            onThemeClick?.invoke(theme)
        }
    }
}