package com.ss.instagramdownloader.base

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.Insets
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.ss.instagramdownloader.R
import com.ss.instagramdownloader.databinding.ActivityBaseBinding


abstract class BaseActivity<B : ViewDataBinding, V : BaseViewModel> : AppCompatActivity() {

    private lateinit var mBinding: ActivityBaseBinding
    protected lateinit var binding: B
    protected lateinit var viewModel: V
    private var mProgressDialog: Dialog? = null

    @LayoutRes
    abstract fun getLayoutId(): Int

    abstract fun getActivityViewModel(): V


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_base)
        performDataBinding()
        initViewModel()
        setFullScreen()
        setLoadingObserver()
        this::viewModel.isInitialized.let {
            lifecycle.addObserver(viewModel)
        }
    }


    override fun onStart() {
        super.onStart()
    }

    private fun setFullScreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v: View, windowInsets: androidx.core.view.WindowInsetsCompat ->
            val insets: Insets = windowInsets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars())
            // Apply the insets as a margin to the view. Here the system is setting
            // only the bottom, left, and right dimensions, but apply whichever insets are
            // appropriate to your layout. You can also update the view padding
            // if that's more appropriate.
            val mlp = v.layoutParams as ViewGroup.MarginLayoutParams
            mlp.leftMargin = insets.left
            mlp.bottomMargin = insets.bottom
            mlp.rightMargin = insets.right
            v.layoutParams = mlp
            androidx.core.view.WindowInsetsCompat.CONSUMED
        }
    }



    protected fun setLoadingObserver() {


        viewModel.getLoading().observe(this as LifecycleOwner, Observer {
            if (it) showProgressBar()
            else hideProgressBar()


        })
        viewModel.getMessageData().observe(this as LifecycleOwner, Observer {

            it?.let {
                showSnackBar(it.msg)
            }


        })
    }


    fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        val decorView = window.decorView
        decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_IMMERSIVE // Set the content to appear under the system bars so that the
                    // content doesn't resize when the system bars hide and show.
                    and View.SYSTEM_UI_FLAG_LAYOUT_STABLE and View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION and View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Hide the nav bar and status bar
                    and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION and View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    fun showSystemUI() {
        val decorView = window.decorView
        decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }


    fun addFragmentWithoutBS(containerId: Int, fragment: Fragment, inclusive: Boolean = false) {
        val manager = supportFragmentManager
        if (inclusive) manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        val transaction: FragmentTransaction = manager.beginTransaction()
        //transaction.setCustomAnimations(R.anim.fragment_fade_enter,R.anim.fragment_fade_exit)
        transaction.add(containerId, fragment, fragment::class.java.name)
        transaction.commit()

    }

    fun addFragmentWithBs(containerId: Int, fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        // transaction.setCustomAnimations(R.anim.fragment_fade_enter,R.anim.fragment_fade_exit)
        transaction.add(containerId, fragment, fragment::class.java.name)
        transaction.addToBackStack(fragment::class.java.name)
        transaction.commit()
    }

    fun replaceFragment(containerId: Int, fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(
            containerId, fragment, fragment::class.java.name
        )  // parent_lt_main  , flMainContainer
            .addToBackStack(fragment::class.java.name)

            .commit()


    }


    fun showSnackBar(message: String?, length: Int = Snackbar.LENGTH_SHORT) {
        message?.let { Snackbar.make(binding.root, it, length).show() }
    }


    private fun performDataBinding() {
        binding = DataBindingUtil.inflate(
            layoutInflater, getLayoutId(), findViewById(R.id.content_frame), true
        )
        // binding = DataBindingUtil.setContentView(this, getLayoutId())
        binding.executePendingBindings()
    }

    private fun initViewModel() {
        viewModel = getActivityViewModel()
    }

    fun showProgressBar() {
        if (!isFinishing) {
            hideProgressBar()
            mProgressDialog = Dialog(this)
            mProgressDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val view: View = LayoutInflater.from(this).inflate(R.layout.layout_progressbar, null)
            mProgressDialog?.setContentView(view)

            view.visibility = View.VISIBLE
            mProgressDialog?.setCancelable(false)

            if (mProgressDialog?.window != null) {
                mProgressDialog?.window?.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        this, android.R.color.transparent
                    )
                )
                mProgressDialog?.window?.setDimAmount(0f)
                mProgressDialog?.window?.setGravity(Gravity.CENTER)
            }
            mProgressDialog?.show()
        }
    }

    fun hideProgressBar() {
        if (!isFinishing && mProgressDialog != null && mProgressDialog?.isShowing!!) {
            mProgressDialog?.dismiss()
            mProgressDialog = null
        }
    }

    fun cancelProgressBar() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.cancel();
        }
    }


    /*
     get current fragment instance
     */
    fun getCurrentFragment(containerId: Int): Fragment? {
        return supportFragmentManager.findFragmentById(containerId)
    }

    /*
    used for hiding keyboard
     */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val view = currentFocus

        if (view != null && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) && view is EditText && !view.javaClass.getName()
                .startsWith(
                    "android.webkit."
                )
        ) {
            val scrcoords = IntArray(2)
            view.getLocationOnScreen(scrcoords)
            val x = ev.rawX + view.left - scrcoords[0]
            val y = ev.rawY + view.top - scrcoords[1]
            if (x < view.left || x > view.right || y < view.top || y > view.bottom) (this.getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager).hideSoftInputFromWindow(
                this.window.decorView.applicationWindowToken, 0
            )
        }
        return super.dispatchTouchEvent(ev)
    }


    fun showProgressDialog() {
        showProgressBar()
    }

    fun dismissProgressDialog() {
        hideProgressBar()
    }

    fun isProgressDialogShowing(): Boolean {
        if (!isFinishing && mProgressDialog != null) {
            return mProgressDialog!!.isShowing
        }
        return false
    }


    fun getBaseActContext(): Context? {
        return if (!isFinishing) this
        else null
    }

    fun showShortToast(msg: Int) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun showLongToast(msg: String?) {
        msg?.let {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }
    }


    fun hideSoftKeyboard() {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        this::viewModel.isInitialized.let {
            lifecycle.removeObserver(viewModel)
        }
    }
}