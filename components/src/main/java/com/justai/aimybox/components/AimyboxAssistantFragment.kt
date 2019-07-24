package com.justai.aimybox.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.justai.aimybox.Aimybox
import com.justai.aimybox.components.adapter.AimyboxAssistantAdapter
import com.justai.aimybox.components.extensions.isPermissionGranted
import com.justai.aimybox.components.view.AimyboxButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlin.coroutines.CoroutineContext


abstract class AimyboxAssistantFragment : Fragment(), CoroutineScope {

    companion object {
        private const val REQUEST_PERMISSION_CODE = 100
    }

    override val coroutineContext: CoroutineContext = Dispatchers.Main + Job()

    private lateinit var viewModel: AimyboxAssistantViewModel
    private lateinit var recycler: RecyclerView
    private lateinit var aimyboxButton: AimyboxButton

    private val adapter = AimyboxAssistantAdapter()

    private var revealTimeMs = 0L

    private val onBackPressedCallback = OnBackPressedCallback {
        val isVisible = viewModel.isAssistantVisible.value ?: false
        if (isVisible) viewModel.onBackPressed()
        isVisible
    }

    abstract fun getAimyboxViewModel(): AimyboxAssistantViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (!::viewModel.isInitialized) {
            viewModel = getAimyboxViewModel()
        }
        onViewModelInitialized(viewModel)

        revealTimeMs = context.resources.getInteger(R.integer.assistant_reveal_time_ms).toLong()

        requireActivity().addOnBackPressedCallback(onBackPressedCallback)
    }

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.fragment_aimybox_assistant, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.apply {
            recycler = findViewById(R.id.fragment_aimybox_assistant_recycler)

            recycler.adapter = adapter

            aimyboxButton = findViewById(R.id.fragment_aimybox_assistant_button)
            aimyboxButton.setOnClickListener(::onAimyboxButtonClick)
        }
    }

    @CallSuper
    open fun onViewModelInitialized(viewModel: AimyboxAssistantViewModel) {
        viewModel.isAssistantVisible.observe(this, Observer { isVisible ->
            coroutineContext.cancelChildren()
            if (isVisible) aimyboxButton.expand() else aimyboxButton.collapse()
        })

        viewModel.aimyboxState.observe(this, Observer { state ->
            if (state == Aimybox.State.LISTENING) {
                aimyboxButton.onRecordingStarted()
            } else {
                aimyboxButton.onRecordingStopped()
            }
        })

        viewModel.widgets.observe(this, Observer(adapter::setData))

        viewModel.soundVolumeRms.observe(this, Observer { volume ->
            if (::aimyboxButton.isInitialized) {
                aimyboxButton.onRecordingVolumeChanged(volume)
            }
        })
    }

    @SuppressLint("MissingPermission")
    private fun onAimyboxButtonClick(view: View) {
        if (requireContext().isPermissionGranted(Manifest.permission.RECORD_AUDIO)) {
            viewModel.onButtonClick()
        } else {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_PERMISSION_CODE)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION_CODE && permissions.firstOrNull() == Manifest.permission.RECORD_AUDIO) {
            if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
                viewModel.onButtonClick()
            } else {
                requireActivity().supportFragmentManager.beginTransaction().apply {
                    add(R.id.fragment_aimybox_container, MicrophonePermissionFragment())
                    addToBackStack(null)
                    commit()
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        requireActivity().removeOnBackPressedCallback(onBackPressedCallback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        coroutineContext.cancel()
    }
}