package com.justai.aimybox.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.justai.aimybox.Aimybox
import com.justai.aimybox.components.adapter.AimyboxAssistantAdapter
import com.justai.aimybox.components.extensions.isPermissionGranted
import com.justai.aimybox.components.extensions.startActivityIfExist
import com.justai.aimybox.components.view.AimyboxButton
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlin.coroutines.CoroutineContext

@Suppress("unused")
class AimyboxAssistantFragment : Fragment(), CoroutineScope {

    companion object {
        private const val REQUEST_PERMISSION_CODE = 100

        private const val ARGUMENTS_KEY = "arguments"
    }

    override val coroutineContext: CoroutineContext = Dispatchers.Main + Job()

    private lateinit var viewModel: AimyboxAssistantViewModel
    private lateinit var recycler: RecyclerView
    private lateinit var aimyboxButton: AimyboxButton

    private lateinit var adapter: AimyboxAssistantAdapter
    private var revealTimeMs = 0L

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val aimyboxProvider = requireNotNull(findAimyboxProvider()) {
            "Parent Activity or Application must implement AimyboxProvider interface"
        }

        if (!::viewModel.isInitialized) {
            viewModel =
                ViewModelProviders.of(requireActivity(), aimyboxProvider.getViewModelFactory())
                    .get(AimyboxAssistantViewModel::class.java)

            val initialPhrase = arguments?.getParcelable<Arguments>(ARGUMENTS_KEY)?.initialPhrase
                ?: context.getString(R.string.initial_phrase)

            viewModel.setInitialPhrase(initialPhrase)
        }

        revealTimeMs = context.resources.getInteger(R.integer.assistant_reveal_time_ms).toLong()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) =
        inflater.inflate(R.layout.fragment_aimybox_assistant, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.apply {
            recycler = findViewById(R.id.fragment_aimybox_assistant_recycler)
            aimyboxButton = findViewById(R.id.fragment_aimybox_assistant_button)
            aimyboxButton.setOnClickListener(::onAimyboxButtonClick)
        }

        adapter = AimyboxAssistantAdapter(viewModel::onButtonClick)
        recycler.adapter = adapter

        viewModel.isAssistantVisible.observe(viewLifecycleOwner, Observer { isVisible ->
            coroutineContext.cancelChildren()
            if (isVisible) aimyboxButton.expand() else aimyboxButton.collapse()
        })

        viewModel.aimyboxState.observe(viewLifecycleOwner, Observer { state ->
            if (state == Aimybox.State.LISTENING) {
                aimyboxButton.onRecordingStarted()
            } else {
                aimyboxButton.onRecordingStopped()
            }
        })

        viewModel.widgets.observe(viewLifecycleOwner, Observer(adapter::setData))

        viewModel.soundVolumeRms.observe(this, Observer { volume ->
            if (::aimyboxButton.isInitialized) {
                aimyboxButton.onRecordingVolumeChanged(volume)
            }
        })

        launch {
            viewModel.urlIntents.consumeEach { url ->
                context?.startActivityIfExist(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun onAimyboxButtonClick(view: View) {
        if (requireContext().isPermissionGranted(Manifest.permission.RECORD_AUDIO)) {
            viewModel.onAssistantButtonClick()
        } else {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_PERMISSION_CODE)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSION_CODE && permissions.firstOrNull() == Manifest.permission.RECORD_AUDIO) {
            if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
                viewModel.onAssistantButtonClick()
            } else {
                requireActivity().supportFragmentManager.beginTransaction().apply {
                    add(R.id.fragment_aimybox_container, MicrophonePermissionFragment())
                    addToBackStack(null)
                    commit()
                }
            }
        }
    }
    /**
    * Use the method to set custom initial phrase for the assistant
    * */
    fun putArguments(arguments: Arguments) = apply {
        setArguments(Bundle().apply { putParcelable(ARGUMENTS_KEY, arguments) })
    }

    /**
     * Back press handler. Add this method invocation in your activity to make back pressed behavior correct.
     *
     * For example:
     * ```
     * override fun onBackPressed() {
     *     if (!assistantFragment.onBackPressed()) super.onBackPressed()
     * }
     * ```
     * */
    fun onBackPressed(): Boolean {
        val isVisible = viewModel.isAssistantVisible.value ?: false
        if (isVisible) viewModel.onBackPressed()
        return isVisible
    }

    override fun onPause() {
        super.onPause()
        viewModel.muteAimybox()
    }

    override fun onResume() {
        super.onResume()
        viewModel.unmuteAimybox()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        coroutineContext.cancel()
    }

    private fun findAimyboxProvider(): AimyboxProvider? {
        val activity = requireActivity()
        val application = activity.application
        return when {
            activity is AimyboxProvider -> activity
            application is AimyboxProvider -> application
            else -> null
        }
    }

    @Parcelize
    data class Arguments(val initialPhrase: String? = null) : Parcelable
}