package am.widget.selectionview;

/**
 * 选择器
 * Created by Alex on 2016/7/29.
 */
public interface Selection {

    int getItemCount();

    Object getItemBar(int position);

    Object getItemNotice(int position);
}
