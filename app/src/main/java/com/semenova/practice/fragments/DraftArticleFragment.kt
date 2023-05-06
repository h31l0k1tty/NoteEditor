package com.example.practica.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.semenova.practice.R
import com.semenova.practice.adapters.DraftPostsAdapter
import com.semenova.practice.api.Database

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class DraftArticleFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("CheckResult")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_articule_user, container, false)
        val articleListView = view?.findViewById<ListView>(R.id.articlesListView);
        articleListView?.adapter = DraftPostsAdapter(requireActivity().applicationContext, R.layout.article_item, Database(requireActivity().applicationContext,null).getArticles())
        return view
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DraftArticleFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}