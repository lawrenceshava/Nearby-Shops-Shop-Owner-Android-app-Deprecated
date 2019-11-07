package org.nearbyshops.shopkeeperappnew.OrderHistory;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.wunderlist.slidinglayer.SlidingLayer;
import okhttp3.ResponseBody;
import org.nearbyshops.shopkeeperappnew.API.OrderServiceShopStaff;
import org.nearbyshops.shopkeeperappnew.DaggerComponentBuilder;
import org.nearbyshops.shopkeeperappnew.Interfaces.NotifySearch;
import org.nearbyshops.shopkeeperappnew.Interfaces.NotifySort;
import org.nearbyshops.shopkeeperappnew.Model.Order;
import org.nearbyshops.shopkeeperappnew.Model.ModelEndpoints.OrderEndPoint;
import org.nearbyshops.shopkeeperappnew.Model.ModelRoles.User;
import org.nearbyshops.shopkeeperappnew.OrderDetail.OrderDetail;
import org.nearbyshops.shopkeeperappnew.OrderDetail.PrefOrderDetail;
import org.nearbyshops.shopkeeperappnew.OrderHistory.Utility.PrefSortOrders;
import org.nearbyshops.shopkeeperappnew.OrderHistory.Utility.SlidingLayerSortOrders;
import org.nearbyshops.shopkeeperappnew.ViewHoldersForOrders.ViewHolderOrder;
import org.nearbyshops.shopkeeperappnew.Prefrences.PrefLogin;
import org.nearbyshops.shopkeeperappnew.R;
import org.nearbyshops.shopkeeperappnew.ViewHolderCommon.Models.EmptyScreenDataFullScreen;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;


public class OrderHistoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, NotifySort, NotifySearch, ViewHolderOrder.ListItemClick {



    public static final String TAG_SLIDING_LAYER = "sliding_layer_orders";
    public static final String IS_FILTER_BY_SHOP = "IS_FILTER_BY_SHOP";


    @Inject
    OrderServiceShopStaff orderServiceShopStaff;


    private RecyclerView recyclerView;
    private Adapter adapter;

    public List<Object> dataset = new ArrayList<>();

    GridLayoutManager layoutManager;
    private SwipeRefreshLayout swipeContainer;


    final private int limit = 10;
    private int offset = 0;
    private int item_count = 0;

    private boolean isDestroyed;


    @BindView(R.id.slidingLayer) SlidingLayer slidingLayer;
    @BindView(R.id.shop_count_indicator) TextView orderCountIndicator;





    public OrderHistoryFragment() {

        DaggerComponentBuilder.getInstance()
                .getNetComponent()
                .Inject(this);

    }


    public static OrderHistoryFragment newInstance() {
        OrderHistoryFragment fragment = new OrderHistoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        setRetainInstance(true);
        View rootView = inflater.inflate(R.layout.fragment_orders_new, container, false);
        ButterKnife.bind(this,rootView);


        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        swipeContainer = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeContainer);


        if(savedInstanceState==null)
        {
            makeRefreshNetworkCall();
        }


        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), R.color.white));
//        toolbar.setTitle("Nearby Shops");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
//


        setupRecyclerView();
        setupSwipeContainer();

        setupSlidingLayer();


        return rootView;
    }


    void setupSwipeContainer()
    {
        if(swipeContainer!=null) {

            swipeContainer.setOnRefreshListener(this);
            swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
        }

    }


    void setupSlidingLayer()
    {

        ////slidingLayer.setShadowDrawable(R.drawable.sidebar_shadow);
        //slidingLayer.setShadowSizeRes(R.dimen.shadow_size);

        if(slidingLayer!=null) {
            slidingLayer.setChangeStateOnTap(true);
            slidingLayer.setSlidingEnabled(true);
            slidingLayer.setPreviewOffsetDistance(15);
            slidingLayer.setOffsetDistance(10);
            slidingLayer.setStickTo(SlidingLayer.STICK_TO_RIGHT);

//            DisplayMetrics metrics = new DisplayMetrics();
//            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            //RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(250, ViewGroup.LayoutParams.MATCH_PARENT);

            //slidingContents.setLayoutParams(layoutParams);

            //slidingContents.setMinimumWidth(metrics.widthPixels-50);


            if (getChildFragmentManager().findFragmentByTag(TAG_SLIDING_LAYER)==null)
            {
                getChildFragmentManager()
                        .beginTransaction()
                        .add(R.id.slidinglayerfragment,new SlidingLayerSortOrders(),TAG_SLIDING_LAYER)
                        .commit();
            }

        }
    }









    @OnClick({R.id.icon_sort, R.id.text_sort})
    void sortClick()
    {
        slidingLayer.openLayer(true);
    }





    void setupRecyclerView()
    {

        adapter = new Adapter(dataset,this,getActivity());

        recyclerView.setAdapter(adapter);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

//        layoutManager.setSpanCount(metrics.widthPixels/400);



//
//        int spanCount = (int) (metrics.widthPixels/(230 * metrics.density));
//
//        if(spanCount==0){
//            spanCount = 1;
//        }

//        layoutManager.setSpanCount(spanCount);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);


                if(offset + limit > layoutManager.findLastVisibleItemPosition() + 1 - 1)
                {
                    return;
                }


                if(layoutManager.findLastVisibleItemPosition()==dataset.size()-1 + 1)
                {
                    // trigger fetch next page

//                    if(layoutManager.findLastVisibleItemPosition() == previous_position)
//                    {
//                        return;
//                    }


                    if((offset+limit)<=item_count)
                    {
                        offset = offset + limit;
                        makeNetworkCall(false);
                    }

//                    previous_position = layoutManager.findLastVisibleItemPosition();

                }

            }
        });
    }



