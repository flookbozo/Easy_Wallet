package com.example.easywallet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.easywallet.adapter.LedgerRecyclerViewAdapter;
import com.example.easywallet.db.AppDatabase;
import com.example.easywallet.db.LedgerDao;
import com.example.easywallet.db.LedgerItem;
import com.example.easywallet.db.LedgerRepository;
import com.example.easywallet.net.ApiClient;
import com.example.easywallet.net.GetLedgerResponse;
import com.example.easywallet.net.WebServices;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reloadServerData();

        Button incomeButton = findViewById(R.id.income_button);
        incomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InsertActivity.class);
                intent.putExtra("type", 0);
                startActivity(intent);
            }
        });

        Button expenseButton = findViewById(R.id.expense_button);
        expenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InsertActivity.class);
                intent.putExtra("type", 1);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadServerData();
    }

    private void reloadData() {
        LedgerRepository repo = new LedgerRepository(MainActivity.this);

        repo.getLedger(new LedgerRepository.Callback() {
            @Override
            public void onGetLedger(List<LedgerItem> itemList) {

                int totalAmount = 0;
                for (LedgerItem item : itemList){
                    totalAmount += item.amount;
                }

                TextView balance = findViewById(R.id.balance_text_view);
                balance.setText("คงเหลือ ".concat(String.valueOf(totalAmount)).concat(" บาท"));
                RecyclerView recyclerView = findViewById(R.id.ledger_recycler_view);
                LedgerRecyclerViewAdapter adapter = new LedgerRecyclerViewAdapter(
                        MainActivity.this,
                        R.layout.item_ledger,
                        itemList
                );
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                recyclerView.setAdapter(adapter);
            }
        });
    }

    private void reloadServerData(){
        Retrofit retrofit = ApiClient.getClient();
        WebServices services = retrofit.create(WebServices.class);

        Call<GetLedgerResponse> call = services.getLedger();
        call.enqueue(new Callback<GetLedgerResponse>() {
            @Override
            public void onResponse(Call<GetLedgerResponse> call, Response<GetLedgerResponse> response) {
                GetLedgerResponse result = response.body();
                List<LedgerItem> itemList = result.ledgerItemList;

                int totalAmount = 0;
                for (LedgerItem item : itemList){
                    totalAmount += item.amount;
                }

                TextView balance = findViewById(R.id.balance_text_view);
                balance.setText("คงเหลือ ".concat(String.valueOf(totalAmount)).concat(" บาท"));
                RecyclerView recyclerView = findViewById(R.id.ledger_recycler_view);
                LedgerRecyclerViewAdapter adapter = new LedgerRecyclerViewAdapter(
                        MainActivity.this,
                        R.layout.item_ledger,
                        itemList
                );
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<GetLedgerResponse> call, Throwable t) {

            }
        });
    }

}
