package com.example.mobilne_projekt.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.*
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.mobilne_projekt.R
import com.example.mobilne_projekt.adapter.WordAdapter
import com.example.mobilne_projekt.data.db.entity.Word
import kotlinx.android.synthetic.main.course_detail_fragment.*
import kotlinx.android.synthetic.main.course_detail_top_panel.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CourseDetailFragment : Fragment() {

    companion object {
        fun newInstance() = CourseDetailFragment()
    }

    private lateinit var viewModel: CourseDetailViewModel
    private lateinit var courseName: String
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.course_detail_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CourseDetailViewModel::class.java)
        courseName = arguments!!.getString("courseName", "unknown")
        mContext = activity!!.applicationContext

        bindUI()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addWordFab.setOnClickListener {
            val bundle = bundleOf("courseName" to courseName)
            Navigation.findNavController(view).navigate(R.id.action_courseDetailFragment_to_courseAddWord, bundle)
        }
    }

    private fun bindUI() = lifecycleScope.launch(Dispatchers.IO) {

        val wordAdapter = WordAdapter(mContext)
        wordsRecyclerView.apply {
            adapter = wordAdapter
            val topSpacingItemDecoration = TopSpacingItemDecoration(24)
            addItemDecoration(topSpacingItemDecoration)
            layoutManager = LinearLayoutManager(mContext)
        }

        val wordsLiveData = viewModel.getWordsLiveData(courseName)
        val wordsCountLiveData = viewModel.getWordsCountLiveData(courseName)

        withContext(Dispatchers.Main) {
            wordsLiveData.observe(viewLifecycleOwner, Observer { words ->
                words.let {wordAdapter.setWords(it)}
            })

            wordsCountLiveData.observe(viewLifecycleOwner, Observer {
                wordCountValueTextView.text = it.toString()
                wordCountTextView.text = resources.getQuantityString(R.plurals.slowek, it)
            })
        }
        courseDetailNameTextView.text = courseName
    }

}
