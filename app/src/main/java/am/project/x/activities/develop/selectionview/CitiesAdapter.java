package am.project.x.activities.develop.selectionview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;


/**
 * CitiesAdapter
 * Created by Alex on 2016/8/3.
 */
public class CitiesAdapter extends BaseAdapter {

    private List<CitiesUtils.CityDto> cities;

    private Context context;
    private int textViewResourceId;

    public CitiesAdapter(Context context, int textViewResourceId) {
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        cities = getCitiesByPinyin();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(textViewResourceId, parent, false);
        }
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        CitiesUtils.CityDto country = getItem(position);
        textView.setText(country.getCityName());
        return view;
    }

    @Override
    public int getCount() {
        return cities.size();
    }

    @Override
    public CitiesUtils.CityDto getItem(int position) {
        return cities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public final int getSelection(int tag) {
        int position = 0;
        switch (tag) {
            case 0:
                position = 0;
                break;
            case 1:
                position = 11;
                break;
            case 2:
                position = 22;
                break;
            case 3:
                position = 41;
                break;
            case 4:
                position = 60;
                break;
            case 5:
                position = 74;
                break;
            case 6:
                position = 77;
                break;
            case 7:
                position = 84;
                break;
            case 8:
                position = 95;
                break;
            case 9:
                position = 130;
                break;
            case 10:
                position = 151;
                break;
            case 11:
                position = 156;
                break;
            case 12:
                position = 184;
                break;
            case 13:
                position = 190;
                break;
            case 14:
                position = 202;
                break;
            case 15:
                position = 210;
                break;
            case 16:
                position = 223;
                break;
            case 17:
                position = 225;
                break;
            case 18:
                position = 254;
                break;
            case 19:
                position = 270;
                break;
            case 20:
                position = 284;
                break;
            case 21:
                position = 305;
                break;
            case 22:
                position = 330;
                break;
            default:
                break;
        }
        return position;
    }

    /**
     * 获取拼音排序好的城市集合
     *
     * @return 城市集合
     */
    private List<CitiesUtils.CityDto> getCitiesByPinyin() {
        List<CitiesUtils.CityDto> cities = CitiesUtils.getCityList();
        // 添加热门城市
        cities.add(0, new CitiesUtils.CityDto(CitiesUtils.City.XI_AN));
        cities.add(0, new CitiesUtils.CityDto(CitiesUtils.City.CHENG_DOU));
        cities.add(0, new CitiesUtils.CityDto(CitiesUtils.City.WU_HAN));
        cities.add(0, new CitiesUtils.CityDto(CitiesUtils.City.NAN_JING));
        cities.add(0, new CitiesUtils.CityDto(CitiesUtils.City.SHEN_YANG));
        cities.add(0, new CitiesUtils.CityDto(CitiesUtils.City.CHONG_QING));
        cities.add(0, new CitiesUtils.CityDto(CitiesUtils.City.TIAN_JIN));
        cities.add(0, new CitiesUtils.CityDto(CitiesUtils.City.SHEN_ZHEN));
        cities.add(0, new CitiesUtils.CityDto(CitiesUtils.City.GUANG_ZHOU));
        cities.add(0, new CitiesUtils.CityDto(CitiesUtils.City.SHANG_HAI));
        cities.add(0, new CitiesUtils.CityDto(CitiesUtils.City.BEI_JING));
        return cities;
    }

}