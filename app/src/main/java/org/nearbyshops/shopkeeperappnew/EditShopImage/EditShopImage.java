package org.nearbyshops.shopkeeperappnew.EditShopImage;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import org.nearbyshops.shopkeeperappnew.R;


public class EditShopImage extends AppCompatActivity {

    public static final String TAG_FRAGMENT_EDIT = "fragment_edit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_shop_image);
//
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_EDIT)==null)
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container,new EditShopImageFragment(),TAG_FRAGMENT_EDIT)
                    .commit();
        }
    }


}
