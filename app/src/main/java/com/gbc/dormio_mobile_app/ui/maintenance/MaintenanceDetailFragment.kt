package com.gbc.dormio_mobile_app.ui.maintenance

import android.R.id.message
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.gbc.dormio_mobile_app.R
import com.gbc.dormio_mobile_app.data.model.maintenance.MaintenanceRequestDto
import com.gbc.dormio_mobile_app.data.model.maintenance.MaintenanceResponse
import com.gbc.dormio_mobile_app.data.model.maintenance.RequestStatus
import com.gbc.dormio_mobile_app.databinding.FragmentMaintenanceDetailBinding
import com.gbc.dormio_mobile_app.utils.NetworkResult
import com.gbc.dormio_mobile_app.viewmodel.maintenance.MaintenanceViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MaintenanceDetailFragment : Fragment(R.layout.fragment_maintenance_detail) {

    private var _binding: FragmentMaintenanceDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MaintenanceViewModel by viewModels()
    private val args: MaintenanceDetailFragmentArgs by navArgs()

    private var isEditMode = false
    private var selectedImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){ uri ->
        uri?.let{
            selectedImageUri = it
            Glide.with(this)
                .load(it)
                .centerCrop()
                .override(1000, 1000)
                .into(binding.ivPreview)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMaintenanceDetailBinding.bind(view)

        viewModel.getRequestDetail(args.requestId)

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.detailState.collect { result: NetworkResult<MaintenanceResponse> ->
                        when (result) {
                            is NetworkResult.Success -> {
                                populateFields(result.data.data)
                            }

                            is NetworkResult.Error -> {
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(context, result.apiError.message, Toast.LENGTH_SHORT)
                                    .show()
                            }

                            is NetworkResult.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                        }
                    }
                }

                launch {
                    viewModel.formState.collect { state ->
                        binding.progressBar.visibility =
                            if (state.isLoading) View.VISIBLE else View.GONE

                        state.successMessage?.let { message ->
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                            if (message.contains("deleted", ignoreCase = true)) {
                                findNavController().popBackStack()
                            } else {
                                toggleEditMode(false)
                                viewModel.getRequestDetail(args.requestId)
                            }

                            viewModel.resetFormState()
                        }

                        state.errorMessage?.let { error ->
                            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
                            viewModel.resetFormState()
                        }
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.fabEdit.setOnClickListener {
            toggleEditMode(!isEditMode)
        }

        binding.btnSelectImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSubmitUpdate.setOnClickListener {
            val role = viewModel.formState.value.userRole ?: "STUDENT"

            if (role == "ADMIN") {
                viewModel.updateRequestStatus(
                    requestId = args.requestId,
                    status = binding.spinnerStatus.selectedItem.toString(),
                    comment = binding.etAdminComment.text.toString()
                )
            } else {
                viewModel.updateMaintenanceRequest(
                    requestId = args.requestId,
                    title = binding.etTitle.text.toString(),
                    description = binding.etDescription.text.toString(),
                    urgency = binding.spinnerUrgency.selectedItem.toString(),
                    imageUri = selectedImageUri
                )
            }
        }

        binding.btnDelete.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Request")
                .setMessage("Are you sure you want to delete this request?")
                .setPositiveButton("Delete") { _, _ ->
                    viewModel.deleteRequest(args.requestId)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun populateFields(request: MaintenanceRequestDto) {
        binding.etTitle.setText(request.title)
        binding.etDescription.setText(request.description)
        binding.chipStatus.text = request.status.name
        binding.etAdminComment.setText(request.adminComment ?: "No comments from admin yet.")

        val urgencyArray = resources.getStringArray(R.array.urgency_levels)
        val index = urgencyArray.indexOf(request.urgency.name)
        if (index >= 0) binding.spinnerUrgency.setSelection(index)

        Glide.with(requireContext())
            .load(request.imageUrl)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.placeholder_image)
            .centerCrop()
            .into(binding.ivPreview)

        val role = viewModel.formState.value.userRole ?: "STUDENT"

        binding.fabEdit.visibility = if (role == "ADMIN") {
            View.VISIBLE
        } else if (request.status == RequestStatus.PENDING) {
            View.VISIBLE
        } else {
            View.GONE
        }

        binding.btnDelete.visibility = if (role == "ADMIN") {
            View.VISIBLE
        } else if (request.status == RequestStatus.PENDING) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun toggleEditMode(enabled: Boolean) {
        isEditMode = enabled
        val role = viewModel.formState.value.userRole

        if (role == "ADMIN") {
            binding.spinnerStatus.visibility = if (enabled) View.VISIBLE else View.GONE
            binding.chipStatus.visibility = if (enabled) View.GONE else View.VISIBLE

            binding.etAdminComment.isEnabled = enabled

            binding.etTitle.isEnabled = false
            binding.etDescription.isEnabled = false
            binding.spinnerUrgency.isEnabled = false
            binding.btnSelectImage.visibility = View.GONE
        } else {
            binding.etTitle.isEnabled = enabled
            binding.etDescription.isEnabled = enabled
            binding.spinnerUrgency.isEnabled = enabled
            binding.btnSelectImage.visibility = if (enabled) View.VISIBLE else View.GONE

            binding.etAdminComment.isEnabled = false
        }

        binding.btnSubmitUpdate.visibility = if (enabled) View.VISIBLE else View.GONE
        binding.imageOverlay.visibility = if (enabled) View.VISIBLE else View.GONE

        val icon = if (enabled) R.drawable.ic_close else R.drawable.ic_edit
        binding.fabEdit.setImageResource(icon)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Glide.with(this).clear(binding.ivPreview)
        _binding = null
    }
}