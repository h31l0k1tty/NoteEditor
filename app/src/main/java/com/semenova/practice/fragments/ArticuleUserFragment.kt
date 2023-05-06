package com.example.practica.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import com.semenova.practice.R
import com.semenova.practice.adapters.PostsAdapter
import com.semenova.practice.api.IApiClient
import com.semenova.practice.untitle.CurrentUser
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ArticuleUserFragment : Fragment() {
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
        CoroutineScope(Dispatchers.IO).launch {
            IApiClient.create(requireActivity().applicationContext).getArticleByAuthor(CurrentUser.user.login)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        articleListView?.adapter = PostsAdapter(requireActivity().applicationContext, R.layout.article_item, result)
                    },
                    { error ->
                        var errorMessage: String = ""
                        errorMessage = when (error) {
                            is HttpException ->{
                                when (error.code()) {
                                    401 ->"Необходимо авторизоваться"
                                    403-> "Это действие нельзя выполнить"
                                    else -> "Неизвестная ошибка"
                                }
                            }
                            else -> {
                                error.localizedMessage!!
                            }
                        }
                        println(errorMessage)
                        Toast.makeText(requireActivity().applicationContext, errorMessage, Toast.LENGTH_LONG).show()
                    }
                )
        }
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ArticuleUserFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}