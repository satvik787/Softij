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
import com.kest.softij.vm.ProductViewModel
import com.kest.softij.api.model.Product

class ProductFragment : Fragment() {

    private val viewModel: ProductViewModel by lazy {
        ViewModelProvider(this).get(ProductViewModel::class.java)
    }

    private var type:Int? = null
    private var index:Int? = null

    companion object{
        private const val KEY_PRODUCT = "KEY_PRODUCT"
        private const val KEY_TYPE = "KEY_TYPE"
        private const val KEY_INDEX = "KEY_INDEX"
        const val WISHLIST_PRODUCT:Int = 0
        const val DEFAULT:Int = 1
        fun init(product: Product,type:Int,index:Int):ProductFragment{
            return ProductFragment().apply {
                val bundle = Bundle()
                arguments = bundle.apply{
                    putSerializable(KEY_PRODUCT,product)
                    putInt(KEY_TYPE,type)
                    putInt(KEY_INDEX,index)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            viewModel.product = it.getSerializable(KEY_PRODUCT) as Product
            type = it.getInt(KEY_TYPE)
            index = it.getInt(KEY_INDEX)
        }
        if(viewModel.inWishlist == null) viewModel.checkWishlist(31)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.checkWishLiveData.observe(viewLifecycleOwner,{
            it?.let {
                viewModel.inWishlist = it.code > 0
                loadUI(view)
            }
        })
        viewModel.putWishlist.observe(viewLifecycleOwner,{
            it?.let { res ->
                viewModel.inWishlist = res.code > 0
                Toast.makeText(context,res.msg,Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.removeWishlist.observe(viewLifecycleOwner,{
            it?.let { res ->
                viewModel.inWishlist = !(res.code > 0)
                Toast.makeText(context,res.msg,Toast.LENGTH_SHORT).show()
            }
        })

    }

    override fun onStop() {
        super.onStop()
        if(isRemoving){
            viewModel.postUpdateViews()
            viewModel.product.viewed++
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(type == WISHLIST_PRODUCT){
            val target = targetFragment as ListFragment
            if(!viewModel.inWishlist!!) target.onRemove(index!!)
        }
    }


    private fun loadUI(view: View){
        val btnWishlist = view.findViewById<Button>(R.id.btn_wishlist)
        btnWishlist.setOnClickListener {
            if(viewModel.inWishlist!!){
                viewModel.postRemoveWishlist()
            }else{
                viewModel.postWishlist()
            }
            viewModel.inWishlist = !viewModel.inWishlist!!
        }
        view.findViewById<TextView>(R.id.order_name)
            .text = viewModel.product.name
        view.findViewById<TextView>(R.id.product_model)
            .text = viewModel.product.model
        view.findViewById<TextView>(R.id.product_price)
            .text = getString(R.string.text_product_price,viewModel.product.price.toString())
        view.findViewById<TextView>(R.id.product_desc)
            .text =  viewModel.product.description
        view.findViewById<TextView>(R.id.product_viewed)
            .text = getString(R.string.text_viewed,viewModel.product.viewed.toString())
        view.findViewById<TextView>(R.id.product_stock)
            .text = getString(R.string.text_stock,viewModel.product.stock.toString())
        view.findViewById<TextView>(R.id.product_date)
            .text = getString(R.string.text_date_available,viewModel.product.dateAdded.substring(0,11))
    }


    interface ToProductFragment{
        fun launchProduct(productFragment: ProductFragment)
    }

    interface WishlistState{
        fun onRemove(index:Int)
    }
}