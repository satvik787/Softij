package com.kest.softij

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kest.softij.vm.ListViewModel
import com.kest.softij.api.model.Order
import com.kest.softij.api.model.Product


class ListFragment: Fragment(),ProductFragment.WishlistState {

    private lateinit var recyclerView: RecyclerView
    private var listType:Int? = null

    private val viewModel: ListViewModel by lazy{
        ViewModelProvider(this).get(ListViewModel::class.java)
    }
    private lateinit var toFragment:ProductFragment.ToProductFragment

    companion object{
        const val LIST_PRODUCTS = 0
        const val LIST_WISHLIST = 1
        const val LIST_ORDERS = 2
        private const val LIST_TYPE = "KEY_LIST"
        fun init(type:Int):ListFragment{
            return ListFragment().apply {
                val bundle = Bundle()
                bundle.putInt(LIST_TYPE,type)
                arguments = bundle
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        toFragment = context as ProductFragment.ToProductFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            listType = it.getInt(LIST_TYPE)
            if(savedInstanceState == null) viewModel.init(listType!!)
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
        if(listType == LIST_ORDERS){
            viewModel.orders.observe(viewLifecycleOwner,{
                it?.let { res->
                    res.data?.let{ list ->
                        recyclerView.adapter = OrderAdapter(list)
                    }?:run{
                        Toast.makeText(context,res.msg,Toast.LENGTH_LONG).show()
                    }
                }
            })
        }else {
            viewModel.products.observe(viewLifecycleOwner, {
                it?.let { res ->
                    res.data?.let { list ->
                        recyclerView.adapter = ProductAdapter(list)
                    }?:run{
                        Toast.makeText(context,res.msg,Toast.LENGTH_LONG).show()
                    }
                }
            })
        }
    }

    private inner class OrderAdapter(var list:MutableList<Order>)
        :RecyclerView.Adapter<OrderViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
            val view:View = LayoutInflater.from(parent.context).inflate(R.layout.item_order,parent,false)
            return OrderViewHolder(view)
        }

        override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
            holder.bind(list[position])
        }

        override fun getItemCount() = list.size

    }

    private inner class OrderViewHolder(view :View) :RecyclerView.ViewHolder(view){
        private val image:ImageView = view.findViewById(R.id.img_order)
        private val name:TextView = view.findViewById(R.id.order_name)
        private val quantity:TextView = view.findViewById(R.id.order_quantity)
        private val date:TextView = view.findViewById(R.id.order_date)
        private val price:TextView = view.findViewById(R.id.order_price)
        private val status:TextView = view.findViewById(R.id.order_status)

        fun bind(order: Order){
            name.text = order.name
            quantity.text = getString(R.string.text_order_quantity,order.quantity.toString())
            date.text = getString(R.string.text_order_date,order.dateAdded.substring(0,11))
            price.text = getString(R.string.text_product_price,order.totalPrice.toString())
            status.text = getString(R.string.text_order_status,order.orderStatusId.toString())
        }

    }

    private inner class ProductAdapter(var list:MutableList<Product>)
        :RecyclerView.Adapter<ProductViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            val view:View = LayoutInflater.from(parent.context).inflate(R.layout.item_product,parent,false)
            return ProductViewHolder(view)
        }

        override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
            holder.bind(list[position],position)
        }

        override fun getItemCount() = this.list.size

    }

    private inner class ProductViewHolder(view:View)
        :RecyclerView.ViewHolder(view),View.OnClickListener{
        val image:ImageView = view.findViewById(R.id.img_product)
        val name:TextView = view.findViewById(R.id.order_name)
        val model:TextView = view.findViewById(R.id.product_model)
        val price:TextView = view.findViewById(R.id.product_price)
        val buyBtn:Button = view.findViewById(R.id.product_buy)
        val cartBtn:Button = view.findViewById(R.id.product_add_cart)
        lateinit var product: Product
        var index:Int? = null
        init {
            view.findViewById<LinearLayout>(R.id.info_box).setOnClickListener(this)
        }
        fun bind(product: Product,index: Int){
            this.index = index
            this.product = product
            image.setImageResource(R.mipmap.ic_wishlist)
            name.text = product.name
            model.text = product.model
            price.text = itemView.context.getString(R.string.text_product_price,product.price.toString())
        }

        override fun onClick(v: View?) {
            toFragment.launchProduct(ProductFragment.init(
                this.product,
                if(this@ListFragment.listType == LIST_WISHLIST) ProductFragment.WISHLIST_PRODUCT
                else ProductFragment.DEFAULT,
                this.index!!
            ).apply{
                if(this@ListFragment.listType == LIST_WISHLIST) {
                    setTargetFragment(this@ListFragment,ProductFragment.WISHLIST_PRODUCT)
                }
            })
        }
    }

    override fun onRemove(index:Int) {
        val productAdapter = recyclerView.adapter as ProductAdapter
        productAdapter.list.removeAt(index)
        productAdapter.notifyItemRemoved(index)
    }


}