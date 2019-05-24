package org.nearbyshops.shopkeeperappnew.Markets.ViewHolders;

import android.content.Context;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.nearbyshops.shopkeeperappnew.Markets.Interfaces.listItemMarketNotifications;
import org.nearbyshops.shopkeeperappnew.Markets.Model.ServiceConfigurationGlobal;

import java.util.List;

public class AdapterSavedMarkets extends RecyclerView.Adapter<RecyclerView.ViewHolder> {




    private List<ServiceConfigurationGlobal> dataset;
    private Context context;
    private listItemMarketNotifications subscriber;




    public AdapterSavedMarkets(List<ServiceConfigurationGlobal> dataset, Context context, listItemMarketNotifications subscriber) {
        this.dataset = dataset;
        this.context = context;
        this.subscriber = subscriber;
    }





    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        return ViewHolderSavedMarket.create(viewGroup,context,subscriber);
    }




    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


        if(viewHolder instanceof ViewHolderSavedMarket)
        {
            ((ViewHolderSavedMarket) viewHolder).setItem(dataset.get(i));
        }
    }






    @Override
    public int getItemCount() {
        return dataset.size();
    }




}
