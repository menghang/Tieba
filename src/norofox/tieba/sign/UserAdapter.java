/**
 * 
 */
package norofox.tieba.sign;

import java.util.ArrayList;



import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

/**
 * @author
 * adapter
 */
public class UserAdapter extends BaseAdapter {
	
	ArrayList<UserModel> list;
	Context c;
	public UserAdapter(Context context){
		c = context;
	}
	
	public void setList(ArrayList<UserModel> l){
		list = l;
	}
	
	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		Activity ac = (Activity) c;

		LayoutInflater inflater = ac.getLayoutInflater();

		View mMainView = inflater.inflate(R.layout.listitem, null);
		
		UserModel user = list.get(position);
		
		CheckBox cb=(CheckBox)mMainView.findViewById(R.id.ItemCheckBox);
		cb.setText(user.id);
		cb.setChecked(user.sign);
		
		
		return mMainView;
	}

}
