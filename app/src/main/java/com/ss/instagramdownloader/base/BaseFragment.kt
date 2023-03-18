package com.ss.instagramdownloader.base

import android.app.Dialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.*
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.ss.instagramdownloader.R
import com.ss.instagramdownloader.databinding.FragmentBaseBinding
import com.ss.instagramdownloader.utils.IRecyclerviewItemClickListener


abstract class BaseFragment<B : ViewDataBinding, V : BaseViewModel> : Fragment(),
    IRecyclerviewItemClickListener {

    private lateinit var mBinding: FragmentBaseBinding
    protected lateinit var binding: B
    protected lateinit var viewModel: V

    private lateinit var rootView: View
    private var mProgressDialog: Dialog? = null




    @LayoutRes
    abstract fun getLayoutId(): Int

    // abstract fun getBindingVariable(): Int

    abstract fun getFragmentViewModel(): V

//    fun setViewModel(viewModel: V) {
//        this.viewModel = viewModel
//    }

//    fun getViewDataBinding(): B {
//        return binding
//    }


//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if (context is BaseActivity<*, *>) {
//            activity = context as BaseActivity<B, V>
//        }
//
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getFragmentViewModel()
        setHasOptionsMenu(false)
        this::viewModel.isInitialized.let {
            lifecycle.addObserver(viewModel)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_base, container, false)
        binding = DataBindingUtil.inflate(
            inflater,
            getLayoutId(),
            mBinding.root.findViewById(R.id.content_frame),
            true
        )
        mBinding.lifecycleOwner = viewLifecycleOwner
        rootView = mBinding.root
        return rootView
        //  return inflater.inflate(R.layout.fragment_generate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.executePendingBindings()

//                viewModel.getMessageData().observe(requireActivity(), Observer {
//                    it?.let {
//                showSnackBar(it)
//                }
//
//
//            })
        viewModel.getLoading().observe(viewLifecycleOwner, Observer {

            if (it) showProgressBar()
            else hideProgressBar()


        })

    }

    fun showSnackBar(message: String?, length: Int = Snackbar.LENGTH_SHORT) {
        message?.let { Snackbar.make(binding.root, it, length).show() }
    }

    private fun showProgressBar() {
        if (!requireActivity().isFinishing) {
            hideProgressBar()
            mProgressDialog = Dialog(requireActivity())
            mProgressDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val view: View =
                LayoutInflater.from(requireActivity()).inflate(R.layout.layout_progressbar, null)
            mProgressDialog?.setContentView(view)

            view.visibility = View.VISIBLE
            mProgressDialog?.setCancelable(false)

            if (mProgressDialog?.window != null) {
                mProgressDialog?.window?.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(), android.R.color.transparent
                    )
                )
                mProgressDialog?.window?.setDimAmount(0f)
                mProgressDialog?.window?.setGravity(Gravity.CENTER)
            }
            mProgressDialog?.show()
        }
    }

    private fun hideProgressBar() {
        if (!requireActivity().isFinishing && mProgressDialog != null && mProgressDialog?.isShowing!!) {
            mProgressDialog?.dismiss()
            mProgressDialog = null
        }
    }







    override fun onItemClick(position: Int?, model: Any, tag: Any?) {
    }

    override fun onDestroy() {
        super.onDestroy()
        this::viewModel.isInitialized.let {
            lifecycle.removeObserver(viewModel)
        }
    }

}