package demo.zgl.com.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.LynnYYY.ExpandableModeSelector;

public class MainActivity extends Activity {

    private ExpandableModeSelector root;
    private String[] modeTags = new String[]{"vr","planet","sphere","normal"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        root = (ExpandableModeSelector) findViewById(R.id.root);

        root.addItem(R.drawable.selector_live_view_mode_vr,modeTags[0]);
        root.addItem(R.drawable.selector_live_view_mode_planet,modeTags[1]);
        root.addItem(R.drawable.selector_live_view_mode_sphere,modeTags[2]);
        //总是最后添加主按钮
        root.addMainItem(R.drawable.selector_live_view_mode_normal,modeTags[3]);
        root.setOnItemClickListener(new ExpandableModeSelector.OnItemClickListener() {
            @Override
            public void onClick(String tag) {
                Toast.makeText(MainActivity.this,tag,Toast.LENGTH_SHORT).show();
            }
        });
    }


}
