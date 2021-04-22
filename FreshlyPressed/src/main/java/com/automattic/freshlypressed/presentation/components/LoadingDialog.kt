/*
 * Copyright (c) 2021.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.automattic.freshlypressed.presentation.components

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.automattic.freshlypressed.R
import com.automattic.freshlypressed.databinding.DialogLoadingBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Dialog Fragment that shows a indeterminate loading in full screen.
 */
class LoadingDialog : DialogFragment(), CoroutineScope {

    private lateinit var binding: DialogLoadingBinding

    private val loadingValues by lazy { requireActivity().resources.getStringArray(R.array.loading_values) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogLoadingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
        setStyle(STYLE_NORMAL, R.style.ThemeOverlay_MaterialComponents_BottomSheetDialog_FullScreen)
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.also {
            val windowParams = it.attributes
            windowParams.dimAmount = 0f
            windowParams.flags = windowParams.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
            it.attributes = windowParams
        }

        val animation = binding.loadingImage.drawable as? AnimationDrawable

        animation?.start()

        launch {
            loadingValues.forEach {
                binding.loadingText.text = it
                delay(1000L)
            }
        }
    }

    override val coroutineContext: CoroutineContext = Dispatchers.Main
}