//    int previous_position = -1;



    @Override
    public void onRefresh() {

        offset = 0;
        makeNetworkCall(true);
    }





    void makeRefreshNetworkCall()
    {
        swipeContainer.post(new Runnable() {
            @Override
            public void run() {
                swipeContainer.setRefreshing(true);

                onRefresh();
            }
        });
    }






    void makeNetworkCall(final boolean clearDataset)
    {

//            Shop currentShop = UtilityShopHome.getShop(getContext());


            User endUser = PrefLogin.getUser(getActivity());

            if(endUser==null)
            {
                showToastMessage("You are not logged in !");
                swipeContainer.setRefreshing(false);
                return;
            }




            String current_sort = "";
            current_sort = PrefSortOrders.getSort(getActivity()) + " " + PrefSortOrders.getAscending(getActivity());




//            Call<OrderEndPoint> call = orderService.getOrders(
//                        null,shopID,null,
//                        null,null,null,
//                        null,searchQuery,
//                        current_sort,limit,offset,null);


        Call<OrderEndPoint> call = orderServiceShopStaff.getOrders(
                PrefLogin.getAuthorizationHeaders(getActivity()),
                null,null,null,
                null,null,null,
                null,null,
                null,null,
                null, searchQuery,
                current_sort,limit,offset,null);





        call.enqueue(new Callback<OrderEndPoint>() {
                @Override
                public void onResponse(Call<OrderEndPoint> call, Response<OrderEndPoint> response) {

                    if(isDestroyed)
                    {
                        return;
                    }


                    if(response.code()==200)
                    {
                        if(response.body()!= null)
                        {

                            if(clearDataset)
                            {
                                item_count = response.body().getItemCount();
                                dataset.clear();
                            }


                            if(response.body().getResults()!=null)
                            {
                                dataset.addAll(response.body().getResults());
                            }

                            orderCountIndicator.setText(String.valueOf(dataset.size()) + " out of " + String.valueOf(item_count) + " Orders");



                        }



                        if(item_count==0)
                        {
                            dataset.add(EmptyScreenDataFullScreen.emptyScreenOrders());

                        }


                        if(offset + limit >= item_count)
                        {
                            adapter.setLoadMore(false);
                        }
                        else
                        {
                            adapter.setLoadMore(true);
                        }



                    }
                    else
                    {

//                        showToastMessage("Failed Code : " + String.valueOf(response.code()));

                        dataset.clear();
                        dataset.add(EmptyScreenDataFullScreen.getErrorTemplate(response.code()));

                    }


                    adapter.notifyDataSetChanged();
                    swipeContainer.setRefreshing(false);
                }




                @Override
                public void onFailure(Call<OrderEndPoint> call, Throwable t) {

                    if(isDestroyed)
                    {
                        return;
                    }

//                    showToastMessage("Network Request failed !");
                    swipeContainer.setRefreshing(false);


                    dataset.clear();
                    dataset.add(EmptyScreenDataFullScreen.getOffline());
                    adapter.notifyDataSetChanged();

                }
            });

    }


    @Override
    public void onResume() {
        super.onResume();
        isDestroyed=false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestroyed=true;
    }



    void showToastMessage(String message)
    {
        if(getActivity()!=null)
        {
            Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
        }

    }





    // Refresh the Confirmed PlaceholderFragment

    private static String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }






    @Override
    public void notifyOrderSelected(Order order) {
        PrefOrderDetail.saveOrder(order,getActivity());
        getActivity().startActivity(new Intent(getActivity(), OrderDetail.class));
    }





    @Override
    public void notifyCancelOrder(final Order order) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Confirm Cancel Order !")
                .setMessage("Are you sure you want to cancel this order !")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        cancelOrder(order);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        showToastMessage(" Not Cancelled !");
                    }
                })
                .show();
    }






    private void cancelOrder(Order order) {

        showToastMessage("Cancel Order !");


//        Call<ResponseBody> call = orderService.cancelOrderByShop(order.getOrderID());

        Call<ResponseBody> call = orderServiceShopStaff.cancelledByShop(
                PrefLogin.getAuthorizationHeaders(getActivity()),
                order.getOrderID()
        );




        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.code() == 200 )
                {
                    showToastMessage("Successful");
                    makeRefreshNetworkCall();
                }
                else if(response.code() == 304)
                {
                    showToastMessage("Not Cancelled !");
                }
                else
                {
                    showToastMessage("Server Error");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                showToastMessage("Network Request Failed. Check your internet connection !");
            }
        });

    }


    @Override
    public void notifySortChanged() {
        makeRefreshNetworkCall();
    }


    String searchQuery = null;

    @Override
    public void search(final String searchString) {
        searchQuery = searchString;
        makeRefreshNetworkCall();
    }

    @Override
    public void endSearchMode() {
        searchQuery = null;
        makeRefreshNetworkCall();
    }

}
