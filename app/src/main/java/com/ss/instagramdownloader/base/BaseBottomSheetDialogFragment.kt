package com.ss.instagramdownloader.base

import android.app.Dialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ss.instagramdownloader.R
import com.ss.instagramdownloader.utils.IRecyclerviewItemClickListener


abstract class BaseBottomSheetDialogFragment<B : ViewDataBinding, V : BaseViewModel> :
    BottomSheetDialogFragment(), IRecyclerviewItemClickListener {

    protected lateinit var binding: B
    protected lateinit var viewModel: V

    private lateinit var rootView: View

    @LayoutRes
    abstract fun getLayoutId(): Int


    abstract fun getFragmentViewModel(): V

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.BottomSheetDialogStyle)
        viewModel = getFragmentViewModel()
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        rootView = binding.root
        return rootView
    }

    fun isNetworkAvailable(): Boolean {
        val connectivity =
            activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivity.activeNetworkInfo
        return (networkInfo != null && networkInfo.isAvailable && networkInfo.isConnected)
    }


    fun showShortToast(msg: String?) {
        msg?.let {
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
        }


    }

    fun showLongToast(msg: String?) {
        msg?.let {
            Toast.makeText(activity, msg, Toast.LENGTH_LONG).show()
        }


    }

    override fun onItemClick(position: Int?, model: Any, tag: Any?) {

    }
}