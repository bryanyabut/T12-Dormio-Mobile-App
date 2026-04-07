package com.gbc.dormio_mobile_app.ui.maintenance

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.databinding.FragmentCreateMaintenanceBinding
import com.gbc.dormio_mobile_app.viewmodel.maintenance.MaintenanceViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateMaintenanceFragment : Fragment(R.layout.fragment_create_maintenance) {

    private var _binding: FragmentCreateMaintenanceBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MaintenanceViewModel by viewModels()

    private val pickImageLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let {
            selectedImageUri = it

            binding.ivPreview.visibility = View.VISIBLE

            com.bumptech.glide.Glide.with(this)
                .load(it)
                .centerCrop()
                .into(binding.ivPreview)

            binding.ivPreview.visibility = View.VISIBLE
        }
    }

    private var selectedImageUri: android.net.Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCreateMaintenanceBinding.bind(view)

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.formState.collect { state ->
                    binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    binding.btnSubmit.isEnabled = !state.isLoading

                    state.successMessage?.let {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                        viewModel.resetFormState()

                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }

                    state.errorMessage?.let {
                        binding.tvError.text = it
                        binding.tvError.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnSubmit.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val description = binding.etDescription.text.toString().trim()
            val urgency = binding.spinnerUrgency.selectedItem.toString()

            if (title.isNotEmpty() && description.isNotEmpty()) {
                viewModel.createMaintenanceRequest(title, description, urgency, selectedImageUri)
            } else {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSelectImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}