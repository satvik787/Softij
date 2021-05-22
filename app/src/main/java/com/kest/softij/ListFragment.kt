package com.kest.softij

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kest.softij.api.model.Product


class ListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val viewModel:ListFragmentViewModel by lazy{
        ViewModelProvider(this).get(ListFragmentViewModel::class.java)
    }
    companion object{
        const val LIST_PRODUCTS = 0
        const val LIST_WISHLIST = 1
        private const val KEY_LIST = "KEY_LIST"
        fun init(type:Int):ListFragment{
            return ListFragment().apply {
                val bundle = Bundle()
                bundle.putInt(KEY_LIST,type)
                arguments = bundle
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            viewModel.init(it.getInt(KEY_LIST))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        viewModel.products.observe(viewLifecycleOwner,{
            it?.let { res ->
                res.data?.let { list ->
                    recyclerView.adapter = SearchAdapter(list)
                }
            }
        })
    }

    private class SearchAdapter(private var list:MutableList<Product>)
        :RecyclerView.Adapter<ProductViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            val view:View = LayoutInflater.from(parent.context).inflate(R.layout.item_product,parent,false)
            return ProductViewHolder(view)
        }

        override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
            holder.bind(list[position])
        }

        override fun getItemCount() = this.list.size

    }

    private class ProductViewHolder(view:View)
        :RecyclerView.ViewHolder(view){
        val image:ImageView = view.findViewById(R.id.img_product)
        val name:TextView = view.findViewById(R.id.product_name)
        val model:TextView = view.findViewById(R.id.product_model)
        val price:TextView = view.findViewById(R.id.product_price)
        val buyBtn:Button = view.findViewById(R.id.product_buy)
        val cartBtn:Button = view.findViewById(R.id.product_add_cart)

        fun bind(product: Product){
            image.setImageResource(R.mipmap.ic_wishlist)
            name.text = product.name
            model.text = product.model
            price.text = itemView.context.getString(R.string.text_product_price,product.price.toString())
        }
    }

}