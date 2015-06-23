package com.grapesnberries.grapes_n_berries;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class activity_products extends AppCompatActivity {

    final OkHttpClient client = new OkHttpClient();
    private ArrayList<cls_products> products_list = new ArrayList<>();
    MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(products_list);
    StaggeredGridLayoutManager mLayoutManager;

    int load_product = 0;
    String url = "http://grapesnberries.getsandbox.com/products?count=10&from=";
    private boolean loadingMore = false;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    int[] firstVisibleItemPositions = new int[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                pastVisibleItems = mLayoutManager.findFirstVisibleItemPositions(firstVisibleItemPositions)[0];
                if (loadingMore) {
                    if ( (visibleItemCount+pastVisibleItems) >= totalItemCount) {
                        load_product += 10;
                        new getData().execute(url+load_product);
                        loadingMore = false;
                    }
                }
            }
        });
        new getData().execute(url+load_product);
    }

    public class getData extends AsyncTask<String, Void, Void> {
        private boolean error = false;
        @Override
        protected Void doInBackground(String... url) {
            Request request = new Request.Builder().url(url[0]).build();
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    JSONArray products = new JSONArray(response.body().string());
                    if (products.length() > 0) {
                        loadingMore = true;
                        for (int i = 0; i < products.length(); i++) {
                            JSONObject j = products.getJSONObject(i);

                            String id = j.getString("id");
                            String productDescription = j.getString("productDescription");
                            String price = j.getString("price");

                            JSONObject img = j.getJSONObject("image");
                            String imgWidth = img.getString("width");
                            String imgHeight = img.getString("height");
                            String imgUrl = img.getString("url");

                            products_list.add(new cls_products(id, imgWidth, imgHeight, imgUrl, price, productDescription));
                        }
                    }else{
                        loadingMore = false;
                    }
                }
            } catch (Exception e) {
                error = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (error)
                Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_LONG).show();
            adapter.notifyDataSetChanged();
        }
    }

    public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.productViewHolder>{
        class productViewHolder extends RecyclerView.ViewHolder {
            TextView prodPrice;
            TextView prodDescription;
            ImageView prodImage;
            CardView cardView;
            productViewHolder(View itemView) {
                super(itemView);
                prodPrice = (TextView) itemView.findViewById(R.id.prodPrice);
                prodDescription = (TextView) itemView.findViewById(R.id.prodDescription);
                prodImage = (ImageView) itemView.findViewById(R.id.prodImage);
                cardView = (CardView) itemView.findViewById(R.id.cardView);
            }
        }

        MyRecyclerViewAdapter(ArrayList<cls_products> products){
            products_list = products;
        }

        @Override
        public int getItemCount() {
            return products_list.size();
        }

        @Override
        public productViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gridview_item, viewGroup, false);
            return new productViewHolder(v);
        }

        @Override
        public void onBindViewHolder(productViewHolder productViewHolder, final int position) {
            productViewHolder.prodPrice.setText("$"+products_list.get(position).getPrice());
            productViewHolder.prodDescription.setText(products_list.get(position).getDescription());

            float h = Float.valueOf(products_list.get(position).getImageHeight());
            float w = Float.valueOf(products_list.get(position).getImageWidth());

            ViewGroup.LayoutParams layoutParams = productViewHolder.prodImage.getLayoutParams();
            layoutParams.height = convertDpToPixels(h, getApplicationContext());
            layoutParams.width = convertDpToPixels(w, getApplicationContext());
            productViewHolder.prodImage.setLayoutParams(layoutParams);

            Ion.with(productViewHolder.prodImage).fadeIn(true).load(products_list.get(position).getImageUrl());

            productViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), products_list.get(position).getProductID(), Toast.LENGTH_SHORT).show();
                }
            });

        }
        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }
    }

    public static int convertDpToPixels(float dp, Context context){
        Resources resources = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,resources.getDisplayMetrics());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_products, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
