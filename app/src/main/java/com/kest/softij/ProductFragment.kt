package com.kest.softij

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.kest.softij.api.model.Product

class ProductFragment : Fragment() {

    private val viewModel: ProductViewModel by lazy {
        ViewModelProvider(this).get(ProductViewModel::class.java)
    }
    private lateinit var btnWishlist:Button
    companion object{
        private const val KEY_PRODUCT = "KEY_PRODUCT"
        fun init(product: Product):ProductFragment{
            return ProductFragment().apply {
                val bundle = Bundle()
                arguments = bundle.apply{ putSerializable(KEY_PRODUCT,product) }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            viewModel.product = it.getSerializable(KEY_PRODUCT) as Product
        }
        viewModel.checkWishlist(2)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.product_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.checkWishLiveData.observe(viewLifecycleOwner,{
            it?.let {
                viewModel.inWishlist = it.code > 0
                loadUI(view)
            }
        })
    }

    private fun loadUI(view: View){
        btnWishlist = view.findViewById<Button>(R.id.btn_wishlist).apply {
            isEnabled = !viewModel.inWishlist!!
            setOnClickListener {
                viewModel.postWishlist(2)
                setObserver()
                isEnabled = false
            }
        }
        view.findViewById<TextView>(R.id.product_name)
            .text = viewModel.product.name
        view.findViewById<TextView>(R.id.product_model)
            .text = viewModel.product.model
        view.findViewById<TextView>(R.id.product_price)
            .text = getString(R.string.text_product_price,viewModel.product.price.toString())
        view.findViewById<TextView>(R.id.product_desc)
            .text =  viewModel.product.description
        view.findViewById<TextView>(R.id.product_viewed)
            .text = viewModel.product.viewed.toString()
    }

    private fun setObserver(){
        viewModel.postLiveData.observe(viewLifecycleOwner,{
            it?.let { res ->
                viewModel.inWishlist = res.code > 0
                btnWishlist.isEnabled = !viewModel.inWishlist!!
                Toast.makeText(context,res.msg,Toast.LENGTH_LONG).show()
            }
        })
    }

    interface ToProductFragment{
        fun navigate(product:Product)
    }
}