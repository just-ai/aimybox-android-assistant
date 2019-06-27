package com.justai.aimybox.components

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.CallSuper
import androidx.annotation.RequiresPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.justai.aimybox.components.adapter.AimyboxAssistantAdapter
import com.justai.aimybox.components.launchfab.ExtendableFloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


abstract class AimyboxAssistantFragment
@RequiresPermission(android.Manifest.permission.RECORD_AUDIO)
constructor() : Fragment(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Main + Job()

    private lateinit var viewModel: AimyboxAssistantViewModel
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: AimyboxAssistantAdapter
    private lateinit var fab: ExtendableFloatingActionButton

    private var revealTimeMs = 0L

    private val onBackPressedCallback = OnBackPressedCallback {
        val isVisible = viewModel.isAssistantVisible.value ?: false
        if (isVisible) viewModel.setAssistantVisibility(false)
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
            adapter = AimyboxAssistantAdapter(requireContext())
            recycler.adapter = adapter

            fab = findViewById(R.id.fragment_aimybox_assistant_fab)
            fab.setOnClickListener { viewModel.onButtonClick() }
        }
    }

    @CallSuper
    open fun onViewModelInitialized(viewModel: AimyboxAssistantViewModel) {
        viewModel.isAssistantVisible.observe(this, Observer { isVisible ->
            coroutineContext.cancelChildren()
            launch {
                if (isVisible) {
                    fab.show()
                } else {
                    fab.hide()
                }

            }
        })

        viewModel.widgets.observe(this, Observer { data ->
            adapter.setData(data)
        })
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