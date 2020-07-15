package cashbacklogin.lt.belekas.ehomeshoplt.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cashbacklogin.lt.belekas.ehomeshoplt.FilterProductUser;
import cashbacklogin.lt.belekas.ehomeshoplt.R;
import cashbacklogin.lt.belekas.ehomeshoplt.models.ModelProduct;

public class AdapterProductUser extends RecyclerView.Adapter<AdapterProductUser.HolderProductUser> implements Filterable {

    private Context context;
    public ArrayList<ModelProduct> productList, filterList;
    private FilterProductUser filter;

    public AdapterProductUser(Context context, ArrayList<ModelProduct> productList) {
        this.context = context;
        this.productList = productList;
        this.filterList = productList;
    }

    @NonNull
    @Override
    public HolderProductUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_user, parent, false);
        return new HolderProductUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductUser holder, int position) {
        // get data
        ModelProduct modelProduct = productList.get(position);
        String discountAvailable = modelProduct.getDiscountAvailable();
        String discountNote = modelProduct.getDiscountNote();
        String discountPrice = modelProduct.getDiscountPrice();
        String productCategory = modelProduct.getProductCategory();
        String originalPrice = modelProduct.getOriginalPrice();
        String productDescription = modelProduct.getProductDescription();
        String productTitle = modelProduct.getProductTitle();
        String productQuantity = modelProduct.getProductQuantity();
        String productId = modelProduct.getProductId();
        String timestamp = modelProduct.getTimestamp();
        String productIcon = modelProduct.getProductIcon();

        // set data
        holder.titleTv.setText(productTitle);
        holder.discountNoteTv.setText(productDescription);
        holder.originalPriceTv.setText(originalPrice);
        holder.descriptionTv.setText("$" + productDescription);
        holder.discountedPriceTv.setText("$" + discountPrice);

        if (discountAvailable.equals("true")){
            // product is on discount
            holder.discountedPriceTv.setVisibility(View.VISIBLE);
            holder.discountNoteTv.setVisibility(View.VISIBLE);
            holder.originalPriceTv.setPaintFlags(holder.originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); // add strike through
        } else {
            // product is not on discount
            holder.discountedPriceTv.setVisibility(View.GONE);
            holder.discountNoteTv.setVisibility(View.GONE);
            holder.originalPriceTv.setPaintFlags(0);

        }
        try {
            Picasso.get().load(productIcon).placeholder(R.drawable.ic_baseline_add_shopping_primary).into(holder.productIconIv);
        } catch (Exception e){
            holder.productIconIv.setImageResource(R.drawable.ic_baseline_add_shopping_primary);
        }

        holder.addToCartTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add product to cart
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show product details
            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new FilterProductUser(this, filterList);
        }
        return filter;
    }

    class HolderProductUser extends RecyclerView.ViewHolder{

        // ui views
        private ImageView productIconIv;
        private TextView discountNoteTv, titleTv, descriptionTv, addToCartTv, discountedPriceTv,
                originalPriceTv;


        public HolderProductUser(@NonNull View itemView) {
            super(itemView);

            // init ui views
            productIconIv  = itemView.findViewById(R.id.productIconIv);
            discountNoteTv  = itemView.findViewById(R.id.discountNoteTv);
            titleTv  = itemView.findViewById(R.id.titleTv);
            descriptionTv  = itemView.findViewById(R.id.descriptionTv);
            addToCartTv  = itemView.findViewById(R.id.addToCartTv);
            discountedPriceTv  = itemView.findViewById(R.id.discountedPriceTv);
            originalPriceTv  = itemView.findViewById(R.id.originalPriceTv);
        }
    }
}
