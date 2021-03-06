package com.tunm.cwallpaper2.ui.component.home

import android.annotation.SuppressLint
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tunm.cwallpaper2.R
import com.tunm.cwallpaper2.customview.SimpleItemDecoration
import com.tunm.cwallpaper2.data.dto.category.Category
import com.tunm.cwallpaper2.data.remote.firebase.FirebaseStatus
import com.tunm.cwallpaper2.databinding.FragmentCategoryBinding
import com.tunm.cwallpaper2.ui.base.BaseFragmentBinding
import com.tunm.cwallpaper2.ui.component.login.LoginViewModel
import com.tunm.cwallpaper2.ui.component.profile.categorymanager.CategoryManagerAdapter
import com.tunm.cwallpaper2.ui.component.profile.categorymanager.CategoryViewModel
import com.tunm.cwallpaper2.utils.observe
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.ArrayList

@AndroidEntryPoint
class CategoryFragment : BaseFragmentBinding<FragmentCategoryBinding>(
    FragmentCategoryBinding::inflate
), View.OnClickListener {

    private val loginViewModel: LoginViewModel by activityViewModels()
    private val categoryViewModel: CategoryViewModel by activityViewModels()

    private lateinit var categoryManagerAdapter: CategoryManagerAdapter

    override fun observeViewModel() {
        observe(categoryViewModel.listCategoryResponse) {
            handleResponse(it)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleResponse(status: FirebaseStatus<ArrayList<Category>>) {
        when (status) {
            is FirebaseStatus.Success -> {
                status.data?.forEach {
                    Timber.d("data - ${it.categoryName}, ")
                }
                status.data?.let { categoryManagerAdapter.updateData(it) }
                binding.loadingView.visibility = View.GONE
            }
            is FirebaseStatus.Error -> {
                Toast.makeText(context, status.msg, Toast.LENGTH_SHORT).show()
                binding.loadingView.visibility = View.GONE
            }
            is FirebaseStatus.Loading -> {
                binding.loadingView.visibility = View.VISIBLE
            }
        }
    }

    override fun initUI() {
        categoryViewModel.getAllCategories()
        initRecycleView()
    }

    private fun initRecycleView() {
        categoryManagerAdapter = CategoryManagerAdapter(emptyList())
        binding.categoryRv.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(SimpleItemDecoration(context, 16))
            adapter = categoryManagerAdapter
        }
    }

    override fun setupListeners() {
        binding.searchBar.apply {
            tvCancel.setOnClickListener(this@CategoryFragment)
            edtSearch.setOnFocusChangeListener { view, hasFocus ->
                tvCancel.visibility = if (hasFocus) View.VISIBLE else  View.GONE
            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.tvCancel -> {
                binding.searchBar.apply {
                    edtSearch.clearFocus()
                    tvCancel.visibility = View.GONE
                }
            }
        }
    }

    override fun initToolbar() {

    }

    override fun handleBack() {

    }
}